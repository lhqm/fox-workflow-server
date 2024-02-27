package com.activiti.z_six.mapper.taskAssigneeMapper;

import com.activiti.z_six.entity.taskAssignee.SmsEntity;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
@Repository
public interface SmsEntityMapper {
    /**
     * 获取流程通知列表
     * @param toUser
     * @param pageNum
     * @param pagesize
     * @param status
     * @return
     */
    List<SmsEntity> smsList(@Param("toUser")String toUser,@Param("pageNum")Integer pageNum,
                            @Param("pagesize")Integer pagesize,@Param("status")String status);

    /**
     * 获取流程通知数量
     * @param toUser
     * @param status
     * @return
     */
    List<SmsEntity> smsListNum(@Param("toUser")String toUser,@Param("status")String status);
    /**
     * 获取流程知会列表
     * @param toUser
     * @param pageNum
     * @param pagesize
     * @param status
     * @return
     */
    List<SmsEntity> ccSmsList(@Param("toUser")String toUser,@Param("pageNum")Integer pageNum,
                            @Param("pagesize")Integer pagesize,@Param("status")String status);

    /**
     * 获取流程知会数量
     * @param toUser
     * @param status
     * @return
     */
    List<SmsEntity> ccSmsListNum(@Param("toUser")String toUser,@Param("status")String status);
    SmsEntity smsEntity(@Param("id")String id);

    /**
     * 增加消息
     * @param smsEntity
     * @return
     */
    int addSms(SmsEntity smsEntity);

    /**
     * 设置消息已读
     * @param id
     * @return
     */
    int updateState(@Param("id")String id);

    /**
     * 删除消息
     * @param id
     * @return
     */
    int deleteSms(@Param("id")String id);
}
