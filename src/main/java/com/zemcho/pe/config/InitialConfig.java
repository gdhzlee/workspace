package com.zemcho.pe.config;

import com.zemcho.pe.controller.course.vo.CourseCountVO;
import com.zemcho.pe.mapper.course.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class InitialConfig {

    public final static Map<Integer,String> SECTION_TIME = new LinkedHashMap<Integer, String>(){
        {
            put(1,"星期一 1-2节");
            put(2,"星期一 3-4节");
            put(3,"星期一 5-6节");
            put(4,"星期一 7-8节");
            put(5,"星期一 9-10节");

            put(6,"星期二 1-2节");
            put(7,"星期二 3-4节");
            put(8,"星期二 5-6节");
            put(9,"星期二 7-8节");
            put(10,"星期二 9-10节");

            put(11,"星期三 1-2节");
            put(12,"星期三 3-4节");
            put(13,"星期三 5-6节");
            put(14,"星期三 7-8节");
            put(15,"星期三 9-10节");

            put(16,"星期四 1-2节");
            put(17,"星期四 3-4节");
            put(18,"星期四 5-6节");
            put(19,"星期四 7-8节");
            put(20,"星期四 9-10节");

            put(21,"星期五 1-2节");
            put(22,"星期五 3-4节");
            put(23,"星期五 5-6节");
            put(24,"星期五 7-8节");
            put(25,"星期五 9-10节");
        }
    };

    public final static String SCHEDULE_PREFIX = "schedule:";
    public static Integer TERM = 0;
    public static Integer YEAR = 0;

    @Autowired
    CourseMapper courseMapper;

    @Autowired
    RedisTemplate<String,Integer> redisTemplate;

    /* 初始化课程数量 */
    @PostConstruct
    private void initCourseCount(){
        TERM = getTerm();
        YEAR = getYear(TERM);
        List<CourseCountVO> courseCountVOS = courseMapper.selectCourseCountByYearAndTerm(YEAR, TERM);

        for (CourseCountVO courseCount : courseCountVOS){
            redisTemplate.opsForValue().set(SCHEDULE_PREFIX + courseCount.getCourseId(), courseCount.getCount() == null ? 0 : courseCount.getCount());
        }
    }

    /* 定时修改学年学期 */
    @Scheduled(cron = "0 0 0 1 * ?")
    public void modifyYearAndTerm(){
        TERM = getTerm();
        YEAR = getYear(TERM);
    }

    private int getTerm(){
        LocalDate now = LocalDate.now();

        int monthValue = now.getMonthValue();
        if ( monthValue == 1 || monthValue >= 8){
            return 1;
        }else {
            return 2;
        }
    }

    private int getYear(int term){
        LocalDate now = LocalDate.now();

        int year = now.getYear();
        int monthValue = now.getMonthValue();
        if (term == 1){
            if (monthValue == 1){
                return year - 1;
            }
        }

        return year;
    }
}
