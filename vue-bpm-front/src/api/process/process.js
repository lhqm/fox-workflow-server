import request from '@/utils/request';

// 获取流程定义xml
export function deploymentXML(queryParams) {
  return request({
    url: '/api/deployment/getDeploymentXmlById',
    method: 'get',
    params: queryParams
  });
}

// 流程发布
export function deployWithBPMNJS(queryParams) {
  return request({
    url: '/api/deployment/deployWithBPMNJS',
    method: 'post',
    data: queryParams
  });
}

// 设置节点处理人信息
export function setAssigneeUserWithTaskId(queryParams) {
  return request({
    url: '/api/as/setAssigneeUser',
    method: 'post',
    data: queryParams
  });
}

// 获取节点处理人信息
export function getAssigneeUserWithTaskId(queryParams) {
  return request({
    url: '/api/as/getAssigneeUserWithTaskId',
    method: 'get',
    params: queryParams
  });
}

// 删除处理人信息
export function deleteAssigneeUser(approval) {
  return request({
    url: '/api/as/deleteAssigneeUser',
    method: 'post',
    data: approval
  });
}
// 设置流程信息
export function setFlowAttrs(data) {
  return request({
    url: '/api/as/setFlowElementAttrs',
    method: 'post',
    data: data
  });
}
