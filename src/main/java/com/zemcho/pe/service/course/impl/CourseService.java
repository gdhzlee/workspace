package com.zemcho.pe.service.course.impl;

import com.github.pagehelper.PageInfo;
import com.zemcho.pe.common.Message;
import com.zemcho.pe.common.Result;
import com.zemcho.pe.config.initial.InitialConfig;
import com.zemcho.pe.controller.course.dto.BasicDTO;
import com.zemcho.pe.controller.course.dto.CancelDTO;
import com.zemcho.pe.controller.course.dto.SaveDataDTO;
import com.zemcho.pe.controller.course.dto.SelectCourseDTO;
import com.zemcho.pe.controller.course.vo.CourseVO;
import com.zemcho.pe.controller.course.vo.UserInfoVO;
import com.zemcho.pe.entity.course.CourseResults;
import com.zemcho.pe.entity.course.FormLog;
import com.zemcho.pe.entity.course.SelectCourseLog;
import com.zemcho.pe.entity.course.SelectCourseRecord;
import com.zemcho.pe.mapper.course.CourseMapper;
import com.zemcho.pe.service.course.ICourseService;
import com.zemcho.pe.util.IpAddressUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CourseService implements ICourseService {

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    CourseService thisSelf;

    @Autowired
    RedisTemplate<String, Integer> readIntegerRedisTemplate;

    @Autowired
    RedisTemplate<String, Integer> writeIntegerRedisTemplate;

    @Autowired
    RedisTemplate<String, Object> readObjectRedisTemplate;

    @Autowired
    RedisTemplate<String, Object> writeObjectRedisTemplate;

    @Autowired
    RedisTemplate<String,String> ehallStringRedisTemplate;

    public final static Semaphore semaphore = new Semaphore(1);

    public static ExecutorService executorService = Executors.newCachedThreadPool();

    public static Map<Integer, String> SEX = new LinkedHashMap<Integer, String>(){
        {
            put(1,"男");
            put(2,"女");
        }
    };

    @Override
    public Result getCourseList(BasicDTO basicDTO) {

        long start = System.currentTimeMillis();

        Integer page = basicDTO.getPage();
        Integer pageRows = basicDTO.getPageRows();
        String userNumber = basicDTO.getUserNumber();
        HttpServletRequest request = basicDTO.getRequest();

        // 判断用户是否存在
        UserInfoVO userInfo = (UserInfoVO) readObjectRedisTemplate.opsForValue().get(InitialConfig.USER_INFO_PREFIX + userNumber);
        if (userInfo == null) {
            return new Result(Message.ERR_NOT_STUDENT);
        }

        // 校验用户认证与学号的匹配
        Integer uid = (Integer) request.getAttribute("uid");
        if (!userInfo.getUid().equals(uid)){
            return new Result(Message.ERR_ERROR_AUTHORIZATION_RELATIVE);
        }

        // 获取对应班级的课表
        Integer classId = userInfo.getClassId();
        PageInfo<CourseVO> pageInfo = thisSelf.getCourseList(classId, page, pageRows);
        List<CourseVO> list = pageInfo.getList();

        // TODO 需要循环添加课程剩余 这里有毒
        int i = 0;
        for (CourseVO course : list) {
            Integer remaining = readIntegerRedisTemplate.opsForValue().get(InitialConfig.SCHEDULE_PREFIX + course.getSchId());
            course.setRemaining(remaining == null ? 0 : remaining);
            list.set(i++, course);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("is_preview",InitialConfig.isPreview());
        result.put("count", pageInfo.getTotal());
        result.put("page", pageInfo.getPageNum());

        long end = System.currentTimeMillis();
        log.info("运行时长：{} ms", end - start);

        return new Result(Message.SU_COURSE_LIST, result);
    }

    @Override
    public Result selectCourse(SelectCourseDTO selectCourseDTO) {
        String phone = selectCourseDTO.getPhone();
        Integer schId = selectCourseDTO.getSchId();
        String userNumber = selectCourseDTO.getUserNumber();
        HttpServletRequest request = selectCourseDTO.getRequest();

        LettuceConnectionFactory factory = (LettuceConnectionFactory)readObjectRedisTemplate.getConnectionFactory();
        String hostName = factory.getHostName();
        String password = factory.getPassword();
        int port = factory.getPort();
        int database = factory.getDatabase();
        log.info("系统读redis {host:{}, password:{}, port:{}, database:{}}",hostName, password, port, database);

        // 判断用户是否存在
        UserInfoVO userInfo = (UserInfoVO) readObjectRedisTemplate.opsForValue().get(InitialConfig.USER_INFO_PREFIX + userNumber);
        if (userInfo == null) {
            return new Result(Message.ERR_NOT_STUDENT);
        }

        // 校验用户认证与学号的匹配
        Integer uid = (Integer) request.getAttribute("uid");
        if (!userInfo.getUid().equals(uid)){
            return new Result(Message.ERR_ERROR_AUTHORIZATION_RELATIVE);
        }

        // 判断课程是否符合选课要求
        Integer classId = userInfo.getClassId();
        log.info("classId:{}", classId);
        List<CourseVO> courses = (List<CourseVO>) readObjectRedisTemplate.opsForValue().get(InitialConfig.CLASS_SCHEDULES_PREFIX + classId);
        List<CourseVO> collect = courses.parallelStream().filter(c -> c.getSchId().equals(schId)).collect(Collectors.toList());
        if (collect.size() == 0){
            return new Result(Message.ERR_COURSE_DISABLE_SELECTED);
        }

        CourseVO courseVO = collect.get(0);
        Integer courseCampus = courseVO.getCourseCampus();
        Integer campus = userInfo.getCampus();

        // 判断校区
        if (!campus.equals(courseCampus)){
            return new Result(Message.ERR_COURSE_DISABLE_SELECTED);
        }

        Integer userSex = userInfo.getSex();
        String courseSex = courseVO.getSex();

        // 判断性别
        if (!courseSex.equals("不限")){
            if (!courseSex.equals(SEX.get(userSex))){
                return new Result(Message.ERR_COURSE_DISABLE_SELECTED);
            }
        }

        log.info("-------------选课流程开始-------------");
        try {
            semaphore.acquire();

            // 判断是否已选
            String s = (String) readObjectRedisTemplate.opsForValue().get(InitialConfig.SELECTED_PREFIX + userNumber);
            if (s != null){
                semaphore.release();
                log.info("-------------选课流程结束 - 已选课-------------");
                return new Result(Message.ERR_HAD_SELECTED);
            }

            String key = InitialConfig.SCHEDULE_PREFIX + schId;
            Integer count = readIntegerRedisTemplate.opsForValue().get(key);

            // 判断课程剩余数
            if (count == null || count <= 0) {
                semaphore.release();
                log.info("-------------选课流程结束 - 无课程-------------");
                return new Result(Message.ERR_COURSE_NOT_ENOUGH);
            }

            // 课程剩余数减1
            RedisAtomicInteger redisAtomicInteger = new RedisAtomicInteger(key, writeIntegerRedisTemplate);
            count = redisAtomicInteger.decrementAndGet();

            if (count < 0) {
                // 更正由于并发导致count < 0
                writeIntegerRedisTemplate.opsForValue().set(key, 0);
                semaphore.release();
                log.info("-------------选课流程结束 - 无课程-------------");
                return new Result(Message.ERR_COURSE_NOT_ENOUGH);
            }

            writeObjectRedisTemplate.opsForValue().set(InitialConfig.SELECTED_PREFIX + userNumber, userNumber);
            semaphore.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
            semaphore.release();
            log.info("-------------选课流程结束 - 异常-------------");
            return new Result(Message.ERR_COURSE_SELECT);
        }

        //TODO 开线程执行保存数据
        Integer faculty = userInfo.getFaculty();

        Integer courseId = courseVO.getCourseId();
        Integer teacherId = courseVO.getTeacherId();

        SaveDataDTO saveDataDTO = new SaveDataDTO();
        saveDataDTO.setRequest(request);
        saveDataDTO.setUid(uid);
        saveDataDTO.setSchId(schId);
        saveDataDTO.setClassId(classId);
        saveDataDTO.setPhone(phone);
        saveDataDTO.setCampus(campus);
        saveDataDTO.setCourseId(courseId);
        saveDataDTO.setTeacherId(teacherId);
        saveDataDTO.setFaculty(faculty);
        executorService.execute(() -> saveData(saveDataDTO));

        log.info("-------------选课流程结束 - 正常-------------");
        return new Result(Message.SU_COURSE_SELECT);
    }

    @Override
    public Result cancelCourseSelect(CancelDTO cancelDTO) {

        String userNumber = cancelDTO.getUserNumber();
        Integer schId= cancelDTO.getSchId();

        // 判断用户是否存在
        UserInfoVO userInfoVO = (UserInfoVO) readObjectRedisTemplate.opsForValue().get(InitialConfig.USER_INFO_PREFIX + userNumber);
        if (userInfoVO == null){
            return new Result(Message.ERR_NOT_STUDENT);
        }

        String username = userInfoVO.getUsername();
        Integer classId = userInfoVO.getClassId();

        try {
            semaphore.acquire();

            // 判断是否有选课或已退选
            String selectedKey = InitialConfig.SELECTED_PREFIX + username;
            String s =(String) readObjectRedisTemplate.opsForValue().get(selectedKey);
            if (s == null){
                semaphore.release();
                return new Result(Message.ERR_COURSE_SELECT);
            }

            // 删除已选标记
            writeObjectRedisTemplate.delete(selectedKey);

            // 班级已选课人数减1
            String courseNumKey = InitialConfig.COURSE_NUM + classId;
            RedisAtomicInteger courseNum = new RedisAtomicInteger(courseNumKey, writeIntegerRedisTemplate);
            courseNum.decrementAndGet();

            String scheduleKey = InitialConfig.SCHEDULE_PREFIX + schId;

            Integer sch = readIntegerRedisTemplate.opsForValue().get(scheduleKey);
            if (sch == null){
                semaphore.release();
                return new Result(Message.ERR_ERROR_SCHEDULES);
            }

            // 课程剩余数自增1
            RedisAtomicInteger schedule = new RedisAtomicInteger(scheduleKey, writeIntegerRedisTemplate);
            schedule.incrementAndGet();

            semaphore.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
            semaphore.release();
        }

        return new Result(Message.SU_COURSE_WITHDRAW);
    }

    @Override
    public Result getUserInfo(BasicDTO basicDTO) {
        HttpServletRequest request = basicDTO.getRequest();
        String userNumber = basicDTO.getUserNumber();

        // 判断用户是否存在
        UserInfoVO userInfo = (UserInfoVO) readObjectRedisTemplate.opsForValue().get(InitialConfig.USER_INFO_PREFIX + userNumber);
        if (userInfo == null) {
            return new Result(Message.ERR_NOT_STUDENT);
        }

        // 校验用户认证与学号的匹配
        Integer uid = (Integer) request.getAttribute("uid");
        System.out.println(uid);
        if (!userInfo.getUid().equals(uid)){
            return new Result(Message.ERR_ERROR_AUTHORIZATION_RELATIVE);
        }

        return new Result(Message.SU_STUDENT_INFO, userInfo);
    }

    @Cacheable(value = "course_list", key = "'course_list_'+#classId +'_'+#page+'_'+#pageRows", sync = true)
    public PageInfo<CourseVO> getCourseList(Integer classId, Integer page, Integer pageRows) {

        List<CourseVO> courseVOS = (List<CourseVO>) readObjectRedisTemplate.opsForValue().get(InitialConfig.CLASS_SCHEDULES_PREFIX + classId);
        if (courseVOS == null) {
            PageInfo<CourseVO> pageInfo = new PageInfo<>(new ArrayList<>());
            pageInfo.setPageNum(page);
            pageInfo.setTotal(0);
        }

        int size = courseVOS.size();
        int fromIndex = (page - 1) * pageRows;
        int endIndex = 0;
        if (page * pageRows > size) {
            endIndex = size;
        } else {
            endIndex = fromIndex + pageRows;
        }

        List<CourseVO> subList = new ArrayList<>();
        for (int i = fromIndex; i < endIndex; i++) {
            subList.add(courseVOS.get(i));
        }

        PageInfo<CourseVO> pageInfo = new PageInfo<>(subList);
        pageInfo.setPageNum(page);
        pageInfo.setTotal(size);

        return pageInfo;
    }

    private void saveData(SaveDataDTO saveDataDTO){

        HttpServletRequest request = saveDataDTO.getRequest();
        Integer uid = saveDataDTO.getUid();
        Integer schId = saveDataDTO.getSchId();
        Integer classId = saveDataDTO.getClassId();
        String phone = saveDataDTO.getPhone();
        Integer campus = saveDataDTO.getCampus();
        Integer courseId = saveDataDTO.getCourseId();
        Integer teacherId = saveDataDTO.getTeacherId();
        Integer faculty = saveDataDTO.getFaculty();

        // TODO 保存选课记录
        Long now = System.currentTimeMillis() / 1000;
        SelectCourseRecord record = new SelectCourseRecord();

        record.setYear(InitialConfig.YEAR);
        record.setTerm(InitialConfig.TERM);
        record.setUid(uid);
        record.setSchId(schId);
        record.setClassId(classId);
        record.setPhone(phone);
        record.setRemark("");
        record.setOperateType(1);
        record.setCreateTime(now);
        record.setUpdateTime(now);
        record.setStatus(1);
        record.setIsDel(false);
        record.setExemptStatus(0);
        courseMapper.saveSelectSourceRecord(record);

        // TODO 保存选课日志
        String ip = IpAddressUtil.getIp(request);
        SelectCourseLog selectCourseLog = new SelectCourseLog();
        selectCourseLog.setUid(uid);
        selectCourseLog.setSchId(schId);
        selectCourseLog.setIp(ip);
        selectCourseLog.setCreateTime(now);
        selectCourseLog.setUpdateTime(now);
        courseMapper.saveSelectCourseLog(selectCourseLog);

        // TODO 保存成绩记录
        CourseResults courseResults = courseMapper.selectCourseResultsByUid(uid, InitialConfig.YEAR, InitialConfig.TERM);
        if (courseResults == null){
            courseResults = new CourseResults();
            courseResults.setYear(InitialConfig.YEAR);
            courseResults.setTerm(InitialConfig.TERM);
            courseResults.setCampus(campus);
            courseResults.setUid(uid);
            courseResults.setCourseId(courseId);
            courseResults.setTeacherId(teacherId);
            courseResults.setCreateTime(now);
            courseResults.setUpdateTime(now);
            courseMapper.saveCourseResults(courseResults);
        }else {
            courseResults.setYear(InitialConfig.YEAR);
            courseResults.setTerm(InitialConfig.TERM);
            courseResults.setCampus(campus);
            courseResults.setCourseId(courseId);
            courseResults.setTeacherId(teacherId);
            courseResults.setUpdateTime(now);

            courseMapper.updateCourseResults(courseResults);
        }

        // TODO 更新上课班级人数(这个定时更新) -- 还没做
        String key = InitialConfig.COURSE_NUM + classId;
        RedisAtomicInteger redisAtomicInteger = new RedisAtomicInteger(key, writeIntegerRedisTemplate);
        redisAtomicInteger.incrementAndGet();

        // TODO 保存大厅事务
        Integer dataId = record.getId();
        FormLog formLog = new FormLog();
        formLog.setFormId(InitialConfig.FORM_ID);
        formLog.setDataId(dataId);
        formLog.setUid(uid);
        formLog.setFaculty(faculty);
        formLog.setCreateTime(now);
        formLog.setUpdateTime(now);
        courseMapper.saveFormLog(formLog);
    }

}
