package com.activiti.z_six.service.impl;

import com.activiti.z_six.common.BusinessException;
import com.activiti.z_six.dto.controllerParams.GroupUsersParams;
import com.activiti.z_six.dto.controllerParams.UserParams;
import com.activiti.z_six.dto.orgDto.SysRoleDept;
import com.activiti.z_six.dto.orgDto.SysRoleDto;
import com.activiti.z_six.dto.orgDto.SysRoleMenu;
import com.activiti.z_six.entity.UserInfo;
import com.activiti.z_six.entity.orgmanagement.*;
import com.activiti.z_six.mapper.UserInfoMapper;
import com.activiti.z_six.mapper.orgmanagementMapper.*;
import com.activiti.z_six.service.IOrgManagementService;
import com.activiti.z_six.service.manager.CommManager;
import com.activiti.z_six.util.SystemConfig;
import com.activiti.z_six.util.encode.AesPasswordEncoder;
import com.activiti.z_six.util.encode.AesUtil;
import lombok.SneakyThrows;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IOrgManagementServiceImpl implements IOrgManagementService {
    @Autowired
    private CompanyEntityMapper companyEntityMapper;
    @Autowired
    private DepartmentEntityMapper departmentEntityMapper;
    @Autowired
    private UserEntityMapper userEntityMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RolesEntityMapper rolesEntityMapper;
    @Autowired
    private PositionEntityMapper positionEntityMapper;
    @Autowired
    private GroupMapper groupMapper;
    @Autowired
    private UserGroupMapper userGroupMapper;
    @Autowired
    UserInfoMapper mapper;
    @Autowired
    private CommManager commManager;
    /**
     * 获取公司列表
     * @return
     */
    @Override
    public List<CompanyEntity> getCompanyList(){
        return getCompanyEntityList();
    }
    private List<CompanyEntity> getCompanyEntityList(){
        List<CompanyEntity> companyEntityList= companyEntityMapper.getCompanyList();
        //2、组装树形结构
        List<CompanyEntity> newList = companyEntityList.stream()
                .filter(t -> t.getParentid() == 0)
                .map((menu) -> {
                    menu.setChildren(this.getCompanyChildren(menu,companyEntityList));
                    return menu;
                })
                .collect(Collectors.toList());
        return newList;
    }
    /**
     * 增加公司
     * @param name
     * @param parentid
     * @return
     */
    @Override
    public String addCompany(String name, Integer parentid){
        CompanyEntity companyEntity=new CompanyEntity();
        companyEntity.setName(name);
        companyEntity.setParentid(parentid);
        companyEntityMapper.addCompany(companyEntity);
        return "添加成功";
    }
    /**
     * 修改公司信息
     * @param id
     * @param name
     * @param parentid
     * @return
     */
    @Override
    public String updateCompany(Integer id, String name, Integer parentid){
        CompanyEntity companyEntity=new CompanyEntity();
        companyEntity.setId(id);
        companyEntity.setName(name);
        companyEntity.setParentid(parentid);
        companyEntityMapper.updateCompany(companyEntity);
        return "修改成功";
    }
    /**
     * 删除公司信息
     * @param id
     * @param name
     * @param parentid
     * @return
     */
    @Override
    public String deleteCompany(Integer id, String name, Integer parentid){
        CompanyEntity companyEntity=new CompanyEntity();
        companyEntity.setId(id);
        companyEntity.setName(name);
        companyEntity.setParentid(parentid);
        companyEntityMapper.deleteCompany(companyEntity);

        return "删除成功。";
    }
    /**
     * 递归查找当前公司的子公司
     * @param root 单个对象
     * @param all 所有的集合
     * @return 排序后的子类
     */
    private List<CompanyEntity> getCompanyChildren(CompanyEntity root,List<CompanyEntity> all){
        List<CompanyEntity> childrenList = all.stream()
                .filter(t -> t.getParentid() == root.getId())
                .map(g -> {
                    //找子菜单
                    g.setChildren(getCompanyChildren(g,all));
                    return g;
                })
                //菜单排序
                .collect(Collectors.toList());

        return childrenList;
    }
    /**
     * 获取部门列表
     * @return
     */
    @Override
    public HashMap<String,Object> getDepartmentList(String name, String companyid, Integer page,
                                       Integer pageSize){
        HashMap<String,Object> hashMap=new HashMap<>();
        if(commManager.getUserNo().equals("admin")) {
            List<DepartmentEntity> departmentEntities = departmentEntityMapper.getDepartmentList("",name, companyid);
            if (pageSize == 0) {
                hashMap.put("total", departmentEntities.size());
                hashMap.put("list", departmentEntities);
            } else {
                Integer indexStart = (page - 1) * pageSize;
                List<DepartmentEntity> departmentList = departmentEntityMapper.getDepartmentPage("",name,
                        companyid, indexStart, pageSize);
                hashMap.put("total", departmentEntities.size());
                hashMap.put("list", departmentList);
            }
        }
        else{
            UserEntity userInfo=userEntityMapper.getUserEntityInfo(commManager.getUserNo());
            List<DepartmentEntity> departmentEntities = departmentEntityMapper.getDepartmentList(userInfo.getDepartid().toString(),
                    name, companyid);
            if (pageSize == 0) {
                hashMap.put("total", departmentEntities.size());
                hashMap.put("list", departmentEntities);
            } else {
                Integer indexStart = (page - 1) * pageSize;
                List<DepartmentEntity> departmentList = departmentEntityMapper.getDepartmentPage(userInfo.getDepartid().toString(),
                        name,
                        companyid, indexStart, pageSize);
                hashMap.put("total", departmentEntities.size());
                hashMap.put("list", departmentList);
            }
        }
        return hashMap;
    }

    /**
     * 获取部门人员
     * @return
     */
    @Override
    public HashMap<String,Object> getDeptUsers(Integer id, Integer page, Integer pageSize){
        HashMap<String,Object> hashMap=new HashMap<>();
        List<UserEntity> userList=userEntityMapper.getUserByDepartId(id);
        if(pageSize==0){
            hashMap.put("total",userList.size());
            hashMap.put("list",userList);
        }
        else{
            Integer startIndex=(page-1)*pageSize;
            List<UserEntity> userEntities=userEntityMapper.getUserByDepartIdPage(id,startIndex,pageSize);
            hashMap.put("total",userList.size());
            hashMap.put("list",userEntities);
        }
        return hashMap;
    }
    @Override
    public List<UserEntity> getDeptUserTree(){
        List<UserEntity> userList=userEntityMapper.getDeptUsers();;
        //2、组装树形结构
        List<UserEntity> newList = userList.stream()
                .filter(t -> t.getDepartid() == 0)
                .map((menu) -> {
                    menu.setChildren(commManager.getUserChildren(menu,userList));
                    return menu;
                })
                .collect(Collectors.toList());
        return newList;
    }

    /**
     * 获取公司下的部门
     * @param companyId
     * @return
     */
    @Override
    public List<DepartmentEntity> getDeptByCompany(Integer companyId){
        return getDepartmentEntityListByCompany(companyId);
    }
    private List<DepartmentEntity> getDepartmentEntityListByCompany(Integer companyId){
        List<DepartmentEntity> departmentList= departmentEntityMapper.getDeptByCompany(companyId);
        //2、组装树形结构
        List<DepartmentEntity> newList = departmentList.stream()
                .filter(t -> t.getParentid() == 0)
                .map((menu) -> {
                    menu.setChildren(commManager.getDeptChildren(menu,departmentList));
                    return menu;
                })
                .collect(Collectors.toList());
        return newList;
    }

    /**
     * 获取部门树
     * @return
     */
    @Override
    public List<DepartmentEntity> getDepartmentEntityList(){

        if(!commManager.getUserNo().equals("admin")){
            UserEntity userEntity=userEntityMapper.getUserEntityInfo(commManager.getUserNo());
            List<DepartmentEntity> departmentList= departmentEntityMapper.getDepartmentList(userEntity.getDepartid().toString(),"","all");
            //2、组装树形结构
            List<DepartmentEntity> newList = departmentList.stream()
                    .filter(t -> t.getParentid() == userEntity.getDepartid()||t.getId() == userEntity.getDepartid())
                    .map((menu) -> {
                        menu.setChildren(commManager.getDeptChildren(menu,departmentList));
                        return menu;
                    })
                    .collect(Collectors.toList());
            return newList;
        }
        else{
            List<DepartmentEntity> departmentList= departmentEntityMapper.getDepartmentList("","","all");
            //2、组装树形结构
            List<DepartmentEntity> newList = departmentList.stream()
                    .filter(t -> t.getParentid() == 0)
                    .map((menu) -> {
                        menu.setChildren(commManager.getDeptChildren(menu,departmentList));
                        return menu;
                    })
                    .collect(Collectors.toList());
            return newList;
        }

    }
    private List<UserEntity> getUserList(){
        List<UserEntity> userList=userEntityMapper.getDeptUsers();;
        //2、组装树形结构
        List<UserEntity> newList = userList.stream()
                .filter(t -> t.getDepartid() == 0)
                .map((menu) -> {
                    menu.setChildren(commManager.getUserChildren(menu,userList));
                    return menu;
                })
                .collect(Collectors.toList());
        return newList;
    }
    /**
     * 增加部门
     * @param name
     * @param parentid
     * @param manager
     * @param leader
     * @param companyid
     * @return
     */
    @Override
    public String addDepartment(String name, Integer parentid, String manager,
                                   String leader, Integer companyid){
        DepartmentEntity departmentEntity=new DepartmentEntity();
        departmentEntity.setName(name);
        departmentEntity.setParentid(parentid);
        departmentEntity.setManager(manager);
        departmentEntity.setLeader(leader);
        departmentEntity.setCompanyid(companyid);

        departmentEntityMapper.addDepartment(departmentEntity);
        return "添加成功";
    }
    /**
     * 修改部门
     * @param id
     * @param name
     * @param parentid
     * @param manager
     * @param leader
     * @param companyid
     * @return
     */
    @Override
    public String updateDepartment(Integer id, String name, Integer parentid,
                                      String manager, String leader, Integer companyid){
        DepartmentEntity departmentEntity=new DepartmentEntity();
        departmentEntity.setId(id);
        departmentEntity.setName(name);
        departmentEntity.setParentid(parentid);
        departmentEntity.setManager(manager);
        departmentEntity.setLeader(leader);
        departmentEntity.setCompanyid(companyid);

        departmentEntityMapper.updateDepartment(departmentEntity);
        return "修改成功";
    }
    /**
     * 删除部门
     * @param id
     * @return
     */
    @Override
    public String deleteDepartment(Integer id){
        DepartmentEntity departmentEntity=new DepartmentEntity();
        departmentEntity.setId(id);

        departmentEntityMapper.deleteDepartment(departmentEntity);
        return "删除成功";
    }
    /**
     * 设置部门负责人
     * @param id
     * @param manager
     * @return
     */
    @Override
    public String setDepartmentManager(Integer id, String manager){
        DepartmentEntity departmentEntity=new DepartmentEntity();
        departmentEntity.setId(id);
        departmentEntity.setManager(manager);

        departmentEntityMapper.setDepartmentManager(departmentEntity);
        return "设置成功";
    }
    /**
     * 设置部门分管领导
     * @param id
     * @param leader
     * @return
     */
    @Override
    public String setDepartmentLeader(Integer id, String leader){
        DepartmentEntity departmentEntity=new DepartmentEntity();
        departmentEntity.setId(id);
        departmentEntity.setLeader(leader);

        departmentEntityMapper.setDepartmentLeader(departmentEntity);
        return "设置成功";
    }
    /**
     * 设置所属公司
     * @param id
     * @param companyid
     * @return
     */
    @Override
    public String updateDeptCompany(Integer id, Integer companyid){
        DepartmentEntity departmentEntity=new DepartmentEntity();
        departmentEntity.setId(id);
        departmentEntity.setCompanyid(companyid);

        departmentEntityMapper.updateDeptCompany(departmentEntity);
        return "设置成功";
    }
    /**
     * 获取组织结构数据
     * @return
     */
    @Override
    public HashMap<String,Object> getOrgData(){
        List<DepartmentEntity> departmentList=getDepartmentEntityList();
        List<UserEntity> userEntityList=userEntityMapper.getUserEntityList("","","all");
        List<RolesEntity> rolesEntities=rolesEntityMapper.rolesList("");
        List<PositionEntity> positionEntities=positionEntityMapper.positionList("");
        List<CompanyEntity> companyEntities=getCompanyEntityList();
        List<GroupEntity> groupEntityList=groupMapper.getGroupList("");
        HashMap<String,Object> hashMap=new HashMap<>();

        hashMap.put("departList",departmentList);
        hashMap.put("userList",userEntityList);
        hashMap.put("rolesList",rolesEntities);
        hashMap.put("positionList",positionEntities);
        hashMap.put("companyList",companyEntities);
        hashMap.put("groupEntityList",groupEntityList);

        return hashMap;
    }
    /**
     * 获取岗位列表
     * @return
     */
    @Override
    public List<PositionEntity> positionList(){
        return positionEntityMapper.positionList("");
    }

    /**
     * 获取岗位列表
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public HashMap<String,Object> positionListPage(String name,Integer page,Integer pageSize){
        HashMap<String,Object> hashMap=new HashMap<>();
        List<PositionEntity> positionEntities=positionEntityMapper.positionList(name);
        if(pageSize==0){
            hashMap.put("list",positionEntities);
            hashMap.put("total",positionEntities.size());
        }
        else{
            Integer indexStart=(page-1)*pageSize;
            List<PositionEntity> positionEntityList=positionEntityMapper.positionListPage(name,indexStart,pageSize);
            hashMap.put("list",positionEntityList);
            hashMap.put("total",positionEntities.size());
        }
        return hashMap;
    }

    /**
     * 获取用户的岗位
     * @param username
     * @return
     */
    @Override
    public List<PositionEntity> positionListByUser(String username){
        return positionEntityMapper.positionListByUser(username);
    }
    /**
     * 获取岗位成员
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public HashMap<String,Object>  getPositionUser(String id, Integer page, Integer pageSize){
        HashMap<String,Object> hashMap=new HashMap<>();
        List<UserEntity> userEntities=userEntityMapper.getUserByPositionId(id);
        if(pageSize==0){
            hashMap.put("total",userEntities.size());
            hashMap.put("list",userEntities);
        }
        else{
            Integer startIndex=(page-1)*pageSize;
            List<UserEntity> userEntityList=userEntityMapper.getUserByPositionIdPage(id,startIndex,pageSize);
            hashMap.put("total",userEntities.size());
            hashMap.put("list",userEntityList);
        }
        return hashMap;
    }
    /**
     * 增加岗位
     * @param name
     */
    @Override
    public String addPosition(String name){
        PositionEntity positionEntity=new PositionEntity();
        positionEntity.setId(UUID.randomUUID().toString());
        positionEntity.setName(name);
        positionEntityMapper.addPosition(positionEntity);
        return "添加成功";
    }

    /**
     * 更新岗位信息
     * @param id
     * @param name
     * @return
     */
    @Override
    public String updatePosition(String id, String name){
        PositionEntity positionEntity=new PositionEntity();
        positionEntity.setId(id);
        positionEntity.setName(name);
        positionEntityMapper.updatePosition(positionEntity);
        return "更新成功";
    }

    /**
     * 删除岗位
     * @param id
     * @return
     */
    @Override
    public String deletePosition(String id){
        positionEntityMapper.deletePosition(id);
        return "删除成功";
    }

    /**
     * 删除用户的岗位
     * @param username
     * @return
     */
    @Override
    public String deletePositionUser(String username){
        positionEntityMapper.deletePositionUser(username);
        return "删除成功";
    }

    /**
     * 获取角色列表
     * @return
     */
    @Override
    public List<RolesEntity> rolesList(){
        return rolesEntityMapper.rolesList("");
    }

    /**
     * 角色部门
     * @param id
     * @return
     */
    @Override
    public List<SysRoleDept> roleDeptTreeselect(String id){
        return rolesEntityMapper.roleDeptTreeselect(id);
    }
    /**
     * 获取角色列表分页
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public HashMap<String,Object> rolesListPage(String name,Integer page,Integer pageSize){
        HashMap<String,Object> hashMap=new HashMap<>();
        List<RolesEntity> rolesEntities=rolesEntityMapper.rolesList(name);
        if(pageSize==0){
            hashMap.put("list",rolesEntities);
            hashMap.put("total",rolesEntities.size());
        }
        else{
            Integer indexStart=(page-1)*pageSize;
            List<RolesEntity> rolesEntityList=rolesEntityMapper.rolesListPage(name,indexStart,pageSize);
            hashMap.put("list",rolesEntityList);
            hashMap.put("total",rolesEntities.size());
        }
        return  hashMap;
    }

    /**
     * 获取用户的角色
     * @param username
     * @return
     */
    @Override
    public List<RolesEntity> rolesListByUser(String username){
        return rolesEntityMapper.rolesListByUser(username);
    }

    /**
     * 获取角色成员
     * @param id
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public HashMap<String,Object> getRoleUsers(String id, Integer page, Integer pageSize){
        HashMap<String,Object> hashMap=new HashMap<>();
        List<UserEntity> userEntities=userEntityMapper.getUserByRoleId(id);
        if(pageSize==0){
            hashMap.put("total",userEntities.size());
            hashMap.put("list",userEntities);
        }
        else{
            Integer startIndex=(page-1)*pageSize;
            List<UserEntity> userEntityList=userEntityMapper.getUserByRoleIdPage(id,startIndex,pageSize);
            hashMap.put("total",userEntities.size());
            hashMap.put("list",userEntityList);
        }
        return hashMap;
    }
    /**
     * 增加角色
     * @param sysRoleDto
     * @return
     */
    @Override
    public String addRoles(SysRoleDto sysRoleDto){
        RolesEntity rolesEntity=new RolesEntity();
        rolesEntity.setId(UUID.randomUUID().toString());
        rolesEntity.setName(sysRoleDto.getName());
        rolesEntityMapper.addRoles(rolesEntity);
        return insertRoleMenu(sysRoleDto);
    }

    /**
     * 修改角色信息
     * @param sysRoleDto
     * @return
     */
    @Override
    public String updateRoles(SysRoleDto sysRoleDto){
        RolesEntity rolesEntity=new RolesEntity();
        rolesEntity.setId(sysRoleDto.getId());
        rolesEntity.setName(sysRoleDto.getName());
        rolesEntity.setData_scope(sysRoleDto.getDataScope());
        rolesEntityMapper.updateRoles(rolesEntity);
        insertRoleMenu(sysRoleDto);
        setDataScope(sysRoleDto);
        return "修改成功";
    }

    /**
     * 更新角色菜单
     * @param sysRoleDto
     * @return
     */
    public String insertRoleMenu(SysRoleDto sysRoleDto){
        List<SysRoleMenu> sysRoleMenus=new ArrayList<SysRoleMenu>();
        for (String menuId: sysRoleDto.getMenuIds()){
            SysRoleMenu sysRoleMenu=new SysRoleMenu();
            sysRoleMenu.setMenuId(menuId);
            sysRoleMenu.setRoleId(sysRoleDto.getId());
            sysRoleMenus.add(sysRoleMenu);
        }
        if(sysRoleMenus.size()>0){
            rolesEntityMapper.deleteRoleMenu(sysRoleDto.getId());
            rolesEntityMapper.batchRoleMenu(sysRoleMenus);
        }
        return "修改成功";
    }

    /**
     * 设置数据权限
     * @param sysRoleDto
     * @return
     */
    public String setDataScope(SysRoleDto sysRoleDto){
        List<SysRoleDept> sysRoleDepts=new ArrayList<SysRoleDept>();
        for (String deptId: sysRoleDto.getDepts()){
            SysRoleDept sysRoleDept=new SysRoleDept();
            sysRoleDept.setDept_Id(deptId);
            sysRoleDept.setRole_Id(sysRoleDto.getId());
            sysRoleDepts.add(sysRoleDept);
        }
        if(sysRoleDepts.size()>0){
            rolesEntityMapper.deleteDataScope(sysRoleDto.getId());
            rolesEntityMapper.batchRoleDept(sysRoleDepts);
        }
        return "修改成功";
    }

    /**
     * 删除角色
     * @param sysRoleDto
     * @return
     */
    @Override
    public String deleteRoles(SysRoleDto sysRoleDto){
        rolesEntityMapper.deleteRoles(sysRoleDto.getId());
        rolesEntityMapper.deleteRoleMenu(sysRoleDto.getId());
        return "删除成功";
    }

    /**
     * 删除用户的角色
     * @param username
     * @return
     */
    @Override
    public String deleteRoleUser(String username){
        rolesEntityMapper.deleteRoleUser(username);
        return "删除成功";
    }

    /**
     * 获取人员列表
     * @return
     */
    @Override
    public List<UserEntity> getUserEntities(){
        List<UserEntity> userEntityList = userEntityMapper.getUserEntityList("","","all");
        return userEntityList;
    }

    /**
     * 获取人员列表分页
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public HashMap<String,Object> getUserList(String name,String username,
                                              String departid,Integer page,Integer pageSize){
        HashMap<String,Object> hashMap=new HashMap<>();
        if(commManager.getUserNo().equals("admin")) {
            List<UserEntity> userEntityList = userEntityMapper.getUserEntityList(name, username, departid);
            if (pageSize == 0) {
                hashMap.put("list", userEntityList);
                hashMap.put("total", userEntityList.size());
            } else {
                Integer indexStart = (page - 1) * pageSize;
                List<UserEntity> userEntities = userEntityMapper.getUserList(name, username, departid, indexStart, pageSize);
                hashMap.put("list", userEntities);
                hashMap.put("total", userEntityList.size());
            }
            return hashMap;
        }
        else{
            UserEntity userEntity=userEntityMapper.getUserEntityInfo(commManager.getUserNo());
            if(SystemConfig.IsNullOrEmpty(departid)||departid.equals("all")){
                departid=userEntity.getDepartid().toString();
            }
            List<UserEntity> userEntityList = userEntityMapper.getUserEntityList(name, username, departid);
            if (pageSize == 0) {
                hashMap.put("list", userEntityList);
                hashMap.put("total", userEntityList.size());
            } else {
                Integer indexStart = (page - 1) * pageSize;
                List<UserEntity> userEntities = userEntityMapper.getUserList(name, username, departid, indexStart, pageSize);
                hashMap.put("list", userEntities);
                hashMap.put("total", userEntityList.size());
            }
            return hashMap;
        }
    }

    /**
     * 获取人员详细信息
     * @param username
     * @return
     */
    @Override
    public HashMap<String,Object> getUserEntity(String username){
        HashMap<String,Object> hashMap=new HashMap<>();
        try {
            UserEntity userEntityInfo = userEntityMapper.getUserEntityInfo(username);
            List<PositionEntity> positionEntities=positionEntityMapper.positionListByUser(username);
            List<RolesEntity> rolesEntities=rolesEntityMapper.rolesListByUser(username);
            hashMap.put("user",userEntityInfo);
            hashMap.put("positions",positionEntities);
            hashMap.put("roles",rolesEntities);
            return hashMap;
        }
        catch (Exception ex){
            return hashMap;
        }
    }

    /**
     * 增加人员
     * @param param
     * @return
     */
    @Override
    @SneakyThrows
    public String addUser(UserParams param) throws BusinessException {
        String password=param.getPassword()==null? AesUtil.encryptAES("123456"):param.getPassword();
        UserEntity userEntity=new UserEntity();
        BeanUtils.copyProperties(param, userEntity);
        userEntity.setState(1);
        userEntity.setRoles("ROLE_ACTIVITI_USER");
        userEntity.setDepartid(param.getDepartid());
        userEntity.setPassword(password);
        userEntity.setGuuid(UUID.randomUUID().toString());

        userEntityMapper.addUser(userEntity);

        //修改岗位信息
        if(param.getPositions().length>0){
            positionEntityMapper.deletePositionUser(param.getUsername());
            for(String position: param.getPositions()){
                userEntityMapper.addUserPosition(UUID.randomUUID().toString(),param.getUsername(),position);
            }
        }
        //修改角色信息
        if(param.getRoles().length>0){
            rolesEntityMapper.deleteRoleUser(param.getUsername());
            for(String role: param.getRoles()){
                userEntityMapper.addUserRoles(UUID.randomUUID().toString(),param.getUsername(),role);
            }
        }

        return "新增成功";
    }

    /**
     * 根据部门编号查找人员
     * @param departid
     * @return
     */
    @Override
    public List<UserEntity> getUserByDepartId(Integer departid){
        return userEntityMapper.getUserByDepartId(departid);
    }

    /**
     * 修改人员信息
     * @param param
     * @return
     */
    @Override
    public String updateUser(UserParams param){
        UserEntity userEntity=new UserEntity();
        BeanUtils.copyProperties(param, userEntity);
        userEntity.setId(param.getId().longValue());

        if(SystemConfig.IsNullOrEmpty(param.getGuuid())){
            userEntity.setGuuid(UUID.randomUUID().toString());
            userEntityMapper.addDeptUser(userEntity);
        }
        userEntityMapper.updateUser(userEntity);

        //修改岗位信息
        if(!SystemConfig.IsNullOrEmpty(param.getPositions())) {
            if (param.getPositions().length > 0) {
                positionEntityMapper.deletePositionUser(param.getUsername());
                for (String position : param.getPositions()) {
                    userEntityMapper.addUserPosition(UUID.randomUUID().toString(), param.getUsername(), position);
                }
            }
        }
        //修改角色信息
        if(!SystemConfig.IsNullOrEmpty(param.getRoles())) {
            if (param.getRoles().length > 0) {
                rolesEntityMapper.deleteRoleUser(param.getUsername());
                for (String role : param.getRoles()) {
                    userEntityMapper.addUserRoles(UUID.randomUUID().toString(), param.getUsername(), role);
                }
            }
        }
        return "修改成功";
    }

    /**
     * 修改密码
     * @param username
     * @param oldPwd
     * @param newPwd
     * @return
     */
    @Override
    public String updatePwd(String username, String oldPwd, String newPwd){
        UserInfo userInfo = mapper.getUserInfo(username);
        AesPasswordEncoder encoder = new AesPasswordEncoder();
        if(encoder.matches(oldPwd,userInfo.getPassword())) {
            String newPassword=passwordEncoder.encode(newPwd);
            userEntityMapper.updatePwd(username,newPassword);
        }
        else{
            return "原密码不正确";
        }
        return "修改成功";
    }

    /**
     * 重置密码
     * @param username
     * @return
     */
    @Override
    public String resetPwd(String username){
        String newPassword=passwordEncoder.encode("1");
        userEntityMapper.updatePwd(username,newPassword);
        return "修改成功";
    }

    /**
     * 删除用户
     * @param userEntity
     * @return
     */
    @Override
    public String deleteUser(UserEntity userEntity){
        userEntityMapper.deleteUser(userEntity);
        return "删除成功";
    }
    @Override
    public List<GroupEntity> getGroupList(String name){
        return groupMapper.getGroupList(name);
    }

    /**
     * 获取用户分组列表
     * @param name
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public HashMap<String,Object> getGroupPage(String name, Integer page, Integer pageSize){
        HashMap<String,Object> hashMap=new HashMap<>();
        Integer indexStart=(page-1)*pageSize;
        List<GroupEntity> groupEntityList=groupMapper.getGroupPage(name,indexStart,pageSize);
        List<GroupEntity> groupEntities=groupMapper.getGroupList(name);
        hashMap.put("total",groupEntities.size());
        hashMap.put("list",groupEntityList);
        return hashMap;
    }
    /**
     * 获取分组内成员
     * @param groupId
     * @return
     */
    @Override
    public HashMap<String,Object> getUserByGorupId(String groupId, Integer page,
                                      Integer pageSize){
        HashMap<String,Object> hashMap=new HashMap<>();
        List<UserEntity> userEntities = groupMapper.getUserByGorupId(groupId);
        if(pageSize==0) {
            hashMap.put("total",userEntities.size());
            hashMap.put("list",userEntities);
        }
        else{
            Integer startIndex=(page-1)*pageSize;
            List<UserEntity> userEntityList=groupMapper.getGroupUserPage(groupId, startIndex, pageSize);
            hashMap.put("total",userEntities.size());
            hashMap.put("list",userEntityList);
        }
        return hashMap;
    }

    /**
     * 增加分组
     * @param groupEntity
     * @return
     */
    @Override
    public String addGroup(GroupEntity groupEntity){
        groupMapper.addGroup(groupEntity);
        return "添加成功";
    }

    /**
     * 更新分组
     * @param groupEntity
     * @return
     */
    @Override
    public String updataGroup(GroupEntity groupEntity){
        groupMapper.updataGroup(groupEntity);
        return "更新成功";
    }

    /**
     * 删除分组
     * @param groupEntity
     * @return
     */
    @Override
    public String deleteGroup(GroupEntity groupEntity){
        groupMapper.deleteGroup(groupEntity.getId());
        return "删除成功";
    }

    /**
     * 增加人员分组
     * @param param
     * @return
     */
    @Override
    public String addUserGroup(GroupUsersParams param){
        List<HashMap<String,Object>> hashMapList=new ArrayList<>();
        if(param.getUserDatalist().size()==0)
        {
            for (UserEntity user : param.getUserlist()) {
                HashMap<String,Object> hashMap = new HashMap<>();
                hashMap.put("id", UUID.randomUUID().toString());
                hashMap.put("username", user.getUsername());
                hashMap.put("groupid", param.getGroupid());
                hashMapList.add(hashMap);
            }
        }
        else {
            for (UserEntity userEntity : param.getUserDatalist()) {
                for (UserEntity user : param.getUserlist()) {
                    if (userEntity.getUsername().equals(user.getUsername()))
                        continue;
                    else {
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("id", UUID.randomUUID().toString());
                        hashMap.put("username", user.getUsername());
                        hashMap.put("groupid", param.getGroupid());
                        hashMapList.add(hashMap);
                    }
                }
            }
        }
        userGroupMapper.addUserGroup(hashMapList);
        return "新增成功";
    }

    /**
     * 删除分组人员
     * @param username
     * @param groupid
     * @return
     */
    @Override
    public String deleteUserGroup(String username, String groupid){
        userGroupMapper.deleteUserGroup(username,groupid);
        return "删除成功";
    }
}
