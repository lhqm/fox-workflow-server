<template>
  <container>
    <div class="app-container">
      <el-row :gutter="20">
        <!--公司数据-->
        <el-col :span="6" :xs="24">
          <div class="head-container">
            <el-input
              v-model="companyName"
              placeholder="请输入名称"
              clearable
              size="small"
              prefix-icon="el-icon-search"
              style="margin-bottom: 20px"
            />
          </div>
          <div class="head-container">
            <el-tree
              :data="companyOptions"
              :props="companyProps"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              :render-content="renderContent"
              ref="tree"
              default-expand-all
              highlight-current
              @node-click="handleNodeClick"
            />
          </div>
        </el-col>
        <el-col :span="18" :xs="24">
          <el-form ref="form" :inline="true" :model="searchForm" class="form-inline">
            <el-row>
              <el-col :span="12">
                <el-form-item label="部门名称" prop="name">
                  <el-input v-model="searchForm.name" prefix-icon="el-icon-search" placeholder="请输入部门名称"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12" class="btn-container">
                <el-button icon="el-icon-search" type="primary"  @click="search">查询</el-button>
                <el-button icon="el-icon-refresh"  @click="search('reset')">重置</el-button>
              </el-col>
            </el-row>
          </el-form>
          <div class="btn-container">
            <el-button type="primary" plain icon="el-icon-plus" @click="openDialog('add')">新增</el-button>
          </div>
          <el-table
            :data="dataTable"
            row-key="id"
            style="width: 100%"
          >
            <el-table-column type="index" label="序号" header-align="center" align="left" width="50"></el-table-column>
            <el-table-column prop="id" v-if="false" label="部门编号" ></el-table-column>
            <el-table-column prop="name" label="部门名称"></el-table-column>
            <el-table-column prop="parentid" label="所属上级" v-if="false"></el-table-column>
            <el-table-column prop="managername" label="部门负责人"></el-table-column>
            <el-table-column prop="parentid" label="所属上级" v-if="false"></el-table-column>
            <el-table-column prop="manager" label="部门负责人编号" v-if="false"></el-table-column>
            <el-table-column prop="leader"  v-if="false" label="分管领导编号"></el-table-column>
            <el-table-column prop="leadername" label="分管领导编号"></el-table-column>
            <el-table-column prop="companyid" label="所属组织编号" v-if="false"></el-table-column>
            <el-table-column prop="companyname" label="所属组织"></el-table-column>
            <el-table-column label="操作"  align="center">
              <template slot-scope="scope">
                <div class="operation-btn">
                  <el-button icon="el-icon-edit" @click="edit(scope.row)" type="text"
                  >编辑</el-button>
                  <el-popconfirm title="您确定要删除吗？"
                                 @confirm="deleteById(scope.row.id,scope.row.name,scope.row.parentid)">
                    <el-button icon="el-icon-delete" slot="reference" type="text" class="btn-danger">删除</el-button>
                  </el-popconfirm>
                </div>
              </template>
            </el-table-column>
          </el-table>

          <pagination
            v-show="total > 0"
            :total="total"
            :page.sync="searchForm.page"
            :limit.sync="searchForm.pagesize"
            @pagination="loadData"
          />
        </el-col>
      </el-row>
    </div>
    <el-drawer
        :title="title"
        v-if="dialogNewSortVisible==true"
        :visible.sync="dialogNewSortVisible"
        @close="cloaseSortDialog"
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
                label-suffix="："
                label-width="100px"
                style="width:95%"
            >
              <el-row>
                <el-col :span="12">
                  <el-form-item label="部门名称" prop="name">
                    <el-input v-model="ruleForm.name" ></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="所属上级" class="w-full px-1" prop="parentId">
                    <select-tree class="tree" v-model="ruleForm.parentid" :data="treedata" placeholder="请选择所属上级"></select-tree>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="12">
                  <el-form-item label="部门负责人" class="w-full px-1" prop="manager">
                    <el-select v-model="ruleForm.manager" placeholder="请选择" class="w-full">
                      <el-option
                        v-for="item in managerdata"
                        :key="item.username"
                        :value="item.username"
                        :label="item.name"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="分管领导" class="w-full px-1" prop="leader">
                    <el-select v-model="ruleForm.leader" placeholder="请选择" class="w-full">
                      <el-option
                        v-for="item in leaderdata"
                        :key="item.username"
                        :value="item.username"
                        :label="item.name"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24">
                  <el-form-item label="所属组织" class="w-full px-1" prop="companyid">
                    <select-tree class="tree" v-model="ruleForm.companyid" :data="companyOptions" placeholder="请选择所属组织"></select-tree>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24">
                  <template v-if="isAdd==false">
                    <span style="margin-bottom: 5px">所属成员列表</span>

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
                      @pagination="getDeptUser"
                    />
                  </template>
                </el-col>
              </el-row>
            </el-form>
        </div>
      <div class="drawer-footer">
        <el-button v-if="isAdd==true" type="primary" @click="add">确认</el-button>
        <el-button v-if="isAdd==false" type="primary" @click="saveEdit">保存</el-button>
        <el-button @click="cloaseSortDialog">取消</el-button>
      </div>
      </el-drawer>
  </container>
