package com.activiti.z_six.base.sql;

import java.util.Map;

public interface Sql {

    /**
     * 构建
     *
     * @return
     */
    Sql assemble();

    /**
     * 返回sql
     *
     * @return
     */
    default String getSql() {
        return assemble().getSql();
    }

    /**
     * 返回参数
     *
     * @return
     */
    default Map<String, Object> getParam() {
        return assemble().getParam();
    }

}
