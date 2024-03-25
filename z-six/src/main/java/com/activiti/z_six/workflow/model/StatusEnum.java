package com.activiti.z_six.workflow.model;

/**
 * 状态列举
 */
public enum StatusEnum {
    RUNNING("RUNNING","正常运行"),
    COUNTERSIGN("COUNTERSIGN","加签"),
    ROLLBACK("ROLLBACK","回滚"),
    HUMAN_MODEL_SELECTION("HUMAN_MODEL_SELECTION","人工选择路径"),
    INTERRUPT("INTERRUPT","手动干预结束"),
    TRANSFER("TRANSFER","转办或移交"),
    REFUSE("REFUSE","拒绝"),
    FINISH("FINISH","已正常结束"),
    STARTING("STARTING","开始"),
    ;
    private final String statusCode;
    private final String statusName;

    StatusEnum(String statusCode, String statusName){
        this.statusCode=statusCode;
        this.statusName=statusName;
    }

    public String getStatusName(){
        return statusName;
    }
    public String getStatusCode(){
        return statusCode;
    }
    public static StatusEnum getStatusName(String statusCode){
        for(StatusEnum statusEnum:StatusEnum.values()){
            if(statusEnum.getStatusCode().equals(statusCode)){
                return statusEnum;
            }
        }
        return null;
    }

}
