import request from '@/utils/request';

// 获取表单列表
export function formList(queryParams) {
  return request({
    url: '/api/form/formList',
    method: 'post',
    data: queryParams
  });
}
// 获取表单分类
export function formSortList() {
  return request({
    url: '/api/form/formSortList',
    method: 'get'
  });
}
// 删除表单
export function deleteForm(queryParams) {
  return request({
    url: '/api/form/deleteForm',
    method: 'post',
    data: queryParams
  });
}
export function getFormTree() {
  return request({
    url: '/api/form/getFormTree',
    method: 'get'
  });
}
// 增加表单
export function editForm(queryParams) {
  return request({
    url: '/api/form/editForm',
    method: 'post',
    data: queryParams
  });
}
// 增加表单分类
export function addFormSort(queryParams) {
  return request({
    url: '/api/form/addFormSort',
    method: 'post',
    data: queryParams
  });
}

// 保存表单分类
export function updateFormSort(queryParams) {
  return request({
    url: '/api/form/updateFormSort',
    method: 'post',
    data: queryParams
  });
}
// 根据表单id，获取数据
export function getDataById(approval) {
  return request({
    url: '/api/formData/getDataById',
    method: 'post',
    data: approval
  });
}
// 删除表单分类
export function deleteFormSort(approval) {
  return request({
    url: '/api/form/deleteFormSort',
    method: 'post',
    data: approval
  });
}

