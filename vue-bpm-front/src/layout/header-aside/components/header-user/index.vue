<template>
  <el-dropdown size="small" class="Z6-mr">
    <div>
      <icon-svg class="user-icon" name="touxiang" size="22" />
      <span class="btn-text">{{ this.name }}</span>
      <i class="el-icon-arrow-down"></i>
    </div>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item @click.native="openInfo">
        <i class="el-icon-user"  />
        个人信息
      </el-dropdown-item>
      <el-dropdown-item @click.native="logOff">
        <icon name="power-off" class="Z6-mr-5" />
        注销
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
  import { mapState, mapActions } from 'vuex';
  import { getName } from '@/utils/auth';
  export default {
    data() {
      return {
        name:getName()
      }
    },
    methods: {
      ...mapActions('store/account', ['logout']),
      /**
       * @description 登出
       */
      logOff() {
        this.logout({
          confirm: true
        });
      },
      openInfo(){
        this.$router.push('/userInfo');
      }
    }
  };
</script>
