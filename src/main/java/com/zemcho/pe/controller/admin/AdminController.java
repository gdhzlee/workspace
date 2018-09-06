package com.zemcho.pe.controller.admin;

import com.zemcho.pe.common.Message;
import com.zemcho.pe.common.Result;
import com.zemcho.pe.config.initial.InitialConfig;
import com.zemcho.pe.mapper.course.CourseMapper;
import com.zemcho.pe.service.quartz.CourseNumberJob;
import org.quartz.*;
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
    RedisTemplate<String, Integer> readIntegerRedisTemplate;

    @Autowired
    InitialConfig initialConfig;

    @Autowired
    Scheduler scheduler;

//    @GetMapping("/load/preview")
//    public void loadPreviewTime() {
//        InitialConfig.PREVIEW_TIME = courseMapper.selectPreviewTimeByYearAndTerm(InitialConfig.YEAR, InitialConfig.TERM);
//    }
//
//    @GetMapping("/load/selective")
//    public void loadSelectiveTime() {
//        initialConfig.initSelectiveTime();
//    }
//
//    @GetMapping("/load/userInfo")
//    public void loadUserInfo() {
//        initialConfig.initUserInfo();
//    }
//
//    @GetMapping("/load/course/count")
//    public void loadCourseCount() {
//        initialConfig.initCourseCount();
//    }
//
//    @GetMapping("/load/schedules")
//    public void loadClassSchedules() {
//        initialConfig.initClassSchedules();
//    }

    @GetMapping("/load/all")
    public Result loadAll() {
        initialConfig.initSelectiveTime();
        initialConfig.initPreviewTime();
        initialConfig.initUserInfo();
        initialConfig.initCourseCount();
        initialConfig.initClassSchedules();

        return new Result(Message.SU_DATA_SYNCHRONIZATION_SUCCESS);
    }

    @GetMapping("/redis")
    public void testRedis(@RequestParam(defaultValue = "10000") int cli) {

        Long start = System.currentTimeMillis();
        System.out.println(cli);
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i < cli; i++) {
            executorService.execute(() -> {
                readIntegerRedisTemplate.opsForValue().get(InitialConfig.SCHEDULE_PREFIX + 200 + new Random(200).nextInt());
            });
        }

        executorService.shutdown();

        while (true) {
            if (executorService.isTerminated()) {
                break;
            }
        }

        Long end = System.currentTimeMillis();

        System.out.println(end - start);
    }

    @GetMapping("/quartz/start")
    public Result startQuartz(CronSchedule cronSchedule, String cron) throws SchedulerException {
        String cronString = null;

        if (cronSchedule != null) {
            cronString = cronSchedule.getValue();
        } else {
            if (cron != null) {
                cronString = cron;
            }
        }

        String courseNumberJobName = InitialConfig.COURSE_NUMBER_JOB_NAME;
        JobDetail jobDetail = JobBuilder.newJob(CourseNumberJob.class).withIdentity(courseNumberJobName, courseNumberJobName).build();
        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(courseNumberJobName, courseNumberJobName).withSchedule(CronScheduleBuilder.cronSchedule(cronString)).build();


        boolean exists = scheduler.checkExists(JobKey.jobKey(courseNumberJobName));

        if (exists) {
            return new Result(Message.ERR_COURSE_NUMBER_JOB_EXIST);
        }

        scheduler.scheduleJob(jobDetail, trigger);
        if (!scheduler.isShutdown()) {
            scheduler.start();
        }

        return new Result(Message.SU_START_COURSE_NUMBER_JOB);

    }

    @GetMapping("/quartz/shutdown")
    public Result shutdownQuartz() throws SchedulerException {
        String courseNumberJobName = InitialConfig.COURSE_NUMBER_JOB_NAME;

        boolean exists = scheduler.checkExists(JobKey.jobKey(courseNumberJobName));
        if (exists == false) {

            return new Result(Message.ERR_COURSE_NUMBER_JOB_NOT_EXIST);
        }

        scheduler.shutdown();

        return new Result(Message.SU_SHUTDOWN_COURSE_NUMBER_JOB);

    }

    enum CronSchedule {
        MINUTE("0 */2 * * * ?"),
        HALF_HOUR("0 */30 * * * ?");

        private String value;

        CronSchedule(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
