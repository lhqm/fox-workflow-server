package com.activiti.z_six.entity.process;

import java.util.List;

public class FlowSort {
    //主键
    private Long id;
    //流程类型名称
    private String name;
    //父节点ID
    private Long parentid;
    //子类集合
    private List<FlowSort> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getParentId() {
        return parentid;
    }

    public List<FlowSort> getChildren() {
        return children;
    }

    public void setChildren(List<FlowSort> children) {
        this.children = children;
    }

    public void setParentId(Long parentId) {
        this.parentid = parentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
