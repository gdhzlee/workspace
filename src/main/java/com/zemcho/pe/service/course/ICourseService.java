package com.zemcho.pe.service.course;

import com.zemcho.pe.common.Result;
import com.zemcho.pe.controller.course.dto.BasicDTO;
import com.zemcho.pe.controller.course.dto.CancelDTO;
import com.zemcho.pe.controller.course.dto.SelectCourseDTO;

public interface ICourseService {

    Result getCourseList(BasicDTO basicDTO);

    Result selectCourse(SelectCourseDTO selectCourseDTO);

    Result cancelCourseSelect(CancelDTO cancelDTO);

    Result getUserInfo(BasicDTO basicDTO);
}
