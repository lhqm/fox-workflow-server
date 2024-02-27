package com.activiti.z_six.strategy.manager;

import com.activiti.z_six.dto.controllerParams.CCParams;
import com.activiti.z_six.entity.taskAssignee.CopyForEntity;
import com.activiti.z_six.mapper.taskAssigneeMapper.CopyForMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class CopyForManager {
    @Autowired
    private CopyForMapper copyForMapper;

    /**
     * 设置抄送人员
     * @param ccParams
     * @return
     */
    public String setCCData(CCParams ccParams){
        try {
            List<CCParams.CCData> ccDataList = ccParams.getData();
            List<CopyForEntity> copyForEntities =ccDataList.stream().map(en->{
                CopyForEntity copyForEntity = new CopyForEntity();
                copyForEntity.setMypk(UUID.randomUUID().toString());
                copyForEntity.setTargetId(en.getId());
                copyForEntity.setCcWay(ccParams.getCcWay());
                copyForEntity.setAutoCCWay(ccParams.getAutoCCWay());
                copyForEntity.setTask_def_key(ccParams.getTask_key());
                copyForEntity.setProc_inst_id(ccParams.getProc_inst_id());
                return copyForEntity;
            }).collect(Collectors.toList());
            copyForMapper.deleteCCDataByTaskKey(ccParams.getTask_key());
            copyForMapper.insertCCData(copyForEntities);
            return "设置成功";
        }
        catch (Exception ex){
            return "设置失败";
        }
    }
}
