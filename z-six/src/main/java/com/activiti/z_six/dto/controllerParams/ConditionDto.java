package com.activiti.z_six.dto.controllerParams;

import lombok.Data;

import java.io.Serializable;

@Data
public class ConditionDto implements Serializable {
    /**
     * 方向条件id
     */
    private String expressionId;
    /**
     * 执行类型
     */
    private String type;
    /**
     * 执行的sql
     */
    private String sqlBody;
    /**
     * 执行的接口
     */
    private String webApiBody;
}
