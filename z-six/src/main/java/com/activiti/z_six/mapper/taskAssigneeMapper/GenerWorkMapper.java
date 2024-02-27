package com.activiti.z_six.mapper.taskAssigneeMapper;

import com.activiti.z_six.entity.taskAssignee.GenerWork;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@Repository
public interface GenerWorkMapper {
    /**
    获取工作列表
     */
    List<GenerWork> generWorkList(@Param("username")String username,
                                  @Param("pageNum")Integer pageNum,
                                  @Param("pagesize")Integer pagesize,
                                  @Param("flowName")String flowName);
    List<GenerWork> allGenerWork(@Param("username")String username,@Param("flowName")String flowName);
    List<GenerWork> generWorkNum(@Param("username")String username,@Param("flowName")String flowName);

    /**
     * 获取待办列表
     * @param username
     * @param pageNum
     * @param pagesize
     * @param flowName
     * @return
     */
    List<GenerWork> getTodoList(@Param("username")String username,
                                @Param("pageNum")Integer pageNum,
                                @Param("pagesize")Integer pagesize,
                                @Param("flowName")String flowName);

    /**
     * 获取待办数量
     * @param username
     * @param flowName
     * @return
     */
    List<GenerWork> getTodoListNum(@Param("username")String username,@Param("flowName")String flowName);

    /**
     * 已审核列表
     * @param username
     * @param pageNum
     * @param pagesize
     * @param flowName
     * @return
     */
    List<GenerWork> getDoneList(@Param("username")String username,
                                @Param("pageNum")Integer pageNum,
                                @Param("pagesize")Integer pagesize,
                                @Param("flowName")String flowName);

    /**
     * 已审核数量
     * @param username
     * @param flowName
     * @return
     */
    List<GenerWork> getDoneListNum(@Param("username")String username,@Param("flowName")String flowName);

    /**
     * 获取工作信息
     * @param id
     * @return
     */
    GenerWork generWork(@Param("id")String id);

    /**
     * 根据porc_inst_id获取数据
     * @param proce_inst_id
     * @return
     */
    GenerWork getGenerWorkByInst(@Param("proce_inst_id")String proce_inst_id);

    /**
     * 新增记录
     * @param generWork
     * @return
     */
    int insertGenerWork(GenerWork generWork);

    /**
     * 更新信息
     * @param generWork
     * @return
     */
    int updateGenerWork(GenerWork generWork);

    /**
     * 删除表单数据
     * @param ids
     * @return
     */
    int deleteGenerWork(@Param("ids") List<String> ids);
    /**
     * 删除主表数据
     * @param mainTable
     * @param ids
     * @return
     */
    int deleteFrmData(@Param("mainTable") String mainTable,
                      @Param("ids") List<String> ids);

    /**
     * 删除子表数据
     * @param mainTable
     * @param ids
     * @return
     */
    int deleteFrmSubData(@Param("mainTable") String mainTable,
                         @Param("ids") List<String> ids);
    /**
     * 关联流程
     * @param generWork
     * @return
     */
    int updateFlowWork(GenerWork generWork);

    /**
     * 设置标题
     * @param title
     * @param id
     * @return
     */
    int setGenerWorkTitle(@Param("title")String title,@Param("id")String id);
}
