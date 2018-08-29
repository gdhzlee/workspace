package com.zemcho.pe.mapper.course;

import com.zemcho.pe.controller.course.vo.*;
import com.zemcho.pe.entity.course.TakeCourseRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseMapper {

    List<CourseVO> selectCourseList(@Param("classId")Integer classId, @Param("year")Integer year, @Param("term") Integer term);

    List<SelectiveTimeVO> selectTimeList(@Param("configId") Integer configId, @Param("isGregory") Boolean isGregory);

    Integer selectConfigIdByYearAndTerm(@Param("year")Integer year, @Param("term")Integer term);

    List<CourseCountVO> selectCourseCountByYearAndTerm(@Param("year")Integer year, @Param("term")Integer term);

    Integer saveTakeSourceRecord(@Param("records") List<TakeCourseRecord> records);

    SelectCourseRecordVO selectSelectedCourceRecord(@Param("uid")Integer uid, @Param("id") Integer id);

    Integer cancelTakeCourse(@Param("uid")Integer uid, @Param("id") Integer id);

    List<UserInfoVO> selectUserInfoByCampusAndYearAndTerm(@Param("year")Integer year, @Param("term")Integer term);

    List<Integer> selectClassIdAll();

    PreviewTimeVO selectPreviewTimeByYearAndTerm(@Param("year")Integer year, @Param("term")Integer term);

    UserInfoVO selectUserInfoById(@Param("id")Integer id);
}
