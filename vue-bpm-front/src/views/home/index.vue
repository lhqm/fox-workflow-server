<template>
  <el-row :gutter="20" style="margin-right: 5px !important;">
    <el-col :span="6" style="padding-right: 2px; height: 80vh;">
      <dv-border-box-10  class="border11"
                        :color="[themeActiveSetting.dataVbgColor,themeActiveSetting.dataVbgColor]"
                        style="height: 98%; width: 100%;">
        <div id="main" style="height: 50%; width: 100%;"></div>
        <div id="main1" style="height: 50%; width: 100%;"></div>
      </dv-border-box-10>

    </el-col>
<!--    <el-col :span="12" style="padding-right: 2px; height: 40vh">-->
<!--      <dv-border-box-11 title="审核信息" class="border11"-->
<!--                        :color="[themeActiveSetting.dataVbgColor,themeActiveSetting.dataVbgColor]"-->
<!--                        style="height: 98%; width: 100%;">-->
<!--        <el-table-->
<!--          :data="dataTable"-->
<!--          row-key="id"-->
<!--          class="el-table"-->
<!--          style="width: 100%"-->
<!--          default-expand-all>-->
<!--          <el-table-column type="index" label="序号" width="100"></el-table-column>-->
<!--          <el-table-column prop="id" label="角色编号" ></el-table-column>-->
<!--          <el-table-column prop="name" label="角色名称"></el-table-column>-->
<!--          <el-table-column prop="data_scope" label="数据权限范围" v-if="false"></el-table-column>-->
<!--          <el-table-column label="操作"  align="center">-->
<!--            <template slot-scope="scope">-->
<!--              <div class="operation-btn">-->
<!--                <el-button icon="el-icon-edit" @click="edit(scope.row)" type="text"-->
<!--                >编辑</el-button>-->
<!--                <el-popconfirm title="您确定要删除吗？"-->
<!--                               @confirm="deleteById(scope.row.id,scope.row.name)">-->
<!--                  <el-button icon="el-icon-delete" slot="reference" type="text" class="btn-danger">删除</el-button>-->
<!--                </el-popconfirm>-->
<!--              </div>-->
<!--            </template>-->
<!--          </el-table-column>-->
<!--        </el-table>-->
<!--      </dv-border-box-11>-->
<!--    </el-col>-->
    <el-col :span="18" style="padding-right: 2px; height: 80vh">
      <dv-border-box-10  class="border11"
                         :color="[themeActiveSetting.dataVbgColor,themeActiveSetting.dataVbgColor]"
                         style="height: 98%; width: 100%;">
        <div id="main2" style="height: 98%; width: 100%;padding-top: 10px;padding-left: 10px;padding-right: 10px"></div>
      </dv-border-box-10>
    </el-col>
  </el-row>
