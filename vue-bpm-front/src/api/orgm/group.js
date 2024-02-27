import request from '@/utils/request';
// 获取全部分组信息
export function getGroupList(name) {
  const data={
    name:name
  };
  return request({
    url: '/api/orgm/getGroupList',
    method: 'post',
    data:data
  });
}
// 获取分组分页数据
export function getGroupPage(data) {
  return request({
    url: '/api/orgm/getGroupPage',
    method: 'post',
    data: data
  });
}
// 获取分组中的人员
export function getUserByGorupId(data) {
  return request({
    url: '/api/orgm/getUserByGorupId',
    method: 'post',
    data: data
  });
}
// 增加分组
export function addGroup(data) {
  return request({
    url: '/api/orgm/addGroup',
    method: 'post',
    data: data
  });
}
// 更新分组
export function updataGroup(data) {
  return request({
    url: '/api/orgm/updataGroup',
    method: 'post',
    data: data
  });
}
// 删除分组
export function deleteGroup(data) {
  return request({
    url: '/api/orgm/deleteGroup',
    method: 'post',
    data: data
  });
}
// 增加分组人员
export function addUserGroup(data) {
  return request({
    url: '/api/orgm/addUserGroup',
    method: 'post',
    data: data
  });
}
// 删除分组人员
export function deleteUserGroup(username,groupid) {
  var data={
    username:username,
    groupid:groupid
  };
  return request({
    url: '/api/orgm/deleteUserGroup',
    method: 'get',
    params: data
  });
}
