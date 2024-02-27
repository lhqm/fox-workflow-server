package com.activiti.z_six.mapper.taskAssigneeMapper;

import com.activiti.z_six.entity.taskAssignee.OvTaskEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface OvTaskEntityMapper {
    /**
     * 根据task获取任务实例
     * @param id
     * @return
     */
    OvTaskEntity ovTaskEntity(String id);

    /**
     * 根据执行人与流程定义id获取
     * @param taskEntity
     * @return
     */
    List<OvTaskEntity> ovTaskEntityList(OvTaskEntity taskEntity);

    /**
     * 根据流程实例ID获取
     * @param processInsid
     * @return
     */

    List<OvTaskEntity> ovTaskEntityByProcessInsId(String processInsid);

    /**
     * 移交、加签的任务状态
     * @param taskEntity
     * @return
     */
    int setTaskStatus(OvTaskEntity taskEntity);

    /**
     * 修改驳回任务状态
     * @param taskEntity
     * @return
     */
    int setTaskStatusToReturn(OvTaskEntity taskEntity);

    /**
     * 修改手动结束或拒绝的时间
     * @param end_time
     * @param proc_inst_id
     * @return
     */
    int setPrcoInstStatus(@Param("end_time") String end_time,
                          @Param("proc_inst_id") String proc_inst_id);

    /**
     * 删除流程实例
     * @param delete_reason
     * @param proc_inst_id
     * @return
     */
    int setPrcoInstReason(@Param("delete_reason") String delete_reason,
                          @Param("proc_inst_id") String proc_inst_id);
    /**
     * 删除task，用于手动结束、拒绝
     * @param taskEntity
     * @return
     */
    int delete(OvTaskEntity taskEntity);
}
