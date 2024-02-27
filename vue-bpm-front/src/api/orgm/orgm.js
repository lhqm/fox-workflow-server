import request from '@/utils/request';

// 获取组织结构信息
export function getOrgData() {
  return request({
    url: '/api/orgm/getOrgData',
    method: 'get'
  });
}
// 获取公司信息
export function getCompanyList() {
  return request({
    url: '/api/orgm/getCompanyList',
    method: 'get'
  });
}
// 增加公司
export function add(data) {
  return request({
    url: '/api/orgm/addCompany',
    method: 'post',
    data: data
  });
}
// 修改公司信息
export function updateCompany(data) {
  return request({
    url: '/api/orgm/updateCompany',
    method: 'post',
    data: data
  });
}
// 删除公司
export function deleteCompany(data) {
  return request({
    url: '/api/orgm/deleteCompany',
    method: 'post',
    data: data
  });
}

// 获取部门列表
export function getDepartmentlist(data) {
  return request({
    url: '/api/orgm/getDepartmentList',
    method: 'post',
    data: data
  });
}

/**
 * 获取部门树
 * @returns {*}
 */
export function getDepartmentEntityList(){
  return request({
    url: '/api/orgm/getDepartmentEntityList',
    method: 'get'
  });
}
// 获取部门人员
export function getDeptUsers(data) {
  return request({
    url: '/api/orgm/getDeptUsers',
    method: 'post',
    data:data
  });
}
// 获取人员树
export function getDeptUserTree() {
  return request({
    url: '/api/orgm/getDeptUserTree',
    method: 'get'
  });
}
// 获取部门列表
export function getDeptByCompany(data) {
  return request({
    url: '/api/orgm/getDeptByCompany',
    method: 'get',
    params: data
  });
}
// 增加部门
export function addDepartment(data) {
  return request({
    url: '/api/orgm/addDepartment',
    method: 'post',
    data: data
  });
}
// 修改部门信息
export function updateDepartment(data) {
  return request({
    url: '/api/orgm/updateDepartment',
    method: 'post',
    data: data
  });
}

// 删除部门
export function delDepartment(data) {
  return request({
    url: '/api/orgm/deleteDepartment',
    method: 'post',
    data: data
  });
}
// 获取岗位列表
export function positionList(data) {
  return request({
    url: '/api/orgm/positionList',
    method: 'get',
    params: data
  });
}
export function positionListPage(data) {
  return request({
    url: '/api/orgm/positionListPage',
    method: 'post',
    data: data
  });
}
// 获取岗位人员
export function getStionUsers(data) {
  return request({
    url: '/api/orgm/getPositionUser',
    method: 'get',
    params:data
  });
}
// 增加岗位
export function addPosition(data) {
  return request({
    url: '/api/orgm/addPosition',
    method: 'post',
    data: data
  });
}
// 修改岗位信息
export function updatePosition(data) {
  return request({
    url: '/api/orgm/updatePosition',
    method: 'post',
    data: data
  });
}

// 删除岗位
export function deletePosition(data) {
  return request({
    url: '/api/orgm/deletePosition',
    method: 'post',
    data: data
  });
}

// 获取角色列表
export function rolesList(data) {
  return request({
    url: '/api/orgm/rolesList',
    method: 'get',
    params: data
  });
}
export function rolesListPage(data) {
  return request({
    url: '/api/orgm/rolesListPage',
    method: 'post',
    data: data
  });
}
// 获取角色人员
export function getRoleUsers(data) {
  return request({
    url: '/api/orgm/getRoleUsers',
    method: 'get',
    params:data
  });
}
// 增加角色
export function addRoles(data) {
  return request({
    url: '/api/orgm/addRoles',
    method: 'post',
    data: data
  });
}
// 修改角色信息
export function updateRoles(data) {
  return request({
    url: '/api/orgm/updateRoles',
    method: 'post',
    data: data
  });
}
export function roleDeptTreeselect(id){
  const queryParams={
    id: id
  };
  return request({
    url: '/api/orgm/roleDeptTreeselect',
    method: 'get',
    params:queryParams
  });
}

// 删除角色
export function deleteRoles(data) {
  return request({
    url: '/api/orgm/deleteRoles',
    method: 'post',
    data: data
  });
}
