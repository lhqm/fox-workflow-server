package com.activiti.z_six.mapper;

import com.activiti.z_six.entity.Attachment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface AttachmentMapper {
    /**
     * 数据
     * @param attachment
     * @return
     */
    Integer addAtt(Attachment attachment);
    int deleteAttByGenerWork(String genweWorkId,String moduleName);
}
