package com.activiti.z_six.base.sql;

import java.util.Map;
import java.util.function.Consumer;

public interface SqlBuilder extends Sql, SqlFragment {

    SqlBuilder putParam(String key, Object value);

    SqlBuilder putAllParam(Map<String, Object> param);

    SqlBuilder append(String sqlFragment);

    SqlBuilder append(String sqlFragment, String key, Object value);

    SqlBuilder append(SqlBuilder... sqlBuilders);

    SqlBuilder append(Consumer<SqlBuilder>... actions);

    SqlBuilder param(String key, Object value);

    SqlBuilder or();

    SqlBuilder and();

    SqlBuilder brackets(Consumer<SqlBuilder> action);

    SqlBuilder between(Consumer<SqlBuilder> before, Consumer<SqlBuilder> after);

    SqlBuilder concat(Consumer<SqlBuilder>... actions);

    SqlBuilder deleteLastCharAt(String sqlFragment);
}
