package com.activiti.z_six.excLog.entity;

import com.activiti.z_six.operLog.entity.base.BaseAllLongByEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 * 操作异常日志
 * </p>
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@ApiModel(value="ExcLog对象", description="操作异常日志")
public class ExcLogEntity extends BaseAllLongByEntity {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "日志ID")
    private String excId;

    @ApiModelProperty(value = "请求参数")
    private String excReqParam;

    @ApiModelProperty(value = "异常名称")
    private String excName;

    @ApiModelProperty(value = "异常信息")
    private String excMessage;

    @ApiModelProperty(value = "操作人ID")
    private String operUserId;

    @ApiModelProperty(value = "操作人名称")
    private String operUserName;

    @ApiModelProperty(value = "操作类方法")
    private String operMethod;

    @ApiModelProperty(value = "请求方式")
    private String operReqMethod;

    @ApiModelProperty(value = "请求方法")
    private String operUri;

    @ApiModelProperty(value = "请求ip")
    private String operIp;


}
