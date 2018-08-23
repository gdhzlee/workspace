package com.zemcho.pe.controller.course;

import com.zemcho.pe.common.Result;
import com.zemcho.pe.controller.course.dto.BasicDTO;
import com.zemcho.pe.service.course.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/Course")
public class CourseController {

    @Autowired
    ICourseService courseService;

    @PostMapping("/getCourseList")
    public Result getCourseList(@RequestBody @Validated BasicDTO basicDTO, BindingResult result){

        return courseService.getCourseList(basicDTO);
    }

    @PostMapping("/getUserInfo")
    public Result getUserInfo(){

        return null;
    }
}
