package com.activiti.z_six.entity.taskAssignee;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssigneeUserEntity {
    private String id;
    private String usertaskid;
    private String username;
    private String name;
    private String ruleName;
    private List<AssigneeUserEntity> assigneeUserList;

}
