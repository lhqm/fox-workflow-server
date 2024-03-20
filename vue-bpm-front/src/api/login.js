import request from '@/utils/request';

// 获取token
export function getToken(username, password) {
  const data = {
    username,
    password
  };
  return request({
    url: '/api/login/getToken',
    method: 'post',
    data: data
  });
}
// 登录方法
export function login(username, password) {
  const data = {
    username,
    password
  };
  return request({
    url: '/api/login/loginAs',
    method: 'post',
    data: data
  });
}

// 注册方法
export function register(data) {
  return request({
    url: '/register',
    headers: {
      isToken: false
    },
    method: 'post',
    data: data
  });
}

// 退出方法
export function logout() {
  return request({
    url: '/api/orgm/logout',
    method: 'post'
  });
}
// 获取RAM路径
export function getRamUrl() {
  return request({
    url: '/api/ram/redirectUrl',
    method: 'get'
  });
}
