package com.activiti.z_six.controller;

import com.activiti.z_six.aspectj.annotation.OperLog;
import com.activiti.z_six.aspectj.operate.LogConst;
import com.activiti.z_six.dto.controllerParams.FormDataValue;
import com.activiti.z_six.dto.controllerParams.QueryParamDto;
import com.activiti.z_six.service.IFormValueService;
import com.activiti.z_six.util.ResultRes;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("/formData")
public class FormValueController {
    @Autowired
    private IFormValueService formValueService;
    /**
     * 查询表单数据集
     */
    @OperLog(operModul = "表单操作" , operType = LogConst.OTHER , operDesc = "根据表单id获取数据列表")
    @PostMapping({"/getDataById"})
    public ResultRes getFormData(@RequestBody QueryParamDto paramDto) throws Exception {
        return ResultRes.success(formValueService.getFormData(paramDto));
    }


    /**
     * 保存表单数据
     * @param formDataValue
     * @return
     */
    @OperLog(operModul = "表单操作" , operType = LogConst.UPDATE , operDesc = "保存表单数据")
    @PostMapping("/saveFormDataValue")
    public ResultRes saveFormDataValue(@RequestBody FormDataValue formDataValue) throws Exception {
        String msg=formValueService.saveFormValueByJson(formDataValue);
        if(!msg.equals("error"))
            return ResultRes.success(msg);
        else
            return ResultRes.error("数据写入失败");
    }
    /**
     * 获取表单应用数据
     * @param param
     * @return
     */
    @OperLog(operModul = "表单操作" , operType = LogConst.OTHER , operDesc = "获取表单应用数据")
    @PostMapping("/getApplicationData")
    public ResultRes getApplicationData(@RequestBody JSONObject param){
        return ResultRes.success(formValueService.getApplicationData(param));
    }
}
