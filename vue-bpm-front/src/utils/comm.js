import { isArray, isString } from 'lodash';

export function tansParams(params) {
  let result = '';
  for (const propName of Object.keys(params)) {
    const value = params[propName];
    var part = encodeURIComponent(propName) + '=';
    if (value !== null && value !== '' && typeof value !== 'undefined') {
      if (typeof value === 'object') {
        for (const key of Object.keys(value)) {
          if (value[key] !== null && value[key] !== '' && typeof value[key] !== 'undefined') {
            const params = propName + '[' + key + ']';
            var subPart = encodeURIComponent(params) + '=';
            result += subPart + encodeURIComponent(value[key]) + '&';
          }
        }
      } else {
        result += part + encodeURIComponent(value) + '&';
      }
    }
  }
  return result;
}
/**
 * 构造树型结构数据
 * @param {*} data 数据源
 * @param {*} id id字段 默认 'id'
 * @param {*} parentId 父节点字段 默认 'parentId'
 * @param {*} children 孩子节点字段 默认 'children'
 */
export function handleTree(data, id, parentId, children) {
  let config = {
    id: id || 'id',
    parentId: parentId || 'parentId',
    childrenList: children || 'children'
  };

  var childrenListMap = {};
  var nodeIds = {};
  var tree = [];

  for (let d of data) {
    let parentId = d[config.parentId];
    if (childrenListMap[parentId] == null) {
      childrenListMap[parentId] = [];
    }
    nodeIds[d[config.id]] = d;
    childrenListMap[parentId].push(d);
  }

  for (let d of data) {
    let parentId = d[config.parentId];
    if (nodeIds[parentId] == null) {
      tree.push(d);
    }
  }

  for (let t of tree) {
    adaptToChildrenList(t);
  }

  function adaptToChildrenList(o) {
    if (childrenListMap[o[config.id]] !== null) {
      o[config.childrenList] = childrenListMap[o[config.id]];
    }
    if (o[config.childrenList]) {
      for (let c of o[config.childrenList]) {
        adaptToChildrenList(c);
      }
    }
  }
  return tree;
}
// 日期格式化
export function parseTime(time, pattern) {
  if (arguments.length === 0 || !time) {
    return null
  }
  const format = pattern || '{y}-{m}-{d} {h}:{i}:{s}'
  let date
  if (typeof time === 'object') {
    date = time
  } else {
    date = new Date(time)
  }
  const formatObj = {
    y: date.getFullYear(),
    m: date.getMonth() + 1,
    d: date.getDate(),
    h: date.getHours(),
    i: date.getMinutes(),
    s: date.getSeconds(),
    a: date.getDay()
  }
  const time_str = format.replace(/{(y|m|d|h|i|s|a)+}/g, (result, key) => {
    let value = formatObj[key]
    // Note: getDay() returns 0 on Sunday
    if (key === 'a') { return ['日', '一', '二', '三', '四', '五', '六'][value] }
    if (result.length > 0 && value < 10) {
      value = '0' + value
    }
    return value || 0
  })
  return time_str
}
// 验证是否为blob格式
export async function blobValidate(data) {
  try {
    const text = await data.text();
    JSON.parse(text);
    return false;
  } catch (error) {
    return true;
  }
}

export const clearObj = (obj) => {
  for (const key in obj) {
    if (isArray(obj[key])) {
      obj[key] = [];
    } else if (isString(obj[key])) {
      obj[key] = '';
    } else {
      obj[key] = null;
    }
  }
};

const createColor = () => {
  const r = randomInt(255);
  const g = randomInt(255);
  const b = randomInt(255);
  const c = `#${r.toString(16)}${g.toString(16)}${b.toString(16)}000`;
  return c.slice(0, 7);
};

/**
 * 获取
 * @param json
 * @returns {*[]}
 */
