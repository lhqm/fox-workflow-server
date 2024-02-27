import request from '@/utils/request';

// 获取日志列表
export function findOperLogPage(queryParams) {
  return request({
    url: '/api/system/operLog',
    method: 'post',
    data: queryParams
  });
}
export function deleteOperLog(queryParams) {
  return request({
    url: '/api/system/deleteOperLog',
    method: 'post',
    data: queryParams
  });
}
