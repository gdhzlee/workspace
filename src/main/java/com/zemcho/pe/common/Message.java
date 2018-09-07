package com.zemcho.pe.common;

public enum Message {

    /* SUCCESS */
    SU_COURSE_SELECT(200,"选课成功"),
    SU_COURSE_WITHDRAW(200,"退选成功"),
    SU_COURSE_LIST(200,"获取课程表成功"),
    SU_STUDENT_INFO(200,"获取学生信息成功"),
    SU_TEACHER_INFO(200,"获取教师信息成功"),
    SU_RECORD_SELECT(200,"获取选课记录成功"),
    SU_RECORD_WITHDRAW(200,"获取退选记录成功"),
    SU_START_COURSE_NUMBER_JOB(200,"更新班级已选人数的工作任务启动成功"),
    SU_SHUTDOWN_COURSE_NUMBER_JOB(200,"更新班级已选人数的工作任务关闭成功"),
    SU_DATA_SYNCHRONIZATION_SUCCESS(200,"数据同步成功"),

    /* ERROR */
    ERR_SERVER_HTTP_MEDIA_TYPE_NOT_SUPPORTED(400,"Content-Type的类型错误"),
    ERR_SERVER_HTTP_MESSAGE_NOT_READABLE(400,"请求缺少body"),
    ERR_SERVER_HTTP_REQUEST_METHOD_NOT_SUPPORTED(400,"请求方式不支持"),
    ERR_SERVER_EXCEPTION(400,"服务器处理异常，请联系管理员"),
    ERR_NOT_STUDENT(400,"学生不存在或学生没有选课资格"),
    ERR_NOT_TEACHER(400,"教师不存在"),
    ERR_NOT_LOGIN(400,"用户未登录"),
    ERR_NOT_AUTH(400,"无权限访问"),
    ERR_PAGE_PARAM(400,"分页参数错误"),
    ERR_INCOMPLETE_DATA(400,"数据不完善，请联系管理员"),
    ERR_NOT_SCHEDULES(400,"缺少排课ID"),
    ERR_NOT_PHONE(400,"缺少联系方式"),
    ERR_COURSE_NOT_ENOUGH(400,"课程可选数不足"),
    ERR_COURSE_SELECT(400,"选课失败,学生可能未选课或已退选"),
    ERR_NOT_USER_NUMBER(400,"缺少学号"),
    ERR_NOT_COURSE_RECORD_ID(400,"缺少选课记录ID"),
    ERR_NOT_COURSE_RECORD(400,"选课记录不存在"),
    ERR_COURSE_WITHDRAW(400,"取消选课失败"),
    ERR_HAD_SELECTED(400,"已成功选课，请到个人事务查看"),
    ERR_COURSE_DISABLE_SELECTED(400,"课程不满足选课要求"),
    ERR_NOT_SELECTIVE_TIME(400,"非选课时间段，不可进行选课操作"),
    ERR_NOT_OPEN_TIME(400,"非选课时间段，系统未开放"),
    ERR_ERROR_AUTHORIZATION_VALUE(400,"用户认证信息的值错误，请联系管理员"),
    ERR_ERROR_AUTHORIZATION_RELATIVE(400,"用户认证信息与学号匹配错误"),
    ERR_ERROR_SCHEDULES(400,"排课ID错误"),
    ERR_COURSE_NUMBER_JOB_EXIST(400,"更新班级已选人数的工作任务已存在"),
    ERR_COURSE_NUMBER_JOB_NOT_EXIST(400,"更新班级已选人数的工作任务不存在"),
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
