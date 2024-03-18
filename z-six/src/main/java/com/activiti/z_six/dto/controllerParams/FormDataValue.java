package com.activiti.z_six.dto.controllerParams;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormDataValue {
    /**
     * id
     */
    private String id;
    /**
     * 流程key
     */
    private String processKey;
    /**
     * 表单类型
     */
    private String form_type;
    /**
     * 表单地址
     */
    private String form_url;
    /**
     * 表单json
     */
    private String mapJson;
    /**
     * 表单数据
     */
    private String dataJson;
}

