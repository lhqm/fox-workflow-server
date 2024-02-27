/**
 * 在主框架之外显示
 */
const frameOut = [
  // 登录
  {
    path: '/login',
    name: 'login',
    component: () => import('@/views/login/index.vue')
  },
  // 刷新页面 必须保留
  {
    path: '/refresh',
    name: 'refresh',
    hidden: true,
    component: () => import('@/views/system/function/refresh')
  },
  // 页面重定向 必须保留
  {
    path: '/redirect/:route*',
    name: 'redirect',
    hidden: true,
    component: () => import('@/views/system/function/redirect')
  }
];

/**
 * 错误页面
 */
const errorPage = [
  {
    path: '/error/404',
    name: '404',
    component: () => import('@/views/system/error/404/index.vue')
  }
];

// 重新组织后导出
export default [...frameOut, ...errorPage];
