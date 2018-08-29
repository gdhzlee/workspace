package com.zemcho.pe.config;

import com.zemcho.pe.controller.course.vo.*;
import com.zemcho.pe.mapper.course.CourseMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 初始化配置
 *
 * @Author Jetvin
 * @Date 2018/8/29
 * @Time 17:13
 * @Version ╮(╯▽╰)╭
 *
 * <!--         ░░░░░░░░░░░░░░░░░░░░░░░░▄░░░        -->
 * <!--         ░░░░░░░░░▐█░░░░░░░░░░░▄▀▒▌░░        -->
 * <!--         ░░░░░░░░▐▀▒█░░░░░░░░▄▀▒▒▒▐ ░        -->
 * <!--         ░░░░░░░▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐░░        -->
 * <!--         ░░░░░▄▄▀▒░▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐ ░        -->
 * <!--         ░░░▄▀▒▒▒░░░▒▒▒░░░▒▒▒▀██▀▒▌░░        -->
 * <!--         ░░▐▒▒▒▄▄▒▒▒▒░░░▒▒▒▒▒▒▒▀▄▒▒ ░        -->
 * <!--         ░░▌░░▌█▀▒▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒▐░        -->
 * <!--         ░▐░░░▒▒▒▒▒▒▒▒▌██▀▒▒░░░▒▒▒▀▄░        -->
 * <!--         -░▌░▒▄██▄▒▒▒▒▒▒▒▒▒░░░░░░▒▒▒▒        -->
 * <!--         ▀▒▀▐▄█▄█▌▄░▀▒▒░░░░░░░░░░▒▒▒░        -->
 * <!--                                             -->
 * <!--                 咦！有人在改BUG               -->
 */
@Configuration
@Slf4j
public class InitialConfig {

    public final static Map<String, String> SECTION_TIME = new LinkedHashMap<String, String>() {
        {
            put("1", "星期一 1-2节");
            put("2", "星期一 3-4节");
            put("3", "星期一 5-6节");
            put("4", "星期一 7-8节");
            put("5", "星期一 9-10节");

            put("6", "星期二 1-2节");
            put("7", "星期二 3-4节");
            put("8", "星期二 5-6节");
            put("9", "星期二 7-8节");
            put("10", "星期二 9-10节");

            put("11", "星期三 1-2节");
            put("12", "星期三 3-4节");
            put("13", "星期三 5-6节");
            put("14", "星期三 7-8节");
            put("15", "星期三 9-10节");

            put("16", "星期四 1-2节");
            put("17", "星期四 3-4节");
            put("18", "星期四 5-6节");
            put("19", "星期四 7-8节");
            put("20", "星期四 9-10节");

            put("21", "星期五 1-2节");
            put("22", "星期五 3-4节");
            put("23", "星期五 5-6节");
            put("24", "星期五 7-8节");
            put("25", "星期五 9-10节");
        }
    };

    public final static String SCHEDULE_PREFIX = "schedule:";
    public final static String USER_INFO_PREFIX = "user_info:";
    public final static String CLASS_SCHEDULES_PREFIX = "class_schedules:";
    public final static String SELECTED_PREFIX = "selected:";

    public static List<SelectiveTimeVO> TOTAL = new ArrayList<>();
    public static List<SelectiveTimeVO> FIRST = new ArrayList<>();
    public static List<SelectiveTimeVO> SECOND = new ArrayList<>();
    public static Integer TERM = getTerm();
    public static Integer YEAR = getYear(TERM);
    public static PreviewTimeVO PREVIEW_TIME;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    RedisTemplate<String, Integer> redisTemplate;

    @Autowired
    RedisTemplate<String, Object> objectRedisTemplate;

    /* 初始化课程数量 */
    @PostConstruct
    private void initCourseCount() {

        List<CourseCountVO> courseCountVOS = courseMapper.selectCourseCountByYearAndTerm(YEAR, TERM);
        int i = 0;
        for (CourseCountVO courseCount : courseCountVOS) {
            int count = courseCount.getCount() == null ? 0 : courseCount.getCount();
            Integer courseId = courseCount.getCourseId();
            if (courseId <= 249) {
                i += count;
            }
            redisTemplate.opsForValue().set(SCHEDULE_PREFIX + courseId, count);
        }

        System.out.println("总课程人数 = " + i);
    }