export function dataJson(json){
  let dataJson=[];
  let key={
    id:'',
    label:'',
    compType:'',
    options:[],
    dataType:'',
    action:''
  };
  json.list.forEach(item => {
    if (
      item.compType == 'text' ||
      item.compType == 'divider' ||
      item.compType == 'barCode' ||
      item.compType == 'alert' ||
      item.compType == 'link' ||
      item.compType == 'button' ||
      item.compType == 'upload' ||
      item.compType == 'dynamicTable' ||
      item.compType == 'colorPicker'
    ) {
      return;
    }
    else if (item.compType == 'row'){
      item.columns.forEach(row => {
        row.list.forEach(list => {
          if (
            list.compType == 'text' ||
            list.compType == 'divider' ||
            list.compType == 'barCode' ||
            list.compType == 'alert' ||
            list.compType == 'link' ||
            list.compType == 'button' ||
            list.compType == 'upload' ||
            list.compType == 'dynamicTable' ||
            list.compType == 'colorPicker'
          ) {
            return;
          }
          else if (list.compType == 'select' || list.compType == 'radio'
            || list.compType == 'cascader'|| list.compType == 'checkbox'){
            key={
              id:'',
              label:'',
              compType:'',
              options:[],
              dataType:'',
              action:''
            };
            key.id=list.id;
            key.label=list.label;
            key.compType=list.compType;
            key.options=list.options;
            key.dataType=list.dataType;
            key.action=list.action;
            dataJson.push(key);
          }
          else{
            key={
              id:'',
              label:'',
              compType:'',
              options:[],
              dataType:'',
              action:''
            };
            key.id=list.id;
            key.label=list.label;
            key.compType=list.compType;
            dataJson.push(key);
          }
        });
      });
    }else if (item.compType == 'select' || item.compType == 'radio'
      || item.compType == 'cascader'|| item.compType == 'checkbox'){
      key={
        id:'',
        label:'',
        compType:'',
        options:[],
        dataType:'',
        action:''
      };
      key.id=item.id;
      key.label=item.label;
      key.compType=item.compType;
      key.options=item.options;
      key.dataType=item.dataType;
      key.action=item.action;
      dataJson.push(key);
    }
    else{
      key={
        id:'',
        label:'',
        compType:'',
        options:[],
        dataType:'',
        action:''
      };
      key.id=item.id;
      key.label=item.label;
      key.compType=item.compType;
      dataJson.push(key);
    }
  })
  return dataJson;
}
export function itemReadOnly(json){
  let newRefList = json.filter((item) => {
    if (
      item.compType == 'text' ||
      item.compType == 'divider' ||
      item.compType == 'barCode' ||
      item.compType == 'alert' ||
      item.compType == 'link' ||
      item.compType == 'button' ||
      item.compType == 'colorPicker'
    ) {
      return item;
    } else if (item.compType == 'dynamicTable') {
      let childrenColumns = item.columns.filter((col) => {
        if (col.compType == 'text') {
          return col;
        } else if (col.compType == 'upload') {
          return col;
        } else {
          col.readonly = true;
          col.disabled = true;
          col.rules = [];
          return col;
        }
      });
      item.buttonAdd = false;
      item.buttonDel = false;
      return (item.columns = childrenColumns);
    } else if (item.compType == 'row') {
      let rows = item.columns;
      let colRows = rows.filter((row) => {
        let rowList = row.list;
        let colRowList = rowList.filter((rl) => {
          if (
            rl.compType == 'text' ||
            rl.compType == 'divider' ||
            rl.compType == 'barCode' ||
            rl.compType == 'alert' ||
            rl.compType == 'link' ||
            rl.compType == 'button' ||
            rl.compType == 'colorPicker'
          ) {
            return rl;
          } else if (rl.compType == 'dynamicTable') {
            let childrenColumns = rl.columns.filter((col) => {
              if (col.compType == 'text') {
                return col;
              } else if (col.compType == 'upload') {
                return col;
              } else {
                col.readonly = true;
                col.disabled = true;
                col.rules = [];
                return col;
              }
            });
            rl.buttonAdd = false;
            rl.buttonDel = false;
            return (rl.columns = childrenColumns);
          } else {
            rl.readonly = true;
            rl.disabled = true;
            rl.rules = [];
            return rl;
          }
        });
        row.list = colRowList;
        return row;
      });
      return (item.columns = colRows);
    } else {
      item.readonly = true;
      item.disabled = true;
      item.rules = [];
      return item;
    }
  });
  return newRefList;
}
/**
 * 导出excel
 * @param tables
 * @returns {any}
 */
export function exp(tables,that){
  var xlsxParam = {raw: true}  // 导出的内容只做解析，不进行格式转换
  let table_book = that.$xlsx.utils.table_to_book(tables, xlsxParam);
  const table_write = that.$xlsx.write(table_book, {
    bookType: "xlsx",
    bookSST: true,
    type: "array",
  });
  try {
    that.$fileServer.saveAs(
      new Blob([table_write], {type: "application/octet-stream"}), `${new Date().getTime()}.xlsx`   //这里是文件名
    );
  } catch (e) {
    if (typeof console !== "undefined") console.log(e, table_write);
  }
  return table_write;
}
const randomInt = (max) => {
  return Math.floor(Math.random() * max);
};

export const randomColor = () => {
  return `linear-gradient(${180}deg, ${createColor()} 0%, ${createColor()} 100%)`;
  // return '#' + Math.floor(Math.random() * 256).toString(10);
};
