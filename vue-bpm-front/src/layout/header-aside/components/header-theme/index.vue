<template>
  <div>
    <el-tooltip effect="dark" content="主题设置" placement="bottom">
      <el-button class="Z6-ml-0 Z6-mr btn-text can-hover" type="text" @click="dialogVisible = true">
        <icon-svg name="theme" />
      </el-button>
    </el-tooltip>
    <el-dialog title="设置" width="600px" :visible.sync="dialogVisible" :append-to-body="true">
      <theme-config style="height: 200px" />
    </el-dialog>
  </div>
</template>

<script>
  import themeConfig from './components/index.vue';
  export default {
    components: {
      'theme-config': themeConfig
    },
    data() {
      return {
        dialogVisible: false
      };
    }
  };
</script>
