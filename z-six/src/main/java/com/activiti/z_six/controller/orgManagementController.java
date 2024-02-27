package com.activiti.z_six.controller;

import com.activiti.z_six.aspectj.annotation.OperLog;
import com.activiti.z_six.aspectj.operate.LogConst;
import com.activiti.z_six.dto.controllerParams.GroupUsersParams;
import com.activiti.z_six.dto.controllerParams.UserParams;
import com.activiti.z_six.dto.orgDto.SysRoleDto;
import com.activiti.z_six.entity.orgmanagement.GroupEntity;
import com.activiti.z_six.entity.orgmanagement.UserEntity;
import com.activiti.z_six.service.IOrgManagementService;
import com.activiti.z_six.util.ResultRes;
import com.activiti.z_six.util.SystemConfig;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

@RestController
public class orgManagementController {
    @Autowired
    private IOrgManagementService orgManagementService;

    /***
     * 获取公司列表
     * @return
     */
    @OperLog(operModul = "组织结构-公司" , operType = LogConst.OTHER , operDesc = "获取公司列表")
    @GetMapping("/orgm/getCompanyList")
    public ResultRes getCompanyList(){
        try {
            return ResultRes.success(orgManagementService.getCompanyList());
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 增加公司
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-公司" , operType = LogConst.INSERT , operDesc = "增加公司")
    @PostMapping(value = "/orgm/addCompany")
    public ResultRes addCompany(@RequestBody JSONObject param){
        String name=param.getString("name");
        Integer parentid=param.getInteger("parentid");
        try {
            return ResultRes.success(orgManagementService.addCompany(name, parentid));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 修改公司信息
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-公司" , operType = LogConst.UPDATE , operDesc = "修改公司信息")
    @PostMapping(value = "/orgm/updateCompany")
    public ResultRes updateCompany(@RequestBody JSONObject param){
        Integer id=param.getInteger("id");
        String name=param.getString("name");
        Integer parentid=param.getInteger("parentid");
        try{
            return ResultRes.success(orgManagementService.updateCompany(id,name,parentid));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 删除公司信息
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-公司" , operType = LogConst.DELETE , operDesc = "删除公司")
    @PostMapping(value = "/orgm/deleteCompany")
    public ResultRes deleteCompany(@RequestBody JSONObject param){
        Integer id=param.getInteger("id");
        String name=param.getString("name");
        Integer parentid=param.getInteger("parentid");
        try {
            return ResultRes.success(orgManagementService.deleteCompany(id, name, parentid));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 获取部门列表
     * @return
     */
    @OperLog(operModul = "组织结构-部门" , operType = LogConst.OTHER , operDesc = "获取部门列表")
    @PostMapping("/orgm/getDepartmentList")
    public ResultRes getDepartmentList(@RequestBody JSONObject param){
        String name=param.getString("name");
        String companyid=param.getString("companyid");
        if(SystemConfig.IsNullOrEmpty(companyid)){
            companyid="all";
        }
        Integer page=param.getInteger("page");
        Integer pageSize=param.getInteger("pagesize");
        try {
            return ResultRes.success(orgManagementService.getDepartmentList(name, companyid, page, pageSize));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 获取部门树
     * @return
     */
    @OperLog(operModul = "组织结构-部门" , operType = LogConst.OTHER , operDesc = "获取部门树")
    @GetMapping("/orgm/getDepartmentEntityList")
    public ResultRes getDepartmentEntityList(){
        return ResultRes.success(orgManagementService.getDepartmentEntityList());
    }

    /**
     * 获取部门人员
     * @return
     */
    @OperLog(operModul = "组织结构-部门" , operType = LogConst.OTHER , operDesc = "获取部门下属人员")
    @PostMapping("/orgm/getDeptUsers")
    public ResultRes getDeptUsers(@RequestBody JSONObject param){
        Integer id=param.getInteger("id");
        Integer page=param.getInteger("page");
        Integer pageSize=param.getInteger("pagesize");
        try {
            return ResultRes.success(orgManagementService.getDeptUsers(id,page,pageSize));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 获取人员树
     * @return
     */
    @OperLog(operModul = "组织结构-部门" , operType = LogConst.OTHER , operDesc = "获取部门人员树型数据")
    @GetMapping("/orgm/getDeptUserTree")
    public ResultRes getDeptUserTree(){
        try {
            return ResultRes.success(orgManagementService.getDeptUserTree());
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 获取公司下的部门
     * @param companyId
     * @return
     */
    @OperLog(operModul = "组织结构-公司" , operType = LogConst.OTHER , operDesc = "获取公司下的部门列表")
    @GetMapping("/orgm/getDeptByCompany")
    public ResultRes getDeptByCompany(@RequestParam("companyId") Integer companyId){
        try {
            return ResultRes.success(orgManagementService.getDeptByCompany(companyId));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }
    /**
     * 增加部门
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-部门" , operType = LogConst.INSERT , operDesc = "增加部门")
    @PostMapping(value = "/orgm/addDepartment")
    public ResultRes addDepartment(@RequestBody JSONObject param){
        String name=param.getString("name");
        Integer parentid=param.getInteger("parentid");
        String manager=param.getString("manager");
        String leader=param.getString("leader");
        Integer companyid=param.getInteger("companyid");
        try {
            return ResultRes.success(orgManagementService.addDepartment(name,parentid,manager,leader,companyid));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 修改部门
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-部门" , operType = LogConst.UPDATE , operDesc = "修改部门信息")
    @PostMapping(value = "/orgm/updateDepartment")
    public ResultRes updateDepartment(@RequestBody JSONObject param){
        Integer id=param.getInteger("id");
        String name=param.getString("name");
        Integer parentid=param.getInteger("parentid");
        String manager=param.getString("manager");
        String leader=param.getString("leader");
        Integer companyid=param.getInteger("companyid");
        try {
            return ResultRes.success(orgManagementService.updateDepartment(id,name,parentid,manager,leader,companyid));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 删除部门
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-部门" , operType = LogConst.DELETE , operDesc = "删除部门信息")
    @PostMapping(value = "/orgm/deleteDepartment")
    public ResultRes deleteDepartment(@RequestBody JSONObject param){
        Integer id=param.getInteger("id");
        try {
            return ResultRes.success(orgManagementService.deleteDepartment(id));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 设置部门负责人
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-部门" , operType = LogConst.UPDATE , operDesc = "设置部门负责人")
    @PostMapping(value = "/orgm/setDepartmentManager")
    public ResultRes setDepartmentManager(@RequestBody JSONObject param){
        Integer id=param.getInteger("id");
        String manager=param.getString("manager");
        try {
            return ResultRes.success(orgManagementService.setDepartmentManager(id,manager));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 设置部门分管领导
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-部门" , operType = LogConst.UPDATE , operDesc = "设置部门分管领导")
    @PostMapping(value = "/orgm/setDepartmentLeader")
    public ResultRes setDepartmentLeader(@RequestBody JSONObject param){
        Integer id=param.getInteger("id");
        String leader=param.getString("leader");
        try {
            return ResultRes.success(orgManagementService.setDepartmentLeader(id,leader));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 设置所属公司
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-部门" , operType = LogConst.UPDATE , operDesc = "设置部门所属公司")
    @PostMapping(value = "/orgm/updateDeptCompany")
    public ResultRes updateDeptCompany(@RequestBody JSONObject param){
        Integer id=param.getInteger("id");
        Integer companyid=param.getInteger("companyid");
        try {
            return ResultRes.success(orgManagementService.updateDeptCompany(id,companyid));
        }
        catch (Exception ex){
            return ResultRes.error(ex.getMessage());
        }
    }

    /**
     * 获取组织结构数据
     * @return
     */
    @OperLog(operModul = "组织结构" , operType = LogConst.OTHER , operDesc = "获取整个组织结构的数据")
    @GetMapping("/orgm/getOrgData")
    public ResultRes getOrgData(){
        return ResultRes.success(orgManagementService.getOrgData());
    }

    /**
     * 获取岗位列表
     * @return
     */
    @OperLog(operModul = "组织结构-岗位" , operType = LogConst.OTHER , operDesc = "获取岗位列表")
    @GetMapping("/orgm/positionList")
    public ResultRes positionList(){
        try {
            return ResultRes.success(orgManagementService.positionList());
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 获取岗位列表--分页
     * @return
     */
    @OperLog(operModul = "组织结构-岗位" , operType = LogConst.OTHER , operDesc = "获取岗位列表分页数据")
    @PostMapping("/orgm/positionListPage")
    public ResultRes positionListPage(@RequestBody JSONObject param){
        try{
            String name=param.getString("name");
            Integer page=param.getInteger("page");
            Integer pageSize=param.getInteger("pagesize");
            return ResultRes.success(orgManagementService.positionListPage(name,page,pageSize));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 获取用户的岗位
     * @param response
     * @param username
     * @return
     */
    @OperLog(operModul = "组织结构-岗位" , operType = LogConst.OTHER , operDesc = "获取人员拥有的岗位列表")
    @GetMapping("/orgm/positionListByUser")
    public ResultRes positionListByUser(HttpServletResponse response,
                                        @RequestParam("username") String username){
        try {
            return ResultRes.success(orgManagementService.positionListByUser(username));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 获取岗位成员
     * @param id
     * @param page
     * @param pagesize
     * @return
     */
    @OperLog(operModul = "组织结构-岗位" , operType = LogConst.OTHER , operDesc = "获取岗位成员")
    @GetMapping("/orgm/getPositionUser")
    public ResultRes getPositionUser(@RequestParam("id") String id,
                                     @RequestParam("page") Integer page,
                                     @RequestParam("pagesize") Integer pagesize)
    {
        try {
            return ResultRes.success(orgManagementService.getPositionUser(id,page,pagesize));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 增加岗位
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-岗位" , operType = LogConst.INSERT , operDesc = "增加岗位")
    @PostMapping("/orgm/addPosition")
    public ResultRes addPosition(@RequestBody JSONObject param){
        String name=param.getString("name");
        try {
            return ResultRes.success(orgManagementService.addPosition(name));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 修改岗位信息
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-岗位" , operType = LogConst.UPDATE , operDesc = "修改岗位信息")
    @PostMapping("/orgm/updatePosition")
    public ResultRes updatePosition(@RequestBody JSONObject param){
        String id=param.getString("id");
        String name=param.getString("name");
        try {
            return ResultRes.success(orgManagementService.updatePosition(id,name));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 删除岗位
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-岗位" , operType = LogConst.DELETE , operDesc = "删除岗位信息")
    @PostMapping("/orgm/deletePosition")
    public ResultRes deletePosition(@RequestBody JSONObject param){
        String id=param.getString("id");
        try {
            return ResultRes.success(orgManagementService.deletePosition(id));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 删除用户岗位
     * @param response
     * @param username
     * @return
     */
    @OperLog(operModul = "组织结构-岗位" , operType = LogConst.DELETE , operDesc = "删除用户岗位")
    @GetMapping("/orgm/deletePositionByUser")
    public ResultRes deletePositionByUser(HttpServletResponse response,
                                          @RequestParam("username") String username){
        try {
            return ResultRes.success(orgManagementService.deletePositionUser(username));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
    /**
     * 获取角色列表
     * @return
     */
    @OperLog(operModul = "组织结构-角色" , operType = LogConst.OTHER , operDesc = "获取角色列表")
    @GetMapping("/orgm/rolesList")
    public ResultRes rolesList(){
        try {
            return ResultRes.success(orgManagementService.rolesList());
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
    /**
     * 获取角色部门
     * @return
     */
    @OperLog(operModul = "组织结构-角色" , operType = LogConst.OTHER , operDesc = "获取角色部门")
    @GetMapping("/orgm/roleDeptTreeselect")
    public ResultRes roleDeptTreeselect(HttpServletResponse response,
                                        @RequestParam("id") String id){
        try {
            return ResultRes.success(orgManagementService.roleDeptTreeselect(id));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 获取角色列表--分页
     * @return
     */
    @OperLog(operModul = "组织结构-角色" , operType = LogConst.OTHER , operDesc = "获取角色列表分页")
    @PostMapping("/orgm/rolesListPage")
    public ResultRes rolesListPage(@RequestBody JSONObject param){
        try{
            String name=param.getString("name");
            Integer page=param.getInteger("page");
            Integer pageSize=param.getInteger("pagesize");
            return ResultRes.success(orgManagementService.rolesListPage(name,page,pageSize));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 获取用户的角色
     * @param response
     * @param username
     * @return
     */
    @OperLog(operModul = "组织结构-角色" , operType = LogConst.OTHER , operDesc = "获取用户角色列表")
    @GetMapping("/orgm/rolesListByUser")
    public ResultRes rolesListByUser(HttpServletResponse response,
                                     @RequestParam("username") String username){
        try {
            return ResultRes.success(orgManagementService.rolesListByUser(username));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
    /**
     * 获取角色成员
     * @param id
     * @param page
     * @param pagesize
     * @return
     */
    @OperLog(operModul = "组织结构-角色" , operType = LogConst.OTHER , operDesc = "获取角色成员列表")
    @GetMapping("/orgm/getRoleUsers")
    public ResultRes getRoleUsers(@RequestParam("id") String id,
                                  @RequestParam("page") Integer page,
                                  @RequestParam("pagesize") Integer pagesize)
    {
        try {
            return ResultRes.success(orgManagementService.getRoleUsers(id,page,pagesize));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
    /**
     * 增加角色
     * @param sysRoleDto
     * @return
     */
    @OperLog(operModul = "组织结构-角色" , operType = LogConst.INSERT , operDesc = "增加角色")
    @PostMapping("/orgm/addRoles")
    public ResultRes addRoles(@RequestBody SysRoleDto sysRoleDto){
        try {
            return ResultRes.success(orgManagementService.addRoles(sysRoleDto));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 修改角色信息
     * @param sysRoleDto
     * @return
     */
    @OperLog(operModul = "组织结构-角色" , operType = LogConst.UPDATE , operDesc = "修改角色信息")
    @PostMapping("/orgm/updateRoles")
    public ResultRes updateRoles(@RequestBody SysRoleDto sysRoleDto){
        try {
            return ResultRes.success(orgManagementService.updateRoles(sysRoleDto));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 删除角色
     * @param sysRoleDto
     * @return
     */
    @OperLog(operModul = "组织结构-角色" , operType = LogConst.DELETE , operDesc = "删除角色")
    @PostMapping("/orgm/deleteRoles")
    public ResultRes deleteRoles(@RequestBody SysRoleDto sysRoleDto){
        try {
            return ResultRes.success(orgManagementService.deleteRoles(sysRoleDto));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 删除用户角色
     * @param response
     * @param username
     * @return
     */
    @OperLog(operModul = "组织结构-角色" , operType = LogConst.DELETE , operDesc = "删除用户角色")
    @GetMapping("/orgm/deleteRoleByUser")
    public ResultRes deleteRoleByUser(HttpServletResponse response,
                                      @RequestParam("username") String username){
        try {
            return ResultRes.success(orgManagementService.deleteRoleUser(username));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 获取人员列表
     * @return
     */
    @OperLog(operModul = "组织结构-人员" , operType = LogConst.OTHER , operDesc = "获取人员列表")
    @GetMapping("/orgm/getUserEntities")
    public ResultRes getUserEntities(){
        try {
            return ResultRes.success(orgManagementService.getUserEntities());
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 获取人员列表--分页
     * @return
     */
    @OperLog(operModul = "组织结构-人员" , operType = LogConst.OTHER , operDesc = "获取人员列表分页数据")
    @PostMapping("/orgm/getUserList")
    public ResultRes getUserList(@RequestBody JSONObject param){
        try {
            String name=param.getString("name");
            String username=param.getString("username");
            String departid=param.getString("departid");
            if(SystemConfig.IsNullOrEmpty(departid)){
                departid="all";
            }
            Integer page=param.getInteger("page");
            Integer pagesize=param.getInteger("pagesize");
            return ResultRes.success(orgManagementService.getUserList(name,username,departid,page,pagesize));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
    @OperLog(operModul = "组织结构-人员" , operType = LogConst.OTHER , operDesc = "获取人员信息")
    @GetMapping("/orgm/getUserEntity")
    public ResultRes getUserEntity(HttpServletResponse response,
                                   @RequestParam("username") String username){
        try{
            return ResultRes.success(orgManagementService.getUserEntity(username));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
    @OperLog(operModul = "组织结构-人员" , operType = LogConst.OTHER , operDesc = "根据部门编号获取人员列表")
    @GetMapping("/orgm/getUserByDepartId")
    public ResultRes getUserByDepartId(HttpServletResponse response,
                                       @RequestParam("departid") Integer departid){
        try{
            return ResultRes.success(orgManagementService.getUserByDepartId(departid));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 增加人员
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-人员" , operType = LogConst.INSERT , operDesc = "增加人员")
    @PostMapping(value = "/orgm/addUser")
    public ResultRes addUser(@RequestBody UserParams param){
        try{
            return ResultRes.success(orgManagementService.addUser(param));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 修改人员信息
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-人员" , operType = LogConst.UPDATE , operDesc = "修改人员信息")
    @PostMapping(value = "/orgm/updateUser")
    public ResultRes updateUser(@RequestBody UserParams param){
        try{
            return ResultRes.success(orgManagementService.updateUser(param));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 修改密码
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-人员" , operType = LogConst.UPDATE , operDesc = "修改登录密码")
    @PostMapping(value = "/orgm/updatePwd")
    public ResultRes updatePwd(@RequestBody JSONObject param){
        String username=param.getString("username");
        String oldPwd=param.getString("oldpwd");
        String newPwd=param.getString("newpwd");
        try{
            return ResultRes.success(orgManagementService.updatePwd(username,oldPwd,newPwd));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 重置密码
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-人员" , operType = LogConst.UPDATE , operDesc = "重置密码")
    @PostMapping(value = "/orgm/resetPwd")
    public ResultRes resetPwd(@RequestBody JSONObject param){
        String username=param.getString("username");
        try{
            return ResultRes.success(orgManagementService.resetPwd(username));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 删除用户
     * @param userEntity
     * @return
     */
    @OperLog(operModul = "组织结构-人员" , operType = LogConst.DELETE , operDesc = "删除人员")
    @PostMapping(value = "/orgm/deleteUser")
    public ResultRes deleteUser(@RequestBody UserEntity userEntity){
        try{
            return ResultRes.success(orgManagementService.deleteUser(userEntity));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
    @OperLog(operModul = "组织结构-用户组" , operType = LogConst.OTHER , operDesc = "获取用户组列表")
    @PostMapping(value = "/orgm/getGroupList")
    public ResultRes getGroupList(@RequestBody JSONObject param){
        String name=param.getString("name");
        try{
            return ResultRes.success(orgManagementService.getGroupList(name));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 获取用户分组
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-用户组" , operType = LogConst.OTHER , operDesc = "获取用户组列表分页数据")
    @PostMapping(value = "/orgm/getGroupPage")
    public ResultRes getGroupPage(@RequestBody JSONObject param){
        String name=param.getString("name");
        Integer page=param.getInteger("page");
        Integer pageSize=param.getInteger("pagesize");
        try{
            return ResultRes.success(orgManagementService.getGroupPage(name,page,pageSize));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 获取分组内的成员
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-用户组" , operType = LogConst.OTHER , operDesc = "获取用户组中的人员")
    @PostMapping(value = "/orgm/getUserByGorupId")
    public ResultRes getUserByGorupId(@RequestBody JSONObject param){
        String groupid=param.getString("groupId");
        Integer page=param.getInteger("page");
        Integer pageSize=param.getInteger("pagesize");
        try{
            return ResultRes.success(orgManagementService.getUserByGorupId(groupid,page,pageSize));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
    /**
     * 增加分组
     * @param groupEntity
     * @return
     */
    @OperLog(operModul = "组织结构-用户组" , operType = LogConst.OTHER , operDesc = "增加用户组")
    @PostMapping(value = "/orgm/addGroup")
    public ResultRes addGroup(@RequestBody GroupEntity groupEntity){
        try{
            return ResultRes.success(orgManagementService.addGroup(groupEntity));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 更新分组
     * @param groupEntity
     * @return
     */
    @OperLog(operModul = "组织结构-用户组" , operType = LogConst.OTHER , operDesc = "修改用户组")
    @PostMapping(value = "/orgm/updataGroup")
    public ResultRes updataGroup(@RequestBody GroupEntity groupEntity){
        try{
            return ResultRes.success(orgManagementService.updataGroup(groupEntity));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 删除分组
     * @param groupEntity
     * @return
     */
    @OperLog(operModul = "组织结构-用户组" , operType = LogConst.OTHER , operDesc = "删除用户组")
    @PostMapping(value = "/orgm/deleteGroup")
    public ResultRes deleteGroup(@RequestBody GroupEntity groupEntity){
        try{
            return ResultRes.success(orgManagementService.deleteGroup(groupEntity));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 增加分组人员
     * @param param
     * @return
     */
    @OperLog(operModul = "组织结构-用户组" , operType = LogConst.OTHER , operDesc = "增加用户组成员")
    @PostMapping(value = "/orgm/addUserGroup")
    public ResultRes addUserGroup(@RequestBody GroupUsersParams param){
        try{
            return ResultRes.success(orgManagementService.addUserGroup(param));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }

    /**
     * 删除分组人员
     * @param username
     * @param groupid
     * @return
     */
    @OperLog(operModul = "组织结构-用户组" , operType = LogConst.OTHER , operDesc = "删除用户组")
    @GetMapping(value = "/orgm/deleteUserGroup")
    public ResultRes deleteUserGroup(@RequestParam("username") String username,
                                     @RequestParam("groupid") String groupid){
        try{
            return ResultRes.success(orgManagementService.deleteUserGroup(username,groupid));
        }
        catch (Exception ex){
            return ResultRes.error(ex.toString());
        }
    }
}
