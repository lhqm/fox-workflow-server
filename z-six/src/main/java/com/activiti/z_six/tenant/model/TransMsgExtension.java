package com.activiti.z_six.tenant.model;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransMsgExtension {
    /**
     * 加签或者转签源
     */
    private String from;
    /**
     * 加签或者转签目标
     */

    private String to;
    /**
     * 加签或者转签消息
     */
    private String msg;
    /**
     * 加签或者转签任务名称
     */
    private String taskName;

    public String getJsonString() {
        return "{" +
                "\"from\":" + "\"" + this.from + "\"," +
                "\"to\":" + "\"" + this.to + "\"," +
                "\"msg\":" + "\"" + this.msg + "\"," +
                "\"taskName\":" + "\"" + this.taskName + "\"" +
                "}";
    }
}
