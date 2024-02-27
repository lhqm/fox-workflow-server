package com.activiti.z_six.controller;

import com.activiti.z_six.aspectj.annotation.OperLog;
import com.activiti.z_six.aspectj.operate.LogConst;
import com.activiti.z_six.dto.controllerParams.ConditionDto;
import com.activiti.z_six.service.IFlowCondService;
import com.activiti.z_six.util.ResultRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/flowCond")
public class FlowCondController {
    @Autowired
    private IFlowCondService iFlowCondService;

    /**
     * 获取方向条件设置
     * @param id
     * @return
     */
    @OperLog(operModul = "流程定义相关" , operType = LogConst.OTHER , operDesc = "获取方向条件设置")
    @GetMapping("/getFlowCond")
    public ResultRes getFlowCond(@RequestParam("id") String id){
        return ResultRes.success(iFlowCondService.getFlowCond(id));
    }

    /**
     * 设置方向条件
     * @param param
     * @return
     */
    @OperLog(operModul = "流程定义相关" , operType = LogConst.OTHER , operDesc = "设置方向条件")
    @PostMapping("/setFlowCond")
    public ResultRes setFlowCond(@RequestBody ConditionDto param){
        return ResultRes.success(iFlowCondService.setFlowCond(param));
    }

    /**
     * 删除方向条件设置
     * @param param
     * @return
     */
    @OperLog(operModul = "流程定义相关" , operType = LogConst.OTHER , operDesc = "删除方向条件设置")
    @PostMapping("/deleteFlowCond")
    public ResultRes deleteFlowCond(@RequestBody ConditionDto param){
        return ResultRes.success(iFlowCondService.deleteFlowCond(param));
    }
}
