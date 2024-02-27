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
            <span>没有表单类型</span>
          </div>
        </el-tab-pane>
      </el-tabs>
    </div>
    <div class="theme-main-container">
      <!-- 搜索 -->
      <div class="filter-container">
        <el-form ref="form" :model="searchForm" label-width="80px" align="right">
          <el-row>
            <el-col :span="6">
              <el-form-item label="表单名称" prop="name">
                <el-input v-model="searchForm.name" placeholder="请输入表单名称"></el-input>
              </el-form-item>
            </el-col>
            <!-- <el-col :span="6">
              <el-form-item label="流程类型">
                <el-input v-model="searchForm.form_type" placeholder="请输入流程类型"></el-input>
              </el-form-item>
            </el-col> -->
            <el-col :span="18" class="btn-container">
              <el-button icon="el-icon-search" type="primary" @click="search">查询</el-button>
              <el-button icon="el-icon-refresh" @click="search('reset')">重置</el-button>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div class="btn-container">
        <el-button type="primary" icon="el-icon-plus" @click="openNewDialog">新建表单</el-button>
        <!-- <el-button style="width: 117px">批量操作</el-button> -->
        <!-- <el-button style="width: 117px">导入</el-button> -->
      </div>
      <!-- table -->
      <el-table row-key="id" stripe v-loading="listLoading" :data="list" style="width: 100%">
        <el-table-column label="序号" type="index" />
        <el-table-column prop="formSort" v-if="false" />
        <el-table-column label="表单名称" prop="name" :show-overflow-tooltip="true" />
        <el-table-column label="创建时间" prop="createTime" :show-overflow-tooltip="true" />
        <el-table-column label="类别" prop="formSortName" :show-overflow-tooltip="true" />
        <el-table-column label="操作" align="center">
          <template slot-scope="scope">
            <div class="operation-btn">
              <el-button icon="el-icon-edit-outline" type="text" @click="edit(scope.row)"
                >设计</el-button
              >
              <el-button icon="el-icon-view" type="text" @click="openView(scope.row)"
                >预览</el-button
              >
              <el-popconfirm title="您确定要删除吗？" @confirm="deleteById(scope.row)">
                <el-button icon="el-icon-delete" slot="reference" type="text" class="btn-danger"
                  >删除</el-button
                >
              </el-popconfirm>
            </div>
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
    </div>
    <el-dialog
      title="新建表单"
      v-if="dialogNewSortVisible == true"
      :close-on-click-modal="false"
      :modal-append-to-body="true"
      :visible.sync="dialogNewSortVisible"
      @close="dialogNewSortVisible = false"
      custom-class="center-dialog"
    >
      <slot name="-" style="border: none; padding: 0px">
        <el-form
          :model="ruleForm"
          ref="ruleForm"
          style="width: 95%"
          label-width="100px"
          class="demo-ruleForm"
        >
          <el-form-item label="表单ID" prop="id">
            <el-input v-model="ruleForm.id"></el-input>
          </el-form-item>
          <el-form-item label="表单名称" prop="name">
            <el-input v-model="ruleForm.name" @change="setTableName"></el-input>
          </el-form-item>
          <el-form-item label="存储表名" prop="tableName">
            <el-input v-model="ruleForm.tableName"></el-input>
          </el-form-item>
          <el-form-item label="表单分类" prop="formSort">
            <select-tree
              class="tree"
              v-model="ruleForm.formSort"
              :data="tabList"
              placeholder="请选择所属分类"
            >
            </select-tree>
          </el-form-item>
        </el-form>
      </slot>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogNewSortVisible = false"> 取消 </el-button>
        <el-button type="primary" @click="cloaseFormDialog"> 确定 </el-button>
      </div>
    </el-dialog>

    <el-drawer
      class="form-design"
      title="表单设计"
      v-if="dialogVisible == true"
      :visible.sync="dialogVisible"
      size="100%"
      :append-to-body="false"
      direction="btt"
      destroy-on-close
      @close="closeDialog"
    >
      <form-designer :formConf="formConf" :mapList="mapList" :formId="formId"></form-designer>
    </el-drawer>

    <el-drawer
      :visible.sync="viewVisible"
      title="表单预览"
      size="95%"
      class="radius"
      :append-to-body="false"
      direction="btt"
      destroy-on-close
    >
      <preview :itemList="itemList" :formConf="formConf" />
    </el-drawer>
  </container>
</template>

