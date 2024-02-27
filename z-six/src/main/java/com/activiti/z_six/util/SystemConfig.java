package com.activiti.z_six.util;

public class SystemConfig {
    public enum ResponseCode {
        SUCCESS(200, "成功"),
        ERROR(500, "错误");

        private final int code;
        private final String desc;

        ResponseCode(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }
    public static boolean IsNullOrEmpty(Object object) {
        if (object == null || object.equals("") == true || object.equals("null") == true)
            return true;
        return false;
    }
}
