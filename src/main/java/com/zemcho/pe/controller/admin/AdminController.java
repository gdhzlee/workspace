package com.zemcho.pe.controller.admin;

import com.zemcho.pe.config.InitialConfig;
import com.zemcho.pe.mapper.course.CourseMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/java")
public class AdminController {
    @Autowired
    CourseMapper courseMapper;

    @Autowired
    RedisTemplate<String,String> stringRedisTemplate;

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

    @GetMapping("/load/year")
    public void loadYear(){

    }

    @GetMapping("/load/term")
    public void loadTerm(){

    }

    @GetMapping("/token")
    public void token(String token) throws Exception{

        token =  stringRedisTemplate.opsForValue().get("ehall_zc_session_" + token);
        System.out.println(token);
    }
}
