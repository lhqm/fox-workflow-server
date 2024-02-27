package com.activiti.z_six.entity.taskAssignee;

import lombok.Data;

import java.util.List;

@Data
public class AssigneeUserEntity {
    private String id;
    private String usertaskid;
    private String username;
    private String name;
    private String ruleName;
    private List<AssigneeUserEntity> assigneeUserList;

}
