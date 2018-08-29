package com.zemcho.pe.controller.course.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Timestamp;

@Setter
@Getter
public class SelectiveTimeVO  implements Serializable {

    private Long start;

    @JsonProperty(value = "start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:dd", timezone = "GMT+8")
    private Timestamp startTime;

    private Long end;

    @JsonProperty(value = "end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:dd", timezone = "GMT+8")
    private Timestamp endTime;

    private int campus;

//    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
//    public Timestamp getStartTime(){
//
//        return new Timestamp(startTime * 1000);
//    }
//
//    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8")
//    public Timestamp getEndTime(){
//
//        return new Timestamp(endTime * 1000);
//    }
}
