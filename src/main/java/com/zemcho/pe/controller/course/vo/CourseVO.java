package com.zemcho.pe.controller.course.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.zemcho.pe.config.InitialConfig;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Setter
@Getter
public class CourseVO  implements Serializable {

    private int classId;

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
    private String sex;

    @JsonProperty(value = "section_time")
    private String sectionTime;

    @JsonProperty(value = "week_class_time")
    private String weekClassTime;

    public String getSex(){
        if ("1".equals(sex)){
            return "男";
        }else if ("2".equals(sex)){
            return "女";
        }else if ("3".equals(sex)){
            return "不限";
        }else {
            return sex;
        }
    }

    public String getSectionTime(){
        String s = InitialConfig.SECTION_TIME.get(sectionTime);
        return s == null ? sectionTime : s;
    }

    public String getWeekClassTime(){
        return "[" + weekClassTime.replace(",","-") + "]周";
    }
}
