package com.activiti.z_six.mapper.taskAssigneeMapper;

import com.activiti.z_six.entity.taskAssignee.OvProcessInstance;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface OvProcessInstanceMapper {
    /*
    根据id获取processins
     */
    OvProcessInstance getOvProcessIns(String id);

    /**
     * 根据key获取最新版本的processins
     * @param key
     * @return
     */
    OvProcessInstance getProcessInsLastVersion(String key);
    /**
    *根据deploymentid获取processins
     */
    OvProcessInstance getProcessInsByDeployId(String deploymentid);
}
