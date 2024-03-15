package com.activiti.z_six.dto.controllerParams;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserParams {
    /**
     * id
     */
    private Integer id;
    /**
     * 登录帐号
     */
    private String username;
    /**
     * 用户名称
     */
    private String name;
    /**
     * 性别
     */
    private Integer sex;
    /**
     * 电子邮箱
     */
    private String email;
    /**
     * 部门编号
     */
    private Integer departid;
    /**
     * 角色
     */
    private String[] roles;
    /**
     * 岗位
     */
    private String[] positions;
    /**
     * 手机号
     */
    private String tel;
    /**
     * 状态
     */
    private Integer state;
    private String guuid;

}
