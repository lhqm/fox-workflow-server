package com.activiti.z_six.service.manager;

import com.activiti.z_six.entity.formComponents.CreateTable;
import com.activiti.z_six.entity.formComponents.CustomTable;
import com.activiti.z_six.entity.formComponents.FormEntity;
import com.activiti.z_six.entity.formComponents.FormSortEntity;
import com.activiti.z_six.mapper.SqlMapper;
import com.activiti.z_six.mapper.formComponentsMapper.FormEntityMapper;
import com.activiti.z_six.util.StringUtils;
import com.activiti.z_six.util.SystemConfig;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.*;

import java.util.stream.Collectors;


@Slf4j
@Component
@RequiredArgsConstructor
public class IFormManager {
    @Value("spring.datasource.name")
    private String dataSourceName;
    @Autowired
    private FormEntityMapper formEntityMapper;
    @Autowired
    private SqlMapper sqlMapper;
    @Autowired
    private CommManager commManager;
    public String insertSql(String tableId, String formDataTable , JSONObject param ){
        String tableName= null;
        if(!Objects.isNull(param)){
            StringBuilder fieldSqlStr = new StringBuilder(), valueSqlStr = new StringBuilder();
            JSONObject jsonObject=JSONObject.parseObject(param.getString("list"));
            JSONObject configJson=JSONObject.parseObject(jsonObject.getString("config"));
            tableName = configJson.getString("tableName").toString();
            JSONArray.parseArray(jsonObject.getString("list")).stream().forEach(var->{
                JSONObject json = (JSONObject) var;
                if(json.getString("viewType").equals("component")){
                    fieldSqlStr.append("`").append(json.getString("id")).append("`,");
                    valueSqlStr.append("#{param.").append(json.getString("value")).append("},");
                }
            });
            return "insert into "+ tableName+
                    "(`id`," + fieldSqlStr + ") " +
                    "values(" + valueSqlStr + ")";
        }
        return null;
    }
    public String applicationDataScope(){
        String userNo=commManager.getUserNo();
        if(userNo.equals("admin")){
            return " where 1=1 ";
        }
        else {
            String dataScope="select username from sys_deptuser where " +
                    "departid in(select dept_Id from sys_role_dept " +
                    "where role_Id in(select roleid from sys_roleusers " +
                    "where username='"+userNo+"'))";
            List<Map<String,Object>> mapList=sqlMapper.list(dataScope,null);
            if(mapList.size()<=0){
                dataScope="where b.starter='"+userNo+"' ";
            }
            else{
                dataScope="where b.starter in( select username from sys_deptuser where " +
                        "departid in(select dept_Id from sys_role_dept " +
                        "where role_Id in(select roleid from sys_roleusers " +
                        "where username='"+userNo+"')))";
            }
            return dataScope;
        }
    }

    /**
     * 返回查询数据
     * @param sql
     * @param sqlCount
     * @return
     */
    public HashMap<String,Object> returnSqlData(String sql,String sqlCount){
        //查询总条数
        Long count = sqlMapper.count(sqlCount,null);
        //查询数据
        List<Map<String,Object>> mapList=sqlMapper.list(sql,null);
        //返回数据
        HashMap<String,Object> hashMap=new HashMap<>();
        hashMap.put("total",count);
        hashMap.put("list",mapList);
        return hashMap;
    }
    /**
     * 增加表单
     * @param formEntity
     * @return
     */
    public String addForm(FormEntity formEntity){
        if(!StringUtils.isEmpty(formEntity.getForm_json())) {
            JSONObject jsonObject=JSONObject.parseObject(formEntity.getForm_json());
            String msg=createTableByJson(formEntity.getForm_data_table(),jsonObject.getString("list"),false);
            if(!msg.equals("suess")){
                return msg;
            }
        }
        //增加表单模版
        formEntityMapper.addForm(formEntity);
        return "执行成功";
    }

