<template>
  <div :class="className" :style="{height:height,width:width}" />
</template>

<script>
import * as echarts from 'echarts' // V5,V4版本可以直接 import echarts from
require('echarts/theme/macarons')

export default {
  name:'PieChart',
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
    pieData: {
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
    pieData: {
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
      this.chart = echarts.init(this.$el, 'macarons')
      const option = {
        tooltip: {
          trigger: 'item'
        },
        color:['#10D070','rgb(245, 182, 88)'],
        legend: {
          top: '5%',
          left: 'left'
        },
        series: [
          {
            name: this.pieName,
            type: 'pie',
            radius: ['40%', '70%'],
            avoidLabelOverlap: false,
            emphasis: {
              label: {
                show: true,
                fontSize: 18,
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: this.pieData
          }
        ]
      };
      this.chart.setOption(option);
    }
  }
}
</script>
