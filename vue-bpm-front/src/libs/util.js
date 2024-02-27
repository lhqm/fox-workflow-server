import cookies from './util.cookies';
import db from './util.db';
import log from './util.log';

const util = {
  cookies,
  db,
  log
};

/**
 * @description 更新标题
 * @param {String} title 标题
 */
util.title = function (titleText) {
  const processTitle = process.env.VUE_APP_TITLE || 'Z6';
  window.document.title = `${processTitle}${titleText ? ` | ${titleText}` : ''}`;
};

/**
 * @description 打开新页面
 * @param {String} url 地址
 */
util.open = function (url) {
  var a = document.createElement('a');
  a.setAttribute('href', url);
  a.setAttribute('target', '_blank');
  a.setAttribute('id', 'Z6-link-temp');
  document.body.appendChild(a);
  a.click();
  document.body.removeChild(document.getElementById('Z6-link-temp'));
};

export default util;
