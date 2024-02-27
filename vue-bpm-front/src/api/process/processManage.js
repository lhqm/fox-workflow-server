import request from '@/utils/request';

//获取所有启动的流程实例
export function getProcessManageListPage(queryParams) {
  return request({
    url: '/api/processManage/getProcessManageListPage',
    method: 'post',
    data: queryParams
  });
}
//获取所有启动的流程实例
export function getTaskManageListPage(queryParams) {
  return request({
    url: '/api/processManage/getTaskManageListPage',
    method: 'post',
    data: queryParams
  });
}
//获取所有启动的流程实例补充参数
export function getManageParams(business_key,proce_inst_id) {
  const queryParams={
    business_key,
    proce_inst_id
  }
  return request({
    url: '/api/processManage/getManageParams',
    method: 'post',
    data: queryParams
  });
}
//获取流程统计分析数据
export function getStatAnalysisData(queryParams) {
  return request({
    url: '/api/processManage/getStatAnalysisData',
    method: 'post',
    data: queryParams
  });
}
//获取我发起的流程列表
export function getMyStartListPage(queryParams) {
  return request({
    url: '/api/processManage/getMyStartListPage',
    method: 'post',
    data: queryParams
  });
}
//获取我审核的流程列表
export function getMyProcessPage(queryParams) {
  return request({
    url: '/api/processManage/getMyProcessPage',
    method: 'post',
    data: queryParams
  });
}
