<template>
  <div v-show="props.compType === 'link'">
    <!-- <el-form-item label="字段名">
      <el-input class="input" v-model="props"></el-input>
    </el-form-item> -->
    <el-form-item label="ID" label-width="80px">
      <el-tooltip class="item" effect="dark" content="请注意,ID的修改可能会导致该组件相关事件失效！" placement="left">
        <el-input class="input" v-model="props.id"  @change="handlerChangeId" ></el-input>
      </el-tooltip>
    </el-form-item>
    <el-form-item label="栅格" label-width="80px">
        <el-slider class="input" v-model="props.span" :max="24" :min="1" :marks="{12:''}"></el-slider>
      </el-form-item>
    <el-form-item label="链接文字" label-width="80px">
      <el-input class="input" v-model="props.text"></el-input>
    </el-form-item>
    <el-form-item label="类型" label-width="80px">
      <el-select v-model="props.type" placeholder="请选择">
        <el-option label="primary" value="primary"/>
        <el-option label="success" value="success"/>
        <el-option label="warning" value="warning"/>
        <el-option label="danger" value="danger"/>
        <el-option label="info" value="info"/>
      </el-select>
    </el-form-item>
    <el-form-item label="连接地址" label-width="80px">
      <el-input class="input" v-model="props.href"></el-input>
    </el-form-item>
    <el-form-item label="禁用" label-width="80px">
      <el-switch v-model="props.disabled"></el-switch>
    </el-form-item>
    <el-form-item label="是否显示" label-width="80px">
      <el-switch v-model="props.isShow"></el-switch>
    </el-form-item>
    <el-form-item label="新窗口" label-width="80px">
      <el-switch v-model="isNewWindow" @change="handlerChangeTargetValue"></el-switch>
    </el-form-item>
  </div>
</template>
<script>
import {changeId} from '../mixin'
export default {
  name:"buttonConfig",
  props:{
    props:{}
  },
  components:{
  },
  mixins:[changeId],
  data(){
    return {
      isNewWindow:true
    }
  },
  methods:{
    handlerShowLabel(val){
      if(val){
        this.props.labelWidth = 80;
      }else{
        this.props.labelWidth = 0;
      }
    },
    handlerChangeTargetValue(val){
      if(val){
        this.props.target = '_blank';
      }else{
        this.props.target = '_self';
      }
    }
  },
  mounted(){
  }
}
</script>
