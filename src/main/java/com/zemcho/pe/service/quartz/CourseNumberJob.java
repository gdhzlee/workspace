package com.zemcho.pe.service.quartz;

import com.zemcho.pe.config.initial.InitialConfig;
import com.zemcho.pe.mapper.course.CourseMapper;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class CourseNumberJob implements Job {

    @Autowired
    RedisTemplate<String, Integer> readIntegerRedisTemplate;

    @Autowired
    CourseMapper courseMapper;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        // 获取key集
        Set<String> keys = readIntegerRedisTemplate.keys("course_num:*");
        Long now = System.currentTimeMillis() / 1000;

        for (String key : keys){

            // 获取classId
            String[] split = key.split(":");
            Integer classId = Integer.parseInt(split[1]);

            // 获取班级已选人数
            Integer courseNumber = readIntegerRedisTemplate.opsForValue().get(key);

            // 检查是否需要更新到数据库
            Integer value = InitialConfig.numberMap.get(classId);

            if (value == null){
                InitialConfig.numberMap.put(classId, courseNumber);
                courseMapper.updateCourseNumber(InitialConfig.YEAR, InitialConfig.TERM, classId, courseNumber, now);
            }else {

                if (!value.equals(courseNumber)){
                    InitialConfig.numberMap.put(classId, courseNumber);
                    courseMapper.updateCourseNumber(InitialConfig.YEAR, InitialConfig.TERM, classId, courseNumber, now);
                }
            }
        }
    }
}
