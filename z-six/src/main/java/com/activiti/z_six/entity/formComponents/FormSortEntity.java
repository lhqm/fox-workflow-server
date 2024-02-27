package com.activiti.z_six.entity.formComponents;


import java.util.List;

public class FormSortEntity {
    /**
     * 表单分类id
     */
    private String id;
    /**
     * 表单分类名称
     */
    private String name;
    /**
     * 上级分类id
     */
    private String parentNo;
    /**
     * 子类集合
     */
    private List<FormSortEntity> children;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentNo() {
        return parentNo;
    }

    public void setParentNo(String parentNo) {
        this.parentNo = parentNo;
    }

    public List<FormSortEntity> getChildren() {
        return children;
    }

    public void setChildren(List<FormSortEntity> children) {
        this.children = children;
    }
}
