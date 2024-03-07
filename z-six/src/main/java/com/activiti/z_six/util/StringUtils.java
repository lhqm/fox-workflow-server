/**
 * Copyright &copy; 2012-2014 <a href="https://github.com/thinkgem/jeesite">JeeSite</a> All rights reserved.
 */
package com.activiti.z_six.util;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {

    private static final char SEPARATOR = '_';
    private static final String CHARSET_NAME = "UTF-8";

    //匹配数字正则
    private static final Pattern PATTERN_NUM = Pattern.compile("[0-9]{1,}");
    /**
     * 匹配非数字
     */
    private static final  Pattern PATTERN_NOT_NUM = Pattern.compile("[^0-9]+");

    /**
     * 转换为字节数组
     *
     * @param str
     * @return
     */
    public static byte[] getBytes(String str) {
        if (str != null) {
            try {
                return str.getBytes(CHARSET_NAME);
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 转换为字节数组
     *
     * @param bytes
     * @return
     */
    public static String toString(byte[] bytes) {
        try {
            return new String(bytes, CHARSET_NAME);
        } catch (UnsupportedEncodingException e) {
            return EMPTY;
        }
    }

    /**
     * 是否包含字符串
     *
     * @param str  验证字符串
     * @param strs 字符串组
     * @return 包含返回true
     */
    public static boolean inString(String str, String... strs) {
        if (str != null) {
            for (String s : strs) {
                if (str.equals(trim(s))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 替换掉HTML标签方法
     */
    public static String replaceHtml(String html) {
        if (isBlank(html)) {
            return "";
        }
        String regEx = "<.+?>";
        Pattern p = Pattern.compile(regEx);
        Matcher m = p.matcher(html);
        String s = m.replaceAll("");
        return s;
    }

    /**
     * 替换为手机识别的HTML，去掉样式及属性，保留回车。
     *
     * @param html
     * @return
     */
    public static String replaceMobileHtml(String html) {
        if (html == null) {
            return "";
        }
        return html.replaceAll("<([a-z]+?)\\s+?.*?>", "<$1>");
    }


    /**
     * 去除掉字符串尾部的字符，比如说","
     *
     * @param str
     * @return
     */
    public static String trimEnd(String str, String tail) {
        if (tail.length() >= str.length()) {
            return str;
        }
        String teml = str.substring(str.length() - tail.length(), str.length());
        if (tail.equals(teml)) {
            return trimEnd(str.substring(0, str.length() - tail.length()), tail);
        } else {
            return str;
        }
    }

    /**
     * 转换为Double类型
     */
    public static Double toDouble(Object val) {
        if (val == null || "".equals(val) || "null".equals(val)) {
            return 0D;
        }
        try {
            return Double.valueOf(trim(val.toString()));
        } catch (Exception e) {
            return 0D;
        }
    }

    /**
     * 转换为Float类型
     */
    public static Float toFloat(Object val) {
        return toDouble(val).floatValue();
    }

    /**
     * 转换为Long类型
     */
    public static Long toLong(Object val) {
        return toDouble(val).longValue();
    }

    /**
     * 转换为String类型
     */
    public static String toString(Object val) {
        if (val == null || "null".equals(val)) {
            return "";
        }
        return val.toString();
    }

    /**
     * 转换为Integer类型
     */
    public static Integer toInteger(Object val) {
        return toLong(val).intValue();
    }

    /**
     * 获得i18n字符串

     public static String getMessage(String code, Object[] args) {
     LocaleResolver localLocaleResolver = (LocaleResolver) SpringContextHolder.getBean(LocaleResolver.class);
     HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.getRequestAttributes()).getRequest();
     Locale localLocale = localLocaleResolver.resolveLocale(request);
     return SpringContextHolder.getApplicationContext().getMessage(code, args, localLocale);
     }*/

    /**
     * 获得用户远程地址
     */
    public static String getRemoteAddr(HttpServletRequest request) {
        String remoteAddr = request.getHeader("X-Real-IP");
        if (isNotBlank(remoteAddr)) {
            remoteAddr = request.getHeader("X-Forwarded-For");
        } else if (isNotBlank(remoteAddr)) {
            remoteAddr = request.getHeader("Proxy-Client-IP");
        } else if (isNotBlank(remoteAddr)) {
            remoteAddr = request.getHeader("WL-Proxy-Client-IP");
        }
        return remoteAddr != null ? remoteAddr : request.getRemoteAddr();
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase(" hello_world ") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }

        s = s.toLowerCase();

        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == SEPARATOR) {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }

        return sb.toString();
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase(" hello_world ") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toCapitalizeCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = toCamelCase(s);
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    /**
     * 驼峰命名法工具
     *
     * @return toCamelCase(" hello_world ") == "helloWorld"
     * toCapitalizeCamelCase("hello_world") == "HelloWorld"
     * toUnderScoreCase("helloWorld") = "hello_world"
     */
    public static String toUnderScoreCase(String s) {
        if (s == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder();
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            boolean nextUpperCase = true;

            if (i < (s.length() - 1)) {
                nextUpperCase = Character.isUpperCase(s.charAt(i + 1));
            }

            if ((i > 0) && Character.isUpperCase(c)) {
                if (!upperCase || !nextUpperCase) {
                    sb.append(SEPARATOR);
                }
                upperCase = true;
            } else {
                upperCase = false;
            }

            sb.append(Character.toLowerCase(c));
        }

        return sb.toString();
    }


    /**
     * 转换为JS获取对象值，生成三目运算返回结果
     *
     * @param objectString 对象串
     *                     例如：row.user.id
     *                     返回：!row?'':!row.user?'':!row.user.id?'':row.user.id
     */
    public static String jsGetVal(String objectString) {
        StringBuilder result = new StringBuilder();
        StringBuilder val = new StringBuilder();
        String[] vals = split(objectString, ".");
        for (int i = 0; i < vals.length; i++) {
            val.append("." + vals[i]);
            result.append("!" + (val.substring(1)) + "?'':");
        }
        result.append(val.substring(1));
        return result.toString();
    }

    /***
     * 首字母转换成大写
     * @param str
     * @return
     */
    public static String toUpperCase(String str) {
        return str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toUpperCase());
    }

    /***
     * 首字母转成小写
     * @param str
     * @return
     */
    public static String toLowerCase(String str) {
        return str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toLowerCase());
    }

    /***
     * 判断子字符串是不是为空
     * @param str
     * @return
     */
    public static boolean isNull(String str) {
        return null == str || "".equals(str);
    }

    /**
     * * 判断一个对象是否为空
     *
     * @param object Object
     * @return true：为空 false：非空
     */
    public static boolean isNull(Object object)
    {
        return object == null;
    }

    /**
     * 去掉 字符串尾部的/
     *
     * @param str
     * @return 不以/结尾的新字符创
     */
    public static String dropStr(String str) {
        String teml = str.substring(str.length() - 1, str.length());
        if ("/".equals(teml)) {//是以/结尾 就
            return dropStr(str.substring(0, str.length() - 1));
        } else {
            return str;
        }

    }

    /**
     * 生成随机数字验证码
     *
     * @param length
     * @return
     */
    public static String getRandomNum(int length) { //length表示生成字符串的长度
        String base = "6482570319";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * double 乘法
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double mul(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.multiply(bd2).doubleValue();
    }


    /**
     * double 相加
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double sum(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.add(bd2).doubleValue();
    }


    /**
     * double 相减
     *
     * @param d1
     * @param d2
     * @return
     */
    public static double sub(double d1, double d2) {
        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.subtract(bd2).doubleValue();
    }

	/*public static void main(String[] args) {
		Double a2 = 0.1;
		Double a1 = 0.3;
		Double as = StringUtils.sub(a2,a1);
		System.out.println(as*100);
	}*/

    /**
     * double 除法
     *
     * @param d1
     * @param d2
     * @param scale 四舍五入 小数点位数
     * @return
     */
    public static double div(double d1, double d2, int scale) {
        //  当然在此之前，你要判断分母是否为0，
        //  为0你可以根据实际需求做相应的处理

        BigDecimal bd1 = new BigDecimal(Double.toString(d1));
        BigDecimal bd2 = new BigDecimal(Double.toString(d2));
        return bd1.divide
                (bd2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    // 判断一个字符串是否都为数字
    public static boolean isDigit(String strNum) {
        Matcher matcher = PATTERN_NUM.matcher((CharSequence) strNum);
        return matcher.matches();
    }

    /***
     * 防止sql注入
     * @param str
     * @return
     */
    public static boolean sqlInj(String str) {
        String injStr = "':and:exec:insert:select:delete:update:count:*:%:chr:mid:master:truncate:char:declare:;:or:-:+:,";
        String[] injStra = injStr.split(":");
        for (int i = 0; i < injStra.length; i++) {
            if (str.indexOf(injStra[i]) != -1) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取真正的字符串值,将字符串null置为真正的null
     *
     * @param str
     * @return
     */
    public static String getRelString(String str) {
        if (StringUtils.isNotBlank(str) && "null".equals(str)) {
            str = null;
        }
        return str;
    }

    /**
     * 获取真正的字符串值,将字符串null置为真正的null
     *
     * @param username
     * @param id
     * @return
     */
    public static String getUserNameIDHash(String username, String id) {
        StringBuffer userInfo = new StringBuffer();
        userInfo.append(id).append(username);
        return sha1IdAndName(userInfo.toString());
    }

    /**
     * 对字符串进行散列, 支持md5与sha1算法.
     */
    public static String sha1IdAndName(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-1");


            byte[] result = digest.digest(input.getBytes());

            return Base64.getEncoder().encodeToString(result);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 去除指定字符串中的 中英文空格，tab，回车
     *
     * @param str
     * @return
     */
    public static String genValidInput(String str) {
        if (null != str) {
            String regex = "\\s+";
            str = str.replaceAll(regex, "");
        }
        return str;
    }

    public static boolean checkValidInput(String str) {
        String regex = ".*\\s.*";
        if (null != str && str.matches(regex)) {
            return false;
        }
        return true;
    }

    /**
     * 格式化数字不足位数的前边补齐0
     *
     * @param num    数字
     * @param length 想要的数字长度
     * @return
     */
    public static String formatNo(Integer num, Integer length) {

        String rs = "";
        Integer numLength = num.toString().length();
        if (numLength >= length) {
            rs += num;
            return rs.substring(0, length);
        }

        for (int i = numLength; i < length; i++) {
            rs += "0";
        }
        return rs + num;
    }

    public static String subStrByLastStr(String str, String lastStr) {
        if (isNotBlank(str)) {
            int index = str.lastIndexOf(lastStr);
            if (-1 != index) {
                str = str.substring(0, index);
            }
        }
        return str;
    }

    /**
     * 保留两位小数，四舍五入的一个老土的方法
     *
     * @param d
     * @return
     */
    public static Double formatDouble(double d) {
        return (double) Math.round(d * 100);
    }
    /**
	 * 生成订单号
	 * 订单编号生成规则：DD+订单类型编码首字母+时间戳（秒）+4位随机数。
	 * @param orderType 订单类型编码 R 数据报告 L 紧缺资源
	 * @return
	 * @throws Exception
	 */

	/**
	 * @param typeStr 编码类型
	 * @return
	 * @throws Exception
	 */
    public static String generateNo(String typeStr){

		StringBuffer orderNo = new StringBuffer().append(typeStr);
		//获取时间戳
//		Long time = System.currentTimeMillis();
		String timeStr = DateUtils.formatDate(new Date(),"yyyyMMddHHmmss");
		orderNo.append(timeStr);
		//增加4位随机数
		orderNo.append(getRandomNum(4));
		return orderNo.toString();

	}

    public static String generateNo(){
        return generateNo("");
    }

    /**
     * 获取URL HOST
     * @param link
     * @return
     */
    public static String getHost(String link) {
        URL url;
        String host = "";
        try {
            url = new URL(link);
            host = url.getHost();
        } catch (MalformedURLException e) {
        }
        return host;
    }

    /**
     *  根据索引获取字母
     * @param index
     * @return
     */
    public static String getLetter(int index){
        return String.valueOf((char) (96 + index)).toUpperCase();
    }

    /**
     * 获取字符串中的所有数字
     * @param str
     * @return
     */
    public static List<String> getNums(String str){
        /**
         * 获取字符
         */
        String[] numArr = PATTERN_NOT_NUM.split(str.trim());
        List<String> list = new ArrayList<>();
        if (numArr.length > 0 ){
            for (String res:numArr){
                if (StringUtils.isNotBlank(res)){
                    list.add(res);
                }
            }
        }
        return  list;

    }

    /**
     * 获取字符串最后的数字
     * @param str
     * @return
     */
    public static String getLastNum(String str){
        List<String> list = getNums(str);
        if (null != list && list.size()>0){
            return list.get(list.size()-1);
        }
        return "0";
    }

    /***
     * 通过全限定类名称获取类的直接名称
     * @param fullClassName 全限定名称
     * @return 类名
     */
    public static String getClassNameByFullClassName(String fullClassName){
        //通过ASCII码来切割点号。
        int index = fullClassName.lastIndexOf(46);
//        如果没命中直接返回，否则返回全限定名称转换后的名称
        return index==-1?fullClassName:fullClassName.substring(index+1);
    }
}
