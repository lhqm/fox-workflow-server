<template>
  <div :class="prefixCls">
    <div v-for="item in menuTypeList" :key="item.title">
      <el-tooltip effect="dark" :content="item.title" placement="bottom">
        <div
          @click="toggleMenuMode(item)"
          :class="[
            `${prefixCls}__item`,
            `${prefixCls}__item--${item.type}`,
            {
              [`${prefixCls}__item--active`]: menuMode === item.type
            }
          ]"
        >
          <div class="mix-sidebar"></div>
        </div>
      </el-tooltip>
    </div>
  </div>
</template>
<script>
  import { mapState, mapActions } from 'vuex';
  import { menuTypeList } from './const';

  export default {
    name: 'menu-type-picker',
    data() {
      return {
        prefixCls: 'menu-type-picker',
        menuTypeList
      };
    },
    computed: {
      ...mapState('store/menu', ['menuMode'])
    },
    methods: {
      ...mapActions('store/menu', ['menuModeSet']),
      ...mapActions('store/menu', ['asideCollapseSet']),
      toggleMenuMode(item) {
        this.menuModeSet(item.type);
        this.asideCollapseSet(false);
      }
    }
  };
</script>

<style lang="scss" scoped>
  $prefix-cls: 'menu-type-picker';
  .#{$prefix-cls} {
    display: flex;
    box-sizing: border-box;
    justify-content: center;
    &__item {
      position: relative;
      width: 56px;
      height: 48px;
      margin-right: 16px;
      overflow: hidden;
      cursor: pointer;
      background-color: #f0f2f5;
      border-radius: 4px;
      box-shadow: 0 1px 2.5px 0 rgb(0 0 0 / 18%);

      &::before,
      &::after {
        position: absolute;
        content: '';
      }

      &--left,
      &--light {
        &::before {
          top: 0;
          left: 0;
          z-index: 1;
          width: 33%;
          height: 100%;
          background-color: #273352;
          border-radius: 4px 0 0 4px;
        }

        &::after {
          top: 0;
          left: 0;
          width: 100%;
          height: 25%;
          background-color: #fff;
        }
      }
      &--top {
        &::after {
          top: 0;
          left: 0;
          width: 100%;
          height: 25%;
          background-color: #273352;
        }
      }

      &--dark {
        background-color: #273352;
      }

      &:hover,
      &--active {
        border: 2px solid #3370ff;

        &::before,
        &::after {
          border-radius: 0;
        }
      }
    }

    img {
      width: 100%;
      height: 100%;
      cursor: pointer;
    }
  }
</style>
