const CompressionWebpackPlugin = require('compression-webpack-plugin');
const VueFilenameInjector = require('@d2-projects/vue-filename-injector');
const ThemeColorReplacer = require('webpack-theme-color-replacer');
const forElementUI = require('webpack-theme-color-replacer/forElementUI');
const { chain, set, each } = require('lodash');

// 拼接路径
const resolve = (dir) => require('path').join(__dirname, dir);

// 增加环境变量
process.env.VUE_APP_VERSION = require('./package.json').version;
process.env.VUE_APP_BUILD_TIME = require('dayjs')().format('YYYY-M-D HH:mm:ss');

const { VUE_APP_PUBLIC_PATH, VUE_PORT } = process.env;

// 设置不参与构建的库
const externals = {};

module.exports = {
  publicPath: VUE_APP_PUBLIC_PATH,
  lintOnSave: false,
  devServer: {
    port: VUE_PORT,
    disableHostCheck: true,
    open: process.platform === 'darwin',
    proxy: {
      '/api': {
        target: 'http://localhost:8082',
        changeOrigin: true,
        timeout: 300000, // 设置超时时间
        pathRewrite: {
          '^/api': '' // 这里是将/api替换为空字符串“” ，也就是删除的意思
        }
      }
    }
  },
  css: {
    loaderOptions: {
      // 设置 scss 公用变量文件
      sass: {
        prependData: "@import '~@/assets/style/public.scss';"
      }
    }
  },
  configureWebpack: (config) => {
    const configNew = {};
    if (process.env.NODE_ENV === 'production') {
      configNew.externals = externals;
      configNew.plugins = [
        // gzip
        new CompressionWebpackPlugin({
          filename: '[path].gz[query]',
          test: new RegExp('\\.(' + ['js', 'css'].join('|') + ')$'),
          threshold: 10240,
          minRatio: 0.8,
          deleteOriginalAssets: false
        })
      ];
    }
    return configNew;
  },
  chainWebpack: (config) => {
    config.plugins.delete('prefetch').delete('preload');
    config.resolve.symlinks(true);
    config.plugin('theme-color-replacer').use(ThemeColorReplacer, [
      {
        fileName: 'css/theme-colors.[contenthash:8].css',
        matchColors: [
          ...forElementUI.getElementUISeries(process.env.VUE_APP_ELEMENT_COLOR) // Element-ui主色系列
        ],
        externalCssFiles: ['./node_modules/element-ui/lib/theme-chalk/index.css'],
        changeSelector: forElementUI.changeSelector,
        injectCss: true
      }
    ]);
    config
      // 开发环境 sourcemap 不包含列信息
      .when(process.env.NODE_ENV === 'development', (config) => config.devtool('cheap-source-map'))
      // 预览环境构建 vue-loader 添加 filename
      .when(process.env.VUE_APP_SCOURCE_LINK === 'TRUE', (config) =>
        VueFilenameInjector(config, {
          propName: process.env.VUE_APP_SOURCE_VIEWER_PROP_NAME
        })
      );
    // markdown
    config.module.rule('md').test(/\.md$/).use('text-loader').loader('text-loader').end();
    // svg
    const svgRule = config.module.rule('svg');
    svgRule.uses.clear();
    svgRule.include
      .add(resolve('src/assets/svg-icons/icons'))
      .end()
      .use('svg-sprite-loader')
      .loader('svg-sprite-loader')
      .options({
        symbolId: 'Z6-[name]'
      })
      .end();
    // image exclude
    const imagesRule = config.module.rule('images');
    imagesRule
      .test(/\.(png|jpe?g|gif|webp|svg)(\?.*)?$/)
      .exclude.add(resolve('src/assets/svg-icons/icons'))
      .end();
    // 重新设置 alias
    config.resolve.alias.set('@api', resolve('src/api'));
  },
  // 不输出 map 文件
  productionSourceMap: false
};
