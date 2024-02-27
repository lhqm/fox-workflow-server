package com.activiti.z_six.mapper.processMapper;

import com.activiti.z_six.entity.process.ProcessManage;
import com.activiti.z_six.entity.taskAssignee.GenerWork;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ProcessManageMapper {
    /**
     * 获取所有启动的流程实例
     * @param title
     * @param flowName
     * @param state
     * @return
     */
    List<ProcessManage> processManageList(@Param("title") String title,
                                          @Param("flowName")String flowName,
                                          @Param("state")String state);

    /**
     * 获取所有启动的流程实例-分页
     * @param title
     * @param flowName
     * @param state
     * @param page
     * @param pagesize
     * @return
     */
    List<ProcessManage> processManageListPage(@Param("title") String title,
                                              @Param("flowName")String flowName,
                                              @Param("state")String state,
                                              @Param("page")Integer page,
                                              @Param("pagesize")Integer pagesize);

    /**
     * 获取所有任务列表
     * @param title
     * @param userid
     * @return
     */
    List<GenerWork> getTaskManageList(@Param("title") String title,
                                      @Param("userid")String userid);

    /**
     * 获取所有任务列表分页
     * @param title
     * @param userid
     * @param page
     * @param pagesize
     * @return
     */
    List<GenerWork> getTaskManageListPage(@Param("title") String title,
                                              @Param("userid")String userid,
                                              @Param("page")Integer page,
                                              @Param("pagesize")Integer pagesize);

    /**
     * 根据任务状态获取任务数量
     * @param processKey
     * @param act_type
     * @return
     */
    List<GenerWork> getTaskManageByActType(@Param("processKey") String processKey,
                                           @Param("act_type")String act_type);

    /**
     * 获取我发起的流程列表
     * @param title
     * @param flowName
     * @param state
     * @param username
     * @return
     */
    List<ProcessManage> getMyStartList(@Param("title") String title,
                                       @Param("flowName")String flowName,
                                       @Param("state")String state,
                                       @Param("username") String username);

    /**
     * 获取我发起的流程列表分页
     * @param title
     * @param flowName
     * @param state
     * @param username
     * @param page
     * @param pagesize
     * @return
     */
    List<ProcessManage> getMyStartListPage(@Param("title") String title,
                                           @Param("flowName")String flowName,
                                           @Param("state")String state,
                                           @Param("username") String username,
                                           @Param("page")Integer page,
                                           @Param("pagesize")Integer pagesize);

    /**
     * 获取当前系统登陆人流程被拒绝数据
     * @param username
     * @return
     */
    List<ProcessManage> getMyStartRefuseList(@Param("username") String username);
    /**
     * 已审核列表
     * @param username
     * @param pageNum
     * @param pagesize
     * @param flowName
     * @return
     */
    List<GenerWork> getMyProcessPage(@Param("username")String username,
                                     @Param("state")String state,
                                @Param("title") String title,
                                @Param("pageNum")Integer pageNum,
                                @Param("pagesize")Integer pagesize,
                                @Param("flowName")String flowName);

    /**
     * 已审核数量
     * @param username
     * @param flowName
     * @return
     */
    List<GenerWork> getMyProcessList(@Param("username")String username,@Param("state")String state,
                                     @Param("flowName")String flowName,@Param("title") String title);
    /**
     * 获取流程被拒绝数据
     * @param username
     * @return
     */
    List<GenerWork> getMyProcessRefuse(@Param("username") String username);
}
