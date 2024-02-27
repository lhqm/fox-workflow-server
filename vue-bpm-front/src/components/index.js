import Vue from 'vue';

import container from './container';

// 注意 有些组件使用异步加载会有影响
Vue.component('container', container);
Vue.component('icon', () => import('./icon'));
Vue.component('icon-svg', () => import('./icon-svg/index.vue'));
