package com.activiti.z_six.service.impl;

import com.activiti.z_six.dto.controllerParams.StatAnalysisParams;
import com.activiti.z_six.entity.process.ProcessManage;
import com.activiti.z_six.entity.taskAssignee.FlowElementAttrs;
import com.activiti.z_six.entity.taskAssignee.GenerWork;
import com.activiti.z_six.entity.taskAssignee.OvTaskEntity;
import com.activiti.z_six.mapper.processMapper.ProcessManageMapper;
import com.activiti.z_six.mapper.taskAssigneeMapper.FlowElementAttrsMapper;
import com.activiti.z_six.mapper.taskAssigneeMapper.GenerWorkMapper;
import com.activiti.z_six.mapper.taskAssigneeMapper.OvTaskEntityMapper;
import com.activiti.z_six.service.IProcessManageService;
import com.activiti.z_six.service.manager.IProcessMManager;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service
public class IProcessManageServiceImpl implements IProcessManageService {
    @Autowired
    private ProcessManageMapper processManageMapper;
    @Autowired
    private IProcessMManager processMManager;
    @Autowired
    private GenerWorkMapper generWorkMapper;
    @Autowired
    private FlowElementAttrsMapper flowElementAttrsMapper;
    @Autowired //自定义
    private OvTaskEntityMapper ovTaskEntityMapper;

    @Override
    public List<ProcessManage> getProcessManageList(String title, String processKey, String state){
        return processManageMapper.processManageList(title,processKey,state);
    }
    /**
     * 获取已经启动的流程实例的分页数据
     * @param title
     * @param flowName
     * @param state
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public HashMap<String,Object> getProcessManageListPage(String title, String flowName, String state,
                                              Integer page, Integer pagesize){
        HashMap<String,Object> hashMap=new HashMap<>();
        List<ProcessManage> processManages=processManageMapper.processManageList(title,flowName,state);
        if(pagesize==0){
            hashMap.put("total",processManages.size());
            hashMap.put("list",processManages);
        }
        else{
            Integer startIndex=(page-1)*pagesize;
            List<ProcessManage> processManageList=processManageMapper.processManageListPage(title,flowName,state,startIndex,pagesize);

            hashMap.put("total",processManages.size());
            hashMap.put("list",processManageList);
        }
        return hashMap;
    }
    /**
     * 获取流程实例补充参数
     * @param proce_inst_id
     * @return
     */
    @Override
    public HashMap<String,Object> getManageParams(String proce_inst_id,String business_key){
        HashMap<String,Object> hashMap=new HashMap<>();
//        proce_inst_id=JSON.parseObject(business_key).getJSONObject("business_key").getString("proce_inst_id");
        List<OvTaskEntity> tasks = ovTaskEntityMapper.ovTaskEntityByProcessInsId(proce_inst_id);
        GenerWork generWork=generWorkMapper.getGenerWorkByInst(proce_inst_id);
        if(generWork==null)
            generWork=generWorkMapper.generWork(business_key);
        if(tasks.size()>0) {
            OvTaskEntity task = tasks.get(0);
            FlowElementAttrs flowElementAttrs=
                    flowElementAttrsMapper.getFlowElementAttrs(task.getTask_def_key_());
            hashMap.put("form_type",flowElementAttrs.getForm_type());
            hashMap.put("form_url",flowElementAttrs.getForm_url());
            hashMap.put("taskid",task.getId_());
            hashMap.put("task_def_name",task.getName_());
        }
        else{
            try{
                hashMap.put("form_type","0");
                hashMap.put("form_url","");
            }
            catch (Exception ex){
                hashMap.put("form_type","1");
                hashMap.put("form_url",generWork.getMap_json());
            }
//            System.out.println("步出try块\n\n\n\n\n\n\n\n\n\n\n\n\n\n");
            hashMap.put("taskid",'0');
//            System.out.println(1);
            hashMap.put("task_def_name","结束");
//            System.out.println(2);
        }
//        System.out.println(generWork.toString());
//        System.out.println(generWork.getMap_json());
        hashMap.put("map_json",generWork.getMap_json());
//        System.out.println(3);
        hashMap.put("data_json",generWork.getData_json());
//        System.out.println(4);
        return hashMap;
    }

    /**
     * 获取所有任务分页
     * @param title
     * @param userid
     * @param page
     * @param pagesize
     * @return
     */
    @Override
    public HashMap<String,Object> getTaskManageListPage(String title, String userid, Integer page, Integer pagesize){
        HashMap<String,Object> hashMap=new HashMap<>();
        List<GenerWork> generWorks=processManageMapper.getTaskManageList(title,userid);
        if(pagesize==0){
            hashMap.put("total",generWorks.size());
            hashMap.put("list",generWorks);
        }
        else{
            Integer startIndex=(page-1)*pagesize;
            List<GenerWork> generWorkList=processManageMapper.getTaskManageListPage(title,userid,startIndex,pagesize);
            hashMap.put("total",generWorks.size());
            hashMap.put("list",generWorkList);
        }
        return hashMap;
    }

    /**
     * 获取流程统计分析数据
     * @param statAnalysisParams
     * @return
     */
    @Override
    public HashMap<String,Object> getStatAnalysisData(StatAnalysisParams statAnalysisParams){
        HashMap<String,Object> hashMap=processMManager.getStatAnalysisData(statAnalysisParams);
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
    @Override
    public HashMap<String,Object> getMyStartListPage(String title, String flowName, String state,
                                                     Integer page, Integer pagesize){
        return processMManager.getMyStartList(title,flowName,state,page,pagesize);
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
    @Override
    public HashMap<String,Object> getMyProcessPage(String title, String flowName, String state,
                                                     Integer page, Integer pagesize){
        return processMManager.getMyProcessPage(title,flowName,state,page,pagesize);
    }
}
