import { omit } from 'lodash';

// 路由表
const constantRouterComponents = {
  BasicLayout: () => import('@/layout/header-aside'),
  EmptyLayout: () => import('@/layout/routerView/index.vue'),
  // 页面组件
  home: () => import('@/views/home/index.vue'),
  //个人信息
  userInfo: () => import('@/views/system/userInfo/index.vue'),
  //消息页面
  smslist: () => import('@/views/message/sysMsg/index.vue')
};

export const commonRouter = [
  {
    path: 'userInfo',
    name: 'userInfo',
    title: '个人信息',
    show: false
  },
  {
    path: 'smslist',
    name: 'smslist',
    title: '系统消息',
    show: false
  }
];

const rootRouter = {
  path: '/',
  name: 'index',
  redirect: '',
  meta: { title: '首页' },
  component: 'BasicLayout',
  children: []
};

// 树型结构生成层级路由表
export const generator = (routerMap, parent) => {
  return routerMap.map((item) => {
    const path = item.path;
    const currentRouter = {
      path:
        parent && parent.path
          ? `${parent.path === '/' ? '' : parent.path || ''}/${path}`
          : `${path}`,
      name: item.name || '',
      query:item.query || '',
      component:
        item.componentName ||
        constantRouterComponents[item.component] ||
        constantRouterComponents['EmptyLayout'],
      meta: omit(item, ['children', 'path'])
    };
    if (item.redirect) {
      currentRouter.redirect = item.redirect;
    }
    if (item.children && item.children.length) {
      currentRouter.children = generator(item.children, currentRouter);
    }
    return currentRouter;
  });
};

export function getRootMenu(rows) {
  const rootMenu = [];
  const menus = buildMenu(rows);
  rootRouter.children = menus;
  rootMenu.push(rootRouter);
  return rootMenu;
}
export function buildMenu(list) {
  return list.map((item) => {
    const child = {
      ...item,
      component: item.name,
      componentName:item.component
        ? (resolve)=>require(['@/views/'+item.component+'.vue'],resolve)
        : null,
      children: item.children && item.children.length > 0 ? buildMenu(item.children) : []
    };
    return child;
  });
}

// 树型结构获取首个可访问菜单
export function getFirstLastChild(data) {
  let newPath;
  const getRoutePath = function (newdata) {
    let firstPath = '';
    if (newdata.children && newdata.children.length > 0) {
      firstPath = getRoutePath(newdata.children[0]);
    } else {
      firstPath = `${newdata.path}`;
    }
    return firstPath;
  };
  if (data.length && data[0].children && data[0].children.length) {
    newPath = getRoutePath(data[0].children[0]);
  } else {
    newPath = data.length > 0 ? data[0].path : '';
  }
  return newPath;
}
