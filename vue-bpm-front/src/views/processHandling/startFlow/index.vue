<template>
  <container type="ghost">
    <div class="theme-tab-container">
      <div class="card-title">
        <span class="name">常用流程</span>
        <el-input v-model="keyword" placeholder="输入关键词" @input="search" />
      </div>
      <div class="card-box">
        <div
          v-for="(x, i) in commonList"
          :key="x.id"
          :style="{ background: colorList[i % colorList.length] }"
          class="card-content"
          @click="startTask(x.processKey, x.name_)"
          >{{ x.name_ }}</div
        >
        <div
          v-if="!commonList.length"
          class="card-box-empty"
          flex="dir:top main:center cross:center"
        >
          <icon name="inbox"></icon>
          <span>没有表单类型</span>
        </div>
      </div>
    </div>
    <div class="all-flow-container">
      <div class="custom-tab">
        <div class="title">全部流程</div>
        <ul class="custom-tab-content">
          <li
            class="custom-tab-item"
            :class="{ 'is-active': activeId === -1 }"
            @click="onClickTab(-1)"
            >全部</li
          >
          <li
            :id="item.id"
            class="custom-tab-item"
            :class="{ 'is-active': activeId === item.id }"
            v-for="item in allList"
            :key="item.id"
            @click="onClickTab(item.id)"
            >{{ item.name }}</li
          >
        </ul>
      </div>
      <div class="custom-tab-container">
        <div v-for="(item, index) in allList" :key="index" class="custom-tab-block">
          <template v-if="activeId === item.id || activeId === -1">
            <p class="title">{{ item.name }}</p>
            <el-row :gutter="20" class="custom-tab-block-content">
              <el-col
                :sm="8"
                :lg="6"
                :xl="4"
                v-for="(f, findex) in getFlowList(item.id)"
                :key="f.id_"
              >
                <div class="custom-tab-block-item" @click="startTask(f.processKey, f.name_)">
                  <div
                    class="img"
                    :style="{ background: colorList[index % colorList.length] }"
                  ></div>
                  <span class="name">{{ f.name_ }}</span>
                </div>
              </el-col>
            </el-row>

            <div v-for="(x, i) in item.children" :key="i">
              <p class="subtitle">{{ x.name }}</p>
              <el-row :gutter="20" class="custom-tab-block-content">
                <el-col
                  :sm="8"
                  :lg="6"
                  :xl="4"
                  v-for="(f, findex) in getFlowList(x.id)"
                  :key="f.id_"
                >
                  <div class="custom-tab-block-item" @click="startTask(f.processKey, f.name_)">
                    <div
                      class="img"
                      :style="{ background: colorList[(i+findex) % colorList.length] }"
                    ></div>
                    <span class="name">{{ f.name_ }}</span>
                  </div>
                </el-col>
              </el-row>
            </div>
          </template>
        </div>
      </div>
      <el-drawer
        v-if="dialogVisible == true"
        :visible.sync="dialogVisible"
        :with-header="false"
        title="发起申请"
        size="95%"
        class="radius"
        direction="btt"
        :append-to-body="false"
        :wrapperClosable="false"
        destroy-on-close
      >
        <reviewpage
          v-if="loading"
          @cloaseDialog="cloaseDialog"
          :procinstid="procinstid"
          :taskid="taskid"
          :dataJson="dataJson"
          :bid="bid"
          :processKey="processKey"
          :processName="processName"
          :mapList="mapList"
          :mapConfig="mapConfig"
          :form_type="form_type"
          :form_url="form_url"
        >
        </reviewpage>
      </el-drawer>
    </div>
  </container>
