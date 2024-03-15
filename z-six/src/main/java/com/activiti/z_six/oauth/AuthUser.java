package com.activiti.z_six.oauth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author 离狐千慕
 * @version 1.0
 * @date 2023/12/12 9:20
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthUser {
    private Long id;
    private Long localId;
    private Integer clientId;
    private String platform;
    private Long binding;
    private String bindingCode;
    private String account;
    private String password;
    private String avatar;
    private String nickName;
    private String email;
    private Integer gender;
    private Long phone;
    private Integer active;
    private Integer del;
}
