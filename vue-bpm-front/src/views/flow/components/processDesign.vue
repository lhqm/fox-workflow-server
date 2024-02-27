<template>
  <div id="procDes">
    <my-process-designer
      :key="`designer-${reloadIndex}`"
      :options="{
        taskResizingEnabled: true,
        eventResizingEnabled: true
      }"
      v-model="xmlString"
      v-bind="controlForm"
      keyboard
      ref="processDesigner"
      @element-click="elementClick"
      @element-contextmenu="elementContextmenu"
      @init-finished="initModeler"
    >
    </my-process-designer>
<!--    <my-properties-panel-->
<!--      :key="`penal-${reloadIndex}`"-->
<!--      :bpmn-modeler="modeler"-->
<!--      :prefix="controlForm.prefix"-->
<!--      class="process-panel"-->
<!--    />-->
    <el-drawer
      title="节点属性"
      v-if="dialogNodeVisible == true"
      :close-on-click-modal="false"
      :visible.sync="dialogNodeVisible"
      @close="cloaseDialog"
      :close-on-press-escape="false"
      :wrapperClosable="false"
      :append-to-body="true"
      destroy-on-close
      size="60%"
    >
      <div class="drawer-content">
        <user-task-properties
          ref="taskProperties"
          :user-task-id="elementBusinessObject.id"
          :user-task-name="elementBusinessObject.name"
          :element="taskElement"
          :task-modeler="modeler"
          :task-type="taskType"
        ></user-task-properties>
      </div>
      <div class="drawer-footer">
        <el-button type="primary" @click="saveEdit">保存</el-button>
        <el-button @click="cloaseDialog">取消</el-button>
      </div>
    </el-drawer>
    <el-drawer
      title="流转条件"
      v-if="dialogConditionVisible == true"
      :close-on-click-modal="false"
      :visible.sync="dialogConditionVisible"
      @close="cloaseDialog"
      :close-on-press-escape="false"
      :wrapperClosable="false"
      :append-to-body="true"
      destroy-on-close
      size="60%"
    >
      <div class="drawer-content">
        <condition-perties
          ref="conditionProperties"
          :business-object="elementBusinessObject"
          :element="taskElement"
          :cond-modeler="modeler"
        ></condition-perties>
      </div>
      <div class="drawer-footer">
        <el-button type="primary" @click="saveCondition">保存</el-button>
        <el-button @click="cloaseDialog">取消</el-button>
      </div>
    </el-drawer>
  </div>
</template>

