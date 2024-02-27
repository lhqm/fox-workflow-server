<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts' // V5,V4版本可以直接 import echarts from
require('echarts/theme/macarons')

export default {
  name:'NighPieChart',
  props: {
    className: {
      type: String,
      default: 'chart'
    },
    width: {
      type: String,
      default: '100%'
    },
    height: {
      type: String,
      default: '40vh'
    },
    pieName:{
      type: String,
      default: ''
    },
    data: {
      type: Array,
      default: function () {
        return [];
      }
    }
  },
  data() {
    return {
      chart: null
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initChart()
    })
  },
  beforeDestroy() {
    if (!this.chart) {
      return
    }
    this.chart.dispose()
    this.chart = null
  },
  watch: {
    data: {
      immediate: false,
      handler(val) {
        if (val) this.initChart();
      }
    },
    pieName: {
      immediate: false,
      handler(val) {
        if (val) this.initChart();
      }
    }
  },
  methods: {
    initChart() {
      this.chart = echarts.init(this.$el, 'macarons');
      let xAxis = this.data.filter((item) => {
        return item.name;
      });
      const option = {
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b} : {c} ({d}%)'
        },
        legend: {
          left: 'center',
          bottom: '10',
          data: xAxis
        },
        series: [
          {
            name: this.pieName,
            type: 'pie',
            roseType: 'radius',
            radius: [15, 95],
            center: ['50%', '40%'],
            data: this.data,
            animationEasing: 'cubicInOut',
            animationDuration: 2600
          }
        ]
      };
      this.chart.setOption(option);
    }
  }
}
</script>
