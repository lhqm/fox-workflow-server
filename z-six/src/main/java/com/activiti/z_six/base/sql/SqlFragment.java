package com.activiti.z_six.base.sql;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public interface SqlFragment {

    /**
     * 符号
     */
    String BLANK = "", STAR = "*", COMMA = ",", NOT_EQUAL = "!", SPACE = " ", QUOTE = "'", DOUBLE_QUOTES = "\"",
            POINT = ".", SEMICOLON = ";",
            PERCENT = "%", BACK_QUOTES = "`", LEFT_BRACE = "(", RIGHT_BRACE = ")";

    /**
     * sql片段
     */
    String
            SELECT = "SELECT", FROM = "FROM", WHERE = "WHERE",
            AND = "AND", OR = "OR",
            EQ = "=", NE = "<>", GT = ">", GE = ">=", LT = "<", LE = "<=",
            IN = "IN", NOT_IN = "NOT IN", NOT = "NOT", LIKE = "LIKE", NOT_LIKE = "NOT LIKE",
            IS_NULL = "IS NULL", IS_NOT_NULL = "IS NOT NULL",
            GROUP_BY = "GROUP BY", HAVING = "HAVING",
            ORDER_BY = "ORDER BY", ASC = "ASC", DESC = "DESC",
            EXISTS = "EXISTS",
            BETWEEN = "BETWEEN";

    enum NullCondition {
        IS_NULL("%s IS NULL ", 1),
        IS_NOT_NULL("%s IS NOT NULL ", 1),
        IS_BLANK("(%s IS NULL or %s = '') ", 2),
        IS_NOT_BLANK("(%s IS NOT NULL and %s != '') ", 2);

        private final String format;

        private final int size;

        NullCondition(String format, int size) {
            this.format = format;
            this.size = size;
        }

        public String format() {
            return format;
        }

        public int size() {
            return size;
        }
    }

    enum Condition {
        EQ(SqlFragment.EQ, "%s = %s "),
        NE(SqlFragment.NE, "%s <> %s "),
        GT(SqlFragment.GT, "%s > %s "),
        GE(SqlFragment.GE, "%s >= %s "),
        LT(SqlFragment.LT, "%s < %s "),
        LE(SqlFragment.LE, "%s <= %s ");

        private final String symbol;

        private final String format;

        Condition(String symbol, String format) {
            this.symbol = symbol;
            this.format = format;
        }

        public String symbol() {
            return symbol;
        }

        public String format() {
            return format;
        }
    }

    enum SetCondition {

        IN(SqlFragment.IN, "%s IN (%s) "),
        NOT_IN(SqlFragment.NOT_IN, "%s NOT IN (%s) ");

        private final String symbol;

        private final String format;

        SetCondition(String symbol, String format) {
            this.symbol = symbol;
            this.format = format;
        }

        public String symbol() {
            return symbol;
        }

        public String format() {
            return format;
        }
    }

    enum FuzzyCondition {
        LIKE(SqlFragment.LIKE, "%s LIKE %s "),
        NOT_LIKE(SqlFragment.NOT_LIKE, "%s NOT LIKE %s ");

        private final String symbol;

        private final String format;

        FuzzyCondition(String symbol, String format) {
            this.symbol = symbol;
            this.format = format;
        }

        public String symbol() {
            return symbol;
        }

        public String format() {
            return format;
        }
    }

    /**
     * 包裹
     *
     * @param str
     * @param wrapWith
     * @return
     */
    static String wrap(final String str, final String wrapWith) {
        return StringUtils.wrap(str, wrapWith);
    }

    /**
     * 包裹
     *
     * @param str
     * @param startWith
     * @param endWith
     * @return
     */
    static String wrap(final String str, final String startWith, final String endWith) {
        return startWith + str + endWith;
    }

    /**
     * 包裹单引号
     *
     * @param str
     * @return
     */
    static String wrapQuote(final String str) {
        return wrap(str, QUOTE);
    }

    /**
     * 包裹双引号
     *
     * @param str
     * @return
     */
    static String wrapDoubleQuotes(final String str) {
        return wrap(str, DOUBLE_QUOTES);
    }

    /**
     * 包裹小括号
     *
     * @param str
     * @return
     */
    static String wrapBrackets(final String str) {
        return LEFT_BRACE + str + RIGHT_BRACE;
    }

    /**
     * 包裹参数
     *
     * @param str
     * @return
     */
    static String wrapParam(final String str) {
        return "#{param." + str + "}";
    }

    /**
     * 包裹参数
     *
     * @param str
     * @return
     */
    static String wrapParam(final String str, Integer subscript) {
        return "#{param." + str + "[" + Objects.requireNonNull(subscript, "subscript") + "]}";
    }

    /**
     * 包裹参数
     *
     * @param str
     * @return
     */
    static String wrapIndexParam(final String str, Integer subscript) {
        return "#{param." + str + "[" + Objects.requireNonNull(subscript, "subscript") + "]}";
    }

    /**
     * 判断是否是一个数
     *
     * @param str
     * @return
     */
    static boolean isNumeric(final String str) {
        return Pattern.compile("^[-\\+]?[\\d]*$").matcher(str).matches();
    }

    static String concat(String... args) {
        if (args == null) {
            throw new NullPointerException();
        }
        return "CONCAT(" + Stream.of(args).filter(StringUtils::isNotBlank).reduce((a, b) -> a + "," + b).orElseThrow(NullPointerException::new) + ")";
    }

}
