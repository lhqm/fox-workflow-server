package com.activiti.z_six.mapper.processMapper;

import com.activiti.z_six.entity.process.ProcessEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface ProcessEntityMapper {
    int updateProcessEntity(ProcessEntity processEntity);
}
