<template>
  <div style="margin-top: 16px">
    <el-form-item label="处理人规则">
      <el-select v-model="userTaskForm.taskRules" @change="updateElementTask('taskRules')">
        <el-option
          v-for="(trs, index) in taskRules"
          :key="index"
          :label="trs.taskRuleName"
          :value="trs.ruleKey"
        />
      </el-select>
      <div v-if="processTaskRule == 'byUser'">
        <el-table :data="assigneeUserList" size="mini" max-height="240" border fit>
          <el-table-column label="序号" width="50px" type="index" />
          <el-table-column label="帐号" prop="username" show-overflow-tooltip />
          <el-table-column label="姓名" prop="name" show-overflow-tooltip />
          <el-table-column label="操作" fixed="right" width="90px">
            <template slot-scope="{ row, $index }">
              <el-button
                size="mini"
                type="text"
                style="color: #ff4d4f"
                @click="removeUser(row, $index)"
                >移除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
        <div class="element-drawer__button">
          <el-button
            size="mini"
            type="primary"
            icon="el-icon-plus"
            @click="openUserbutesForm(null, -1, 'byUser')"
            >添加人员</el-button
          >
        </div>
      </div>
      <el-dialog
        :visible.sync="byUserFormModelVisible"
        :close-on-click-modal="false"
        title="选择人员"
        width="80%"
        append-to-body
        destroy-on-close
      >
        <el-form :model="byUserForm" label-width="80px" style="width: 99%" size="mini">
          <rule-to-user ref="assigneeByUserRef"></rule-to-user>
        </el-form>
        <template slot="footer">
          <el-button size="mini" @click="byUserFormModelVisible = false">取 消</el-button>
          <el-button size="mini" type="primary" @click="saveAssigneeByUser('byUser')"
            >确 定</el-button
          >
        </template>
      </el-dialog>
      <div v-if="processTaskRule == 'byDept'">
        <el-table :data="assigneeUserList" size="mini" max-height="240" border fit>
          <el-table-column label="序号" width="50px" type="index" />
          <el-table-column label="部门名称" prop="name" show-overflow-tooltip />
          <el-table-column label="操作" fixed="right" width="90px">
            <template slot-scope="{ row, $index }">
              <el-button
                size="mini"
                type="text"
                style="color: #ff4d4f"
                @click="removeUser(row, $index)"
                >移除</el-button
              >
            </template>
          </el-table-column>
        </el-table>
        <div class="element-drawer__button">
          <el-button
            size="mini"
            type="primary"
            icon="el-icon-plus"
            @click="openUserbutesForm(null, -1, 'byDept')"
            >添加部门</el-button
          >
        </div>
      </div>
      <el-dialog
        :visible.sync="byDeptFormModelVisible"
        :close-on-click-modal="false"
        title="选择部门"
        width="80%"
        append-to-body
        destroy-on-close
      >
        <el-form :model="byDeptForm" label-width="80px" style="width: 99%" size="mini">
          <rule-to-dept :treedata="deptList" ref="assigneeByDeptRef"></rule-to-dept>
        </el-form>
        <template slot="footer">
          <el-button size="mini" @click="byDeptFormModelVisible = false">取 消</el-button>
          <el-button size="mini" type="primary" @click="saveAssigneeByUser('byDept')"
            >确 定</el-button
          >
        </template>
      </el-dialog>
      <div v-if="processTaskRule == 'byRole'">
        <template>
          <el-select v-model="userTaskForm.assigneeRoles" multiple placeholder="请选择角色">
            <el-option
              v-for="item in assigneeRoles"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
            </el-option>
          </el-select>
          <div class="element-drawer__button">
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-plus"
              @click="saveAssigneeByUser('byRole')"
              >确认</el-button
            >
          </div>
        </template>
      </div>
      <div v-if="processTaskRule == 'byPosition'">
        <template>
          <el-select v-model="userTaskForm.assigneePosition" multiple placeholder="请选择岗位">
            <el-option
              v-for="item in assigneePosition"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
            </el-option>
          </el-select>
          <div class="element-drawer__button">
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-plus"
              @click="saveAssigneeByUser('byPosition')"
              >确认</el-button
            >
          </div>
        </template>
      </div>
      <div v-if="processTaskRule == 'byGroup'">
        <template>
          <el-select v-model="userTaskForm.assigneeGroup" multiple placeholder="请选择分组">
            <el-option
              v-for="item in groupList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            >
            </el-option>
          </el-select>
          <div class="element-drawer__button">
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-plus"
              @click="saveAssigneeByUser('byGroup')"
            >确认</el-button
            >
          </div>
        </template>
      </div>
      <div v-if="processTaskRule == 'byDeptLeader'">
        <template>
          <div class="element-drawer__button">
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-plus"
              @click="saveAssigneeByUser('byDeptLeader')"
            >确认</el-button>
          </div>
        </template>
      </div>
      <div v-if="processTaskRule == 'byDeptMaster'">
        <template>
          <div class="element-drawer__button">
            <el-button
              size="mini"
              type="primary"
              icon="el-icon-plus"
              @click="saveAssigneeByUser('byDeptMaster')"
            >确认</el-button>
          </div>
        </template>
      </div>
    </el-form-item>
    <el-form-item  label="设置说明:" prop="overDueWarUrl">
      <span style="color: darkorange">设置或修改处理人规则后，需要保存发布才可生效</span>
    </el-form-item>
  </div>
