package com.activiti.z_six.dto.formApplication;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ColsDto {
    /**
     * 字段id
     */
    private String id;
    /**
     * 字段标签
     */
    private String label;
    /**
     * 字段类型
     */
    private String compType;
    /**
     * 选择值
     */
    private List<OptiongsDto> options;
    /**
     * 字段类型
     */
    private String dataType;
    /**
     * 动态数据地址
     */
    private String action;

    /**
     * json转对象
     * @param jsonArray
     * @return
     */
    public List<ColsDto> JsonArrayToClass(JSONArray jsonArray){
        List<ColsDto> colsDtos=new ArrayList<>();
        for(int i = 0; i < jsonArray.size(); i++){
            ColsDto colsDto=new ColsDto();
            List<OptiongsDto> options=new ArrayList<>();
            JSONObject jsonObject=jsonArray.getJSONObject(i);
            //获取List<OptiongsDto>
            JSONArray optionArray= jsonObject.getJSONArray("options");
            for(int k = 0; k < optionArray.size(); k++){
                JSONObject optionObject=optionArray.getJSONObject(k);
                OptiongsDto optiongsDto=new OptiongsDto();
                optiongsDto.setId(optionObject.getString("id"));
                optiongsDto.setLabel(optionObject.getString("label"));
                options.add(optiongsDto);
            }
            //设置id
            colsDto.setId(jsonObject.getString("id"));
            //设置label
            colsDto.setLabel(jsonObject.getString("label"));
            //设置compType
            colsDto.setCompType(jsonObject.getString("compType"));
            //设置action
            colsDto.setAction(jsonObject.getString("action"));
            //设置dataType
            colsDto.setDataType(jsonObject.getString("dataType"));
            //设置options
            colsDto.setOptions(options);
            colsDtos.add(colsDto);
        }
        return colsDtos;
    }
}
