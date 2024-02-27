import Cookies from 'js-cookie';
import util from '@/libs/util.js';

const TokenKey = 'Token';

export function getToken() {
  return util.cookies.get('token');
  // return Cookies.get(TokenKey);
}

export function setToken(token) {
  util.cookies.set('token', token);
  // return Cookies.set(TokenKey, token);
}

export function removeToken() {
  util.cookies.remove('token');
  // return Cookies.remove(TokenKey);
}

export function getRefToken() {
  return util.cookies.get('RefToken');
  // return Cookies.get('RefToken');
}

export function setRefToken(RefToken) {
  util.cookies.set('RefToken', RefToken);
  // return Cookies.set('RefToken', RefToken);
}

export function getUserName() {
  return util.cookies.get('username');
  // return Cookies.get('UserName');
}

export function setUserName(userName) {
  util.cookies.set('username', userName);
  // return Cookies.set('UserName', userName);
}

export function getName() {
  return util.cookies.get('name');
  // return Cookies.get('Name');
}

export function setName(name) {
  util.cookies.set('name', name);
}

