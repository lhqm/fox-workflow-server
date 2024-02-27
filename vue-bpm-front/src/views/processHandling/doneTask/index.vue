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
        <el-table-column prop="form_type" v-if="false" />
        <el-table-column prop="form_url" v-if="false" />
        <el-table-column label="标题" prop="title" :show-overflow-tooltip="true" />
        <el-table-column label="流程名称" prop="flowName" :show-overflow-tooltip="true" />
        <el-table-column label="申请时间" prop="createtime" :show-overflow-tooltip="true" />
        <el-table-column prop="act_type" v-if="false" />
        <el-table-column prop="map_json" v-if="false" />
        <el-table-column prop="data_json" v-if="false" />
        <el-table-column label="审核状态" prop="status" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <div slot="reference" class="name-wrapper">
              <el-tag type="medium" v-if="scope.row.endtime == undefined || scope.row.endtime == ''"
                >审批中</el-tag
              >
              <el-tag type="success" v-else>审核通过</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class="operation-btn">
          <template slot-scope="scope">
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
      <el-drawer
        v-if="dialogView == true"
        :visible.sync="dialogView"
        :with-header="false"
        title="查看申请"
        size="95%"
        class="radius"
        direction="btt"
        :append-to-body="false"
        :wrapperClosable="false"
        show-close="show-close"
        destroy-on-close
      >
        <viewpage
          @cloaseView="cloaseView"
          :procinstid="procinstid"
          :dataJson="dataJson"
          :processKey="processKey"
          :processName="processName"
          :mapList="mapList"
          :form_type="form_type"
          :form_url="form_url"
          :mapConfig="mapConfig"
        >
        </viewpage>
      </el-drawer>
    </div>
  </container>
</template>

<script>
  import viewpage from '../reviewForm/viewpage';
  import Pagination from '@/components/Pagination';
  import { taskDoneQuery } from '@/api/task/task';

  export default {
    name: 'done',
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
      Pagination
    },
    created() {
      this.getList();
    },
    methods: {
      cloaseView() {
        this.dialogView = false;
        this.getList();
      },
      /** 查询列表 */
      getList() {
        this.loading = false;
        taskDoneQuery(this.queryParams).then((res) => {
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
      /** 查看按钮操作 */
      handleView(row) {
        this.loading = true;
        if (row.map_json == '' || row.map_json == undefined) {
          this.processKey = '';
          this.processName = '';
          this.mapConfig = {};
          this.mapList = [];
          this.bid = row.id;
          this.procinstid = row.proce_inst_id;
          this.taskid = row.taskid;
          this.form_url = row.map_json;
          this.form_type = row.form_type;
          this.dataJson = {};
          this.dialogView = true;
        } else {
          const mapJson = JSON.parse(row.map_json);
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
          this.form_url = row.form_url;
          this.form_type = row.form_type;
          this.procinstid = row.proce_inst_id;
          this.taskid = row.taskid;
          this.dataJson = dj;
          this.dialogView = true;
        }
      }
    }
  };
</script>
