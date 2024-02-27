<template>
  <div class="panel-tab__content">
    <el-form size="mini" label-width="80px" @submit.native.prevent>
      <el-form-item label="表单类型">
        <el-select v-model="taskForm.form_type" @change="updateElementTask('form_type')">
          <el-option label="内置表单" value="0" />
          <el-option label="外部表单" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="taskFormtype=='0'" label="流程表单">
        <el-select v-model="taskForm.formMap" filterable @change="updateAttrs">
          <el-option v-for="i in formMapList" :key="i.id" :value="i.id" :label="i.name" />
        </el-select>
      </el-form-item>
      <el-form-item v-if="taskFormtype=='1'" label="表单地址">
        <el-input v-model="taskForm.form_url" @change="updateElementTask('form_url')"></el-input>
      </el-form-item>
      <div v-if="taskFormtype=='0'">
        <div class="element-drawer__button">
          <el-button size="mini" type="primary" icon="el-icon-view" @click="openView()">查看表单</el-button>
        </div>
        <el-table :data="formAttrs" row-key="id" size="mini" max-height="240" border fit :tree-props="{children: 'columns', hasChildren: 'hasChildren'}" default-expand-all>
          <el-table-column label="序" width="40px" type="index" />
          <el-table-column label="字段名称" min-width="30%" prop="label" show-overflow-tooltip />
          <el-table-column label="可见" min-width="25%" prop="isShow">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.isShow"
                class="switch"
                :active-value=true
                :inactive-value=false
                @change="stateChange(scope.row,'isShow')"
              />
            </template>
          </el-table-column>
          <el-table-column label="禁用" min-width="25%" prop="disabled">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.disabled"
                class="switch"
                :active-value=true
                :inactive-value=false
                @change="stateChange(scope.row,'disabled')"
              />
            </template>
          </el-table-column>
          <el-table-column label="只读" min-width="25%" prop="readonly">
            <template slot-scope="scope">
              <el-switch
                v-model="scope.row.readonly"
                class="switch"
                :active-value=true
                :inactive-value=false
                @change="stateChange(scope.row,'readonly')"
              />
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-form>
    <el-drawer :visible.sync="viewVisible" title="表单预览" size="100%" :v-model="false" :append-to-body="true" destroy-on-close>
          <preview :itemList="itemList"  :formConf="formConf" />
        </el-drawer>
  </div>
</template>
<style>
  .el-table__indent{
    padding-left:0px !important
  }
