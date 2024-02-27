package com.activiti.z_six.entity.dictData;

import com.activiti.z_six.entity.base.BaseEntity;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
public class DictDataEntity extends BaseEntity implements Serializable {
    /**
     * 字典编号
     */
    private String no;
    /**
     * 字典名称
     */
    private String name;
    /**
     * 字典类型（0=静态数据，1=sql数据，2=接口数据）
     */
    private String dataType;
    /**
     * 字典内容
     */
    private String doc;
    /**
     * 字典状态
     */
    private String status;
    /**
     * 备注
     */
    private String remark;
    /**
     * 字典数据
     */
    private List<DictDataEntity> dictDatas=new ArrayList<>();

}
