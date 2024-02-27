import request from '@/utils/request';

// 获取菜单列表
export function getMenuList(queryParams) {
  return request({
    url: '/api/menu/getMenuList',
    method: 'post',
    data: queryParams
  });
}
// 获取登录菜单
export async function getMenuApi() {
  return request({
    url: '/api/menu/getUserMenuList',
    method: 'get'
  });
}
export function roleMenuTreeselect(id) {
  const queryParams={
    id: id
  };
  return request({
    url: '/api/menu/getRoleMenus',
    method: 'get',
    params:queryParams
  });
}
/**
 * 更新菜单
 * @param menu
 * @returns {Promise<*>}
 */
export async function updateMenu(menu) {
  return request({
    url: '/api/menu/updateMenu',
    method: 'post',
    data: menu
  });
}

/**
 * 增加菜单
 * @param menu
 * @returns {Promise<*>}
 */
export async function addMenu(menu) {
  return request({
    url: '/api/menu/addMenu',
    method: 'post',
    data: menu
  });
}

/**
 * 删除菜单
 * @param menu
 * @returns {Promise<*>}
 */
export async function deleteMenu(menu) {
  return request({
    url: '/api/menu/deleteMenu',
    method: 'post',
    data: menu
  });
}