</template>
<script>
  import { colorList } from '@/const';
  import { getStartList, getHisFormJson } from '@/api/task/task';
  import { flowSortTree } from '@/api/process/flowsort';
  import reviewpage from '../reviewForm/reviewpage';

  export default {
    name: 'startFlow',
    data() {
      return {
        // 遮罩层
        loading: false,
        colorList,
        // 流程list
        commonList: [],
        processList:[],
        // 用于搜索
        commonListCopy: [],
        // 全部流程类别
        allList: [],
        keyword: '',
        activeId: -1,
        processKey: '',
        processName: '',
        mapConfig: Object,
        dataJson: Object,
        mapList: [],
        taskid: '',
        procinstid: '',
        form_type:'',
        form_url:'',
        bid: '',
        dialogVisible: false
      };
    },
    components: {
      reviewpage
    },
    created() {
      this.getCommonList();
      this.getAllList();
    },
    methods: {
      // 获取常用流程
      getCommonList() {
        getStartList({ keyword: this.keyword }).then((res) => {
          if (res.error === 200) {
            this.commonList = res.result.commProceList;
            this.processList=res.result.flowList;
            this.commonListCopy = res.result.commProceList;
          } else {
            this.$message.error(`获取数据失败`);
          }
        });
      },
      //获取所有的流程类别
      getAllList() {
        flowSortTree().then((res) => {
          if (res.error === 200) {
            this.allList = res.result;
          } else {
            this.$message.error(`获取数据失败`);
          }
        });
      },
      //发起流程
      startTask(procKey, proceName) {
        if (procKey == '') {
          this.$message.error('没有找到流程processKey，请联系管理员');
          return;
        }
        getHisFormJson(procKey, '')
          .then((res) => {
            if (res.error == '200') {
              this.processKey = procKey;
              this.processName = proceName;
              this.bid = '';
              this.procinstid = '';
              this.taskid = '';
              this.dataJson = {};
              this.loading = true;
              if (res.result == '' || res.result == undefined) {
                this.mapConfig = {};
                this.mapList = [];
              } else {
                const mapJson = JSON.parse(res.result.mapJson);
                this.mapConfig = mapJson.config;
                this.mapList = mapJson.list;
                this.form_url=res.result.form_url;
                this.form_type=res.result.form_type;
              }
              this.dialogVisible = true;
            } else {
              this.loading = false;
              this.$message.warning(res.result);
            }
          })
          .catch((error) => {});
      },
      randomColor() {
        return colorList[Math.floor(Math.random() * colorList.length)];
      },
      search() {
        this.commonList = this.commonListCopy.filter((x) => x.name_.includes(this.keyword));
      },
      //获取流程类别下的流程
      getFlowList(sortId) {
        const flowRows = this.processList.filter((item) => item.id == sortId);
        return flowRows[0]?.flowList || [];
      },
      // 点击左侧tab
      onClickTab(id) {
        this.activeId = id;
      },
      cloaseDialog() {
        this.dialogVisible = false;
      }
    }
  };
</script>
<style lang="scss">
  .all-flow-container {
    display: flex;
    padding: 15px 20px;
    min-height: calc(100% - 180px);
    .custom-tab {
      flex-grow: 0;
      margin-right: 35px;
      .title {
        font-size: 16px;
        font-weight: 500;
      }
      .custom-tab-content {
        list-style-type: none;
        padding: 0;
        .custom-tab-item {
          min-width: 100px;
          height: 43px;
          line-height: 43px;
          font-size: 14px;
          text-align: center;
          border-bottom: 1px solid #e8e8e8;
          cursor: pointer;
          &.is-active {
            color: #1890ff;
            border-bottom: 2px solid #1890ff;
          }
        }
      }
    }
    .custom-tab-container {
      flex-grow: 1;
      overflow-y: auto;
      .custom-tab-block {
        box-sizing: border-box;

        .title {
          font-size: 16px;
          font-weight: 500;
          margin: 10px 0 20px 0;
          text-decoration-line: underline;
          text-decoration-thickness: initial;
          text-decoration-style: double;
          text-decoration-color: #1890ff;
        }
        .subtitle {
          font-size: 16px;
          font-weight: 500;
        }
        .custom-tab-block-content {
          box-sizing: border-box;
          width: 100%;
          .custom-tab-block-item {
            box-sizing: border-box;
            height: 70px;
            line-height: 70px;
            text-align: left;
            border-radius: 6px;
            border: 1px solid #dee0e3;
            padding: 0 20px;
            margin-bottom: 20px;
            font-size: 15px;
            font-weight: 500;
            cursor: pointer;
            display: flex;
            align-items: center;

            &:hover {
              border-color: #1890ff;
              color: #1890ff;
            }
            .img {
              display: inline-block;
              width: 34px;
              height: 34px;
              line-height: 34px;
              text-align: center;
              border-radius: 4px;
              vertical-align: middle;
              margin-right: 10px;
              color: #ffffff;
            }
            .name {
              flex: 1;
              display: inline-block;
              line-height: 18px;
              word-wrap: break-word;
              word-break: break-all;
            }
          }
        }
      }
    }
  }
</style>
