<template>
  <div :class="prefixCls">
    <div v-for="item in colorList" :key="item.color">
      <el-tooltip effect="dark" :content="item.title" placement="bottom">
        <span
          @click="handleClick(item)"
          :class="[
            `${prefixCls}__item`,
            {
              [`${prefixCls}__item--active`]: activeName === item.name
            }
          ]"
          :style="{ background: item.color }"
        >
          <i v-if="activeName === item.name" class="el-icon-check"></i>
        </span>
      </el-tooltip>
    </div>
  </div>
</template>
<script>
  import { mapState, mapActions } from 'vuex';
  import setting from '@/setting.js';
  import { get } from 'lodash';

  export default {
    name: 'ThemeColorPicker',
    props: {
      colorList: {
        type: Array,
        default: () => get(setting, 'theme.list', [])
      }
    },
    data() {
      return {
        prefixCls: 'setting-theme-picker'
      };
    },
    computed: {
      ...mapState('store/theme', ['activeName'])
    },
    methods: {
      ...mapActions('store/theme', ['set']),
      ...mapActions('store/color', { setColor: 'set' }),
      handleClick(item) {
        this.set(item.name);
        this.setColor(item.primaryColor);
      }
    }
  };
</script>
<style lang="scss" scoped>
  $prefix-cls: 'setting-theme-picker';

  .#{$prefix-cls} {
    display: flex;
    flex-wrap: wrap;
    margin: 16px 0;
    justify-content: center;

    &__item {
      display: inline-block;
      width: 20px;
      height: 20px;
      text-align: center;
      cursor: pointer;
      border: 1px solid #ddd;
      border-radius: 2px;
      margin-right: 15px;

      i {
        display: inline-block;
        font-size: 12px;
        color: #fff !important;
      }

      &--active {
        border: 2px solid lighten(#3370ff, 10%);

        svg {
          display: inline-block;
          margin: 0 0 3px 3px;
          font-size: 12px;
          fill: #fff !important;
        }
      }
    }
  }
</style>