</template>
<script>
import { mapGetters} from "vuex";
import * as echarts from 'echarts'
export default {
    name: 'home',
    data() {
      return {
        dataTable: [],
        config:{
          data: [
            ['行1列1', '行1列2', '行1列3'],
            ['行2列1', '行2列2', '行2列3'],
            ['行3列1', '行3列2', '行3列3'],
            ['行4列1', '行4列2', '行4列3'],
            ['行5列1', '行5列2', '行5列3'],
            ['行6列1', '行6列2', '行6列3'],
            ['行7列1', '行7列2', '行7列3'],
            ['行8列1', '行8列2', '行8列3'],
            ['行9列1', '行9列2', '行9列3'],
            ['行10列1', '行10列2', '行10列3']
          ],
          oddRowBGC:'#5378a2',
          evenRowBGC:'#44cef6'
        }
      }
    },
    computed: {
      ...mapGetters('store', {
        themeActiveSetting: 'theme/activeSetting'
      }),
    },
  mounted() {
    this.$nextTick(() => {
      this.initChart();
      this.initChart1();
      this.initChart2();
    })
  },
  methods:{
      initChart() {
        let app = {};
        let chartDom = document.getElementById('main');
        let myChart = echarts.init(chartDom);
        let option;

        const data = [
          { value: 800, name: 'A' },
          { value: 635, name: 'B' },
          { value: 580, name: 'C' },
          { value: 484, name: 'D' },
          { value: 300, name: 'E' },
          { value: 200, name: 'F' }
        ];
        const defaultPalette = [
          // '#51689b', '#ce5c5c', '#fbc357', '#8fbf8f', '#659d84', '#fb8e6a', '#c77288', '#786090', '#91c4c5', '#6890ba'
          '#5470c6',
          '#91cc75',
          '#fac858',
          '#ee6666',
          '#73c0de',
          '#3ba272',
          '#fc8452',
          '#9a60b4',
          '#ea7ccc'
        ];
        const radius = ['30%', '80%'];
        const pieOption = {
          series: [
            {
              type: 'pie',
              id: 'distribution',
              radius: radius,
              label: {
                show: false
              },
              universalTransition: true,
              animationDurationUpdate: 1000,
              data: data
            }
          ]
        };
        const parliamentOption = (function () {
          let sum = data.reduce(function (sum, cur) {
            return sum + cur.value;
          }, 0);
          let angles = [];
          let startAngle = -Math.PI / 2;
          let curAngle = startAngle;
          data.forEach(function (item) {
            angles.push(curAngle);
            curAngle += (item.value / sum) * Math.PI * 2;
          });
          angles.push(startAngle + Math.PI * 2);
          function parliamentLayout(startAngle, endAngle, totalAngle, r0, r1, size) {
            let rowsCount = Math.ceil((r1 - r0) / size);
            let points = [];
            let r = r0;
            for (let i = 0; i < rowsCount; i++) {
              // Recalculate size
              let totalRingSeatsNumber = Math.round((totalAngle * r) / size);
              let newSize = (totalAngle * r) / totalRingSeatsNumber;
              for (
                let k = Math.floor((startAngle * r) / newSize) * newSize;
                k < Math.floor((endAngle * r) / newSize) * newSize - 1e-6;
                k += newSize
              ) {
                let angle = k / r;
                let x = Math.cos(angle) * r;
                let y = Math.sin(angle) * r;
                points.push([x, y]);
              }
              r += size;
            }
            return points;
          }
          return {
            series: {
              type: 'custom',
              id: 'distribution',
              data: data,
              coordinateSystem: undefined,
              universalTransition: true,
              animationDurationUpdate: 1000,
              renderItem: function (params, api) {
                var idx = params.dataIndex;
                var viewSize = Math.min(api.getWidth(), api.getHeight());
                var r0 = ((parseFloat(radius[0]) / 100) * viewSize) / 2;
                var r1 = ((parseFloat(radius[1]) / 100) * viewSize) / 2;
                var cx = api.getWidth() * 0.5;
                var cy = api.getHeight() * 0.5;
                var size = viewSize / 50;
                var points = parliamentLayout(
                  angles[idx],
                  angles[idx + 1],
                  Math.PI * 2,
                  r0,
                  r1,
                  size + 3
                );
                return {
                  type: 'group',
                  children: points.map(function (pt) {
                    return {
                      type: 'circle',
                      autoBatch: true,
                      shape: {
                        cx: cx + pt[0],
                        cy: cy + pt[1],
                        r: size / 2
                      },
                      style: {
                        fill: defaultPalette[idx % defaultPalette.length]
                      }
                    };
                  })
                };
              }
            }
          };
        })();
        let currentOption = (option = pieOption);
        setInterval(function () {
          currentOption = currentOption === pieOption ? parliamentOption : pieOption;
          myChart.setOption(currentOption);
        }, 2000);

        myChart.setOption(option);
      },
    initChart1() {
      var app = {};
      var chartDom = document.getElementById('main1');
      var myChart = echarts.init(chartDom);
      var option;

      option = {
        title: {
          text: '',
          subtext: 'Fake Data',
          left: 'center'
        },
        tooltip: {
          trigger: 'item'
        },
        legend: {
          orient: 'vertical',
          left: 'left'
        },
        series: [
          {
            name: 'Access From',
            type: 'pie',
            radius: '50%',
            data: [
              { value: 735, name: 'Direct' },
              { value: 580, name: 'Email' },
              { value: 484, name: 'Union Ads' },
              { value: 300, name: 'Video Ads' }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            }
          }
        ]
      };

      myChart.setOption(option);
    },
    initChart2() {
      var app = {};
      var chartDom = document.getElementById('main2');
      var myChart = echarts.init(chartDom);
      var option;

      let category = [];
      let dottedBase = +new Date();
      let lineData = [];
      let barData = [];
      for (let i = 0; i < 20; i++) {
        let date = new Date((dottedBase += 3600 * 24 * 1000));
        category.push(
          [date.getFullYear(), date.getMonth() + 1, date.getDate()].join('-')
        );
        let b = Math.random() * 200;
        let d = Math.random() * 200;
        barData.push(b);
        lineData.push(d + b);
      }
// option
      option = {
        backgroundColor: '#0f375f',
        tooltip: {
          trigger: 'axis',
          axisPointer: {
            type: 'shadow'
          }
        },
        legend: {
          data: ['line', 'bar'],
          textStyle: {
            color: '#ccc'
          }
        },
        xAxis: {
          data: category,
          axisLine: {
            lineStyle: {
              color: '#ccc'
            }
          }
        },
        yAxis: {
          splitLine: { show: false },
          axisLine: {
            lineStyle: {
              color: '#ccc'
            }
          }
        },
        series: [
          {
            name: 'line',
            type: 'line',
            smooth: true,
            showAllSymbol: true,
            symbol: 'emptyCircle',
            symbolSize: 15,
            data: lineData
          },
          {
            name: 'bar',
            type: 'bar',
            barWidth: 10,
            itemStyle: {
              borderRadius: 5,
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: '#14c8d4' },
                { offset: 1, color: '#43eec6' }
              ])
            },
            data: barData
          },
          {
            name: 'line',
            type: 'bar',
            barGap: '-100%',
            barWidth: 10,
            itemStyle: {
              color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                { offset: 0, color: 'rgba(20,200,212,0.5)' },
                { offset: 0.2, color: 'rgba(20,200,212,0.2)' },
                { offset: 1, color: 'rgba(20,200,212,0)' }
              ])
            },
            z: -12,
            data: lineData
          },
          {
            name: 'dotted',
            type: 'pictorialBar',
            symbol: 'rect',
            itemStyle: {
              color: '#0f375f'
            },
            symbolRepeat: true,
            symbolSize: [12, 4],
            symbolMargin: 1,
            z: -10,
            data: lineData
          }
        ]
      };

      myChart.setOption(option);
    },
    }
  };
</script>
<style>
  .dv-border-box-11 .border-box-content {
    position: relative;
    width: 97%;
    height: 100%;
    padding-left: 3%;
    padding-top: 8%;
  }
  .el-table,
  .el-table__expanded-cell {
    background-color: transparent !important;
  }
  .el-table th,
  .el-table tr,
  .el-table td {
    background-color: transparent !important;
  }
</style>
