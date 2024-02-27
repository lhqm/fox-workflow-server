<template>
  <el-tabs :tab-position="tabPosition" style="overflow-y: hidden; height: 100%">
    <el-tab-pane label="业务表单">
      <el-container>
        <el-header style="text-align: right; height: auto">
          <el-card>
            <el-tag type="success" size="large" effect="dark" style="float: left">业务表单</el-tag>
            <el-button type="danger" @click="cloaseDialog">关闭</el-button>
          </el-card>
        </el-header>
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
            <el-card style="background-color: cornflowerblue; color: white"> 审批轨迹图 </el-card>
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
  import { downLoadFile, getApprovalTracks } from '@/api/reviewpage/reviewpage';
  import { getToken } from '@/utils/auth';
  export default {
    name: 'viewpage',
    data() {
      return {
        itemList: this.mapList,
        formConf: this.mapConfig,
        itemDataJson: this.dataJson,
        tabPosition: 'left',
        trackImg: '',
        track: []
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
      },
      downApprovalFile(url, fileName) {
        downLoadFile(url, fileName);
      },
      cloaseDialog() {
        this.$emit('cloaseView');
      }
    }
  };
</script>
<style>
  .el-container {
    height: 100%;
  }
</style>
