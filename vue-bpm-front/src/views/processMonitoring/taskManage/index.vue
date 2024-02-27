<template>
  <container>
    <div class="filter-container">
      <el-form
        :model="queryParams"
        ref="queryForm"
        :inline="true"
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
        <el-form-item label="部门" label-width="80px" prop="departid" >
          <select-tree class="tree"
                       v-model="queryParams.departid"
                       :data="deptList"
                       placeholder="请选择所属部门">
          </select-tree>
        </el-form-item>
        <el-form-item label="人员" prop="userid">
          <el-select v-model="queryParams.userid" >
            <el-option
              v-for="(item, index) in userList"
              :key="item.username"
              :label="item.name"
              :value="item.username"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
      <div class="btn-container">
        <el-button type="primary" plain icon="el-icon-upload" @click="openNewDialog('transfer')">工作移交</el-button>
        <el-button type="danger" plain icon="el-icon-s-check" @click="openNewDialog('setEnd')">批量结束</el-button>
      </div>
      <el-table
        v-loading="loading"
        :data="processList"
        @selection-change="handleSelectionChange"
        style="width: 100%"
      >
        <el-table-column type="selection" width="50"></el-table-column>
        <el-table-column label="序号" type="index" />
        <el-table-column prop="id" v-if="false" />
        <el-table-column prop="taskid" v-if="false" />
        <el-table-column prop="proce_inst_id" v-if="false" />
        <el-table-column
          label="标题"
          prop="title"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          label="任务名称"
          prop="taskName"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          label="流程名称"
          prop="flowName"
          :show-overflow-tooltip="true"
        />
        <el-table-column
          label="申请时间"
          prop="createtime"
          :show-overflow-tooltip="true"
        />
        <el-table-column prop="act_type" v-if="false" />
        <el-table-column prop="form_type" v-if="false" />
        <el-table-column prop="form_url" v-if="false" />
        <el-table-column prop="map_json" v-if="false" />
        <el-table-column prop="data_json" v-if="false" />
        <el-table-column
          label="审核状态"
          prop="status"
          :show-overflow-tooltip="true"
        >
          <template slot-scope="scope">
            <div slot="reference" class="name-wrapper">
              <el-tag type="medium" v-if="scope.row.proce_inst_id == undefined">未提交</el-tag>
              <el-tag type="medium" v-else-if="scope.row.endtime == undefined">审批中</el-tag>
              <el-tag type="warning" v-else-if="scope.row.act_type != undefined&&scope.row.act_type !=''&&scope.row.act_type =='returnWork'">驳回</el-tag>
              <el-tag type="warning" v-else-if="scope.row.act_type != undefined&&scope.row.act_type !=''&&scope.row.act_type=='transfer'">移交</el-tag>
              <el-tag type="warning" v-else-if="scope.row.act_type != undefined&&scope.row.act_type !=''&&scope.row.act_type=='countersign'">加签</el-tag>
              <el-tag type="medium" v-else>审核通过</el-tag>
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
        :page.sync="queryParams.page"
        :limit.sync="queryParams.pageSize"
        @pagination="getList"
      />

      <el-dialog
        v-if="dialogVisible == true"
        :visible.sync="dialogVisible"
        :title="dialogTitle"
        width="630px"
        destroy-on-close
        :close-on-click-modal="false"
        :close-on-press-escape="false"
      >
        <el-form
          ref="dataForm"
          :rules="rules"
          :model="transfer"
          label-suffix="："
          label-width="110px"
          class="dialog-form"
        >
          <el-form-item v-if="dialogType=='transfer'" label="移交至："  prop="countersignUser">
            <el-input type="text" readonly v-model="transfer.selectEmpName"></el-input>
            <el-input type="text" v-if="false" v-model="transfer.selectEmp"></el-input>
          </el-form-item>
          <el-form-item v-if="dialogType=='transfer'">
            <el-input
              placeholder="输入关键字进行过滤"
              v-model="transferFilterText">
            </el-input>

            <el-tree
              class="filter-tree"
              :data="selectList"
              :props="transferProps"
              :filter-node-method="transferFilterNode"
              @node-click="transferHandleNodeClick"
              ref="transferTree">
            </el-tree>
          </el-form-item>

          <el-form-item label="执行意见" prop="actionMsg">
            <el-input type="textarea" :rows="3" v-model="transfer.actionMsg"></el-input>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" v-if="dialogType=='transfer'" @click="transferClick">执行移交</el-button>
          <el-button type="primary" v-if="dialogType=='setEnd'" @click="setEndTaskClick">执行结束</el-button>
          <el-button @click="dialogVisible=false">取消</el-button>
        </div>
      </el-dialog>

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
import { getTaskManageListPage } from '@/api/process/processManage';
import ManageView from "@/views/processMonitoring/processManage/manageView";
import SelectTree from "@/components/selectTree";
import {getDeptUserTree, getOrgData} from "@/api/orgm/orgm";
import {getUserByDepartId} from "@/api/orgm/user";
import {setEndTaskBatch, transferBatch} from "@/api/task/task";

