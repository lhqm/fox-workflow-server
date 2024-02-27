package com.activiti.z_six.controller;

import com.activiti.z_six.aspectj.annotation.OperLog;
import com.activiti.z_six.aspectj.operate.LogConst;
import com.activiti.z_six.entity.menu.MenuEntity;
import com.activiti.z_six.service.IMenuEntityService;
import com.activiti.z_six.util.ResultRes;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
public class MenuController {
    @Autowired
    private IMenuEntityService iMenuEntityService;

    @OperLog(operModul = "系统参数相关" , operType = LogConst.DELETE , operDesc = "获取系统菜单")
    @PostMapping("/menu/getMenuList")
    public ResultRes getMenuList(@RequestBody JSONObject param){
        String menuName=param.getString("menuName");
        String status=param.getString("status");
        try{
            return ResultRes.success(iMenuEntityService.getMenuList(menuName,status));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    @OperLog(operModul = "系统参数相关" , operType = LogConst.DELETE , operDesc = "获取登录菜单")
    @GetMapping("/menu/getUserMenuList")
    public ResultRes getUserMenuList(){
        try{
            return ResultRes.success(iMenuEntityService.getUserMenuList());
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    @OperLog(operModul = "系统参数相关" , operType = LogConst.DELETE , operDesc = "获取角色菜单")
    @GetMapping("/menu/getRoleMenus")
    public ResultRes getRoleMenus(HttpServletResponse response,
                                     @RequestParam("id") String id){
        try{
            return ResultRes.success(iMenuEntityService.getRoleMenus(id));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    @OperLog(operModul = "系统参数相关" , operType = LogConst.DELETE , operDesc = "更新菜单")
    @PostMapping("/menu/updateMenu")
    public ResultRes updateMenu(@RequestBody MenuEntity menuEntity){
        try{
            return ResultRes.success(iMenuEntityService.updateMenu(menuEntity));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    @OperLog(operModul = "系统参数相关" , operType = LogConst.DELETE , operDesc = "增加菜单")
    @PostMapping("/menu/addMenu")
    public ResultRes addMenu(@RequestBody MenuEntity menuEntity){
        try{
            return ResultRes.success(iMenuEntityService.addMenu(menuEntity));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    @OperLog(operModul = "系统参数相关" , operType = LogConst.DELETE , operDesc = "删除菜单")
    @PostMapping("/menu/deleteMenu")
    public ResultRes deleteMenu(@RequestBody MenuEntity menuEntity){
        try{
            return ResultRes.success(iMenuEntityService.deleteMenu(menuEntity));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
}
