package com.zemcho.pe.controller.course.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PreviewTimeVO {

    private LocalDateTime previewStartTime;
    private LocalDateTime previewEndTime;
}
