package com.zemcho.pe.mapper.course;

import com.zemcho.pe.controller.course.vo.*;
import com.zemcho.pe.entity.course.CourseResults;
import com.zemcho.pe.entity.course.FormLog;
import com.zemcho.pe.entity.course.SelectCourseLog;
import com.zemcho.pe.entity.course.SelectCourseRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseMapper {

    /* 获取课表 */
    List<CourseVO> selectCourseList(@Param("classId") Integer classId, @Param("year") Integer year, @Param("term") Integer term);

    /* 获取选课时间 */
    List<SelectiveTimeVO> selectTimeList(@Param("configId") Integer configId, @Param("isGregory") Boolean isGregory);

    /* 获取配置ID */
    Integer selectConfigIdByYearAndTerm(@Param("year") Integer year, @Param("term") Integer term);

    /* 获取课程限定人数 */
    List<CourseCountVO> selectCourseCountByYearAndTerm(@Param("year") Integer year, @Param("term") Integer term);

    /* 保存选课记录 */
    Integer saveSelectSourceRecord(SelectCourseRecord record);

    /* 获取选课记录 */
    SelectCourseRecordVO selectSelectedCourseRecord(@Param("uid") Integer uid, @Param("id") Integer id);

    /* 取消选课 */
    Integer cancelTakeCourse(@Param("uid") Integer uid, @Param("id") Integer id);

    /* 获取选课学生名单 */
    List<UserInfoVO> selectUserInfoByCampusAndYearAndTerm(@Param("year") Integer year, @Param("term") Integer term);

    /* 获取选课班级ID */
    List<Integer> selectClassIdAll();

    /* 获取预览时间 */
    PreviewTimeVO selectPreviewTimeByYearAndTerm(@Param("year") Integer year, @Param("term") Integer term);

    /* 获取用户信息 */
    UserInfoVO selectUserInfoById(@Param("id") Integer id);

    /* 获取表单ID */
    Integer selectFormIdByCode(@Param("code") String code);

    /* 保存选课日志 */
    Integer saveSelectCourseLog(SelectCourseLog log);

    /* 保存学生成绩 */
    Integer saveCourseResults(CourseResults results);

    /* 获取学生成绩 */
    CourseResults selectCourseResultsByUid(@Param("uid") Integer uid, @Param("year")Integer year, @Param("term")Integer term);

    /* 更新学生成绩 */
    Integer updateCourseResults(CourseResults results);

    /* 事务日志 */
    Integer saveFormLog(FormLog log);

    Integer updateCourseNumber(@Param("year") Integer year, @Param("term") Integer term, @Param("classId") Integer classId, @Param("courseNumber") Integer courseNumber, @Param("updateTime") Long updateTime);
}
