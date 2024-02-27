<template>
  <container>
    <div class="filter-container">
      <el-form
        :model="queryParams"
        ref="queryForm"
        :inline="true"
        v-show="showSearch"
        label-width="68px"
      >
        <el-form-item label="流程标题" prop="title">
          <el-input
            v-model="queryParams.title"
            placeholder="请输入流程标题"
            clearable
            style="width: 240px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="流程名称" prop="flowName">
          <el-input
            v-model="queryParams.flowName"
            placeholder="请输入流程名称"
            clearable
            style="width: 240px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="流程状态" prop="state">
          <el-select
            v-model="queryParams.state"
            placeholder="流程状态"
            clearable
            style="width: 240px"
          >
            <el-option value="0" label="审核中"/>
            <el-option value="1" label="已完成"/>
            <el-option value="2" label="已删除"/>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="processList"  style="width: 100%">
        <el-table-column label="序号"  type="index" />
        <el-table-column prop="proc_inst_id_" v-if="false" />
        <el-table-column prop="business_key_" v-if="false" />
        <el-table-column prop="key_" v-if="false" />
        <el-table-column prop="start_user_id_" v-if="false" />
        <el-table-column prop="delete_reason_" v-if="false" />
        <el-table-column
          label="标题"
          prop="title"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          label="发起人"
          prop="startname"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          label="流程名称"
          prop="name_"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          label="运行状态"
          prop="status"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <div slot="reference" class="name-wrapper">
              <el-tag type="medium" v-if="scope.row.end_time_ == undefined||scope.row.end_time_ == ''">审核中</el-tag>
              <el-tag type="danger" v-else-if="scope.row.delete_reason_!=undefined && scope.row.delete_reason_ != '' && scope.row.delete_reason_ == 'deleteTask'">已删除</el-tag>
              <el-tag type="warning" v-else-if="scope.row.delete_reason_!=undefined && scope.row.delete_reason_ != '' && scope.row.delete_reason_ == 'refuse'">已拒绝</el-tag>
              <el-tag type="success" v-else>已完成</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          label="启动时间"
          align="center"
          prop="start_time_"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          label="结束时间"
          align="center"
          prop="end_time_"
          :show-overflow-tooltip="true"
        />


        <el-table-column label="操作" align="center" class="operation-btn">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-s-tools" @click="handleUpdate(scope.row)"
            >管理</el-button
            >
            <el-button type="text" icon="el-icon-view" @click="handleView(scope.row)"
            >查看</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.page"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />

      <!-- 流程管理对话框 -->
      <el-drawer
        v-if="dialogVisible == true"
        :visible.sync="dialogVisible"
        :with-header="false"
        title="审核"
        size="95%"
        class="radius"
        direction="btt"
        :append-to-body="false"
        :wrapperClosable="false"
        destroy-on-close
      >
        <manage-view
          v-if="loading"
          @cloaseDialog="cloaseDialog"
          :procinstid="procinstid"
          :taskid="taskid"
          :dataJson="dataJson"
          :bid="bid"
          :ruleForm="ruleForm"
          :processKey="processKey"
          :processName="processName"
          :mapList="mapList"
          :mapConfig="mapConfig"
          :form_type="form_type"
          :delete_reason="delete_reason"
          :form_url="form_url"
        >
        </manage-view>
        <div class="drawer-footer">
          <el-button @click="cloaseDialog">取消</el-button>
        </div>
      </el-drawer>

      <el-drawer
        v-if="dialogView == true"
        :visible.sync="dialogView"
        :with-header="false"
        title="查看"
        size="95%"
        class="radius"
        direction="btt"
        :append-to-body="false"
        :wrapperClosable="false"
        destroy-on-close
      >
        <viewpage
          v-if="loading"
          @cloaseView="cloaseView"
          :procinstid="procinstid"
          :dataJson="dataJson"
          :processKey="processKey"
          :processName="processName"
          :mapList="mapList"
          :form_type="form_type"
          :form_url="form_url"
          :mapConfig="mapConfig"
        ></viewpage>
      </el-drawer>
    </div>
  </container>
</template>

