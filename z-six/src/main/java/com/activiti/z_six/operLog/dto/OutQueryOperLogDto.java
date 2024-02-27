package com.activiti.z_six.operLog.dto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * <p>
 * 操作日志表新增入参
 * </p>
 * @version: V1.0
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Data
@ApiModel(value="OperLog对象", description="操作日志表")
public class OutQueryOperLogDto{
    @ApiModelProperty(value = "日志ID")
	private String operId;
    @ApiModelProperty(value = "操作模块名称")
	private String operModule;
    @ApiModelProperty(value = "操作类型")
	private String operType;
    @ApiModelProperty(value = "模块描述")
	private String operDesc;
    @ApiModelProperty(value = "创建时间")
    private String createTime;
    @ApiModelProperty(value = "请求参数")
	private String operReqParam;
    @ApiModelProperty(value = "返回数据")
	private String operResParam;
    @ApiModelProperty(value = "操作人ID")
	private String operUserId;
    @ApiModelProperty(value = "操作人名称")
	private String operUserName;
    @ApiModelProperty(value = "操作方法")
	private String operMethod;
    @ApiModelProperty(value = "请求方法")
	private String operUri;
    @ApiModelProperty(value = "请求ip")
	private String operIp;
}
