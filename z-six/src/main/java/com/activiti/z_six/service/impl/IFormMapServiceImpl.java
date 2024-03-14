package com.activiti.z_six.service.impl;

import com.activiti.z_six.dto.controllerParams.FormDataValue;
import com.activiti.z_six.entity.formComponents.*;
import com.activiti.z_six.entity.taskAssignee.GenerWork;
import com.activiti.z_six.mapper.SqlMapper;
import com.activiti.z_six.mapper.formComponentsMapper.FormEntityMapper;
import com.activiti.z_six.mapper.formComponentsMapper.FormSortEntityMapper;
import com.activiti.z_six.mapper.menu.MenuMapper;
import com.activiti.z_six.mapper.taskAssigneeMapper.GenerWorkMapper;
import com.activiti.z_six.service.IFormMapService;
import com.activiti.z_six.service.manager.IFormManager;
import com.activiti.z_six.util.ListUtils;
import com.activiti.z_six.util.StringUtils;
import com.activiti.z_six.util.SystemConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IFormMapServiceImpl implements IFormMapService {
    @Autowired
    private FormEntityMapper formEntityMapper;
    @Autowired
    private FormSortEntityMapper formSortEntityMapper;
    @Autowired
    private GenerWorkMapper generWorkMapper;
    @Autowired
    private IFormManager iFormManager;
    @Autowired
    private SqlMapper sqlMapper;

    /**
     * 获取表单列表
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public HashMap<String,Object> formList(String name,String formSort,Integer page,Integer pagesize){
        HashMap<String,Object> hashMap=new HashMap<>();
        try{
            List<FormEntity> formList = formEntityMapper.formList(name,formSort);
            if(pagesize==0) {
                hashMap.put("list",formList);
                hashMap.put("total",formList.size());
            }
            else{
                Integer startIndex=0;
                if(page>1){
                    startIndex=(page-1)*pagesize;
                }
                List<FormEntity> formListPage=formEntityMapper.formListPage(name,formSort,startIndex,pagesize);
                hashMap.put("list",formListPage);
                hashMap.put("total",formList.size());
            }
            return hashMap;
        }
        catch (Exception ex){
            return hashMap;
        }
    }


    /**
     * 根据sql获取字段列表
     * @return
     */
    @Override
    public List<HashMap<String,Object>> getKeysBySql(String sql){
        JSONObject jsonObject= JSON.parseObject(sql);
        Map<String,Object> map=sqlMapper.single(jsonObject.getString("keySql"),null);
        List<HashMap<String,Object>> hashMapList=new ArrayList<>();
        Set<String> keySet=map.keySet();
        for (String key : keySet) {
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("id",key);
            hashMap.put("label","");
            hashMap.put("compType","");
            hashMapList.add(hashMap);
        }
        return hashMapList;
    }
    /**
     * 获取表单树
     * @return
     */
    @Override
    public List<FormEntity> getFormTree(){
        return formEntityMapper.getFormTree();
    }
    @Override
    public List<FormEntity> getFormTreeByAdd(){
        return formEntityMapper.getFormTreeByAdd();
    }
    @Override
    public FormEntity getFormEntity(String id){
        try{
            FormEntity formEntity=formEntityMapper.formEntity(id);
            return formEntity;
        }
        catch (Exception ex){
            return new FormEntity();
        }
    }
    /**
     * 增加/修改表单
     * @param param
     * @return
     */
    @Override
    public String editForm(JSONObject param){
        JSONObject jsonObject=JSONObject.parseObject(param.getString("viewcode"));
        JSONObject configJson=JSONObject.parseObject(jsonObject.getString("config"));

        FormEntity formEntity=getFormEntity(param.getString("formid"));
        if(formEntity==null) {
            formEntity=new FormEntity();
            formEntity.setId(param.getString("formid"));
            formEntity.setName(configJson.getString("formRef"));
            formEntity.setCreateTime(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
            formEntity.setFormSort(configJson.getString("formSort"));
            formEntity.setForm_data_table(configJson.getString("tableName"));
            formEntity.setForm_json(param.getString("viewcode"));

            iFormManager.addForm(formEntity);
        }
        else{
            formEntity.setForm_json(param.getString("viewcode"));
            iFormManager.updateForm(formEntity);
        }


        return "设置成功";
    }

    /**
     * 删除表单
     * @param id
     * @return
     */
    @Override
    public String deleteForm(String id){
        try{
            formEntityMapper.deleteForm(id);
            return "执行成功";
        }
        catch (Exception ex){
            return ex.getMessage().toString();
        }
    }
    /**
     * 获取表单分类列表
     * @return
     */
    @Override
    public List<FormSortEntity> formSortList(){
        try{
            List<FormSortEntity> formSortList=formSortEntityMapper.formSortList();
            //2、组装树形结构
            List<FormSortEntity> newList = formSortList.stream()
                    .filter(t -> t.getParentNo().equals("0") )
                    .map((menu) -> {
                        menu.setChildren(iFormManager.getChildren(menu,formSortList));
                        return menu;
                    })
                    .collect(Collectors.toList());
            return newList;
        }
        catch (Exception ex){
            return new ArrayList<>();
        }
    }

    @Override
    public FormSortEntity getFormSort(String id){
        try{
            FormSortEntity formSortEntity=formSortEntityMapper.formSort(id);
            return formSortEntity;
        }
        catch (Exception ex){
            return new FormSortEntity();
        }
    }
    @Override
    public String addFormSort(String id,String name,String parentNo){
        try{
            FormSortEntity formSortEntity=new FormSortEntity();
            formSortEntity.setId(id);
            formSortEntity.setName(name);
            formSortEntity.setParentNo(parentNo);
            formSortEntityMapper.addFormSort(formSortEntity);
            return "执行成功";
        }
        catch (Exception ex){
            return ex.getMessage().toString();
        }
    }

    /**
     * 删除表单分类
     * @param id
     * @return
     */
    @Override
    public String deleteFormSort(String id){
        try{
            formSortEntityMapper.deleteFormSort(id);
            return "执行成功";
        }
        catch (Exception ex){
            return ex.getMessage().toString();
        }
    }

    /**
     * 修改表单分类
     * @param id
     * @param name
     * @param parentNo
     * @return
     */
    @Override
    public String updateFormSort(String id,String name,String parentNo){
        try{
            FormSortEntity formSortEntity=new FormSortEntity();
            formSortEntity.setId(id);
            formSortEntity.setName(name);
            formSortEntity.setParentNo(parentNo);
            formSortEntityMapper.updateFormSort(formSortEntity);
            return "执行成功";
        }
        catch (Exception ex){
            return ex.getMessage().toString();
        }
    }
    /**
     * 保存表单数据
     * @param formDataValue
     * @return
     */
    @Override
    public String saveFormDataJson(FormDataValue formDataValue){
        String guid=formDataValue.getId();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        try{
            GenerWork generWork=new GenerWork();
            if(!SystemConfig.IsNullOrEmpty(guid)&&!guid.equals("0")){
                generWork=generWorkMapper.generWork(guid);
                //如果为空，可能是外部表单传过来的businessKey
                if(generWork==null){
                    generWork=new GenerWork();
                    generWork.setId(guid);
                    if(formDataValue.getForm_type().equals("1")){
                        generWork.setMap_json(formDataValue.getForm_url());
                    }
                    else {
                        generWork.setMap_json(formDataValue.getMapJson());
                        generWork.setData_json(formDataValue.getDataJson());
                    }
                    generWork.setStarter(username);
                    generWork.setCreatetime(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
                    generWork.setProcesskey(formDataValue.getProcessKey());
                    generWorkMapper.insertGenerWork(generWork);
                }
                else {
                    generWork.setId(guid);
                    if (formDataValue.getForm_type().equals("1")) {
                        generWork.setMap_json(formDataValue.getForm_url());
                    } else {
                        generWork.setMap_json(formDataValue.getMapJson());
                        generWork.setData_json(formDataValue.getDataJson());
                    }
                    generWork.setProcesskey(formDataValue.getProcessKey());
                    generWorkMapper.updateGenerWork(generWork);
                }
            }
            else {
                guid=UUID.randomUUID().toString();
                generWork.setId(guid);
                if(formDataValue.getForm_type().equals("1")){
                    generWork.setMap_json(formDataValue.getForm_url());
                }
                else {
                    generWork.setMap_json(formDataValue.getMapJson());
                    generWork.setData_json(formDataValue.getDataJson());
                }
                generWork.setProcesskey(formDataValue.getProcessKey());
                generWork.setStarter(username);
                generWork.setCreatetime(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
                generWorkMapper.insertGenerWork(generWork);
            }
            return guid;
        }
        catch (Exception ex){
            return "error";
        }
    }
    @Override
    public GenerWork getGenerWork(String id){
        GenerWork generWork=generWorkMapper.generWork(id);
        return generWork;
    }

    /**
     * 删除应用数据
     * @param frmId
     * @param ids
     * @return
     */
    @Override
    public String deleteGenerWork(String frmId,String ids){
        String msg="";
        try{
            List<String> idList=new ArrayList<>();
           String str= StringUtils.removeEnd(ids,",");
           if(str.contains(",")){
               for(String item:str.split(",")){
                   idList.add(item);
               }
           }
           else
               idList.add(str);
           //删除记录数据
            generWorkMapper.deleteGenerWork(idList);
            if(!SystemConfig.IsNullOrEmpty(frmId)) {
                //删除主表数据
                FormEntity formEntity = formEntityMapper.formEntity(frmId);
                generWorkMapper.deleteFrmData(formEntity.getForm_data_table(), idList);

                //删除子表数据
                List<String> subTables=new ArrayList<>();
                JSONObject jsonObject=JSONObject.parseObject(formEntity.getForm_json());
                JSONArray listJson = JSONArray.parseArray(jsonObject.getString("list"));
                if(ListUtils.isNotNull(listJson)){
                    //如果是子表
                    listJson.stream().map(var -> (JSONObject) var).forEach(object -> {
                        if (object.get("compType").equals("dynamicTable")) {
                            //遍历相关的子
                            String formChildName = object.get("id").toString();//子表的表
                            subTables.add(formChildName);
                        }
                        if(object.getString("compType").equals("row")){
                            JSONArray childList = JSONArray.parseArray(object.getString("columns"));
                            for(int i = 0; i < childList.size(); i++){
                                JSONObject children = childList.getJSONObject(i);
                                JSONArray childrenList=JSONArray.parseArray(children.getString("list"));
                                for(int k=0;k<childrenList.size();k++){
                                    JSONObject rowlist=childrenList.getJSONObject(k);
                                    //保存附件
                                    if (rowlist.get("compType").equals("dynamicTable")) {
                                        //遍历相关的子
                                        String formChildName = rowlist.get("id").toString();//子表的表
                                        subTables.add(formChildName);
                                    }
                                }
                            }
                        }
                    });
                }
                if(subTables.size()>0) {
                    for (String subTable : subTables) {
                        generWorkMapper.deleteFrmSubData(subTable, idList);
                    }
                }
            }
            msg="suess";
            return msg;
        }
        catch (Exception ex){
            return ex.getMessage();
        }
    }
}
