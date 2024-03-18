package com.activiti.z_six.tenant.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FormCommit {
    private String processKey;
    private String dataJson;
    private String taskId;
}

