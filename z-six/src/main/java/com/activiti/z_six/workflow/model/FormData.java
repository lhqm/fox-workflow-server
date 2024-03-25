package com.activiti.z_six.workflow.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 对应流程的表单数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormData {
    private String formUrl;
    private String formType;
    private String mapJson;
}