<script>
  import translations from '@/translations';
  // 自定义渲染（隐藏了 label 标签）
  import CustomRenderer from '@/modules/custom-renderer';
  // 自定义元素选中时的弹出菜单（修改 默认任务 为 用户任务）
  import CustomContentPadProvider from '@/package/designer/plugins/content-pad';
  // 自定义左侧菜单（修改 默认任务 为 用户任务）
  import CustomPaletteProvider from '@/package/designer/plugins/palette';
  import Log from '@/package/Log';
  import UserTaskProperties from "@/components/designercomponents/UserTaskProperties";
  import ConditionPerties from "@/components/designercomponents/ConditionPerties";

  export default {
    name: 'processDesign',
    components: {
      UserTaskProperties,
      ConditionPerties
    },
    data() {
      return {
        xmlString: '',
        modeler: null,
        reloadIndex: 0,
        pageMode: false,
        translationsSelf: translations,
        controlForm: {
          processId: this.processId,
          deployMName: this.deployMName,
          deployMId: this.deployMId,
          simulation: true,
          labelEditing: false,
          labelVisible: false,
          prefix: 'activiti',
          headerButtonSize: 'mini',
          events: ['element.click', 'element.contextmenu'],
          // additionalModel: []
         // moddleExtension: { user: UserSql },
          additionalModel: [CustomContentPadProvider, CustomPaletteProvider, CustomRenderer]
        },
        addis: {
          CustomContentPadProvider,
          CustomPaletteProvider
        },
        dialogNodeVisible:false,
        dialogConditionVisible:false,
        elementBusinessObject: {},
        taskElement:Object,
        taskType:''
      };
    },
    props: {
      processId: String,
      deployMId: String,
      deployMName: String
    },
    created() {},
    methods: {
      initModeler(modeler) {
        setTimeout(() => {
          this.modeler = modeler;
          const canvas = modeler.get('canvas');

          const rootElement = canvas.getRootElement();
          Log.prettyPrimary('Process Id:', rootElement.id);
          Log.prettyPrimary('Process Name:', rootElement.businessObject.name);
        }, 10);
      },
      elementClick(element) {
        console.log(element);
        this.element = element;
        this.taskType=this.element.type.split(":")[1] || "";
        this.elementBusinessObject = JSON.parse(JSON.stringify(this.element.businessObject));
        if(this.taskType.indexOf('Task') !== -1){
          this.taskElement=this.element;
          this.dialogNodeVisible=true;
        }
        if(this.taskType=="SequenceFlow"){
          this.taskElement=this.element;
          this.dialogConditionVisible=true;
        }
      },
      elementContextmenu(element) {
        console.log('elementContextmenu:', element);
      },
      toggle() {
        console.log(this.modeler);
        console.log(this.modeler.get('toggleMode'));
        this.modeler.get('toggleMode').toggleMode();
      },
      saveEdit(){
        this.$refs.taskProperties.saveEdit();
      },
      saveCondition(){
        this.$refs.conditionProperties.save();
      },
      cloaseDialog() {
        this.dialogNodeVisible=false;
        this.dialogConditionVisible=false;
      }
    }
  };
</script>

<style lang="scss">
  body {
    overflow: hidden;
    margin: 0;
    box-sizing: border-box;
  }
  #procDes {
    max-height: 100vh;
    width: 100%;
    box-sizing: border-box;
    display: inline-grid;
    grid-template-columns: auto max-content;
    height: calc(100vh - 76px);
  }
  .demo-info-bar {
    position: fixed;
    right: 8px;
    bottom: 108px;
    z-index: 1;
  }
  .demo-control-bar {
    position: fixed;
    right: 8px;
    bottom: 48px;
    z-index: 1;
  }
  .open-model-button {
    width: 48px;
    height: 48px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 4px;
    font-size: 32px;
    background: rgba(64, 158, 255, 1);
    color: #ffffff;
    cursor: pointer;
  }
  .zoom-in-right-enter-active,
  .zoom-in-right-leave-active {
    opacity: 1;
    transform: scaleY(1) translateY(-48px);
    transition: all 300ms cubic-bezier(0.23, 1, 0.32, 1);
    transform-origin: right center;
  }
  .zoom-in-right-enter,
  .zoom-in-right-leave-active {
    opacity: 0;
    transform: scaleX(0) translateY(-48px);
  }
  .info-tip {
    position: absolute;
    width: 480px;
    top: 0;
    right: 64px;
    z-index: 10;
    box-sizing: border-box;
    padding: 0 16px;
    color: #333333;
    background: #f2f6fc;
    transform: translateY(-48px);
    border: 1px solid #ebeef5;
    border-radius: 4px;
    &::before,
    &::after {
      content: '';
      width: 0;
      height: 0;
      border-width: 8px;
      border-style: solid;
      position: absolute;
      right: -15px;
      top: 50%;
    }
    &::before {
      border-color: transparent transparent transparent #f2f6fc;
      z-index: 10;
    }
    &::after {
      right: -16px;
      border-color: transparent transparent transparent #ebeef5;
      z-index: 1;
    }
  }
  .control-form {
    .el-radio {
      width: 100%;
      line-height: 32px;
    }
  }
  .element-overlays {
    box-sizing: border-box;
    padding: 8px;
    background: rgba(0, 0, 0, 0.6);
    border-radius: 4px;
    color: #fafafa;
  }
</style>
