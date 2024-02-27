<template>
  <container type="ghost">
    <div class="theme-tab-container">
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card style="background: #3D96F9;color: white">
          <div slot="header" >
            <span>流程数</span>
            <el-button style="float: right;color: white; padding: 3px 0" type="text" @click="openInfo('flow/flowDefinition')">详情</el-button>
          </div>
          <el-row>
            <el-col :span="20">
              <div style="text-align: center;font-size: 1.5em">{{processDefinitions}}</div>
            </el-col>
            <el-col :span="4">
              <div style="text-align: right; "><img src="/image/common/flownum.png"></div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="6" >
        <el-card style="background: #F5B658;color: white">
          <div slot="header" >
            <span>实例数</span>
            <el-button style="float: right;color: white; padding: 3px 0" type="text" @click="openInfo('processMonitoring/processManage')">详情</el-button>
          </div>
          <el-row>
            <el-col :span="20">
              <div style="text-align: center;font-size: 1.5em">{{processManages}}</div>
            </el-col>
            <el-col :span="4">
              <div style="text-align: right; "><img src="/image/common/procinst.png"></div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="background: #42DDA9;color: white">
          <div slot="header">
            <span>任务数</span>
            <el-button style="float: right;color: white; padding: 3px 0" type="text" @click="openInfo('processMonitoring/taskManage')">详情</el-button>
          </div>
          <el-row>
            <el-col :span="20">
              <div style="text-align: center;font-size: 1.5em">{{generWorks}}</div>
            </el-col>
            <el-col :span="4">
              <div style="text-align: right; "><img src="/image/common/tasks.png"></div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card style="background: #B48BE4;color: white">
          <div slot="header">
            <span>办结数</span>
            <el-button style="float: right;color: white; padding: 3px 0" type="text">截至当前</el-button>
          </div>
          <el-row>
            <el-col :span="20">
              <div style="text-align: center;font-size: 1.5em">{{completeProcess}}</div>
            </el-col>
            <el-col :span="4">
              <div style="text-align: right; "><img src="/image/common/done.png"></div>
            </el-col>
          </el-row>
        </el-card>
      </el-col>
    </el-row>
    </div>
    <el-row :gutter="20">
      <el-col :span="12" style="padding-right: 5px;">
        <div class="theme-main-container">
          <header>
            流程完成率
          </header>
          <el-divider></el-divider>
          <pie-chart :pie-data="processPies" :pie-name="processPiesName"/>
        </div>
      </el-col>
      <el-col :span="12" style="padding-left: 5px">
        <div class="theme-main-container">
          <header>
            任务类型占比
          </header>
          <el-divider></el-divider>
          <nigh-pie-chart :data="taskPies" :pie-name="taskPiesName"/>
        </div>
      </el-col>
    </el-row>
    <el-row :gutter="20">
      <el-col :span="24" style="padding-top: 10px;">
        <div class="theme-main-container">
          <header>
            流程任务情况
          </header>
          <el-divider></el-divider>
          <bar-chart :data="proceTaskBar"/>
        </div>

      </el-col>
    </el-row>
  </container>
</template>

<script>
import PieChart from "@/components/echartscomponents/PieChart";
import BarChart from "@/components/echartscomponents/BarChart";
import NighPieChart from "@/components/echartscomponents/NighPieChart";

import {getStatAnalysisData} from "@/api/process/processManage";

export default {
  name: "ststAnalysis",
  components:{
    PieChart,
    BarChart,
    NighPieChart
  },
  data(){
    return{
      processDefinitions:0,
      processManages:0,
      generWorks:0,
      completeProcess:0,
      processPies:[],
      processPiesName:'流程完成率',
      taskPies:[],
      taskPiesName:'任务占比',
      proceTaskBar:[],

      queryParam:{
        proceInstKey:'',
        actInstKey:'',
        processKeys:[]
      }
    }
  },
  created(){
    this.showData();
  },
  methods:{
    openInfo(toPath){
      this.$router.push('/'+toPath);
    },
    async showData(){
      const data=await getStatAnalysisData(this.queryParam);
      if(data.error == '200'){
        this.processDefinitions=data.result.processDefinitions;
        this.processManages=data.result.processManages;
        this.generWorks=data.result.generWorks;
        this.completeProcess=data.result.completeProcess;
        this.processPies=data.result.processPies;
        this.taskPies=data.result.taskPies;
        this.proceTaskBar=data.result.proceTaskBar;
      }
    },
  }
}
</script>

<style scoped>

</style>
