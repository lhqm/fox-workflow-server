package com.activiti.z_six.operLog.dao;

import com.activiti.z_six.dto.controllerParams.OperLogParams;
import com.activiti.z_six.operLog.dto.OutQueryOperLogDto;
import java.util.List;

import com.activiti.z_six.operLog.entity.OperLogEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * 操作日志表 Mapper 接口
 * </p>
 * @version: V1.0
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Mapper
@Repository
public interface OperLogMapper {
     /**
      * 分页查询数据
      * @return
      * @throws Exception
      */
     List<OutQueryOperLogDto> findOperLogPage(@Param("dto") OperLogParams dto,
                                              @Param("startIndex") Integer startIndex,
                                              @Param("pageSize") Integer pageSize);

     Integer findOperLog(@Param("dto") OperLogParams dto);

     /**
      * 增加日志
      * @param dto
      * @return
      */
     int InAddOperLog(@Param("dto") OperLogEntity dto);

     /**
      * 更新日志
      * @param dto
      * @return
      */
     int InUpdOperLog(@Param("dto") OperLogEntity dto);

     /**
      * 删除日志
      * @param dto
      * @return
      */
     int InDealOperLog(@Param("dto") OperLogEntity dto);
}
