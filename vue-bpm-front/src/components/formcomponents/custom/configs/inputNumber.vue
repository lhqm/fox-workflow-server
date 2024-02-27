<template>
  <div v-show="props.compType === 'inputNumber'">
    <el-form-item label="ID" label-width="80px">
      <el-tooltip
        class="item"
        effect="dark"
        content="请注意,ID的修改可能会导致该组件相关事件失效！"
        placement="left"
      >
        <el-input class="input" v-model="props.id" @change="handlerChangeId"></el-input>
      </el-tooltip>
    </el-form-item>
    <el-form-item label="标题" label-width="80px">
      <el-input class="input" v-model="props.label"></el-input>
    </el-form-item>
    <el-form-item label="表单栅格" label-width="80px">
      <el-slider
        class="input"
        v-model="props.span"
        :max="24"
        :min="1"
        :marks="{ 12: '' }"
      ></el-slider>
    </el-form-item>
    <el-form-item label="栅格间隔" label-width="80px">
      <el-input-number v-model="props.gutter" :min="0"></el-input-number>
    </el-form-item>
    <el-form-item label="标签宽度" label-width="80px">
      <el-input-number v-model="props.labelWidth" :min="1" :max="200"></el-input-number>
    </el-form-item>
    <el-form-item label="显示标签" label-width="80px">
      <el-switch v-model="props.showLabel" @change="handlerChangeLabel"></el-switch>
    </el-form-item>
    <el-form-item label="必填" label-width="80px">
      <el-switch v-model="props.required"></el-switch>
    </el-form-item>
    <el-form-item label="最小值" label-width="80px">
      <el-input-number v-model="props.min"></el-input-number>
    </el-form-item>
    <el-form-item label="最大值" label-width="80px">
      <el-input-number v-model="props.max"></el-input-number>
    </el-form-item>
    <el-form-item label="步长" label-width="80px">
      <el-input-number v-model="props.step"></el-input-number>
    </el-form-item>
    <el-form-item label="精度" label-width="80px">
      <el-input-number v-model="props.precision" :min="0" :max="10"></el-input-number>
    </el-form-item>
    <el-form-item label="按钮位置" label-width="80px">
      <el-radio-group v-model="props['controls-position']">
        <el-radio-button label="default">默认</el-radio-button>
        <el-radio-button label="right">右侧</el-radio-button>
      </el-radio-group>
    </el-form-item>
    <el-form-item label="禁用" label-width="80px">
      <el-switch v-model="props.disabled" @change="handlerChangeDisStatus('disabled')"></el-switch>
    </el-form-item>
    <el-form-item label="是否显示" label-width="80px">
      <el-switch v-model="props.isShow"></el-switch>
    </el-form-item>
    <el-form-item label="默认值" label-width="80px">
      <el-input class="input" v-model="props.value"></el-input>
    </el-form-item>
  </div>
</template>
<script>
  import { changeId } from '../mixin';
  export default {
    name: 'inputConfig',
    props: ['props', 'getFormId'],
    components: {},
    mixins: [changeId],
    data() {
      return {};
    },
    methods: {
      handlerChangeLabel(val) {
        this.props.labelWidth = val ? '80' : '1';
      },
      handlerChangeDisStatus(val) {
        this.props.readOnly = val ? false : true;
      },
      handlerChangeReadStatus(val) {
        this.props.disabled = val ? false : true;
      }
    },
    mounted() {}
  };
</script>
<style scoped>
  .input {
    /* width:75% */
  }
</style>
