<template>
  <container type="ghost">
    <div class="theme-tab-container">
        <el-form ref="form" :inline="true" :model="queryParams" class="form-inline">
            <el-row>
                <el-col :span="6">
                    <el-form-item label="角色名称" prop="name">
                        <el-input placeholder="请输入内容" v-model="queryParams.name"  prefix-icon="el-icon-search" ></el-input>
                    </el-form-item>
                </el-col>
                <el-col :span="18" class="btn-container">
                    <el-button icon="el-icon-search" type="primary"  @click="search">查询</el-button>
                    <el-button icon="el-icon-refresh"  @click="search('reset')">重置</el-button>
                </el-col>
            </el-row>
        </el-form>
    </div>
    <div class="theme-main-container">
      <div class="btn-container">
        <el-button type="primary" plain icon="el-icon-plus" @click="openDialog('add')">新增</el-button>
      </div>
      <el-table
        :data="dataTable"
        row-key="id"
        style="width: 100%"
        default-expand-all>
        <el-table-column type="index" label="序号" width="100"></el-table-column>
        <el-table-column prop="id" label="角色编号" ></el-table-column>
        <el-table-column prop="name" label="角色名称"></el-table-column>
        <el-table-column prop="data_scope" label="数据权限范围" v-if="false"></el-table-column>
        <el-table-column label="操作"  align="center">
          <template slot-scope="scope">
            <div class="operation-btn">
                <el-button icon="el-icon-edit" @click="edit(scope.row)" type="text"
                    >编辑</el-button>
                <el-popconfirm title="您确定要删除吗？"
                    @confirm="deleteById(scope.row.id,scope.row.name)">
                    <el-button icon="el-icon-delete" slot="reference" type="text" class="btn-danger">删除</el-button>
                </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.page"
          :limit.sync="queryParams.pagesize"
          @pagination="loadData"
        />

      <el-drawer
        :title="title"
        v-if="dialogNewVisible==true"
        :visible.sync="dialogNewVisible"
        @close="cloaseDialog"
        :close-on-press-escape="false"
        :wrapperClosable="false"
        destroy-on-close
        size="50%"
      >
        <div class="drawer-content" style="height: 100%;">
          <el-form
            :model="ruleForm"
            :rules="rules"
            ref="ruleForm"
            style="width:95%"
            label-width="100px"
            class="demo-ruleForm"
          >
            <el-row>
              <el-col :span="12">
                <el-form-item label="角色名称" prop="name">
                  <el-input v-model="ruleForm.name" ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="角色编号" prop="id" v-if="isAdd==false">
                  <el-input readonly v-model="ruleForm.id" ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <template v-if="isAdd==false">
                  <div class="filter-container">
                    <el-tabs v-model="subActiveName" type="card">
                      <el-tab-pane label="菜单权限" name="1">
                        <el-form-item label="菜单权限">
                          <el-checkbox v-model="menuExpand" @change="handleCheckedTreeExpand($event, 'menu')">展开/折叠</el-checkbox>
                          <el-checkbox v-model="menuNodeAll" @change="handleCheckedTreeNodeAll($event, 'menu')">全选/全不选</el-checkbox>
                          <el-tree
                            class="tree-border"
                            :data="menuOptions"
                            show-checkbox
                            ref="menu"
                            node-key="menuId"
                            :check-strictly="!ruleForm.menuCheckStrictly"
                            empty-text="加载中，请稍候"
                            :props="defaultProps"
                          ></el-tree>
                        </el-form-item>
                      </el-tab-pane>
                      <el-tab-pane label="数据权限" name="2">
                        <el-form-item label="权限范围">
                          <el-select v-model="ruleForm.dataScope" @change="dataScopeSelectChange">
                            <el-option
                              v-for="item in dataScopeOptions"
                              :key="item.value"
                              :label="item.label"
                              :value="item.value"
                            ></el-option>
                          </el-select>
                        </el-form-item>
                        <el-form-item label="数据权限" v-show="ruleForm.dataScope == 2">
                          <el-checkbox v-model="deptExpand" @change="handleCheckedTreeExpand($event, 'dept')">展开/折叠</el-checkbox>
                          <el-checkbox v-model="deptNodeAll" @change="handleCheckedTreeNodeAll($event, 'dept')">全选/全不选</el-checkbox>
                          <el-tree
                            class="tree-border"
                            :data="deptTreeOptions"
                            show-checkbox
                            ref="deptTree"
                            node-key="id"
                            :check-strictly="!ruleForm.deptCheckStrictly"
                            empty-text="加载中，请稍候"
                            :props="deptDefaultProps"
                          ></el-tree>
                        </el-form-item>
                      </el-tab-pane>
                      <el-tab-pane label="成员列表" name="3">
                        <el-table row-key="id"  :data="userList" stripe style="width: 100%;margin-top: 10px" ref="groupUser">
                          <el-table-column v-for="item in tableColumns" :key="item.prop" v-bind="item" align="center">
                            <template slot-scope="scope">
                              <div v-if="item.prop === 'index'">{{ scope.$index + 1 }}</div>
                              <div v-else-if="item.prop === 'state'">
                                <el-tag type="warning" v-if="scope.row.state==0">禁用</el-tag>
                                <el-tag>正常</el-tag>
                              </div>
                              <div v-else>{{ scope.row[item.prop] }}</div>
                            </template>
                          </el-table-column>
                        </el-table>
                        <pagination
                          v-show="usertotal>0"
                          :total="usertotal"
                          :pageSizes="[5,10,15,20]"
                          :page.sync="userParams.page"
                          :limit.sync="userParams.pagesize"
                          @pagination="getRoleUser"
                        />
                      </el-tab-pane>
                    </el-tabs>
                  </div>
                </template>
              </el-col>
            </el-row>
          </el-form>
        </div>
        <div class="drawer-footer">
          <el-button v-if="isAdd==true" type="primary" @click="add">确认</el-button>
          <el-button v-if="isAdd==false" type="primary" @click="saveEdit">保存</el-button>
          <el-button @click="cloaseDialog">取消</el-button>
        </div>
      </el-drawer>
    </div>
  </container>
