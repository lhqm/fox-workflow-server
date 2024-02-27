package com.activiti.z_six.service.impl;

import com.activiti.z_six.dto.controllerParams.DictDataParams;
import com.activiti.z_six.entity.dictData.DictDataEntity;
import com.activiti.z_six.mapper.dict.DictMapper;
import com.activiti.z_six.service.IDictDataService;
import com.activiti.z_six.service.manager.CommManager;
import com.activiti.z_six.strategy.dict.DictContext;
import com.activiti.z_six.strategy.dict.DictStrategy;
import com.activiti.z_six.util.DateUtils;
import com.activiti.z_six.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class IDictDataServiceImpl implements IDictDataService {
    @Autowired
    private DictMapper dictMapper;
    @Autowired
    private DictContext dictContext;
    @Autowired
    private CommManager commManager;

    /**
     * 查询字典列表
     * @param dictDataParams
     * @return
     */
    @Override
    public HashMap<String,Object> getDicts(DictDataParams dictDataParams){
        HashMap<String,Object> hashMap=new HashMap<>();
        List<DictDataEntity> dictDataEntities=dictMapper.getDicts(dictDataParams.getName()
                , dictDataParams.getStatus());
        if(dictDataParams.getPage()==0){
            hashMap.put("total",dictDataEntities.size());
            hashMap.put("list",dictDataEntities);
        }
        else{
            Integer startIndex=(dictDataParams.getPage()-1)*dictDataParams.getPageSize();
            dictDataEntities=dictMapper.getDictsPage(dictDataParams.getName(),dictDataParams.getStatus()
            ,startIndex,dictDataParams.getPageSize());
            hashMap.put("total",dictDataEntities.size());
            hashMap.put("list",dictDataEntities);
        }
        return hashMap;
    }
    /**
     * 根据字典编号，获取字典详情
     * @param dictData
     * @return
     */
    @Override
    public List<Map<String,Object>> getDictAndData(DictDataEntity dictData){
        if(StringUtils.isNull(dictData.getDataType())){
            dictData=dictMapper.dictDataEntity(dictData.getNo());
        }
        DictStrategy dictStrategy=dictContext.getService(dictData.getDataType());
        List<Map<String,Object>> mapList=dictStrategy.getDictData(dictData);
        return mapList;
    }
    /**
     * 增加字典
     * @param dictData
     * @return
     */
    @Override
    public int addDict(DictDataEntity dictData){
        DictDataEntity dictDataEntity=new DictDataEntity();
        BeanUtils.copyProperties(dictData,dictDataEntity);
        dictDataEntity.setCreateBy(commManager.getUserNo());
        dictDataEntity.setCreateTime(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
        return dictMapper.addDict(dictDataEntity);
    }
    /**
     * 更新字典
     * @param dictData
     * @return
     */
    @Override
    public int updateDict(DictDataEntity dictData){
        DictDataEntity dictDataEntity=new DictDataEntity();
        BeanUtils.copyProperties(dictData,dictDataEntity);
        dictDataEntity.setUpdateBy(commManager.getUserNo());
        dictDataEntity.setUpdateTime(DateUtils.getDate("yyyy-MM-dd HH:mm:ss"));
        return dictMapper.updateDict(dictDataEntity);
    }
    /**
     * 删除字典
     * @param dictDataEntities
     * @return
     */
    @Override
    public int deleteDict(List<DictDataEntity> dictDataEntities){
        return dictMapper.deleteDict(dictDataEntities);
    }
}
