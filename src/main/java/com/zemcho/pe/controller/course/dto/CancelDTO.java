package com.zemcho.pe.controller.course.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Setter
@Getter
public class CancelDTO {

    @JsonProperty(value = "user_number")
    @NotNull(message = "ERR_NOT_USER_NUMBER")
    private String userNumber;

    @JsonProperty(value = "sch_id")
    @NotNull(message = "ERR_NOT_SCHEDULES")
    private Integer schId;
}
