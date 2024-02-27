<template>
  <div >
    <el-form :model="flowConditionForm" label-width="90px" size="mini" @submit.native.prevent>
      <el-form-item label="流转类型">
        <el-select v-model="flowConditionForm.type" @change="updateFlowType">
          <el-option label="普通流转路径" value="normal" />
          <el-option label="默认流转路径" value="default" />
          <el-option label="条件流转路径" value="condition" />
        </el-select>
      </el-form-item>
      <el-form-item label="条件格式" v-if="flowConditionForm.type === 'condition'" key="condition">
        <el-select v-model="flowConditionForm.conditionType">
          <el-option label="表达式" value="expression" />
        </el-select>
      </el-form-item>
      <el-form-item label="" v-if="flowConditionForm.type === 'condition'" >
        <span slot="label">
                  <el-tooltip content="注意查看参数设置说明" placement="top">
                  <i class="el-icon-question"></i>
                  </el-tooltip>
                  条件格式
                </span>
        <el-select v-model="expressionType.type">
          <el-option label="参数模式(表单参数、自定义参数等)" value="params" />
          <el-option label="SQL模式" value="sql" />
          <el-option label="WebApi模式" value="webApi" />
          <el-option label="用户选择" value="bySelect" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="expressionType.type==='params'" label="参数说明:" >
        <span style="color: darkorange">参数模式，可以是表单中的字段，或者外部传入的自定义的参数，表达式格式举例:${jine>100};</span>
      </el-form-item>
      <template v-if="expressionType.type==='sql'">
        <el-form-item  label="sql语句:" >
          <el-input v-model="expressionType.sqlBody" clearable />
        </el-form-item>
        <el-form-item  label="参数说明:" >
          <el-form-item>
            <span style="color: darkorange">sql模式，编写select语句，并返回count结果，编写表达式，表达式格式举例：${count>5};</span>
          </el-form-item>
          <el-form-item>
            <span style="color: darkorange">select语句中，可以增加表单字段参数或者流程参数;</span>
          </el-form-item>
          <el-form-item>
            <span style="color: darkorange">表单字段参数，即节点中绑定的表单中的字段，不包括子表字段、附件等组件;</span>
          </el-form-item>
          <el-form-item>
            <span style="color: darkorange">流程参数，包括businessKey（表单业务id）、proce_inst_id（流程实例id）、taskid（任务id）、processKey（流程key）;</span>
          </el-form-item>
          <el-form-item>
            <span style="color: darkorange">select语句格式，select username from sys_user where name='@RYMC';</span>
          </el-form-item>
        </el-form-item>
      </template>
      <template v-if="expressionType.type==='webApi'">
        <el-form-item  label="接口地址:" >
          <el-input v-model="expressionType.webApiBody" clearable />
        </el-form-item>
        <el-form-item  label="参数说明:" >
          <el-form-item>
            <span style="color: darkorange">表单字段参数，即节点中绑定的表单中的字段，不包括子表字段、附件等组件;</span>
          </el-form-item>
          <el-form-item>
            <span style="color: darkorange">流程参数，包括businessKey（表单业务id）、proce_inst_id（流程实例id）、taskid（任务id）、processKey（流程key）;</span>
          </el-form-item>
        </el-form-item>

      </template>

      <el-form-item label="表达式" v-if="flowConditionForm.conditionType
      && flowConditionForm.conditionType === 'expression'
                    && expressionType.type!='bySelect'" key="express">
        <el-input v-model="flowConditionForm.body" clearable @change="updateFlowCondition" />
      </el-form-item>
      <el-form-item  label="设置说明:" >
        <span style="color: darkorange">表达式设置或修改后，需要保存发布才可生效；其他修改无需发布</span>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
