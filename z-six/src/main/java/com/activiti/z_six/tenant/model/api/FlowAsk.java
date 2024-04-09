package com.activiti.z_six.tenant.model.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FlowAsk {
    private String ackCode;
    private String tenant;
}
