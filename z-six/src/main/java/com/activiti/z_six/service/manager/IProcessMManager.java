package com.activiti.z_six.service.manager;

import com.activiti.z_six.dto.controllerParams.StatAnalysisParams;
import com.activiti.z_six.entity.process.ProcessManage;
import com.activiti.z_six.entity.taskAssignee.GenerWork;
import com.activiti.z_six.mapper.SqlMapper;
import com.activiti.z_six.mapper.processMapper.ProcessManageMapper;
import com.activiti.z_six.util.SystemConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.activiti.engine.RepositoryService;
import org.activiti.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class IProcessMManager {
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private ProcessManageMapper processManageMapper;
    @Autowired
    private SqlMapper sqlMapper;

    /**
     * 用户账号
     * @return
     */
    public final String getUserName(){
        //获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username=authentication.getName();
        return username;
    }

    /**
     * 获取流程统计图表数据
     * @param statAnalysisParams
     * @return
     */
    public HashMap<String,Object> getStatAnalysisData(StatAnalysisParams statAnalysisParams){
        HashMap<String,Object> hashMap=new HashMap<>();
        HashMap<String,Object> processPie=new HashMap<>();
        List<HashMap<String,Object>> processPies=new ArrayList<>();
        HashMap<String,Object> taskPie=new HashMap<>();
        List<HashMap<String,Object>> taskPies=new ArrayList<>();
        List<Map<String,Object>> proceTaskBar=new ArrayList<>();

        if(SystemConfig.IsNullOrEmpty(statAnalysisParams.getProceInstKey()))
            statAnalysisParams.setProceInstKey("all");
        if(SystemConfig.IsNullOrEmpty(statAnalysisParams.getActInstKey()))
            statAnalysisParams.setActInstKey("all");

        //流程定义数量
        List<ProcessDefinition> processDefinitionlist = repositoryService.createProcessDefinitionQuery()
                .latestVersion()
                .list();
        //流程实例数量
        List<ProcessManage> processManages=processManageMapper.processManageList("","all","");
        //流程任务数量
        List<GenerWork> generWorks=processManageMapper.getTaskManageList("","");
        //流程办结数量
        List<ProcessManage> completeProcess=processManageMapper.processManageList("",statAnalysisParams.getProceInstKey(),"1");
        //流程待审核数量
        List<ProcessManage> runningProcess=processManageMapper.processManageList("",statAnalysisParams.getProceInstKey(),"0");
        //驳回任务数量
        List<GenerWork> returnWorks=processManageMapper.getTaskManageByActType(statAnalysisParams.getActInstKey(),"returnWork");
        //移交任务数量
        List<GenerWork> transferWorks=processManageMapper.getTaskManageByActType(statAnalysisParams.getActInstKey(),"transfer");
        //加签任务数量
        List<GenerWork> countersignWorks=processManageMapper.getTaskManageByActType(statAnalysisParams.getActInstKey(),"countersign");
        //正常任务数量
        List<GenerWork> userTaskWorks=processManageMapper.getTaskManageByActType(statAnalysisParams.getActInstKey(),"userTask");

        hashMap.put("processDefinitions",processDefinitionlist.size());
        hashMap.put("processManages",processManages.size());
        hashMap.put("generWorks",generWorks.size());
        hashMap.put("completeProcess",completeProcess.size());

        processPie=new HashMap<>();
        processPie.put("value",completeProcess.size());
        processPie.put("name","已办结");
        processPies.add(processPie);
        processPie=new HashMap<>();
        processPie.put("value",runningProcess.size());
        processPie.put("name","未办结");
        processPies.add(processPie);

        taskPie=new HashMap<>();
        taskPie.put("value",userTaskWorks.size());
        taskPie.put("name","待审核");
        taskPies.add(taskPie);

        taskPie=new HashMap<>();
        taskPie.put("value",transferWorks.size());
        taskPie.put("name","移交");
        taskPies.add(taskPie);

        taskPie=new HashMap<>();
        taskPie.put("value",countersignWorks.size());
        taskPie.put("name","加签");
        taskPies.add(taskPie);

        taskPie=new HashMap<>();
        taskPie.put("value",returnWorks.size());
        taskPie.put("name","驳回");
        taskPies.add(taskPie);

        hashMap.put("processPies",processPies);
        hashMap.put("taskPies",taskPies);

        Map<String,Object> params=new HashMap<>();
        proceTaskBar=sqlMapper.list("select NAME_ AS name,COUNT(*) AS value from(select a.ID_,b.ACT_TYPE_,c.KEY_,c.NAME_ from act_ru_task a left join act_hi_actinst b " +
                "            on a.ID_=b.TASK_ID_ left join act_re_procdef c on a.PROC_DEF_ID_=c.ID_) T GROUP BY T.NAME_", params);

        hashMap.put("proceTaskBar",proceTaskBar);
        return hashMap;
    }

    /**
     * 获取当前系统登陆人发起的流程列表
     * @param title
     * @param flowName
     * @param state
     * @param page
     * @param pagesize
     * @return
     */
    public HashMap<String,Object> getMyStartList(String title, String flowName, String state,
                                                 Integer page, Integer pagesize){
        HashMap<String,Object> hashMap=new HashMap<>();
        HashMap<String,Object> pieMap=new HashMap<>();
        List<HashMap<String,Object>> pieData=new ArrayList<>();
        List<ProcessManage> processManages=processManageMapper.getMyStartList(title,flowName,state,
                this.getUserName());

        List<ProcessManage> RefuseList=processManageMapper.getMyStartRefuseList(this.getUserName());
        if(pagesize==0){
            hashMap.put("total",processManages.size());
            hashMap.put("list",processManages);
        }
        else{
            Integer startIndex=(page-1)*pagesize;
            List<ProcessManage> processManageList=processManageMapper.getMyStartListPage(title,flowName,state,
                    this.getUserName(),startIndex,pagesize);

            hashMap.put("total",processManages.size());
            hashMap.put("list",processManageList);
        }
        pieMap.put("name","审核通过");
        pieMap.put("value",(processManages.size()-RefuseList.size()));
        pieData.add(pieMap);

        pieMap=new HashMap<>();
        pieMap.put("name","被拒绝");
        pieMap.put("value",RefuseList.size());
        pieData.add(pieMap);

        hashMap.put("processManages",processManages.size()-RefuseList.size());
        hashMap.put("refuseList",RefuseList.size());
        hashMap.put("pieData",pieData);
        return hashMap;
    }

    /**
     * 获取我审核的数据
     * @param title
     * @param flowName
     * @param state
     * @param page
     * @param pagesize
     * @return
     */
    public HashMap<String,Object> getMyProcessPage(String title, String flowName, String state,
                                                 Integer page, Integer pagesize){
        HashMap<String,Object> hashMap=new HashMap<>();
        HashMap<String,Object> pieMap=new HashMap<>();
        List<HashMap<String,Object>> pieData=new ArrayList<>();
        List<GenerWork> processManages=processManageMapper.getMyProcessList(this.getUserName(),state,flowName,title);

        List<GenerWork> RefuseList=processManageMapper.getMyProcessRefuse(this.getUserName());
        if(pagesize==0){
            hashMap.put("total",processManages.size());
            hashMap.put("list",processManages);
        }
        else{
            Integer startIndex=(page-1)*pagesize;
            List<GenerWork> processManageList=processManageMapper.getMyProcessPage(this.getUserName(),state,title,startIndex,pagesize,flowName);

            hashMap.put("total",processManages.size());
            hashMap.put("list",processManageList);
        }
        pieMap.put("name","审核通过");
        pieMap.put("value",(processManages.size()-RefuseList.size()));
        pieData.add(pieMap);

        pieMap=new HashMap<>();
        pieMap.put("name","被拒绝");
        pieMap.put("value",RefuseList.size());
        pieData.add(pieMap);

        hashMap.put("processManages",processManages.size()-RefuseList.size());
        hashMap.put("refuseList",RefuseList.size());
        hashMap.put("pieData",pieData);
        return hashMap;
    }
}
