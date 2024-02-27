<template>
  <div class="app-container">
    <div class="theme-main-container">
      <div class="filter-container">
        <el-tabs v-model="taskActiveTabs" type="card">
          <el-tab-pane label="基本信息" name="1">
            <el-form
              label-suffix="："
              label-width="100px"
              :model="TaskModel"
              ref="TaskModel"
              style="width: 95%"
            >
              <el-row>
                <el-col :span="12">
                  <el-form-item label="节点编号" prop="id">
                    <el-input v-model="TaskModel.id" readonly></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="节点名称" prop="name">
                    <el-input v-model="TaskModel.name" @input="portInput"
                              @change="updateBaseInfo('name')"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row v-show="taskType==='UserTask'">
                <el-col :span="12">
                  <el-form-item label="驳回规则">
                    <el-select v-model="TaskModel.returnWay">
                      <el-option
                        v-for="(trs, index) in returnWays"
                        :key="index"
                        :label="trs.returnWayName"
                        :value="trs.returnWayKey"
                      />
                    </el-select>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="直接返回" v-if="TaskModel.returnWay == 'allStepsTaken'">
                    <el-form-item>
                      <el-radio-group v-model="TaskModel.runWay">
                        <el-radio label="none">否</el-radio>
                        <el-radio label="directReturn">是</el-radio>
                      </el-radio-group>
                    </el-form-item>
                    <el-form-item>
                          <span style="color: darkorange">
                            如果选择否，流程中存在并发分支的情况下，分支退回时，其他分支任务将会被删除</span>
                    </el-form-item>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row v-show="taskType==='UserTask'">
                <el-col :span="12">
                  <el-form-item label="移交">
                    <el-form-item>
                      <el-radio-group v-model="TaskModel.transfer">
                        <el-radio :label="0">不启用</el-radio>
                        <el-radio :label="1">启用</el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="加签">
                    <el-form-item>
                      <el-radio-group
                        v-model="TaskModel.countersign"
                      >
                        <el-radio :label="0">不启用</el-radio>
                        <el-radio :label="1">启用</el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row v-show="taskType==='UserTask'">
                <el-col :span="12">
                  <el-form-item label="拒绝">
                    <el-form-item>
                      <el-radio-group v-model="TaskModel.refuse">
                        <el-radio :label="0">不启用</el-radio>
                        <el-radio :label="1">启用</el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </el-form-item>
                </el-col>
                <el-col :span="12">
                  <el-form-item label="手动结束">
                    <el-form-item>
                      <el-radio-group v-model="TaskModel.endTask" >
                        <el-radio :label="0">不启用</el-radio>
                        <el-radio :label="1">启用</el-radio>
                      </el-radio-group>
                    </el-form-item>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="设置处理人" name="2"  v-if="taskType==='UserTask'">
            <el-form
              label-suffix="："
              label-width="100px"
              :model="TaskModel"
              ref="TaskModel"
              style="width: 95%"
            >
              <el-row>
                <el-col :span="24">
                  <user-task :id="TaskModel.id" :type="type" :element="ruleElement"></user-task>
                </el-col>
              </el-row>

            </el-form>
          </el-tab-pane>
          <el-tab-pane label="多人处理" name="3" v-if="taskType==='UserTask'">
            <el-row>
              <el-col :span="24">
                <element-multi-instance :task-modeler="taskModeler" :mui-element="ruleElement" :business-object="elementBaseInfo" :type="type" ></element-multi-instance>
              </el-col>
            </el-row>
          </el-tab-pane>
          <el-tab-pane label="表单配置" name="4" v-if="taskType==='UserTask'">
            <el-form
              label-suffix="："
              label-width="100px"
              :model="TaskModel"
              ref="TaskModel"
              style="width: 95%"
            >
            <el-row>
              <el-col :span="12">
                <el-form-item label="表单类型" prop="form_type">
                  <el-select v-model="TaskModel.form_type" @change="updateFromType">
                    <el-option label="内置表单" value="0" />
                    <el-option label="外部表单" value="1" />
                  </el-select>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item v-if="fromType == '0'" prop="formMap" label="表单模版">
                  <el-select v-model="TaskModel.formMap" filterable @change="updateAttrs">
                    <el-option v-for="i in formMapList" :key="i.id" :value="i.id" :label="i.name" />
                  </el-select>
                </el-form-item>
                <el-form-item v-if="fromType == '1'" prop="form_url" label="表单地址">
                  <el-input
                    v-model="TaskModel.form_url"
                  ></el-input>
                </el-form-item>
              </el-col>
            </el-row>
            <el-row>
              <el-col :span="12">
                <el-form-item v-if="TaskModel.form_type == '0'" label="表单预览">
                  <div>
                    <div class="element-drawer__button">
                      <el-button size="mini" type="primary" icon="el-icon-view" @click="openView()">
                        点击查看</el-button>
                    </div>
                  </div>
                </el-form-item>
              </el-col>
              <el-col :span="12">
                <el-form-item v-if="TaskModel.form_type == '0'" label="权限配置">
                  <div>
                    <div class="element-drawer__button">
                      <el-button size="mini" type="primary" icon="el-icon-setting" @click="handlerFormEdit()">
                        表单权限配置</el-button>
                    </div>
                  </div>
                </el-form-item>
              </el-col>
            </el-row>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="事件管理" name="5">
            <el-form
              label-suffix="："
              label-width="120px"
              style="width: 95%"
            >
              <el-row>
                <el-col :span="24">
                  <span style="color: darkorange">暂未开放</span>
                </el-col>
              </el-row>
            </el-form>
          </el-tab-pane>
          <el-tab-pane label="逾期设置" name="6">
            <el-form
              label-suffix="："
              label-width="120px"
              style="width: 95%"
            >
              <span style="color: darkorange">暂未开放</span>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </div>
      <el-drawer
        :visible.sync="viewVisible"
        title="表单预览"
        size="95%"
        class="radius"
        :append-to-body="true"
        direction="btt"
        destroy-on-close
      >
        <preview :itemList="itemList" :dataJson="[]" :formConf="formConf" />
      </el-drawer>
      <el-drawer
        title="表单权限配置"
        v-if="formEditVisible == true"
        size="100%"
        :visible.sync="formEditVisible"
        @close="cloaseDialog"
        :append-to-body="true"
        destroy-on-close
        :withHeader="false"
      >
        <div class="drawer-content">
         <form-setting :formConf="formConf" :mapList="itemList" :formId="formId" ref="formEditSetting">
         </form-setting>
        </div>
        <div class="drawer-footer">
          <el-button type="primary" @click="saveSetting">保存</el-button>
          <el-button @click="cloaseDialog">取消</el-button>
        </div>
      </el-drawer>
    </div>
  </div>
