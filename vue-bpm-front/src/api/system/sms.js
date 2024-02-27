import request from '@/utils/request';

// 获取消息列表
export function getSmsList(queryParams) {
  return request({
    url: '/api/system/getSmsList',
    method: 'post',
    data: queryParams
  });
}
// 获取抄送消息列表
export function getCCSmsList(queryParams) {
  return request({
    url: '/api/system/getCCSmsList',
    method: 'post',
    data: queryParams
  });
}
// 设置消息已经读取
export function readSms(queryParams) {
  return request({
    url: '/api/system/readSms',
    method: 'post',
    data: queryParams
  });
}

// 删除消息
export function deleteSms(queryParams) {
  return request({
    url: '/api/system/deleteSms',
    method: 'post',
    data: queryParams
  });
}
