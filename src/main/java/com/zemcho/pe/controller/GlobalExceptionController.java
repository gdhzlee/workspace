package com.zemcho.pe.controller;

import com.zemcho.pe.common.Result;
import com.zemcho.pe.common.Message;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionController {

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public Result handleException(Exception e) {
        e.printStackTrace();
        log.error(ExceptionUtils.getFullStackTrace(e));  // 记录错误信息
        return new Result(Message.ERR_SERVER_EXCEPTION);
    }

}
