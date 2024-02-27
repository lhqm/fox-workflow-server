<template>
  <el-select
    popper-class="select-tree"
    ref="select"
    clearable
    v-model="modalValue"
    @clear="clear"
    @visible-change="visibleChange"
  >
    <el-option :label="options.label" :value="options.value" style="height: auto">
      <el-tree
        :data="data"
        :node-key="nodeKey"
        check-strictly
        ref="tree"
        highlight-current
        default-expand-all
        :expand-on-click-node="false"
        :props="props"
        @node-click="handleNodeClick"
      >
        <div class="custom-tree-node" slot-scope="{ node, data }">
          <span>{{ node.label }}</span>
          <div v-if="data.disabled" class="disabled" @click.stop></div>
        </div>
      </el-tree>
    </el-option>
  </el-select>
</template>

<script>
  export default {
    name: 'SelectTree',
    props: {
      multiple: {
        type: Boolean,
        default: false
      },
      value: {
        type: [String, Number],
        default: ''
      },
      // 树形的数据
      data: {
        type: Array,
        default: function () {
          return [];
        }
      },
      // 每个树节点用来作为唯一标识的属性
      nodeKey: {
        type: String,
        default: 'id'
      },
      // tree的props配置
      props: {
        type: Object,
        default: function () {
          return {
            label: 'name',
            children: 'children'
          };
        }
      }
    },
    data() {
      return {
        options: {
          label: '',
          value: null
        }
      };
    },
    computed: {
      modalValue: {
        get() {
          return this.value || null;
        },
        set(val) {
          this.$emit('input', val);
        }
      }
    },
    watch: {
      data: {
        immediate: true,
        handler(val) {
          if (val) this.init();
        }
      },
      value:{
        immediate: true,
        handler(val) {
          if (val) this.init();
        }
      }
    },
    methods: {
      handleNodeClick(obj) {
        this.options = {
          label: obj[this.props.label],
          value: obj[this.nodeKey]
        };
        this.modalValue = obj[this.nodeKey];
        this.$refs.select.visible = false;
      },
      init() {
        this.$nextTick(() => {
          if (this.value) {
            this.$refs.tree.setCurrentKey(this.value);
            const node = this.$refs.tree.getNode(this.value);
            if (node) {
              this.options.value = this.value;
              this.options.label = node.data.name;
            }
          } else {
            this.$refs.tree.setCurrentKey();
          }
        });
      },
      clear() {
        this.$emit('input', null);
        this.$refs.tree.setCurrentKey();
      },
      visibleChange(e) {
        if (e) {
          setTimeout(() => {
            const selectDom = document.querySelector('.select-tree .is-current');
            const selectContainer = document.querySelector('.select-tree .el-scrollbar__wrap');
            if (selectDom) {
              const scrollY = selectDom.offsetTop;
              selectContainer.scrollTo(0, scrollY);
            } else {
              selectContainer.scrollTo(0, 0);
            }
          }, 0);
        }
      }
    }
  };
</script>
<style lang="scss">
  .el-tree-node__content {
    position: relative;
    font-weight: normal;
  }
  .disabled {
    cursor: not-allowed;
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 2;
    color: #888;
  }
</style>
