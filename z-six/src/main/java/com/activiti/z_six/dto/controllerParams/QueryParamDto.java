package com.activiti.z_six.dto.controllerParams;

import lombok.Data;

/**
 * Created by zzz on 2022/8/19.
 */
@Data
public class QueryParamDto {
    /**
     * 查询id
     */
    private String id;
    /**
     * 关键字
     */
    private String keyword;
    /**
     * 返回值类型
     */
    private String type;//0---带有json个拼装  1---直接返回数据
    /**
     * 当前页码
     */
    private Integer pageno;
    /**
     * 每页显示数量
     */
    private Integer pagesize;
    /**
     * 排序 字段
     */
    private String orderField;
    /**
     * 排序方式，asc,desc
     */
    private String orderType;
}
