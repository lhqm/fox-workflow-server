package com.activiti.z_six.mapper.taskAssigneeMapper;

import com.activiti.z_six.dto.controllerParams.CCParams;
import com.activiti.z_six.entity.orgmanagement.PositionEntity;
import com.activiti.z_six.entity.orgmanagement.RolesEntity;
import com.activiti.z_six.entity.orgmanagement.UserEntity;
import com.activiti.z_six.entity.taskAssignee.CopyForEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;

@Mapper
@Repository
public interface CopyForMapper {
    /**
     *获取设置的抄送人员的信息
     * @return
     */
    List<UserEntity> selectByUserData(CCParams ccParams);

    /**
     * 根据角色id查询知会人员
     * @param hashMapList
     * @return
     */
    List<UserEntity> selectUsersByRoles(List<HashMap<String,Object>> hashMapList);

    /**
     * 根据岗位id查询知会人员
     * @param hashMapList
     * @return
     */
    List<UserEntity> selectUsersByPositions(List<HashMap<String,Object>> hashMapList);

    /**
     * 获取设置的抄送角色的信息
     * @param ccParams
     * @return
     */
    List<RolesEntity> selectByRoleData(CCParams ccParams);

    /**
     * 获取设置的抄送的岗位信息
     * @param ccParams
     * @return
     */
    List<PositionEntity> selectByStationData(CCParams ccParams);
    /**
     * 配置抄送
     * @param copyForEntities
     * @return
     */
    int insertCCData(List<CopyForEntity> copyForEntities);

    /**
     * 删除节点抄送配置
     * @param task_def_key
     * @return
     */
    int deleteCCDataByTaskKey(@Param("task_def_key") String task_def_key);
    /**
     * 删除节点抄送配置
     * @param proc_inst_id
     * @return
     */
    int deleteCCDataByProcInstId(@Param("proc_inst_id") String proc_inst_id);
}
