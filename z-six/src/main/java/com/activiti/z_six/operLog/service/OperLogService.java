package com.activiti.z_six.operLog.service;

import com.activiti.z_six.dto.controllerParams.OperLogParams;
import com.activiti.z_six.operLog.dto.*;
import com.activiti.z_six.common.BusinessException;

import java.util.HashMap;

/**
 * <p>
 * 操作日志表 服务类
 * </p>
 *
 * @author zn
 * @date 2021-03-17
 * @version: V1.0
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
public interface OperLogService {

    /**
     * 新增数据
     *
     * @param in
     * @return
     */
    String addOperLog(InAddOperLogDto in) throws BusinessException;

    /**
    * 修改数据
    *
    * @param in
    * @return
    */
    Boolean updateOperLog(InUpdOperLogDto in) throws BusinessException;

    /**
    * 删除系统用户
    *
    * @param in
    * @return
    */
    Boolean deleteOperLog(OperLogParams dto) throws BusinessException;

    /**
    * 分页查询数据
    * @return
    */
    HashMap<String,Object> findOperLogPage(OperLogParams dto) throws BusinessException;

}
