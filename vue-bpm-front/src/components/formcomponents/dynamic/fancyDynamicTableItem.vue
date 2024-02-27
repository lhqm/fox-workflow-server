<script>
  import render from '../custom/previewRender';
  import checkRules from '../custom/rule';
  const layouts = {
    colItem(element, value, parent, index) {
      const { valChange } = this.$listeners;
      let rules = [];
      if (element.compType == 'upload') {
        return (
          <el-form-item
            label={''}
            label-width={'0px'}
            prop={parent.id + '.' + index + '.' + element.id}
            rules={rules}
          >
            <render
              key={element.id}
              conf={element}
              value={value}
              onInput={(event) => {
                this.eleValue = event;
                valChange(this.eleParent.id, this.index, element.id, this.eleValue);
              }}
            />
            {element.required ? <span style="color:#F56C6C">*</span> : ''}
          </el-form-item>
        );
      } else if (element.compType == 'dialogList') {
        return (
          <el-form-item
            label={''}
            label-width={'0px'}
            prop={parent.id + '.' + index + '.' + element.id}
            rules={rules}
          >
            <render
              key={element.id}
              conf={element}
              value={value}
              elParent={parent}
              elIndex={index}
              onInput={(event) => {
                var returnVal = JSON.parse(event);
                this.eleValue = returnVal.inputVal;
                //this.$set(element,'value',returnVal.inputVal);
                valChange(parent.id, index, element.id, returnVal.inputVal);

                //如果设置了联动填充
                if (returnVal.linkConfig != '') {
                  //获取填充字段设置
                  var linkConf = JSON.parse(returnVal.linkConfig);
                  //选中行的值
                  let rowItem = returnVal.rowItem;
                  for (let i = 0; i < linkConf.length; i++) {
                    var linkData = rowItem[linkConf[i].property];
                    var linkKey = linkConf[i].label;
                    valChange(parent.id, index, linkKey, linkData);
                  }
                }
              }}
            />
            {element.required ? <span style="color:#F56C6C">*</span> : ''}
          </el-form-item>
        );
      } else {
        if (element && element.rules) {
          rules = rules.concat(checkRules(element));
          return (
            <el-form-item
              label={''}
              label-width={'0px'}
              prop={parent.id + '.' + index + '.' + element.id}
              rules={rules}
            >
              <render
                key={element.id}
                conf={element}
                value={value}
                elParent={parent}
                elIndex={index}
                onInput={(event) => {
                  this.eleValue = event;
                  this.$set(element, 'value', event);
                  valChange(parent.id, index, element.id, event);
                }}
              />
              {element.required ? <span style="color:#F56C6C">*</span> : ''}
            </el-form-item>
          );
        }
      }
    }
  };

  export default {
    name: 'fancyDynamicTableItem',
    components: {
      render
    },
    props: ['model', 'value', 'parent', 'index'],
    mounted() {
      let __eleConfig = {};
      Object.assign(__eleConfig, this.model);
      this.eleConfig = __eleConfig;
    },
    data() {
      return {
        eleConfig: {},
        eleParent: this.parent,
        eleValue: this.value
      };
    },
    render() {
      return layouts.colItem.call(this, this.eleConfig, this.eleValue, this.eleParent, this.index);
    },
    watch: {
      value(newVal) {
        this.eleValue = newVal;
      }
    }
  };
</script>
