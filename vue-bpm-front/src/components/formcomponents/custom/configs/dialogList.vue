<template>
  <div v-show="props.compType === 'dialogList'" class="dialogList">
    <el-collapse v-model="activePanel" accordion>
      <el-collapse-item title="基础设置" name="1">
        <el-form-item label="ID" label-width="80px">
          <el-tooltip
            class="item"
            effect="dark"
            content="请注意,ID的修改可能会导致该组件相关事件失效！"
            placement="left"
          >
            <el-input class="input" v-model="props.id"></el-input>
          </el-tooltip>
        </el-form-item>
        <el-form-item label="标题" label-width="80px">
          <el-input class="input" v-model="props.label"></el-input>
        </el-form-item>
        <el-form-item label="栅格" label-width="80px">
          <el-slider
            class="input"
            v-model="props.span"
            :max="24"
            :min="1"
            :marks="{ 12: '' }"
          ></el-slider>
        </el-form-item>
        <el-form-item label="标签宽度" label-width="80px">
          <el-input-number v-model="props.labelWidth" :min="1" :max="200"></el-input-number>
        </el-form-item>
        <el-form-item label="显示标签" label-width="80px">
          <el-switch v-model="props.showLabel"></el-switch>
        </el-form-item>
        <el-form-item label="必填" label-width="80px">
          <el-switch v-model="props.required"></el-switch>
        </el-form-item>
        <el-form-item label="禁用" label-width="80px">
          <el-switch v-model="props.disabled"></el-switch>
        </el-form-item>
        <el-form-item label="是否显示" label-width="80px">
          <el-switch v-model="props.isShow"></el-switch>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="选择设置" name="2">
        <el-form-item label="标题" label-width="80px">
          <el-input v-model="props.title"></el-input>
        </el-form-item>
<!--        <el-form-item label="默认值" label-width="80px">-->
<!--          <el-input class="input" v-model="props.value"></el-input>-->
<!--        </el-form-item>-->
        <el-form-item label="数据类型" label-width="80px">
          <el-radio-group v-model="props.dataType">
            <el-radio-button label="static">内置表单数据</el-radio-button>
            <el-radio-button label="dynamic">动态数据</el-radio-button>
          </el-radio-group>
        </el-form-item>
        <div v-show="props.dataType === 'static'">
          <el-form-item label="内置表单" label-width="80px">
            <treeselect
              v-model="props.formKey"
              :options="formTree"
              :normalizer="normalizer"
              @select="setSelectedTree"
              placeholder="选择表单模版"
            />
          </el-form-item>
        </div>
        <div v-show="props.dataType === 'dynamic'">
          <el-form-item label="请求地址" label-width="80px">
            <el-input v-model="props.action"></el-input>
          </el-form-item>
        </div>

        <el-form-item label="多选" label-width="80px">
          <el-switch v-model="props.multi"></el-switch>
        </el-form-item>
        <el-form-item label="显示序号" label-width="80px">
          <el-switch v-model="props.showIndex"></el-switch>
        </el-form-item>
        <el-form-item label="表格高度" label-width="80px">
          <el-input-number
            v-model="props.height"
            :step="10"
            :max="1500"
            :min="100"
          ></el-input-number>
        </el-form-item>
        <el-form-item label="主键字段" label-width="80px">
          <el-tooltip
            class="item"
            effect="dark"
            placement="left"
          >
            <div slot="content">
              <span>主键字段，必须是返回列表中的主键或唯一值，不可重复</span><br/>
              <span>主键字段与回填字段的关系，可以理解为：</span><br/>
              <span>select 回填字段 from xxx where Key='主键字段'</span>
            </div>
            <el-input v-model="props.dval"></el-input>
          </el-tooltip>
        </el-form-item>
        <el-form-item label="回填字段" label-width="80px">
          <el-tooltip
            class="item"
            effect="dark"
            placement="left"
          >
            <div slot="content">
              <span>主键字段与回填字段的关系，可以理解为：</span><br/>
              <span>select 回填字段 from xxx where Key='主键字段'</span>
            </div>
            <el-input v-model="props.dlabel"></el-input>
          </el-tooltip>
        </el-form-item>
      </el-collapse-item>
      <el-collapse-item title="字段配置(双击列删除)" name="3">
        <el-table :data="colOptions" @row-dblclick="handlerDeleteRow">
          <el-table-column property="label" label="名称" align="center" />
          <el-table-column property="property" label="字段id" align="center" />
          <el-table-column property="width" label="宽度" align="center" width="70" />
          <el-table-column label="显示"> </el-table-column>
        </el-table>
        <br />
        <el-alert
          title="名称和字段id不能为空,请检查"
          v-show="alertShow"
          type="error"
          :closable="false"
        />
        <el-alert
          title="字段id已存在请检查"
          v-show="propertyExistShow"
          type="error"
          :closable="false"
        />
        <br />
        <el-form-item label="名称" label-width="80px">
          <el-input v-model="dLabel" />
        </el-form-item>
        <el-form-item label="字段id" label-width="80px">
          <el-input v-model="dProperty" />
        </el-form-item>
        <el-form-item label="宽度" label-width="80px">
          <el-input-number v-model="dWidth" />
        </el-form-item>
        <el-form-item label="显示" label-width="80px">
          <el-switch v-model="dShow" />
        </el-form-item>
        <div style="margin-left: 20px">
          <el-button
            style="padding-bottom: 0"
            icon="el-icon-circle-plus-outline"
            type="text"
            @click="addColItem"
          >
            添加选项
          </el-button>
        </div>
      </el-collapse-item>
      <el-collapse-item title="数值联动(双击列删除)" name="4">
        <el-table :data="linkOptions" @row-dblclick="handlerDeleteLinkRow">
          <el-table-column property="label" label="填充字段" align="center" />
          <el-table-column property="property" label="对应id" align="center" />
