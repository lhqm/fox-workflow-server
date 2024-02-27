package com.activiti.z_six.entity.orgmanagement;

import java.util.List;

public class UserEntity {
    /**
     * 主键，用户id
     */
    private Long id;
    /**
     * 登录名
     */
    private String name;
    /**
     * 用户名称
     */
    private String username;
    /**
     * 登录密码
     */
    private String password;
    /**
     * 部门编号
     */
    private Integer departid;
    /**
     * 部门名称
     */
    private String departname;
    /**
     * 性别，0=女，1=男
     */
    private Integer sex;
    /**
     * 手机号
     */
    private String tel;
    /**
     * 邮箱
     */
    private String email;
    /**
     * 状态 0=false 1=true
     */
    private Integer state;
    /**
     * guuid
     */
    private String guuid;
    /**
     * 角色,activiti7强制要求，此字段主要用于新增用户
     */
    private String roles;

    private List<UserEntity> children;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getDepartid() {
        return departid;
    }

    public void setDepartid(Integer departid) {
        this.departid = departid;
    }

    public String getDepartname() {
        return departname;
    }

    public void setDepartname(String departname) {
        this.departname = departname;
    }

    public String getGuuid() {
        return guuid;
    }

    public void setGuuid(String guuid) {
        this.guuid = guuid;
    }

    public String getRoles() {
        return roles;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public Integer getSex() {
        return sex;
    }

    public void setSex(Integer sex) {
        this.sex = sex;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public List<UserEntity> getChildren() {
        return children;
    }

    public void setChildren(List<UserEntity> children) {
        this.children = children;
    }
}
