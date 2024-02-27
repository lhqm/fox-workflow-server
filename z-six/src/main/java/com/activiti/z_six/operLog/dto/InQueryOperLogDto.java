package com.activiti.z_six.operLog.dto;

import com.activiti.z_six.dto.BaseInPageDto;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 操作日志表新增入参
 * </p>
 *
 * @author zn
 * @date 2021-03-17
 * @version: V1.0
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Data
@ApiModel(value="OperLog对象", description="操作日志表")
public class InQueryOperLogDto extends BaseInPageDto implements Serializable{

    @ApiModelProperty(value = "日志ID")
	private Integer operId;     

    @ApiModelProperty(value = "操作模块名称")
	private String operModule;     

    @ApiModelProperty(value = "操作类型")
	private String operType;     

    @ApiModelProperty(value = "模块描述")
	private String operDesc;     

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
