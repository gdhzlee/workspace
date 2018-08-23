package com.zemcho.pe.controller.course.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Min;

@Getter
@Setter
public class BasicDTO {

    @JsonProperty(value = "page")
    @Min(value = 1, message = "ERR_PAGE_PARAM")
    private int page = 1;

    @JsonProperty(value = "page_rows")
    @Min(value = 1, message = "ERR_PAGE_PARAM")
    private int pageRows = 8;
}
