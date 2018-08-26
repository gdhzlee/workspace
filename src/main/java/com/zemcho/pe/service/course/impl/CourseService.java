package com.zemcho.pe.service.course.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.pe.common.Result;
import com.zemcho.pe.common.Message;
import com.zemcho.pe.config.InitialConfig;
import com.zemcho.pe.controller.course.dto.BasicDTO;
import com.zemcho.pe.controller.course.dto.SelectCourseDTO;
import com.zemcho.pe.controller.course.vo.CourseVO;
import com.zemcho.pe.controller.course.vo.SelectiveTimeVO;
import com.zemcho.pe.controller.course.vo.UserInfoVO;
import com.zemcho.pe.entity.course.TakeCourseRecord;
import com.zemcho.pe.mapper.course.CourseMapper;
import com.zemcho.pe.service.course.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicInteger;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotBlank;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

@Service
public class CourseService implements ICourseService {

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    CourseService thisSelf;

    @Autowired
    RedisTemplate<String,Integer> redisTemplate;

    public final static Semaphore semaphore = new Semaphore(1);

    @Override
    public Result getCourseList(BasicDTO basicDTO) {

        Integer page = basicDTO.getPage();
        Integer pageRows = basicDTO.getPageRows();
        Integer year = basicDTO.getYear();
        Integer term = basicDTO.getTerm();


        PageInfo<CourseVO> pageInfo = thisSelf.getCourseList(page, pageRows, year, term);
        List<CourseVO> list = pageInfo.getList();

        // TODO 需要循环添加课程剩余 这里有毒
        int i = 0;
        for(CourseVO course : list){
            Integer remaining = redisTemplate.opsForValue().get(InitialConfig.SCHEDULE_PREFIX + course.getSchId());
            course.setRemaining(remaining == null ? 0 : remaining);
            list.set(i++, course);
        }

        Map<String,Object> result = new HashMap<>();
        result.put("list", list);
        result.put("count",pageInfo.getTotal());
        result.put("page",pageInfo.getPageNum());

        return new Result(Message.SU_COURSE_LIST, result);
    }

    @Override
    public Result selectCourse(SelectCourseDTO selectCourseDTO) {
        String phone = selectCourseDTO.getPhone();
        Integer schId = selectCourseDTO.getSchId();

        try {
            semaphore.acquire();
            String key = InitialConfig.SCHEDULE_PREFIX + schId;
            Integer count = redisTemplate.opsForValue().get(key);

            // 判断课程剩余数
            if (count == null || count <= 0){
                semaphore.release();
                return new Result(Message.ERR_COURSE_NOT_ENOUGH);
            }

            // 课程剩余数减1
            RedisAtomicInteger redisAtomicInteger = new RedisAtomicInteger(key, redisTemplate);
            count = redisAtomicInteger.decrementAndGet();

            if (count < 0){
                // 更正由于并发导致count < 0
                redisTemplate.opsForValue().set(key,0);
                semaphore.release();
                return new Result(Message.ERR_COURSE_NOT_ENOUGH);
            }
            semaphore.release();

            //保存选课记录到队列里面，延迟插入数据库，还是直接插？
            Long now = System.currentTimeMillis() / 1000;
            TakeCourseRecord record = new TakeCourseRecord();

            record.setYear(InitialConfig.YEAR);
            record.setTerm(InitialConfig.TERM);
            record.setUid(9990999);
            record.setSchId(1010101);
            record.setClassId(12021);
            record.setPhone(phone);
            record.setRemark("我是备注");
            record.setOperateType(1);
            record.setCreateTime(now);
            record.setUpdateTime(now);
            record.setStatus(1);
            record.setIsDel(false);
            record.setExemptStatus(0);

            courseMapper.saveTakeSourceRecord(Arrays.asList(record));
            return new Result(Message.SU_COURSE_SELECT);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return new Result(Message.ERR_COURSE_SELECT);
    }

    @Override
    public Result getUserInfo(BasicDTO basicDTO) {

        Integer year = basicDTO.getYear();
        Integer term = basicDTO.getTerm();

        Integer configId = courseMapper.selectConfigIdByYearAndTerm(year, term);
        if (configId == null){
            return new Result(Message.ERR_INCOMPLETE_DATA);
        }

        List<SelectiveTimeVO> first = courseMapper.selectTimeList(configId, true);
        List<SelectiveTimeVO> second = courseMapper.selectTimeList(configId, false);

        UserInfoVO userInfoVO = new UserInfoVO();
        userInfoVO.setFirst(first);
        userInfoVO.setSecond(second);

        return new Result(Message.SU_STUDENT_INFO, userInfoVO);
    }

    @Cacheable(value = "course_list", key = "'course_list_'+#page+'_'+#pageRows+'_'+#year+'_'+#term",sync = true)
    public PageInfo<CourseVO> getCourseList(Integer page, Integer pageRows, Integer year, Integer term){
        PageHelper.startPage(page,pageRows);
        List<CourseVO> courseVOS = courseMapper.selectCourseList(year,term);
        PageInfo<CourseVO> pageInfo = new PageInfo<>(courseVOS);
        return pageInfo;
    }
}
