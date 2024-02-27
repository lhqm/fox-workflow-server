<template>
  <container>

    <div class="btn-container">
      <el-button type="primary" plain icon="el-icon-plus" @click="openDialog('add')">新增</el-button>
    </div>

    <el-table
        :data="dataTable"
        row-key="id"
        :indent="36"
        default-expand-all
        :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
        style="width: 100%"
    >
        <el-table-column type="index" label="序号" width="100"></el-table-column>
        <el-table-column prop="id" label="编号" ></el-table-column>
        <el-table-column prop="name" label="名称"></el-table-column>
        <el-table-column prop="parentid" label="所属上级" v-if="false"></el-table-column>
        <el-table-column label="操作" align="center">
            <template slot-scope="scope">
                <div class="operation-btn">
                    <el-button icon="el-icon-edit"
                      @click="edit(scope.row.id,scope.row.name,scope.row.parentid)" type="text"
                    >编辑</el-button>
                    <el-popconfirm title="您确定要删除吗？"
                        @confirm="deleteById(scope.row.id,scope.row.name,scope.row.parentid)">
                        <el-button icon="el-icon-delete" slot="reference" type="text" class="btn-danger">删除</el-button>
                    </el-popconfirm>
                </div>
            </template>
      </el-table-column>
    </el-table>
    <el-dialog :title="title"  v-if="dialogNewSortVisible==true"
            :close-on-click-modal="false"
            :visible.sync="dialogNewSortVisible"
            @close="cloaseSortDialog" custom-class="center-dialog">
              <slot name="-" style="border: none;padding: 0px;">
                <el-form :model="ruleForm" :rules="rules" ref="ruleForm" style="width:95%" label-width="100px" class="demo-ruleForm">
                  <el-form-item label="名称" prop="name">
                    <el-input v-model="ruleForm.name" ></el-input>
                  </el-form-item>
                  <el-form-item label="所属上级" prop="flowSort">
                    <select-tree class="tree" v-model="value" :data="treedata" placeholder="请选择所属上级"></select-tree>
                  </el-form-item>

                  <el-form-item style="margin-right:100px">
                    <el-button v-if="isAdd==true" type="primary" @click="addCompany">确认</el-button>
                    <el-button v-if="isAdd==false" type="primary" @click="saveEdit">保存</el-button>
                    <el-button @click="cloaseSortDialog">取消</el-button>
                  </el-form-item>
                </el-form>
              </slot>
      </el-dialog>
    </container>
</template>

<script>
  import SelectTree from '@/components/selectTree/index.vue';
  import { getCompanyList, add, updateCompany, deleteCompany } from '@/api/orgm/orgm'

  export default {
    name: "company",
    data() {
      return {
          formInline: {
          name: ''
        },
        value:null,
        title:'',
        isAdd:true,
        treedata:[],
        dataTable:[],
        dialogNewSortVisible: false,
        ruleForm: {
          id:'',
          name: '',
          parentid: this.value
        },
        rules: {
          name: [
            { required: true, message: '请输入名称', trigger: 'blur' },
            { min: 1, message: '不能为空字符', trigger: 'blur' }
          ]
        }
      }
    },
    components: {
      SelectTree
    },//页面加载后渲染
    mounted(){
      this.loadData();
    },
    methods:{
      //dialog关闭事件
      cloaseSortDialog(){
        this.dialogNewSortVisible = false;
        this.loadSortData();
      },//初始化表格数据
      loadData(){
        getCompanyList().then(res => {
          if(res.error=="200"){
            this.dataTable = res.result;
            this.treedata = res.result;
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
        this.ruleForm.parentid=null;
        this.value=null;
        this.dialogNewSortVisible = true;
      },//编辑
      edit(id,name,parentId){
        this.isAdd=false;
        this.title="修改";
        this.ruleForm.name=name;
        this.ruleForm.id=id;
        if(parentId==0){
          this.ruleForm.parentid=null;
          this.value=null;
        }else
        {
          this.ruleForm.parentid=parentId;
          this.value=parentId;
        }
        this.dialogNewSortVisible = true;
      },//加载所属类型
      //新增
      addCompany(){
        if(this.value==null){
            this.ruleForm.parentid=0;
        }
        else{
            this.ruleForm.parentid=this.value;
        }
        add(this.ruleForm).then(res => {
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
        if(this.value==null){
            this.ruleForm.parentid=0;
        }
        else{
            this.ruleForm.parentid=this.value;
        }
        updateCompany(this.ruleForm).then(res => {
          if(res.error=="200"){
            this.$message.success("修改成功");
            this.dialogNewSortVisible = false;
            setTimeout(()=>{this.reload();},1000);
          }
          else{
            this.$message.error("增加失败");
          }
        })
      },//删除
      async deleteById(id,name,parentId){
        const res = await deleteCompany(this.ruleForm);
        if (res.error === 200) {
          this.$message.success(`删除成功`);
          this.search('reset');
        } else {
          this.$message.error(`删除失败`);
        }
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
