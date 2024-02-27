package com.activiti.z_six.entity.process;

import lombok.Data;

@Data
public class FlowCond{
    /**
     * id
     */
    private String id;
    /**
     * 方向条件id
     */
    private String condition_id;
    /**
     * 执行类型
     */
    private String expression_type;
    /**
     * 执行内容
     */
    private String expression_body;
}