<script>
  import { mapActions } from 'vuex';
  import { formList, deleteForm, formSortList } from '@/api/process/processFrom';
  import Pagination from '@/components/Pagination';
  import SelectTree from '@/components/selectTree/index.vue';
  import FormDesigner from '@/components/formcomponents/formDesigner.vue';
  import formConf from '@/components/formcomponents/custom/formConf';
  import preview from '@/components/formcomponents/preview';
  import { titleMap, colorList } from '@/const';
  const pinyin = require('js-pinyin');

  export default {
    name: 'flowDefinition',
    components: { Pagination, FormDesigner, preview, SelectTree },
    data() {
      return {
        colorList,
        titleMap,
        tabList: [],
        activeName: '',
        tableColumns: [
          {
            label: '序号',
            prop: 'index',
            type: 'index'
          },
          {
            label: '表单名称',
            prop: 'name'
          },
          {
            label: '创建/修改时间',
            prop: 'createTime'
          },
          {
            label: '业务分类',
            prop: 'formSortName'
          },
          {
            label: '操作',
            prop: 'operation',
            width: '350px'
          }
        ],
        list: [],
        total: 0,
        listLoading: false,
        searchForm: {
          page: 1,
          pagesize: 10,
          name: '',
          form_type: ''
        },
        dialogVisible: false,
        viewVisible: false,
        dialogNewSortVisible: false,
        ruleForm: {
          id: '',
          name: '',
          tableName: '',
          formSort: null
        },
        itemList: [],
        formConf,
        formId: '',
        mapList: []
      };
    },
    created() {
      this.getList();
      this.getFormSortData();
    },
    methods: {
      ...mapActions('store/page', ['close']),
      // 获取
      getList() {
        this.listLoading = true;
        formList(this.searchForm).then((res) => {
          if (res.error === 200) {
            this.list = res.result.list;
            this.total = res.result.total;
          } else {
            this.$message.error(`获取列表失败`);
          }
          this.listLoading = false;
        });
      },
      search(type) {
        if (type === 'reset') {
          this.$refs.form.resetFields();
          this.searchForm.form_type = null;
        }
        this.searchForm.page = 1;
        this.getList();
      },
      onChangeType(type) {
        this.searchForm.form_type = type;
        this.search();
      },
      handleClick(tab, event) {
        this.onChangeType(tab.name);
      },
      async getFormSortData() {
        const data = (await formSortList()).result;
        if (data.length) {
          this.tabList = data;
          this.activeName = this.tabList[0].id + '';
        }
      },
      async deleteById(row) {
        const res = await deleteForm({ id: row.id });
        if (res.error === 200) {
          this.$message.success(`删除成功`);
          this.search('reset');
        } else {
          this.$message.error(`删除失败`);
        }
      },
      // 新建表单
      openNewDialog() {
        this.ruleForm.id = this.uuidv4();
        this.ruleForm.formSort = null;
        this.dialogNewSortVisible = true;
      },
      setTableName() {
        this.ruleForm.tableName = pinyin.getCamelChars(this.ruleForm.name);
      },
      // 打开表单设计器
      cloaseFormDialog() {
        if (this.ruleForm.name == '' || this.ruleForm.name == null) {
          this.$message.error('请填写必填项');
          return;
        }
        this.dialogNewSortVisible = false;
        this.dialogVisible = true;
        this.formConf = formConf;
        this.formConf.tableName = this.ruleForm.tableName;
        this.formConf.formSort = this.ruleForm.formSort;
        this.formConf.formRef = this.ruleForm.name;
        this.mapList = [];
        this.formId = this.ruleForm.id;
      },
      // 预览
      openView(row) {
        if (row.form_json == undefined) {
          this.formConf = formConf;
          this.itemList = [];
        } else {
          const mapJson = JSON.parse(row.form_json);
          this.formConf = mapJson.config;
          this.itemList = mapJson.list;
        }
        this.viewVisible = true;
      },
      // 关闭后，刷新列表
      closeDialog() {
        this.search('reset');
      },
      // 编辑
      edit(row) {
        console.log('row', row);
        if (row.form_json === undefined) {
          this.formConf = formConf;
          this.mapList = [];
          this.formId = row.id;
        } else {
          const mapJson = JSON.parse(row.form_json);
          this.formConf = mapJson.config;
          this.formId = row.id;
          this.mapList = mapJson.list;
        }
        this.dialogVisible = true;
      },
      uuidv4() {
        return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
          var r = (Math.random() * 16) | 0;
          var v = c == 'x' ? r : (r & 0x3) | 0x8;
          return v.toString(16);
        });
      },
      addForm() {
        // 新打开一个标签显示
        this.$router.push('/test');
        // 关闭该标签
        // this.close({ tagName: '/test' });
      }
    },
    mounted() {
      document.body.ondrop = (event) => {
        event.preventDefault();
        event.stopPropagation();
      };
    }
  };
</script>
<style lang="scss">
  .el-drawer__header {
    margin-bottom: 0px !important;
  }
  .v-modal {
    z-index: 2000 !important;
  }
</style>
