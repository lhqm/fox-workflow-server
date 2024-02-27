package com.activiti.z_six.service;

import com.activiti.z_six.dto.controllerParams.FormDataValue;
import com.activiti.z_six.dto.controllerParams.QueryParamDto;
import com.activiti.z_six.dto.OutTotalDto;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;


/**
 * Created by zzz on 2022/8/19.
 */
public interface IFormValueService {

    /**
     * 根据表单id获取数据
     * @param paramDto
     * @return
     */
    OutTotalDto getFormData(QueryParamDto paramDto);

    OutTotalDto selectFormData(String formTable, QueryParamDto paramDto, Integer startIndex , Integer maxIndex);

    /**
     * 保存表单数据
     * @param formDataValue
     * @return
     * @throws Exception
     */
    String saveFormValueByJson(FormDataValue formDataValue)throws Exception;

    /**
     * 查询表单应用数据
     * @param param
     * @return
     */
    HashMap<String,Object> getApplicationData(JSONObject param);
}