import { getFlowCond,setFlowCond } from "@api/process/flowConde";
export default {
  name: "FlowCondition",
  props: {
    businessObject: Object,
    //type: String,
    condElement:Object
  },
  data() {
    return {
      flowConditionForm: {},
      expressionType:{
        type:'',
        sqlBody:'',
        webApiBody:''
      }
    };
  },
  watch: {
    businessObject: {
      immediate: true,
      handler() {
        this.$nextTick(() => this.resetFlowCondition());
      }
    }
  },
  methods: {
    resetFlowCondition() {
      this.bpmnElement = this.condElement;
      this.bpmnElementSource = this.bpmnElement.source;
      this.bpmnElementSourceRef = this.bpmnElement.businessObject.sourceRef;
      if (this.bpmnElementSourceRef && this.bpmnElementSourceRef.default && this.bpmnElementSourceRef.default.id === this.bpmnElement.id) {
        // 默认
        this.flowConditionForm = { type: "default" };
      } else if (!this.bpmnElement.businessObject.conditionExpression) {
        // 普通
        this.flowConditionForm = { type: "normal" };
      } else {
        // 带条件
        const conditionExpression = this.bpmnElement.businessObject.conditionExpression;
        this.flowConditionForm = { ...conditionExpression, type: "condition" };
        // resource 可直接标识 是否是外部资源脚本
        if (this.flowConditionForm.resource) {
          this.$set(this.flowConditionForm, "conditionType", "script");
          this.$set(this.flowConditionForm, "scriptType", "externalScript");
          return;
        }
        if (conditionExpression.language) {
          this.$set(this.flowConditionForm, "conditionType", "script");
          this.$set(this.flowConditionForm, "scriptType", "inlineScript");
          return;
        }
        this.$set(this.flowConditionForm, "conditionType", "expression");

        getFlowCond(this.bpmnElement.id).then((res)=>{
          if(res.error=="200"&&res.result!=undefined){
            this.expressionType.type=res.result.expression_type;
            if(res.result.expression_type=="sql")
              this.expressionType.sqlBody=res.result.expression_body;
            if(res.result.expression_type=="webApi")
              this.expressionType.webApiBody=res.result.expression_body;
          }
        })
      }
    },
    updateFlowType(flowType) {
      // 正常条件类
      if (flowType === "condition") {
        this.flowConditionRef = window.bpmnInstances.moddle.create("bpmn:FormalExpression");
        window.bpmnInstances.modeling.updateProperties(this.bpmnElement, {
          conditionExpression: this.flowConditionRef
        });
        return;
      }
      // 默认路径
      if (flowType === "default") {
        window.bpmnInstances.modeling.updateProperties(this.bpmnElement, {
          conditionExpression: null
        });
        window.bpmnInstances.modeling.updateProperties(this.bpmnElementSource, {
          default: this.bpmnElement
        });
        return;
      }
      // 正常路径，如果来源节点的默认路径是当前连线时，清除父元素的默认路径配置
      if (this.bpmnElementSourceRef.default && this.bpmnElementSourceRef.default.id === this.bpmnElement.id) {
        window.bpmnInstances.modeling.updateProperties(this.bpmnElementSource, {
          default: null
        });
      }
      window.bpmnInstances.modeling.updateProperties(this.bpmnElement, {
        conditionExpression: null
      });
    },
    updateFlowCondition() {
      let { conditionType, scriptType, body, resource, language } = this.flowConditionForm;
      let condition;
      if (conditionType === "expression") {
        condition = window.bpmnInstances.moddle.create("bpmn:FormalExpression", { body });
      } else {
        if (scriptType === "inlineScript") {
          condition = window.bpmnInstances.moddle.create("bpmn:FormalExpression", { body, language });
          this.$set(this.flowConditionForm, "resource", "");
        } else {
          this.$set(this.flowConditionForm, "body", "");
          condition = window.bpmnInstances.moddle.create("bpmn:FormalExpression", { resource, language });
        }
      }
      window.bpmnInstances.modeling.updateProperties(this.bpmnElement, { conditionExpression: condition });
    },
    save(){
      const queryParams={
        expressionId:this.bpmnElement.id,
        type:this.expressionType.type,
        sqlBody:this.expressionType.sqlBody,
        webApiBody:this.expressionType.webApiBody
      }
      //如果是手动选择
      if(this.expressionType.type==='bySelect'){
        this.flowConditionForm.body="${"+this.bpmnElement.id+">0}";
        this.updateFlowCondition();
      }
      setFlowCond(queryParams).then((res) =>{
        if (res.error == '200') {
          this.$message.success('设置成功');
        } else {
          this.$message.error('设置失败');
        }
      })
    }
  },
  beforeDestroy() {
    this.bpmnElement = null;
    this.bpmnElementSource = null;
    this.bpmnElementSourceRef = null;
  }
};
</script>
