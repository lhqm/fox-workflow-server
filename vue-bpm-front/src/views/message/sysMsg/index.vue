<template>
  <container>
    <div class="filter-container">
      <el-form
        :model="queryParams"
        ref="queryForm"
        size="small"
        :inline="true"
        v-show="showSearch"
        label-width="68px"
      >
        <el-form-item label="状态" prop="status">
          <el-select
            v-model="queryParams.status"
            placeholder="消息状态"
            clearable
            style="width: 240px"
          >
            <el-option value="0" label="未读" />
            <el-option value="1" label="已读" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>

      <el-table v-loading="loading" :data="smslist">
        <el-table-column label="序号" type="index" />
        <el-table-column prop="id" v-if="false" />
        <el-table-column prop="proce_inst_id" v-if="false" />
        <el-table-column label="状态" prop="state" :show-overflow-tooltip="true">
          <template slot-scope="scope">
            <div slot="reference" class="name-wrapper">
              <el-tag size="medium" v-if="scope.row.state == 0">未读</el-tag>
              <el-tag type="success" size="medium" v-else>已读</el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column label="标题" prop="title" :show-overflow-tooltip="true" />
        <el-table-column label="消息内容" prop="msgCont" :show-overflow-tooltip="true" />
        <el-table-column label="发送时间" prop="rdt" :show-overflow-tooltip="true" />
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <div class="operation-btn">
              <el-button icon="el-icon-view" @click="handleUpdate(scope.row)" type="text"
                >查看</el-button
              >
              <el-popconfirm title="您确定要删除吗？" @confirm="handleDelete(scope.row)">
                <el-button icon="el-icon-delete" slot="reference" type="text" class="btn-danger"
                  >删除</el-button
                >
              </el-popconfirm>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <pagination
        v-show="total > 0"
        :total="total"
        :page.sync="queryParams.pageno"
        :limit.sync="queryParams.pagesize"
        @pagination="getList"
      />
    </div>
    <!-- 添加或修改参数配置对话框 -->
    <el-dialog :title="title" :visible.sync="open" custom-class="center-dialog" append-to-body>
      <el-form ref="form" :model="form" label-width="80px">
        <el-form-item label="标题" prop="c_title">
          <el-input v-model="form.c_title" readonly />
        </el-form-item>
        <el-form-item label="发送时间" prop="c_rdt">
          <el-input v-model="form.c_rdt" readonly />
        </el-form-item>
        <el-form-item label="消息内容" prop="c_cont">
          <el-input type="textarea" :rows="6" v-model="form.c_cont" readonly />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </container>
</template>

<script>
  import reviewpage from '@/views/processHandling/reviewForm/reviewpage.vue';
  import { getSmsList, readSms, deleteSms } from '@/api/system/sms';
  import { getUserName } from '@/utils/auth';
  import Pagination from '@/components/Pagination';
  import Viewpage from '@/views/processHandling/reviewForm/viewpage.vue';

  export default {
    name: 'smslist',
    data() {
      return {
        // 遮罩层
        loading: true,
        open: false,
        processKey: '',
        mapConfig: Object,
        mapList: [],
        processName: '',
        // 选中数组
        ids: [],
        // 显示搜索条件
        showSearch: true,
        // 总条数
        total: 0,
        title: '',
        // 字典表格数据
        smslist: [],
        // 日期范围
        dateRange: [],
        // 查询参数
        queryParams: {
          pageno: 1,
          pagesize: 10,
          status: '',
          username: getUserName(),
          id: ''
        },
        // 表单参数
        form: {
          c_cont: '',
          c_title: '',
          c_rdt: ''
        }
      };
    },
    components: {
      Viewpage,
      Pagination,
      reviewpage
    },
    created() {
      this.getList();
    },
    methods: {
      cloaseDialog() {
        this.dialogVisible = false;
      },
      cloaseView() {
        this.dialogView = false;
      },
      /** 查询字典类型列表 */
      getList() {
        this.loading = false;
        this.smslist = [];
        getSmsList(this.queryParams).then((res) => {
          if (res.error == '200') {
            this.smslist = res.result.list;
            this.total = res.result.total;
          } else {
            this.$message.error('获取失败');
          }
          this.loading = false;
        });
      },
      // 取消按钮
      cancel() {
        this.open = false;
      },
      /** 搜索按钮操作 */
      handleQuery() {
        this.queryParams.pageNum = 1;
        this.getList();
      },
      /** 重置按钮操作 */
      resetQuery() {
        this.dateRange = [];
        this.$refs.queryForm.resetFields();
        this.handleQuery();
      },
      /** 修改按钮操作 */
      handleUpdate(row) {
        this.open = true;
        this.form.c_title = row.title;
        this.form.c_cont = row.msgCont;
        this.form.c_rdt = row.rdt;
        readSms({ id: row.id }).then((res) => {
          if (res.error == '200') {
            setTimeout(() => {
              this.getList()();
            }, 1000);
          } else {
            this.$message.error('设置失败');
          }
        });
      },
      submitForm() {
        this.open = false;
        this.reset();
      },
      /** 删除按钮操作 */
      handleDelete(row) {
        this.$confirm('确定要删除?', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
          .then(() => {
            deleteSms({ id: row.id }).then((res) => {
              if (res.error == '200') {
                this.$message.success('删除成功');
                setTimeout(() => {
                  this.getList()();
                }, 1000);
              } else {
                this.$message.error('删除失败');
              }
            });
          })
          .catch(() => {});
      }
    }
  };
</script>
