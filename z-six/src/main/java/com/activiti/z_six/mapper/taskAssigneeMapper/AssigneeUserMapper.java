package com.activiti.z_six.mapper.taskAssigneeMapper;

import com.activiti.z_six.entity.taskAssignee.AssigneeDSREntity;
import com.activiti.z_six.entity.taskAssignee.AssigneeUserEntity;
import com.activiti.z_six.entity.taskAssignee.ManuallySelectEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AssigneeUserMapper {
    /**
    根据节点获取任务处理人
     */
    List<AssigneeUserEntity> getAssigneeUserWithTaskKey(@Param("usertaskid") String usertaskid,
                                                        @Param("ruleName") String ruleName);
    List<AssigneeDSREntity> getAssigneeDSR(String userTaskId);
    List<ManuallySelectEntity> getManuallySelectDSR(@Param("task_def_id") String task_def_id,
                                                    @Param("proc_inst_id")String proc_inst_id);
    /**
    设置任务处理人
     */
    int setAssigneeUser(List<AssigneeUserEntity> assigneeUserEntityList);
    int setAssigneeByDeptAndStionOrRole(List<AssigneeDSREntity> assigneeDSREntityList);
    int setAssigneeByManuallySelect(List<ManuallySelectEntity> manuallySelectEntities);
    /**
    更新任务处理人
     */
    int deleteAssigneeUser(AssigneeUserEntity assigneeUserEntity);
    int deleteAssigneeManuallySelect(@Param("task_def_id") String task_def_id,
                                     @Param("proc_inst_id")String proc_inst_id);

    /**
     * 删除任务处理人
     * @param taskid
     * @return
     */
    int deleteAssigneeUserByTaskid(String taskid);
}
