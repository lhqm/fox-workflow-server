export default {
  // 快捷键
  // 支持快捷键 例如 ctrl+shift+s
  hotkey: {
    search: {
      open: 's',
      close: 'esc'
    }
  },
  // 尺寸
  size: 'medium',
  // 侧边栏默认配置
  menu: {
    asideCollapse: false,
    asideTransition: false,
    menuMode: 'left'
  },
  // 在读取持久化数据失败时默认页面
  page: {
    opened: [
      {
        name: 'index',
        fullPath: '/index',
        meta: {
          title: '首页',
          auth: false
        }
      }
    ]
  },
  // 菜单搜索
  search: {
    enable: true
  },
  // 注册的主题
  theme: {
    list: [
      {
        title: 'darkNav',
        name: 'darkNav',
        color: '#020021',
        dataVbgColor:'#3370ff',
        primaryColor: '#3370ff'
      },
      {
        title: 'light',
        name: 'light',
        color: '#ffffff',
        dataVbgColor:'#5378a2',
        primaryColor: '#3370ff'
      }
    ]
  },
  // 是否默认开启页面切换动画
  transition: {
    active: true
  }
};
