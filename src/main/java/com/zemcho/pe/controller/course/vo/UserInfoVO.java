package com.zemcho.pe.controller.course.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Setter
@Getter
public class UserInfoVO implements Serializable {

    private List<SelectiveTimeVO> first;

    private List<SelectiveTimeVO> second;

    private Integer uid;

    private String username;

    private String name;

    private Integer type;

    private Integer faculty;

    @JsonProperty("class")
    private Integer classId;

    private Integer sex;

    private String phone;

    @JsonProperty("class_title")
    private String classTitle;

    private String grade;

    private Integer campus;

    @JsonProperty("faculty_title")
    private String facultyTitle;

    @JsonProperty("year_term")
    private String yearTerm;
}
