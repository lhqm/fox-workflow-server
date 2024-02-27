import request from '@/utils/request';
import util from "@/libs/util";

// 获取流程控制按钮方法
export function getElementButton(taskid) {
  const data = {
    taskid: taskid
  };
  return request({
    url: '/api/task/getElementButton',
    method: 'get',
    params: data
  });
}

// 获取流程审批轨迹
export function getApprovalTracks(process_ints_id) {
  const data = {
    process_ints_id: process_ints_id
  };
  return request({
    url: '/api/task/getApprovalTracks',
    method: 'post',
    data: data
  });
}
export function getUserTaskTrack(process_ints_id) {
  const data = {
    process_ints_id: process_ints_id
  };
  return request({
    url: '/api/task/getUserTaskTrack',
    method: 'post',
    data: data
  });
}
export function saveFormDataJson(processKey,id,form_type,form_url, mapJson, dataJson) {
  const data = {
    processKey,
    id,
    form_type,
    form_url,
    mapJson,
    dataJson
  };
  return request({
    url: '/api/formData/saveFormDataValue',
    method: 'post',
    data: data
  });
}
// 外部表单保存方法,暂不启用
export function saveSelfFormDataJson(id,form_type,form_url, mapJson, dataJson) {
  const data = {
    id,
    form_type,
    form_url,
    mapJson,
    dataJson
  };
  return request({
    url: '/api/form/saveFormDataJson',
    method: 'post',
    data: data
  });
}
export function downLoadFile(url,fileName){
  this.$axios({
    method:'POST',
    url:"/api/"+this.Apis.downFiles+"?access_token="+util.cookies.get('token'),
    data:{
      fileName: fileName,
      filePath:url
    },
    responseType: 'blob'
  })
    .then(response => {
      var patt = new RegExp('filename=([^;]+\\.[^\\.;]+);*')
      var contentDisposition = decodeURI(response.headers['content-disposition'])
      var result = patt.exec(contentDisposition)
      var fileName = result[1]

      let  resdata = response.data;
      const blob = new Blob([resdata],{
        type: 'application/octet-stream;charset=utf-8'
      })

      // 从响应头中获取文件名称
      if ('download' in document.createElement('a')) {
        // 非IE下载
        const elink = document.createElement('a')
        elink.download = fileName
        elink.href = URL.createObjectURL(blob)
        elink.style.display = 'none'
        document.body.appendChild(elink)
        elink.click()
        URL.revokeObjectURL(elink.href) // 释放URL 对象
        document.body.removeChild(elink)
      } else {
        // IE10+下载
        navigator.msSaveBlob(blob, fileParams.fileName)
      }
    })
}
