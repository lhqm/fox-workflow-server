package com.activiti.z_six.workflow.model;

public enum FormType {
    EMBEDDED("0","内嵌表单"),
    EXTERNAL("1","外部表单");

    private final String value;
    private final String type;

    FormType(String value,String type) {
        this.value = value;
        this.type = type;
    }

    public String getValue() {
        return value;
    }
    public String getType() {
        return type;
    }
    public static FormType getFormType(String value) {
        for (FormType formType : FormType.values()) {
            if (formType.getValue().equals(value)) {
                return formType;
            }
        }
        return null;
    }
}
