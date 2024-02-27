import request from '@/utils/request';

// 获取人员列表
export function getUserInfoList(queryParams) {
  return request({
    url: '/api/orgm/getUserList',
    method: 'post',
    data: queryParams
  });
}
// 获取人员列表
export function getUserByDepartId(queryParams) {
  return request({
    url: '/api/orgm/getUserByDepartId',
    method: 'get',
    params: queryParams
  });
}
// 获取人员信息
export function getUserInfo(data) {
  return request({
    url: '/api/orgm/getUserEntity',
    method: 'get',
    params: data
  });
}
// 增加人员
export function add(data) {
  return request({
    url: '/api/orgm/addUser',
    method: 'post',
    data: data
  });
}
// 修改人员信息
export function updateUser(data) {
  return request({
    url: '/api/orgm/updateUser',
    method: 'post',
    data: data
  });
}
// 重置密码
export function resetPwd(data) {
  return request({
    url: '/api/orgm/resetPwd',
    method: 'post',
    data:data
  });
}
// 修改密码
export function updatePwd(data) {
  return request({
    url: '/api/orgm/updatePwd',
    method: 'post',
    data: data
  });
}
// 删除人员
export function deleteUser(data) {
  return request({
    url: '/api/orgm/deleteUser',
    method: 'post',
    data: data
  });
}
