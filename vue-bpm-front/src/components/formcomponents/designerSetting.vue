<template>
  <!--中间面板-->
  <div class="center-board">
    <el-scrollbar class="center-scrollbar">
      <el-row class="center-board-row" :gutter="formConf.gutter">
        <el-form
          :size="formConf.size"
          :label-position="formConf.labelPosition"
          :disabled="formConf.disabled"
          :label-width="formConf.labelWidth + 'px'"
        >
          <draggable
            class="drawing-board"
            :list="list"
            :animation="100"
            group="componentsGroup"
            draggable=".drawing-item"
          >
            <design-item
              v-for="(element, index) in list"
              :key="index"
              :model="element"
              :activeItem="activeItem"
              @rowItemRollBack="handlerRollBack"
              @onActiveItemChange="handlerActiveItemChange"
              @copyItem="handlerItemCopy"
              @deleteItem="handlerItemDelete"
            />
          </draggable>
          <div v-show="infoShow" class="empty-info">
            <el-empty description="从左侧拖拽添加控件"></el-empty>
          </div>
        </el-form>
      </el-row>
    </el-scrollbar>
    <config-panel :activeItem="activeItem" :itemList="list" />
  </div>
</template>
<script>
import draggable from 'vuedraggable';
import SelectTree from '@/components/selectTree/index.vue';
import configPanel from './configPanel';
import designItem from './designItem';
import { getSimpleId } from './utils/IdGenerate';
import { isLayout, isTable, inTable } from './utils/index';
import { formSortList } from '@/api/process/processFrom';
import formConf from './custom/formConf';
import preview from './preview';
import codemirror  from 'vue-codemirror';
// 核心样式
import 'codemirror/lib/codemirror.css';
// 引入主题后还需要在 options 中指定主题才会生效
import 'codemirror/theme/dracula.css';
import 'codemirror/mode/javascript/javascript';

