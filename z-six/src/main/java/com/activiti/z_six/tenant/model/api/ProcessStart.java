package com.activiti.z_six.tenant.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProcessStart {
    private String processKey;
    private String processName;
    private String businessKey;
}
