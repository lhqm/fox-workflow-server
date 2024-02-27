<template>
  <container type="ghost">
    <el-row :gutter="20">
      <el-col :span="12" style="padding-right: 5px;">
        <div class="theme-main-container">
          <header>
            流程审核情况
          </header>
          <el-divider></el-divider>
          <el-row :gutter="20">
            <el-col :span="12" style="height: 40vh">
              <el-card style="background: #3D96F9;color: white">
                <div slot="header" >
                  <span>审核通过</span>
                </div>
                <el-row>
                  <el-col :span="20">
                    <div style="text-align: center;font-size: 1.5em">{{processManages}}</div>
                  </el-col>
                  <el-col :span="4">
                    <div style="text-align: right; "><img src="/image/common/flownum.png"></div>
                  </el-col>
                </el-row>
              </el-card>
            </el-col>
            <el-col :span="12" >
              <el-card style="background: #F5B658;color: white">
                <div slot="header" >
                  <span>被拒绝</span>
                </div>
                <el-row>
                  <el-col :span="20">
                    <div style="text-align: center;font-size: 1.5em">{{refuseList}}</div>
                  </el-col>
                  <el-col :span="4">
                    <div style="text-align: right; "><img src="/image/common/procinst.png"></div>
                  </el-col>
                </el-row>
              </el-card>
            </el-col>
          </el-row>
        </div>
      </el-col>
      <el-col :span="12" style="padding-left: 5px;height: 40vh">
        <div class="theme-main-container">
          <header>
            流程审核情况
          </header>
          <el-divider></el-divider>
          <pie-chart :pie-data="pieData" :pie-name="pieName" />
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="24" style="padding-top: 10px;">
        <div class="theme-main-container">
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
                  <el-option value="2" label="已拒绝"/>
                </el-select>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
                <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
              </el-form-item>
            </el-form>

            <el-table v-loading="loading" :data="list" stripe style="width: 100%">
              <el-table-column label="序号"  type="index" />
              <el-table-column  prop="id" v-if="false" />
              <el-table-column  prop="taskid" v-if="false" />
              <el-table-column  prop="proce_inst_id" v-if="false" />
              <el-table-column prop="form_type" v-if="false" />
              <el-table-column prop="form_url" v-if="false" />
              <el-table-column label="标题"  prop="title" :show-overflow-tooltip="true" />
              <el-table-column label="流程名称"  prop="flowName" :show-overflow-tooltip="true" />
              <el-table-column label="申请时间"  prop="createtime" :show-overflow-tooltip="true" />
              <el-table-column prop="act_type" v-if="false" />
              <el-table-column  prop="map_json" v-if="false" />
              <el-table-column  prop="data_json" v-if="false" />
              <el-table-column label="审核状态"  prop="status" :show-overflow-tooltip="true">
                <template slot-scope="scope">
                  <div slot="reference" class="name-wrapper">
                    <el-tag type="medium" v-if="scope.row.endtime==undefined||scope.row.endtime==''">审批中</el-tag>
                    <el-tag type="success" v-else>审核通过</el-tag>
                  </div>
                </template>
              </el-table-column>
              <el-table-column label="操作" align="center" class="operation-btn">
                <template slot-scope="scope">
                  <el-button
                    type="text"
                    icon="el-icon-view"
                    @click="handleView(scope.row)"
                  >查看</el-button>
                </template>
              </el-table-column>
            </el-table>

            <pagination
              v-show="total > 0"
              :total="total"
              :page.sync="queryParams.page"
              :limit.sync="queryParams.pageSize"
              :page-sizes="[5,10,15,20,25]"
              @pagination="initData"
            />
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
        </div>
      </el-col>
    </el-row>

  </container>
</template>

<script>
import PieChart from "@/components/echartscomponents/PieChart";
import viewpage from '../../processHandling/reviewForm/viewpage';
import { getMyProcessPage} from "@/api/process/processManage";
import Pagination from '@/components/Pagination';
export default {
  name: "userProceStatAnalysis",
  data(){
    return{
      total:0,
      list:[],
      processManages:0,
      refuseList:0,
      pieData:[],
      pieName:'申请情况',
      queryParams:{
        page: 1,
        pageSize: 5,
        title: '',
        state:'',
        flowName:''
      },
      loading:false,
      dialogView: false,
      procinstid: '',
      dataJson: Object,
      processKey: '',
      processName: '',
      mapConfig: Object,
      mapList: [],
      form_type:'',
      form_url:'',
    }
  },
  components:{
    PieChart,
    viewpage,
    Pagination
  },
  created(){
    this.initData();
  },
  methods:{
    async initData(){
      const data=await getMyProcessPage(this.queryParams);
      if(data.error == '200'){
        this.total=data.result.total;
        this.list=data.result.list;
        this.processManages=data.result.processManages;
        this.refuseList=data.result.refuseList;
        this.pieData=data.result.pieData;
      }
    },
    handleView(row){
      this.loading=true;
      if(row.map_json==""||row.map_json==undefined){
        this.processKey="";
        this.processName="";
        this.mapConfig={};
        this.mapList=[];
        this.procinstid=row.proce_inst_id;
        this.form_url=row.form_url;
        this.form_type=row.form_type;
        this.dataJson={};
        this.dialogView=true;
      }
      else{
        const mapJson = JSON.parse(row.map_json);
        let dj={};
        if(row.data_json!=""&&row.data_json!=null&&row.data_json!=undefined){
          this.dataJson=JSON.parse(row.data_json);
          dj=JSON.parse(row.data_json);
        }
        this.processKey="";
        this.processName="";
        this.mapConfig=mapJson.config;
        this.mapList=mapJson.list;
        this.form_url=row.form_url;
        this.form_type=row.form_type;
        this.procinstid=row.proce_inst_id;
        this.dataJson=dj;
        this.dialogView=true;
      }
    },
    cloaseView(){
      this.dialogView=false;
      this.loading=false;
      this.initData();
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.page = 1;
      this.initData();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.handleQuery();
    },
  }
}
</script>

<style scoped>

</style>
