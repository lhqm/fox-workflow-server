// 模态框
import util from "@/libs/util";
import {getDictDatas} from '@/api/system/dicts';
import {getDataById} from "@/api/process/processFrom";

export const remoteData = {
    methods: {
      getRemoteData(){
        switch(this.conf.compType){
          case "dialogList":
            //动态数据
            if(this.conf.compType=='dialogList'&&this.conf.action!=''&&this.conf.dataType === 'dynamic'){
              if(this.conf.options.length==0){
                getDataById(this.conf.action).then(res=>{
                  this.conf.options=[];
                  if(res.error=='200'){
                    this.conf.options = this.conf.options.concat(res.result);
                    return;
                  }
                });
              }
            }
            break;
          case "select":
          case "radio":
          case "checkbox":
            if(this.conf.dataType === 'dynamic'&&this.conf.action!=''){
              if(this.conf.options.length==0){
                getDictDatas(this.conf.action).then(res=>{
                  this.conf.options=[];
                  if(res.error=='200'){
                    this.conf.options = this.conf.options.concat(res.result);
                  }
                });
              }
            }
            break;
        }


      },
      upDownLoad(fileParams){
        this.$axios({
          method:'POST',
          url:"/proc-api/"+this.Apis.downFiles+"?access_token="+util.cookies.get('token'),
          data:{
            fileName: fileParams.fileName,
            filePath:fileParams.filePath
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
    }
}

export const changeId = {
    methods: {
        handlerChangeId(val){
            let idArray = this.getFormId(this.props._id);
            console.log(idArray);
            if(idArray.includes(val)){  //如果存在id相等，则提示
                this.$message.error('该ID已经存在，请修改');
                this.props.id=this.props._id;
            }else{
                this.props._id=val;
            }
        }
    }
}
