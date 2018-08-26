package com.zemcho.pe.mapper.course;

import com.zemcho.pe.controller.course.vo.CourseCountVO;
import com.zemcho.pe.controller.course.vo.CourseVO;
import com.zemcho.pe.controller.course.vo.SelectiveTimeVO;
import com.zemcho.pe.entity.course.TakeCourseRecord;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CourseMapper {

    List<CourseVO> selectCourseList(@Param("year")Integer year, @Param("term") Integer term);

    List<SelectiveTimeVO> selectTimeList(@Param("configId") Integer configId, @Param("isGregory") Boolean isGregory);

    Integer selectConfigIdByYearAndTerm(@Param("year")Integer year, @Param("term")Integer term);

    List<CourseCountVO> selectCourseCountByYearAndTerm(@Param("year")Integer year, @Param("term")Integer term);

    Integer saveTakeSourceRecord(@Param("records") List<TakeCourseRecord> records);
}
