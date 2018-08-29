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
    ERR_NOT_SCHEDULES(1008,"缺少排课ID"),
    ERR_NOT_PHONE(1009,"缺少联系方式"),
    ERR_COURSE_NOT_ENOUGH(1010,"课程可选数不足"),
    ERR_COURSE_SELECT(1011,"选课失败"),
    ERR_NOT_USER_NUMBER(1012,"缺少学号"),
    ERR_NOT_COURSE_RECORD_ID(1013,"缺少选课记录ID"),
    ERR_NOT_COURSE_RECORD(1014,"选课记录不存在"),
    ERR_COURSE_WITHDRAW(1015,"取消选课失败"),
    ERR_HAD_SELECTED(1016,"已成功选课，不可再次提交选择"),
    ERR_COURSE_DISABLE_SELECTED(1017,"课程不满足选课要求"),
    ERR_NOT_SELECTIVE_TIME(1018,"非选课时间段，不可进行选课操作"),
    ERR_NOT_OPEN_TIME(1019,"非选课时间段，系统未开放"),
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
