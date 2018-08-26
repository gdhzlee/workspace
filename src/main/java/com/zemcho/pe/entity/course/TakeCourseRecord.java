package com.zemcho.pe.entity.course;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TakeCourseRecord {

    private Integer id;

    private Integer year;

    private Integer term;

    private Integer uid;

    private Integer schId;

    private Integer classId;

    private String phone;

    private String remark;

    private Integer operateType;

    private Long createTime;

    private Long updateTime;

    private Integer status;

    private Boolean isDel;

    private Integer exemptStatus;
}
