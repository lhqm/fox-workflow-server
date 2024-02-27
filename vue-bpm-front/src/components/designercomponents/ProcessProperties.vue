<template>
  <div class="app-container">
    <el-form
      label-suffix="："
      label-width="90px"
      :model="ruleForm"
      ref="ruleForm"
      style="width: 95%"
    >
      <el-row>
        <el-col :span="12">
          <el-form-item label="流程Key" prop="key">
            <el-input v-model="ruleForm.key" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="流程名称" prop="name">
            <el-input v-model="ruleForm.name" @change="updateBaseInfo('name')"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="12">
          <el-form-item label="所属类别" prop="flowSort">
            <select-tree
              class="tree"
              v-model="ruleForm.sortid"
              :data="sortdata"
              placeholder="请选择所属类别"
            ></select-tree>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row>
        <el-col :span="24">
          <el-form-item label="标题生成" prop="titleModel">
            <el-input v-model="ruleForm.titleModel"></el-input>
          </el-form-item>
        </el-col>
      </el-row>
    </el-form>
    <div class="theme-main-container">
      <!-- 搜索 -->
      <div class="filter-container">
        <el-tabs v-model="subActiveName" type="card">
          <el-tab-pane label="参数设置" name="1">
            <span style="color: darkorange">暂未开放</span>
          </el-tab-pane>
          <el-tab-pane label="逾期设置" name="2">
            <span style="color: darkorange">暂未开放</span>
          </el-tab-pane>
          <el-tab-pane label="定时启动" name="3">
            <span style="color: darkorange">暂未开放</span>
          </el-tab-pane>
          <el-tab-pane label="事件管理" name="4">

            <span style="color: darkorange">暂未开放</span>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>
  </div>
</template>

<script>
import {flowSortTree} from "@/api/process/flowsort";
import { processProperties,saveProcessProperties } from "@/api/process/flowdesign"
import SelectTree from '@/components/selectTree/index.vue';

export default {
  name: "ProcessProperties",
  components:{
    SelectTree
  },
  props: {
    processId: '',
    deployMName:'',
    bpmElement:Object
  },
  data(){
    return{
      //流程分类
      sortdata: [],
      //tabs标签
      subActiveName:'1',
      //基本信息
      ruleForm: {
        id: '',
        name: '',
        key:'',
        titleModel:'',
        sortid: '',
        process_key:''
      }
    }
  },
  watch: {
    businessObject: {
      immediate: false,
      handler: function(val) {
        if (val) {
          this.$nextTick(() => this.resetBaseInfo());
        }
      }
    }
  },
  mounted() {
    this.ruleForm.key=this.processId;
    this.ruleForm.name=this.deployMName;
    this.proInit();
    this.flowSortTree();
  },
  methods:{
    resetBaseInfo() {
      this.bpmElement = window?.bpmnInstances?.bpmElement || {};
    },
    updateBaseInfo(key) {
      let element=window.bpmnInstances.elementRegistry.find(el => el.type === "bpmn:Process");
      const attrObj = Object.create(null);
      attrObj[key] = this.ruleForm.name;
      window.bpmnInstances.modeling.updateProperties(element, attrObj);
    },
    //加载流程属性
    proInit(){
      window.bpmnInstances = {
        modeler: this.bpmElement,
        modeling: this.bpmElement.get("modeling"),
        moddle: this.bpmElement.get("moddle"),
        eventBus: this.bpmElement.get("eventBus"),
        bpmnFactory: this.bpmElement.get("bpmnFactory"),
        elementFactory: this.bpmElement.get("elementFactory"),
        elementRegistry: this.bpmElement.get("elementRegistry"),
        replace: this.bpmElement.get("replace"),
        selection: this.bpmElement.get("selection")
      };
      processProperties({process_key:this.ruleForm.key}).then((res)=>{
        if(res.error=='200'){
          this.ruleForm.sortid=res.result.sortid;
          this.ruleForm.titleModel=res.result.titleModel;
        }
      });
    },
    //获取流程类别
    flowSortTree() {
      flowSortTree().then((res) => {
        if (res.error == '200') {
          this.sortdata = res.result;
        }
      });
    },
    //保存编辑
    saveEdit() {
      let processDefinitionParams= {
        name:this.ruleForm.name,
        key:this.ruleForm.key,
        sortid:this.ruleForm.sortid,
        titleModel:this.ruleForm.titleModel
      };
      saveProcessProperties(processDefinitionParams).then((res)=>{
        if (res.error == '200') {
          this.$message.success("保存成功")
        } else {
          this.$message.error('保存失败');
        }
      })
    }
  }
}
</script>

<style scoped>

</style>
