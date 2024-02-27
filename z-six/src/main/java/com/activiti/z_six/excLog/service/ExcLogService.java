package com.activiti.z_six.excLog.service;

import com.activiti.z_six.common.BusinessException;
import com.activiti.z_six.excLog.dto.*;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 操作异常日志 服务类
 * </p>
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
public interface ExcLogService {

    /**
     * 新增数据
     *
     * @param in
     * @return
     */
    String addExcLog(InAddExcLogDto in) throws BusinessException;

    /**
    * 修改数据
    *
    * @param in
    * @return
    */
    Boolean updateExcLog(InUpdExcLogDto in) throws BusinessException;

    /**
    * 删除系统用户
    *
    * @param in
    * @return
    */
    Boolean deleteExcLog(InDealExcLogDto in) throws BusinessException;

    /**
    * 分页查询数据
    *
    * @param in
    * @return
    */
    List<OutQueryExcLogDto> findExcLogPage(InQueryExcLogDto in,Integer page,
                                           Integer pageSize) throws BusinessException;

}
