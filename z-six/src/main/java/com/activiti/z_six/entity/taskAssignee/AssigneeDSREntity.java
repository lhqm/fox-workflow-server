package com.activiti.z_six.entity.taskAssignee;

import java.util.List;

public class AssigneeDSREntity {
    private String id;
    private String usertaskid;
    private String targetId;
    private String dsr;
    private List<AssigneeDSREntity> assigneeDSRList;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsertaskid() {
        return usertaskid;
    }

    public void setUsertaskid(String usertaskid) {
        this.usertaskid = usertaskid;
    }

    public String getTargetId() {
        return targetId;
    }

    public void setTargetId(String targetId) {
        this.targetId = targetId;
    }

    public String getDsr() {
        return dsr;
    }

    public void setDsr(String dsr) {
        this.dsr = dsr;
    }

    public List<AssigneeDSREntity> getAssigneeDSRList() {
        return assigneeDSRList;
    }

    public void setAssigneeDSRList(List<AssigneeDSREntity> assigneeDSRList) {
        this.assigneeDSRList = assigneeDSRList;
    }
}
