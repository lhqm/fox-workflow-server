<template>
  <container>
    <div class="app-container">
      <el-row :gutter="20">
        <!--部门数据-->
        <el-col :span="6" :xs="24">
          <div class="head-container">
            <el-input
              v-model="deptName"
              placeholder="请输入部门名称"
              clearable
              size="small"
              prefix-icon="el-icon-search"
              style="margin-bottom: 20px"
            />
          </div>
          <div class="head-container">
            <el-tree
              :data="deptOptions"
              :props="defaultProps"
              :expand-on-click-node="false"
              :filter-node-method="filterNode"
              :render-content="renderContent"
              ref="tree"
              default-expand-all
              highlight-current
              @node-click="handleNodeClick"
            />
          </div>
        </el-col>
        <el-col :span="18" :xs="24">
          <el-form ref="form" :model="searchForm" align="right">
            <el-row>
              <el-col :span="8">
                <el-form-item label="人员账号" label-width="80px" prop="username">
                  <el-input v-model="searchForm.username" prefix-icon="el-icon-search" placeholder="请输入人员账号"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="8">
                <el-form-item label="人员姓名" label-width="80px" prop="name">
                  <el-input v-model="searchForm.name" prefix-icon="el-icon-search" placeholder="请输入人员姓名"></el-input>
                </el-form-item>
              </el-col>
              <el-col :span="8" class="btn-container">
                <el-button icon="el-icon-search" type="primary"   @click="search">查询</el-button>
                <el-button icon="el-icon-refresh" @click="search('reset')">重置</el-button>
              </el-col>
            </el-row>
          </el-form>
          <div class="btn-container">
            <el-button type="primary" plain icon="el-icon-plus"  @click="openDialog('add')"
            >新增</el-button>
          </div>
          <el-table row-key="id" v-loading="listLoading" :data="list"  style="width: 100%">
            <el-table-column v-for="item in tableColumns" :key="item.prop" v-bind="item">
              <template slot-scope="scope">
                <div v-if="item.prop === 'index'">{{ scope.$index + 1 }}</div>
                <div v-else-if="item.prop === 'sex'">{{
                    selectList.sexSelectList.find((x) => x.value == scope.row.sex)?.label
                  }}</div>
                <div v-else-if="item.prop === 'state'">
                  <el-switch v-model="scope.row.state" :active-value="1" :inactive-value="0" />
                </div>
                <div v-else-if="item.prop === 'operation'" class="operation-btn">
                  <el-button icon="el-icon-edit" type="text" @click="openDialog('edit', scope.row)">编辑</el-button>
                  <el-popconfirm title="您确定要删除吗？"
                                 @confirm="deleteById(scope.row)">
                    <el-button icon="el-icon-delete" slot="reference" type="text" class="btn-danger">删除</el-button>
                  </el-popconfirm>
                </div>
                <div v-else>{{ scope.row[item.prop] }}</div>
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
        </el-col>
      </el-row>
    </div>
    <el-drawer
      :title="titleMap[dialogStatus]"
      :visible.sync="dialogVisible"
      :close-on-press-escape="false"
      :wrapperClosable="false"
      destroy-on-close
      size="50%"
    >
      <div class="drawer-content">
        <el-form
          ref="dataForm"
          :rules="rules"
          :model="formData"
          label-suffix="："
          label-width="90px"
        >
          <el-form-item label="账号" prop="username">
            <el-input v-model="formData.username"  placeholder="请输入" />
          </el-form-item>
          <el-form-item label="名称" prop="name">
            <el-input v-model="formData.name" placeholder="请输入" />
          </el-form-item>
          <el-form-item label="所属部门" prop="departid" class="w-full px-1">
            <select-tree class="tree"
                         v-model="formData.departid"
                         :data="deptOptions"
                         placeholder="请选择所属部门">
            </select-tree>
          </el-form-item>
          <el-form-item label="所属角色" prop="roles">
            <el-select
              v-model="formData.roles"
              class="filter-item"
              multiple
              filterable
              placeholder="请选择"
            >
              <el-option
                v-for="item in selectList.rolesList"
                :key="item.id"
                :value="item.id"
                :label="item.name"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="所属岗位" prop="positions">
            <el-select
              v-model="formData.positions"
              class="filter-item"
              multiple
              filterable
              placeholder="请选择"
            >
              <el-option
                v-for="item in selectList.positionList"
                :key="item.id"
                :value="item.id"
                :label="item.name"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="state">
            <el-select v-model="formData.state" class="filter-item" placeholder="请选择">
              <el-option
                v-for="item in selectList.stateList"
                :key="item.value"
                :value="item.value"
                :label="item.label"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="电话" prop="tel">
            <el-input v-model="formData.tel" placeholder="请输入" />
          </el-form-item>
          <el-form-item label="性别" prop="sex">
            <el-select v-model="formData.sex" placeholder="请选择">
              <el-option
                v-for="item in selectList.sexSelectList"
                :key="item.value"
                :value="item.value"
                :label="item.label"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="邮箱" prop="email">
            <el-input v-model="formData.email" placeholder="请输入" />
          </el-form-item>
        </el-form>
      </div>
      <div class="drawer-footer">
        <el-button @click="dialogVisible = false"> 取消 </el-button>
        <el-button @click="setPwd"> 重置密码 </el-button>
        <el-button type="primary" @click="onOk(dialogStatus)"> 确定 </el-button>
      </div>
    </el-drawer>
  </container>
