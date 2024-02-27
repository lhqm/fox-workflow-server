package com.activiti.z_six.dto.controllerParams;

import lombok.Data;

import java.util.List;

@Data
public class StatAnalysisParams {
    /**
     * 实例key
     */
    private String proceInstKey;
    /**
     * 任务key
     */
    private String actInstKey;
    /**
     * key集合
     */
    private List<processKeys> processKeys;
    @Data
    public class processKeys{
        private String processKey;
    }
}

