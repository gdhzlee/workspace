package com.zemcho.pe.controller.course;

import com.zemcho.pe.common.Result;
import com.zemcho.pe.controller.course.dto.BasicDTO;
import com.zemcho.pe.controller.course.dto.CancelDTO;
import com.zemcho.pe.controller.course.dto.SelectCourseDTO;
import com.zemcho.pe.service.course.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * 体育选课
 *
 * @Author Jetvin
 * @Date 2018/8/29
 * @Time 16:17
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
@RestController
@RequestMapping("/Course")
public class CourseController {

    @Autowired
    ICourseService courseService;


    /**
     * 获取课表
     * @param basicDTO -> page：页码；page_rows：页面大小；user_number：学号
     * @param result
     * @return
     */
    @PostMapping("/getCourseList")
    public Result getCourseList(HttpServletRequest request, @RequestBody @Validated BasicDTO basicDTO, BindingResult result){

        basicDTO.setRequest(request);
        return courseService.getCourseList(basicDTO);
    }

    @GetMapping("/getCourseList")
    public Result getCourseList(BasicDTO basicDTO){

        return courseService.getCourseList(basicDTO);
    }

    /**
     * 选课
     * @param selectCourseDTO -> schId：课程ID；phone：联系电话；user_number：学号
     * @param result
     * @return
     */
    @PostMapping("/selectCourse")
    public Result selectCourse(HttpServletRequest request, @RequestBody @Validated SelectCourseDTO selectCourseDTO, BindingResult result){

        selectCourseDTO.setRequest(request);
        return courseService.selectCourse(selectCourseDTO);
    }

    /**
     * 取消选课
     * @param cancelDTO -> id：选课记录ID；uid：用户ID
     * @param result
     * @return
     */
    @PostMapping("/cancelCourseSelect")
    public Result cancelCourseSelect(@RequestBody @Validated CancelDTO cancelDTO, BindingResult result){

        // 注：这里并发量不大可以选择上锁
        synchronized (this){
            return courseService.cancelCourseSelect(cancelDTO);
        }
    }

    /**
     * 获取用户信息
     * @param basicDTO -> user_number：学号
     * @param result
     * @return
     */
    @PostMapping("/getUserInfo")
    public Result getUserInfo(HttpServletRequest request, @RequestBody @Validated BasicDTO basicDTO, BindingResult result){

        basicDTO.setRequest(request);
        return courseService.getUserInfo(basicDTO);
    }
}