</style>
<script>
export default {
  name: "ElementForm",
  props: {
    id: String,
    type: String
  },
  inject: {
    prefix: "prefix",
    width: "width"
  },
  data() {
    return {
      businessKey: "",     
      fieldModelVisible: false,
      viewVisible:false,
      isInitLoad:false,
      formMapList:[],
      formColunms:[],
      mapJson:"",
      formAttrs:[],
      taskFormtype:'',
      taskForm: {
        form_type:"",
        formMap:"",
        form_url:""
      },
      itemList:[],
      formConf:Object,
    };
  },
  components: {
      
    },
  watch: {
    id: {
      immediate: true,
      handler(val) {
        val && val.length && this.$nextTick(() => this.resetFormList());
      }
    }
  },
  mounted(){
    this.loadFormList();
  },
  methods: {
    wathLoad(){
      // this.get(this.Apis.getFormElement+"?access_token="+localStorage.getItem("token"), {usertaskid:this.id}, res => {
      //     if(res.error=="200"){
      //       if(res.result.mapJson==""){
      //         this.isInitLoad=true;
      //       }
      //       else{
      //         this.mapJson=JSON.parse(res.result.mapJson);
      //       }
            
      //       this.taskForm.form_type=res.result.form_type;
      //       this.taskForm.formMap=res.result.formMap;
      //       this.taskForm.form_url=res.result.form_url;
      //       this.taskFormtype=res.result.form_type;
      //       this.formAttrs=[];
      //       this.formColunms=[];
      //       if(this.isInitLoad){
      //         this.loadAttrList();
      //       }
      //       else{
      //         this.updateAttrList();
      //       }
      //     }
      //   })
    },
    loadFormList(){
      // this.get(this.Apis.formList+"?access_token="+localStorage.getItem("token"),{page:1,pagesize:0},res=>{
      //     if(res.error=="200"){
      //       this.formMapList = res.result;
      //     }
      //   })
    },
    updateAttrs(){
      let selectOption=this.formMapList.filter((item)=>{
        if(item.id==this.taskForm.formMap){
          return item;
        }
      });
      this.setFlowElementAttrs(this.id+"_formMap",this.taskForm.formMap);
      this.setFlowElementAttrs(this.id+"_form_type",this.taskForm.form_type);
      this.setFlowElementAttrs(this.id+"_form_url","");
      if(selectOption[0].form_json==undefined)
      {
        this.mapJson=undefined;
        this.formColunms=[];
        this.formAttrs=[];
        this.setFlowElementAttrs(this.id+"_mapJson","");
        return;
      }
      this.formAttrs=[];
      this.formColunms=[];
      this.mapJson=JSON.parse(selectOption[0].form_json);
      this.setFlowElementAttrs(this.id+"_mapJson",selectOption[0].form_json);
      this.updateAttrList();
    },
    loadAttrList(){
      const columnList=this.mapJson.list;
      let taskType="";
      if(this.bpmnElement.businessObject.$type=="bpmn:StartEvent"){
        taskType="bpmn:StartEvent"
      }
      else{
        const taskInComing=this.bpmnElement.businessObject.incoming;
        const taskSourceRef=taskInComing[0].sourceRef;
        taskType=taskSourceRef.$type
      }
      let obj={
          id:"",
          label:"",
          isShow:true,
          disabled:false,
          readonly:false,
          columns:[]
        };
      if(taskType=="bpmn:StartEvent"){
        for(let i in columnList){
        
          if(columnList[i].compType=="text"){
            continue;
          }
          else if(columnList[i].compType=="dynamicTable"){
            obj={
              id:columnList[i].id,
              label:columnList[i].label,
              isShow:columnList[i].isShow,
              disabled:columnList[i].disabled,
              readonly:columnList[i].readonly,
              columns:columnList[i].columns
            };
          }
          else{
            obj={
              id:columnList[i].id,
              label:columnList[i].label,
              isShow:columnList[i].isShow,
              disabled:columnList[i].disabled,
              readonly:columnList[i].readonly,
              columns:[]
            };
          }
          this.formColunms.push(obj);
        }
      }
      else{
        for(let i in columnList){
        if(columnList[i].compType=="text"){
          continue;
        }
        else if(columnList[i].compType=="dynamicTable"){
          let children={
            id:"",
            label:"",
            isShow:true,
            disabled:false,
            readonly:false,
          };
          let childrenColumns=[];
          for(let j in columnList[i].columns){
            children={
              id:columnList[i].columns[j].id,
              label:columnList[i].columns[j].label,
              isShow:true,
              disabled:true,
              readonly:true,
            };
            childrenColumns.push(children);
          }
          obj={
            id:columnList[i].id,
            label:columnList[i].label,
            isShow:true,
            disabled:true,
            readonly:true,
            columns:childrenColumns
          };
        }
        else{
          obj={
            id:columnList[i].id,
            label:columnList[i].label,
            isShow:true,
            disabled:true,
            readonly:true,
            columns:[]
          };
        }
        this.formColunms.push(obj);
      }
      }
      
      this.formAttrs=this.formColunms;
    },
    updateAttrList(){
      const columnList=this.mapJson.list;
      let taskType="";
      if(this.bpmnElement.businessObject.$type=="bpmn:StartEvent"){
        taskType="bpmn:StartEvent"
      }
      else{
        const taskInComing=this.bpmnElement.businessObject.incoming;
        const taskSourceRef=taskInComing[0].sourceRef;
        taskType=taskSourceRef.$type
      }
      let obj={
          id:"",
          label:"",
          isShow:true,
          disabled:false,
          readonly:false,
          columns:[]
        };
      for(let i in columnList){
        if(columnList[i].compType=="text"){
          continue;
        }
        else if(columnList[i].compType=="dynamicTable"){
          let children={
            id:"",
            label:"",
            isShow:true,
            disabled:false,
            readonly:false,
          };
          let childrenColumns=[];
          for(let j in columnList[i].columns){
            children={
              id:columnList[i].columns[j].id,
              label:columnList[i].columns[j].label,
              isShow:columnList[i].columns[j].isShow,
              disabled:columnList[i].columns[j].disabled,
              readonly:columnList[i].columns[j].readonly,
            };
            childrenColumns.push(children);
          }
          obj={
            id:columnList[i].id,
            label:columnList[i].label,
            isShow:columnList[i].isShow,
            disabled:columnList[i].disabled,
            readonly:columnList[i].readonly,
            columns:childrenColumns
          };
        }
        else{
          obj={
            id:columnList[i].id,
            label:columnList[i].label,
            isShow:columnList[i].isShow,
            disabled:columnList[i].disabled,
            readonly:columnList[i].readonly,
            columns:[]
          };
        }
        this.formColunms.push(obj);
      }
      this.formAttrs=this.formColunms;
    },
    stateChange(row,type){
      if(type=="isShow"){
        if(row.isShow==true){
          let changeOption=this.mapJson.list.filter((item)=>{
            if(item.compType=="dynamicTable"){
              let childrenOption=item.columns.filter((t)=>{
                if(t.id==row.id){
                  t.isShow=true;
                }
                return t;
              });
              item.columns=childrenOption;
            }
            else{
              if(item.id==row.id){
                if(item.compType!="text"){
                  item.isShow=true;
                }
              }
            }
            return item;
          });
          this.mapJson.list=changeOption;
        }
        else{
          let changeOption=this.mapJson.list.filter((item)=>{
            if(item.compType=="dynamicTable"){
              let childrenOption=item.columns.filter((t)=>{
                if(t.id==row.id){
                  t.isShow=false;
                }
                return t;
              });
              item.columns=childrenOption;
            }
            else{
              if(item.id==row.id){
                if(item.compType!="text"){
                  item.isShow=false;
                }
              }
            }
            return item;
          });
          this.mapJson.list=changeOption;
        }
      }
      if(type=="disabled"){
        if(row.disabled==true){
          let changeOption=this.mapJson.list.filter((item)=>{
            if(item.compType=="dynamicTable"){
              let childrenOption=item.columns.filter((t)=>{
                if(t.id==row.id){
                  t.disabled=true;
                }
                return t;
              });
              item.columns=childrenOption;
            }
            else{
              if(item.id==row.id){
                if(item.compType!="text"){
                  item.disabled=true;
                }
              }
            }
            return item;
          });
          this.mapJson.list=changeOption;
        }
        else{
          let changeOption=this.mapJson.list.filter((item)=>{
            if(item.compType=="dynamicTable"){
              let childrenOption=item.columns.filter((t)=>{
                if(t.id==row.id){
                  t.disabled=false;
                }
                return t;
              });
              item.columns=childrenOption;
            }
            else{
              if(item.id==row.id){
                if(item.compType!="text"){
                  item.disabled=false;
                }
              }
            }
            return item;
          });
          this.mapJson.list=changeOption;
        }
      }
      if(type=="readonly"){
        if(row.readonly==true){
          let changeOption=this.mapJson.list.filter((item)=>{
            if(item.compType=="dynamicTable"){
              let childrenOption=item.columns.filter((t)=>{
                if(t.id==row.id){
                  t.readonly=true;
                }
                return t;
              });
              item.columns=childrenOption;
            }
            else{
              if(item.id==row.id){
                if(item.compType!="text"){
                  item.readonly=true;
                }
              }
            }
            return item;
          });
          this.mapJson.list=changeOption;
        }
        else{
          let changeOption=this.mapJson.list.filter((item)=>{
            if(item.compType=="dynamicTable"){
              let childrenOption=item.columns.filter((t)=>{
                if(t.id==row.id){
                  t.readonly=false;
                }
                return t;
              });
              item.columns=childrenOption;
            }
            else{
              if(item.id==row.id){
                if(item.compType!="text"){
                  item.readonly=false;
                }
              }
            }
            return item;
          });
          this.mapJson.list=changeOption;
        }
      }
      this.setFlowElementAttrs(this.id+"_mapJson",JSON.stringify(this.mapJson));
    },
    openView() {
      if(this.mapJson==undefined){
          this.$message.error("表单内容为空");
          return;
      }
      else if(this.mapJson==[]){
          this.$message.error("请选择表单");
          return;
      }
      else{
          this.formConf=this.mapJson.config;
          this.itemList=this.mapJson.list;
      }
      this.viewVisible=true;
    },
    resetFormList() {
      this.bpmnElement = window.bpmnInstances.bpmnElement;
      this.wathLoad();
    },
    // 打开字段详情侧边栏
    openFieldForm(field, index) {
     
      this.fieldModelVisible = true;
    },
    setFlowElementAttrs(redisKey,attrKey){
      this.post(this.Apis.setFlowElementAttrs+"?access_token="+localStorage.getItem("token"),
                    {redisKey:redisKey,attrKey:attrKey},res=>{
            if(res.error=="200"){

            }
          });
    },
    updateElementTask(key) {
      if(key=="form_type"){
          this.taskFormtype=this.taskForm[key];
          this.setFlowElementAttrs(this.id+"_"+key,this.taskForm[key]);
           return;
      }
      if(key=="form_url"){
        this.taskFormtype=this.taskForm[key];
          this.setFlowElementAttrs(this.id+"_"+key,this.taskForm[key]);
           return;
      }
    }
  }
};
</script>
