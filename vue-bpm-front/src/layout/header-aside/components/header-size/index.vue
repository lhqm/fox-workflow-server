<template>
  <el-dropdown placement="bottom" size="small" @command="handleChange">
    <el-button class="Z6-mr btn-text can-hover" type="text">
      <icon-svg name="zihao" size="15" />
    </el-button>
    <el-dropdown-menu slot="dropdown">
      <el-dropdown-item v-for="item in options" :key="item.value" :command="item.value">
        <icon :name="iconName(item.value)" class="Z6-mr-5" />{{ item.label }}
      </el-dropdown-item>
    </el-dropdown-menu>
  </el-dropdown>
</template>

<script>
  import { mapState, mapMutations, mapActions } from 'vuex';
  export default {
    name: 'header-size',
    data() {
      return {
        options: [
          { label: '默认', value: 'default' },
          { label: '中', value: 'medium' },
          { label: '小', value: 'small' },
          { label: '最小', value: 'mini' }
        ]
      };
    },
    computed: {
      ...mapState('store/size', ['value'])
    },
    methods: {
      ...mapMutations({
        pageKeepAliveClean: 'store/page/keepAliveClean'
      }),
      ...mapActions({
        sizeSet: 'store/size/set'
      }),
      handleChange(value) {
        this.sizeSet(value);
        this.$notify({
          title: '提示',
          dangerouslyUseHTMLString: true,
          message: '已更新页面内 <b>组件</b> 的 <b>默认尺寸</b>',
          type: 'success'
        });
      },
      iconName(name) {
        return name === this.value ? 'dot-circle-o' : 'circle-o';
      }
    }
  };
</script>
