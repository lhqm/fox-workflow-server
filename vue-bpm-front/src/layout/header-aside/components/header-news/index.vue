<template>
  <div>
    <el-tooltip effect="dark" content="消息" placement="bottom">
      <el-button class="Z6-ml-0 Z6-mr btn-text can-hover" type="text" @click.native="toSms">
        <icon-svg name="xiaoxi" size="16" />
      </el-button>
    </el-tooltip>
  </div>
</template>

<script>
  export default {
    data() {
      return {};
    },
    methods: {
      toSms(){
        this.$router.push('/smslist');
      }
    }
  };
</script>
