<template>
  <el-col>
    <el-row  :gutter="model.gutter">
      <el-card style="margin-top: 5px;">
        <div class="drag-wrapper">
          <slot></slot>
        </div>
      </el-card>
    </el-row>
  </el-col>
</template>
<script>



export default {
  name:"previewRowItem",
  components: {
  },
  props: ['model','value'],
  data(){
    return {

    }
  },
  computed:{
  }
}
</script>
