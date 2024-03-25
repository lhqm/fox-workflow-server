package com.activiti.z_six.workflow.driver;

import com.activiti.z_six.util.HttpsUtils;
import com.activiti.z_six.workflow.model.FlowProcess;
import com.activiti.z_six.workflow.model.FormData;
import com.activiti.z_six.workflow.model.ProcessStart;
import com.activiti.z_six.workflow.model.SubmitData;
import com.alibaba.fastjson.JSONObject;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.List;

/**
 * 客户端流程驱动
 */
public class WorkflowJTNCDriver implements WorkflowDriver{
    @Value("${fox.workflow.url:http://localhost:8082/tenant/api/}")
    private String baseUrl;
    /**
     * 获取表单数据
     * @param processKey 流程key
     * @param taskId 任务id
     * @return 表单数据
     */
    @Override
    @SneakyThrows
    public FormData getFormData(String processKey, String taskId) {
        String res = HttpsUtils.get(baseUrl + "/getFormJson?processKey=" + processKey + "&taskId=" + taskId, null, null);
        String s = resultResolver(res);
        if (s != null && !"".equals(s)) {
            return JSONObject.parseObject(s, FormData.class);
        }
        return new FormData();
    }

    /**
     * 提交动态表单
     * @param formData 表单数据
     * @return 表单消费码
     */
    @Override
    @SneakyThrows
    public String submitForm(SubmitData formData) {
        String res = HttpsUtils.post(baseUrl + "/submitForm", null, JSONObject.toJSONString(formData));
        if (res != null && !"".equals(res)) {
            return resultResolver(res);
        }
        return null;
    }

    /**
     * 获取流程流转节点
     * @param processKey 流程key
     * @return 返回
     */

    @Override
    @SneakyThrows
    public List<FlowProcess> getFlowElementsByProcessKey(String processKey) {
        String res = HttpsUtils.get(baseUrl + "/getFormJson?processKey=" + processKey, null, null);
        if (res != null && !"".equals(res)) {
            return JSONObject.parseArray(resultResolver(res), FlowProcess.class);
        }
        return null;
    }

    /**
     * 启动流程
     * @param processStart 流程启动参数
     * @param userName 实例绑定用户（需要在流程系统里边有这个人）
     */

    @Override
    @SneakyThrows
    public void startProcess(ProcessStart processStart,String userName) {
        HashMap<String, String> map = new HashMap<>();
        map.put("userName",userName);
        String res = HttpsUtils.post(baseUrl + "/startProcess", map, JSONObject.toJSONString(userName));
        if (res != null && !"".equals(res)) {
            resultResolver(res);
        }
    }


    private static String resultResolver(String res) {
        JSONObject jsonObject;
        try {
            jsonObject = JSONObject.parseObject(res);
            if (jsonObject.getInteger("error") == 200) {
                return jsonObject.getString("result");
            }
        }catch (Exception e){
            throw new RuntimeException("请求失败，返回数据:"+res);
        }
        throw new RuntimeException(jsonObject.getString("result"));
    }
}