</template>

<script>
  import SelectTree from '@/components/selectTree/index.vue';
  import Pagination from '@/components/Pagination';
  import { getDepartmentlist, getOrgData, addDepartment, updateDepartment, delDepartment,getDeptUsers } from '@/api/orgm/orgm'

  export default {
    name: "departments",
    data() {
      return {
        formInline: {
          name: ''
        },
        companyName:'',
        companyOptions:[],
        companyProps:{
          children: "children",
          label: "name"
        },
        total:0,
        usertotal:0,
        userList:[],
        userParams:{
          id:null,
          page: 1,
          pagesize: 5
        },
        title:'',
        isAdd:true,
        treedata:[],
        dataTable:[],
        managerdata:[],
        leaderdata:[],
        dialogNewSortVisible: false,
        searchForm: {
          page: 1,
          pagesize: 10,
          name: '',
          companyid: ''
        },
        ruleForm: {
          id:'',
          name: '',
          parentid: null,
          manager:null,
          leader:null,
          companyid:null
        },
        rules: {
          name: [
            { required: true, message: '请输入部门名称', trigger: 'blur' },
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
    watch: {
      // 根据名称筛选部门树
      companyName(val) {
        this.$refs.tree.filter(val);
      }
    },
    components: {
      SelectTree,
      Pagination
    },//页面加载后渲染
    mounted(){
      this.loadData();
      this.initData();
    },
    methods:{
      //dialog关闭事件
      cloaseSortDialog(){
        this.dialogNewSortVisible = false;
        this.loadData();
      },//初始化表格数据
      loadData(){
        getDepartmentlist(this.searchForm).then(res=>{
          if(res.error=="200"){
            this.total=res.result.total;
            this.dataTable = res.result.list;
          }
          else{
            this.$message.error("获取失败");
          }
        });
      },
      initData(){
        getOrgData().then(res => {
          if(res.error=="200"){
            this.treedata = res.result.departList;
            this.companyOptions=res.result.companyList;
            this.managerdata=res.result.userList;
            this.leaderdata=res.result.userList;
          }
        })
      },
      async getDeptUser(){
        const res=await getDeptUsers(this.userParams);
        if(res.error=="200"){
          this.userList=res.result.list;
          this.usertotal=res.result.total;
        }
      },
      reload(){
        this.loadData();
      },//新建
      openDialog(){
        this.title="新建";
        this.isAdd=true;
        this.ruleForm.name='';
        this.ruleForm.parentid=null;
        this.ruleForm.companyid=null;
        this.ruleForm.manager=null;
        this.ruleForm.leader=null;
        this.userParams.id='';
        this.dialogNewSortVisible = true;
      },//编辑
      edit(row){
        this.isAdd=false;
        this.title="修改";
        this.ruleForm.name=row.name;
        this.ruleForm.id=row.id;
        this.userParams.id=row.id;
        this.ruleForm.companyid=row.companyid;
        this.ruleForm.manager=row.manager;
        this.ruleForm.leader=row.leader;
        if(row.parentid==0){
          this.ruleForm.parentid=null;
        }else
        {
          this.ruleForm.parentid=row.parentid;
        }
        this.getDeptUser();
        this.dialogNewSortVisible = true;
      },
      //新增
      add(){
        if(this.ruleForm.parentid==null)
        {
          this.ruleForm.parentid=0;
        }
        addDepartment(this.ruleForm).then(res => {
          if(res.error=="200"){
            this.$message.success("添加成功");
            this.dialogNewSortVisible = false;
            setTimeout(()=>{this.reload();},1000);
          }
          else{
            this.$message.error("增加失败");
          }
        })
      },//保存
      saveEdit(){
        if(this.ruleForm.parentid==null||this.ruleForm.parentid==''){
          this.ruleForm.parentid=0;
        }
        updateDepartment(this.ruleForm).then(res => {
          if(res.error=="200"){
            this.$message.success("修改成功");
            this.dialogNewSortVisible = false;
            setTimeout(()=>{this.reload();},1000);
          }
          else{
            this.$message.error("增加失败");
          }
        })
      },//删除分类
      async deleteById(id){
        const res = await delDepartment({id:id});
        if(res.error=="200"){
            this.$message.success("删除成功");
            this.search('reset');
        }
        else{
            this.$message.error("删除失败");
        }
      },
      search(type) {
        if (type === 'reset') {
          this.$refs.form.resetFields();
        }
        this.reload();
      },
      // 筛选节点
      filterNode(value, data) {
        if (!value) return true;
        return data.name.indexOf(value) !== -1;
      },
      // 节点单击事件
      handleNodeClick(data) {
        this.searchForm.companyid = data.id;
        this.search('search');
      },
      /** 显示label **/
      renderContent(h,{node}){
        return (
          <span class="custom-tree-node">
          <span title={node.label}>{node.label}</span>
        </span>
        );
      }
    }

  }

</script>

<style scoped>
.option {
        height: auto;
        line-height: 1;
        padding: 0;
        background-color: #fff;
    }
    .tree {
        font-weight: 400;
    }
</style>
