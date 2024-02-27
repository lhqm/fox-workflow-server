package com.activiti.z_six.mapper.orgmanagementMapper;

import com.activiti.z_six.entity.orgmanagement.CompanyEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@Repository
public interface CompanyEntityMapper {
    /**
     * 查询公司列表
     * @return
     */
    List<CompanyEntity> getCompanyList();

    /**
     * 增加公司
     * @param companyEntity
     * @return
     */
    int addCompany(CompanyEntity companyEntity);

    /**
     * 修改公司信息
     * @param companyEntity
     * @return
     */
    int updateCompany(CompanyEntity companyEntity);

    /**
     * 删除公司
     * @param companyEntity
     * @return
     */
    int deleteCompany(CompanyEntity companyEntity);
}
