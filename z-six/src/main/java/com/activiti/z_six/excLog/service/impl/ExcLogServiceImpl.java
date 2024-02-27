package com.activiti.z_six.excLog.service.impl;

import com.activiti.z_six.common.BusinessException;
import com.activiti.z_six.excLog.dao.ExcLogMapper;
import com.activiti.z_six.excLog.dto.*;
import com.activiti.z_six.excLog.entity.ExcLogEntity;
import com.activiti.z_six.excLog.service.ExcLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 操作异常日志 服务实现类
 * </p>
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class ExcLogServiceImpl implements ExcLogService {

    @Autowired
    private ExcLogMapper excLogMapper;
    @Override
    public String addExcLog(InAddExcLogDto in) throws BusinessException {
       ExcLogEntity po = new ExcLogEntity();
       BeanUtils.copyProperties(in, po);
       String excId= UUID.randomUUID().toString();
       po.setExcId(excId);
       po.preInsert();
       po.setUpdateTime(null);
        excLogMapper.InAddExcLog(po);
       return excId;
    }

    @Override
    public Boolean updateExcLog(InUpdExcLogDto in) throws BusinessException{
       ExcLogEntity po = new ExcLogEntity();
       BeanUtils.copyProperties(in, po);
       po.preUpdate();
       int out = excLogMapper.InUpdExcLog(po);
		if (out == 1) {
          return true;
       } else {
          throw new BusinessException("请刷新数据重新操作!");
       }
    }
	
    @Override
    public Boolean deleteExcLog(InDealExcLogDto in) throws BusinessException{
       ExcLogEntity po = new ExcLogEntity();
       BeanUtils.copyProperties(in, po);
		po.preDelete();
       int out = excLogMapper.InDealExcLog(po);
       if (out == 1) {
          return true;
       } else {
          throw new BusinessException("请刷新数据重新操作!");
       }
    }

    @Override
    public List<OutQueryExcLogDto> findExcLogPage(InQueryExcLogDto query,Integer page,
                                                  Integer pageSize) throws BusinessException{
        Integer startIndex=(page-1)*pageSize;
       return excLogMapper.findExcLogPage(query,startIndex,pageSize);
    }
}
