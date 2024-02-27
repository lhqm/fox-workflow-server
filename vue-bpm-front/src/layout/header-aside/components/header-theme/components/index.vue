<template>
  <div class="theme-config">
    <el-divider content-position="center">主题</el-divider>
    <!-- <div :class="getClass" @click="toggleDarkMode">
      <div :class="`${prefixCls}-inner`"></div>
      <icon-svg name="sun" size="14" />
      <icon-svg name="moon" size="14" />
    </div> -->
    <ThemeColorPicker />

    <el-divider content-position="center">导航栏模式</el-divider>
    <type-picker />
  </div>
</template>

<script>
  import { mapState, mapActions } from 'vuex';
  import TypePicker from './TypePicker.vue';
  import ThemeColorPicker from './ThemeColorPicker.vue';

  export default {
    name: 'theme-config',
    components: {
      TypePicker,
      ThemeColorPicker
    },
    data() {
      return {
        prefixCls: 'light-dark-switch'
      };
    },
    computed: {
      ...mapState('store/theme', ['activeName']),
      isDark() {
        return this.activeName === 'dark';
      },
      getClass() {
        return [
          this.prefixCls,
          {
            [`${this.prefixCls}--dark`]: this.isDark
          }
        ];
      }
    },
    methods: {
      ...mapActions('store/theme', ['set']),
      toggleDarkMode() {
        if (this.isDark) {
          this.set('light');
        } else {
          this.set('dark');
        }
      }
    }
  };
</script>

<style lang="scss" scoped>
  $prefix-cls: 'light-dark-switch';
  html[data-theme='dark'] {
    .#{$prefix-cls} {
      border: 1px solid rgb(196 188 188);
    }
  }
  .el-divider--horizontal {
    margin: 30px 0;
  }

  .#{$prefix-cls} {
    position: relative;
    display: flex;
    width: 50px;
    height: 26px;
    padding: 0 6px;
    margin: 0 auto;
    cursor: pointer;
    background-color: #151515;
    border-radius: 30px;
    justify-content: space-between;
    align-items: center;
    box-sizing: border-box;

    &-inner {
      position: absolute;
      z-index: 1;
      width: 18px;
      height: 18px;
      background-color: #fff;
      border-radius: 50%;
      transition: transform 0.5s, background-color 0.5s;
      will-change: transform;
    }

    &--dark {
      .#{$prefix-cls}-inner {
        transform: translateX(calc(100% + 2px));
      }
    }
  }
</style>
