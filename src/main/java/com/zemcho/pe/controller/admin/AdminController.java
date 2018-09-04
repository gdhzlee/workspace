package com.zemcho.pe.controller.admin;

import com.zemcho.pe.config.initial.InitialConfig;
import com.zemcho.pe.mapper.course.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@RestController
@RequestMapping("/admin/java")
public class AdminController {
    @Autowired
    CourseMapper courseMapper;

    @Autowired
    RedisTemplate<String,Integer> readIntegerRedisTemplate;

    @Autowired
    InitialConfig initialConfig;

    @GetMapping("/load/preview")
    public void loadPreviewTime(){
        InitialConfig.PREVIEW_TIME = courseMapper.selectPreviewTimeByYearAndTerm(InitialConfig.YEAR,InitialConfig.TERM);
    }

    @GetMapping("/load/selective")
    public void loadSelectiveTime(){
        Integer configId = courseMapper.selectConfigIdByYearAndTerm(InitialConfig.YEAR, InitialConfig.TERM);
        System.out.println("configId = " + configId);
        if (configId != null) {
            InitialConfig.FIRST = courseMapper.selectTimeList(configId, true);
            InitialConfig.SECOND = courseMapper.selectTimeList(configId, false);
            InitialConfig.TOTAL.clear();
            InitialConfig.TOTAL.addAll(InitialConfig.FIRST);
            InitialConfig.TOTAL.addAll(InitialConfig.SECOND);
        }
    }

    @GetMapping("/load/userInfo")
    public void loadUserInfo(){
        initialConfig.initUserInfo();
    }

    @GetMapping("/load/course/count")
    public void loadCourseCount(){
        initialConfig.initCourseCount();
    }

    @GetMapping("/load/schedules")
    public void loadClassSchedules(){
        initialConfig.initClassSchedules();
    }

    @GetMapping("/redis")
    public void testRedis(@RequestParam(defaultValue = "10000") int cli){

        Long start = System.currentTimeMillis();
        System.out.println(cli);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < cli; i ++){
            executorService.execute(() ->{
                readIntegerRedisTemplate.opsForValue().get(InitialConfig.SCHEDULE_PREFIX + 200 + new Random(200).nextInt());
            });
        }

        executorService.shutdown();

        while (true){
            if (executorService.isTerminated()){
                break;
            }
        }

        Long end = System.currentTimeMillis();

        System.out.println(end - start);
    }
}
