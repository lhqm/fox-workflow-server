<template>
  <el-tabs :tab-position="tabPosition" style="overflow-y: hidden; height: 100%">
    <el-tab-pane label="流程管理">
      <el-container>
        <el-header style="text-align: center; height: auto">
          <h2>基本信息</h2>
          <el-divider></el-divider>
        </el-header>
        <el-main style="flex-grow: 1; height: 80vh">
          <div class="drawer-content" style="height: 100%">
            <el-form
              :model="ruleForm"
              ref="ruleForm"
              style="width: 95%"
              label-width="120px"
              class="demo-ruleForm"
            >
              <el-row>
                <el-col :span="24">
                  <el-form-item label="流程标题" prop="title">
                    <el-input readonly v-model="ruleForm.title"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="8">
                  <el-form-item label="发起人" prop="task_user">
                    <el-input readonly v-model="ruleForm.task_user"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="启动时间" prop="start_time">
                    <el-input readonly v-model="ruleForm.start_time"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="结束时间" prop="end_time">
                    <el-input readonly v-model="ruleForm.end_time"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="8">
                  <el-form-item label="流程名称" prop="flowname">
                    <el-input readonly v-model="ruleForm.flowname"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="运行状态" prop="state">
                    <el-input readonly v-model="ruleForm.state"></el-input>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="当前步骤" prop="task_def_name">
                    <el-input readonly v-model="ruleForm.task_def_name"></el-input>
                  </el-form-item>
                </el-col>
              </el-row>
              <el-row>
                <el-col :span="24">
                  <el-tabs type="border-card" v-model="proceActiveName">
                    <el-tab-pane name="rollBack">
                      <span slot="label"><i class="el-icon-refresh-left"></i> 回滚</span>
                      <el-form
                        :model="rollBackForm"
                        style="width: 95%"
                        label-width="100px"
                        class="demo-ruleForm"
                      >
                        <el-form-item label="回滚至步骤" prop="rollBackStepId">
                          <el-select v-model="rollBackForm.rollBackStepId" placeholder="请选择">
                            <el-option
                              v-for="item in userTaskTrack"
                              :key="item.userTaskId"
                              :label="item.userTaskName"
                              :value="item.userTaskId"
                            >
                            </el-option>
                          </el-select>
                        </el-form-item>

                        <el-form-item label="回滚原因" prop="rollBackMsg">
                          <el-input
                            type="textarea"
                            :rows="3"
                            v-model="rollBackForm.rollBackMsg"
                          ></el-input>
                        </el-form-item>

                        <el-form-item style="margin-right: 100px">
                          <el-button type="primary" @click="rollBackClick">回滚</el-button>
                        </el-form-item>
                      </el-form>
                    </el-tab-pane>
                    <el-tab-pane name="transfer">
                      <span slot="label"><i class="el-icon-upload"></i> 移交</span>
                      <el-form
                        style="width: 95%"
                        :model="transfer"
                        label-width="100px"
                        class="demo-ruleForm"
                      >
                        <el-form-item label="移交至：" prop="countersignUser">
                          <el-input
                            type="text"
                            readonly
                            v-model="transfer.selectEmpName"
                          ></el-input>
                          <el-input
                            type="text"
                            v-if="false"
                            v-model="transfer.selectEmp"
                          ></el-input>
                        </el-form-item>
                        <el-form-item>
                          <el-input placeholder="输入关键字进行过滤" v-model="transferFilterText">
                          </el-input>

                          <el-tree
                            class="filter-tree"
                            :data="userList"
                            :props="transferProps"
                            :filter-node-method="transferFilterNode"
                            @node-click="transferHandleNodeClick"
                            ref="transferTree"
                          >
                          </el-tree>
                        </el-form-item>

                        <el-form-item label="移交意见" prop="actionMsg">
                          <el-input
                            type="textarea"
                            :rows="3"
                            v-model="transfer.actionMsg"
                          ></el-input>
                        </el-form-item>

                        <el-form-item style="margin-right: 100px">
                          <el-button type="primary" @click="transferClick">移交</el-button>
                        </el-form-item>
                      </el-form>
                    </el-tab-pane>
                    <el-tab-pane name="setEndTask">
                      <span slot="label"><i class="el-icon-s-check"></i> 结束</span>
                      <el-form
                        style="width: 95%"
                        :model="transfer"
                        label-width="100px"
                        class="demo-ruleForm"
                      >
                        <el-form-item label="结束意见" prop="actionMsg">
                          <el-input
                            type="textarea"
                            :rows="3"
                            v-model="setEndTask.actionMsg"
                          ></el-input>
                        </el-form-item>

                        <el-form-item style="margin-right: 100px">
                          <el-button type="primary" @click="setEndTaskClick">结束</el-button>
                        </el-form-item>
                      </el-form>
                    </el-tab-pane>
                    <el-tab-pane name="deleteProce">
                      <span slot="label"><i class="el-icon-delete"></i> 删除</span>
                      <el-form
                        style="width: 95%"
                        :model="transfer"
                        label-width="100px"
                        class="demo-ruleForm"
                      >
                        <el-form-item label="删除原因" prop="actionMsg">
                          <el-input
                            type="textarea"
                            :rows="3"
                            v-model="deleteProce.actionMsg"
                          ></el-input>
                        </el-form-item>

                        <el-form-item style="margin-right: 100px">
                          <el-button type="primary" @click="deleteProceClick">删除</el-button>
                        </el-form-item>
                      </el-form>
                    </el-tab-pane>
                  </el-tabs>
                </el-col>
              </el-row>
            </el-form>
          </div>
        </el-main>
      </el-container>
    </el-tab-pane>
    <el-tab-pane label="业务表单">
      <el-container>
        <el-main style="flex-grow: 1; height: 80vh">
          <div style="overflow-y: auto; height: 100%">
            <slot>
              <preview
                v-if="form_type == '0'"
                :itemList="itemList"
                :itemDataJson="itemDataJson"
                :formConf="formConf"
                ref="preview"
              />
              <iframe
                v-if="form_type == '1'"
                :src="form_url"
                style="width: 100%; height: 100%"
                frameborder="0"
              ></iframe>
            </slot>
          </div>
        </el-main>
      </el-container>
    </el-tab-pane>
    <el-tab-pane label="审批轨迹" style="overflow-y: auto; height: 90vh">
      <el-container style="display: flex; flex-wrap: wrap">
        <div style="width: 100%">
          <el-header style="text-align: left; height: auto">
            <el-card style="background-color: darkseagreen; color: white"> 审批轨迹图 </el-card>
          </el-header>
          <el-main>
            <el-card>
              <div class="block">
                <el-image :src="trackImg">
                  <div slot="placeholder" class="image-slot">
                    加载中<span class="dot">...</span>
                  </div>
                </el-image>
              </div>
            </el-card>
          </el-main>
        </div>
        <div style="width: 100%">
          <el-header style="text-align: left; height: auto">
            <el-card style="background-color: darkseagreen; color: white"> 审批轨迹表 </el-card>
          </el-header>
          <el-main style="flex-grow: 1">
            <el-card>
              <el-table :data="track">
                <el-table-column label="序号" align="center" type="index" />
                <el-table-column
                  label="审核节点"
                  align="center"
                  prop="userTaskName"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  label="执行人"
                  align="center"
                  prop="userName"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  label="联审建议"
                  align="center"
                  prop="actionType"
                  :show-overflow-tooltip="true"
                >
                  <template slot-scope="scope">
                    <div slot="reference" class="name-wrapper">
                      <el-tag size="success" v-if="scope.row.actionType == 1">{{
                        scope.row.actionName
                      }}</el-tag>
                      <el-tag size="medium" v-else-if="scope.row.actionType == 0">{{
                        scope.row.actionName
                      }}</el-tag>
                      <el-tag size="danger" v-else-if="scope.row.actionType == 2">{{
                        scope.row.actionName
                      }}</el-tag>
                      <el-tag size="danger" v-else-if="scope.row.actionType == 5">{{
                        scope.row.actionName
                      }}</el-tag>
                      <el-tag size="success" v-else-if="scope.row.actionType == 6">{{
                        scope.row.actionName
                      }}</el-tag>
                      <el-tag size="warning" v-else>{{ scope.row.actionName }}</el-tag>
                    </div>
                  </template>
                </el-table-column>
                <el-table-column
                  label="审核意见"
                  align="center"
                  prop="msg"
                  :show-overflow-tooltip="true"
                />
                <el-table-column
                  label="审核时间"
                  align="center"
                  prop="rdt"
                  :show-overflow-tooltip="true"
                />
                <el-table-column label="审核附件" align="center" :show-overflow-tooltip="true">
                  <template slot-scope="scope">
                    <div slot="reference" class="name-wrapper">
                      <el-link
                        type="primary"
                        @click="downApprovalFile(scope.row.filePath, scope.row.msgFiles)"
                        >{{ scope.row.msgFiles }}</el-link
                      >
                    </div>
                  </template>
                </el-table-column>
              </el-table>
            </el-card>
          </el-main>
        </div>
      </el-container>
    </el-tab-pane>
  </el-tabs>
