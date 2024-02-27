package com.activiti.z_six.service.impl;

import com.activiti.z_six.entity.Attachment;
import com.activiti.z_six.mapper.AttachmentMapper;
import com.activiti.z_six.service.IAttachmentService;
import com.activiti.z_six.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by jeeseli on 2022/8/26.
 */
@Service
@Slf4j
public class IAttachmentServiceImpl implements IAttachmentService {

    @Autowired
    private AttachmentMapper attachmentMapper;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveNameByGenwordId(String genworkId, String fileName, String filePath) throws Exception {

        Attachment attachment = new Attachment();
        attachment.setId(StringUtils.toString(RandomUtils.nextLong()));
        attachment.setGenworkId(genworkId);
        attachment.setName(fileName);
        attachment.setDelFlag("0");
        attachment.setPath(filePath);
        attachmentMapper.addAtt(attachment);
    }
}
