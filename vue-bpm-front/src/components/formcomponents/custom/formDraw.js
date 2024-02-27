import { getName,getUserName } from '@/utils/auth';
//初始化data里面的数据（1、默认为空，2、之前存在的数据。。）
let itemDatas = {}
let itemListDatas= {};
export function datas(){
    itemDatas = {};
    itemListDatas=this.itemDataJson;
    this.itemList.forEach(val => {
        if(val.layout === 'rowItem'){ //row布局
            dataResolveRowItem(val);  //解析row布局
        }else if(val.layout === 'dynamicItem'){ //动态表单布局
            let obj =dataResolveDynamicItem(val);
            let array = [];
            array.push(obj);
            itemDatas[val.id] = array;
        }else{    //表单
            dataResolveColItem(val);
        }
    })
    Object.keys(itemDatas).forEach(key =>{
        this.$set(this.form, key, itemDatas[key]);
    })
}

function dataResolveRowItem(val){
    const columns = val.columns;
    columns.forEach(v =>{
        v.list.forEach(item =>{
            if(item.layout==='dynamicItem'){
                let obj =dataResolveDynamicItem(item);
                let array = [];
                array.push(obj);
                itemDatas[item.id] = array;
            }else{
                dataResolveColItem(item);
            }
        })
    })
}
function dataResolveDynamicItem(val){
    const columns = val.columns;

    let obj ={};
    columns.forEach(v =>{
        const key = v.id;
        obj[key] = v.value;
    })
    return obj;
}

function dataResolveColItem(val){
  if(itemListDatas==undefined){
    itemDatas[val.id]=val.value;
  }
  else{
    //itemDatas[val.id] = val.value;
    if(val.value=="userInfo.name"){
      itemDatas[val.id]=getName();
    }
    else if(val.value=="userInfo.userId"){
      itemDatas[val.id]=getUserName();
    }
    else {
      if (val.value == "" || val.value == undefined || val.value == null) {
        if (val.compType == "select") {
          if (itemListDatas[val.id] == undefined)
            itemDatas[val.id] = val.value;
          else
            itemDatas[val.id] = parseInt(itemListDatas[val.id]);
        } else {
          itemDatas[val.id] = itemListDatas[val.id];
        }

      } else
        itemDatas[val.id] = val.value;
    }
  }
}

export function addRow(element){
    let obj =dataResolveDynamicItem(element);
    this.form[element.id].push(obj);
}

export function deleteRow(scope,element){
    this.form[element.id].splice(scope.$index,1);
}

export function batchDeleteRow(indexs,element){
    for(let i =0;i<indexs.length;i++){
        const index = indexs[i];
        this.form[element.id].splice(index,1);
    }
}
