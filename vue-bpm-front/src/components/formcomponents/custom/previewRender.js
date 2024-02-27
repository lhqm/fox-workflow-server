import { isAttr, jsonClone } from '../utils';
import childrenItem from './slot/index';
import { remoteData } from './mixin';
import { getToken } from '@/utils/auth';
//先修改在这里,后续需要优化
function vModel(self, dataObject) {
  dataObject.props.value = self.value;
  dataObject.on.input = (val) => {
    self.$emit('input', val);
  };
  //判断是否为上传组件
  if (self.conf.compType === 'upload') {
    dataObject.attrs['before-upload'] = (file) => {
      //非限定后缀不允许上传
      const fileName = file.name;
      const suffixName = fileName.split('.').pop();

      if (!self.conf.accept.includes(suffixName)) {
        self.$message.error('该后缀文件不允许上传');
        return false;
      }
      const fileSize = file.size;
      if (fileSize > dataObject.props.fileSize * 1024 * 1024) {
        self.$message.error('文件大小超出限制，请检查！');
        return false;
      }
    };
    //点击下载附件
    dataObject.attrs['on-preview'] = (res) => {
      const result = res.response.result;
      self.upDownLoad(result);
    };
    //上传成功后
    dataObject.attrs['on-success'] = (res, file, list) => {
      let fileList = [];
      list.forEach((f) => {
        let fl = {
          fileName: f.response.result.fileName,
          filePath: f.response.result.filePath
        };
        fileList.push(fl);
      });
      dataObject.props.value = fileList;
      self.$emit('input', fileList);
    };
    //删除后
    dataObject.attrs['on-remove'] = (file, list) => {
      let fileList = [];
      let fl = {
        fileName: '',
        filePath: ''
      };
      list.forEach((f) => {
        fl.fileName = f.response.result.fileName;
        fl.filePath = f.response.result.filePath;
        fileList.push(fl);
      });
      dataObject.props.value = fileList;
      self.$emit('input', fileList);
    };
  }
}

export default {
  render(h) {
    let dataObject = {
      attrs: {},
      props: {},
      on: {},
      style: {}
    };
    //远程获取数据
    this.getRemoteData();
    const confClone = jsonClone(this.conf);
    const children = childrenItem(h, confClone);
    Object.keys(confClone).forEach((key) => {
      const val = confClone[key];
      if (confClone.compType === 'upload') {
        if (dataObject[key]) {
          dataObject[key] = val;
        } else if (key === 'width') {
          dataObject.style = 'width:' + val;
        } else if (!isAttr(key)) {
          dataObject.props[key] = val;
        } else if (key === 'action') {
          dataObject.attrs[key] = '/api/' + this.Apis.uploadfiles + '?access_token=' + getToken();
        } else {
          if (key !== 'value') {
            dataObject.attrs[key] = val;
          } else {
            // 获取附件列表回显
            if (val != '') {
              var fileList = [];
              val.forEach((uploadFiles) => {
                var fliesObj = {
                  name: uploadFiles.fileName,
                  response: {
                    result: {
                      filePath: uploadFiles.filePath,
                      fileName: uploadFiles.fileName
                    }
                  }
                };
                fileList.push(fliesObj);
              });
              dataObject.props['file-list'] = fileList;
            }
          }
        }
      } else if (confClone.compType == 'dialogList') {
        if (dataObject[key]) {
          dataObject[key] = val;
        } else if (key === 'width') {
          dataObject.style = 'width:' + val;
        } else if (!isAttr(key)) {
          dataObject.props[key] = val;
        } else if (key == 'action') {
          dataObject.attrs[key] = '/api/' + this.Apis.getGenerWork + '?access_token=' + getToken();
        } else {
          if (key !== 'value') {
            dataObject.attrs[key] = val;
          }
        }
      } else {
        if (dataObject[key]) {
          dataObject[key] = val;
        } else if (key === 'width') {
          dataObject.style = 'width:' + val;
        } else if (!isAttr(key)) {
          dataObject.props[key] = val;
        } else {
          if (key !== 'value') {
            dataObject.attrs[key] = val;
          }
        }
      }
    });
    /*调整赋值模式，规避cascader组件赋值props会出现覆盖预制参数的bug */
    vModel(this, dataObject);
    return h(confClone.ele, dataObject, children);
  },
  props: ['conf', 'value'],
  mixins: [remoteData]
};
