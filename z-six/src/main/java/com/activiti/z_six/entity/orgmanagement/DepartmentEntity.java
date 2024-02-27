package com.activiti.z_six.entity.orgmanagement;

import java.util.List;

public class DepartmentEntity {
    /**
     * 部门ID
     */
    private Integer id;
    /**
     * 部门名称
     */
    private String name;
    /**
     * 上级部门
     */
    private Integer parentid;
    /**
     * 部门负责人编号
     */
    private String managername;
    /**
     * 部门负责人名称
     */
    private String manager;
    /**
     * 部门分管领导编号
     */
    private String leadername;
    /**
     * 部门分管领导名称
     */
    private String leader;
    /**
     * 所属公司编号
     */
    private Integer companyid;
    /**
     * 所属公司名称
     */
    private String companyname;
    /**
     * 子部门集合
     */
    private List<DepartmentEntity> children;

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

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    public String getLeader() {
        return leader;
    }

    public void setLeader(String leader) {
        this.leader = leader;
    }

    public String getManagername() {
        return managername;
    }

    public void setManagername(String managername) {
        this.managername = managername;
    }

    public String getLeadername() {
        return leadername;
    }

    public void setLeadername(String leadername) {
        this.leadername = leadername;
    }

    public void setCompanyid(Integer companyid) {
        this.companyid = companyid;
    }

    public String getCompanyname() {
        return companyname;
    }

    public void setCompanyname(String companyname) {
        this.companyname = companyname;
    }

    public Integer getCompanyid() {
        return companyid;
    }

    public List<DepartmentEntity> getChildren() {
        return children;
    }

    public void setChildren(List<DepartmentEntity> children) {
        this.children = children;
    }
}