<script>
import viewpage from '../../processHandling/reviewForm/viewpage';
import Pagination from '@/components/Pagination';
import { getProcessManageListPage, getManageParams } from '@/api/process/processManage';
import ManageView from "@/views/processMonitoring/processManage/manageView";

export default {
  name: 'processManage',
  data() {
    return {
      // 遮罩层
      loading: true,
      dialogVisible: false,
      dialogView: false,
      processKey: '',
      mapConfig: Object,
      mapList: [],
      form_type:'',
      form_url:'',
      delete_reason:'',
      processName: '',
      // 选中数组
      ids: [],
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 字典表格数据
      processList: [],
      taskid: '',
      procinstid: '',
      bid: '',
      // 查询参数
      queryParams: {
        page: 1,
        pageSize: 10,
        title: '',
        state:'',
        flowName:''
      },
      // 表单参数
      form: {},
      dataJson: Object,
      // 表单校验
      rules: {
        dictName: [{ required: true, message: '字典名称不能为空', trigger: 'blur' }]
      },
      ruleForm:{
        title:'',
        state:'',
        startname:'',
        start_time:'',
        flowname:'',
        end_time:'',
        task_def_name:'',
        task_user:'',
        task_state:''
      }
    };
  },
  components: {
    ManageView,
    viewpage,
    Pagination
  },
  created() {
    this.getList();
  },
  methods: {
    cloaseDialog() {
      this.dialogVisible = false;
      this.getList();
    },
    cloaseView() {
      this.dialogView = false;
      this.getList();
    },
    /** 查询字典类型列表 */
    getList() {
      this.loading = false;
      getProcessManageListPage(this.queryParams).then((res) => {
        if (res.error == '200') {
          this.processList = res.result.list;
          this.total = res.result.total;
        } else {
          this.$message.error('获取失败');
        }
        this.loading = false;
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.handleQuery();
    },
    /** 审核按钮操作 */
    handleUpdate(row) {
      this.loading = true;
      this.showMapJSON(row, false);
    },
    //查看
    handleView(row) {
      this.loading = true;
      this.showMapJSON(row, true);
    },
    async showMapJSON(row, isView) {
      const res=await getManageParams(row.business_key_,row.proc_inst_id_);

      if(res.error == '200'){
        this.form_url=res.result.form_url;
        this.form_type=res.result.form_type;
        this.taskid = res.result.taskid;
        this.ruleForm.task_def_name=res.result.task_def_name;
      }
      else{
        this.form_url='';
        this.form_type='';
        this.taskid = '';
        this.ruleForm.task_def_name='';
      }
      this.ruleForm.title=row.title;
      this.ruleForm.end_time=row.end_time_;
      this.ruleForm.flowname=row.name_;
      this.ruleForm.start_time=row.start_time_;
      this.ruleForm.task_user=row.startname;
      this.delete_reason=row.delete_reason_;
      if(row.end_time_ == undefined||row.end_time_ == ''){
        this.ruleForm.state="运行中";
      }
      else{
        this.ruleForm.state="已完成";
      }
      if (res.result.map_json == '' || res.result.map_json == undefined) {
        this.processKey = '';
        this.processName = '';
        this.mapConfig = {};
        this.mapList = [];
        this.bid = row.business_key_;
        this.procinstid = row.proc_inst_id_;
        this.dataJson = {};
        if (isView) {
          this.dialogView = true;
        } else {
          this.dialogVisible = true;
        }
      } else {
        const mapJson = JSON.parse(res.result.map_json);
        let dj = {};
        if (res.result.data_json != '' && res.result.data_json != null && res.result.data_json != undefined) {
          this.dataJson = JSON.parse(res.result.data_json);
          dj = JSON.parse(res.result.data_json);
        }
        this.processKey = '';
        this.processName = '';
        this.mapConfig = mapJson.config;
        this.mapList = mapJson.list;
        this.bid = row.business_key_;
        this.procinstid = row.proc_inst_id_;
        this.dataJson = dj;
        if (isView) {
          this.dialogView = true;
        } else {
          this.dialogVisible = true;
        }
      }
    }
  }
};
</script>
<style lang="scss">
.el-drawer__header {
  margin-bottom: 0px !important;
}
.v-modal {
  z-index: 2000 !important;
}
</style>
