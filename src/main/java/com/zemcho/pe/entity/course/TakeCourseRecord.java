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

    private String phone;

    private String remark;

    private Long createTime;

    private Long updateTime;

    private Boolean status;

    private Boolean isDel;

    private Boolean exemptStatus;
}
