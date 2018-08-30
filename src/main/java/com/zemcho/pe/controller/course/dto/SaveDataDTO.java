package com.zemcho.pe.controller.course.dto;

import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;

@Setter
@Getter
public class SaveDataDTO {

    HttpServletRequest request;
    Integer uid;
    Integer schId;
    Integer classId;
    String phone;
    Integer campus;
    Integer courseId;
    Integer teacherId;
    Integer faculty;
}
