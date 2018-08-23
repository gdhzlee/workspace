package com.zemcho.pe.controller.course.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CourseVO {

    @JsonProperty(value = "year")
    private int year;

    @JsonProperty(value = "term")
    private int term;

    @JsonProperty(value = "sch_id")
    private int schId;

    @JsonProperty(value = "course_id")
    private int courseId;

    @JsonProperty(value = "course_name")
    private String courseName;

    @JsonProperty(value = "course_site")
    private String courseSite;

    @JsonProperty(value = "course_campus")
    private int courseCampus;

    @JsonProperty(value = "teacher_name")
    private String teacherName;

    @JsonProperty(value = "limit_person")
    private int limitPerson;

    @JsonProperty(value = "remaining")
    private int remaining;

    @JsonProperty(value = "sex")
    private int sex;

    @JsonProperty(value = "section_time")
    private String sectionTime;

    @JsonProperty(value = "week_class_time")
    private String weekClassTime;

    public String getSex(){
        if (sex == 1){
            return "男";
        }else if (sex == 2){
            return "女";
        }else if (sex == 3){
            return "不限";
        }else {
            return "-";
        }
    }
}
