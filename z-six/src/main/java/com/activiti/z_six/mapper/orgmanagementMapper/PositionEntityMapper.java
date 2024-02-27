package com.activiti.z_six.mapper.orgmanagementMapper;

import com.activiti.z_six.entity.orgmanagement.PositionEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@Repository
public interface PositionEntityMapper {
    /**
     * 获取岗位列表
     * @return
     */
    List<PositionEntity> positionList(@Param("name")String name);

    /**
     * 获取岗位列表--分页
     * @param  page
     * @param pagesize
     * @return
     */
    List<PositionEntity> positionListPage(@Param("name")String name,@Param("page")Integer page,@Param("pagesize")Integer pagesize);

    /**
     * 获取人员所有的岗位集合
     * @param username
     * @return
     */
    List<PositionEntity> positionListByUser(String username);
    /**
     * 获取岗位信息
     * @param id
     * @return
     */
    PositionEntity positionEntity(String id);

    /**
     * 增加岗位
     * @param positionEntity
     * @return
     */
    int addPosition(PositionEntity positionEntity);

    /**
     * 修改岗位
     * @param positionEntity
     * @return
     */
    int updatePosition(PositionEntity positionEntity);

    /**
     * 删除岗位
     * @param id
     * @return
     */
    int deletePosition(String id);

    /**
     * 删除人员的岗位
     * @param username
     * @return
     */
    int deletePositionUser(String username);
}
