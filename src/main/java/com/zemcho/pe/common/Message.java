package com.zemcho.pe.common;

public enum Message {

    /* SUCCESS */
    SU_COURSE_SELECT(1000,"选课成功"),
    SU_COURSE_WITHDRAW(1000,"退选成功"),
    SU_COURSE_LIST(1000,"获取课程表成功"),
    SU_STUDENT_INFO(1000,"获取学生信息成功"),
    SU_TEACHER_INFO(1000,"获取教师信息成功"),
    SU_RECORD_SELECT(1000,"获取选课记录成功"),
    SU_RECORD_WITHDRAW(1000,"获取退选记录成功"),

    /* ERROR */
    ERR_SERVER_HTTP_MESSAGE_NOT_READABLE(1001,"请求缺少body"),
    ERR_SERVER_HTTP_REQUEST_METHOD_NOT_SUPPORTED(1001,"请求方式不支持"),
    ERR_SERVER_EXCEPTION(1001,"服务器处理异常，请联系管理员"),
    ERR_NOT_STUDENT(1002,"学生不存在"),
    ERR_NOT_TEACHER(1003,"教师不存在"),
    ERR_NOT_LOGIN(1004,"用户未登录"),
    ERR_NOT_AUTH(1005,"无权限访问"),
    ERR_PAGE_PARAM(1006,"分页参数错误"),
    ERR_INCOMPLETE_DATA(1007,"数据不完善，请联系管理员"),
    ;

    private int code;

    private String message;

    Message(int code, String message){
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