</template>

<script>
import {rolesListPage, addRoles, updateRoles, deleteRoles,
  getRoleUsers, getDepartmentlist,roleDeptTreeselect} from '@/api/orgm/orgm'
import {getMenuList,roleMenuTreeselect} from "@/api/system/menu";
import {handleTree} from "@/utils/comm";

  import Pagination from '@/components/Pagination';
  export default {
    name: "role",
    data() {
      return {
        title:'',
        isAdd:true,
        dataTable:[],
        total:0,
        usertotal:0,
        userList:[],
        subActiveName:"1",
        userParams:{
          id:null,
          page: 1,
          pagesize: 5
        },
        // 查询参数
        queryParams: {
          name:'',
          page: 1,
          pagesize: 10
        },
        dialogNewVisible: false,
        menuExpand: false,
        menuNodeAll: false,
        deptExpand: true,
        deptNodeAll: false,
        //部门树
        deptTreeOptions:[],
        deptDefaultProps: {
          children: "children",
          label: "name"
        },
        //菜单列表
        menuOptions:[],
        defaultProps: {
          children: "children",
          label: "menuName"
        },
        // 数据范围选项
        dataScopeOptions: [
          {
            value: "1",
            label: "全部数据权限"
          },
          {
            value: "2",
            label: "自定数据权限"
          },
          {
            value: "3",
            label: "本部门数据权限"
          },
          {
            value: "4",
            label: "本部门及以下数据权限"
          },
          {
            value: "5",
            label: "仅本人数据权限"
          }
        ],
        ruleForm: {
          id:'',
          name: '',
          dataScope:'',
          menuIds:[],
          menuCheckStrictly:true,
          deptCheckStrictly:true
        },
        rules: {
          name: [
            { required: true, message: '请输入角色名称', trigger: 'blur' },
            { min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
          ]
        },
        tableColumns: [
          {
            label: '序号',
            prop: 'index',
            width:'50px'
          },
          {
            label: '人员账号',
            prop: 'username'
          },
          {
            label: '人员姓名',
            prop: 'name'
          },
          {
            label: '状态',
            prop: 'state'
          }
        ],
      }
    },
    components: {
      Pagination
    },//页面加载后渲染
    created(){
      this.loadData();
      this.getMenuTreeselect();
      this.getDeptTree();
    },
    methods:{
      //dialog关闭事件
      cloaseDialog(){
        this.dialogNewVisible = false;
        this.subActiveName="1";
        this.loadData();
      },//初始化表格数据
      loadData(){
        rolesListPage(this.queryParams).then(res => {
          if(res.error=="200"){
            this.dataTable = res.result.list;
            this.total=res.result.total;
          }
          else{
            this.$message.error("获取失败");
          }
        })
      },
      /** 查询菜单树结构 */
      getMenuTreeselect() {
        getMenuList({menuName:"",visible:undefined}).then(response => {
          this.menuOptions = handleTree(response.result, "menuId");
        });
      },
      getDeptTree(){
        getDepartmentlist({name:"",companyid:"",page:0,pagesize:0}).then(res=>{
          if(res.error=="200"){
            this.deptTreeOptions = handleTree(res.result.list, "id","parentid");
          }
        });
      },
      async getRoleUser(){
        const res=await getRoleUsers(this.userParams);
        if(res.error=="200"){
          this.userList=res.result.list;
          this.usertotal=res.result.total;
        }
      },
      reload(){
        this.loadData();
        this.getMenuTreeselect();
      },//新建
      openDialog(){
        this.title="新建";
        this.isAdd=true;
        this.ruleForm.name='';
        this.ruleForm.id='';
        this.userParams.id='';
        this.getMenuTreeselect();
        this.dialogNewVisible = true;
      },//编辑
      edit(row){
        this.isAdd=false;
        this.title="修改";
        this.ruleForm.name=row.name;
        this.ruleForm.id=row.id;
        this.userParams.id=row.id;
        this.ruleForm.dataScope=row.data_scope;
        this.getRoleUser();
        const roleMenu = this.getRoleMenuTreeselect(row.id);
        const dataScope=this.getRoleDeptTreeselect(row.id);
        let checkedKeys=[];
        this.$nextTick(() => {
          roleMenu.then(res => {
            let checkedKeys = res;
            checkedKeys.forEach((v) => {
              this.$nextTick(()=>{
                this.$refs.menu.setChecked(v, true ,false);
              })
            })
          });
        });
        //数据范围回显
        this.$nextTick(()=>{
          dataScope.then((data)=>{
            let res = data;
            res.forEach((v) => {
              let deptRes={
                role_Id:"",
                id:0
              };
              deptRes.role_Id=v.role_Id;
              deptRes.id=parseInt(v.dept_Id);
              checkedKeys.push(deptRes);
            });
            checkedKeys.forEach((v) => {
              this.$nextTick(()=>{
                this.$refs.deptTree.setChecked(v, true ,false);
              })
            })
          });
        });
        this.dialogNewVisible = true;
      },//加载所属类型
      //新增
      add(){
        if(this.ruleForm.name==""){
          this.$message.error("请填写角色名称");
          return;
        }
        addRoles(this.ruleForm).then(res => {
          if(res.error=="200"){
            this.$message.success("添加成功");
            this.dialogNewVisible = false;
            setTimeout(()=>{this.reload();},1000);
          }
          else{
            this.$message.error("增加失败");
          }
        })
      },//保存
      saveEdit(){
        this.ruleForm.menuIds=this.getMenuAllCheckedKeys();
        this.ruleForm.depts=this.getDeptAllCheckedKeys();
        updateRoles(this.ruleForm).then(res => {
          if(res.error=="200"){
            this.$message.success("修改成功");
            this.dialogNewVisible = false;
            setTimeout(()=>{this.reload();},1000);
          }
          else{
            this.$message.error("修改失败");
          }
        })
      },//删除
      deleteById(id,name){
        this.$confirm("确定要删除["+name+"]?", "提示", {
						confirmButtonText: "确定",
						cancelButtonText: "取消",
						type: "warning"
					})
					.then(() => {
						this.ruleForm.name=name;
            this.ruleForm.id=id;
            deleteRoles(this.ruleForm).then(res => {
              if(res.error=="200"){
                this.$message.success("删除成功");
                this.dialogNewVisible = false;
                setTimeout(()=>{this.reload();},1000);
              }
              else{
                this.$message.error("删除失败");
              }
            })
					})
					.catch(() => {
            this.ruleForm.name='';
            this.ruleForm.id='';
          });
      },
      /** 选择角色权限范围触发 */
      dataScopeSelectChange(value) {
        if(value !== '2') {
          this.$refs.deptTree.setCheckedKeys([]);
        }
      },
      // 树权限（展开/折叠）
      handleCheckedTreeExpand(value, type) {
        if (type == 'menu') {
          let treeList = this.menuOptions;
          for (let i = 0; i < treeList.length; i++) {
            this.$refs.menu.store.nodesMap[treeList[i].menuId].expanded = value;
          }
        } else if (type == 'dept') {
          let treeList = this.deptTreeOptions;
          for (let i = 0; i < treeList.length; i++) {
            this.$refs.deptTree.store.nodesMap[treeList[i].id].expanded = value;
          }
        }
      },
      // 树权限（全选/全不选）
      handleCheckedTreeNodeAll(value, type) {
        if (type == 'menu') {
          this.$refs.menu.setCheckedNodes(value ? this.menuOptions: []);
        } else if (type == 'dept') {
          this.$refs.deptTree.setCheckedNodes(value ? this.deptTreeOptions: []);
        }
      },
      // 所有菜单节点数据
      getMenuAllCheckedKeys() {
        // 目前被选中的菜单节点
        let checkedKeys = this.$refs.menu.getCheckedKeys();
        // 半选中的菜单节点
        let halfCheckedKeys = this.$refs.menu.getHalfCheckedKeys();
        checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
        return checkedKeys;
      },
      // 所有部门节点数据
      getDeptAllCheckedKeys() {
        // 目前被选中的部门节点
        let checkedKeys = this.$refs.deptTree.getCheckedKeys();
        // 半选中的部门节点
        let halfCheckedKeys = this.$refs.deptTree.getHalfCheckedKeys();
        checkedKeys.unshift.apply(checkedKeys, halfCheckedKeys);
        return checkedKeys;
      },
      /** 根据角色ID查询菜单树结构 */
      getRoleMenuTreeselect(roleId) {
        return roleMenuTreeselect(roleId).then(response => {
          return response.result;
        });
      },
      /** 根据角色ID查询部门树结构 */
      getRoleDeptTreeselect(roleId) {
        return roleDeptTreeselect(roleId).then(response => {
          return response.result;
        });
      },
      search(type) {
        if (type === 'reset') {
          this.$refs.form.resetFields();
        }
        this.reload();
      }
    }

  }

</script>