</template>

<script>
import {mapGetters} from "vuex";
import {userTaskProperties,saveUserTaskProperties} from "@/api/process/flowdesign";
import UserTask from "@/package/penal/task/task-components/UserTask";
import ElementMultiInstance from "@/package/penal/multi-instance/ElementMultiInstance";
import { formList} from "@/api/process/processFrom";
import preview from '@/components/formcomponents/preview';
import FormSetting from "@/components/formcomponents/formSrtting";

export default {
  name: "UserTaskProperties",
  components:{
    FormSetting,
    UserTask,
    ElementMultiInstance,
    preview
  },
  props:{
    userTaskId:'',
    userTaskName:'',
    businessObject: Object,
    element:Object,
    taskModeler:Object,
    taskType:''
  },
  data(){
    return{
      //是否预览表单
      viewVisible:false,
      //表单权限配置
      formEditVisible:false,
      //节点基本
      TaskModel:{
        //节点id
        id:'',
        //节点名称
        name:'',
        //退回方式
        returnWay:'',
        //退回后执行方式
        runWay:'',
        //移交
        transfer:0,
        //加签
        countersign:0,
        //拒绝
        refuse:0,
        form_type: '',
        formMap: '',
        form_url: ''
      },
      taskActiveTabs:'1',
      title:'',
      elementBaseInfo: {},
      type:'UserTask',
      ruleElement:this.element,
      //表单集合
      formMapList: [],
      //表单预览json
      mapJson: '',
      //表单基本信息
      formConf: Object,
      //表单字段集合
      itemList: [],
      fromType:'',
      formId:''
    }
  },
  computed: {
    ...mapGetters('store/taskRule', [
      'returnWays'
    ])
  },
  watch: {
    businessObject: {
      immediate: false,
      handler: function(val) {
        if (val) {
          this.$nextTick(() => this.resetBaseInfo());
        }
      }
    },

  },
  created() {
    this.TaskModel.id=this.userTaskId;
    this.TaskModel.name=this.userTaskName;
    this.proInit();
    this.initFormMapList();
  },
  methods:{
    resetBaseInfo() {
      this.elementBaseInfo = JSON.parse(JSON.stringify(this.element.businessObject));
      if (this.elementBaseInfo && this.elementBaseInfo.$type === "bpmn:SubProcess") {
        this.$set(this.elementBaseInfo, "isExpanded", this.elementBaseInfo.di?.isExpanded);
      }
    },
    updateBaseInfo(key) {
      const attrObj = Object.create(null);
      attrObj[key] = this.TaskModel.name;
      window.bpmnInstances.modeling.updateProperties(this.element, attrObj);
    },
    //加载属性
    proInit(){
      window.bpmnInstances = {
        modeler: this.taskModeler,
        modeling: this.taskModeler.get("modeling"),
        moddle: this.taskModeler.get("moddle"),
        eventBus: this.taskModeler.get("eventBus"),
        bpmnFactory: this.taskModeler.get("bpmnFactory"),
        elementFactory: this.taskModeler.get("elementFactory"),
        elementRegistry: this.taskModeler.get("elementRegistry"),
        replace: this.taskModeler.get("replace"),
        selection: this.taskModeler.get("selection")
      };
      userTaskProperties({task_def_key:this.TaskModel.id}).then((res)=>{
        if(res.error=="200"){
          this.TaskModel=res.result.formModel;
          this.TaskModel.id=this.userTaskId;
          this.TaskModel.name=this.userTaskName;
          this.fromType=this.TaskModel.form_type;
          if(this.TaskModel.form_type=="0"){
            if(this.TaskModel.form_url!=""&&this.TaskModel.form_url!=null
              &&this.TaskModel.form_url!=undefined) {
              this.mapJson = JSON.parse(this.TaskModel.form_url);
              this.formConf = this.mapJson.config;
              this.itemList = this.mapJson.list;
            }
            else{
              if(this.TaskModel.formMap!=""&&this.TaskModel.formMap!=null
                &&this.TaskModel.formMap!=undefined) {
                this.updateAttrs();
              }
            }
          }
        }
        else{
          this.$message.warning("获取节点属性失败");
        }
      });
    },
    initFormMapList(){
      //加载表单列表
      const params = {
        name:'',
        form_type:'',
        page: 1,
        pagesize: 0
      };
      formList(params).then((res) => {
        if (res.error == '200') {
          this.formMapList = res.result.list;
        } else {
          this.$message.error('获取失败');
        }
        this.loading = false;
      });

    },
    //保存编辑
    saveEdit() {
      let taskPropertiesParams={
        id:this.userTaskId,
        name:this.userTaskName,
        formModel:this.TaskModel
      }
      saveUserTaskProperties(taskPropertiesParams).then((res)=>{
        if (res.error == '200') {
          this.$message.success("保存成功")
        } else {
          this.$message.error('保存失败');
        }
      });
    },
    handlerFormEdit(){
      this.formId=this.TaskModel.formMap;
      this.formConf = this.mapJson.config;
      this.itemList = this.mapJson.list;
      this.formEditVisible=true;
    },
    //保存表单权限配置
    saveSetting(){
      let formCode=this.$refs.formEditSetting.saveMap();
      this.TaskModel.form_url=formCode;
      this.TaskModel.formMap=this.formId;
      this.saveEdit();
    },
    //设置表单预览参数
    updateAttrs() {
      let selectOption = this.formMapList.filter((item) => {
        if (item.id == this.TaskModel.formMap) {
          return item;
        }
      });
      if (selectOption[0].form_json == undefined) {
        this.mapJson = undefined;
        return;
      }
      else{
        this.mapJson = JSON.parse(selectOption[0].form_json);
        this.TaskModel.form_url=selectOption[0].form_json;
        this.formConf = this.mapJson.config;
        this.itemList = this.mapJson.list;
      }
    },
    updateFromType(){
      if(this.TaskModel.form_type=="0"){
        this.fromType="0";
        this.updateAttrs();
      }
      else{
        this.fromType="1";
        this.TaskModel.form_url="";
      }
    },
    //表单预览
    openView() {
      this.viewVisible = true;
    },
    //限制只能输入数字
    formatNumber(field,fieldModel) {
      if(fieldModel=="ruleForm") {
        this.ruleForm[field] = this.ruleForm[field].toString().replace(/[^\d]/g, "");
      }
    },
    //解决输入框无法输入
    portInput() {
      this.$forceUpdate()
    },
    cloaseDialog() {
      this.formEditVisible=false;
    }
  }
}
</script>

<style scoped>
.el-drawer .el-drawer__body{
  padding-top: 0px !important;
  padding-bottom:  0px !important;
}
</style>
