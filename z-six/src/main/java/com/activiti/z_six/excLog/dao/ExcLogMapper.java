package com.activiti.z_six.excLog.dao;

import com.activiti.z_six.excLog.dto.InQueryExcLogDto;
import com.activiti.z_six.excLog.dto.OutQueryExcLogDto;
import com.activiti.z_six.excLog.entity.ExcLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * <p>
 * 操作异常日志 Mapper 接口
 * </p>
 *
 * @author zn
 * @date 2021-03-18
 * @version: V1.0
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Mapper
@Repository
public interface ExcLogMapper {
     /**
      * 分页查询数据
      *
      * @param in
      * @return
      * @throws Exception
      */
     List<OutQueryExcLogDto> findExcLogPage(@Param("dto") InQueryExcLogDto in,
                                            @Param("startIndex") Integer startIndex,
                                            @Param("pageSize") Integer pageSize);

     /**
      * 增加日志
      * @param dto
      * @return
      */
     int InAddExcLog(@Param("dto") ExcLogEntity dto);

     /**
      * 更新日志
      * @param dto
      * @return
      */
     int InUpdExcLog(@Param("dto") ExcLogEntity dto);

     /**
      * 删除日志
      * @param dto
      * @return
      */
     int InDealExcLog(@Param("dto") ExcLogEntity dto);
}
