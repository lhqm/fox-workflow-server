package com.activiti.z_six.service.impl;

import com.activiti.z_six.common.Constants;
import com.activiti.z_six.dto.controllerParams.FormDataValue;
import com.activiti.z_six.dto.controllerParams.QueryParamDto;
import com.activiti.z_six.base.sql.SqlFragment;
import com.activiti.z_six.dto.OutTotalDto;
import com.activiti.z_six.dto.formApplication.ColsDto;
import com.activiti.z_six.entity.Attachment;
import com.activiti.z_six.mapper.AttachmentMapper;
import com.activiti.z_six.mapper.SqlMapper;
import com.activiti.z_six.entity.formComponents.FormEntity;
import com.activiti.z_six.mapper.formComponentsMapper.FormEntityMapper;
import com.activiti.z_six.service.IFormMapService;
import com.activiti.z_six.service.IFormValueService;
import com.activiti.z_six.service.manager.CommManager;
import com.activiti.z_six.service.manager.IFormManager;
import com.activiti.z_six.util.ListUtils;
import com.activiti.z_six.util.ResultRes;
import com.activiti.z_six.util.SystemConfig;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.activiti.z_six.base.sql.SqlFragment.*;

@Service
@Slf4j
public class IFormValueServiceImpl implements IFormValueService {

    @Autowired
    private FormEntityMapper formEntityMapper;

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Autowired
    private SqlMapper sqlMapper;
    @Autowired
    private IFormManager iFormManager;
    @Autowired
    private IFormMapService formMapService;

    @Override
    public OutTotalDto getFormData(QueryParamDto paramDto) {
        OutTotalDto dto =new OutTotalDto();
        FormEntity formEntity = formEntityMapper.formEntity(paramDto.getId());
        //获取到具体的表单名称和对应的json
        if (null != formEntity) {
            if (org.springframework.util.StringUtils.isEmpty(paramDto.getType()) || Constants.ONE.toString().equals(paramDto.getType())) {
                int startIndex = (paramDto.getPageno() - 1) * paramDto.getPagesize();
                int maxIndex = startIndex + paramDto.getPagesize();
                dto=selectFormData(formEntity.getForm_data_table(), paramDto, startIndex, paramDto.getPagesize());
                return dto;
            }
        }
        return dto;
    }
    /**
     * 获取实际表的数据
     * @param formTable
     * @throws Exception
     */
    @Override
    public OutTotalDto selectFormData(String formTable, QueryParamDto paramDto, Integer startIndex , Integer maxIndex) {
        OutTotalDto dto =new OutTotalDto();
        StringBuilder sql = new StringBuilder().append("select a.* ,g.starter,g.proce_inst_id FROM ").append(formTable).append(" a  left join ac_sl_generwork g  on a.id =g.id  where 1=1");
        StringBuilder sqlCount = new StringBuilder().append("select count(*) FROM ").append(formTable).append(" a  left join ac_sl_generwork g  on a.id =g.id where 1=1  ");
        StringBuilder sqlKeyStr =new StringBuilder();
        StringBuilder sqlKey = new StringBuilder();
        if(StringUtils.isNotEmpty(paramDto.getKeyword())){
         //临时动态获取表结构
            List<Map<String, Object>> descList = sqlMapper.list("desc "+formTable,null);
            if(ListUtils.isNotNull(descList)){
                sqlKey.append(" and (");
                descList.stream().forEach(vv->{
                    if(!"id".equals(vv.get("Field"))){
                        sqlKey.append(vv.get("Field")).append(" like '%").append(paramDto.getKeyword()).append("%' or ");
                    }
                });
                sqlKeyStr.append(sqlKey.substring(0, sqlKey.toString().lastIndexOf("or")));
                sqlKeyStr.append(" )");
            }
            sql.append(sqlKeyStr);
            sqlCount.append(sqlKeyStr);

        }
        Long count = sqlMapper.count(sqlCount.toString(),null);

        if(StringUtils.isNotBlank(paramDto.getOrderField())){
            if(null != paramDto.getOrderType()){
                sql.append(" order by a.").append(paramDto.getOrderField()).append(" ").append(paramDto.getOrderType());
            }else{
                sql.append(" order by a.").append(paramDto.getOrderField());
            }
        }

        if(null !=startIndex && null != maxIndex){
            sql.append(" limit ").append(startIndex).append(",").append(maxIndex);
        }

        dto.setTotal(count);
        dto.setList( sqlMapper.list(sql.toString(),null));
        return dto;
    }

