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

            <el-table  :data="list" stripe style="width: 100%">
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
                label="申请时间"
                prop="start_time_"
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
                    <el-tag type="warning" v-else-if="scope.row.delete_reason_!=undefined && scope.row.delete_reason_ != '' && scope.row.delete_reason_ == 'refuse'">已拒绝</el-tag>
                    <el-tag type="success" v-else>已完成</el-tag>
                  </div>
                </template>
              </el-table-column>

              <el-table-column
                label="结束时间"
                prop="end_time_"
                :show-overflow-tooltip="true"
              />


              <el-table-column label="操作" align="center" class="operation-btn">
                <template slot-scope="scope">
                  <el-button type="text" icon="el-icon-view" @click="handleView(scope.row)"
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
import { getMyStartListPage, getManageParams} from "@/api/process/processManage";
import Pagination from '@/components/Pagination';
export default {
  name: "userStatAnalysis",
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
        const data=await getMyStartListPage(this.queryParams);
      if(data.error == '200'){
        this.total=data.result.total;
        this.list=data.result.list;
        this.processManages=data.result.processManages;
        this.refuseList=data.result.refuseList;
        this.pieData=data.result.pieData;
      }
    },
    handleView(row){
      getManageParams({proce_inst_id:row.proc_inst_id_}).then((res) => {
        if(res.error == '200'){
          this.form_url=res.result.form_url;
          this.form_type=res.result.form_type;
        }
        else{
          this.form_url='';
          this.form_type='';
        }
        this.procinstid=row.proc_inst_id_;
        if(res.result.map_json==""||res.result.map_json==undefined){
          this.processKey="";
          this.processName="";
          this.mapConfig={};
          this.mapList=[];
          this.dataJson={};
          this.dialogView=true;
        }
        else{
          const mapJson = JSON.parse(res.result.map_json);
          let dj={};
          if(res.result.data_json!=""&&res.result.data_json!=null&&res.result.data_json!=undefined){
            this.dataJson=JSON.parse(res.result.data_json);
            dj=JSON.parse(res.result.data_json);
          }
          this.processKey="";
          this.processName="";
          this.mapConfig=mapJson.config;
          this.mapList=mapJson.list;
          this.dataJson=dj;
          this.dialogView=true;
        }
      });

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
