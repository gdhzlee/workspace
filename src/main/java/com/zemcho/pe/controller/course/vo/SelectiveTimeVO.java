package com.zemcho.pe.controller.course.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDate;

@Setter
@Getter
public class SelectiveTimeVO  implements Serializable {

    private Long start;

    @JsonProperty(value = "start_time")
    private Long startTime;

    private Long end;

    @JsonProperty(value = "end_time")
    private Long endTime;

    private int campus;

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Timestamp getStartTime(){

        return new Timestamp(startTime * 1000);
    }

    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
    public Timestamp getEndTime(){

        return new Timestamp(endTime * 1000);
    }
}
