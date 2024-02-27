import { Message, MessageBox } from 'element-ui';
import util from '@/libs/util.js';
import router from '@/router';
import { login,getToken } from '@/api/login';

export default {
  namespaced: true,
  actions: {
    /**
     * @description 登录
     * @param {Object} context
     * @param {Object} payload username {String} 用户账号
     * @param {Object} payload password {String} 密码
     * @param {Object} payload route {Object} 登录成功后定向的路由对象 任何 vue-router 支持的格式
     */
    async login({ dispatch }, { username = '', password = '' } = {}) {
      const data = await getToken(username, password);
      if (data.error != "200") {
        Message.warning({
          message: '用户名或密码错误'
        });
        return false;
      }
      util.cookies.set('token', data.result.token);

      const res = await login(username, password);
      if (res.error != "200") {
        Message.warning({
          message: '用户名或密码错误'
        });
        return false;
      }
      util.cookies.set('userId', res.result.id);
      util.cookies.set('username', res.result.username);
      util.cookies.set('name', res.result.name);
      window.$count = 0;

      await dispatch(
        'store/menu/getUserMenu',
        { userId: res.result.id },
        {
          root: true
        }
      );

      // 用户登录后从持久化数据加载一系列的设置
      await dispatch('load');
    },
    /**
     * @description 注销用户并返回登录页面
     * @param {Object} context
     * @param {Object} payload confirm {Boolean} 是否需要确认
     */
    logout({ commit, dispatch }, { confirm = false } = {}) {
      /**
       * @description 注销
       */
      async function logout() {
        // 删除cookie
        util.cookies.remove('username');
        util.cookies.remove('userId');
        util.cookies.remove('token');
        util.cookies.remove('name');
        // 清空 tab页
        await dispatch(
          'store/page/closeAll',
          {},
          {
            root: true
          }
        );
        //退出时，清空菜单
        commit('store/menu/clearMeuns', [],{root:true});

        // 跳转路由，不携带重定向地址
        router.push({
          name: 'login'
          // query: {
          //   redirect: window.location.pathname + window.location.search
          // }
        });
      }
      // 判断是否需要确认
      if (confirm) {
        MessageBox.confirm('确定要注销当前用户吗', '注销用户', {
          type: 'warning'
        }).then(() => {
          logout();
        });
      } else {
        logout();
      }
    },
    /**
     * @description 用户登录后从持久化数据加载一系列的设置
     * @param {Object} context
     */
    async load({ dispatch }) {
      // 加载主题
      await dispatch('store/theme/load', null, {
        root: true
      });
      // 加载页面过渡效果设置
      await dispatch('store/transition/load', null, {
        root: true
      });
      // 持久化数据加载上次退出时的多页列表
      await dispatch('store/page/openedLoad', null, {
        root: true
      });
      // 持久化数据加载侧边栏配置
      await dispatch('store/menu/asideLoad', null, {
        root: true
      });
      // 持久化数据加载全局尺寸
      await dispatch('store/size/load', null, {
        root: true
      });
      // 持久化数据加载颜色设置
      await dispatch('store/color/load', null, {
        root: true
      });
    }
  }
};