</template>

<script>
  import SelectTree from '@/components/selectTree/index.vue';
  import {getUserInfoList, getUserInfo, add, updateUser, deleteUser, resetPwd} from '@/api/orgm/user';
  import { getOrgData } from '@/api/orgm/orgm';
  import Pagination from '@/components/Pagination';
  import { titleMap, stateList, sexSelectList } from '@/const';
  import { clearObj } from '@/utils/comm';

  export default {
    name: 'user-manage',
    components: { Pagination,SelectTree },
    data() {
      return {
        titleMap,
        deptOptions:[],
        deptName:'',
        defaultProps: {
          children: "children",
          label: "name"
        },
        tableColumns: [
          {
            label: '序号',
            prop: 'index',
            width:'50px'
          },
          {
            label: '人员账号',
            prop: 'username'
          },
          {
            label: '人员姓名',
            prop: 'name'
          },
          {
            label: '性别',
            prop: 'sex'
          },
          {
            label: '手机号',
            prop: 'tel'
          },
          {
            label: '邮箱',
            prop: 'email'
          },
          {
            label: '所属部门',
            prop: 'departname'
          },
          {
            label: '状态',
            prop: 'state',
            width:'80px'
          },
          {
            label: '操作',
            prop: 'operation',
            width: '150px'
          }
        ],
        list: [],
        total: 0,
        listLoading: false,
        searchForm: {
          page: 1,
          pagesize: 10,
          username: '',
          name: '',
          departid: ''
        },
        dialogVisible: false,
        dialogStatus: 'add',
        formData: {},
        rules: {
          name: [
            { required: true, message: '请输入名称', trigger: 'blur' },
            { min: 2, message: '至少2个字符', trigger: 'blur' }
          ],
          username: [
            { required: true, message: '请输入帐号', trigger: 'blur' },
            { min: 3, message: '至少 3 个字符', trigger: 'blur' }
          ]
        },
        selectList: {
          rolesList: [],
          positionList: [],
          stateList,
          sexSelectList
        }
      };
    },
    created() {
      this.getList();
      this.getSelectData();
    },
    watch: {
      // 根据名称筛选部门树
      deptName(val) {
        this.$refs.tree.filter(val);
      }
    },
    methods: {
      // 获取
      getList() {
        this.listLoading = true;
        getUserInfoList(this.searchForm).then((res) => {
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
        }
        this.searchForm.page = 1;
        this.getList();
      },
      // 获取下拉框数据
      getSelectData() {
        getOrgData().then((res) => {
          this.deptOptions = res.result.departList;
          this.selectList.rolesList = res.result.rolesList;
          this.selectList.positionList = res.result.positionList;
        });
      },
      async openDialog(action, row) {
        clearObj(this.formData);
        this.dialogStatus = action;
        this.dialogVisible = true;
        this.$nextTick(() => {
          this.$refs.dataForm.clearValidate();
        });
        if (action !== 'add') {
          const res = await getUserInfo({ username: row.username });
          if (res.error === 200) {
            this.formData = { ...res.result.user, roles: [], positions: [] };
            for (let i in res.result.roles) {
              this.formData.roles.push(res.result.roles[i].id);
            }
            for (let i in res.result.positions) {
              this.formData.positions.push(res.result.positions[i].id);
            }
          } else {
            this.$message.error(`获取详情失败`);
          }
        }
      },
      onOk(action) {
        this.$refs.dataForm.validate(async (valid) => {
          if (valid) {
            const res =
              action === 'add' ? await add(this.formData) : await updateUser(this.formData);
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
      async setPwd(){

        const res =await resetPwd(this.formData);
        if (res.error === 200) {
          this.$message.success(`修改成功`);
          this.search('reset');
        } else {
          this.$message.error(`修改失败`);
        }
      },
      async deleteById(row) {
        const res = await deleteUser({ id: row.id });
        if (res.error === 200) {
          this.$message.success(`删除成功`);
          this.search('reset');
        } else {
          this.$message.error(`删除失败`);
        }
      },
      // 筛选节点
      filterNode(value, data) {
        if (!value) return true;
        return data.name.indexOf(value) !== -1;
      },
      // 节点单击事件
      handleNodeClick(data) {
        this.searchForm.departid = data.id;
        this.search('search');
      },
      /** 显示label **/
      renderContent(h,{node}){
        return (
          <span class="custom-tree-node">
          <span title={node.label}>{node.label}</span>
        </span>
        );
      }
    }
  };
</script>
