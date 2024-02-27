<template>
  <container type="ghost">
    <div class="theme-tab-container">
      <el-tabs v-model="activeName" @tab-click="handleClick">
        <el-tab-pane v-for="item in tabList" :key="item.id" :name="item.id + ''" :label="item.name">
          <div v-if="item.children && item.children.length" class="card-box">
            <div
              v-for="(x, i) in item.children"
              :key="x.id"
              :style="{ background: colorList[i % colorList.length] }"
              class="card-content"
              :class="{ 'is-active': searchForm.form_type == x.id }"
              @click="onChangeType(x.id)"
              >{{ x.name }}</div
            >
          </div>
          <div v-else class="card-box-empty" flex="dir:top main:center cross:center">
            <icon name="inbox"></icon>
            <span>没有流程类型</span>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <div class="theme-main-container">
      <!-- 搜索 -->
      <div class="filter-container">
        <el-form ref="form" :model="searchForm" label-width="80px" align="right">
          <el-row>
            <el-col :span="18" class="btn-container">
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="btn-container">
        <el-button type="primary" icon="el-icon-plus" size="mini" @click="openNewDialog">新建流程</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="search('reset')">重置</el-button>
      </div>
      <!-- table -->
      <el-table row-key="id" stripe v-loading="listLoading" :data="list" style="width: 100%">
        <el-table-column v-for="item in tableColumns" :key="item.prop" v-bind="item" >
          <template slot-scope="scope">
            <div v-if="item.prop === 'index'">{{ scope.$index + 1 }}</div>
            <div v-else-if="item.prop === 'operation'" class="operation-btn" style="text-align: center">
              <el-button icon="el-icon-setting"
                type="text"
                @click="editFlowDialog(scope.row.deploymentID, scope.row.name, scope.row.procKey)"
                >设计流程</el-button
              >
              <el-popconfirm title="您确定要删除吗？" @confirm="deleteById(scope.row)">
                <el-button icon="el-icon-delete" slot="reference" type="text" class="btn-danger">删除</el-button>
              </el-popconfirm>
            </div>
            <span v-else>{{ scope.row[item.prop] }}</span>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="searchForm.page"
        :limit.sync="searchForm.pagesize"
        @pagination="getList"
      />

      <el-drawer
        class="flow-design"
        title="流程设计"
        v-if="dialogVisible == true"
        :visible.sync="dialogVisible"
        size="100%"
        :append-to-body="false"
        direction="btt"
        destroy-on-close
        @close="cloaseDialog"
      >
        <vue-Bpmn
          :processId="processId"
          :deployMName="deployMName"
          :deployMId="deployMId"
          product="activiti"
        ></vue-Bpmn>
      </el-drawer>
    </div>
  </container>
</template>

<script>
  import VueBpmn from '../components/processDesign';
  import { flowSortTree } from '@/api/process/flowsort';
  import {
    processList,
    delDefinition
  } from '@/api/process/flowdesign';
  import Pagination from '@/components/Pagination';
  import { colorList } from '@/const';
  import { tableColumns } from './const';
  import SelectTree from '@/components/selectTree/index.vue';
  export default {
    name: 'flowDefinition',
    components: { Pagination, VueBpmn, SelectTree },
    data() {
      return {
        //流程设计器是否显示
        dialogVisible: false,
        //流程id
        deployMId: '',
        //流程名称
        deployMName: '',
        //实例id
        processId: '',
        //颜色
        colorList,
        //常用流程分类
        tabList: [],
        //tab活动名称
        activeName: '',
        //table行
        tableColumns,
        //流程列表
        list: [],
        total: 0,
        listLoading: false,
        //查询参数
        searchForm: {
          page: 1,
          pagesize: 10,
          name: '',
          sortid: ''
        }
      };
    },
    created() {
      this.getList();
      this.flowSortTree();
    },
    methods: {
      // 获取流程列表
      getList() {
        this.listLoading = true;
        processList(this.searchForm).then((res) => {
          if (res.error === 200) {
            this.list = res.result.list;
            this.total = res.result.total;
          } else {
            this.$message.error(`获取列表失败`);
          }
          this.listLoading = false;
        });
      },
      //获取流程类别
      flowSortTree() {
        flowSortTree().then((res) => {
          if (res.error == '200') {
            this.sortdata = res.result;
            this.tabList = res.result;
            this.activeName = this.tabList[0].id + '';
          }
        });
      },
      // 新增流程
      openNewDialog() {
        this.dialogVisible = true;
        this.deployMId = '';
        this.deployMName = '';
        this.processId = '';
      },
      //设计流程
      editFlowDialog(deploymentId, deploymentName, processId) {
        this.dialogVisible = true;
        this.deployMId = deploymentId;
        this.deployMName = deploymentName;
        this.processId = processId;
      },
      //关闭事件
      cloaseDialog() {
        this.deploy();
      },
      //刷新事件
      deploy() {
        this.getList();
      },
      handleClick(tab, event){
        this.onChangeType(tab.name);
      },
      //查询
      search(type) {
        if (type === 'reset') {
          this.$refs.form.resetFields();
          this.searchForm.sortid = null;
        }
        this.searchForm.page = 1;
        this.getList();
      },
      //点击流程类型查询
      onChangeType(type) {
        this.searchForm.sortid = type;
        this.search();
      },
      //删除流程实例
      async deleteById(row) {
        const res = await delDefinition({ depID: row.deploymentID });
        if (res.error === 200) {
          this.$message.success(`删除成功`);
          this.search('reset');
        } else {
          this.$message.error(`删除失败`);
        }
      }
    }
  };
</script>
