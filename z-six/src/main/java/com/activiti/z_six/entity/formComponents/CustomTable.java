package com.activiti.z_six.entity.formComponents;

public class CustomTable {
    /**
     * 外部数据字段
     */
    private String externalDataField;
    /**
     * 是否为空
     */
    private  boolean isNotNull;
    /**
     * 默认值
     */
    private String defaultValue;
    /**
     * 评论
     */
    private String comment;
    /**
     * 建表数据字段
     */
    private String createTableFiledName;
    /**
     * 字段类型
     */
    private String fieldType;
    /**
     * 字段长度
     */
    private  int lengthLimit;
    /**
     * 选择,choose如果为true说明是可添加
     */
    private boolean choose;

    public String getExternalDataField() {
        return externalDataField;
    }

    public void setExternalDataField(String externalDataField) {
        this.externalDataField = externalDataField;
    }

    public boolean isNotNull() {
        return isNotNull;
    }

    public void setNotNull(boolean notNull) {
        isNotNull = notNull;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getCreateTableFiledName() {
        return createTableFiledName;
    }

    public void setCreateTableFiledName(String createTableFiledName) {
        this.createTableFiledName = createTableFiledName;
    }

    public String getFieldType() {
        return fieldType;
    }

    public void setFieldType(String fieldType) {
        this.fieldType = fieldType;
    }

    public int getLengthLimit() {
        return lengthLimit;
    }

    public void setLengthLimit(int lengthLimit) {
        this.lengthLimit = lengthLimit;
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }
}
