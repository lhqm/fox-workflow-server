package com.activiti.z_six.controller;

import com.activiti.z_six.aspectj.annotation.OperLog;
import com.activiti.z_six.aspectj.operate.LogConst;
import com.activiti.z_six.dto.controllerParams.DictDataParams;
import com.activiti.z_six.entity.dictData.DictDataEntity;
import com.activiti.z_six.service.IDictDataService;
import com.activiti.z_six.util.ResultRes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
@RestController
public class DictDataController {
    @Autowired
    private IDictDataService iDictDataService;

    @OperLog(operModul = "系统参数相关" , operType = LogConst.OTHER , operDesc = "获取字典列表")
    @PostMapping("/dicts/dictDataList")
    public ResultRes dictDataList(@RequestBody DictDataParams params){
        try{
            return ResultRes.success(iDictDataService.getDicts(params));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    @OperLog(operModul = "系统参数相关" , operType = LogConst.OTHER , operDesc = "获取字典数据")
    @PostMapping("/dicts/getDictDatas")
    public ResultRes getDictDatas(@RequestBody DictDataEntity dictDataEntity){
        try{
            return ResultRes.success(iDictDataService.getDictAndData(dictDataEntity));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    @OperLog(operModul = "系统参数相关" , operType = LogConst.OTHER , operDesc = "增加字典")
    @PostMapping("/dicts/addDict")
    public ResultRes addDict(@RequestBody DictDataEntity dictDataEntity){
        try{
            return ResultRes.success(iDictDataService.addDict(dictDataEntity));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    @OperLog(operModul = "系统参数相关" , operType = LogConst.OTHER , operDesc = "修改字典")
    @PostMapping("/dicts/updateDict")
    public ResultRes updateDict(@RequestBody DictDataEntity dictDataEntity){
        try{
            return ResultRes.success(iDictDataService.updateDict(dictDataEntity));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    @OperLog(operModul = "系统参数相关" , operType = LogConst.OTHER , operDesc = "删除字典")
    @PostMapping("/dicts/deleteDict")
    public ResultRes deleteDict(@RequestBody List<DictDataEntity> dictDataEntities){
        try{
            return ResultRes.success(iDictDataService.deleteDict(dictDataEntities));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
}
