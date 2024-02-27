<template>
  <container>
    <!-- 搜索 -->
    <div class="btn-container">
      <el-button type="primary" icon="el-icon-plus" @click="openDialog('add')">新增类别</el-button>
      <!-- <el-button style="width: 117px">批量操作</el-button> -->
    </div>
    <!-- table -->
    <el-table
      row-key="id"
      v-loading="listLoading"
      :data="list"
      :indent="36"
      default-expand-all
      :tree-props="{ children: 'children' }"
      style="width: 100%"
    >
      <el-table-column label="序号" align="center" type="index" />
      <el-table-column prop="parentNo" v-if="false" />
      <el-table-column label="类型名称" align="center" prop="name" :show-overflow-tooltip="true" />
      <el-table-column label="类型编号" align="center" prop="id" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center">
        <template slot-scope="scope">
          <div class="operation-btn">
            <el-button type="text" icon="el-icon-edit" @click="openDialog('edit', scope.row)"
              >编辑</el-button
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

    <el-dialog
      :title="titleMap[dialogStatus]"
      :visible.sync="dialogVisible"
      width="630px"
      destroy-on-close
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <el-form
        ref="dataForm"
        :rules="rules"
        :model="formData"
        label-suffix="："
        label-width="110px"
        class="dialog-form"
      >
        <el-form-item label="类别名称" prop="name">
          <el-input v-model="formData.name" placeholder="请输入类别名称" />
        </el-form-item>
        <el-form-item label="所属上级" prop="parentNo">
          <select-tree
            class="tree"
            v-model="formData.parentNo"
            :data="treeData"
            placeholder="请选择所属上级"
          ></select-tree>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false"> 取消 </el-button>
        <el-button type="primary" @click="onOk(dialogStatus)"> 确定 </el-button>
      </div>
    </el-dialog>
  </container>
</template>

<script>
  import {
    formSortList,
    addFormSort,
    updateFormSort,
    deleteFormSort
  } from '@/api/process/processFrom';
  import Pagination from '@/components/Pagination';
  import { titleMap } from '@/const';
  import SelectTree from '@/components/selectTree/index.vue';
  import { clearObj } from '@/utils/comm';

  export default {
    name: 'formSort',
    components: { Pagination, SelectTree },
    data() {
      return {
        titleMap,
        list: [],
        total: 0,
        listLoading: false,
        searchForm: {
          page: 1,
          pagesize: 10,
          name: ''
        },
        dialogVisible: false,
        dialogStatus: 'add',
        formData: {
          id: '',
          name: '',
          parentNo: null
        },
        rules: {
          name: [{ required: true, message: '请输入名称', trigger: 'blur' }]
        },
        treeData: []
      };
    },
    created() {
      this.getList();
    },
    methods: {
      // 获取
      getList() {
        this.listLoading = true;
        formSortList().then((res) => {
          if (res.error === 200) {
            this.list = res.result;
          } else {
            this.$message.error(`获取列表失败`);
          }
          this.listLoading = false;
        });
      },
      search(type) {
        this.searchForm.page = 1;
        this.getList();
      },
      // 获取下拉框数据
      getSelectData() {
        this.treeData = this.list;
      },
      openDialog(action, row) {
        this.getSelectData();
        clearObj(this.formData);
        this.dialogStatus = action;
        this.dialogVisible = true;
        this.$nextTick(() => {
          this.$refs.dataForm.clearValidate();
          if (action !== 'add') {
            this.formData.id = row.id;
            this.formData.name = row.name;
            if (row.parentNo == '0') this.formData.parentNo = null;
            else this.formData.parentNo = row.parentNo;
          }
        });
      },
      onOk(action) {
        if (
          this.formData.parentNo == '' ||
          this.formData.parentNo == null ||
          this.formData.parentNo == undefined
        ) {
          this.formData.parentNo = 0;
        }
        this.$refs.dataForm.validate(async (valid) => {
          if (valid) {
            const res =
              action === 'add'
                ? await addFormSort(this.formData)
                : await updateFormSort(this.formData);
            if (res.error === 200) {
              this.$message.success(`${titleMap[action]}成功`);
              this.dialogVisible = false;
              this.search();
            } else {
              this.$message.error(`${titleMap[action]}失败`);
            }
          }
        });
      },
      async deleteById(row) {
        const res = await deleteFormSort(row);
        if (res.error === 200) {
          this.$message.success(`删除成功`);
          this.search();
        } else {
          this.$message.error(`删除失败`);
        }
      }
    }
  };
</script>
