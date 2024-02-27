package com.activiti.z_six.operLog.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * <p>
 * 操作日志表修改入参基类
 * </p>
 *
 * @author zn
 * @date 2021-03-17
 * @version: V1.0
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Data
@ApiModel(value="OperLog对象", description="操作日志表")
public class InDealOperLogDto  implements Serializable{
    @ApiModelProperty(value = "日志ID")
	@NotNull(message = "id不能为空")
	private Integer operId;
    /**
     * 版本号
     */
    private Integer version;
}
