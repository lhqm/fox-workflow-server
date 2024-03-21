<template>
  <div class="login-box">
    <div class="title">OA自动化流程中台</div>
    <div class="login-form">
      <el-input placeholder="请输入用户名" v-model="formLogin.username">
        <icon-svg slot="prefix" name="user" />
      </el-input>
      <el-input placeholder="请输入密码" type="password" v-model="formLogin.password">
        <icon-svg slot="prefix" name="password" />
      </el-input>
      <el-checkbox v-model="formLogin.checked">下次自动登录</el-checkbox>
      <el-link style="float: right" :href="ramUrl" type="primary" size="mini">RAM登录</el-link>
      <div class="btn">
        <el-button type="primary" @click="submit" @keyup.enter="keyDown">登录</el-button>
      </div>
    </div>
    <div class="footer">
      <img src="/image/login/liucheng.png" class="login-icon" />
      <p>基于activiti 7的企业工作流管理中台</p>
    </div>
  </div>
</template>
<script>
  import { mapActions } from 'vuex';
  import { useRoute } from 'vue-router/composables';
  import { getRamUrl } from '@/api/login';

  export default {
    name: 'login-box',
    data() {
      return {
        ramUrl: '',
        formLogin: {
          username: '',
          password: '',
          checked: true
        }
      };
    },
    methods: {
      ...mapActions('store/account', ['login','codeAuth']),
      toRam() {
        // 没有初始化，就去初始化这个路径
        if (!this.ramUrl || this.ramUrl === '') {
          getRamUrl().then((res) => {
            this.ramUrl = res.result;
            console.log('收到重定向：' + this.ramUrl);
          });
        }
        // console.log(this.ramUrl);
      },
      submit() {
        if (!this.formLogin.username || !this.formLogin.password) {
          this.$message.error('用户名或密码不能为空');
          return;
        }
        this.login({
          username: this.formLogin.username,
          password: this.formLogin.password
        }).then((res) => {
          if (res != false) {
            this.$message.success('登录成功');
            // 重定向对象不存在则返回顶层路径
            this.$router.replace(this.$route.query.redirect || '/');
          }
        });
      },
      keyDown(e) {
        // 回车则执行登录方法 enter键的ASCII是13
        if (e.keyCode === 13 || e.keyCode === 100) {
          this.submit(); // 定义的登录方法
        }
      }
    },
    mounted() {
      // 绑定监听事件
      window.addEventListener('keydown', this.keyDown);
      // 获取用户的code
      const route = useRoute();
      this.toRam();
      const path = decodeURIComponent(this.$route.fullPath);
      let code;
      for (let parameter of path.split('?')) {
        // 存在code
        if (parameter.includes("code")){
          parameter.split('&').forEach((item) => {
            if (item.includes("code")){
              code = item.split("=")[1];
            }
          });
        }
      }
      if (code){
        this.codeAuth({code:code}).then((res) => {
          if (res != false) {
            this.$message.success('登录成功');
            // 重定向对象不存在则返回顶层路径
            this.$router.push('/')
          }
        });
      }
    },
    destroyed() {
      // 销毁事件
      window.removeEventListener('keydown', this.keyDown, false);
    }
  };
</script>
<style lang="scss">
  .login-box {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 700px;
    height: 420px;
    background-color: rgba(255, 255, 255, 0.8);
    padding: 14px 16px 7px 16px;
    box-shadow: #3d96f9 5px 5px 10px;
    .title {
      width: 100%;
      font-size: 23px;
      font-family: '宋体', sans-serif;
      font-weight: 500;
      color: #3b3b3b;
      text-align: center;
      line-height: 42px;
      border-bottom: 1px solid #b3c0d1;
    }
    .login-form {
      padding: 26px 10px 0 10px;
      .el-input {
        margin-bottom: 7px;
      }
      .el-input__inner {
        border: none;
        background-color: #f4f4f4 !important;
        padding-left: 42px;
        color: #606266 !important;
        height: 36px;
      }
      .el-input__prefix {
        padding-left: 10px;
        svg {
          width: 22px;
          height: 22px;
          margin-top: 9px;
        }
      }
      .el-checkbox {
        margin-top: 7px;
        .el-checkbox__label {
          width: 65px;
          font-size: 11px;
          font-family: SourceHanSansCN-Regular, SourceHanSansCN;
          font-weight: 400;
          color: #bcbcbc;
          line-height: 16px;
        }
        .el-checkbox__input.is-checked .el-checkbox__inner {
          background-color: #3370ff;
          border-color: #3370ff;
        }
      }
      .btn {
        margin-top: 5rem;
        .el-button {
          width: 100%;
          height: 36px;
          background-color: #3370ff;
          border-color: #3370ff;
        }
      }
    }
    .footer {
      margin-top: 2rem;
      text-align: center;
      .login-icon {
        width: 55px;
      }
      p {
        font-size: 11px;
        font-family: HarmonyOS_Sans;
        color: #a4a4a4;
        line-height: 30px;
        margin: 0;
      }
    }
  }
</style>
