<template>
  <container type="ghost">
    <div class="theme-tab-container">
      <el-form ref="form" :inline="true" :model="queryParams" class="form-inline">
        <el-row>
          <el-col :span="6">
            <el-form-item label="分组名称" prop="name">
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
        default-expand-all
        :indent="36"
        style="width: 100%"
      >
        <el-table-column type="index" label="序号" width="100"></el-table-column>
        <el-table-column prop="id" label="分组编号" ></el-table-column>
        <el-table-column prop="name" label="分组名称"></el-table-column>
        <el-table-column prop="remark" label="分组描述"></el-table-column>
        <el-table-column label="操作"  align="center">
          <template slot-scope="scope">
            <div class="operation-btn">
              <el-button icon="el-icon-edit" @click="edit(scope.row)" type="text">编辑
              </el-button>
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
        :close-on-press-escape="false"
        :wrapperClosable="false"
        destroy-on-close
        size="60%"
        @close="cloaseDialog"
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
                <el-form-item label="分组名称" prop="name">
                  <el-input v-model="ruleForm.name" ></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item label="分组描述" prop="remark">
                  <el-input v-model="ruleForm.remark" ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="24">
                <template v-if="isAdd==false">
                  <el-button type="primary" @click="addGroupUser">添加组员</el-button>

                  <el-table row-key="id"  :data="list" stripe style="width: 100%;margin-top: 10px" ref="groupUser">
                    <el-table-column v-for="item in tableColumns" :key="item.prop" v-bind="item" align="center">
                      <template slot-scope="scope">
                        <div v-if="item.prop === 'index'">{{ scope.$index + 1 }}</div>
                        <div v-else-if="item.prop === 'state'">
                          <el-tag type="warning" v-if="scope.row.state==0">禁用</el-tag>
                          <el-tag>正常</el-tag>
                        </div>
                        <div v-else-if="item.prop === 'operation'" class="operation-btn">
                          <el-popconfirm title="您确定要删除吗？" @confirm="deleteGroupUsers(scope.row)">
                            <el-button icon="el-icon-delete" slot="reference" type="text" class="btn-danger">移除</el-button>
                          </el-popconfirm>
                        </div>
                        <div v-else>{{ scope.row[item.prop] }}</div>
                      </template>
                    </el-table-column>
                  </el-table>
                  <pagination
                    v-show="grouptotal>0"
                    :total="grouptotal"
                    :pageSizes="[5,10,15,20]"
                    :page.sync="groupParams.page"
                    :limit.sync="groupParams.pagesize"
                    @pagination="getGroupUser"
                  />
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
      <el-dialog
        :visible.sync="byUserFormModelVisible"
        :close-on-click-modal="false"
        title="选择人员"
        width="80%"
        append-to-body
        destroy-on-close
      >
        <el-form label-width="80px" style="width: 99%" size="mini">
          <rule-to-user ref="assigneeByUserRef"></rule-to-user>
        </el-form>
        <template slot="footer">
          <el-button size="mini" @click="byUserFormModelVisible = false">取 消</el-button>
          <el-button size="mini" type="primary" @click="saveAssigneeByUser()"
          >确 定</el-button>
        </template>
      </el-dialog>
    </div>
  </container>
</template>

<script>
import { getGroupPage, getUserByGorupId, addGroup, updataGroup, deleteGroup , addUserGroup, deleteUserGroup} from '@/api/orgm/group'
import Pagination from '@/components/Pagination';
import ruleToUser from '@/components/taskcomponents/ruleToUser.vue';

export default {
  name: "groups",
  data() {
    return {
      total:0,
      // 查询参数
      queryParams: {
        name:'',
        page: 1,
        pagesize: 10
      },
      title:'',
      isAdd:true,
      dataTable:[],
      dialogNewVisible: false,
      ruleForm: {
        id:'',
        name: '',
        remark:''
      },
      rules: {
        name: [
          { required: true, message: '请输入分组名称', trigger: 'blur' },
          { min: 3, max: 5, message: '长度在 3 到 5 个字符', trigger: 'blur' }
        ]
      },
      byUserFormModelVisible:false,
      list:[],
      selectData:[],
      grouptotal:0,
      groupParams:{
        groupId:'',
        page: 1,
        pagesize: 5
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
        },
        {
          label: '操作',
          prop: 'operation'
        }
      ],
    }
  },
  components: {
    Pagination,
    ruleToUser
  },//页面加载后渲染
  mounted(){
    this.loadData();
  },
  methods:{
    //dialog关闭事件
    cloaseDialog(){
      this.dialogNewVisible = false;
      this.loadData();
    },//初始化表格数据
    loadData(){
      getGroupPage(this.queryParams).then(res => {
        if(res.error=="200"){
          this.dataTable = res.result.list;
          this.total=res.result.total;
        }
        else{
          this.$message.error("获取失败");
        }
      })
    },
    reload(){
      this.loadData();
    },//新建
    openDialog(){
      this.title="新建";
      this.isAdd=true;
      this.ruleForm.name='';
      this.ruleForm.id='';
      this.groupParams.groupId='';
      this.dialogNewVisible = true;
    },//编辑
    async edit(row){
      this.isAdd=false;
      this.title="修改";
      this.ruleForm.name=row.name;
      this.ruleForm.id=row.id;
      this.ruleForm.remark=row.remark;
      this.groupParams.groupId=row.id;
      this.getGroupUser();
      this.dialogNewVisible = true;
    },
    async getGroupUser(){
      const res=await getUserByGorupId(this.groupParams);
      if(res.error=="200"){
        this.list=res.result.list;
        this.grouptotal=res.result.total;
      }
    },
    //新增
    add(){
      if(this.ruleForm.name==""){
        this.$message.error("请填写分组名称");
        return;
      }
      addGroup(this.ruleForm).then(res => {
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
      updataGroup(this.ruleForm).then(res => {
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
          deleteGroup(this.ruleForm).then(res => {
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
    search(type) {
      if (type === 'reset') {
        this.$refs.form.resetFields();
      }
      this.reload();
    },
    addGroupUser(){
      this.byUserFormModelVisible=true;
    },
    saveAssigneeByUser() {
      this.selectData = this.$refs.assigneeByUserRef.saveTaskUser();
      if(this.selectData.length<=0)
      {
        this.$message.warning("请选择要添加的成员");
        return;
      }
      addUserGroup({userlist:this.selectData,userDatalist:this.list,groupid:this.groupParams.groupId}).then(res => {
        if(res.error=="200"){
          this.$message.success("添加成功");
          this.getGroupUser();
        }
        else{
          this.$message.error("添加失败");
        }
      })
    },
    deleteGroupUsers(row){
      deleteUserGroup(row.username,this.groupParams.groupId).then(res => {
        if(res.error=="200"){
          this.$message.success("删除成功");
          this.getGroupUser();
        }
        else{
          this.$message.error("删除失败");
        }
      })
    }
  }

}

</script>

