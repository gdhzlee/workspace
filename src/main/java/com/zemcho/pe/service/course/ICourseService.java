package com.zemcho.pe.service.course;

import com.zemcho.pe.common.Result;
import com.zemcho.pe.controller.course.dto.BasicDTO;

public interface ICourseService {

    Result getCourseList(BasicDTO basicDTO);
}