    /**
     * 修改表单
     * @param formEntity
     * @return
     */
    public FormEntity updateForm(FormEntity formEntity){
        //更新模版
        formEntityMapper.updateForm(formEntity);

        //判断是否已经存在该表
        int isTableExist=formEntityMapper.isTableExist(formEntity.getForm_data_table(),dataSourceName);
        if(isTableExist>0) {
            AlterTableAddField(formEntity.getForm_data_table(),formEntity.getForm_json());
        }
        else{
            JSONObject jsonObject=JSONObject.parseObject(formEntity.getForm_json());
            String msg=createTableByJson(formEntity.getForm_data_table(),jsonObject.getString("list"),false);
            if(!msg.equals("suess")){
                return formEntity;
            }
        }
        return formEntity;
    }
    /**
     * 递归查找当前菜单的子菜单
     * @param root 单个对象
     * @param all 所有的集合
     * @return 排序后的子类
     */
    public List<FormSortEntity> getChildren(FormSortEntity root, List<FormSortEntity> all){
        List<FormSortEntity> childrenList = all.stream()
                .filter(t -> t.getParentNo().equals(root.getId()) )
                .map(g -> {
                    //找子菜单
                    g.setChildren(getChildren(g,all));
                    return g;
                })
                //菜单排序
                .collect(Collectors.toList());

        return childrenList;
    }
    /**
     * 根据json动态创建数据库表
     * @param tableName
     * @param columnsJson
     * @param isSub
     * @return
     */
    public String createTableByJson(String tableName,String columnsJson,Boolean isSub){
        String msg="";
        //要创建的表的集合
        List<CreateTable> createTables=new ArrayList<>();
        JSONArray listJson=JSONArray.parseArray(columnsJson);

        int isTableExist=formEntityMapper.isTableExist(tableName,dataSourceName);
        if(isTableExist>0)
            return "该表["+tableName+"]已经存在，请重新命名表名。";

        try {
            //创建表对象
            CreateTable createTable = new CreateTable();
            createTable.setTableName(tableName);
            //列对象集合
            List<CustomTable> customTableList = new ArrayList<>();

            //列对象
            CustomTable customTable = new CustomTable();
            if (isSub) {

                customTable.setChoose(true);
                customTable.setCreateTableFiledName("fk_id");
                customTable.setComment("主表id");
                customTable.setFieldType("varchar(255)");
                customTableList.add(customTable);

            }
            for (int i = 0; i < listJson.size(); i++) {
                customTable = new CustomTable();
                JSONObject list = listJson.getJSONObject(i);
                //如果类型是文本、二维码、alert、超链接、按钮、颜色选择器、分割线、附件
                if (list.getString("compType").equals("text")
                        || list.getString("compType").equals("barCode")
                        || list.getString("compType").equals("alert")
                        || list.getString("compType").equals("link")
                        || list.getString("compType").equals("button")
                        || list.getString("compType").equals("colorPicker")
                        || list.getString("compType").equals("divider")
                        || list.getString("compType").equals("upload"))
                    continue;
                if (list.getString("compType").equals("dynamicTable")) {
                    if (!SystemConfig.IsNullOrEmpty(list.getString("columns"))) {
                        JSONArray childrens = JSONArray.parseArray(list.getString("columns"));

                        isTableExist = formEntityMapper.isTableExist(list.get("id").toString(),dataSourceName);
                        if (isTableExist > 0)
                            return "该表[" + list.get("id").toString() + "]已经存在，请重新命名表名。";

                        CreateTable childrenTable = new CreateTable();
                        childrenTable.setTableName(list.get("id").toString());
                        //列对象
                        CustomTable childrenCol = new CustomTable();
                        //列对象集合
                        List<CustomTable> childrenColList = new ArrayList<>();

                        childrenCol.setChoose(true);
                        childrenCol.setCreateTableFiledName("fk_id");
                        childrenCol.setComment("主表id");
                        childrenCol.setFieldType("varchar(255)");
                        childrenColList.add(childrenCol);

                        for (int k = 0; k < childrens.size(); k++) {
                            childrenCol = new CustomTable();
                            JSONObject children = childrens.getJSONObject(k);
                            //如果类型是文本、二维码、alert、超链接、按钮、颜色选择器、分割线、附件
                            //这些需要忽略掉。属于提示性组件或者固定组件
                            if (children.getString("compType").equals("text")
                                    || children.getString("compType").equals("barCode")
                                    || children.getString("compType").equals("alert")
                                    || children.getString("compType").equals("link")
                                    || children.getString("compType").equals("button")
                                    || children.getString("compType").equals("colorPicker")
                                    || children.getString("compType").equals("divider")
                                    || children.getString("compType").equals("upload"))
                                continue;
                            //类型如果是 input\select\checkbox、弹窗、联级选择
                            //这些类型需要创建表字段类型varchar(255)
                            if (children.getString("compType").equals("input") ||
                                    children.getString("compType").equals("select") ||
                                    children.getString("compType").equals("checkbox")
                                    || children.getString("compType").equals("dialogList")
                                    || children.getString("compType").equals("ca" +
                                    "scader")
                            ) {
                                childrenCol.setChoose(true);
                                childrenCol.setCreateTableFiledName(children.getString("id"));
                                childrenCol.setComment(children.getString("label"));
                                childrenCol.setFieldType("varchar(255)");
                                childrenColList.add(childrenCol);
                            }
                            //类型如果是 大文本、富文本
                            if (children.getString("compType").equals("textarea")
                                    || children.getString("compType").equals("editor")) {
                                childrenCol.setChoose(true);
                                childrenCol.setCreateTableFiledName(children.getString("id"));
                                childrenCol.setComment(children.getString("label"));
                                childrenCol.setFieldType("text");
                                childrenColList.add(childrenCol);
                            }
                            //如果类型是 日期、时间
                            if (children.getString("compType").equals("date")
                                    || children.getString("compType").equals("time")) {
                                childrenCol.setChoose(true);
                                childrenCol.setCreateTableFiledName(children.getString("id"));
                                childrenCol.setComment(children.getString("label"));
                                childrenCol.setFieldType("varchar(100)");
                                childrenColList.add(childrenCol);
                            }
                            //如果类型是 单选、评分、滑块、数字
                            if (children.getString("compType").equals("radio")
                                    || children.getString("compType").equals("rate")
                                    || children.getString("compType").equals("slider")
                                    || children.getString("compType").equals("inputNumber")) {
                                childrenCol.setChoose(true);
                                childrenCol.setCreateTableFiledName(children.getString("id"));
                                childrenCol.setComment(children.getString("label"));
                                childrenCol.setFieldType("int(11)");
                                childrenColList.add(childrenCol);
                            }
                            //如果类型是 开关
                            if (children.getString("compType").equals("Switch")) {
                                childrenCol.setChoose(true);
                                childrenCol.setCreateTableFiledName(list.getString("id"));
                                childrenCol.setComment(list.getString("label"));
                                childrenCol.setFieldType("varchar(50)");
                                childrenColList.add(customTable);
                            }
                        }
                        childrenTable.setCustomTables(childrenColList);
                        createTables.add(childrenTable);
                    }
                }
                if (list.getString("compType").equals("row")) {
                    if (!SystemConfig.IsNullOrEmpty(list.getString("columns"))) {
                        JSONArray childrens = JSONArray.parseArray(list.getString("columns"));

                        for (int k = 0; k < childrens.size(); k++) {
                            JSONObject children = childrens.getJSONObject(k);
                            JSONArray childrenList=JSONArray.parseArray(children.getString("list"));
                            for(int j = 0; j < childrenList.size(); j++){
                                JSONObject rowlist=childrenList.getJSONObject(j);
                                customTable = new CustomTable();
                                //如果类型是文本、二维码、alert、超链接、按钮、颜色选择器、分割线、附件
                                if (rowlist.getString("compType").equals("text")
                                        || rowlist.getString("compType").equals("barCode")
                                        || rowlist.getString("compType").equals("alert")
                                        || rowlist.getString("compType").equals("link")
                                        || rowlist.getString("compType").equals("button")
                                        || rowlist.getString("compType").equals("colorPicker")
                                        || rowlist.getString("compType").equals("divider")
                                        || rowlist.getString("compType").equals("upload"))
                                    continue;
                                //类型如果是 input\select\checkbox、弹窗、联级选择
                                if (rowlist.getString("compType").equals("input") ||
                                        rowlist.getString("compType").equals("select") ||
                                        rowlist.getString("compType").equals("checkbox")
                                        || rowlist.getString("compType").equals("dialogList")
                                        || rowlist.getString("compType").equals("cascader")
                                ) {
                                    customTable.setChoose(true);
                                    customTable.setCreateTableFiledName(rowlist.getString("id"));
                                    customTable.setComment(rowlist.getString("label"));
                                    customTable.setFieldType("varchar(255)");
                                    customTableList.add(customTable);
                                }
                                if (rowlist.getString("compType").equals("dynamicTable")) {
                                    if (!SystemConfig.IsNullOrEmpty(rowlist.getString("columns"))) {
                                        JSONArray rowlistChildrens = JSONArray.parseArray(rowlist.getString("columns"));

                                        isTableExist = formEntityMapper.isTableExist(rowlist.get("id").toString(),dataSourceName);
                                        if (isTableExist > 0)
                                            return "该表[" + rowlist.get("id").toString() + "]已经存在，请重新命名表名。";

                                        CreateTable childrenTable = new CreateTable();
                                        childrenTable.setTableName(rowlist.get("id").toString());
                                        //列对象
                                        CustomTable childrenCol = new CustomTable();
                                        //列对象集合
                                        List<CustomTable> childrenColList = new ArrayList<>();

                                        childrenCol.setChoose(true);
                                        childrenCol.setCreateTableFiledName("fk_id");
                                        childrenCol.setComment("主表id");
                                        childrenCol.setFieldType("varchar(255)");
                                        childrenColList.add(childrenCol);

                                        for (int z = 0; z < rowlistChildrens.size(); z++) {
                                            childrenCol = new CustomTable();
                                            JSONObject rowlistChildren = rowlistChildrens.getJSONObject(z);
                                            //如果类型是文本、二维码、alert、超链接、按钮、颜色选择器、分割线、附件
                                            if (rowlistChildren.getString("compType").equals("text")
                                                    || rowlistChildren.getString("compType").equals("barCode")
                                                    || rowlistChildren.getString("compType").equals("alert")
                                                    || rowlistChildren.getString("compType").equals("link")
                                                    || rowlistChildren.getString("compType").equals("button")
                                                    || rowlistChildren.getString("compType").equals("colorPicker")
                                                    || rowlistChildren.getString("compType").equals("divider")
                                                    || rowlistChildren.getString("compType").equals("upload"))
                                                continue;
                                            //类型如果是 input\select\checkbox、弹窗、联级选择
                                            if (rowlistChildren.getString("compType").equals("input") ||
                                                    rowlistChildren.getString("compType").equals("select") ||
                                                    rowlistChildren.getString("compType").equals("checkbox")
                                                    || rowlistChildren.getString("compType").equals("dialogList")
                                                    || rowlistChildren.getString("compType").equals("cascader")
                                            ) {
                                                childrenCol.setChoose(true);
                                                childrenCol.setCreateTableFiledName(rowlistChildren.getString("id"));
                                                childrenCol.setComment(rowlistChildren.getString("label"));
                                                childrenCol.setFieldType("varchar(255)");
                                                childrenColList.add(childrenCol);
                                            }
                                            //类型如果是 大文本、富文本
                                            if (rowlistChildren.getString("compType").equals("textarea")
                                                    || rowlistChildren.getString("compType").equals("editor")) {
                                                childrenCol.setChoose(true);
                                                childrenCol.setCreateTableFiledName(rowlistChildren.getString("id"));
                                                childrenCol.setComment(rowlistChildren.getString("label"));
                                                childrenCol.setFieldType("text");
                                                childrenColList.add(childrenCol);
                                            }
                                            //如果类型是 日期、时间
                                            if (rowlistChildren.getString("compType").equals("date")
                                                    || rowlistChildren.getString("compType").equals("time")) {
                                                childrenCol.setChoose(true);
                                                childrenCol.setCreateTableFiledName(rowlistChildren.getString("id"));
                                                childrenCol.setComment(rowlistChildren.getString("label"));
                                                childrenCol.setFieldType("varchar(100)");
                                                childrenColList.add(childrenCol);
                                            }
                                            //如果类型是 单选、评分、滑块、数字
                                            if (rowlistChildren.getString("compType").equals("radio")
                                                    || rowlistChildren.getString("compType").equals("rate")
                                                    || rowlistChildren.getString("compType").equals("slider")
                                                    || rowlistChildren.getString("compType").equals("inputNumber")) {
                                                childrenCol.setChoose(true);
                                                childrenCol.setCreateTableFiledName(rowlistChildren.getString("id"));
                                                childrenCol.setComment(rowlistChildren.getString("label"));
                                                childrenCol.setFieldType("int(11)");
                                                childrenColList.add(childrenCol);
                                            }
                                            //如果类型是 开关
                                            if (rowlistChildren.getString("compType").equals("Switch")) {
                                                childrenCol.setChoose(true);
                                                childrenCol.setCreateTableFiledName(rowlistChildren.getString("id"));
                                                childrenCol.setComment(rowlistChildren.getString("label"));
                                                childrenCol.setFieldType("varchar(50)");
                                                childrenColList.add(customTable);
                                            }
                                        }
                                        childrenTable.setCustomTables(childrenColList);
                                        createTables.add(childrenTable);
                                    }
                                }
                                //类型如果是 大文本、富文本
                                if (rowlist.getString("compType").equals("textarea")
                                        || rowlist.getString("compType").equals("editor")) {
                                    customTable.setChoose(true);
                                    customTable.setCreateTableFiledName(rowlist.getString("id"));
                                    customTable.setComment(rowlist.getString("label"));
                                    customTable.setFieldType("text");
                                    customTableList.add(customTable);
                                }
                                //如果类型是 日期、时间
                                if (rowlist.getString("compType").equals("date")
                                        || rowlist.getString("compType").equals("time")) {
                                    customTable.setChoose(true);
                                    customTable.setCreateTableFiledName(rowlist.getString("id"));
                                    customTable.setComment(rowlist.getString("label"));
                                    customTable.setFieldType("varchar(100)");
                                    customTableList.add(customTable);
                                }
                                //如果类型是 单选、评分、滑块、数字
                                if (rowlist.getString("compType").equals("radio")
                                        || rowlist.getString("compType").equals("rate")
                                        || rowlist.getString("compType").equals("slider")
                                        || rowlist.getString("compType").equals("inputNumber")) {
                                    customTable.setChoose(true);
                                    customTable.setCreateTableFiledName(rowlist.getString("id"));
                                    customTable.setComment(rowlist.getString("label"));
                                    customTable.setFieldType("int(11)");
                                    customTableList.add(customTable);
                                }
                                //如果类型是 开关
                                if (rowlist.getString("compType").equals("Switch")) {
                                    customTable.setChoose(true);
                                    customTable.setCreateTableFiledName(list.getString("id"));
                                    customTable.setComment(list.getString("label"));
                                    customTable.setFieldType("varchar(50)");
                                    customTableList.add(customTable);
                                }
                            }
                        }
                    }
                }
                //类型如果是 input\select\checkbox、弹窗、联级选择
                if (list.getString("compType").equals("input") ||
                        list.getString("compType").equals("select") ||
                        list.getString("compType").equals("checkbox")
                        || list.getString("compType").equals("dialogList")
                        || list.getString("compType").equals("cascader")
                ) {
                    customTable.setChoose(true);
                    customTable.setCreateTableFiledName(list.getString("id"));
                    customTable.setComment(list.getString("label"));
                    customTable.setFieldType("varchar(255)");
                    customTableList.add(customTable);
                }
                //类型如果是 大文本、富文本
                if (list.getString("compType").equals("textarea")
                        || list.getString("compType").equals("editor")) {
                    customTable.setChoose(true);
                    customTable.setCreateTableFiledName(list.getString("id"));
                    customTable.setComment(list.getString("label"));
                    customTable.setFieldType("text");
                    customTableList.add(customTable);
                }
                //如果类型是 日期、时间
                if (list.getString("compType").equals("date")
                        || list.getString("compType").equals("time")) {
                    customTable.setChoose(true);
                    customTable.setCreateTableFiledName(list.getString("id"));
                    customTable.setComment(list.getString("label"));
                    customTable.setFieldType("varchar(100)");
                    customTableList.add(customTable);
                }
                //如果类型是 单选、评分、滑块、数字
                if (list.getString("compType").equals("radio")
                        || list.getString("compType").equals("rate")
                        || list.getString("compType").equals("slider")
                        || list.getString("compType").equals("inputNumber")) {
                    customTable.setChoose(true);
                    customTable.setCreateTableFiledName(list.getString("id"));
                    customTable.setComment(list.getString("label"));
                    customTable.setFieldType("int(11)");
                    customTableList.add(customTable);
                }
                // 如果是开关
                if (list.getString("compType").equals("Switch")) {
                    customTable.setChoose(true);
                    customTable.setCreateTableFiledName(list.getString("id"));
                    customTable.setComment(list.getString("label"));
                    customTable.setFieldType("varchar(50)");
                    customTableList.add(customTable);
                }
            }
            createTable.setCustomTables(customTableList);
            createTables.add(createTable);
            for (CreateTable cte : createTables) {
                formEntityMapper.createFormTable(cte);
            }
            msg = "suess";
            return msg;
        }
        catch (Exception ex){
            return ex.getMessage();
        }
    }

