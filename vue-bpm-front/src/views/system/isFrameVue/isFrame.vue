<template>
  <div class="drawer-content" style="height: 85vh">
    <iframe ref="previewIframe"  :src="form_url" style="width: 100%;height: 100%" frameborder="0"></iframe>
    <el-drawer v-if="handView==true"
               :visible.sync="handView"
               :with-header="false"
               :title="title"
               size="70%"
               :append-to-body="false"
               destroy-on-close>
      <div class="drawer-content" style="height: 100vh">
        <iframe ref="previewIframe"  :src="src" style="width: 100%;height: 100%" frameborder="0"></iframe>
      </div>

      <div class="drawer-footer">
        <el-button @click="handView = false"> 取消 </el-button>
      </div>
    </el-drawer>
  </div>

</template>

<script>
import PostMessage from "@/utils/postMessage";
export default {
  name: "isFrame",
  data(){
    return{
      form_url:"",
      IFRAME:Object,
      handView:false,
      title:'',
      src:'',
    }
  },
  mounted(){
    this.pageInit();
    this.initStaticState()
    this.prefixIframe();
    //this.clickMenuHandle(this.$route)
  },
  methods:{
    pageInit(){
      const queryJson=JSON.parse(this.$route.meta.query);
      this.form_url=queryJson.url;
    },
    initStaticState(){
      this.IFRAME = this.$refs['previewIframe'];
    },
    prefixIframe(){
      PostMessage.on('saveSuccess',()=>{
        Object.assign(this.$data,{
          formState:'static'
        })
        PostMessage.send(this.IFRAME,{
          action:'cancelEdit'
        })
      })
      PostMessage.on('loadingDone',()=>{
        PostMessage.send(this.IFRAME,{
          action:'setFormIframeHeight'
        })
        PostMessage.send(this.IFRAME,{
          action:'setTableIframeHeight'
        })
        PostMessage.on('table:addData',(datas)=>{
          this.title="新增";
          this.src=datas.url;
          this.handView=true;
        })
        PostMessage.on('table:editData',async (datas)=>{
          this.title="编辑";
          this.src=datas.url;
          this.handView=true;
        })
        PostMessage.on('table:detailData',(datas)=>{
          this.title="详情";
          this.src=datas.url;
          this.handView=true;
        })
        Object.assign(this.$data,{
          formState:'static'
        })
      })
      PostMessage.on('setFormIframeHeight',({height})=>{
        Object.assign(this.$data,{
          height:height,
          iframeLoading:false,
          IFRAME :this.$refs['iframe']
        })
      })
      PostMessage.on('setTableIframeHeight',({height})=>{
        Object.assign(this.$data,{
          height:height,
          iframeLoading:false,
          IFRAME :this.$refs['iframe']
        })
      })
      PostMessage.on('modalSaveData',()=>{
        PostMessage.send(this.IFRAME,{
          action:'table:updateDatas'
        })
      })
    }
  },

}
</script>

<style scoped>

</style>
