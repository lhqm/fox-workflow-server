<template>
  <container>
    <div class="filter-container">
      <el-form :model="queryParams" ref="queryForm"  :inline="true" v-show="showSearch" label-width="68px">
        <el-form-item label="模块名称" prop="operModule">
          <el-input
            v-model="queryParams.operModule"
            clearable
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="操作描述" prop="operDesc">
          <el-input
            v-model="queryParams.operDesc"
            clearable
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item label="时间" prop="createTime">
          <el-date-picker
            v-model="queryParams.createTime"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            align="right">
          </el-date-picker>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search"  @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh"  @click="resetQuery">重置</el-button>

        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="operLogList" style="width: 100%">
        <el-table-column label="序号" type="index" />
        <el-table-column  prop="operId" v-if="false" />
        <el-table-column label="模块名称" prop="operModule" :show-overflow-tooltip="true" />
        <el-table-column label="操作描述"  prop="operDesc" :show-overflow-tooltip="true" />
        <el-table-column label="创建时间"  prop="createTime" :show-overflow-tooltip="true" />
        <el-table-column label="操作人帐号"  prop="operUserId" :show-overflow-tooltip="true" />
        <el-table-column prop="operReqParam" v-if="false" />
        <el-table-column  prop="operResParam" v-if="false" />
        <el-table-column label="操作类型"  prop="operType" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <div slot="reference" class="name-wrapper">
              <el-tag type="medium" v-if="scope.row.operType=='other'">默认操作</el-tag>
              <el-tag type="medium" v-if="scope.row.operType=='insert'">新增操作</el-tag>
              <el-tag type="medium" v-if="scope.row.operType=='update'">保存/修改操作</el-tag>
              <el-tag type="medium" v-if="scope.row.operType=='delete'">删除操作</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class="operation-btn">
          <template slot-scope="scope">
            <div class="operation-btn">
              <el-button
                type="text"
                icon="el-icon-view"
                @click="handleView(scope.row)"
              >查看</el-button>
              <el-popconfirm title="您确定要删除吗？"
                             @confirm="deleteById(scope.row.operId)">
                <el-button icon="el-icon-delete" slot="reference" type="text" class="btn-danger">删除</el-button>
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total>0"
        :total="total"
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />
      <el-drawer
        title="日志详情"
        :visible.sync="dialogVisible"
        :close-on-press-escape="false"
        :wrapperClosable="false"
        destroy-on-close
        size="50%"
      >
        <div class="drawer-content">
          <el-form
            ref="dataForm"
            :model="formData"
            label-suffix="："
            label-width="90px"
          >
            <el-form-item label="模块名称" prop="operModule">
              <el-input v-model="formData.operModule" disabled  />
            </el-form-item>
            <el-form-item label="操作描述" prop="operDesc">
              <el-input v-model="formData.operDesc" disabled  />
            </el-form-item>
            <el-form-item label="操作时间" prop="createTime">
              <el-input v-model="formData.createTime" disabled  />
            </el-form-item>
            <el-form-item label="操作员账号" prop="operUserId">
              <el-input v-model="formData.operUserId" disabled />
            </el-form-item>
            <el-form-item label="请求参数" prop="operReqParam">
              <el-input type="textarea" rows="7" disabled v-model="formData.operReqParam"  />
            </el-form-item>
            <el-form-item label="返回参数" prop="operResParam">
              <el-input type="textarea" rows="12" disabled v-model="formData.operResParam"  />
            </el-form-item>

          </el-form>
        </div>
        <div class="drawer-footer">
          <el-popconfirm title="您确定要删除吗？"
                         @confirm="deleteById(formData.operId)">
            <el-button type="danger" slot="reference">删除</el-button>
          </el-popconfirm>
          <el-button @click="dialogVisible = false"> 关闭 </el-button>
        </div>
      </el-drawer>
    </div>
  </container>
</template>

<script>
import Pagination from '@/components/Pagination';
import {findOperLogPage,deleteOperLog} from '@/api/system/operlog';

export default {
  name: "operlog",
  data() {
    return {
      // 遮罩层
      loading: true,
      dialogVisible:false,
      // 显示搜索条件
      showSearch: true,
      operLogList:[],
      // 总条数
      total: 0,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        operModule: '',
        operDesc:'',
        createTime: '',
        startTime:'',
        endTime:'',
        operId:''
      },
      formData:{
        operModule:'',
        operDesc:'',
        createTime: '',
        operUserId:'',
        operReqParam:'',
        operResParam:'',
        operId:''
      }
    };
  },
  components: {
    Pagination
  },
  created() {
    this.getList();
  },
  methods: {
    cloaseView(){
      this.dialogView=false;
      this.getList();
    },
    /** 查询列表 */
    getList() {
      this.loading = false;
      findOperLogPage(this.queryParams).then(res => {
        if(res.error=="200"){
          this.operLogList=res.result.list;
          this.total = res.result.total;
        }
        else{
          this.$message.error("获取失败");
        }
        this.loading = false;
      })
    },
    deleteById(operId){
      this.queryParams.operId=operId;
      deleteOperLog(this.queryParams).then(res => {
        if(res.error=="200"){
          this.$message.success("删除成功");
          this.dialogVisible = false;
          this.resetQuery();
        }
        else{
          this.$message.error("删除失败");
        }
        this.loading = false;
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      if(this.queryParams.createTime!=null
        ||this.queryParams.createTime!='null'
        ||this.queryParams.createTime!='') {
        this.queryParams.startTime = this.queryParams.createTime[0];
        this.queryParams.endTime = this.queryParams.createTime[1];
      }
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.handleQuery();
    },
    /** 查看按钮操作 */
    handleView(row){
      this.formData=row;
      this.dialogVisible=true;
    }
  }
};
</script>
