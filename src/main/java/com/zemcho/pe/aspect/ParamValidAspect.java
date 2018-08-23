package com.zemcho.pe.aspect;

import com.zemcho.pe.common.Message;
import com.zemcho.pe.common.Result;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.validation.BindingResult;

/**
 * 参数校验
 * @author Jetvin
 * @date 2018/3/16
 * @time 16:38
 * @Version ╮(╯▽╰)╭
 *
 * <!--         ░░░░░░░░░░░░░░░░░░░░░░░░▄░░         -->
 * <!--         ░░░░░░░░░▐█░░░░░░░░░░░▄▀▒▌░         -->
 * <!--         ░░░░░░░░▐▀▒█░░░░░░░░▄▀▒▒▒▐          -->
 * <!--         ░░░░░░░▐▄▀▒▒▀▀▀▀▄▄▄▀▒▒▒▒▒▐          -->
 * <!--         ░░░░░▄▄▀▒░▒▒▒▒▒▒▒▒▒█▒▒▄█▒▐          -->
 * <!--         ░░░▄▀▒▒▒░░░▒▒▒░░░▒▒▒▀██▀▒▌          -->
 * <!--         ░░▐▒▒▒▄▄▒▒▒▒░░░▒▒▒▒▒▒▒▀▄▒▒          -->
 * <!--         ░░▌░░▌█▀▒▒▒▒▒▄▀█▄▒▒▒▒▒▒▒█▒▐         -->
 * <!--         ░▐░░░▒▒▒▒▒▒▒▒▌██▀▒▒░░░▒▒▒▀▄         -->
 * <!--         -░▌░▒▄██▄▒▒▒▒▒▒▒▒▒░░░░░░▒▒▒▒        -->
 * <!--         ▀▒▀▐▄█▄█▌▄░▀▒▒░░░░░░░░░░▒▒▒         -->
 * <!--                                             -->
 * <!--                 咦！有人在改BUG               -->
 */
@Aspect
@Component
public class ParamValidAspect {

    @Around("(@within(org.springframework.web.bind.annotation.RestController) || @within(org.springframework.stereotype.Controller)) && args(..,result)")
    public Object paramValid(ProceedingJoinPoint point, BindingResult result) throws Throwable{
        if(result.hasErrors()){
            String defaultMessage = result.getFieldError().getDefaultMessage();
            Message message = Message.valueOf(defaultMessage);

            if (message != null){
                return new Result(message);
            }

            return new Result(1001, defaultMessage);
        }

        return point.proceed();
    }

}
