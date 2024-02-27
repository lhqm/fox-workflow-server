<template>
  <container>
    <div class="filter-container">
      <el-form
        ref="dataForm"
        :model="userData"
        label-suffix="："
        label-width="90px"
        style="margin-left: 25%;width: 50%;margin-top: 2vh;box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);padding: 15px;"
      >
        <el-form-item label="账号" >
          <el-input v-model="userData.user.username" readonly placeholder="请输入" />
        </el-form-item>
        <el-form-item label="名称" >
          <el-input v-model="userData.user.name" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="所属部门"  class="w-full px-1">
          <el-select v-model="userData.user.departid" disabled placeholder="请选择" class="w-full">
            <el-option
              v-for="item in departList"
              :key="item.id"
              :value="item.id"
              :label="item.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属角色">
          <el-select disabled
            v-model="userData.roles"
            class="filter-item"
            multiple
            filterable
            placeholder="请选择"
          >
            <el-option
              v-for="item in rolesList"
              :key="item.id"
              :value="item.id"
              :label="item.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属岗位" >
          <el-select disabled
            v-model="userData.positions"
            class="filter-item"
            multiple
            filterable
            placeholder="请选择"
          >
            <el-option
              v-for="item in positionList"
              :key="item.id"
              :value="item.id"
              :label="item.name"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="电话" >
          <el-input v-model="userData.user.tel" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="性别" >
          <el-select v-model="userData.user.sex" placeholder="请选择">
            <el-option
              v-for="item in sexSelectList"
              :key="item.value"
              :value="item.value"
              :label="item.label"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="userData.user.email" placeholder="请输入" />
        </el-form-item>
        <el-form-item>
          <el-button @click="dialogVisible = true"> 修改密码 </el-button>
          <el-button type="primary" @click="onOk()"> 确定 </el-button>
        </el-form-item>
      </el-form>

      <el-dialog
        title='修改密码'
        :visible.sync="dialogVisible"
        width="630px"
        destroy-on-close
        :close-on-click-modal="false"
        :close-on-press-escape="false"
      >
        <el-form
          :rules="rules"
          :model="formData"
          label-suffix="："
          label-width="110px"
          class="dialog-form"
        >
          <el-form-item label="原密码" prop="oldpwd">
            <el-input v-model="formData.oldpwd" placeholder="请输入类别名称" />
          </el-form-item>
          <el-form-item label="新密码" prop="newpwd">
            <el-input v-model="formData.newpwd" placeholder="请输入类别名称" />
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="dialogVisible = false"> 取消 </el-button>
          <el-button type="primary" @click="setPwd()"> 确定 </el-button>
        </div>
      </el-dialog>
    </div>
  </container>
</template>

<script>
import {sexSelectList} from '@/const';
import {getOrgData} from "@/api/orgm/orgm";
import {getUserInfo, updateUser , updatePwd} from "@/api/orgm/user";
import { getUserName} from "@/utils/auth";

export default {
  name: "userInfo",
  data() {
    return {
      dialogVisible:false,
      departList: [],
      rolesList: [],
      positionList: [],
      sexSelectList:sexSelectList,
      userData: {
        user:{},
        rolesList:[],
        positionList:[]
      },
      formData:{
        oldpwd:'',
        newpwd:'',
        username:getUserName()
      },
      rules: {
        oldpwd: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
        newpwd: [{ required: true, message: '请输入新密码', trigger: 'blur' }]
      },
    };
  },
  created() {
    this.loadData();
    this.getSelectData();
  },
  methods:{
    loadData(){
      const userName=getUserName();
      getUserInfo({username:userName}).then((res) => {
        this.userData.user = res.result.user;
        this.userData.rolesList = res.result.rolesList;
        this.userData.positionList = res.result.positionList;
      });
    },
    // 获取下拉框数据
    getSelectData() {
      getOrgData().then((res) => {
        this.departList = res.result.departList;
        this.rolesList = res.result.rolesList;
        this.positionList = res.result.positionList;
      });
    },
    onOk() {
      this.$refs.dataForm.validate(async (valid) => {
        if (valid) {
          this.userData.user.rolesList=[];
          this.userData.user.positionList=[];
          const res =await updateUser(this.userData.user);
          if (res.error === 200) {
            this.$message.success(`修改成功`);
            this.dialogVisible = false;
          } else {
            this.$message.error(`修改失败`);
          }
        }
      });
    },
    async setPwd(){
      const res =await updatePwd(this.formData);
      if (res.error === 200) {
        this.$message.success(`修改成功`);
        this.formData.newpwd='';
        this.formData.oldpwd='';
        this.dialogVisible = false;
      } else {
        this.$message.error(`修改失败`);
      }
    }
  }
}
</script>

<style scoped>

</style>