    /**
     * 增加表字段
     * @param form_data_table
     * @param form_json
     */
    public void AlterTableAddField(String form_data_table,String form_json) {
        //获取该表中的列
        List<String> columns = formEntityMapper.findFieldByTableName(form_data_table);
        HashMap<String, Object> hashMap = new HashMap<>();
        for (String str : columns) {
            hashMap.put(str, "");
        }

        //要创建的表的集合
        List<CreateTable> createTables = new ArrayList<>();
        if (!SystemConfig.IsNullOrEmpty(form_json)) {
            JSONObject jsonObject = JSONObject.parseObject(form_json);
            JSONObject configJson = JSONObject.parseObject(jsonObject.getString("config"));
            JSONArray listJson = JSONArray.parseArray(jsonObject.getString("list"));

            //创建表对象
            CreateTable createTable = new CreateTable();
            createTable.setTableName(configJson.get("tableName").toString());

            int changecustomTableNum=0;
            //列对象集合
            List<CustomTable> customTableList = new ArrayList<>();
            for (int i = 0; i < listJson.size(); i++) {
                //列对象
                CustomTable customTable = new CustomTable();
                JSONObject list = listJson.getJSONObject(i);
                //如果类型是文本、二维码、alert、超链接、按钮、颜色选择器、分割线、附件
                if (list.getString("compType").equals("text")
                        || list.getString("compType").equals("barCode")
                        || list.getString("compType").equals("alert")
                        || list.getString("compType").equals("link")
                        || list.getString("compType").equals("button")
                        || list.getString("compType").equals("colorPicker")
                        || list.getString("compType").equals("divider")
                        || list.getString("compType").equals("upload"))
                    continue;
//                如果是
                if (list.getString("compType").equals("dynamicTable")) {
                    if (!SystemConfig.IsNullOrEmpty(list.getString("columns"))) {
                        JSONArray childrens = JSONArray.parseArray(list.getString("columns"));
                        CreateTable childrenTable = new CreateTable();
                        childrenTable.setTableName(list.get("id").toString());

                        int isTableExist=formEntityMapper.isTableExist(list.get("id").toString(),dataSourceName);
                        if(isTableExist<=0) {
                            createTableByJson(list.get("id").toString(),list.getString("columns"),true);
                        }
                        else {
                            List<String> childrenColumns = formEntityMapper.findFieldByTableName(list.get("id").toString());
                            HashMap<String, Object> childrenMap = new HashMap<>();
                            for (String str : childrenColumns) {
                                childrenMap.put(str, "");
                            }
                            int childrenChangeNum=0;
                            //列对象
                            CustomTable childrenCol = new CustomTable();
                            //列对象集合
                            List<CustomTable> childrenColList = new ArrayList<>();

                            if (!childrenMap.containsKey("fk_id")) {
                                childrenChangeNum=1;
                                childrenCol.setChoose(true);
                                childrenCol.setCreateTableFiledName("fk_id");
                                childrenCol.setComment("主表id");
                                childrenCol.setFieldType("varchar(255)");
                                childrenColList.add(childrenCol);
                            }

                            for (int k = 0; k < childrens.size(); k++) {
                                childrenCol = new CustomTable();
                                JSONObject children = childrens.getJSONObject(k);
                                //如果类型是文本、二维码、alert、超链接、按钮、颜色选择器、分割线、附件
                                if (children.getString("compType").equals("text")
                                        || children.getString("compType").equals("barCode")
                                        || children.getString("compType").equals("alert")
                                        || children.getString("compType").equals("link")
                                        || children.getString("compType").equals("button")
                                        || children.getString("compType").equals("colorPicker")
                                        || children.getString("compType").equals("divider")
                                        || children.getString("compType").equals("upload"))
                                    continue;
                                if (childrenMap.containsKey(children.getString("id"))) {
                                    continue;
                                } else {
                                    //类型如果是 input\select\checkbox、弹窗、联级选择
                                    if (children.getString("compType").equals("input") ||
                                            children.getString("compType").equals("select") ||
                                            children.getString("compType").equals("checkbox")
                                            || children.getString("compType").equals("dialogList")
                                            || children.getString("compType").equals("cascader")
                                    ) {
                                        childrenCol.setChoose(true);
                                        childrenCol.setCreateTableFiledName(children.getString("id"));
                                        childrenCol.setComment(children.getString("label"));
                                        childrenCol.setFieldType("varchar(255)");
                                        childrenColList.add(childrenCol);
                                        childrenChangeNum++;
                                    }
                                    //类型如果是 大文本、富文本
                                    if (children.getString("compType").equals("textarea")
                                            || children.getString("compType").equals("editor")) {
                                        childrenCol.setChoose(true);
                                        childrenCol.setCreateTableFiledName(children.getString("id"));
                                        childrenCol.setComment(children.getString("label"));
                                        childrenCol.setFieldType("text");
                                        childrenColList.add(childrenCol);
                                        childrenChangeNum++;
                                    }
                                    //如果类型是 日期、时间
                                    if (children.getString("compType").equals("date")
                                            || children.getString("compType").equals("time")) {
                                        childrenCol.setChoose(true);
                                        childrenCol.setCreateTableFiledName(children.getString("id"));
                                        childrenCol.setComment(children.getString("label"));
                                        childrenCol.setFieldType("varchar(100)");
                                        childrenColList.add(childrenCol);
                                        childrenChangeNum++;
                                    }
                                    //如果类型是 单选、评分、滑块、数字
                                    if (children.getString("compType").equals("radio")
                                            || children.getString("compType").equals("rate")
                                            || children.getString("compType").equals("slider")
                                            || children.getString("compType").equals("inputNumber")) {
                                        childrenCol.setChoose(true);
                                        childrenCol.setCreateTableFiledName(children.getString("id"));
                                        childrenCol.setComment(children.getString("label"));
                                        childrenCol.setFieldType("int(11)");
                                        childrenColList.add(childrenCol);
                                        childrenChangeNum++;
                                    }
                                    //如果类型是 开关
                                    if (children.getString("compType").equals("Switch")) {
                                        childrenCol.setChoose(true);
                                        childrenCol.setCreateTableFiledName(children.getString("id"));
                                        childrenCol.setComment(children.getString("label"));
                                        childrenCol.setFieldType("varchar(50)");
                                        childrenColList.add(childrenCol);
                                        childrenChangeNum++;
                                    }
                                }
                            }
                            if(childrenChangeNum>0) {
                                childrenTable.setCustomTables(childrenColList);
                                createTables.add(childrenTable);
                            }
                        }
                    }
                }
                if (list.getString("compType").equals("row")) {
                    if (!SystemConfig.IsNullOrEmpty(list.getString("columns"))) {
                        JSONArray childrens = JSONArray.parseArray(list.getString("columns"));
                        for (int k = 0; k < childrens.size(); k++) {
                            JSONObject children = childrens.getJSONObject(k);
                            JSONArray childrenList=JSONArray.parseArray(children.getString("list"));
                            for(int j = 0; j < childrenList.size(); j++){
                                JSONObject rowlist=childrenList.getJSONObject(j);
                                customTable = new CustomTable();
                                //如果类型是文本、二维码、alert、超链接、按钮、颜色选择器、分割线、附件
                                if (rowlist.getString("compType").equals("text")
                                        || rowlist.getString("compType").equals("barCode")
                                        || rowlist.getString("compType").equals("alert")
                                        || rowlist.getString("compType").equals("link")
                                        || rowlist.getString("compType").equals("button")
                                        || rowlist.getString("compType").equals("colorPicker")
                                        || rowlist.getString("compType").equals("divider")
                                        || rowlist.getString("compType").equals("upload"))
                                    continue;
                                if (hashMap.containsKey(rowlist.getString("id"))) {
                                    continue;
                                } else {
                                    //类型如果是 input\select\checkbox、弹窗、联级选择
                                    if (rowlist.getString("compType").equals("input") ||
                                            rowlist.getString("compType").equals("select") ||
                                            rowlist.getString("compType").equals("checkbox")
                                            || rowlist.getString("compType").equals("dialogList")
                                            || rowlist.getString("compType").equals("cascader")
                                    ) {
                                        customTable.setChoose(true);
                                        customTable.setCreateTableFiledName(rowlist.getString("id"));
                                        customTable.setComment(rowlist.getString("label"));
                                        customTable.setFieldType("varchar(255)");
                                        customTableList.add(customTable);
                                        changecustomTableNum++;
                                    }
                                    if (rowlist.getString("compType").equals("dynamicTable")) {
                                        if (!SystemConfig.IsNullOrEmpty(rowlist.getString("columns"))) {
                                            JSONArray rowlistChildrens = JSONArray.parseArray(rowlist.getString("columns"));
                                            CreateTable childrenTable = new CreateTable();
                                            childrenTable.setTableName(rowlist.get("id").toString());

                                            int isTableExist=formEntityMapper.isTableExist(rowlist.get("id").toString(),dataSourceName);
                                            if(isTableExist<=0) {
                                                createTableByJson(rowlist.get("id").toString(),rowlist.getString("columns"),true);
                                            }
                                            else {
                                                List<String> childrenColumns = formEntityMapper.findFieldByTableName(rowlist.get("id").toString());
                                                HashMap<String, Object> childrenMap = new HashMap<>();
                                                for (String str : childrenColumns) {
                                                    childrenMap.put(str, "");
                                                }
                                                int childrenChangeNum=0;
                                                //列对象
                                                CustomTable childrenCol = new CustomTable();
                                                //列对象集合
                                                List<CustomTable> childrenColList = new ArrayList<>();

                                                if (!childrenMap.containsKey("fk_id")) {
                                                    childrenChangeNum=1;
                                                    childrenCol.setChoose(true);
                                                    childrenCol.setCreateTableFiledName("fk_id");
                                                    childrenCol.setComment("主表id");
                                                    childrenCol.setFieldType("varchar(255)");
                                                    childrenColList.add(childrenCol);
                                                }

                                                for (int z = 0; z < rowlistChildrens.size(); z++) {
                                                    childrenCol = new CustomTable();
                                                    JSONObject rowlistChildren = rowlistChildrens.getJSONObject(z);
                                                    //如果类型是文本、二维码、alert、超链接、按钮、颜色选择器、分割线、附件
                                                    if (rowlistChildren.getString("compType").equals("text")
                                                            || rowlistChildren.getString("compType").equals("barCode")
                                                            || rowlistChildren.getString("compType").equals("alert")
                                                            || rowlistChildren.getString("compType").equals("link")
                                                            || rowlistChildren.getString("compType").equals("button")
                                                            || rowlistChildren.getString("compType").equals("colorPicker")
                                                            || rowlistChildren.getString("compType").equals("divider")
                                                            || rowlistChildren.getString("compType").equals("upload"))
                                                        continue;
                                                    if (childrenMap.containsKey(rowlistChildren.getString("id"))) {
                                                        continue;
                                                    } else {
                                                        //类型如果是 input\select\checkbox、弹窗、联级选择
                                                        if (rowlistChildren.getString("compType").equals("input") ||
                                                                rowlistChildren.getString("compType").equals("select") ||
                                                                rowlistChildren.getString("compType").equals("checkbox")
                                                                || rowlistChildren.getString("compType").equals("dialogList")
                                                                || rowlistChildren.getString("compType").equals("cascader")
                                                        ) {
                                                            childrenCol.setChoose(true);
                                                            childrenCol.setCreateTableFiledName(rowlistChildren.getString("id"));
                                                            childrenCol.setComment(rowlistChildren.getString("label"));
                                                            childrenCol.setFieldType("varchar(255)");
                                                            childrenColList.add(childrenCol);
                                                            childrenChangeNum++;
                                                        }
                                                        //类型如果是 大文本、富文本
                                                        if (rowlistChildren.getString("compType").equals("textarea")
                                                                || rowlistChildren.getString("compType").equals("editor")) {
                                                            childrenCol.setChoose(true);
                                                            childrenCol.setCreateTableFiledName(rowlistChildren.getString("id"));
                                                            childrenCol.setComment(rowlistChildren.getString("label"));
                                                            childrenCol.setFieldType("text");
                                                            childrenColList.add(childrenCol);
                                                            childrenChangeNum++;
                                                        }
                                                        //如果类型是 日期、时间
                                                        if (rowlistChildren.getString("compType").equals("date")
                                                                || rowlistChildren.getString("compType").equals("time")) {
                                                            childrenCol.setChoose(true);
                                                            childrenCol.setCreateTableFiledName(rowlistChildren.getString("id"));
                                                            childrenCol.setComment(rowlistChildren.getString("label"));
                                                            childrenCol.setFieldType("varchar(100)");
                                                            childrenColList.add(childrenCol);
                                                            childrenChangeNum++;
                                                        }
                                                        //如果类型是 单选、评分、滑块、数字
                                                        if (rowlistChildren.getString("compType").equals("radio")
                                                                || rowlistChildren.getString("compType").equals("rate")
                                                                || rowlistChildren.getString("compType").equals("slider")
                                                                || rowlistChildren.getString("compType").equals("inputNumber")) {
                                                            childrenCol.setChoose(true);
                                                            childrenCol.setCreateTableFiledName(rowlistChildren.getString("id"));
                                                            childrenCol.setComment(rowlistChildren.getString("label"));
                                                            childrenCol.setFieldType("int(11)");
                                                            childrenColList.add(childrenCol);
                                                            childrenChangeNum++;
                                                        }
                                                        //如果类型是 开关
                                                        if (rowlistChildren.getString("compType").equals("Switch")) {
                                                            childrenCol.setChoose(true);
                                                            childrenCol.setCreateTableFiledName(rowlistChildren.getString("id"));
                                                            childrenCol.setComment(rowlistChildren.getString("label"));
                                                            childrenCol.setFieldType("varchar(50)");
                                                            childrenColList.add(customTable);
                                                            childrenChangeNum++;
                                                        }
                                                    }
                                                }
                                                if(childrenChangeNum>0) {
                                                    childrenTable.setCustomTables(childrenColList);
                                                    createTables.add(childrenTable);
                                                }
                                            }
                                        }
                                    }
                                    //类型如果是 大文本、富文本
                                    if (rowlist.getString("compType").equals("textarea")
                                            || rowlist.getString("compType").equals("editor")) {
                                        customTable.setChoose(true);
                                        customTable.setCreateTableFiledName(rowlist.getString("id"));
                                        customTable.setComment(rowlist.getString("label"));
                                        customTable.setFieldType("text");
                                        customTableList.add(customTable);
                                        changecustomTableNum++;
                                    }
                                    //如果类型是 日期、时间
                                    if (rowlist.getString("compType").equals("date")
                                            || rowlist.getString("compType").equals("time")) {
                                        customTable.setChoose(true);
                                        customTable.setCreateTableFiledName(rowlist.getString("id"));
                                        customTable.setComment(rowlist.getString("label"));
                                        customTable.setFieldType("varchar(100)");
                                        customTableList.add(customTable);
                                        changecustomTableNum++;
                                    }
                                    //如果类型是 单选、评分、滑块、数字
                                    if (rowlist.getString("compType").equals("radio")
                                            || rowlist.getString("compType").equals("rate")
                                            || rowlist.getString("compType").equals("slider")
                                            || rowlist.getString("compType").equals("inputNumber")) {
                                        customTable.setChoose(true);
                                        customTable.setCreateTableFiledName(rowlist.getString("id"));
                                        customTable.setComment(rowlist.getString("label"));
                                        customTable.setFieldType("int(11)");
                                        customTableList.add(customTable);
                                        changecustomTableNum++;
                                    }
                                    //如果类型是 开关
                                    if (rowlist.getString("compType").equals("Switch")) {
                                        customTable.setChoose(true);
                                        customTable.setCreateTableFiledName(rowlist.getString("id"));
                                        customTable.setComment(rowlist.getString("label"));
                                        customTable.setFieldType("varchar(50)");
                                        customTableList.add(customTable);
                                        changecustomTableNum++;
                                    }
                                }
                            }

                        }
                    }
                }
                if (hashMap.containsKey(list.getString("id"))) {
                    continue;
                } else {
                    //类型如果是 input\select\checkbox、弹窗、联级选择
                    if (list.getString("compType").equals("input") ||
                            list.getString("compType").equals("select") ||
                            list.getString("compType").equals("checkbox")
                            || list.getString("compType").equals("dialogList")
                            || list.getString("compType").equals("cascader")
                    ) {
                        customTable.setChoose(true);
                        customTable.setCreateTableFiledName(list.getString("id"));
                        customTable.setComment(list.getString("label"));
                        customTable.setFieldType("varchar(255)");
                        customTableList.add(customTable);
                        changecustomTableNum++;
                    }
                    //类型如果是 大文本、富文本
                    if (list.getString("compType").equals("textarea")
                            || list.getString("compType").equals("editor")) {
                        customTable.setChoose(true);
                        customTable.setCreateTableFiledName(list.getString("id"));
                        customTable.setComment(list.getString("label"));
                        customTable.setFieldType("text");
                        customTableList.add(customTable);
                        changecustomTableNum++;
                    }
                    //如果类型是 日期、时间
                    if (list.getString("compType").equals("date")
                            || list.getString("compType").equals("time")) {
                        customTable.setChoose(true);
                        customTable.setCreateTableFiledName(list.getString("id"));
                        customTable.setComment(list.getString("label"));
                        customTable.setFieldType("varchar(100)");
                        customTableList.add(customTable);
                        changecustomTableNum++;
                    }
                    //如果类型是 单选、评分、滑块、数字
                    if (list.getString("compType").equals("radio")
                            || list.getString("compType").equals("rate")
                            || list.getString("compType").equals("slider")
                            || list.getString("compType").equals("inputNumber")) {
                        customTable.setChoose(true);
                        customTable.setCreateTableFiledName(list.getString("id"));
                        customTable.setComment(list.getString("label"));
                        customTable.setFieldType("int(11)");
                        customTableList.add(customTable);
                        changecustomTableNum++;
                    }
                    // 如果是开关
                    if (list.getString("compType").equals("Switch")) {
                        customTable.setChoose(true);
                        customTable.setCreateTableFiledName(list.getString("id"));
                        customTable.setComment(list.getString("label"));
                        customTable.setFieldType("varchar(50)");
                        customTableList.add(customTable);
                        changecustomTableNum++;
                    }
                }
            }
            if(changecustomTableNum>0) {
                createTable.setCustomTables(customTableList);
                createTables.add(createTable);
            }
        }
        if(createTables.size()>0) {
            for (CreateTable cte : createTables) {
                int isTableExist=formEntityMapper.isTableExist(cte.getTableName(),dataSourceName);
                if(isTableExist>0) {
                    formEntityMapper.AlterTableAddField(cte);
                }
                else{
                    formEntityMapper.createFormTable(cte);
                }
            }
        }
    }
}
