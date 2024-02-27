package com.activiti.z_six.entity.orgmanagement;

import java.util.List;

public class CompanyEntity {
    /**
     * 公司id
     */
    private Integer id;
    /**
     * 公司名称
     */
    private  String name;
    /**
     * 所属上级
     */
    private Integer parentid;
    /**
     * 公司集合
     */
    private List<CompanyEntity> children;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getParentid() {
        return parentid;
    }

    public void setParentid(Integer parentid) {
        this.parentid = parentid;
    }

    public List<CompanyEntity> getChildren() {
        return children;
    }

    public void setChildren(List<CompanyEntity> children) {
        this.children = children;
    }
}
