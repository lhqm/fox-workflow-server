package com.activiti.z_six.entity;

import lombok.Data;

/**
 * Created by jeeseli on 2022/8/26.
 */
@Data
public class Attachment {
    /**
     * 附件id
     */
    private String id;
    /**
     * 表单数据id
     */
    private String genworkId;
    /**
     * 地址
     */
    private String path;
    /**
     * 附件名称
     */
    private String name;
    /**
     * 附件状态
     */
    private String delFlag;
    /**
     * 模块名称
     */
    private String moduleName;
    /**
     * 上传日期
     */
    private String rdt;
}
