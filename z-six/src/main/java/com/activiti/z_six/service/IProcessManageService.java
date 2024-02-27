package com.activiti.z_six.service;

import com.activiti.z_six.dto.controllerParams.StatAnalysisParams;
import com.activiti.z_six.entity.process.ProcessManage;

import java.util.HashMap;
import java.util.List;

public interface IProcessManageService {
    /**
     * 获取已经启动的流程实例
     * @param title
     * @param processKey
     * @param state
     * @return
     */
    public List<ProcessManage> getProcessManageList(String title, String processKey, String state);

    /**
     * 获取已经启动的流程实例的分页数据
     * @param title
     * @param flowName
     * @param state
     * @param page
     * @param pagesize
     * @return
     */
    HashMap<String,Object> getProcessManageListPage(String title, String flowName, String state,
                                              Integer page, Integer pagesize);

    /**
     * 获取流程实例补充参数
     * @param proce_inst_id
     * @return
     */
    HashMap<String,Object> getManageParams(String proce_inst_id,String business_key);

    /**
     * 获取所有任务分页
     * @param title
     * @param userid
     * @param page
     * @param pagesize
     * @return
     */

    HashMap<String,Object> getTaskManageListPage(String title, String userid, Integer page, Integer pagesize);

    /**
     * 获取流程统计分析
     * @param statAnalysisParams
     * @return
     */
    HashMap<String,Object> getStatAnalysisData(StatAnalysisParams statAnalysisParams);
    /**
     * 获取当前系统登陆人发起的流程列表
     * @param title
     * @param flowName
     * @param state
     * @param page
     * @param pagesize
     * @return
     */
    HashMap<String,Object> getMyStartListPage(String title, String flowName, String state,
                                                 Integer page, Integer pagesize);

    /**
     * 获取我审核的数据
     * @param title
     * @param flowName
     * @param state
     * @param page
     * @param pagesize
     * @return
     */
    HashMap<String,Object> getMyProcessPage(String title, String flowName, String state,
                                                     Integer page, Integer pagesize);
}
