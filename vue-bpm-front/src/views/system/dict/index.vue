<template>
  <container type="ghost">
    <div class="theme-tab-container">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" >
        <el-form-item label="字典名称" prop="name">
          <el-input
            v-model="queryParams.name"
            placeholder="请输入字典名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="字典状态" clearable>
            <el-option
              v-for="dict in optionStatus"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="theme-main-container">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button type="primary" size="mini" plain icon="el-icon-plus"  @click="addNew()">新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="success" size="mini" plain icon="el-icon-plus"  @click="edit()">修改</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button type="warning" size="mini" plain icon="el-icon-plus"  @click="deleteById('more')">删除</el-button>
        </el-col>
      </el-row>
      <el-table
        :data="dataTable"
        row-key="id"
        style="width: 100%"
        @selection-change="handleSelectionChange"
        default-expand-all>
        <el-table-column type="selection" width="50"></el-table-column>
        <el-table-column type="index" align="center" label="序号" width="100"></el-table-column>
        <el-table-column prop="no" align="center" label="字典编号" ></el-table-column>
        <el-table-column prop="name" align="center" label="字典名称"></el-table-column>
        <el-table-column prop="dataType" align="center" label="字典类型" width="80">
          <template slot-scope="scope">
            <dict-tag :options="optionType" :value="scope.row.dataType"/>
          </template>
        </el-table-column>
        <el-table-column prop="status" align="center" label="状态" width="80">
          <template slot-scope="scope">
            <dict-tag :options="optionStatus" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime">
          <template slot-scope="scope">
            <span>{{ parseTime2(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="doc" align="center" label="值"></el-table-column>
        <el-table-column label="操作"  align="center">
          <template slot-scope="scope">
            <div class="operation-btn">
              <el-button icon="el-icon-edit" size="mini" @click="edit(scope.row)" type="text"
              >编辑</el-button>
              <el-popconfirm title="您确定要删除吗？"
                             @confirm="deleteById(scope.row)">
                <el-button icon="el-icon-delete" size="mini" slot="reference" type="text" class="btn-danger">删除</el-button>
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
    </div>

    <!-- 添加或修改菜单对话框 -->
    <el-dialog :title="title" :visible.sync="open"  append-to-body>
      <el-form style="width: 98%" ref="dialogForm" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="字典编号" prop="no">
              <el-input v-model="form.no" placeholder="字典编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="字典名称" prop="name">
              <el-input v-model="form.name" placeholder="字典名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="字典类型" prop="dataType">
              <el-radio-group v-model="form.dataType">
                <el-radio
                  v-for="dict in optionType"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24" prop="status">
            <el-form-item>
                <span slot="label">
                  <el-tooltip content="选择停用则字典数据在表单中不可被使用" placement="top">
                  <i class="el-icon-question"></i>
                  </el-tooltip>
                  字典状态
                </span>
              <el-radio-group v-model="form.status">
                <el-radio
                  v-for="dict in optionStatus"
                  :key="dict.value"
                  :label="dict.value"
                >{{dict.label}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item prop="doc">
              <span slot="label">
                  <el-tooltip content="如果是静态数据，直接填写：@0=女@1=男;如果是sql，直接填写sql语句，必须包含label,value两列;如果是接口，填写接口地址，返回的数组中包括label,value两个键值" placement="top">
                  <i class="el-icon-question"></i>
                  </el-tooltip>
                  值
                </span>
              <el-input v-model="form.doc" type="textarea" placeholder=""></el-input>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder=""></el-input>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </container>
</template>

<script>
import dictTag from "@/components/dictTag";
import {parseTime} from "@/utils/comm";
import Pagination from '@/components/Pagination';
import {addDict, deleteDict, getDictList} from "@/api/system/dicts";
import error from "@/plugin/error";

export default {
  name: "index",
  components:{dictTag,Pagination},
  data(){
    return{
      total:0,
      dataTable:[],
      selectData:[],
      // 是否显示弹出层
      open: false,
      // 弹出层标题
      title:"",
      // 查询参数
      queryParams: {
        name: "",
        status: "",
        page:1,
        pageSize:10
      },
      optionStatus:[
        {
          label:"停用",
          value:"0",
          listClass:"danger"
        },
        {
          label:"正常",
          value:"1",
          listClass: "primary"
        }
      ],
      optionType:[
        {
          label:"静态数据",
          value:"0",
          listClass:"success"
        },
        {
          label:"SQL数据",
          value:"1",
          listClass: "warning"
        }
        ,
        {
          label:"接口数据",
          value:"2",
          listClass: "danger"
        }
      ],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        no: [
          { required: true, message: "字典编号不能为空", trigger: "blur" }
        ],
        name: [
          { required: true, message: "字典名称不能为空", trigger: "blur" }
        ]
      }
    }
  },
  mounted(){
    this.loadData();
  },
  methods:{
    /**
     * 加载数据
     */
    loadData(){
      getDictList(this.queryParams).then(res => {
        if(res.error=="200"){
          this.dataTable =res.result.list;
          this.total=res.result.total;
        }
        else{
          this.$message.warning(error.msg);
        }
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.loadData();
    },
    /**
     * 新增
     */
    addNew(){
      this.$refs.queryForm.resetFields();
      this.reset();
      this.open = true;
      this.title="添加字典";
    },
    /**
     * 修改
     */
    edit(row){
      this.title="修改字典";
      if(row==undefined||row==""){
        if(this.selectData.length>0&&this.selectData.length>1){
          this.$message.warning("每次只能修改一条记录");
          return;
        }
        else if(this.selectData.length==1)
          this.form=this.selectData[0];
        else{
          this.$message.warning("请选择一条数据");
          return;
        }
      }
      else
        this.form=row;
      this.open = true;
    },
    /**
     * 删除
     */
    deleteById(row){
      if(row!="more"){
        this.selectData=[];
        this.selectData.push(row);
      }
      if(this.selectData.length<=0){
        return;
      }
      deleteDict(this.selectData).then(res => {
        if(res.error=="200"){
          this.$message.success("删除成功");
          this.loadData();
        }
        else{
          this.$message.warning(error.msg);
        }
      });
    },
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        no: undefined,
        name: "",
        dataType: "0",
        status: "1",
        doc: "",
        remark: ""
      };
    },
    handleSelectionChange(row){
      this.selectData=row;
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["dialogForm"].validate(valid => {
        if (valid) {
          if (this.title == "修改字典") {
            updateDict(this.form).then(response => {
              this.$message.success("修改成功");
              this.open = false;
              this.loadData();
            });
          } else {
            addDict(this.form).then(response => {
              this.$message.success("新增成功");
              this.open = false;
              this.loadData();
            });
          }
        }
      });
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.handleQuery();
    },
    parseTime2(data){
      return parseTime(data);
    },
  }
}
</script>

<style scoped>

</style>
