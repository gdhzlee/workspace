package com.zemcho.pe.controller.course.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Setter
@Getter
public class SelectiveTimeVO {

    private Long start;

    @JsonProperty(value = "start_time")
    private LocalDate startTime;

    private Long end;

    @JsonProperty(value = "end_time")
    private LocalDate endTime;

    private int campus;
}
