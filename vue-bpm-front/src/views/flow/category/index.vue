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
      <el-table-column prop="parentId" v-if="false" />
      <el-table-column
        label="类型名称"
        align="center"
        prop="name"
        :show-overflow-tooltip="true"
      />
      <el-table-column
        label="类型编号"
        align="center"
        prop="id"
        :show-overflow-tooltip="true"
      />
      <el-table-column label="操作" align="center" >
        <template slot-scope="scope">
          <div  class="operation-btn">
            <el-button type="text" icon="el-icon-edit" @click="openDialog('edit', scope.row)">编辑</el-button>
            <el-popconfirm title="您确定要删除吗？" @confirm="deleteById(scope.row)">
              <el-button icon="el-icon-delete" slot="reference" type="text" class="btn-danger">删除</el-button>
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
        <el-form-item label="所属上级" prop="parentId">
          <select-tree
            class="tree"
            v-model="formData.parentId"
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
  import { flowSortTree, deleteFlowSort, saveFlowSort, addFlowSort } from '@/api/process/flowsort';
  import Pagination from '@/components/Pagination';
  import { titleMap } from '@/const';
  import SelectTree from '@/components/selectTree/index.vue';
  import { clearObj } from '@/utils/comm';

  export default {
    name: 'user-manage',
    components: { Pagination, SelectTree },
    data() {
      return {
        titleMap,
        tableColumns: [
          {
            label: '序号',
            prop: 'index',
            type: 'index'
          },
          {
            label: '类型名称',
            prop: 'name',
            align: 'left'
          },
          {
            label: '类型编号',
            prop: 'id'
          },
          {
            label: '操作',
            prop: 'operation',
            width: '200px'
          }
        ],
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
          id:'',
          name: '',
          parentId: null
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
        flowSortTree(this.searchForm).then((res) => {
          if (res.error === 200) {
            this.list = res.result;
            this.total = res.result.length;
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
      getFlowSortData() {
        flowSortTree().then((res) => {
          if (res.error === 200) {
            const data = res.result;
            data.forEach((x) => {
              x.children.forEach((y) => (y.disabled = true));
            });
            this.treeData = data;
          } else {
            this.$message.error('获取失败');
          }
        });
      },
      openDialog(action, row) {
        this.getFlowSortData();
        clearObj(this.formData);
        this.dialogStatus = action;
        this.dialogVisible = true;
        this.$nextTick(() => {
          this.$refs.dataForm.clearValidate();
          if (action !== 'add') {
            this.formData.id = row.id;
            this.formData.name = row.name;
            if(row.parentId=="0")
              this.formData.parentId = null;
            else
              this.formData.parentId = row.parentId;
          }
        });
      },
      onOk(action) {
        if(this.formData.parentId==''||this.formData.parentId==null||this.formData.parentId==undefined){
          this.formData.parentId=0;
        }
        this.$refs.dataForm.validate(async (valid) => {
          if (valid) {
            const res =
              action === 'add'
                ? await addFlowSort(this.formData)
                : await saveFlowSort(this.formData);
            if (res.error === 200) {
              this.$message.success(`${titleMap[action]}成功`);
              this.dialogVisible = false;
              this.search('reset');
            } else {
              this.$message.error(`${titleMap[action]}失败`);
            }
          }
        });
      },
      async deleteById(row) {
        const res = await deleteFlowSort(row);
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
