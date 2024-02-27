package com.activiti.z_six.operLog.entity.base;

public interface BaseLongEntity {
    void preInsert(Long createBy);

    void preInsert();

    void preDelete(Long deleteBy);

    void preDelete();

    void preUpdate(Long updateBy);

    void preUpdate();
}
