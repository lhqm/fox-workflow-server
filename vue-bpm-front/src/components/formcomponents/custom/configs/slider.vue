<template>
  <div v-show="props.compType === 'slider'">
    <!-- <el-form-item label="字段名">
      <el-input class="input" v-model="props"></el-input>
    </el-form-item> -->
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
      <el-switch v-model="props.showLabel"></el-switch>
    </el-form-item>
    <el-form-item label="必填" label-width="80px">
      <el-switch v-model="props.required"></el-switch>
    </el-form-item>

    <el-form-item label="最小值" label-width="80px">
      <el-input-number v-model="props.min" :min="0"></el-input-number>
    </el-form-item>
    <el-form-item label="最大值" label-width="80px">
      <el-input-number v-model="props.max" placeholder="字符长度" />
    </el-form-item>
    <el-form-item label="步长" label-width="80px">
      <el-input-number v-model="props.step"></el-input-number>
    </el-form-item>
    <el-form-item label="范围选择" label-width="80px">
      <el-switch v-model="props.range"></el-switch>
    </el-form-item>
    <el-form-item label="显示断点" label-width="80px">
      <el-switch v-model="props['show-stops']"></el-switch>
    </el-form-item>
    <el-form-item label="显示提示消息" label-width="80px">
      <el-switch v-model="props['show-tooltip']"></el-switch>
    </el-form-item>
    <el-form-item label="禁用" label-width="80px">
      <el-switch v-model="props.disabled"></el-switch>
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
    methods: {},
    mounted() {}
  };
</script>
<style scoped>
  .input {
    /* width:75% */
  }
</style>
