package com.activiti.z_six.service.impl;

import com.activiti.z_six.dto.controllerParams.*;
import com.activiti.z_six.entity.process.FlowEntity;
import com.activiti.z_six.entity.process.FlowSort;

import com.activiti.z_six.entity.taskAssignee.FlowElementAttrs;
import com.activiti.z_six.entity.taskAssignee.OvProcessInstance;
import com.activiti.z_six.mapper.processMapper.FlowEntityMapper;
import com.activiti.z_six.mapper.processMapper.FlowSortMapper;
import com.activiti.z_six.mapper.taskAssigneeMapper.FlowElementAttrsMapper;
import com.activiti.z_six.mapper.taskAssigneeMapper.OvProcessInstanceMapper;
import com.activiti.z_six.security.RedisUtils;
import com.activiti.z_six.service.IProcessDefinitionService;
import com.activiti.z_six.service.manager.IProcessDefinitionManager;
import com.activiti.z_six.service.manager.MapperManager;
import com.activiti.z_six.util.SystemConfig;
import com.alibaba.fastjson.JSON;
import org.activiti.bpmn.model.*;
import org.activiti.bpmn.model.Process;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.Deployment;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IProcessDefinitionServiceImpl implements IProcessDefinitionService {
    @Autowired //流程部署服务
    private RepositoryService repositoryService;
    @Autowired
    private OvProcessInstanceMapper ovProcessInstanceMapper;
    @Autowired //流程分类服务
    private FlowSortMapper flowSortMapper;
    @Autowired //流程定义自定义服务
    private FlowEntityMapper flowEntityMapper;
    @Autowired
    private FlowElementAttrsMapper flowElementAttrsMapper;
    @Autowired
    private IProcessDefinitionManager iProcessDefinitionManager;
    @Autowired
    private MapperManager mapperManager;
    @Autowired
    private RedisUtils redisUtils;


    /**
     * 获取流程部署列表
     * @param page
     * @param pagesize
     * @param sortid
     * @param name
     * @return
     */
    @Override
    public HashMap<String ,Object> getDeployment(Integer page, Integer pagesize, String sortid, String name){
        int startIndex=(page-1)*pagesize;
        int maxIndex=startIndex+pagesize;

        //流程定义总数量
        List<ProcessDefinition> processDefinitionlist = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .list();

        List<HashMap<String, Object>> listMap= new ArrayList<HashMap<String, Object>>();
        List<ProcessDefinition> list = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .listPage(startIndex,maxIndex);

        list.sort((y,x)->x.getVersion()-y.getVersion());

        for (ProcessDefinition pd : list) {
            if(!SystemConfig.IsNullOrEmpty(sortid))
            {
                if(!sortid.equals(pd.getEngineVersion()))
                    continue;
            }
            HashMap<String, Object> hashMap = new HashMap<>();
            //流程定义id
            hashMap.put("procId", pd.getId());
            //流程定义名称
            hashMap.put("name", pd.getName());
            //流程key
            hashMap.put("procKey", pd.getKey());
            //resourceName
            hashMap.put("resourceName", pd.getResourceName());
            //流程部署ID
            hashMap.put("deploymentID", pd.getDeploymentId());
            hashMap.put("flowSort",pd.getEngineVersion());
            //版本
            hashMap.put("version", pd.getVersion());
            listMap.add(hashMap);
        }
        HashMap<String ,Object> hashMap=new HashMap<>();
        hashMap.put("list",listMap);
        hashMap.put("total",processDefinitionlist.size());
        return hashMap;
    }
    /**
     * 发布流程
     *
     * @param BPMNXml
     * @param tenantId
     * @return
     */
    @Override
    public String deployWithBPMNJS(String BPMNXml, String tenantId){
        Deployment deployment = repositoryService.createDeployment()
                .addString("createWithBPMNJS.bpmn",BPMNXml)
//                定义租户ID
                .tenantId((tenantId==null || tenantId.equals(""))?"main":tenantId)
                .deploy();

        iProcessDefinitionManager.createTask(deployment.getId());
        return deployment.getId();
    }
    /**
     * 流程属性
     * @param process_key
     * @return
     */
    @Override
    public ProcessDefinitionParams processProperties(String process_key){
        ProcessDefinitionParams params=new ProcessDefinitionParams();
        //获取流程模版信息
        OvProcessInstance ovProcessInstance=mapperManager.ovProcessInstance(process_key);
        if(ovProcessInstance==null){
            return iProcessDefinitionManager.getParams(process_key);
        }
        else {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(ovProcessInstance.getId_());
            //获取流程信息
            Process process = bpmnModel.getProcesses().get(0);
            //流程业务分类
            params.setSortid(ovProcessInstance.getEngine_version_());
            try {
                StartEvent startEvent = (StartEvent) process.getInitialFlowElement();
                List<SequenceFlow> outgoingFlows = startEvent.getOutgoingFlows();
                //获取UserTask类型数据
                UserTask userTask = (UserTask) outgoingFlows.get(0).getTargetFlowElement();
                //流程信息
                FlowElementAttrs flowElementAttrs = flowElementAttrsMapper.getFlowElementAttrs(userTask.getId());
                //标题生成规则
                params.setTitleModel(flowElementAttrs.getTitleModel());
            }
            catch (Exception ex){
                //标题生成规则
                params.setTitleModel("");
            }
        }
        return params;
    }

    /**
     * 保存流程属性
     * @param processDefinitionParams
     * @return
     */
    @Override
    public String saveProcessProperties(ProcessDefinitionParams processDefinitionParams){
        OvProcessInstance ovProcessInstance=Optional.ofNullable(ovProcessInstanceMapper
                        .getProcessInsLastVersion(processDefinitionParams.getKey()))
                .orElseGet(()->iProcessDefinitionManager
                        .saveOvProcessInstance(processDefinitionParams.getKey(), processDefinitionParams));
        if(ovProcessInstance!=null){
            iProcessDefinitionManager.updateProcessProperties(processDefinitionParams);
        }
        return "保存成功";
    }

    /**
     * 获取节点属性
     * @param task_def_key
     * @return
     */
    @Override
    public TaskDefinitionParams userTaskProperties(String task_def_key){
        TaskDefinitionParams taskDefinitionParams=new TaskDefinitionParams();
        //节点基本信息
        FlowElementAttrs taskElementAttrs=mapperManager.getFlowElementAttrs(task_def_key);
        if(taskElementAttrs==null){
            return iProcessDefinitionManager.getTaskDefinitionParams(task_def_key, null);
        }
        else {
            TaskModel taskModel = new TaskModel();
            BeanUtils.copyProperties(taskElementAttrs, taskModel);
            taskDefinitionParams.setFormModel(taskModel);
            taskDefinitionParams.setId(task_def_key);
            return taskDefinitionParams;
        }
    }

    /**
     * 保存节点属性
     * @param taskDefinitionParams
     * @return
     */
    @Override
    public String saveUserTaskProperties(TaskDefinitionParams taskDefinitionParams){
        //节点基本信息
        FlowElementAttrs taskElementAttrs=Optional.ofNullable(flowElementAttrsMapper
                .getFlowElementAttrs(taskDefinitionParams.getId()))
                .orElseGet(()->iProcessDefinitionManager
                        .saveFlowElementAttrs(taskDefinitionParams.getId(),taskDefinitionParams));
//        TODO:核心问题（修复改变任务节点的审核规则不起作用）
        /*为什么会存在配置了驳回规则和其他操作信息，但是却发生失效的问题。原因在于配置了taskParam获取走缓存，
        但是保存走的MySQL，两边数据不一样，最后被覆盖掉了
        实际设计上直接惰性加载即可，也不用在之后去另做修改*/
//        redisUtils.remove("UserTask_"+taskDefinitionParams.getId());
        if(taskElementAttrs!=null){
            BeanUtils.copyProperties(taskDefinitionParams.getFormModel(),taskElementAttrs);
            //节点基本信息
            TaskModel taskModel=taskDefinitionParams.getFormModel();
            BeanUtils.copyProperties(taskModel,taskElementAttrs);
            taskElementAttrs.setTask_def_key(taskDefinitionParams.getId());
            flowElementAttrsMapper.updateTaskAttrs(taskElementAttrs);
        }
        return "保存成功";
    }
    /**
     * 获取流程分类分页
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public HashMap<String ,Object> getFlowSort(Integer page, Integer pagesize){
        List<FlowSort> flowSortList= flowSortMapper.getFlowSort(page,pagesize);
        List<HashMap<String, Object>> listMap= new ArrayList<HashMap<String, Object>>();
        for (FlowSort fs : flowSortList) {
            HashMap<String, Object> hashMap = new HashMap<>();
            //流程定义id
            hashMap.put("Id", fs.getId());
            //流程定义名称
            hashMap.put("name", fs.getName());
            hashMap.put("parentid", fs.getParentId());
            listMap.add(hashMap);
        }

        HashMap<String ,Object> hashMap=new HashMap<>();
        hashMap.put("list",listMap);
        hashMap.put("total",flowSortMapper.getFlowSortTotal());

        return hashMap;
    }

    /**
     * 获取全部流程分类
     * @return
     */
    @Override
    public List<HashMap<String, Object>> getFlowSortAll(){
        List<FlowSort> flowSortList= flowSortMapper.getFlowSortAll();
        List<HashMap<String, Object>> listMap= new ArrayList<HashMap<String, Object>>();
        for (FlowSort fs : flowSortList) {
            HashMap<String, Object> hashMap = new HashMap<>();
            //流程定义id
            hashMap.put("Id", fs.getId());
            //流程定义名称
            hashMap.put("name", fs.getName());
            hashMap.put("parentid", fs.getParentId());
            listMap.add(hashMap);
        }

        return listMap;
    }

    /**
     * 获取全部流程分类树
     * @return
     */
    @Override
    public List<FlowSort> getFlowSortTree(){
        List<FlowSort> flowSortList= flowSortMapper.getFlowSortAll();
        //2、组装树形结构
        List<FlowSort> newList = flowSortList.stream()
                .filter(t -> t.getParentId() == 0)
                .map((menu) -> {
                    menu.setChildren(this.getChildren(menu,flowSortList));
                    return menu;
                })
                .collect(Collectors.toList());

        return newList;
    }

    /**
     * 获取流程发起列表
     * @return
     */
    @Override
    public HashMap getSatrtPageData(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        HashMap map=new HashMap<>();
        List<HashMap<String,Object>> mapList=new ArrayList<>();
        List<FlowSort> flowSortList= flowSortMapper.getFlowSortAll();

//        根据流程的key进行去重处理
        List<FlowEntity> commProceList=flowEntityMapper.getCommProceList(username)
                .stream()
                .filter(item->item.getProcessKey()!=null)
                .collect(Collectors.collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FlowEntity::getProcessKey))),
                        ArrayList::new
                ));

        for(FlowSort fs:flowSortList){
            HashMap<String,Object> hashMap=new HashMap<>();
            hashMap.put("id",fs.getId());
            hashMap.put("name",fs.getName());
//            对流程表单项目进行去重
            List<FlowEntity> dataList = flowEntityMapper.getFlowListBySort(fs.getId().toString());
            List<FlowEntity> flowEntities=new LinkedList<>();
            if (dataList!=null && dataList.size()>0){
                flowEntities=dataList
                        .stream()
                        .filter(item-> item.getProcessKey()!=null)
                        .collect(Collectors.collectingAndThen(
                                Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(FlowEntity::getProcessKey))),
                                ArrayList::new
                        ));
            }


            if(flowEntities.size()>0){
                hashMap.put("flowList",flowEntities);
            }
            else{
                hashMap.put("flowList",new ArrayList<>());
            }
            mapList.add(hashMap);
        }
        map.put("commProceList",commProceList);
        map.put("flowList",mapList);

        return map;
    }
    /**
     * 递归查找当前菜单的子菜单
     * @param root 单个对象
     * @param all 所有的集合
     * @return 排序后的子类
     */
    private List<FlowSort> getChildren(FlowSort root,List<FlowSort> all){
        List<FlowSort> childrenList = all.stream()
                .filter(t -> t.getParentId() == root.getId())
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
     * 新增流程类别
     * @param name
     * @param parentId
     * @return
     */
    @Override
    public String addFlowSort(String name, Long parentId){
        FlowSort flowSort=new FlowSort();
        flowSort.setName(name);
        flowSort.setParentId(parentId);
        int i=flowSortMapper.addFlowSort(flowSort);

        return "执行成功";
    }
    /**
     * 流程类别保存修改
     * @param id
     * @param name
     * @param parentId
     * @return
     */
    @Override
    public String saveFlowSort(Long id, String name, Long parentId){
        FlowSort flowSort=new FlowSort();
        flowSort.setId(id);
        flowSort.setName(name);
        flowSort.setParentId(parentId);
        int i=flowSortMapper.saveFlowSort(flowSort);

        return "执行成功";
    }

    /**
     * 删除流程定义
     * @param deploymentId
     * @return
     */
    @Override
    public String deleteDefinition(String deploymentId){
        repositoryService.deleteDeployment(deploymentId, true);
        return "执行成功";
    }
    /**
     * 删除流程分类
     * @param id
     * @param name
     * @param parentId
     * @return
     */
    @Override
    public String deleteFlowSort(Long id, String name, Long parentId){
        FlowSort flowSort=new FlowSort();
        flowSort.setId(id);
        flowSort.setName(name);
        flowSort.setParentId(parentId);
        int i=flowSortMapper.deleteFlowSort(flowSort);

        return "执行成功";
    }
}
