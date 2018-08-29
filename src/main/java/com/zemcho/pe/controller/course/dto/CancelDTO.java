package com.zemcho.pe.controller.course.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class CancelDTO {

    @NotNull(message = "ERR_NOT_COURSE_RECORD_ID")
    private Integer id;

    @NotNull(message = "ERR_NOT_UID")
    private Integer uid;
}
