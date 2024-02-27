package com.activiti.z_six.entity.formComponents;

import lombok.Data;

@Data
public class FormEntity {
    //主键id
    private String id;
    //表单名称
    private String name;
    //创建时间/修改时间
    private String createTime;
    //所属业务类型
    private String formSort;
    //所属业务类型名称
    private String formSortName;
    //表单事件类型
    private String form_event;
    //表单事件执行方式
    private String form_event_link;
    //表单数据存储主表
    private String form_data_table;
    //表单数据存储从表集合
    private String form_sub_tables;
    //表单模版JSON
    private String form_json;
}
