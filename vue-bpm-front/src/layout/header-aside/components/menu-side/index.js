import { mapState } from 'vuex';
import menuMixin from '../mixin/menu';
import { createMenu } from '../libs/util.menu';
import BScroll from 'better-scroll';

export default {
  name: 'layout-header-aside-menu-side',
  mixins: [menuMixin],
  render(h) {
    return (
      <div class="layout-header-aside-menu-side">
        <el-menu
          collapse={this.asideCollapse}
          collapseTransition={this.asideTransition}
          uniqueOpened={true}
          defaultActive={this.$route.name}
          ref="menu"
          onSelect={this.handleMenuSelect}
        >
          {this.aside.map((menu) => createMenu.call(this, h, menu))}
        </el-menu>
        {this.aside.length === 0 && !this.asideCollapse ? (
          <div class="layout-header-aside-menu-empty" flex="dir:top main:center cross:center">
            <icon name="inbox"></icon>
            <span>没有侧栏菜单</span>
          </div>
        ) : null}
      </div>
    );
  },
  data() {
    return {
      asideHeight: 300,
      BS: null
    };
  },
  computed: {
    ...mapState('store/menu', ['aside', 'asideCollapse', 'asideTransition'])
  },
  watch: {
    // 折叠和展开菜单的时候销毁 better scroll
    asideCollapse(val) {
      this.scrollDestroy();
      setTimeout(() => {
        this.scrollInit();
      }, 500);
    }
  },
  mounted() {
    this.scrollInit();
  },
  beforeDestroy() {
    this.scrollDestroy();
  },
  methods: {
    scrollInit() {
      this.BS = new BScroll(this.$el, {
        mouseWheel: true,
        click: true
        // 如果你愿意可以打开显示滚动条
        // scrollbar: {
        //   fade: true,
        //   interactive: false
        // }
      });
    },
    scrollDestroy() {
      try {
        this.BS.destroy();
      } catch (e) {
        delete this.BS;
        this.BS = null;
      }
    }
  }
};
