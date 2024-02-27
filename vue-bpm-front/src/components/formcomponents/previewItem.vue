<script>
import render from './custom/previewRender'
import checkRules from './custom/rule';

const layouts = {
  colItem(h, element,eleList,value) {
    let labelWidth = element.labelWidth ? `${element.labelWidth}px` : null
    let style=null
    let borderStyle=`padding-left: 7.5px; padding-right: 7.5px;`
    if(element.compType=='button'){
      style=element.align ? `text-align:${element.align}` : null
    }
    const {valChange} = this.$listeners;
    const rules = checkRules(element);
    if(element.compType=='dialogList'){
      return (
        <el-col  style={borderStyle}>
          <el-form-item
            style={style}
            label={element.showLabel ? element.label : ''}
            label-width={labelWidth}
            v-show={element.isShow}
            prop={element.id}
            rules={rules}
          >
            <render key={element.id} conf={element} value={value} onInput={ event => {
              var returnVal=JSON.parse(event);
              this.$set(element,'value',returnVal.inputVal);
              valChange(element.id,returnVal.inputVal);

              //如果设置了联动填充
              if(returnVal.linkConfig!='') {
                //获取填充字段设置
                var linkConf = JSON.parse(returnVal.linkConfig);
                //选中行的值
                let rowItem = returnVal.rowItem;
                for(let i=0;i<linkConf.length;i++)
                {
                  var linkData = rowItem[linkConf[i].property];
                  var linkKey = linkConf[i].label;
                  eleList.filter((item)=>{
                    if(item.compType=="dynamicTable"){
                      if(item.id==linkKey){
                        this.$set(item,'value',linkData);
                        valChange(linkKey, linkData);
                      }
                      return item;
                    }
                    else{
                      if(item.compType=="row"){
                        let rows=item.columns;
                        let colRows=rows.filter((row)=>{
                          let rowList=row.list;
                          let colRowList=rowList.filter((rl)=>{
                            if(rl.id==linkKey){
                              this.$set(rl,'value',linkData);
                              valChange(linkKey, linkData);
                            }
                            return item;
                          });
                        });
                        return item;
                      }
                      else{
                        if(item.id==linkKey){
                          if(item.compType=="radio"){
                            this.$set(item,'value',parseInt(linkData));
                            valChange(linkKey, parseInt(linkData));
                          }
                          else{
                            this.$set(item,'value',linkData);
                            valChange(linkKey, linkData);
                          }
                        }
                        return item;
                      }
                    }

                  });
                };
              }
            }}/>
          </el-form-item>

        </el-col>
      )
    }
    else {
      return (
        <el-col style={borderStyle}>
          <el-form-item
            style={style}
            label={element.showLabel ? element.label : ''}
            label-width={labelWidth}
            v-show={element.isShow}
            prop={element.id}
            rules={rules}
          >
            <render key={element.id} conf={element} value={value} onInput={event => {
              this.$set(element, 'value', event);
              valChange(element.id, event);
            }}/>
          </el-form-item>

        </el-col>
      )
    }
  }
}

export default {
  name:"previewItem",
  components: {
    render
  },
  props: ['model','value','conlist'],
  data(){
    return {
      eleConfig:this.model,
      modelListConfig:this.conlist
    }
  },
  render(h) {
    return layouts.colItem.call(this, h, this.eleConfig,this.modelListConfig,this.value)
  }
}
</script>
