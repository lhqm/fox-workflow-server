<template>
  <div class="app-container">
    <div class="theme-main-container">
      <div class="filter-container">
        <element-base-info :id-edit-disabled="idEditDisabled"
                           :business-object="elementBusinessObject"
                           :base-element="element"
                           :type="elementType" />
        <flow-condition ref="flowCondition" style="margin-top: 16px" :business-object="elementBusinessObject"
                        :cond-element="element"
                         />
      </div>
    </div>
  </div>
</template>

<script>
import FlowCondition from "@/package/penal/flow-condition/FlowCondition";
import ElementBaseInfo from "@/package/penal/base/ElementBaseInfo";

export default {
  name: "ConditionPerties",
  components:{
    FlowCondition,
    ElementBaseInfo
  },
  props:{
    businessObject: Object,
    element:Object,
    condModeler:Object
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
  data(){
    return{
      idEditDisabled:true,
      elementType:'',
      elementBusinessObject: {}, // 元素 businessObject 镜像，提供给需要做判断的组件使用
    }
  },
  created() {
    this.proInit();
  },
  methods:{
    resetBaseInfo() {
      this.elementBusinessObject = JSON.parse(JSON.stringify(this.element.businessObject));
      if (this.elementBaseInfo && this.elementBaseInfo.$type === "bpmn:SubProcess") {
        this.$set(this.elementBaseInfo, "isExpanded", this.elementBaseInfo.di?.isExpanded);
      }
    },
    proInit() {
      window.bpmnInstances = {
        modeler: this.condModeler,
        modeling: this.condModeler.get("modeling"),
        moddle: this.condModeler.get("moddle"),
        eventBus: this.condModeler.get("eventBus"),
        bpmnFactory: this.condModeler.get("bpmnFactory"),
        elementFactory: this.condModeler.get("elementFactory"),
        elementRegistry: this.condModeler.get("elementRegistry"),
        replace: this.condModeler.get("replace"),
        selection: this.condModeler.get("selection")
      };
    },
    save(){
      this.$refs.flowCondition.save();
    }
  }
}
</script>

<style scoped>

</style>
