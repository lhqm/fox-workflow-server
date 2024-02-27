import Vue from 'vue';
import VueRouter from 'vue-router';

// 进度条
import NProgress from 'nprogress';
import 'nprogress/nprogress.css';

import store from '@/store/index';
import util from '@/libs/util.js';

// 路由数据
import routes from './routes';
const whiteList = ['/login'];

// fix vue-router NavigationDuplicated
const VueRouterPush = VueRouter.prototype.push;
VueRouter.prototype.push = function push(location) {
  return VueRouterPush.call(this, location).catch((err) => err);
};
const VueRouterReplace = VueRouter.prototype.replace;
VueRouter.prototype.replace = function replace(location) {
  return VueRouterReplace.call(this, location).catch((err) => err);
};

// 导出路由 在 main.js 里使用
const router = new VueRouter({
  mode: 'history',
  routes
});

/**
 * 路由拦截
 * 权限验证
 */
router.beforeEach(
  async (to, from, next) => {
    // 确认已经加载多标签页数据
    await store.dispatch('store/page/isLoaded');
    // 确认已经加载组件尺寸设置
    await store.dispatch('store/size/isLoaded');
    // 进度条
    NProgress.start();
    // 关闭搜索面板
    store.commit('store/search/set', false);
    // 这里暂时将cookie里是否存有token作为验证是否登录的条件
    // 请根据自身业务需要修改
    if (whiteList.indexOf(to.path) !== -1) {
      next();
    } else {
      const token = util.cookies.get('token');
      if (token && token !== 'undefined') {
        try {
          await store.dispatch('store/menu/getUserMenu');
          const routes = router.getRoutes();
          const hasRoute = routes.some((x) => x.name === to.name);
          if (hasRoute) {
            next();
          } else {
            const hasRoutes = store.state.store.menu.hasRoutes;
            if (!hasRoutes) {
              next({ name: 404 });
            }
            return next({ path: to.fullPath, query: to.query });
          }
        } catch (error) {
          store.dispatch('store/account/logout');
        }
      } else {
        // 没有登录的时候跳转到登录界面
        // 携带上登陆成功之后需要跳转的页面完整路径
        next({
          name: 'login',
          query: {
            redirect: to.fullPath
          }
        });
        NProgress.done();
      }
    }
  },
  (err) => {
    console.log('err', err);
  }
);

router.onError((error) => {
  console.log(error, '路由错误');
});

router.afterEach((to) => {
  // 进度条
  NProgress.done();
  // 多页控制 打开新的页面
  store.dispatch('store/page/open', to);
  // 更改标题
  util.title(to.meta.title);
});

export default router;

Vue.use(VueRouter);
