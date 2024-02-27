import request from '@/utils/request';

// 获取流程类别
export function flowSortTree() {
  return request({
    url: '/api/deployment/getFlowSortTree',
    method: 'get'
  });
}

// 增加流程类别
export function addFlowSort(queryParams) {
  return request({
    url: '/api/deployment/addFlowSort',
    method: 'post',
    data: queryParams
  });
}

// 保存流程类型
export function saveFlowSort(queryParams) {
  return request({
    url: '/api/deployment/saveFlowSort',
    method: 'post',
    data: queryParams
  });
}

// 删除流程列表
export function deleteFlowSort(queryParams) {
  return request({
    url: '/api/deployment/deleteFlowSort',
    method: 'post',
    data: queryParams
  });
}
