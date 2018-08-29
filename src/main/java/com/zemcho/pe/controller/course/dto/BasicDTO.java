package com.zemcho.pe.controller.course.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
public class BasicDTO {

    @JsonProperty(value = "page")
    @Min(value = 1, message = "ERR_PAGE_PARAM")
    private Integer page = 1;

    @JsonProperty(value = "page_rows")
    @Min(value = 1, message = "ERR_PAGE_PARAM")
    private Integer pageRows = 8;

    @JsonProperty(value = "user_number")
    private String userNumber = "";

//    private Integer year;
//
//    private Integer term;
}
