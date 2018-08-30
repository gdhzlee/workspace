package com.zemcho.pe.controller.course.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Setter
@Getter
public class SelectCourseDTO {

    @JsonProperty("sch_id")
    @NotNull(message = "ERR_NOT_SCHEDULES")
    private Integer schId;

    @NotBlank(message = "ERR_NOT_PHONE")
    private String phone;

    @JsonProperty("user_number")
    @NotNull(message = "ERR_NOT_USER_NUMBER")
    private String userNumber;

    HttpServletRequest request;
}
