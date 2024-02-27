// 设置文件
import setting from '@/setting.js';
import { get } from 'lodash';
import {getMenuApi} from '@/api/system/menu';
import { generator, getRootMenu, getFirstLastChild, commonRouter } from '@/router/processRouter';
import router from '@/router';
import {handleTree} from '@/utils/comm';

export default {
  namespaced: true,
  state: {
    hasRoutes: false,
    // 菜单
    menuList: [],
    // 顶栏菜单
    header: [],
    // 侧栏菜单
    aside: [],
    // 菜单位置
    menuMode: get(setting, 'menu.menuMode', ''),
    // 侧边栏收缩
    asideCollapse: setting.menu.asideCollapse,
    // 侧边栏折叠动画
    asideTransition: setting.menu.asideTransition
  },
  actions: {
    /**
     * 通过接口获取菜单
     * @param {Object} context
     * @param {Object} payload userId {Number} 用户id
     */
    async getUserMenu({ commit, dispatch, state }, { userId = '' } = {}) {
      if (state.hasRoutes){
        return state.menuList;
      }
      else{
        let data = [];
        const res = await getMenuApi();
        if (res && res.result) {
          state.hasRoutes = true;
          data = handleTree(res.result, "menuId");
        }
        state.menuList = data;
        // 设置顶栏菜单
        commit('headerSet', state.menuList);
        // 设置侧边栏菜单
        commit('asideSet', state.menuList);
        await dispatch('addAsyncRoutes', state.menuList);
        return state.menuList;
      }
    },
    /**
     * 动态添加路由
     * @param {Object} context
     * @param {Array} payload data路由数组
     */
    async addAsyncRoutes({ state, dispatch, commit }, data) {
      if (data.length) {
        const notFoundRouter = {
          path: '/:path(.*)*',
          redirect: '/error/404',
          show: false
        };
        const rootMenu = getRootMenu(data.concat(commonRouter));
        const asyncRoutes = generator(rootMenu);
        asyncRoutes[0].redirect =
          asyncRoutes[0].redirect ?? getFirstLastChild(asyncRoutes[0].children);
        asyncRoutes.push(notFoundRouter);

        data = [...asyncRoutes];
        data.forEach((item) => {
          router.addRoute(item);
        });
        await commit('store/page/init', data, { root: true });
        // 持久化数据加载上次退出时的多页列表
        await dispatch('store/page/openedLoad', null, {
          root: true
        });
      }
    },
    /**
     * 设置侧边栏展开或者收缩
     * @param {Object} context
     * @param {Boolean} collapse is collapse
     */
    async asideCollapseSet({ state, dispatch }, collapse) {
      // store 赋值
      state.asideCollapse = collapse;
      // 持久化
      await dispatch(
        'store/db/set',
        {
          dbName: 'sys',
          path: 'menu.asideCollapse',
          value: state.asideCollapse,
          user: true
        },
        { root: true }
      );
    },
    /**
     * 切换侧边栏展开和收缩
     * @param {Object} context
     */
    async asideCollapseToggle({ state, dispatch }) {
      // store 赋值
      state.asideCollapse = !state.asideCollapse;
      // 持久化
      await dispatch(
        'store/db/set',
        {
          dbName: 'sys',
          path: 'menu.asideCollapse',
          value: state.asideCollapse,
          user: true
        },
        { root: true }
      );
    },
    /**
     * 设置菜单位置
     * @param {Object} context
     * @param {Boolean} menuMode is menuMode
     */
    async menuModeSet({ state, dispatch }, menuMode) {
      // store 赋值
      state.menuMode = menuMode;
      // 持久化
      await dispatch(
        'store/db/set',
        {
          dbName: 'sys',
          path: 'menu.menuMode',
          value: state.menuMode,
          user: true
        },
        { root: true }
      );
    },
    /**
     * 设置侧边栏折叠动画
     * @param {Object} context
     * @param {Boolean} transition is transition
     */
    async asideTransitionSet({ state, dispatch }, transition) {
      // store 赋值
      state.asideTransition = transition;
      // 持久化
      await dispatch(
        'store/db/set',
        {
          dbName: 'sys',
          path: 'menu.asideTransition',
          value: state.asideTransition,
          user: true
        },
        { root: true }
      );
    },
    /**
     * 切换侧边栏折叠动画
     * @param {Object} context
     */
    async asideTransitionToggle({ state, dispatch }) {
      // store 赋值
      state.asideTransition = !state.asideTransition;
      // 持久化
      await dispatch(
        'store/db/set',
        {
          dbName: 'sys',
          path: 'menu.asideTransition',
          value: state.asideTransition,
          user: true
        },
        { root: true }
      );
    },
    /**
     * 持久化数据加载侧边栏设置
     * @param {Object} context
     */
    async asideLoad({ state, dispatch }) {
      // store 赋值
      const menu = await dispatch(
        'store/db/get',
        {
          dbName: 'sys',
          path: 'menu',
          defaultValue: setting.menu,
          user: true
        },
        { root: true }
      );
      state.asideCollapse =
        menu.asideCollapse !== undefined ? menu.asideCollapse : setting.menu.asideCollapse;
      state.menuMode = menu.menuMode !== undefined ? menu.menuMode : setting.menu.menuMode;
      state.asideTransition =
        menu.asideTransition !== undefined ? menu.asideTransition : setting.menu.asideTransition;
    }
  },
  mutations: {
    /**
     * @description 设置顶栏菜单
     * @param {Object} state state
     * @param {Array} menu menu setting
     */
    headerSet(state, menu) {
      // store 赋值
      state.header = menu;
    },
    /**
     * @description 设置侧边栏菜单
     * @param {Object} state state
     * @param {Array} menu menu setting
     */
    asideSet(state, menu) {
      // store 赋值
      state.aside = menu;
    },
    clearMeuns(state, menu){
      state.hasRoutes=false;
    },
  }
};
