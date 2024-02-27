<template>
  <div style="overflow-y:auto;height:100%;">
    <slot>
      <preview v-if="form_type=='0'"
               :itemList="itemList"
               :itemDataJson="itemDataJson"
               :formConf="formConf"
               ref="preview"
      />
      <iframe v-if="form_type=='1'" :src="form_url" style="width: 100%;height: 100%" frameborder="0"></iframe>
    </slot>
  </div>
</template>
<script>
import preview from '@/components/formcomponents/preview';
export default {
  name: 'pageview',
  data() {
    return {
      itemList: this.mapList,
      formConf: this.mapConfig,
      itemDataJson: this.dataJson,
      bussid:this.bid
    };
  },
  components: {
    preview
  },
  props: {
    bid: String,
    form_type:String,
    form_url:String,
    dataJson: Object,
    mapList: Array,
    mapConfig: Object
  },
  mounted() {
  },
  methods: {
    cloaseDialog() {
      this.$emit('cloaseView');
    }
  }
};
</script>
<style>
</style>
