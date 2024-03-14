<template>
  <el-tabs :tab-position="tabPosition" style="height: 100%">
    <el-tab-pane label="流程审核" style="height: 100%; padding-bottom: 20px">
      <el-container v-loading="loading">
        <el-header style="text-align: right; height: auto">
          <el-card>
            <el-button
              icon="el-icon-remove"
              type="danger"
              v-if="toolBar.end"
              @click="actionClick('setEndTask')"
              >结束</el-button
            >
            <el-button
              v-if="toolBar.ccWay == 'manualCC'"
              icon="el-icon-position"
              @click="actionClick('copyFor')"
              >知会</el-button
            >
            <el-button
              icon="el-icon-s-custom"
              v-if="toolBar.transfer"
              @click="actionClick('transfer')"
              >移交</el-button
            >
            <el-button
              icon="el-icon-share"
              v-if="toolBar.countersign"
              @click="actionClick('countersign')"
              >加签</el-button
            >
            <el-button icon="el-icon-s-release" v-if="toolBar.refuse" @click="actionClick('refuse')"
              >不同意</el-button
            >
            <el-button v-if="toolBar.returnWork != 'none'" @click="showReturnModal">驳回</el-button>
            <el-button icon="el-icon-s-claim" @click="save">保存</el-button>
            <el-button
              icon="el-icon-success"
              type="primary"
              v-if="toolBar.processStart"
              @click="processStart"
              >提交</el-button
            >
            <el-button
              icon="el-icon-success"
              type="primary"
              v-if="toolBar.sendWork"
              @click="sendWork"
              >同意</el-button
            >
            <el-button icon="el-icon-error" type="danger" @click="cloaseDialog">关闭</el-button>
          </el-card>
        </el-header>
        <el-main style="flex-grow: 1; height: 80vh">
          <div style="overflow-y: auto; height: 100%">
            <slot>
              <preview
                v-loading="loading"
                element-loading-text="请等待..."
                element-loading-spinner="el-icon-loading"
                v-if="form_type == '0'"
                :itemList="itemList"
                :itemDataJson="itemDataJson"
                :formConf="formConf"
                ref="preview"
              />
              <iframe
                ref="previewIframe"
                v-if="form_type == '1'"
                :src="form_url"
                style="width: 100%; height: 100%"
                frameborder="0"
              ></iframe>
            </slot>
            <slot
              style="height: 100%"
              v-if="
                this.procinstid != '' && this.procinstid != null && this.procinstid != undefined
              "
            >
              <el-card style="margin-top: 10px">
                <el-form label-width="80px" :model="footerApproval">
                  <el-form-item label="审核意见:">
                    <el-input
                      type="textarea"
                      :autosize="{ minRows: 3, maxRows: 6 }"
                      v-model="footerApproval.msg"
                    ></el-input>
                  </el-form-item>
                  <el-form-item label="审核附件:">
                    <el-upload
                      class="upload-demo"
                      :action="action"
                      :limit="1"
                      :multiple="false"
                      accept="*.*"
                      :on-success="fileOnSuccess"
                      :file-list="fileList"
                    >
                      <el-button size="small" type="primary">点击上传</el-button>
                    </el-upload>
                  </el-form-item>
                </el-form>
                <el-form
                  label-width="80px"
                  :inline="true"
                  :model="footerApproval"
                  class="demo-form-inline"
                >
                  <el-form-item label="审批人:">
                    <el-input v-model="footerApproval.user" readonly></el-input>
                  </el-form-item>
                  <el-form-item label="审批时间:">
                    <el-input v-model="footerApproval.rdt" readonly></el-input>
                  </el-form-item>
                </el-form>
              </el-card>
            </slot>
          </div>
          <el-dialog
            title="驳回"
            v-if="dialogNewSortVisible == true"
            :close-on-click-modal="false"
            :visible.sync="dialogNewSortVisible"
            :append-to-body="true"
            @close="loading = false"
            custom-class="center-dialog"
          >
            <slot name="-" style="border: none; padding: 0px">
              <el-form
                :model="returnForm"
                style="width: 95%"
                label-width="100px"
                class="demo-ruleForm"
              >
                <el-form-item label="退回步骤" prop="stepid">
                  <el-select v-model="returnForm.stepid" placeholder="请选择">
                    <el-option
                      v-for="item in userTaskTrack"
                      :key="item.userTaskId"
                      :label="item.userTaskName"
                      :value="item.userTaskId"
                    >
                    </el-option>
                  </el-select>
                </el-form-item>

                <el-form-item label="退回意见" prop="msg">
                  <el-input type="textarea" :rows="3" v-model="returnForm.msg"></el-input>
                </el-form-item>

                <el-form-item style="margin-right: 100px">
                  <el-button type="primary" @click="returnWork">确认</el-button>
                  <el-button
                    @click="
                      dialogNewSortVisible = false;
                      loading = false;
                    "
                    >取消</el-button
                  >
                </el-form-item>
              </el-form>
            </slot>
          </el-dialog>
          <el-dialog
            :title="dialogTitle"
            v-if="dialogSelectVisible == true"
            :close-on-click-modal="false"
            :visible.sync="dialogSelectVisible"
            :append-to-body="true"
            custom-class="center-dialog"
            @close="loading = false"
          >
            <slot name="-" style="border: none; padding: 0px">
              <el-form style="width: 95%" label-width="100px" class="demo-ruleForm">
                <el-form-item
                  :label="selectLabel"
                  v-if="actionType == 'transfer' || actionType == 'countersign'"
                  prop="selectEmp"
                >
                  <el-input type="text" readonly v-model="selectEmpName"></el-input>
                  <el-input type="text" v-if="textIsShow" v-model="selectEmp"></el-input>
                </el-form-item>
                <el-form-item v-if="actionType == 'transfer' || actionType == 'countersign'">
                  <el-input placeholder="输入关键字进行过滤" v-model="filterText"> </el-input>

                  <el-tree
                    class="filter-tree"
                    :data="userList"
                    :props="defaultProps"
                    :filter-node-method="filterNode"
                    @node-click="handleNodeClick"
                    ref="tree"
                  >
                  </el-tree>
                </el-form-item>

                <el-form-item label="执行意见" prop="msg">
                  <el-input type="textarea" :rows="3" v-model="actionMsg"></el-input>
                </el-form-item>

                <el-form-item style="margin-right: 100px">
                  <el-button v-if="actionType == 'transfer'" type="primary" @click="transferClick"
                    >确认</el-button
                  >
                  <el-button
                    v-if="actionType == 'countersign'"
                    type="primary"
                    @click="countersignClick"
                    >确认</el-button
                  >
                  <el-button v-if="actionType == 'refuse'" type="primary" @click="refuseClick"
                    >确认</el-button
                  >
                  <el-button
                    v-if="actionType == 'setEndTask'"
                    type="primary"
                    @click="setEndTaskClick"
                    >确认</el-button
                  >
                  <el-button
                    @click="
                      dialogSelectVisible = false;
                      loading = false;
                    "
                    >取消</el-button
                  >
                </el-form-item>
              </el-form>
            </slot>
          </el-dialog>
          <el-dialog
            title="请选择下一步"
            v-if="dialogSelectNode == true"
            :close-on-click-modal="false"
            :visible.sync="dialogSelectNode"
            :append-to-body="true"
            custom-class="center-dialog"
            @close="closeSelctUserDialog"
          >
            <slot name="-" style="border: none; padding: 0px">
              <el-form ref="dataForm" label-suffix="：" label-width="110px" class="dialog-form">
                <el-form-item
                  v-if="approval.variables.expType === 'ExclusiveGateway'"
                  v-for="item in nextNodes"
                  :key="item.id"
                >
                  <el-form-item>
                    <el-radio v-model="approval.variables.nextNode" :label="item.id">{{
                      item.name
                    }}</el-radio>
                  </el-form-item>
                </el-form-item>
                <el-form-item v-if="approval.variables.expType === 'InclusiveGateway'">
                  <el-checkbox-group v-model="checkedNodes">
                    <el-checkbox v-for="node in nextNodes" :label="node.id" :key="node.id">{{
                      node.name
                    }}</el-checkbox>
                  </el-checkbox-group>
                </el-form-item>
              </el-form>
              <template slot="footer">
                <el-button @click="closeSelctUserDialog">取 消</el-button>
                <el-button type="primary" @click="sendWork">确 定</el-button>
              </template>
            </slot>
          </el-dialog>
          <el-dialog
            title="信息提示"
            :visible.sync="dialogVisible"
            width="600px"
            destroy-on-close
            :close-on-click-modal="false"
            :close-on-press-escape="false"
            :append-to-body="true"
          >
            <el-form ref="dataForm" label-suffix="：" label-width="110px" class="dialog-form">
              <el-form-item v-if="resType == 'suecess'">
                <p
                  ><i
                    class="el-icon-check"
                    style="color: #55a532; font-size: x-large; font-weight: 800"
                  ></i
                  >&nbsp;&nbsp;操作成功。</p
                >
                <p
                  ><i
                    class="el-icon-info"
                    style="color: #55a532; font-size: x-large; font-weight: 800"
                  ></i
                  >&nbsp;&nbsp;{{ this.resTypeMsg }}</p
                >
                <p
                  ><i
                    class="el-icon-video-camera-solid"
                    style="color: #55a532; font-size: x-large; font-weight: 800"
                  ></i
                  >&nbsp;&nbsp;您可以在已办任务中，查看流程审核轨迹。</p
                >
              </el-form-item>
              <el-form-item v-if="resType == 'error'">
                <p
                  ><i
                    class="el-icon-warning"
                    style="color: #bd2c00; font-size: x-large; font-weight: 800"
                  ></i
                  >&nbsp;&nbsp;操作失败。</p
                >
                <p
                  ><i
                    class="el-icon-warning"
                    style="color: #bd2c00; font-size: x-large; font-weight: 800"
                  ></i
                  >&nbsp;&nbsp;{{ this.resTypeMsg }}</p
                >
                <p
                  ><i
                    class="el-icon-message-solid"
                    style="color: #0d72ff; font-size: x-large; font-weight: 800"
                  ></i
                  >&nbsp;&nbsp;您可联系管理员检查流程配置是否正确。</p
                >
              </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
              <el-button v-if="resType == 'error'" @click="dialogVisible = false"> 确定 </el-button>
              <el-button v-if="resType == 'suecess'" type="primary" @click="cloaseDialog">
                确定
              </el-button>
            </div>
          </el-dialog>
        </el-main>
      </el-container>
    </el-tab-pane>
    <el-tab-pane label="审批轨迹" style="overflow-y: auto; height: 90vh">
      <el-container v-loading="loading" style="display: flex; flex-wrap: wrap">
        <div style="width: 100%">
          <el-header style="text-align: left; height: auto">
            <el-card style="background-color: cornflowerblue; color: white"> 审批轨迹图 </el-card>
          </el-header>
          <el-main style="flex-grow: 1">
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
            <el-card style="background-color: cornflowerblue; color: white"> 审批轨迹表 </el-card>
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
                  label="审核意见"
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
  import {
    getElementButton,
    getApprovalTracks,
    saveFormDataJson,
    getUserTaskTrack,
    downLoadFile
  } from '@/api/reviewpage/reviewpage';
  import {
    startProcess,
    sendWork,
    returnWork,
    transfer,
    refuse,
    countersign, setEndTask
  } from '@/api/task/task';
  import { getToken, getName } from '@/utils/auth';
  import { getDeptUserTree } from '@/api/orgm/orgm';
  import preview from '@/components/formcomponents/preview';
  export default {
    name: 'reviewpage',
    data() {
      return {
        // 表单字段
        itemList: this.mapList,
        // 表单配置文件
        formConf: this.mapConfig,
        // 表单数据
        itemDataJson: this.dataJson,
        // 展示方向
        tabPosition: 'left',
        // 加载loading
        loading: false,
        // 驳回窗体
        dialogNewSortVisible: false,
        // 抄送窗体
        ccDefaultVisible: false,
        // 选择所有人窗体
        dialogAllUser: false,
        // 选择指定人窗体
        dialogSelectUser: false,
        // 选择下一步
        dialogSelectNode: false,
        // 选中的节点集合
        checkedNodes: [],
        // 发送后信息提示窗
        dialogVisible: false,
        // 功能操作窗体，加签、移交等
        dialogSelectVisible: false,
        // 是否改变
        isTreeChange: false,
        // 弹窗标题
        dialogTitle: '',
        // 默认信息类型
        resType: 'suecess',
        // 默认提示信息
        resTypeMsg: '',
        // 业务id
        bussid: '',
        // 轨迹图
        trackImg: '',
        // 操作按钮
        toolBar: {
          end: false,
          transfer: false,
          countersign: false,
          refuse: false,
          returnWork: 'none',
          processStart: false,
          sendWork: true,
          ccWay: 'none',
          autoCCWay: 'none',
          task_def_key: ''
        },
        // 发送参数
        approval: {
          taskId: this.taskid,
          processInstanceId: this.procinstid,
          businessKey: this.bid,
          ccData: [],
          msg: '',
          msgFiles: '',
          filePath: '',
          variables: {
            expType: '',
            nextNode: ''
          }
        },
        // 总数，分页时有效
        total: 0,
        // 选择接收人时有效
        searchForm: {
          page: 1,
          pagesize: 5,
          bmsType: '',
          task_def_id: ''
        },
        // 退回参数，驳回时有效
        returnForm: {
          stepid: '',
          msg: ''
        },
        // 人员列表
        userList: [],
        // 选择的人员列表
        selectUserList: [],
        // 选中的数据
        selectData: [],
        // 是否显示，选择人时有效
        textIsShow: false,
        // 选中的人
        selectEmp: '',
        // 选中人的名称
        selectEmpName: '',
        // 显示标签
        selectLabel: '',
        // 活动提示信息
        actionMsg: '',
        // 活动类型
        actionType: '',
        // 文本查询
        filterText: '',
        // 节点默认配置
        defaultProps: {
          children: 'children',
          label: 'name',
          id: 'username',
          state: 'state'
        },
        // 下一步要到达的节点集合，选择方向时有效
        nextNodes: [],
        // 附件集合
        fileList: [],
        // 轨迹集合
        track: [],
        // 用户轨迹集合
        userTaskTrack: [],
        // 上传附件服务
        action: '/api/' + this.Apis.uploadfiles + '?access_token=' + getToken(),
        footerApproval: {
          msg: '',
          user: getName(),
          rdt: this.getTime()
        }
      };
    },
    components: {
      preview
    },
    props: {
      processKey: String,
      processName: String,
      form_type: String,
      form_url: String,
      taskid: String,
      bid: String,
      dataJson: Object,
      procinstid: String,
      mapList: Array,
      mapConfig: Object
    },
    mounted() {
      this.getToolBar();
      this.showFlowTrack();
      this.getApprovalTracks();
    },
    methods: {
      // 轨迹流程轨迹图
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
      // 流程审核列表
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
      },
      // 流程控制按钮
      getToolBar() {
        if (this.procinstid == undefined || this.procinstid == '' || this.procinstid == null) {
          this.toolBar.processStart = true;
          this.toolBar.sendWork = false;
          this.tabPosition = 'left';
          return;
        }
        getElementButton(this.taskid).then((res) => {
          if (res.error == '200') {
            if (res.result.transfer == 1) {
              this.toolBar.transfer = true;
            }
            this.toolBar.returnWork = res.result.returnWay;
            if (res.result.countersign == 1) {
              this.toolBar.countersign = true;
            }
            if (res.result.endTask == 1) {
              this.toolBar.end = true;
            }
            if (res.result.refuse == 1) {
              this.toolBar.refuse = true;
            }
            this.toolBar.ccWay = res.result.ccWay;
            this.toolBar.autoCCWay = res.result.autoCCWay;
            this.toolBar.task_def_key = res.result.task_def_key;
          } else {
            this.$message.warning(res.error);
          }
        });
      },
      // 保存
      save() {
        this.loading = true;
        let refList = {
          keyList: []
        };
        if (this.form_type == '0') {
          refList = this.$refs.preview.saveSelf();
        }
        const mapConfig = {
          config: this.mapConfig,
          list: refList.keyList
        };
        const mapString = JSON.stringify(mapConfig);
        let generid = '';

        if (this.bussid == '') {
          if (this.form_type == '1') {
            generid = this.$refs.previewIframe.contentWindow.SaveData();
          } else {
            generid = this.bid;
          }
        } else {
          generid = this.bussid;
        }
        saveFormDataJson(
          this.processKey,
          generid,
          this.form_type,
          this.form_url + '?businessKey=' + generid,
          mapString,
          JSON.stringify(refList.keyValueList)
        ).then((res) => {
          if (res.error == '200') {
            this.bussid = res.result;
            this.$message.success('保存成功');
          } else {
            this.$message.error('保存失败');
          }
          this.loading = false;
        });
      },
      // 启动流程
      processStart() {
        this.loading = true;
        let refList = {
          keyList: []
        };
        if (this.form_type == '0') {
          refList = this.$refs.preview.saveSelf();
        }
        const newRefList = refList.keyList.filter((item) => {
          if (
            item.compType == 'text' ||
            item.compType == 'divider' ||
            item.compType == 'barCode' ||
            item.compType == 'alert' ||
            item.compType == 'link' ||
            item.compType == 'button' ||
            item.compType == 'colorPicker'
          ) {
            return item;
          } else if (item.compType == 'dynamicTable') {
            const childrenColumns = item.columns.filter((col) => {
              if (col.compType == 'text') {
                return col;
              } else if (col.compType == 'upload') {
                return col;
              } else {
                col.readonly = true;
                col.disabled = true;
                col.rules = [];
                return col;
              }
            });
            item.buttonAdd = false;
            item.buttonDel = false;
            return (item.columns = childrenColumns);
          } else if (item.compType == 'row') {
            const rows = item.columns;
            const colRows = rows.filter((row) => {
              const rowList = row.list;
              const colRowList = rowList.filter((rl) => {
                if (
                  rl.compType == 'text' ||
                  rl.compType == 'divider' ||
                  rl.compType == 'barCode' ||
                  rl.compType == 'alert' ||
                  rl.compType == 'link' ||
                  rl.compType == 'button' ||
                  rl.compType == 'colorPicker'
                ) {
                  return rl;
                } else if (rl.compType == 'dynamicTable') {
                  const childrenColumns = rl.columns.filter((col) => {
                    if (col.compType == 'text') {
                      return col;
                    } else if (col.compType == 'upload') {
                      return col;
                    } else {
                      col.readonly = true;
                      col.disabled = true;
                      col.rules = [];
                      return col;
                    }
                  });
                  rl.buttonAdd = false;
                  rl.buttonDel = false;
                  return (rl.columns = childrenColumns);
                } else {
                  rl.readonly = true;
                  rl.disabled = true;
                  rl.rules = [];
                  return rl;
                }
              });
              row.list = colRowList;
              return row;
            });
            return (item.columns = colRows);
          } else {
            item.readonly = true;
            item.disabled = true;
            item.rules = [];
            return item;
          }
        });
        const mapConfig = {
          config: this.mapConfig,
          list: newRefList
        };
        const mapString = JSON.stringify(mapConfig);
        let generid = '';
        if (this.bussid == '') {
          if (this.form_type == '1') {
            generid = this.$refs.previewIframe.contentWindow.SaveData();
          } else {
            generid = this.bid;
          }
        } else {
          generid = this.bussid;
        }

        saveFormDataJson(
          this.processKey,
          generid,
          this.form_type,
          this.form_url + '?businessKey=' + generid,
          mapString,
          JSON.stringify(refList.keyValueList)
        ).then((res) => {
          if (res.error == '200') {
            this.bussid = res.result;
            startProcess(this.processKey, this.processName, this.bussid).then((res) => {
              if (res.error == '200') {
                if (res.result.state == '200') {
                  if (res.result.msg == 'byManuallySelectDept') {
                    this.searchForm.bmsType = 'byManuallySelectDept';
                    this.searchForm.task_def_id = res.result.task_key;
                    this.approval.businessKey = this.bussid;
                    this.approval.processInstanceId = res.result.proc_inst_id;
                    this.approval.taskId = res.result.taskId;
                    this.loading = true;
                    this.getNextUsers();
                    this.dialogSelectUser = true;
                  } else if (res.result.msg == 'byManuallySelectRole') {
                    this.searchForm.bmsType = 'byManuallySelectRole';
                    this.searchForm.task_def_id = res.result.task_key;
                    this.approval.businessKey = this.bussid;
                    this.approval.processInstanceId = res.result.proc_inst_id;
                    this.approval.taskId = res.result.taskId;
                    this.loading = true;
                    this.getNextUsers();
                    this.dialogSelectUser = true;
                  } else if (res.result.msg == 'allUser') {
                    this.approval.businessKey = this.bussid;
                    this.approval.processInstanceId = res.result.proc_inst_id;
                    this.approval.taskId = res.result.taskId;
                    this.searchForm.task_def_id = res.result.task_key;
                    this.loading = true;
                    this.dialogAllUser = true;
                  } else if (res.result.msg == 'byManuallySelectStion') {
                    this.searchForm.bmsType = 'byManuallySelectStion';
                    this.searchForm.task_def_id = res.result.task_key;
                    this.approval.businessKey = this.bussid;
                    this.approval.processInstanceId = res.result.proc_inst_id;
                    this.approval.taskId = res.result.taskId;
                    this.loading = true;
                    this.getNextUsers();
                    this.dialogSelectUser = true;
                  }
                  // 选择方向
                  else if (res.result.msg == 'bySelect') {
                    this.approval.businessKey = this.bussid;
                    this.approval.processInstanceId = res.result.proc_inst_id;
                    this.approval.taskId = res.result.taskId;
                    this.approval.variables.expType = res.result.params;
                    this.nextNodes = res.result.task_key;
                    this.dialogSelectNode = true;
                  } else {
                    this.loading = false;
                    this.resType = 'suecess';
                    this.resTypeMsg = '流程启动成功，流程进入下一步审核。';
                    this.dialogVisible = true;
                  }
                } else {
                  this.resType = 'suecess';
                  this.resTypeMsg = '流程启动成功，流程进入下一步审核。';
                  this.dialogVisible = true;
                }
              } else {
                this.resType = 'error';
                if (res.error == '500') this.resTypeMsg = res.msg;
                else this.resTypeMsg = res.result.msg;
                this.loading = false;
                this.dialogVisible = true;
              }
            });
          } else {
            this.resType = 'error';
            this.resTypeMsg = res.msg;
            this.loading = false;
            this.dialogVisible = true;
          }
        });
      },
      // 发送到下一步
      sendWork() {
        this.loading = true;
        let nextNodes = '';
        if (this.approval.variables.expType === 'InclusiveGateway') {
          this.approval.variables.nextNode = '';
          this.checkedNodes.forEach(function (item) {
            nextNodes += item + ',';
          });
          this.approval.variables.nextNode = nextNodes;
        }
        let refList = {
          keyList: []
        };
        if (this.form_type == '0') {
          refList = this.$refs.preview.saveSelf();
        }
        const newRefList = refList.keyList.filter((item) => {
          if (
            item.compType == 'text' ||
            item.compType == 'divider' ||
            item.compType == 'barCode' ||
            item.compType == 'alert' ||
            item.compType == 'link' ||
            item.compType == 'button' ||
            item.compType == 'colorPicker'
          ) {
            return item;
          } else if (item.compType == 'dynamicTable') {
            const childrenColumns = item.columns.filter((col) => {
              if (col.compType == 'text') {
                return col;
              } else if (col.compType == 'upload') {
                return col;
              } else {
                col.readonly = true;
                col.disabled = true;
                col.rules = [];
                return col;
              }
            });
            item.buttonAdd = false;
            item.buttonDel = false;
            return (item.columns = childrenColumns);
          } else if (item.compType == 'row') {
            const rows = item.columns;
            const colRows = rows.filter((row) => {
              const rowList = row.list;
              const colRowList = rowList.filter((rl) => {
                if (
                  rl.compType == 'text' ||
                  rl.compType == 'divider' ||
                  rl.compType == 'barCode' ||
                  rl.compType == 'alert' ||
                  rl.compType == 'link' ||
                  rl.compType == 'button' ||
                  rl.compType == 'colorPicker'
                ) {
                  return rl;
                } else if (rl.compType == 'dynamicTable') {
                  const childrenColumns = rl.columns.filter((col) => {
                    if (col.compType == 'text') {
                      return col;
                    } else if (col.compType == 'upload') {
                      return col;
                    } else {
                      col.readonly = true;
                      col.disabled = true;
                      col.rules = [];
                      return col;
                    }
                  });
                  rl.buttonAdd = false;
                  rl.buttonDel = false;
                  return (rl.columns = childrenColumns);
                } else {
                  rl.readonly = true;
                  rl.disabled = true;
                  rl.rules = [];
                  return rl;
                }
              });
              row.list = colRowList;
              return row;
            });
            return (item.columns = colRows);
          } else {
            item.readonly = true;
            item.disabled = true;
            item.rules = [];
            return item;
          }
        });
        const mapConfig = {
          config: this.mapConfig,
          list: newRefList
        };
        const mapString = JSON.stringify(mapConfig);
        let generid = '';
        if (this.bussid == '') {
          generid = this.bid;
        } else {
          generid = this.bussid;
        }
        saveFormDataJson(
          this.processKey,
          generid,
          this.form_type,
          this.form_url,
          mapString,
          JSON.stringify(refList.keyValueList)
        ).then((res) => {
          if (res.error == '200') {
            this.approval.msg = this.footerApproval.msg;
            this.approval.businessKey = res.result;
            sendWork(this.approval).then((res) => {
              if (res.result.state == '200') {
                if (res.result.msg == 'byManuallySelectDept') {
                  this.searchForm.bmsType = 'byManuallySelectDept';
                  this.searchForm.task_def_id = res.result.task_key;
                  this.approval.processInstanceId = res.result.proc_inst_id;
                  this.approval.taskId = res.result.taskId;
                  this.getNextUsers();
                  this.loading = true;
                  this.dialogSelectUser = true;
                } else if (res.result.msg == 'byManuallySelectRole') {
                  this.searchForm.bmsType = 'byManuallySelectRole';
                  this.searchForm.task_def_id = res.result.task_key;
                  this.approval.processInstanceId = res.result.proc_inst_id;
                  this.approval.taskId = res.result.taskId;
                  this.getNextUsers();
                  this.loading = true;
                  this.dialogSelectUser = true;
                } else if (res.result.msg == 'allUser') {
                  this.searchForm.task_def_id = res.result.task_key;
                  this.approval.processInstanceId = res.result.proc_inst_id;
                  this.approval.taskId = res.result.taskId;
                  this.loading = true;
                  this.dialogAllUser = true;
                } else if (res.result.msg == 'byManuallySelectStion') {
                  this.searchForm.bmsType = 'byManuallySelectStion';
                  this.searchForm.task_def_id = res.result.task_key;
                  this.approval.processInstanceId = res.result.proc_inst_id;
                  this.approval.taskId = res.result.taskId;
                  this.getNextUsers();
                  this.loading = true;
                  this.dialogSelectUser = true;
                }
                // 选择方向
                else if (res.result.msg == 'bySelect') {
                  this.approval.businessKey = this.bussid;
                  this.approval.processInstanceId = res.result.proc_inst_id;
                  this.approval.taskId = res.result.taskId;
                  this.approval.variables.expType = res.result.params;
                  this.nextNodes = res.result.task_key;
                  this.dialogSelectNode = true;
                } else {
                  this.resType = 'suecess';
                  this.resTypeMsg = '当前步骤已完成，流程进入下一步审核。';
                  this.loading = true;
                  this.dialogVisible = true;
                }
              } else {
                this.resType = 'suecess';
                this.resTypeMsg = '当前步骤已完成，流程进入下一步审核。';
                this.loading = false;
                this.dialogVisible = true;
              }
            });
          } else {
            this.resType = 'error';
            this.resTypeMsg = res.msg;
            this.loading = false;
            this.dialogVisible = true;
          }
        });
      },
      showReturnModal() {
        this.dialogNewSortVisible = true;
      },
      returnWork() {
        this.loading = true;
        returnWork(this.procinstid, this.taskid, this.returnForm.stepid, this.returnForm.msg).then(
          (res) => {
            if (res.error == '200') {
              this.resType = 'suecess';
              this.resTypeMsg = '当前流程已经驳回，等待处理人处理。';
              this.dialogVisible = true;
            } else {
              this.resType = 'error';
              this.resTypeMsg = res.msg;
              this.dialogVisible = true;
            }
            this.loading = false;
            this.dialogNewSortVisible = false;
          }
        );
      },
      actionClick(type) {
        this.loading = true;
        this.actionType = type;
        if (type == 'refuse') {
          this.dialogTitle = '拒绝';
          this.selectLabel = '拒绝：';
          this.dialogSelectVisible = true;
        } else if (type == 'setEndTask') {
          this.dialogTitle = '手动结束';
          this.selectLabel = '手动结束：';
          this.dialogSelectVisible = true;
        } else if (type == 'copyFor') {
          this.ccDefaultVisible = true;
        } else {
          getDeptUserTree().then((res) => {
            if (res.error == '200') {
              this.userList = res.result;
            }
            if (type == 'transfer') {
              this.dialogTitle = '移交';
              this.selectLabel = '移交给：';
            } else if (type == 'countersign') {
              this.dialogTitle = '加签';
              this.selectLabel = '加签至：';
            }
            this.dialogSelectVisible = true;
          });
        }
      },
      transferClick() {
        transfer(this.procinstid, this.taskid, this.actionMsg, this.selectEmp).then((res) => {
          this.loading = false;
          if (res.error == '200') {
            this.dialogSelectVisible = false;
            this.resType = 'suecess';
            this.resTypeMsg = '当前步骤已移交完成，等待移交人进行处理。';
            this.dialogVisible = true;
          } else {
            this.dialogSelectVisible = false;
            this.resType = 'error';
            this.resTypeMsg = res.msg;
            this.dialogVisible = true;
          }
        });
      },
      countersignClick() {
        countersign(this.procinstid, this.taskid, this.actionMsg, this.selectEmp).then((res) => {
          this.loading = false;
          if (res.error == '200') {
            this.resType = 'suecess';
            this.resTypeMsg = '当前步骤已完成加签，等待加签人进行审核。';
            this.dialogVisible = true;
          } else {
            this.dialogSelectVisible = false;
            this.resType = 'error';
            this.resTypeMsg = res.msg;
            this.dialogVisible = true;
          }
        });
      },
      refuseClick() {
        this.loading = true;
        refuse(this.procinstid, this.taskid, this.actionMsg).then((res) => {
          this.loading = false;
          if (res.error == '200') {
            this.resType = 'suecess';
            this.resTypeMsg = '流程流程已经被拒绝。';
            this.loading = false;
            this.dialogVisible = true;
          } else {
            this.resType = 'error';
            this.resTypeMsg = res.msg;
            this.loading = false;
            this.dialogVisible = true;
          }
        });
      },
      setEndTaskClick() {
        console.log(111111);
        setEndTask(this.procinstid, this.taskid, this.actionMsg).then((res) => {
          this.loading = false;
          if (res.error == '200') {
            this.resType = 'suecess';
            this.resTypeMsg = '当前流程已被手动结束。';
            this.loading = false;
            this.dialogVisible = true;
          } else {
            this.resType = 'error';
            this.resTypeMsg = res.msg;
            this.loading = false;
            this.dialogVisible = true;
          }
        });
      },
      cloaseDialog() {
        this.dialogVisible = false;
        this.loading = false;
        this.resType = '';
        this.resTypeMsg = '';
        this.$emit('cloaseDialog');
      },
      closeSelctUserDialog() {
        this.loading = false;
        this.dialogAllUser = false;
        this.dialogSelectUser = false;
        this.dialogSelectNode = false;
        this.approval.processInstanceId = this.procinstid;
        this.approval.taskId = this.taskid;
        this.approval.businessKey = this.bid;
      },
      handleSelectionChange(row) {
        if (this.isTreeChange == false) {
          this.selectData = row;
        } else {
          if (row.length > 0) {
            this.selectData.push(row[0]);
          }
        }
      },
      fileOnSuccess(file, fileList) {
        this.fileList = [];
        this.approval.msgFiles = file.result.fileName;
        this.approval.filePath = file.result.filePath;
        const fl = {
          name: file.result.fileName,
          response: {
            result: {
              fileName: file.result.fileName,
              filePath: file.result.filePath
            }
          }
        };
        this.fileList.push(fl);
      },
      getTime() {
        var _this = this;
        const yy = new Date().getFullYear();
        var mm =
          new Date().getMonth() < 10
            ? '0' + (new Date().getMonth() + 1)
            : new Date().getMonth() + 1;
        var dd = new Date().getDate() < 10 ? '0' + new Date().getDate() : new Date().getDate();
        const hh = new Date().getHours();
        const mf =
          new Date().getMinutes() < 10 ? '0' + new Date().getMinutes() : new Date().getMinutes();
        const ss =
          new Date().getSeconds() < 10 ? '0' + new Date().getSeconds() : new Date().getSeconds();
        return yy + '-' + mm + '-' + dd + ' ' + hh + ':' + mf + ':' + ss;
      },
      downApprovalFile(url, fileName) {
        downLoadFile(url, fileName);
      },
      filterNode(value, data) {
        if (!value) return true;
        return data.name.indexOf(value) !== -1;
      },
      handleNodeClick(data) {
        if (data.state == 1) {
          this.selectEmp = data.username;
          this.selectEmpName = data.name;
        }
      }
    },
    watch: {
      filterText(val) {
        this.$refs.tree.filter(val);
      }
    }
  };
</script>
<style>
  .el-container {
    height: 100%;
  }
</style>
