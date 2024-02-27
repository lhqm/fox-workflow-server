// Vue
import Vue from 'vue';
import App from './App';
// 核心插件
import pluginInit from '@/plugin';
// store
import store from '@/store/index';
// 菜单和路由设置
import router from './router';
import axios from 'axios';
Vue.prototype.$axios = axios

import fileServer from 'file-saver';
import xlsx from 'xlsx';
Vue.prototype.$fileServer=fileServer;
Vue.prototype.$xlsx=xlsx;

import { vuePlugin } from '@/package/highlight';
import 'highlight.js/styles/atom-one-dark-reasonable.css';
Vue.use(vuePlugin);
import MyPD from '@/package/index.js';
Vue.use(MyPD);
import DataV from '@jiaminghi/data-view';
Vue.use(DataV);

import '@/assets/iconfont/iconfont.js';
import '@/package/theme/index.scss';
import 'bpmn-js/dist/assets/diagram-js.css';
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn.css';
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-codes.css';
import 'bpmn-js/dist/assets/bpmn-font/css/bpmn-embedded.css';
import '@/assets/css/vue-bmpn.css';
import 'element-ui/lib/theme-chalk/index.css';

// 核心插件
Vue.use(pluginInit);

new Vue({
  router,
  store,
  render: (h) => h(App),
  methods: {
    async unloadHandler(e) {
      // 清空打开的tab
      await this.$store.dispatch('store/page/closeAll');
    }
  },
  created() {
    // 用户登录后从数据库加载一系列的设置
    this.$store.dispatch('store/account/load');
  },
  mounted() {
    // 获取并记录用户 UA
    this.$store.commit('store/ua/get');
    // 初始化全屏监听
    this.$store.dispatch('store/fullscreen/listen');
    // 监听浏览器关闭事件
    window.addEventListener('unload', (e) => this.unloadHandler(e));
  },
  destroyed() {
    window.removeEventListener('unload', (e) => this.unloadHandler(e));
  }
}).$mount('#app');
