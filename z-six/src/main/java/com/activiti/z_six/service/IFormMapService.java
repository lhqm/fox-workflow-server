package com.activiti.z_six.service;

import com.activiti.z_six.dto.controllerParams.FormDataValue;
import com.activiti.z_six.entity.formComponents.FormEntity;
import com.activiti.z_six.entity.formComponents.FormSortEntity;
import com.activiti.z_six.entity.taskAssignee.GenerWork;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.List;

public interface IFormMapService {
    /**
     * 获取表单列表
     * @param name
     * @param formSort
     * @param page
     * @param pagesize
     * @return
     */
    HashMap<String,Object> formList(String name,String formSort,Integer page,Integer pagesize);


    /**
     * 获取sql字段
     * @return
     */
    List<HashMap<String,Object>> getKeysBySql(String sql);

    /**
     * 获取表单树
     * @return
     */
    List<FormEntity> getFormTree();

    /**
     * 增加时使用
     * @return
     */
    List<FormEntity> getFormTreeByAdd();

    /**
     * 获取某个表单信息
     * @param id
     * @return
     */
    FormEntity getFormEntity(String id);
    /**
     * 增加/修改表单
     * @param param
     * @return
     */
    String editForm(JSONObject param);
    /**
     * 删除表单
     * @param id
     * @return
     */
    String deleteForm(String id);

    /**
     * 获取表单分类列表
     * @return
     */
    List<FormSortEntity> formSortList();

    /**
     * 获取某一个分类信息
     * @param id
     * @return
     */
    FormSortEntity getFormSort(String id);

    /**
     * 增加表单分类
     * @param id
     * @param name
     * @param parentNo
     * @return
     */
    String addFormSort(String id,String name,String parentNo);

    /**
     * 删除表单分类
     * @param id
     * @return
     */
    String deleteFormSort(String id);

    /**
     * 修改表单分类
     * @param id
     * @param name
     * @param parentNo
     * @return
     */
    String updateFormSort(String id,String name,String parentNo);

    /**
     * 保存表单数据
     * @param formDataValue
     * @return
     */
    String saveFormDataJson(FormDataValue formDataValue);

    /**
     * 获取表单数据
     * @param id
     * @return
     */
    GenerWork getGenerWork(String id);

    /**
     * 删除表单数据
     * @param frmId
     * @return
     */
    String deleteGenerWork(String frmId,String ids);
}
