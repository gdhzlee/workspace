package com.zemcho.pe.service.course.impl;

import com.github.pagehelper.PageInfo;
import com.zemcho.pe.common.Message;
import com.zemcho.pe.common.Result;
import com.zemcho.pe.config.InitialConfig;
import com.zemcho.pe.controller.course.dto.BasicDTO;
import com.zemcho.pe.controller.course.dto.CancelDTO;
import com.zemcho.pe.controller.course.dto.SelectCourseDTO;
import com.zemcho.pe.controller.course.vo.CourseVO;
import com.zemcho.pe.controller.course.vo.SelectCourseRecordVO;
import com.zemcho.pe.controller.course.vo.UserInfoVO;
import com.zemcho.pe.entity.course.TakeCourseRecord;
import com.zemcho.pe.mapper.course.CourseMapper;
import com.zemcho.pe.service.course.ICourseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Semaphore;

@Service
@Slf4j
public class CourseService implements ICourseService {

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    CourseService thisSelf;

    @Autowired
    RedisTemplate<String, Integer> redisTemplate;

    @Autowired
    RedisTemplate<String, Object> objectRedisTemplate;

    @Autowired
    RedisTemplate<String,String> stringRedisTemplate;

    public final static Semaphore semaphore = new Semaphore(1);

    @Override
    public Result getCourseList(BasicDTO basicDTO) {

        Integer page = basicDTO.getPage();
        Integer pageRows = basicDTO.getPageRows();
        String userNumber = basicDTO.getUserNumber();

        // 判断用户是否存在
        UserInfoVO userInfoVO = (UserInfoVO) objectRedisTemplate.opsForValue().get(InitialConfig.USER_INFO_PREFIX + userNumber);
        if (userInfoVO == null) {
            return new Result(Message.ERR_NOT_STUDENT);
        }

        // 获取对应班级的课表
        Integer classId = userInfoVO.getClassId();
        PageInfo<CourseVO> pageInfo = thisSelf.getCourseList(classId, page, pageRows);
        List<CourseVO> list = pageInfo.getList();

        // TODO 需要循环添加课程剩余 这里有毒
        int i = 0;
        for (CourseVO course : list) {
            Integer remaining = redisTemplate.opsForValue().get(InitialConfig.SCHEDULE_PREFIX + course.getSchId());
            course.setRemaining(remaining == null ? 0 : remaining);
            list.set(i++, course);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("is_preview",InitialConfig.isPreview());
        result.put("count", pageInfo.getTotal());
        result.put("page", pageInfo.getPageNum());

        return new Result(Message.SU_COURSE_LIST, result);
    }

    @Override
    public Result selectCourse(SelectCourseDTO selectCourseDTO) {
        String phone = selectCourseDTO.getPhone();
        Integer schId = selectCourseDTO.getSchId();
        String userNumber = selectCourseDTO.getUserNumber();

        // 判断用户是否存在
        UserInfoVO userInfo = (UserInfoVO) objectRedisTemplate.opsForValue().get(InitialConfig.USER_INFO_PREFIX + userNumber);
        if (userInfo == null){
            return new Result(Message.ERR_NOT_STUDENT);
        }

        // 判断课程是否符合选课要求
        Integer classId = userInfo.getClassId();
        List<CourseVO> courses = (List<CourseVO>) objectRedisTemplate.opsForValue().get(InitialConfig.CLASS_SCHEDULES_PREFIX + classId);
        long schIdCount = courses.parallelStream().filter(c -> c.getSchId() == schId).count();
        if (schIdCount == 0){
            return new Result(Message.ERR_COURSE_DISABLE_SELECTED);
        }

        log.info("-------------选课流程开始-------------");
        try {
            semaphore.acquire();

            // 判断是否已选
            String s = stringRedisTemplate.opsForValue().get(InitialConfig.SELECTED_PREFIX + userNumber);
            if (s != null){
                semaphore.release();
                log.info("-------------选课流程结束 - 已选课-------------");
                return new Result(Message.ERR_HAD_SELECTED);
            }

            String key = InitialConfig.SCHEDULE_PREFIX + schId;
            Integer count = redisTemplate.opsForValue().get(key);

            // 判断课程剩余数
            if (count == null || count <= 0) {
                semaphore.release();
                log.info("-------------选课流程结束 - 无课程-------------");
                return new Result(Message.ERR_COURSE_NOT_ENOUGH);
            }

            // 课程剩余数减1
            RedisAtomicInteger redisAtomicInteger = new RedisAtomicInteger(key, redisTemplate);
            count = redisAtomicInteger.decrementAndGet();

            if (count < 0) {
                // 更正由于并发导致count < 0
                redisTemplate.opsForValue().set(key, 0);
                semaphore.release();
                log.info("-------------选课流程结束 - 无课程-------------");
                return new Result(Message.ERR_COURSE_NOT_ENOUGH);
            }

            stringRedisTemplate.opsForValue().set(InitialConfig.SELECTED_PREFIX + userNumber, userNumber);
            semaphore.release();

        } catch (InterruptedException e) {
            e.printStackTrace();
            semaphore.release();
            log.info("-------------选课流程结束 - 异常-------------");
            return new Result(Message.ERR_COURSE_SELECT);
        }

        //保存选课记录到队列里面，延迟插入数据库，还是直接插？
        Long now = System.currentTimeMillis() / 1000;
        TakeCourseRecord record = new TakeCourseRecord();

        record.setYear(InitialConfig.YEAR);
        record.setTerm(InitialConfig.TERM);
        record.setUid(userInfo.getUid());
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

        courseMapper.saveTakeSourceRecord(Arrays.asList(record));
        log.info("-------------选课流程结束 - 正常-------------");
        return new Result(Message.SU_COURSE_SELECT);
    }

    @Override
    public Result cancelCourseSelect(CancelDTO cancelDTO) {

        Integer uid = cancelDTO.getUid();
        Integer id = cancelDTO.getId();

        // 判断用户是否存在
        UserInfoVO userInfoVO = courseMapper.selectUserInfoById(uid);
        if (userInfoVO == null){
            return new Result(Message.ERR_NOT_STUDENT);
        }

        // 判断选课记录是否存在；注：退选后记录与归属与不存在
        SelectCourseRecordVO selectCourseRecordVO = courseMapper.selectSelectedCourceRecord(uid, id);
        if (selectCourseRecordVO == null) {
            return new Result(Message.ERR_NOT_COURSE_RECORD);
        }

        // 更新选课记录状态
        Integer cancelTakeCourse = courseMapper.cancelTakeCourse(uid, id);
        if (cancelTakeCourse != 1) {
            return new Result(Message.ERR_COURSE_WITHDRAW);
        }

        // 课程剩余数自增1
        Integer schId = selectCourseRecordVO.getSchId();
        String key = InitialConfig.SCHEDULE_PREFIX + schId;

        RedisAtomicInteger redisAtomicInteger = new RedisAtomicInteger(key, redisTemplate);
        redisAtomicInteger.incrementAndGet();

        // 删除已选标记
        String username = userInfoVO.getUsername();
        stringRedisTemplate.delete(InitialConfig.SELECTED_PREFIX + username);

        return new Result(Message.SU_COURSE_WITHDRAW);
    }

    @Override
    public Result getUserInfo(BasicDTO basicDTO) {
        String userNumber = basicDTO.getUserNumber();

        UserInfoVO userInfo = (UserInfoVO) objectRedisTemplate.opsForValue().get(InitialConfig.USER_INFO_PREFIX + userNumber);
        if (userInfo == null){
            return new Result(Message.ERR_NOT_STUDENT);
        }

        return new Result(Message.SU_STUDENT_INFO, userInfo);
    }

    @Cacheable(value = "course_list", key = "'course_list_'+#classId +'_'+#page+'_'+#pageRows", sync = true)
    public PageInfo<CourseVO> getCourseList(Integer classId, Integer page, Integer pageRows) {

        List<CourseVO> courseVOS = (List<CourseVO>) objectRedisTemplate.opsForValue().get(InitialConfig.CLASS_SCHEDULES_PREFIX + classId);
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
}
