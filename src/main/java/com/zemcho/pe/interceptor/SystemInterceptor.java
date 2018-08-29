package com.zemcho.pe.interceptor;

import com.alibaba.fastjson.JSON;
import com.zemcho.pe.common.Message;
import com.zemcho.pe.common.Result;
import com.zemcho.pe.config.InitialConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;

/**
 * 全局拦截器
 *
 * @Author Jetvin
 * @Date 2018/8/29
 * @Time 17:12
 * @Version ╮(╯▽╰)╭
 *
 * <!--         ░░░░░░░░░░░░░░░░░░░░░░░░▄░░░        -->
 * <!--         ░░░░░░░░░▐█░░░░░░░░░░░▄▀▒▌░░        -->
 * <!--         ░░░░░░░░▐▀▒█░░░░░░░░▄▀▒▒▒▐ ░        -->
 * <!--         ░░░░░░░▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐░░        -->
 * <!--         ░░░░░▄▄▀▒░▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐ ░        -->
 * <!--         ░░░▄▀▒▒▒░░░▒▒▒░░░▒▒▒▀██▀▒▌░░        -->
 * <!--         ░░▐▒▒▒▄▄▒▒▒▒░░░▒▒▒▒▒▒▒▀▄▒▒ ░        -->
 * <!--         ░░▌░░▌█▀▒▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒▐░        -->
 * <!--         ░▐░░░▒▒▒▒▒▒▒▒▌██▀▒▒░░░▒▒▒▀▄░        -->
 * <!--         -░▌░▒▄██▄▒▒▒▒▒▒▒▒▒░░░░░░▒▒▒▒        -->
 * <!--         ▀▒▀▐▄█▄█▌▄░▀▒▒░░░░░░░░░░▒▒▒░        -->
 * <!--                                             -->
 * <!--                 咦！有人在改BUG               -->
 */
@Slf4j
@Component
public class SystemInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        HttpServletRequest httpServletRequest = request;
        HttpServletResponse httpServletResponse = response;

        String requestURI = request.getRequestURI();
        log.info("url:{}",requestURI);


        //TODO 这不不知道要不要， 校验用户登录

        // 校验预览时间与选课时间
        LocalDateTime now = LocalDateTime.now();

        if (InitialConfig.isPreview() == 1){
            if (requestURI.equals("/sports/Course/selectCourse")){
                return returnFailMessage(response, new Result(Message.ERR_NOT_SELECTIVE_TIME));
            }
        }else {
            if (!InitialConfig.isSelective()){

                return returnFailMessage(response, new Result(Message.ERR_NOT_OPEN_TIME));
            }
        }

        return true;
    }

    public boolean returnFailMessage(HttpServletResponse response, Result result) {

        try {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().print(JSON.toJSONString(result));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }
}
