<template>
  <div>
    <template v-for="(item, index) in options">
      <template v-if="values.includes(item.value)">
        <span
          v-if="item.listClass == 'default' || item.listClass == ''"
          :key="item.value"
          :index="index"
          :class="item.cssClass"
        >{{ item.label }}</span
        >
        <el-tag
          v-else
          :disable-transitions="true"
          :key="item.value"
          :index="index"
          :type="item.listClass == 'primary' ? '' : item.listClass"
          :class="item.cssClass"
        >
          {{ item.label }}
        </el-tag>
      </template>
    </template>
  </div>
</template>

<script>
export default {
  name: "dictTag",
  props: {
    options: {
      type: Array,
      default: null,
    },
    value: [Number, String, Array],
  },
  computed: {
    values() {
      if (this.value !== null && typeof this.value !== 'undefined') {
        return Array.isArray(this.value) ? this.value : [String(this.value)];
      } else {
        return [];
      }
    },
  },
};
</script>
<style scoped>
.el-tag + .el-tag {
  margin-left: 10px;
}
</style>
