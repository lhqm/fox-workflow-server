package com.activiti.z_six.dto.controllerParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;

@Data
@ApiModel(value="OperLog查询参数对象", description="操作日志表")
public class OperLogParams {
    @ApiModelProperty(value = "操作模块ID")
    private String operId;
    @ApiModelProperty(value = "操作模块名称")
    private String operModule;
    @ApiModelProperty(value = "创建开始时间")
    private String startTime;
    @ApiModelProperty(value = "创建结束时间")
    private String endTime;
    @ApiModelProperty(value = "操作类型")
    private String operType;
    @ApiModelProperty(value = "模块描述")
    private String operDesc;
    @ApiModelProperty(value = "查询页码")
    private Integer pageNum;
    @ApiModelProperty(value = "查询size")
    private Integer pageSize;
}
