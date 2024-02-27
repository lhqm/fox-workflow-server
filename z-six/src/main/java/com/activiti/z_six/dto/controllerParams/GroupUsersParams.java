package com.activiti.z_six.dto.controllerParams;

import com.activiti.z_six.entity.orgmanagement.UserEntity;
import lombok.Data;

import java.util.List;

@Data
public class GroupUsersParams {
    /**
     * 分组id
     */
    private String groupid;
    /**
     * 分组人员集合
     */
    private List<UserEntity> userlist;
    private List<UserEntity> userDatalist;
}