</template>

<script>
  import { mapGetters } from 'vuex';
  import ruleToUser from '@/components/taskcomponents/ruleToUser.vue';
  import ruleToDept from '@/components/taskcomponents/ruleToDept.vue';
  import { getOrgData } from '@/api/orgm/orgm';
  import SelectTree from '@/components/selectTree/index.vue';
  import {
    setAssigneeUserWithTaskId,
    getAssigneeUserWithTaskId,
    deleteAssigneeUser,
    setFlowAttrs
  } from '@/api/process/process';
  export default {
    name: 'UserTask',
    props: {
      id: String,
      type: String,
      element:Object
    },
    data() {
      return {
        defaultTaskForm: {
          taskRules: '',
          assignee: '',
          assigneeRoles: '',
          assigneePosition: '',
          candidateUsers: [],
          candidateGroups: [],
          dueDate: '',
          priority: ''
        },
        userTaskForm: {
          taskRules: '',
          assignee: '',
          assigneeRoles: '',
          assigneePosition: '',
          assigneeGroup:'',
          candidateUsers: [],
          candidateGroups: [],
          dueDate: '',
          priority: ''
        },
        assigneeUserList: [],
        taskRules: [],
        deptList:[],
        groupList:[],
        taskList:[],
        processTaskRule: '',
        processReturnWay: '',
        processCCWay: '',
        userList: [],
        assigneeRoles: [],
        assigneePosition: [],
        byUserForm: {},
        byDeptForm: {},
        byUserFormModelVisible: false,
        byDeptFormModelVisible: false,
        editingCCWayIndex: -1,
        autoCCWayVisible: false,
        mockData: [1, 2, 3, 4, 5, 6, 7]
      };
    },
    watch: {
      id: {
        immediate: true,
        handler() {
          this.$nextTick(() => this.resetTaskForm());
        }
      }
    },
    components: {
      ruleToUser,
      ruleToDept,
      SelectTree
    },
    mounted() {
      this.taskLoad();
    },
    computed: {
      ...mapGetters('store/taskRule', [
        'getTaskRules',
        'getStartRules'
      ])
    },
    methods: {
      async taskLoad() {
        let taskType = '';
        if (this.element.businessObject.$type == 'bpmn:StartEvent') {
          taskType = 'bpmn:StartEvent';
        } else {
          const taskInComing = this.element.businessObject.incoming;
          const taskSourceRef = taskInComing[0].sourceRef;
          taskType = taskSourceRef.$type;
        }
        if (taskType == 'bpmn:StartEvent') {
          this.taskRules = this.getStartRules;
        } else {
          this.taskRules = this.getTaskRules;
        }
        let tasks=this.element.children;
        if(this.element.type=="bpmn:UserTask"){
          tasks=this.element.parent.children;
        }
        this.taskList=[];
        let taskElement=tasks.filter((item) => {
          if(item.businessObject.$type=="bpmn:UserTask"){
            let element= {
              id:item.businessObject.id,
              name:item.businessObject.name
            };
            if(this.element.id!=item.businessObject.id) {
              this.taskList.push(element);
            }
            return item;
          }
        });
        const res=await getOrgData();
        if (res.error == '200') {
          this.assigneeRoles = res.result.rolesList;
          this.deptList=res.result.departList;
          this.assigneePosition = res.result.positionList;
          this.groupList=res.result.groupEntityList;
        }
      },
      resetTaskForm() {
        this.taskLoad();
        for (let key in this.defaultTaskForm) {
          let value;
          if (key === 'candidateUsers') {
            value = this.element?.businessObject[key]
              ? this.element.businessObject[key].split(',')
              : [];
            //如果是usertask，将他的规则类型取出来
            if (value == undefined || value == '' || value == null) {
              value = [];
              this.processTaskRule = '';
            } else {
              let ruleRep = value[0].replace(this.id + '_', '');
              ruleRep = ruleRep.replace('$', '');
              ruleRep = ruleRep.replace('{', '');
              value = ruleRep.replace('}', '');
              this.processTaskRule = value;
              this.userTaskForm.taskRules = value;
              this.loadAssigneeUserList(value);
            }
          } else if (key === 'candidateGroups') {
            value = this.element?.businessObject[key]
              ? this.element.businessObject[key].split(',')
              : [];
          } else {
            value = this.element?.businessObject[key] || this.defaultTaskForm[key];
          }
          this.$set(this.userTaskForm, key, value);
        }
      }, //规则选择
      updateElementTask(key) {
        const taskAttr = Object.create(null);
        if (key == 'taskRules') {
          taskAttr['candidateUsers'] = '${' + this.id + '_' + this.userTaskForm[key] + '}';
          this.processTaskRule = this.userTaskForm[key];
          this.loadAssigneeUserList(this.processTaskRule);
        }
        window.bpmnInstances.modeling.updateProperties(this.element, taskAttr);
      }, //根据指定用户规则，设置任务处理人
      saveAssigneeByUser(key) {
        let task_def_key=this.id+"_"+key;
        let selectData = null;
        if (key == 'byUser') {
          selectData = this.$refs.assigneeByUserRef.saveTaskUser();
        } else if (key == 'byDept') {
          selectData = this.$refs.assigneeByDeptRef.saveTaskUser();
        } else if (key == 'byRole') {
          if(this.userTaskForm.assigneeRoles=='')
          {
            this.$message.warning('请选择角色');
            return;
          }
          selectData = [];
          let byRoleObj = {
            id: '',
            name: ''
          };
          this.userTaskForm.assigneeRoles.forEach((element) => {
            byRoleObj = {
              id: element,
              name: ''
            };
            selectData.push(byRoleObj);
          });
        } else if (key == 'byPosition') {
          if(this.userTaskForm.assigneePosition=='')
          {
            this.$message.warning('请选择岗位');
            return;
          }
          selectData = [];
          let byPositionObj = {
            id: '',
            name: ''
          };
          this.userTaskForm.assigneePosition.forEach((element) => {
            byPositionObj = {
              id: element,
              name: ''
            };
            selectData.push(byPositionObj);
          });
        }
        else if (key == 'byGroup') {
          if(this.userTaskForm.assigneeGroup=='')
          {
            this.$message.warning('请选择分组');
            return;
          }
          selectData = [];
          let byGroupObj = {
            id: '',
            name: ''
          };
          this.userTaskForm.assigneeGroup.forEach((element) => {
            byGroupObj = {
              id: element,
              name: ''
            };
            selectData.push(byGroupObj);
          });
        }
        // TODO:这里实现了在前端传入设置用户选择的实例类型
        setAssigneeUserWithTaskId({
          userlist: selectData,
          usertaskid: task_def_key,
          id:this.id,
          ruleName: key
        }).then((res) => {
          if (res.error == '200') {
            this.$message.success('设置成功');
            this.byUserFormModelVisible = false;
            this.byDeptFormModelVisible = false;
            this.loadAssigneeUserList(key);
          }
        });
      }, //根据指定用户规则，新增任务处理人
      openUserbutesForm(attr, index, type) {
        this.editingPropertyIndex = index;
        this.propertyForm = index === -1 ? {} : JSON.parse(JSON.stringify(attr));
        if (type == 'byUser') {
          this.byUserFormModelVisible = true;
        } else if (type == 'byDept') {
          this.byDeptFormModelVisible = true;
        }
      }, //根据指定用户规则，显示配置的任务处理人
      loadAssigneeUserList(key) {
        getAssigneeUserWithTaskId({ usertaskid: this.id, ruleName: key }).then((res) => {
          if (res.error == '200') {
            if (key == 'byRole') {
              var roleIntSet = [];
              for (let roleKey in res.result.assigneeUserEntityList) {
                if(res.result.assigneeUserEntityList[roleKey].username==""||
                  res.result.assigneeUserEntityList[roleKey].username==undefined){
                  roleIntSet.push(res.result.assigneeUserEntityList[roleKey].id);
                }
                else{
                  roleIntSet.push(res.result.assigneeUserEntityList[roleKey].username);
                }
              }
              this.userTaskForm.assigneeRoles = roleIntSet;
            } else if (key == 'byPosition') {
              var postIntSet = [];
              for (let ptKey in res.result.assigneeUserEntityList) {
                if(res.result.assigneeUserEntityList[ptKey].username==""||
                  res.result.assigneeUserEntityList[ptKey].username==undefined){
                  postIntSet.push(res.result.assigneeUserEntityList[ptKey].id);
                }
                else{
                  postIntSet.push(res.result.assigneeUserEntityList[ptKey].username);
                }
              }
              this.userTaskForm.assigneePosition = postIntSet;
            }else if (key == 'byGroup') {
              var groupIntSet = [];
              for (let ptKey in res.result.assigneeUserEntityList) {
                if(res.result.assigneeUserEntityList[ptKey].username==""||
                  res.result.assigneeUserEntityList[ptKey].username==undefined){
                  groupIntSet.push(res.result.assigneeUserEntityList[ptKey].id);
                }
                else{
                  groupIntSet.push(res.result.assigneeUserEntityList[ptKey].username);
                }
              }
              this.userTaskForm.assigneeGroup = groupIntSet;
            }else {
              this.assigneeUserList = res.result.assigneeUserEntityList;
            }
          }
        });
      }, //移除节点处理人
      removeUser(row, index) {
        deleteAssigneeUser({ usertaskid: this.id, username: row.username }).then((res) => {
          if (res.error == '200') {
            this.$message.success('移除成功');
            this.assigneeUserList.splice(index, 1);
          }
        });
      },
      //定义参数
      setFlowElementAttrs(redisKey, attrKey) {
        setFlowAttrs({ redisKey: redisKey, attrKey: attrKey }).then((res) => {});
      }
    },
    beforeDestroy() {

    }
  };
</script>