<!--          <el-table-column property="width" label="宽度" align="center" width="70" />-->
<!--          <el-table-column label="显示"> </el-table-column>-->
        </el-table>
        <br />
        <el-alert
          title="字段和属性不能为空,请检查"
          v-show="alertLinkShow"
          type="error"
          :closable="false"
        />
        <el-alert
          title="属性已存在请检查"
          v-show="propertyExistLinkShow"
          type="error"
          :closable="false"
        />
        <br />
        <el-form-item label="填充字段" label-width="80px">
          <el-input v-model="dLinkLabel" />
        </el-form-item>
        <el-form-item label="对应值" label-width="80px">
          <el-input v-model="dLinkProperty" />
        </el-form-item>
        <div style="margin-left: 20px">
          <el-button
            style="padding-bottom: 0"
            icon="el-icon-circle-plus-outline"
            type="text"
            @click="addColLinkItem"
          >
            添加选项
          </el-button>
        </div>
      </el-collapse-item>
    </el-collapse>
  </div>
</template>
<script>
import treeselect from "@riophae/vue-treeselect";
import {dataJson, handleTree} from "@/utils/comm";
import "@riophae/vue-treeselect/dist/vue-treeselect.css";
import {getFormTree} from "@/api/process/processFrom";
  /**
   * text的配置项
   */
  export default {
    name: 'textConfig',
    components: { treeselect },
    props: {
      props: {}
    },
    data() {
      return {
        activePanel: '1',
        //表单树
        formTree:[],
        //显示的字段
        colOptions: [],
        //填充的字段
        linkOptions: [],
        dLabel: '',
        dProperty: '',
        dWidth: 150,
        dShow: true,
        dLinkLabel: '',
        dLinkProperty: '',
        alertShow: false,
        alertLinkShow: false,
        propertyExistShow: false,
        propertyExistLinkShow: false
      };
    },
    methods: {
      addColItem() {
        if (this.dLabel !== '' && this.dProperty !== '') {
          const existOptions = this.colOptions.find((item) => item.property === this.dProperty);
          if (typeof existOptions === 'undefined') {
            this.alertShow = false;
            this.propertyExistShow = false;
            const obj = {};
            obj.index = this.colOptions.length;
            obj.show = this.dShow;
            obj.label = this.dLabel;
            obj.property = this.dProperty;
            obj.width = this.dWidth;
            this.colOptions.push(obj);
            this.resetFields();
          } else {
            this.propertyExistShow = true;
          }
        } else {
          this.alertShow = true;
        }
      },
      addColLinkItem() {
        if (this.dLinkLabel !== '' && this.dLinkProperty !== '') {
          const existOptions = this.linkOptions.find(
            (item) => item.property === this.dLinkProperty
          );
          if (typeof existOptions === 'undefined') {
            this.alertLinkShow = false;
            this.propertyExistLinkShow = false;
            const obj = {};
            obj.index = this.linkOptions.length;
            obj.label = this.dLinkLabel;
            obj.property = this.dLinkProperty;

            this.linkOptions.push(obj);
            this.resetFields();
          } else {
            this.propertyExistLinkShow = true;
          }
        } else {
          this.alertShow = true;
        }
      },
      resetFields() {
        this.dLabel = '';
        this.dProperty = '';
        this.dWidth = 150;
        this.dShow = true;

        this.dLinkLabel = '';
        this.dLinkProperty = '';
        this.dLinkWidth = 150;
        this.dLinkShow = true;
      },
      handlerDeleteRow(row) {
        let index = this.colOptions.findIndex((item) => item.property == row.property);
        this.colOptions.splice(index, 1);
      },
      handlerDeleteLinkRow(row) {
        let index = this.linkOptions.findIndex((item) => item.property == row.property);
        this.linkOptions.splice(index, 1);
      },
      // 获取内置表单下拉框数据
      getFormTreeData(){
        getFormTree().then(res=>{
          this.formTree=handleTree(res.result, "id","formSort");
          //this.formTreeCopy=this.formTree;
        });
      },
      normalizer(node) {
        if (node.children && !node.children.length) {
          delete node.children;
        }
        return {
          id: node.id,
          label: node.name,
          children: node.children
        };
      },
      setSelectedTree(node, selectedOptions){
        if(node.form_json==""||node.form_json==undefined){
          this.$message.warning("请选择具体的表单")
          return;
        }
        // this.mapJson=node.form_json;
        // this.formKeysData=dataJson(JSON.parse(this.mapJson));
      },
    },
    mounted() {
      this.$nextTick(() => {
        this.getFormTreeData();
        this.colOptions = this.colOptions.concat(JSON.parse(this.props.colConf));
        if (this.props.linkConf != '')
          this.linkOptions = this.linkOptions.concat(JSON.parse(this.props.linkConf));
      });
    },
    watch: {
      colOptions(newVal) {
        this.props.colConf = JSON.stringify(newVal);
      },
      linkOptions(newVal) {
        this.props.linkConf = JSON.stringify(newVal);
      }
    }
  };
</script>
<style scoped>
  .dialogList >>> .el-collapse-item__header {
    /* background-color: #f4f6fc; */
    padding-left: 0px;
  }
  /*.dialogList >>> .el-collapse-item__header {*/
  /*  height: 35px;*/
  /*}*/
</style>
