package com.activiti.z_six.operLog.service.impl;

import com.activiti.z_six.dto.controllerParams.OperLogParams;
import com.activiti.z_six.operLog.dao.OperLogMapper;
import com.activiti.z_six.operLog.dto.*;
import com.activiti.z_six.operLog.entity.OperLogEntity;
import com.activiti.z_six.operLog.service.OperLogService;
import com.activiti.z_six.common.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.UUID;

/**
 * <p>
 * 操作日志表 服务实现类
 * </p>
 *
 * @author zn
 * @date 2021-03-17
 * @version: V1.0
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class OperLogServiceImpl implements OperLogService {

    @Autowired
    private OperLogMapper operLogMapper;
    @Override
    public String addOperLog(InAddOperLogDto in) throws BusinessException{
        OperLogEntity po = new OperLogEntity();
        BeanUtils.copyProperties(in, po);
        String operId=UUID.randomUUID().toString();
        po.setOperId(operId);
        po.preInsert();
        po.setUpdateTime(null);
        operLogMapper.InAddOperLog(po);
        return operId;
    }
    @Override
    public Boolean updateOperLog(InUpdOperLogDto in) throws BusinessException {
       OperLogEntity po = new OperLogEntity();
       BeanUtils.copyProperties(in, po);
       po.preUpdate();
       int out = operLogMapper.InUpdOperLog(po);
		if (out == 1) {
          return true;
       } else {
          throw new BusinessException("请刷新数据重新操作!");
       }
    }
	
    @Override
    public Boolean deleteOperLog(OperLogParams dto) throws BusinessException {
       OperLogEntity po = new OperLogEntity();
       BeanUtils.copyProperties(dto, po);
       po.preDelete();
       int out = operLogMapper.InDealOperLog(po);
       if (out == 1) {
          return true;
       } else {
          throw new BusinessException("请刷新数据重新操作!");
       }
    }
    /**
     * 查询正常日志
     * @return
     * @throws BusinessException
     */
    @Override
    public HashMap<String,Object> findOperLogPage(OperLogParams dto) throws BusinessException {
        Integer startIndex=(dto.getPageNum()-1)*dto.getPageSize();
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("list",operLogMapper.findOperLogPage(dto,startIndex,dto.getPageSize()));
        hashMap.put("total",operLogMapper.findOperLog(dto));
       return hashMap;
    }
}