export default {
  name: 'taskManage',
  data() {
    return {
      // 遮罩层
      loading: true,
      dialogVisible: false,
      dialogTitle:'',
      dialogType:'',
      dialogView: false,
      processKey: '',
      mapConfig: Object,
      mapList: [],
      deptList:[],
      userList:[],
      selectList:[],
      form_type:'',
      form_url:'',
      processName: '',
      // 总条数
      total: 0,
      // 字典表格数据
      processList: [],
      selectData:[],
      taskid: '',
      procinstid: '',
      bid: '',
      // 查询参数
      queryParams: {
        page: 1,
        pageSize: 10,
        title: '',
        departid:0,
        userid:''
      },
      transferFilterText:'',
      transfer:{
        selectEmpName:'',
        selectEmp:'',
        actionMsg:''
      },
      transferProps: {
        children: 'children',
        label: 'name',
        id:'username',
        state:'state'
      },
      dataJson: Object,
      // 表单校验
      rules: {
        dictName: [{ required: true, message: '字典名称不能为空', trigger: 'blur' }]
      }
    };
  },
  components: {
    ManageView,
    viewpage,
    Pagination,
    SelectTree
  },
  created() {
    this.getList();
    this.getSelectData();
  },
  computed:{
    getDeptId: function() {
      return this.queryParams.departid
    }
  },
  watch:{
    getDeptId:{
      handler:function () {
        this.expandUsers();
      },
      deep: true
    }
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
      if(this.queryParams.departid>0){
        if(this.queryParams.userid==''||this.queryParams.userid==null){
          this.$message.error('请选择人员');
          return;
        }
      }
      getTaskManageListPage(this.queryParams).then((res) => {
        if (res.error == '200') {
          this.processList = res.result.list;
          this.total = res.result.total;
        } else {
          this.$message.error('获取失败');
        }
        this.loading = false;
      });
    },
    getSelectData() {
      getOrgData().then((res) => {
        this.deptList = res.result.departList;
      });
      getDeptUserTree().then((res) => {
        if (res.error == '200') {
          this.selectList = res.result;

        }
      })
    },
    expandUsers(){
      getUserByDepartId({departid:this.queryParams.departid}).then(res => {
        if(res.error=="200"){
          this.userList = res.result;
        }
      });
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.handleQuery();
    },
    //查看
    handleView(row) {
      this.loading = true;
      this.showMapJSON(row, true);
    },
    handleSelectionChange(row){
      this.selectData=row;
    },
    async showMapJSON(row, isView) {
      this.form_url=row.form_url;
      this.form_type=row.form_type;
      this.taskid = row.taskid;
      this.delete_reason=row.delete_reason_;
      if (row.map_json == '' || row.map_json == undefined) {
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
        this.procinstid = row.proce_inst_id;
        this.dataJson = dj;
        if (isView) {
          this.dialogView = true;
        } else {
          this.dialogVisible = true;
        }
      }
    },
    openNewDialog(type){
      if(type=="transfer")
        this.dialogTitle="移交";
      else
        this.dialogTitle="结束";
      this.dialogVisible = true;
      this.dialogType=type;
    },
    transferFilterNode(value, data) {
      if (!value) return true;
      return data.name.indexOf(value) !== -1;
    },
    transferHandleNodeClick(data){
      if(data.state==1) {
        this.transfer.selectEmp = data.username;
        this.transfer.selectEmpName = data.name;
      }
    },
    transferClick(){
      if(this.selectData.length<=0){
        this.$message.warning("请选择任务。");
        return;
      }
      else{
        transferBatch(
          this.transfer.actionMsg,
          this.transfer.selectEmp,
          this.selectData
        ).then((res) => {
          if (res.error == '200') {
            this.$message.success("当前步骤已完成加签，等待移交人进行审核。");
            this.cloaseDialog();
          } else {
            this.$message.warning("移交失败，请联系管理员。"+res.result);
          }
        });
      }
    },
    setEndTaskClick(){
      if(this.selectData.length<=0){
        this.$message.warning("请选择任务。");
        return;
      }
      else{
        setEndTaskBatch(
          this.selectData,
          this.transfer.actionMsg
        ).then((res) => {
          if (res.error == '200') {
            this.$message.success("当前流程已结束。");
            this.cloaseDialog();
          } else {
            this.$message.warning("结束失败，请联系管理员。"+res.result);
          }
        });
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
