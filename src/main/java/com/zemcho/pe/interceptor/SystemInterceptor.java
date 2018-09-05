package com.zemcho.pe.interceptor;

import com.alibaba.fastjson.JSON;
import com.zemcho.pe.common.Message;
import com.zemcho.pe.common.Result;
import com.zemcho.pe.config.initial.InitialConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @Autowired
    RedisTemplate<String,String> ehallStringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        HttpServletRequest httpServletRequest = request;

        String requestURI = httpServletRequest.getRequestURI();
        log.info("url:{}",requestURI);

        //TODO 不知道要不要， 校验用户登录
        String authorization = httpServletRequest.getHeader("authorization");
        if (authorization == null){
            return returnFailMessage(response, new Result(Message.ERR_NOT_LOGIN));
        }

        LettuceConnectionFactory factory = (LettuceConnectionFactory) ehallStringRedisTemplate.getConnectionFactory();
        String hostName = factory.getHostName();
        String password = factory.getPassword();
        int port = factory.getPort();
        int database = factory.getDatabase();
        log.info("大厅redis {host:{}, password:{}, port:{}, database:{}}",hostName, password, port, database);
        String var0 = ehallStringRedisTemplate.opsForValue().get("ehall_zc_session_" + authorization);

        if (var0 == null || "".equals(var0)){
            return returnFailMessage(response, new Result(Message.ERR_NOT_LOGIN));
        }

        log.info("authorization:{}", var0);
        String uid = getUid(var0, InitialConfig.RGEX);
        if (uid.equals("")){
            return returnFailMessage(response, new Result(Message.ERR_ERROR_AUTHORIZATION_VALUE));
        }
        request.setAttribute("uid",Integer.valueOf(uid));


        /* ********************************* */
        // TODO 测试时可以开
        //      uid             userNumber
        //      18428           201341404154

//        request.setAttribute("uid",18428);

        /* ********************************* */


//        String[] var1 = var0.split(";");
//        if (var1.length != 5){
//            return returnFailMessage(response, new Result(Message.ERR_ERROR_AUTHORIZATION_VALUE));
//        }
//
//        String var2 = var1[1];
//        String[] var3 = var2.split(":");
//        if (var3.length != 2){
//            return returnFailMessage(response, new Result(Message.ERR_ERROR_AUTHORIZATION_VALUE));
//        }
//
//        String var4 = var3[1];
//        request.setAttribute("uid",Integer.valueOf(var4));



        // 校验预览时间与选课时间
        LocalDateTime now = LocalDateTime.now();

        if (InitialConfig.isPreview() == 1){
            if (requestURI.equals("/dgut-sports/java/api/Course/selectCourse")){
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

    private String getUid(String soap, String rgex){
        Pattern pattern = Pattern.compile(rgex);// 匹配的模式
        Matcher m = pattern.matcher(soap);
        while(m.find()){
            return m.group(1);
        }
        return "";
    }
}
