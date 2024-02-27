package com.activiti.z_six.mapper.processMapper;

import com.activiti.z_six.entity.process.FlowSort;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface FlowSortMapper {
    /*
    获取流程分类分页信息
     */
    List<FlowSort> getFlowSort(Integer page, Integer pagesize);
    /*
    获取流程分类总数
     */
    FlowSort getFlowSortTotal();

    /**
     * 获取流程分类
     * @return
     */
    List<FlowSort> getFlowSortAll();
    /*
    新增
     */
    int addFlowSort(FlowSort flowSort);
    /*
    修改保存
     */
    int saveFlowSort(FlowSort flowSort);
    /*
    删除
     */
    int deleteFlowSort(FlowSort flowSort);
}
