<template>
  <div class="preview">
    <el-row :gutter="formConf.gutter">
      <el-form
        :rules="rules"
        :ref="formConf.formModel"
        :size="formConf.size"
        :model="form"
        :label-position="formConf.labelPosition"
        :disabled="formConf.disabled"
        :validate-on-rule-change="false"
        label-width="formConf.labelWidth + 'px'"
        class="formStyle"
      >
        <template v-for="(element, index) in list">
          <!-- <el-input v-model="element.id" placeholder=""></el-input> -->
          <preview-row-item
            v-if="element.compType === 'row'"
            :key="'row-' + index"
            :model="element"
          >
            <el-col v-for="column in element.columns" :key="column.index" :span="column.span">
              <template v-for="col in column.list">
                <preview-item
                  v-if="col.compType !== 'dynamicTable'"
                  :key="col.id"
                  :model="col"
                  :conlist="list"
                  v-model="form[col.id]"
                  @valChange="handlerValChange"
                />
                <fancy-dynamic-table
                  v-else-if="col.compType === 'dynamicTable'"
                  ref="dynamicTable"
                  :key="'dynamic-' + col.index"
                  :data="form[col.id]"
                  :conf="col"
                  @addRow="handlerAddRow"
                  @deleteRow="handlerDeleteRow"
                  @batchDeleteRow="handlerBatchDeleteRow"
                >
                  <template v-slot:item="{ rowScope, item }">
                    <fancy-dynamic-table-item
                      :model="item"
                      :parent="col"
                      :key="'tableIndex-' + rowScope.$index"
                      :index="rowScope.$index"
                      v-model="rowScope.row[item.id]"
                      @valChange="handlerDynamicValChange"
                    />
                  </template>
                </fancy-dynamic-table>
              </template>
            </el-col>
          </preview-row-item>
          <fancy-dynamic-table
            v-else-if="element.compType === 'dynamicTable'"
            :key="'dynamic-' + index"
            :data="form[element.id]"
            :ref="element.id"
            :conf="element"
            @addRow="handlerAddRow"
            @deleteRow="handlerDeleteRow"
            @batchDeleteRow="handlerBatchDeleteRow"
          >
            <template v-slot:item="{ rowScope, item }">
              <fancy-dynamic-table-item
                :model="item"
                :ref="item.id + rowScope.$index"
                :parent="element"
                :key="'tableIndex-' + rowScope.$index"
                :index="rowScope.$index"
                v-model="rowScope.row[item.id]"
                @valChange="handlerDynamicValChange"
              />
            </template>
          </fancy-dynamic-table>
          <fancy-edit-table
            v-else-if="element.compType === 'table'"
            :key="'table-' + index"
            :layoutArray="element.layoutArray"
            :tdStyle="element.tdStyle"
            :width="element.width"
          >
            <template v-slot="{ td }">
              <template v-for="col in td.columns">
                <preview-item
                  v-if="col.compType !== 'dynamicTable'"
                  :key="col.id"
                  :model="col"
                  v-model="form[col.id]"
                  @valChange="handlerValChange"
                />
              </template>
            </template>
          </fancy-edit-table>
          <!--item-->
          <el-col class="drag-col-wrapper" :key="index" :span="element.span" v-else>
            <div v-if="element.compType == 'text'" class="m-border"></div>
            <preview-item
              :model="element"
              :conlist="list"
              v-model="form[element.id]"
              @valChange="handlerValChange"
            />
          </el-col>
        </template>
      </el-form>
    </el-row>
  </div>
</template>
<script>
  import previewItem from './previewItem';
  import previewRowItem from './previewRowItem';
  import fancyDynamicTable from './dynamic/fancyDynamicTable';
  import fancyDynamicTableItem from './dynamic/fancyDynamicTableItem';
  import fancyEditTable from './table/fancyEditTable';
  import { datas, addRow, batchDeleteRow, deleteRow } from './custom/formDraw';
  export default {
    name: 'preview',
    props: ['itemList', 'formConf', 'itemDataJson'],
    components: {
      previewItem,
      previewRowItem,
      fancyDynamicTable,
      fancyDynamicTableItem,
      fancyEditTable
    },
    data() {
      return {
        list: this.itemList,
        form: {},
        rules: {},
        currentIndex: -1
      };
    },
    methods: {
      saveSelf() {
        const tableData = {
          keyList: this.itemList,
          keyValueList: this.form
        };
        return tableData;
      },
      handlerValChange(key, origin) {
        this.$set(this.form, key, origin);
      },
      handlerDynamicValChange(parentId, index, key, origin) {
        this.$set(this.form[parentId][index], key, origin);
        this.currentIndex = index;
      },
      handlerSubForm() {
        this.$refs[this.formConf.formModel].validate((valid) => {
          if (valid) {
            this.$message.success('success');
          }
        });
      },
      handlerAddRow: addRow,
      handlerDeleteRow: deleteRow,
      handlerBatchDeleteRow: batchDeleteRow,
      handlerInitDatas: datas
    },
    created() {
      this.handlerInitDatas(); //初始化表单
      //表单赋值
      if (this.itemList == '') {
        return;
      } else {
        if (this.itemDataJson == undefined) return;
        let subColumns = this.itemList.filter((col) => {
          if (col.compType == 'dynamicTable') {
            if (
              this.itemDataJson[col.id] != '' &&
              this.itemDataJson[col.id] != null &&
              this.itemDataJson[col.id] != undefined
            ) {
              this.form[col.id] = this.itemDataJson[col.id];
            }
            return col;
          } else {
            if (col.compType == 'row') {
              let rows = col.columns;
              let colRows = rows.filter((row) => {
                let rowList = row.list;
                let colRowList = rowList.filter((rl) => {
                  if (rl.compType == 'dynamicTable') {
                    if (
                      this.itemDataJson[rl.id] != '' &&
                      this.itemDataJson[rl.id] != null &&
                      this.itemDataJson[rl.id] != undefined
                    ) {
                      this.form[rl.id] = this.itemDataJson[rl.id];
                    }
                  }
                  return col;
                });
              });
              return col;
            } else {
              return col;
            }
          }
        });
      }
    },
    mounted() {
      this.$nextTick(() => {});
    },
    beforeCreate() {},
    computed: {}
  };
</script>
<style scoped>
  .preview-board {
    border: 1px dashed #ccc;
  }
  .preview {
    margin-left: 7.5px;
    margin-right: 7.5px;
  }
  /*定义滚动条高宽及背景 高宽分别对应横竖滚动条的尺寸*/
  ::v-deep .preview::-webkit-scrollbar {
    width: 6px;
    /*滚动条宽度*/
    height: 12px;
    /*滚动条高度*/
    background-color: white;
  }

  /*定义滑块 内阴影+圆角*/
  ::-webkit-scrollbar-thumb {
    box-shadow: inset 0 0 0px white;
    -webkit-box-shadow: inset 0 0 0px white;
    background-color: rgb(193, 193, 193);
    /*滚动条的背景颜色*/
    border-radius: 20px;
  }
  .preview >>> .el-radio.is-bordered + .el-radio.is-bordered {
    margin-left: 0px;
  }
  .preview >>> .el-checkbox.is-bordered + .el-checkbox.is-bordered {
    margin-left: 0px;
  }
</style>
