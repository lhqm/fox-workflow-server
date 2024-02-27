package com.activiti.z_six.dto.base;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchPage implements Serializable {
    /**
     * 页数
     */
    private Integer page;
    /**
     * 每页显示条数
     */
    private Integer pageSize;
}
