package com.zemcho.pe.controller.course.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class UserInfoVO {

    private List<SelectiveTimeVO> first;

    private List<SelectiveTimeVO> second;
}