</template>
<script>
  import preview from '@/components/formcomponents/preview';
  import { getApprovalTracks, getUserTaskTrack } from '@/api/reviewpage/reviewpage';
  import { getToken } from '@/utils/auth';
  import { getDeptUserTree } from '@/api/orgm/orgm';
  import { returnWork, transfer, setEndTask, deleteProcInst } from '@/api/task/task';
  export default {
    name: 'manageView',
    data() {
      return {
        itemList: this.mapList,
        formConf: this.mapConfig,
        itemDataJson: this.dataJson,
        tabPosition: 'left',
        trackImg: '',
        proceActiveName: 'rollBack',
        track: [],
        userTaskTrack: [],
        userList: [],
        rollBackForm: {
          rollBackStepId: '',
          rollBackMsg: ''
        },
        transferFilterText: '',
        transfer: {
          selectEmpName: '',
          selectEmp: '',
          actionMsg: ''
        },
        transferProps: {
          children: 'children',
          label: 'name',
          id: 'username',
          state: 'state'
        },
        setEndTask: {
          actionMsg: ''
        },
        deleteProce: {
          actionMsg: ''
        }
      };
    },
    components: {
      preview
    },
    props: {
      processKey: String,
      processName: String,
      procinstid: String,
      form_type: String,
      form_url: String,
      dataJson: Object,
      bid: String,
      taskid: String,
      delete_reason: String,
      ruleForm: Object,
      mapList: Array,
      mapConfig: Object
    },
    mounted() {
      this.showFlowTrack();
      this.getApprovalTracks();
    },
    methods: {
      showFlowTrack() {
        this.$axios
          .get('/api/' + this.Apis.getFlowTrack, {
            params: {
              processInstanceId: this.procinstid,
              porcessKey: this.processKey
            },
            headers: {
              Authorization: 'Bearer ' + getToken()
            },
            responseType: 'blob' // 返回数据类型
          })
          .then((response) => {
            this.trackImg = window.URL.createObjectURL(response.data);
          });
      },
      getApprovalTracks() {
        getApprovalTracks(this.procinstid).then((res) => {
          if (res.error == '200') {
            this.track = res.result;
          } else {
            this.$message.warning(res.error);
          }
        });
        getUserTaskTrack(this.procinstid).then((res) => {
          if (res.error == '200') {
            this.userTaskTrack = res.result;
          } else {
            this.$message.warning(res.error);
          }
        });
        getDeptUserTree().then((res) => {
          if (res.error == '200') {
            this.userList = res.result;
          }
        });
      },
      downApprovalFile(url, fileName) {
        this.$axios
          .post('api/' + this.Apis.downFiles, {
            params: {
              fileName: fileName,
              filePath: url
            },
            headers: {
              Authorization: 'Bearer ' + getToken()
            },
            responseType: 'blob' // 这里是声明期望返回的数据类型,为arraybuffer
          })
          .then((response) => {
            const { data } = response;
            const blob = new Blob([data]);
            const disposition = decodeURI(response.headers['content-disposition']);
            // 从响应头中获取文件名称
            if ('download' in document.createElement('a')) {
              // 非IE下载
              const elink = document.createElement('a');
              elink.download = name;
              elink.style.display = 'none';
              elink.href = URL.createObjectURL(blob);
              document.body.appendChild(elink);
              elink.click();
              URL.revokeObjectURL(elink.href); // 释放URL 对象
              document.body.removeChild(elink);
            } else {
              // IE10+下载
              navigator.msSaveBlob(blob, name);
            }
          });
      },
      rollBackClick() {
        this.$confirm('确定要执行回滚?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            if (this.taskid == '0') {
              this.$message.warning('当前流程已结束，不可回滚。');
            } else {
              returnWork(
                this.procinstid,
                this.taskid,
                this.rollBackForm.rollBackStepId,
                this.rollBackForm.rollBackMsg
              ).then((res) => {
                if (res.error == '200') {
                  this.$message.success('当前流程已经回滚，等待处理人处理。');
                  this.cloaseDialog();
                } else {
                  this.$message.warning('回滚失败，请联系管理员。' + res.result);
                }
              });
            }
          })
          .catch(() => {});
      },
      transferClick() {
        this.$confirm('确定执行移交?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            if (this.taskid == '0') {
              this.$message.warning('当前流程已结束，不可移交。');
            } else {
              transfer(
                this.procinstid,
                this.taskid,
                this.transfer.actionMsg,
                this.transfer.selectEmp
              ).then((res) => {
                if (res.error == '200') {
                  this.$message.success('当前步骤已完成加签，等待移交人进行审核。');
                  this.cloaseDialog();
                } else {
                  this.$message.warning('移交失败，请联系管理员。' + res.result);
                }
              });
            }
          })
          .catch(() => {});
      },
      setEndTaskClick() {
        this.$confirm('确定结束该流程?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            if (this.taskid == '0') {
              this.$message.warning('当前流程已结束，无需再次操作。');
            } else {
              setEndTask(this.procinstid, this.taskid, this.setEndTask.actionMsg).then((res) => {
                if (res.error == '200') {
                  this.$message.success('当前流程已结束。');
                  this.cloaseDialog();
                } else {
                  this.$message.warning('结束失败，请联系管理员。' + res.result);
                }
              });
            }
          })
          .catch(() => {});
      },
      deleteProceClick() {
        this.$confirm('确定删除该流程?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            if (this.delete_reason == 'deleteTask') {
              this.$message.warning('当前流程已经删除，无需再次操作。');
            } else {
              deleteProcInst(this.procinstid, this.taskid, this.setEndTask.actionMsg).then(
                (res) => {
                  if (res.error == '200') {
                    this.$message.success('当前流程已删除。');
                    this.cloaseDialog();
                  } else {
                    this.$message.warning('删除失败，请联系管理员。' + res.result);
                  }
                }
              );
            }
          })
          .catch(() => {});
      },
      cloaseDialog() {
        this.$emit('cloaseDialog');
      },
      transferFilterNode(value, data) {
        if (!value) return true;
        return data.name.indexOf(value) !== -1;
      },
      transferHandleNodeClick(data) {
        if (data.state == 1) {
          this.transfer.selectEmp = data.username;
          this.transfer.selectEmpName = data.name;
        }
      }
    },
    watch: {
      countersignFilterText(val) {
        this.$refs.countersignTree.filter(val);
      },
      transferFilterText(val) {
        this.$refs.transferTree.filter(val);
      }
    }
  };
</script>
<style>
  .el-container {
    height: 100%;
  }
</style>
