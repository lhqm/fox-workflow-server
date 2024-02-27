<template>
  <container type="ghost">
    <div class="theme-tab-container">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch">
        <el-form-item label="菜单名称" prop="menuName">
          <el-input
            v-model="queryParams.menuName"
            placeholder="请输入菜单名称"
            clearable
            @keyup.enter.native="handleQuery"
          />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="queryParams.status" placeholder="菜单状态" clearable>
            <el-option
              v-for="dict in optionStatus"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
          <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    <div class="theme-main-container">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="primary"
            plain
            icon="el-icon-sort"
            size="mini"
            @click="toggleExpandAll"
          >展开/折叠</el-button>
        </el-col>
      </el-row>

      <el-table
        v-if="refreshTable"
        :data="menuList"
        row-key="menuId"
        :default-expand-all="isExpandAll"
        :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
      >
        <el-table-column prop="menuName" label="菜单名称" :show-overflow-tooltip="true" width="160"></el-table-column>
        <el-table-column prop="icon" label="图标" align="center" width="100">
          <template slot-scope="scope">
            <icon-svg :name="scope.row.icon" />
          </template>
        </el-table-column>
        <el-table-column prop="orderNum" label="排序" width="60"></el-table-column>
        <el-table-column prop="path" label="路由地址" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="component" label="组件路径" :show-overflow-tooltip="true"></el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template slot-scope="scope">
            <dict-tag :options="optionStatus" :value="scope.row.status"/>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" align="center" prop="createTime">
          <template slot-scope="scope">
            <span>{{ parseTime2(scope.row.createTime) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
          <template slot-scope="scope">
            <div class="operation-btn">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
              >修改</el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-plus"
                @click="handleAdd(scope.row)"
              >新增</el-button>
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                class="btn-danger"
                @click="handleDelete(scope.row)"
              >删除</el-button>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="menuId"  v-if="false" label="菜单编号"></el-table-column>
        <el-table-column prop="parentId"  v-if="false" label="上级菜单编号"></el-table-column>
        <el-table-column prop="isFrame"  v-if="false" label="是否为外链"></el-table-column>
        <el-table-column prop="menuType"  v-if="false" label="菜单类型"></el-table-column>
        <el-table-column prop="visible"  v-if="false" label="显示状态"></el-table-column>
        <el-table-column prop="query"  v-if="false" label="路由参数"></el-table-column>
        <el-table-column prop="remark"  v-if="false" label="备注"></el-table-column>
      </el-table>

      <!-- 添加或修改菜单对话框 -->
      <el-dialog :title="title" :visible.sync="open"  append-to-body>
        <el-form style="width: 98%" ref="dialogForm" :model="form" :rules="rules" label-width="100px">
          <el-row>
            <el-col :span="24">
              <el-form-item label="上级菜单">
                <treeselect
                  v-model="form.parentId"
                  :options="menuOptions"
                  :normalizer="normalizer"
                  placeholder="选择上级菜单"
                />
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="菜单类型" prop="menuType">
                <el-radio-group v-model="form.menuType">
                  <el-radio label="M">目录</el-radio>
                  <el-radio label="C">菜单</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="菜单图标" prop="icon">
                <el-popover
                  placement="bottom-start"
                  width="460"
                  trigger="click"
                  @show="$refs['iconSelect'].reset()"
                >
                  <iconSelect ref="iconSelect" @selected="selected" />
                  <el-input slot="reference" v-model="form.icon" placeholder="点击选择图标" readonly>
                    <icon-svg
                      v-if="form.icon"
                      slot="prefix"
                      :name="form.icon"
                      class="el-input__icon"
                      style="height: 32px;width: 16px;"
                    />
                    <i v-else slot="prefix" class="el-icon-search el-input__icon" />
                  </el-input>
                </el-popover>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="菜单名称" prop="menuName">
                <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="显示排序" prop="orderNum">
                <el-input-number v-model="form.orderNum" controls-position="right" :min="0" />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item>
                <span slot="label">
                  <el-tooltip content="选择是外链则路由地址需要以`http(s)://`开头" placement="top">
                  <i class="el-icon-question"></i>
                  </el-tooltip>
                  是否外链
                </span>
                <el-radio-group v-model="form.isFrame">
                  <el-radio label="1">是</el-radio>
                  <el-radio label="0">否</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item prop="path">
                <span slot="label">
                  <el-tooltip content="访问的路由地址，如：`user`，如外网地址需内链访问则以`http(s)://`开头" placement="top">
                  <i class="el-icon-question"></i>
                  </el-tooltip>
                  路由地址
                </span>
                <el-input v-model="form.path" placeholder="请输入路由地址" />
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="form.menuType == 'C'">
              <el-form-item prop="component">
                <span slot="label">
                  <el-tooltip content="访问的组件路径，如：`system/user/index`，默认在`views`目录下" placement="top">
                  <i class="el-icon-question"></i>
                  </el-tooltip>
                  组件路径
                </span>
                <el-input v-model="form.component" placeholder="请输入组件路径" />
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="form.menuType != 'M'">
              <el-form-item>
                <el-input v-model="form.perms" placeholder="请输入权限标识" maxlength="100" />
                <span slot="label">
<!--                  <el-tooltip content="控制器中定义的权限字符，如：@PreAuthorize(`@ss.hasPermi('system:user:list')`)" placement="top">-->
<!--                  <i class="el-icon-question"></i>-->
<!--                  </el-tooltip>-->
                  权限字符
                </span>
              </el-form-item>
            </el-col>
            <el-col :span="12" v-if="form.menuType == 'C'">
              <el-form-item>
                <el-input v-model="form.query" placeholder="请输入路由参数" maxlength="255" />
                <span slot="label">
                  <el-tooltip content='访问路由的默认传递参数，如：`{"id": 1, "name": "admin"}`' placement="top">
                  <i class="el-icon-question"></i>
                  </el-tooltip>
                  路由参数
                </span>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item>
                <span slot="label">
                  <el-tooltip content="选择隐藏则路由将不会出现在侧边栏，但仍然可以访问" placement="top">
                  <i class="el-icon-question"></i>
                  </el-tooltip>
                  显示状态
                </span>
                <el-radio-group v-model="form.visible">
                  <el-radio
                    v-for="dict in optionVisible"
                    :key="dict.value"
                    :label="dict.value"
                  >{{dict.label}}</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="12" >
              <el-form-item>
                <span slot="label">
                  <el-tooltip content="选择停用则路由将不会出现在侧边栏，也不能被访问" placement="top">
                  <i class="el-icon-question"></i>
                  </el-tooltip>
                  菜单状态
                </span>
                <el-radio-group v-model="form.status">
                  <el-radio
                    v-for="dict in optionStatus"
                    :key="dict.value"
                    :label="dict.value"
                  >{{dict.label}}</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-col>
            <el-col :span="24">
              <el-form-item label="菜单说明" prop="remark">
                <el-input v-model="form.remark" type="textarea" placeholder=""></el-input>
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </el-dialog>
    </div>
  </container>
</template>

<script>
import {addMenu, updateMenu, deleteMenu, getMenuList} from "@/api/system/menu";
import {handleTree,parseTime} from "@/utils/comm";
import dictTag from "@/components/dictTag";
import iconSvg from "@/components/icon-svg";
import Treeselect from "@riophae/vue-treeselect";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import iconSelect from "@/components/iconSelect";

export default {
  name: "menuList",
  components:{dictTag,iconSvg,Treeselect,iconSelect},
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 菜单表格树数据
      menuList: [],
      // 菜单树选项
      menuOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否展开，默认全部折叠
      isExpandAll: false,
      // 重新渲染表格状态
      refreshTable: true,
      // 查询参数
      queryParams: {
        menuName: undefined,
        visible: undefined
      },
      optionStatus:[
        {
          label:"停用",
          value:"0",
          listClass:"danger"
        },
        {
          label:"正常",
          value:"1",
          listClass: "primary"
        }
      ],
      optionVisible:[
        {
          label:"隐藏",
          value:"0"
        },
        {
          label:"正常",
          value:"1"
        }
      ],
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        menuName: [
          { required: true, message: "菜单名称不能为空", trigger: "blur" }
        ],
        orderNum: [
          { required: true, message: "菜单顺序不能为空", trigger: "blur" }
        ],
        path: [
          { required: true, message: "路由地址不能为空", trigger: "blur" }
        ]
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    // 选择图标
    selected(name) {
      this.form.icon = name;
    },
    /** 查询菜单列表 */
    getList() {
      getMenuList(this.queryParams).then(response => {
        this.menuList = handleTree(response.result, "menuId");
      });
    },
    /** 转换菜单数据结构 */
    normalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children;
      }
      return {
        id: node.menuId,
        label: node.menuName,
        children: node.children
      };
    },
    /** 查询菜单下拉树结构 */
    getTreeselect() {
      getMenuList({menuName:"",visible:undefined}).then(response => {
        this.menuOptions = [];
        const menu = { menuId: 0, menuName: '主目录', children: [] };
        menu.children = handleTree(response.result, "menuId");
        this.menuOptions.push(menu);
      });
    },
    parseTime2(data){
      return parseTime(data);
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        menuId: undefined,
        parentId: 0,
        menuName: undefined,
        icon: undefined,
        menuType: "M",
        orderNum: undefined,
        isFrame: "0",
        visible: "0",
        status: "0"
      };
      //this.$refs.dialogForm.resetFields();
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.$refs.queryForm.resetFields();
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd(row) {
      this.reset();
      this.getTreeselect();
      if (row != null && row.menuId) {
        this.form.parentId = row.menuId;
      } else {
        this.form.parentId = 0;
      }
      this.open = true;
      this.title = "添加菜单";
    },
    /** 展开/折叠操作 */
    toggleExpandAll() {
      this.refreshTable = false;
      this.isExpandAll = !this.isExpandAll;
      this.$nextTick(() => {
        this.refreshTable = true;
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.getTreeselect();
      this.form = row;
      this.open = true;
      this.title = "修改菜单";
    },
    /** 提交按钮 */
    submitForm: function() {
      this.$refs["dialogForm"].validate(valid => {
        if (valid) {
          if (this.form.menuId != undefined) {
            updateMenu(this.form).then(response => {
              this.$message.success("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addMenu(this.form).then(response => {
              this.$message.success("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      this.$confirm('是否确认删除名称为"' + row.menuName + '"的数据项？').then(function() {
        return deleteMenu(row);
      }).then(() => {
        this.getList();
        this.$message.success("删除成功");
      }).catch(() => {});
    }
  }
};
</script>