    /* 初始化班级课表 */
    @PostConstruct
    private void initClassSchedules() {
        List<Integer> classIds = courseMapper.selectClassIdAll();

        for (Integer classId : classIds) {
            List<CourseVO> courseVOS = courseMapper.selectCourseList(classId,YEAR, TERM);
            objectRedisTemplate.opsForValue().set(CLASS_SCHEDULES_PREFIX + classId, courseVOS);
        }
    }

    /* 初始化用户信息 */
    @PostConstruct
    private void initUserInfo() {
        String yearTerm = getYearTerm(YEAR, TERM);
        Integer configId = courseMapper.selectConfigIdByYearAndTerm(YEAR, TERM);
        if (configId != null) {
            FIRST = courseMapper.selectTimeList(configId, true);
            SECOND = courseMapper.selectTimeList(configId, false);
            TOTAL.addAll(FIRST);
            TOTAL.addAll(SECOND);

            List<UserInfoVO> userInfoVOS = courseMapper.selectUserInfoByCampusAndYearAndTerm(YEAR, TERM);
            for (UserInfoVO userInfo : userInfoVOS) {
                userInfo.setFirst(FIRST);
                userInfo.setSecond(SECOND);
                userInfo.setYearTerm(yearTerm);

                String username = userInfo.getUsername();
                objectRedisTemplate.opsForValue().set(USER_INFO_PREFIX + username, userInfo);
            }
        }
    }

    /* 初始化预览时间 */
    @PostConstruct
    private void initPreviewTime(){
        PREVIEW_TIME = courseMapper.selectPreviewTimeByYearAndTerm(YEAR,TERM);
    }

    /* 定时修改学年学期 */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void modifyYearAndTerm() {
        TERM = getTerm();
        YEAR = getYear(TERM);
    }

    /* 定时修改预览时间 */
    @Scheduled(cron = "0 0 0 * * ?")
    public void modifyPreviewTime() {
        PREVIEW_TIME = courseMapper.selectPreviewTimeByYearAndTerm(YEAR,TERM);
    }

    /* 定时修改选课时间 */
    @Scheduled(cron = "0 0 0 * * ?")
    public void modifySelectiveTime() {
        Integer configId = courseMapper.selectConfigIdByYearAndTerm(YEAR, TERM);
        if (configId != null) {
            FIRST = courseMapper.selectTimeList(configId, true);
            SECOND = courseMapper.selectTimeList(configId, false);
            TOTAL.addAll(FIRST);
            TOTAL.addAll(SECOND);
        }
    }

    private static int getTerm() {
        LocalDate now = LocalDate.now();

        int monthValue = now.getMonthValue();
        if (monthValue == 1 || monthValue >= 8) {
            return 1;
        } else {
            return 2;
        }
    }

    private static int getYear(int term) {
        LocalDate now = LocalDate.now();

        int year = now.getYear();
        int monthValue = now.getMonthValue();
        if (term == 1) {
            if (monthValue == 1) {
                return year - 1;
            }
        }

        return year;
    }

    private String getYearTerm(Integer year, Integer term) {

        return year + "-" + (year + 1) + "学年第" + term + "学期";
    }

    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static int isPreview(){

        LocalDateTime now = LocalDateTime.now();
        boolean b = now.compareTo(InitialConfig.PREVIEW_TIME.getPreviewStartTime()) >= 0
                && now.compareTo(InitialConfig.PREVIEW_TIME.getPreviewEndTime()) <= 0;
        log.info("预览开始时间:{}",formatter.format(PREVIEW_TIME.getPreviewStartTime()));
        log.info("预览结束时间:{}",formatter.format(PREVIEW_TIME.getPreviewEndTime()));
        log.info("当前时间:{}",formatter.format(now));
        log.info("是否处于预览时间:{}",b);

        return b ? 1 : 0;
    }

    public static boolean isSelective(){

        LocalDateTime now = LocalDateTime.now();

        for (SelectiveTimeVO selectiveTime : TOTAL){
            LocalDateTime start = selectiveTime.getStartTime().toLocalDateTime();
            LocalDateTime end = selectiveTime.getEndTime().toLocalDateTime();

            boolean b = now.compareTo(start) >= 0
                    && now.compareTo(end) <= 0;

            log.info("选课开始时间:{}",formatter.format(start));
            log.info("选课结束时间:{}",formatter.format(end));
            log.info("当前时间:{}",formatter.format(now));
            log.info("是否处于选课时间:{}",b);

            if (b){
                return true;
            }
        }

        return false;
    }
}
