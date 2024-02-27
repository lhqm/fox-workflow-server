package com.activiti.z_six.excLog.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
/**
 * <p>
 * 操作异常日志修改入参基类
 * </p>
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Data
@ApiModel(value="ExcLog对象", description="操作异常日志")
public class InDealExcLogDto  implements Serializable{
    @ApiModelProperty(value = "日志ID")
	@NotNull(message = "id不能为空")
	private Long excId;
    /**
     * 版本号
     */
    private Integer version;
}
