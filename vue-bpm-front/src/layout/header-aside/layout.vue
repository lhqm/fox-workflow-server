<template>
  <div class="layout-header-aside-group" :class="{ grayMode: grayActive }">
    <!-- 半透明遮罩 -->
    <div class="layout-header-aside-mask"></div>
    <!-- 主体内容 -->
    <div class="layout-header-aside-content" flex="dir:top">
      <!-- 顶栏 -->
      <div class="theme-header" :class="{ 'header-top': menuMode === 'top' }" flex-box="0" flex>
        <router-link
          to="/index"
          :class="{ 'logo-group': true, 'logo-transition': asideTransition }"
          :style="{ width: asideCollapse ? asideWidthCollapse : asideWidth }"
          flex-box="0"
        >
          <img :src="`${$baseUrl}image/theme/${themeActiveSetting.name}/logo/icon-only.png`" />
          <div
            class="logo-title"
            :style="{
              display: asideCollapse ? 'none' : 'block',
              opacity: asideCollapse ? 0 : 1
            }"
          >
            <p>OA流程系统</p>
            <p>OA BPM management system</p>
          </div>
        </router-link>
        <div
          v-if="menuMode === 'left'"
          class="toggle-aside-btn"
          @click="handleToggleAside"
          flex-box="0"
        >
          <icon-svg v-if="asideCollapse" name="indent" />
          <icon-svg v-else name="outdent" />
        </div>
        <menu-header v-if="menuMode === 'top'" flex-box="1" />
        <div flex-box="1"></div>
        <!-- 顶栏右侧 -->
        <div class="header-right" flex-box="0">
          <header-help />
          <header-news />
          <header-theme />
          <header-size />
          <header-user />
        </div>
      </div>
      <!-- 下面 主体 -->
      <div
        class="theme-container"
        :class="{ 'header-top': menuMode === 'top', asideCollapse: asideCollapse }"
        flex-box="1"
        flex
      >
        <!-- 主体 侧边栏 -->
        <div
          v-if="menuMode === 'left'"
          flex-box="0"
          ref="aside"
          :class="{
            'theme-container-aside': true,
            'theme-container-transition': asideTransition
          }"
          :style="{
            ...styleLayoutMainGroup,
            width: asideCollapse ? asideWidthCollapse : asideWidth
          }"
        >
          <menu-side />
        </div>
        <div :style="{ width: menuMode === 'left' ? '10px' : '20px' }"></div>
        <!-- 主体 -->
        <div class="theme-container-main" flex-box="1" flex>
          <!-- 内容 -->
          <transition name="fade-scale">
            <div class="theme-container-main-layer" flex="dir:top">
              <!-- tab -->
              <div v-if="menuMode === 'left'" class="theme-container-main-header" flex-box="0">
                <tabs />
              </div>
              <div v-else class="theme-container-main-breadcrumb" flex-box="0">
                <breadcrumb id="breadcrumb-container" class="breadcrumb-container" />
              </div>
              <!-- 页面 -->
              <div class="theme-container-main-body" flex-box="1">
                <transition :name="transitionActive ? 'fade-transverse' : ''">
                  <keep-alive :include="keepAlive">
                    <router-view :key="routerViewKey" />
                  </keep-alive>
                </transition>
              </div>
            </div>
          </transition>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import MenuSide from './components/menu-side';
import MenuHeader from './components/menu-header';
import Tabs from './components/tabs';
import Breadcrumb from './components/breadcrumb';
import HeaderNews from './components/header-news';
import HeaderHelp from './components/header-help';
import HeaderSize from './components/header-size';
import HeaderTheme from './components/header-theme';
import HeaderUser from './components/header-user';
import { mapState, mapGetters, mapActions } from 'vuex';
import mixinSearch from './mixins/search';
export default {
  name: 'layout-header-aside',
  mixins: [mixinSearch],
  components: {
    MenuSide,
    MenuHeader,
    Tabs,
    Breadcrumb,
    HeaderSize,
    HeaderTheme,
    HeaderUser,
    HeaderHelp,
    HeaderNews
  },
  data() {
    return {
      // [侧边栏宽度] 正常状态
      asideWidth: '220px',
      // [侧边栏宽度] 折叠状态
      asideWidthCollapse: '65px'
    };
  },
  computed: {
    ...mapState('store', {
      keepAlive: (state) => state.page.keepAlive,
      grayActive: (state) => state.gray.active,
      transitionActive: (state) => state.transition.active,
      asideCollapse: (state) => state.menu.asideCollapse,
      menuMode: (state) => state.menu.menuMode,
      asideTransition: (state) => state.menu.asideTransition
    }),
    ...mapGetters('store', {
      themeActiveSetting: 'theme/activeSetting'
    }),
    /**
     * @description 用来实现带参路由的缓存
     */
    routerViewKey() {
      // 默认情况下 key 类似 __transition-n-/foo
      // 这里的字符串操作是为了最终 key 的格式和原来相同 类似 __transition-n-__stamp-time-/foo
      const stamp = this.$route.meta[`__stamp-${this.$route.path}`] || '';
      return `${stamp ? `__stamp-${stamp}-` : ''}${this.$route.path}`;
    },
    /**
     * @description 最外层容器的背景图片样式
     */
    styleLayoutMainGroup() {
      return this.themeActiveSetting.backgroundImage
        ? {
          backgroundImage: `url('${this.$baseUrl}${this.themeActiveSetting.backgroundImage}')`
        }
        : {};
    }
  },
  methods: {
    ...mapActions('store/menu', ['asideCollapseToggle']),
    /**
     * 接收点击切换侧边栏的按钮
     */
    handleToggleAside() {
      this.asideCollapseToggle();
    }
  }
};
</script>

<style lang="scss">
// 注册主题
@import '~@/assets/style/theme/register.scss';
</style>
