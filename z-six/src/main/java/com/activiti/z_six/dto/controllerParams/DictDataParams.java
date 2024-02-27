package com.activiti.z_six.dto.controllerParams;

import com.activiti.z_six.dto.base.SearchPage;
import lombok.Data;

import java.io.Serializable;

@Data
public class DictDataParams extends SearchPage implements Serializable {
    /**
     * 字典编号
     */
    private String no;
    /**
     * 字典名称
     */
    private String name;

    /**
     * 字典状态
     */
    private String status;
}