export default {
  name: 'designerSetting',
  components: {
    draggable,
    configPanel,
    designItem,
    preview,
    codemirror,
    SelectTree
  },
  props: {
    list: {
      type: Array,
      default() {
        return [];
      }
    },
    formConfig: Object,
    formId: String
  },
  data() {
    return {
      formConf: this.formConfig,
      activeItem: {},
      sortList:[],
      lastActiveItem: {},
      formConfVisible: false,
      previewVisible: false,
      JSONVisible: false,
      itemList: [],
      newList: this.list,
      formid: this.formId,
      activeName: 'formConf',
      editorCode: '',
      viewCode: '',
      // 默认配置
      options: {
        tabSize: 2, // 缩进格式
        theme: 'dracula', // 主题，对应主题库 JS 需要提前引入
        lineNumbers: true, // 显示行号
        line: true,
        styleActiveLine: true, // 高亮选中行
        hintOptions: {
          completeSingle: true // 当匹配只有一项的时候是否自动补全
        }
      }
    };
  },
  mounted() {
    this.getSelectData();
  },
  methods: {
    saveMap() {
      return this.code;
    },
    async getSelectData() {
      const data = (await formSortList()).result;
      if (data.length) {
        this.sortList = data;
      }
    },
    preview() {
      const clone = JSON.parse(JSON.stringify(this.list));
      this.itemList = clone;
      this.previewVisible = true;
    },
    handlerActiveItemChange(obj) {
      this.lastActiveItem = this.activeItem;
      this.activeItem = obj;
    },
    handlerItemCopy(origin, parent) {
      if (isLayout(origin)) {
        //布局组件，需要复制布局组件以及下面的组件
        const clone = JSON.parse(JSON.stringify(origin));
        const uId = 'row_' + getSimpleId();
        console.log(uId);
        clone.id = uId;
        clone._id = uId;
        clone.columns.map((column) => {
          let itemList = [];
          column.list.map((item, i) => {
            const cloneitem = JSON.parse(JSON.stringify(item));
            const uId = 'fd_' + getSimpleId();
            cloneitem.id = uId;
            cloneitem._id = uId;
            itemList.push(cloneitem);
          });
          column.list = [];
          column.list = itemList;
        });
        this.newList.push(clone);
        this.handlerActiveItemChange(clone);
      } else {
        //如果是普通组件，需要判断他是否再布局组件下。
        if (parent) {
          if (inTable(parent)) {
            //增加表格组件的支持
            if (parent.columns.some((item) => item.id === origin.id)) {
              const clone = JSON.parse(JSON.stringify(origin));
              const uId = 'fd_' + getSimpleId();
              clone.id = uId;
              clone._id = uId;
              parent.columns.push(clone);
              this.handlerActiveItemChange(clone);
            }
          } else {
            parent.columns.map((column) => {
              if (column.list.some((item) => item.id === origin.id)) {
                const clone = JSON.parse(JSON.stringify(origin));
                const uId = 'fd_' + getSimpleId();
                clone.id = uId;
                clone._id = uId;
                column.list.push(clone);
                this.handlerActiveItemChange(clone);
              }
            });
          }
        } else {
          const clone = JSON.parse(JSON.stringify(origin));
          const uId = 'fd_' + getSimpleId();
          clone.id = uId;
          clone._id = uId;
          this.newList.push(clone);
          this.handlerActiveItemChange(clone);
        }
      }
    },
    handlerItemDelete(origin, parent) {
      console.log(origin);
      console.log(parent);
      if (isLayout(origin) || isTable(origin)) {
        //如果是布局组件,则直接删除
        const index = this.list.findIndex((item) => item.id === origin.id);
        this.newList.splice(index, 1);
      } else {
        //如果不是布局组件，则先判断是不是再布局内部，如果不是，则直接删除就可以，如果是，则要在布局内部删除
        if (parent) {
          if (inTable(parent)) {
            //增加表格组件的支持
            const colIndex = parent.columns.findIndex((item) => item.id === origin.id);
            if (colIndex > -1) {
              parent.columns.splice(colIndex, 1);
            }
          } else {
            parent.columns.map((column) => {
              const colIndex = column.list.findIndex((item) => item.id === origin.id);
              if (colIndex > -1) {
                column.list.splice(colIndex, 1);
              }
            });
          }
        } else {
          const index = this.list.findIndex((item) => item.id === origin.id);
          this.newList.splice(index, 1);
        }
      }
    },
    handlerSaveFormConf() {
      this.saveMap();
      this.formConfVisible = false;
    },
    handlerRollBack(rowItem, oldIndex) {
      //还原
      this.newList.splice(oldIndex, 0, rowItem);
    },
    handlerSetJson() {
      this.$emit('updateJSON', this.viewCode);
      this.JSONVisible = false;
    }
  },
  computed: {
    infoShow() {
      return this.list.length < 1;
    },
    code() {
      let json = {};
      json.config = this.formConf;
      json.list = this.list;
      return JSON.stringify(json, null, 4);
    }
  },
  watch: {
    activeItem(newValue, oldValue) {
      this.lastActiveItem = oldValue;
    }
  }
};
</script>
<style scoped>
.v-modal {
  z-index: 2000 !important;
}

.el-rate {
  display: inline-block;
}
.center-scrollbar >>> .el-scrollbar__bar.is-horizontal {
  display: none;
}
.center-scrollbar >>> .el-scrollbar__wrap {
  overflow-x: hidden;
}
.empty-info >>> .el-empty__description p {
  color: #ccb1ea;
  font-size: 16px;
}
.drawing-board >>> .el-radio.is-bordered + .el-radio.is-bordered {
  margin-left: 0px;
}
.drawing-board >>> .el-checkbox.is-bordered + .el-checkbox.is-bordered {
  margin-left: 0px;
}
</style>
<style lang="scss">
@import './style/designer.scss';
</style>
<style>
@import './style/designer.css';
</style>
