package com.activiti.z_six.entity.formComponents;

import java.util.List;

public class CreateTable {
    /**
     * 表名
     */
    private String tableName;
    /**
     * 列集合
     */
    private List<CustomTable> customTables;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public List<CustomTable> getCustomTables() {
        return customTables;
    }

    public void setCustomTables(List<CustomTable> customTables) {
        this.customTables = customTables;
    }
}
