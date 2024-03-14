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
        <el-form-item label="流程名称" prop="flowName">
          <el-input
            v-model="queryParams.flowName"
            placeholder="请输入流程名称"
            clearable
            style="width: 240px"
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="processList" style="width: 100%">
        <el-table-column label="序号" type="index" />
        <el-table-column prop="id" v-if="false" />
        <el-table-column prop="taskid" v-if="false" />
        <el-table-column prop="proce_inst_id" v-if="false" />
        <el-table-column label="标题" prop="title" :show-overflow-tooltip="true" />
        <el-table-column label="任务名称" prop="taskName" :show-overflow-tooltip="true" />
        <el-table-column label="流程名称" prop="flowName" :show-overflow-tooltip="true" />
        <el-table-column label="申请时间" prop="createtime" :show-overflow-tooltip="true" />
        <el-table-column prop="act_type" v-if="false" />
        <el-table-column prop="form_type" v-if="false" />
        <el-table-column prop="form_url" v-if="false" />
        <el-table-column prop="data_json" v-if="false" />
        <el-table-column label="审核状态" prop="status" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <div slot="reference" class="name-wrapper">
              <el-tag type="medium" v-if="scope.row.proce_inst_id == undefined">未提交</el-tag>
              <el-tag type="medium" v-else-if="scope.row.endtime == undefined">审批中</el-tag>
              <el-tag
                type="warning"
                v-else-if="
                  scope.row.act_type != undefined &&
                  scope.row.act_type != '' &&
                  scope.row.act_type == 'returnWork'
                "
                >驳回</el-tag
              >
              <el-tag
                type="warning"
                v-else-if="
                  scope.row.act_type != undefined &&
                  scope.row.act_type != '' &&
                  scope.row.act_type == 'transfer'
                "
                >移交</el-tag
              >
              <el-tag
                type="warning"
                v-else-if="
                  scope.row.act_type != undefined &&
                  scope.row.act_type != '' &&
                  scope.row.act_type == 'countersign'
                "
                >加签</el-tag
              >
              <el-tag type="medium" v-else>审核通过</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class="operation-btn">
          <template slot-scope="scope">
            <el-button type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)"
              >审批</el-button
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
        :page.sync="queryParams.pageNum"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />

      <!-- 新增申请对话框 -->
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
        <reviewpage
          v-if="loading"
          @cloaseDialog="cloaseDialog"
          :procinstid="procinstid"
          :taskid="taskid"
          :dataJson="dataJson"
          :bid="bid"
          :processKey="processKey"
          :processName="processName"
          :mapList="mapList"
          :mapConfig="mapConfig"
          :form_type="form_type"
          :form_url="form_url"
        >
        </reviewpage>
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
  import reviewpage from '../reviewForm/reviewpage';
  import viewpage from '../reviewForm/viewpage';
  import Pagination from '@/components/Pagination';
  import { getHisFormJson, getTodoList } from '@/api/task/task';

  export default {
    name: 'todo',
    data() {
      return {
        // 遮罩层
        loading: true,
        dialogVisible: false,
        dialogView: false,
        processKey: '',
        mapConfig: Object,
        mapList: [],
        form_type: '',
        form_url: '',
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
          pageNum: 1,
          pageSize: 10,
          flowName: ''
        },
        // 表单参数
        form: {},
        dataJson: Object,
        // 表单校验
        rules: {
          dictName: [{ required: true, message: '字典名称不能为空', trigger: 'blur' }]
        }
      };
    },
    components: {
      viewpage,
      Pagination,
      reviewpage
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
        getTodoList(this.queryParams).then((res) => {
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
      // 查看
      handleView(row) {
        this.loading = true;
        this.showMapJSON(row, true);
      },
      showMapJSON(row, isView) {
        getHisFormJson('', row.taskid)
          .then((res) => {
            if (res.error == '200') {
              if (res.result.mapJson == '' || res.result.mapJson == undefined) {
                this.processKey = '';
                this.processName = '';
                this.mapConfig = {};
                this.mapList = [];
                this.bid = row.id;
                this.form_url = res.result.form_url;
                this.form_type = row.form_type;
                this.procinstid = row.proce_inst_id;
                this.taskid = row.taskid;
                this.dataJson = {};
                if (isView) {
                  this.dialogView = true;
                } else {
                  this.dialogVisible = true;
                }
              } else {
                const mapJson = JSON.parse(res.result.mapJson);
                let dj = {};
                if (row.data_json != '' && row.data_json != null && row.data_json != undefined) {
                  this.dataJson = JSON.parse(row.data_json);
                  dj = JSON.parse(row.data_json);
                }
                this.processKey = '';
                this.processName = '';
                this.mapConfig = mapJson.config;
                this.mapList = mapJson.list;
                this.bid = row.id;
                this.procinstid = row.proce_inst_id;
                this.taskid = row.taskid;
                this.dataJson = dj;
                this.form_url = res.result.mapJson;
                this.form_type = row.form_type;
                if (isView) {
                  this.dialogView = true;
                } else {
                  this.dialogVisible = true;
                }
              }
            }
          })
          .catch((error) => {});
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
