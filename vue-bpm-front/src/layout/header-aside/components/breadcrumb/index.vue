<template>
  <el-breadcrumb class="app-breadcrumb" separator="/">
    <transition-group name="breadcrumb">
      <el-breadcrumb-item v-for="(item, index) in levelList" :key="item.path">
        <span
          v-if="item.redirect === 'noRedirect' || index == levelList.length - 1"
          class="no-redirect"
          >{{ item.meta.title }}</span
        >
        <span v-else>{{ item.meta.title }}</span>
      </el-breadcrumb-item>
    </transition-group>
  </el-breadcrumb>
</template>

<script>
  import pathToRegexp from 'path-to-regexp';

  export default {
    data() {
      return {
        levelList: null
      };
    },
    watch: {
      $route(route) {
        // if you go to the redirect page, do not update the breadcrumbs
        if (route.path.startsWith('/redirect/')) {
          return;
        }
        this.getBreadcrumb();
      }
    },
    created() {
      this.getBreadcrumb();
    },
    methods: {
      getBreadcrumb() {
        const matched = this.$route.matched.filter((item) => item.meta && item.meta.title);
        this.levelList = matched.filter(
          (item) => item.meta && item.meta.title && item.meta.breadcrumb !== false
        );
      }
    }
  };
</script>

<style lang="scss" scoped>
  .app-breadcrumb.el-breadcrumb {
    display: inline-block;
    font-size: 14px;
    line-height: 46px;
  }
</style>
