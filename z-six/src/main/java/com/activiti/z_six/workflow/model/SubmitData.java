package com.activiti.z_six.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 提交表单数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitData {
    private String processKey;
    private String dataJson;
}
