package com.zemcho.pe.service.course.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.zemcho.pe.common.Result;
import com.zemcho.pe.common.Message;
import com.zemcho.pe.controller.course.dto.BasicDTO;
import com.zemcho.pe.controller.course.vo.CourseVO;
import com.zemcho.pe.mapper.course.CourseMapper;
import com.zemcho.pe.service.course.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CourseService implements ICourseService {

    @Autowired
    CourseMapper courseMapper;

    @Override
    public Result getCourseList(BasicDTO basicDTO) {

        int page = basicDTO.getPage();
        int pageRows = basicDTO.getPageRows();

        PageHelper.startPage(page,pageRows);
        List<CourseVO> courseVOS = courseMapper.selectCourseList();

        // TODO 需要循环添加课程剩余

        PageInfo<CourseVO> pageInfo = new PageInfo<>(courseVOS);

        Map<String,Object> result = new HashMap<>();
        result.put("list",courseVOS);
        result.put("count",pageInfo.getTotal());
        result.put("page",pageInfo.getPageNum());

        return new Result(Message.SU_COURSE_LIST, result);
    }
}