    /**
     * 保存表单数据
     * @param formDataValue
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String saveFormValueByJson(FormDataValue formDataValue) throws Exception {
        String genId=formMapService.saveFormDataJson(formDataValue);
        if(genId=="error"){
            return "error";
        }
        if( StringUtils.isNotEmpty(formDataValue.getMapJson())){
            JSONObject jsonObject=JSONObject.parseObject(formDataValue.getMapJson());
            JSONObject dataJsonObj=JSONObject.parseObject(formDataValue.getDataJson());
            JSONObject configJson=JSONObject.parseObject(jsonObject.getString("config"));
            String form_data_table = configJson.getString("tableName");
            List<Attachment> attachmentList=new ArrayList<>();

            //没有找到物理存储表，返回
            if(StringUtils.isEmpty(form_data_table))
                return "error";
            //判断请求参数和表单id是否同一
            /**
             * 判断该数据是否存在 如果存在则进行更新操作 如果不存在则保存
             */
            String sqlexit = "select * from "+form_data_table+" where id= '"+genId+"'";
            Map<String, Object> maps = sqlMapper.single(sqlexit,null);
            if(null!= maps){

                StringBuilder fieldSqlStr = new StringBuilder(), valueSqlStr = new StringBuilder();

                String sql =new String();
                AtomicReference<String> childQql = new AtomicReference<>(new String());
                AtomicReference<String> childDelQql = new AtomicReference<>(new String());
                List<String> sqlList =new ArrayList<>();
                List<String> sqlDelList =new ArrayList<>();
                JSONArray listJson = JSONArray.parseArray(jsonObject.getString("list"));
                if(ListUtils.isNotNull(listJson)){
                    //如果是子
                    listJson.stream().map(var -> (JSONObject) var).forEach(object -> {
                        if (object.get("compType").equals("dynamicTable")) {
                            //遍历相关的子
                            String formChildName = object.get("id").toString();//子表的表
                            JSONArray childList = JSONArray.parseArray(dataJsonObj.getString(formChildName));
                            if(ListUtils.isNotNull(childList)){
                                /**
                                 * 先删后插
                                 */
                                childDelQql.set(" delete from "+formChildName+" where fk_id='"+genId+"'");
                                log.info("更新时删除操作子表InsertSQL======"+childDelQql);

                                childList.stream().forEach(var->{
                                    JSONObject obj = (JSONObject) var;
                                    StringBuilder fieldChildSqlStr = new StringBuilder(), valueChildSqlStr = new StringBuilder();
                                    //子表主键
                                    String dtlId=String.valueOf(RandomUtils.nextLong());
                                    fieldChildSqlStr.append("`id`,");
                                    fieldChildSqlStr.append("`fk_id`,");
                                    valueChildSqlStr.append("\"").append(dtlId).append("\",");
                                    valueChildSqlStr.append("\"").append(genId).append("\",");
                                    for (Map.Entry<String, Object> entry : obj.entrySet()) {
                                        if(!entry.getKey().equals("index")) {
                                            fieldChildSqlStr.append("`").append(entry.getKey()).append("`,");
                                            valueChildSqlStr.append("\"").append(entry.getValue()).append("\",");
                                        }
                                    }
                                    fieldChildSqlStr.append("`").append("rdt").append("`,");
                                    valueChildSqlStr.append("\"").append(DateTime.now().toString("yyyy-MM-dd HH:mm:ss")).append("\",");
                                    fieldChildSqlStr.deleteCharAt(fieldChildSqlStr.length() - 1);
                                    valueChildSqlStr.deleteCharAt(valueChildSqlStr.length() - 1);
                                    childQql.set("insert into " + formChildName +
                                            "(" + fieldChildSqlStr + ") " +
                                            "values(" + valueChildSqlStr + ")");
                                    log.info("插入操作子表InsertSQL======"+childQql);
                                    sqlList.add(String.valueOf(childQql));
                                });
                                sqlDelList.add(String.valueOf(childDelQql));
                            }
                            return;
                        }
                        if (object.getString("compType").equals("text")
                                || object.getString("compType").equals("barCode")
                                || object.getString("compType").equals("alert")
                                || object.getString("compType").equals("link")
                                || object.getString("compType").equals("button")
                                || object.getString("compType").equals("colorPicker")
                                || object.getString("compType").equals("cascader")
                                || object.getString("compType").equals("divider")
                                ) {
                            return;
                        }
                        //保存附件
                        if(object.getString("compType").equals("upload")){
                            if(!SystemConfig.IsNullOrEmpty(object.get("value"))){
                                JSONArray fileJson = JSONArray.parseArray(object.getString("value"));
                                //先删除改组件下的附件数据
                                attachmentMapper.deleteAttByGenerWork(genId,object.getString("id"));
                                fileJson.stream().map(var -> (JSONObject) var).forEach(file -> {
                                    Attachment attachment=new Attachment();
                                    attachment.setGenworkId(genId);
                                    attachment.setDelFlag("0");
                                    attachment.setName(file.getString("fileName"));
                                    attachment.setPath(file.getString("filePath"));
                                    attachment.setModuleName(object.getString("id"));
                                    attachment.setRdt(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
                                    attachment.setId(UUID.randomUUID().toString());
                                    attachmentList.add(attachment);
                                });
                            }
                            return;
                        }
                        if(object.getString("compType").equals("row")){
                            JSONArray childList = JSONArray.parseArray(object.getString("columns"));
                            for(int i = 0; i < childList.size(); i++){
                                JSONObject children = childList.getJSONObject(i);
                                JSONArray childrenList=JSONArray.parseArray(children.getString("list"));
                                for(int k=0;k<childrenList.size();k++){
                                    JSONObject rowlist=childrenList.getJSONObject(k);
                                    if (rowlist.getString("compType").equals("text")
                                            || rowlist.getString("compType").equals("barCode")
                                            || rowlist.getString("compType").equals("alert")
                                            || rowlist.getString("compType").equals("link")
                                            || rowlist.getString("compType").equals("button")
                                            || rowlist.getString("compType").equals("colorPicker")
                                            || rowlist.getString("compType").equals("cascader")
                                            || rowlist.getString("compType").equals("divider")
                                            ) {
                                        continue;
                                    }
                                    //保存附件
                                    if(rowlist.getString("compType").equals("upload")){
                                        if(!SystemConfig.IsNullOrEmpty(rowlist.get("value"))){
                                            JSONArray fileJson = JSONArray.parseArray(rowlist.getString("value"));
                                            //先删除改组件下的附件数据
                                            attachmentMapper.deleteAttByGenerWork(genId,rowlist.getString("id"));
                                            fileJson.stream().map(var -> (JSONObject) var).forEach(file -> {
                                                Attachment attachment=new Attachment();
                                                attachment.setGenworkId(genId);
                                                attachment.setDelFlag("0");
                                                attachment.setName(file.getString("fileName"));
                                                attachment.setPath(file.getString("filePath"));
                                                attachment.setRdt(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
                                                attachment.setModuleName(rowlist.getString("id"));
                                                attachment.setId(UUID.randomUUID().toString());
                                                attachmentList.add(attachment);
                                            });
                                        }
                                        continue;
                                    }
                                    if (rowlist.get("compType").equals("dynamicTable")) {
                                        //遍历相关的子
                                        String formChildName = rowlist.get("id").toString();//子表的表
                                        JSONArray childListRow = JSONArray.parseArray(dataJsonObj.getString(formChildName));
                                        if(ListUtils.isNotNull(childListRow)){
                                            /**
                                             * 先删后插
                                             */
                                            childDelQql.set(" delete from "+formChildName+" where fk_id='"+genId+"'");
                                            log.info("更新时删除操作子表InsertSQL======"+childDelQql);

                                            childListRow.stream().forEach(var->{
                                                JSONObject obj = (JSONObject) var;
                                                StringBuilder fieldChildSqlStr = new StringBuilder(), valueChildSqlStr = new StringBuilder();
                                                //子表主键
                                                String dtlId=String.valueOf(RandomUtils.nextLong());
                                                fieldChildSqlStr.append("`id`,");
                                                fieldChildSqlStr.append("`fk_id`,");
                                                valueChildSqlStr.append("\"").append(dtlId).append("\",");
                                                valueChildSqlStr.append("\"").append(genId).append("\",");
                                                for (Map.Entry<String, Object> entry : obj.entrySet()) {
                                                    if(!entry.getKey().equals("index")) {
                                                        fieldChildSqlStr.append("`").append(entry.getKey()).append("`,");
                                                        valueChildSqlStr.append("\"").append(entry.getValue()).append("\",");
                                                    }
                                                }
                                                fieldChildSqlStr.append("`").append("rdt").append("`,");
                                                valueChildSqlStr.append("\"").append(DateTime.now().toString("yyyy-MM-dd HH:mm:ss")).append("\",");
                                                fieldChildSqlStr.deleteCharAt(fieldChildSqlStr.length() - 1);
                                                valueChildSqlStr.deleteCharAt(valueChildSqlStr.length() - 1);
                                                childQql.set("insert into " + formChildName +
                                                        "(" + fieldChildSqlStr + ") " +
                                                        "values(" + valueChildSqlStr + ")");
                                                log.info("插入操作子表InsertSQL======"+childQql);
                                                sqlList.add(String.valueOf(childQql));
                                            });
                                            sqlDelList.add(String.valueOf(childDelQql));
                                        }
                                        continue;
                                    }
                                    fieldSqlStr.append(rowlist.getString("id")).append("=").append("\"").append(rowlist.get("value")).append("\",");
                                }
                            }
                        }
                        else {
                            fieldSqlStr.append(object.getString("id")).append("=").append("\"").append(object.get("value")).append("\",");
                        }
                    });
                    fieldSqlStr.append(" rdt = ").append("\"").append(DateTime.now().toString("yyyy-MM-dd HH:mm:ss")).append("\",");
                    fieldSqlStr.deleteCharAt(fieldSqlStr.length() - 1);
                    sql= "update  " + form_data_table +" set "+fieldSqlStr + " where id='" + genId+"'" ;
                    log.info("最终执行的主表更新sql==={}",sql);
                    sqlMapper.update(sql,null);
                    if(ListUtils.isNotNull(sqlList)){
                        sqlDelList.stream().forEach(var->{
                            sqlMapper.delete(var,null);
                        });
                        sqlList.stream().forEach(var->{
                            sqlMapper.insert(var,null);
                        });
                    }
                }

            }else{
                StringBuilder fieldSqlStr = new StringBuilder(), valueSqlStr = new StringBuilder();
                fieldSqlStr.append("`id`,");
                valueSqlStr.append("\"").append(genId).append("\",");

                String sql =new String();
                AtomicReference<String> childQql = new AtomicReference<>(new String());
                List<String> sqlList =new ArrayList<>();
                JSONArray listJson = JSONArray.parseArray(jsonObject.getString("list"));
                if(ListUtils.isNotNull(listJson)){
                    //如果是子表
                    // if(!checkId(object.get("id").toString())){
                    listJson.stream().map(var -> (JSONObject) var).forEach(object -> {
                        if (object.get("compType").equals("dynamicTable")) {
                            //遍历相关的子表
                            String formChildName = object.get("id").toString();//子表的表
                            JSONArray childList = JSONArray.parseArray(dataJsonObj.getString(formChildName));

                            if(ListUtils.isNotNull(childList)){
                                childList.stream().forEach(var->{
                                    JSONObject obj = (JSONObject) var;
                                    StringBuilder fieldChildSqlStr = new StringBuilder(), valueChildSqlStr = new StringBuilder();
                                    fieldChildSqlStr.append("`id`,");
                                    fieldChildSqlStr.append("`fk_id`,");
                                    valueChildSqlStr.append("\"").append(RandomUtils.nextLong()).append("\",");
                                    valueChildSqlStr.append("\"").append(genId).append("\",");
                                    for (Map.Entry<String, Object> entry : obj.entrySet()) {
                                        if(!entry.getKey().equals("index")) {
                                            fieldChildSqlStr.append("`").append(entry.getKey()).append("`,");
                                            valueChildSqlStr.append("\"").append(entry.getValue()).append("\",");
                                        }
                                    }
                                    fieldChildSqlStr.append("`").append("rdt").append("`,");
                                    valueChildSqlStr.append("\"").append(DateTime.now().toString("yyyy-MM-dd HH:mm:SS")).append("\",");
                                    fieldChildSqlStr.deleteCharAt(fieldChildSqlStr.length() - 1);
                                    valueChildSqlStr.deleteCharAt(valueChildSqlStr.length() - 1);
                                    childQql.set("insert into " + formChildName +
                                            "(" + fieldChildSqlStr + ") " +
                                            "values(" + valueChildSqlStr + ")");
                                    sqlList.add(String.valueOf(childQql));
                                    log.info("插入操作子表InsertSQL======"+childQql);
                                });
                            }
                            return;
                        }
                        if (object.getString("compType").equals("text")
                                || object.getString("compType").equals("barCode")
                                || object.getString("compType").equals("alert")
                                || object.getString("compType").equals("link")
                                || object.getString("compType").equals("button")
                                || object.getString("compType").equals("colorPicker")
                                || object.getString("compType").equals("cascader")
                                || object.getString("compType").equals("divider")
                                ) {
                            return;
                        }
                        //保存附件
                        if(object.getString("compType").equals("upload")){
                            if(!SystemConfig.IsNullOrEmpty(object.get("value"))){
                                JSONArray fileJson = JSONArray.parseArray(object.getString("value"));
                                //先删除改组件下的附件数据
                                attachmentMapper.deleteAttByGenerWork(genId,object.getString("id"));
                                fileJson.stream().map(var -> (JSONObject) var).forEach(file -> {
                                    Attachment attachment=new Attachment();
                                    attachment.setGenworkId(genId);
                                    attachment.setDelFlag("0");
                                    attachment.setName(file.getString("fileName"));
                                    attachment.setPath(file.getString("filePath"));
                                    attachment.setModuleName(object.getString("id"));
                                    attachment.setRdt(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
                                    attachment.setId(UUID.randomUUID().toString());
                                    attachmentList.add(attachment);
                                });
                            }
                            return;
                        }
                        if(object.getString("compType").equals("row")){
                            JSONArray childList = JSONArray.parseArray(object.getString("columns"));
                            for(int i = 0; i < childList.size(); i++){
                                JSONObject children = childList.getJSONObject(i);
                                JSONArray childrenList=JSONArray.parseArray(children.getString("list"));
                                for(int k=0;k<childrenList.size();k++){
                                    JSONObject rowlist=childrenList.getJSONObject(k);
                                    if (rowlist.getString("compType").equals("text")
                                            || rowlist.getString("compType").equals("barCode")
                                            || rowlist.getString("compType").equals("alert")
                                            || rowlist.getString("compType").equals("link")
                                            || rowlist.getString("compType").equals("button")
                                            || rowlist.getString("compType").equals("colorPicker")
                                            || rowlist.getString("compType").equals("cascader")
                                            || rowlist.getString("compType").equals("divider")
                                            ) {
                                        continue;
                                    }
                                    //保存附件
                                    if(rowlist.getString("compType").equals("upload")){
                                        if(!SystemConfig.IsNullOrEmpty(rowlist.get("value"))){
                                            JSONArray fileJson = JSONArray.parseArray(rowlist.getString("value"));
                                            //先删除改组件下的附件数据
                                            attachmentMapper.deleteAttByGenerWork(genId,rowlist.getString("id"));
                                            fileJson.stream().map(var -> (JSONObject) var).forEach(file -> {
                                                Attachment attachment=new Attachment();
                                                attachment.setGenworkId(genId);
                                                attachment.setDelFlag("0");
                                                attachment.setName(file.getString("fileName"));
                                                attachment.setPath(file.getString("filePath"));
                                                attachment.setModuleName(rowlist.getString("id"));
                                                attachment.setRdt(DateTime.now().toString("yyyy-MM-dd HH:mm:ss"));
                                                attachment.setId(UUID.randomUUID().toString());
                                                attachmentList.add(attachment);
                                            });
                                        }
                                        continue;
                                    }
                                    if (rowlist.get("compType").equals("dynamicTable")) {
                                        //遍历相关的子表
                                        String formChildName = rowlist.get("id").toString();//子表的表
                                        JSONArray childListRow = JSONArray.parseArray(dataJsonObj.getString(formChildName));
                                        if(ListUtils.isNotNull(childListRow)){
                                            /**
                                             * 先删除后插
                                             */
                                            childListRow.stream().forEach(var->{
                                                JSONObject obj = (JSONObject) var;
                                                StringBuilder fieldChildSqlStr = new StringBuilder(), valueChildSqlStr = new StringBuilder();
                                                //子表主键
                                                String dtlId=String.valueOf(RandomUtils.nextLong());
                                                fieldChildSqlStr.append("`id`,");
                                                fieldChildSqlStr.append("`fk_id`,");
                                                valueChildSqlStr.append("\"").append(dtlId).append("\",");
                                                valueChildSqlStr.append("\"").append(genId).append("\",");
                                                for (Map.Entry<String, Object> entry : obj.entrySet()) {
                                                    if(!entry.getKey().equals("index")) {
                                                        fieldChildSqlStr.append("`").append(entry.getKey()).append("`,");
                                                        valueChildSqlStr.append("\"").append(entry.getValue()).append("\",");
                                                    }
                                                }
                                                fieldChildSqlStr.append("`").append("rdt").append("`,");
                                                valueChildSqlStr.append("\"").append(DateTime.now().toString("yyyy-MM-dd HH:mm:ss")).append("\",");
                                                fieldChildSqlStr.deleteCharAt(fieldChildSqlStr.length() - 1);
                                                valueChildSqlStr.deleteCharAt(valueChildSqlStr.length() - 1);
                                                childQql.set("insert into " + formChildName +
                                                        "(" + fieldChildSqlStr + ") " +
                                                        "values(" + valueChildSqlStr + ")");
                                                log.info("插入操作子表InsertSQL======"+childQql);
                                                sqlList.add(String.valueOf(childQql));
                                            });
                                            //sqlDelList.add(String.valueOf(childDelQql));
                                        }
                                        continue;
                                    }
                                    fieldSqlStr.append("`").append(rowlist.getString("id")).append("`,");
                                    valueSqlStr.append("\"").append(rowlist.get("value")).append("\",");
                                }
                            }
                        }
                        else {
                            fieldSqlStr.append("`").append(object.getString("id")).append("`,");
                            valueSqlStr.append("\"").append(object.get("value")).append("\",");
                        }
                    });
                    fieldSqlStr.append("`").append("rdt").append("`,");
                    valueSqlStr.append("\"").append(DateTime.now().toString("yyyy-MM-dd HH:mm:SS")).append("\",");
                    fieldSqlStr.deleteCharAt(fieldSqlStr.length() - 1);
                    valueSqlStr.deleteCharAt(valueSqlStr.length() - 1);
                    sql= "insert into " + form_data_table +
                            "(" + fieldSqlStr + ") " +
                            "values(" + valueSqlStr + ")";
                    sqlMapper.insert(sql,null);
                    if(ListUtils.isNotNull(sqlList)){
                        //TODO
                        sqlList.stream().forEach(var->{
                            sqlMapper.insert(var,null);
                        });
                    }
                }
            }
            attachmentList.stream().forEach(attachment -> {
                attachmentMapper.addAtt(attachment);
            });
        }
        return genId;
    }
    @Override
    public HashMap<String,Object> getApplicationData(JSONObject param){
        ColsDto colObject=new ColsDto();
        //显示的数据列
        List<ColsDto> cols=colObject.JsonArrayToClass(param.getJSONArray("cols"));
        //查询条件
        List<ColsDto> serchCols=colObject.JsonArrayToClass(param.getJSONArray("settingKeys"));
        //数据存储主表
        String mainTable=param.getString("mainTable");
        //查询语句
        StringBuilder sql = new StringBuilder().append("select a.id, ");
        //查询权限
        String dataScope=iFormManager.applicationDataScope();
        //总数
        StringBuilder sqlCount = new StringBuilder().append("select count(*) FROM ")
                .append(mainTable)
                .append(" a  left join ac_sl_generwork b on a.id=b.id "+dataScope+"");
        for(ColsDto col:cols){
            sql.append(col.getId()+",");
        }
        sql.deleteCharAt(sql.length()-1);
        sql.append(" from "+mainTable+" a left join ac_sl_generwork b on a.id=b.id "+dataScope+" ");
        for (ColsDto col:serchCols){
            if(SystemConfig.IsNullOrEmpty(param.getString(col.getId())))
                continue;
            if(col.getCompType().equals("select")||col.getCompType().equals("radio")
            ||col.getCompType().equals("cascader")||col.getCompType().equals("checkbox")) {
                sql.append(" and a." + col.getId() + "=" + param.getString(col.getId()));
                sqlCount.append(" and a." + col.getId() + "=" + param.getString(col.getId()));
            }
            else {
                sql.append(" and a."+col.getId() + " like '%").append(param.getString(col.getId())).append("%' ");
                sqlCount.append(" and a."+col.getId() + " like '%").append(param.getString(col.getId())).append("%' ");
            }
        }
        //数据分页；
        Integer pageSize= param.getInteger("pageSize");
        Integer startIndex= (param.getInteger("page")-1)*pageSize;
        sql.append(" limit ").append(startIndex).append(",").append(pageSize);
        //返回数据
        return iFormManager.returnSqlData(sql.toString(),sqlCount.toString());
    }
}
