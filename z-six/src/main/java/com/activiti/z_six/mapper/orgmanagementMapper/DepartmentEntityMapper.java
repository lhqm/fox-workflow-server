package com.activiti.z_six.mapper.orgmanagementMapper;

import com.activiti.z_six.entity.orgmanagement.DepartmentEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface DepartmentEntityMapper {
    /**
    获取部门列表
     */
    List<DepartmentEntity> getDepartmentList(@Param("name") String name,
                                             @Param("companyid") String companyid);
    List<DepartmentEntity> getDepartmentList(@Param("parentId") String parentId,@Param("name") String name,
                                             @Param("companyid") String companyid);
    List<DepartmentEntity> getDepartmentPage(@Param("name") String name,@Param("companyid") String companyid,
                                             @Param("page") Integer page, @Param("pageSize") Integer pageSize);
    /**
     * 获取部门-分页
     * @param parentId
     * @param name
     * @param companyid
     * @param page
     * @param pageSize
     * @return
     */
    List<DepartmentEntity> getDepartmentPage(@Param("parentId") String parentId,
            @Param("name") String name,@Param("companyid") String companyid,
                                             @Param("page") Integer page, @Param("pageSize") Integer pageSize);
    /**
     * 获取公司下的部门
     * @param companyid
     * @return
     */
    List<DepartmentEntity> getDeptByCompany(Integer companyid);
    /**
    根据id获取部门信息
     */
    DepartmentEntity getDepartmentInfo(Integer id);

    /**
     * 增加部门
     * @param departmentEntity
     * @return
     */
    int addDepartment(DepartmentEntity departmentEntity);

    /**
     * 修改部门信息
     * @param departmentEntity
     * @return
     */
    int updateDepartment(DepartmentEntity departmentEntity);

    /**
     * 删除部门
     * @param departmentEntity
     * @return
     */
    int deleteDepartment(DepartmentEntity departmentEntity);
    /*
    设置部门负责人
     */
    int setDepartmentManager(DepartmentEntity departmentEntity);
    /*
    设置部门分管领导
     */
    int setDepartmentLeader(DepartmentEntity departmentEntity);
    /*
    设置所属公司
     */
    int updateDeptCompany(DepartmentEntity departmentEntity);
}
