package com.activiti.z_six.entity.base;

import lombok.Data;

import java.io.Serializable;

@Data
public class BaseEntity implements Serializable {
    /** 创建者 */
    private String createBy;
    /** 创建时间 */
    private String createTime;

    /** 更新者 */
    private String updateBy;

    /** 更新时间 */
    private String updateTime;
}
