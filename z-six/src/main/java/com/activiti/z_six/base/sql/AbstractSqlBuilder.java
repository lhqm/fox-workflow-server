package com.activiti.z_six.base.sql;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Stream;

public abstract class AbstractSqlBuilder implements SqlBuilder {

    protected StringBuilder sql = new StringBuilder();

    protected Map<String, Object> param = new HashMap<>();

    private final AtomicInteger index = new AtomicInteger();

    @Override
    public SqlBuilder putParam(String key, Object value) {
        param.put(key, value);
        return itself();
    }

    @Override
    public SqlBuilder putAllParam(Map<String, Object> param) {
        this.param.putAll(param);
        return itself();
    }

    @Override
    public SqlBuilder append(String str) {
        sql.append(str);
        return itself();
    }

    @Override
    public SqlBuilder append(String str, String key, Object value) {
        return append(str).param(key, value);
    }

    @Override
    public SqlBuilder append(SqlBuilder... sqlBuilders) {
        Stream.of(sqlBuilders).forEach(sqlBuilder -> {
            sql.append(sqlBuilder.getSql());
            param.putAll(sqlBuilder.getParam());
        });
        return itself();
    }

    @Override
    public SqlBuilder append(Consumer<SqlBuilder>... actions) {
        Stream.of(actions).forEach(var -> var.accept(itself()));
        return itself();
    }

    @Override
    public SqlBuilder param(String key, Object value) {
        final String paramKey = key + index.getAndIncrement();
        param.put(paramKey, value);
        return append(SqlFragment.wrapParam(paramKey));
    }

    @Override
    public SqlBuilder or() {
        sql.append(OR).append(SPACE);
        return itself();
    }

    @Override
    public SqlBuilder and() {
        sql.append(AND).append(SPACE);
        return itself();
    }

    @Override
    public SqlBuilder brackets(Consumer<SqlBuilder> action) {
        sql.append(LEFT_BRACE);
        action.accept(itself());
        sql.append(RIGHT_BRACE);
        return itself();
    }

    @Override
    public SqlBuilder between(Consumer<SqlBuilder> before, Consumer<SqlBuilder> after) {
        sql.append(BETWEEN).append(SPACE);
        before.accept(itself());
        and();
        after.accept(itself());
        return itself();
    }

    @Override
    public SqlBuilder concat(Consumer<SqlBuilder>... actions) {
        sql.append("CONCAT").append(LEFT_BRACE);
        actions[0].accept(itself());
        Stream.of(actions).skip(1).forEach(var -> {
            sql.append(COMMA);
            var.accept(itself());
        });
        sql.append(RIGHT_BRACE);
        return itself();
    }

    @Override
    public SqlBuilder deleteLastCharAt(String str) {
        sql.deleteCharAt(sql.lastIndexOf(str));
        return itself();
    }

    @Override
    public Sql assemble() {
        return itself();
    }

    @Override
    public String getSql() {
        return sql.toString();
    }

    @Override
    public Map<String, Object> getParam() {
        return param;
    }

    protected abstract SqlBuilder itself();

//    protected SqlBuilder nullCondition(Column column, NullCondition condition) {
//        String withTableAliasColumnName = withTableAliasColumnName(column);
//        String[] columnNames = new String[condition.size()];
//        for (int i = 0; i < columnNames.length; i++) {
//            columnNames[i] = withTableAliasColumnName;
//        }
//        sql.append(String.format(condition.format(), columnNames));
//        return itself();
//    }
//
//    protected SqlBuilder condition(Column column, Condition condition, Object value) {
//        String withTableAliasColumnName = withTableAliasColumnName(column);
//        String paramKey = withTableAliasColumnName + index.getAndIncrement();
//        sql.append(String.format(condition.format(), withTableAliasColumnName, SqlFragment.wrapParam(paramKey)));
//        param.put(paramKey, value);
//        return itself();
//    }
//
//    protected SqlBuilder setCondition(Column column, SetCondition condition, Object... values) {
//        String withTableAliasColumnName = withTableAliasColumnName(column);
//        String paramKey = withTableAliasColumnName + index.getAndIncrement();
//        AtomicInteger subscript = new AtomicInteger();
//        sql.append(String.format(condition.format(), Stream.of(values)
//                .map(var -> SqlFragment.wrapIndexParam(paramKey, subscript.getAndIncrement()))
//                .reduce((x, y) -> x + COMMA + y)
//                .get()));
//        param.put(paramKey, values);
//        return itself();
//    }
//
//    protected SqlBuilder fuzzyCondition(Column column, FuzzyCondition condition, String value) {
//        String withTableAliasColumnName = withTableAliasColumnName(column);
//        String paramKey = withTableAliasColumnName + index.getAndIncrement();
//        sql.append(condition.symbol()).append(SPACE);
//
//        param.put(paramKey, value);
//        return itself();
//    }
//
//    protected String checkTable(String tableName) {
//        if (!tables.containsKey(tableName)) {
//            throw new NullPointerException("未知表[" + tableName + "]");
//        }
//        return tableName;
//    }
//
//    protected Column checkColumn(Column column) {
//        if(!tables.get(checkTable(column.tableName())).contains(column.columnName())) {
//            throw new NullPointerException("未知字段[" + column.columnName() + POINT + column.columnName() + "]");
//        }
//        return column;
//    }
//
//    protected String tableAlias(String tableName) {
//        return tableAlias.computeIfAbsent(checkTable(tableName), var -> var + index.getAndIncrement());
//    }
//
//    protected String withTableAliasColumnName(Column column) {
//        return tableAlias(column.tableName()) + POINT + checkColumn(column);
//    }

}
