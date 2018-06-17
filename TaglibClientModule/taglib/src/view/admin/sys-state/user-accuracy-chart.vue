<template>
  <div class='user-accuracy-chart'>
    <el-card>
      <div ref="userAccuracyChart" style="width: 100%; height: 500px;"></div>
    </el-card>
  </div>
</template>

<script type="text/ecmascript-6">
  let echarts = require('echarts/lib/echarts')
  echarts.dataTool = require("echarts/extension/dataTool")
  // 引入柱状图组件
  require('echarts/lib/chart/pie')
  require('echarts/lib/chart/bar')
  require('echarts/lib/chart/line')
  require('echarts/lib/chart/boxplot')
  // 引入提示框和title组件
  require('echarts/lib/component/tooltip')
  require('echarts/lib/component/legend')
  require('echarts/lib/component/title')
  export default {
    name: 'user-accuracy-chart',
    mounted () {
      this.drawUserAccuracyChart()
    },
    methods: {
      drawUserAccuracyChart: function () {
        let myChart = this.$echarts.init(this.$refs.userAccuracyChart)
        let fakeData = [
          [850, 740, 900, 1070, 930, 850, 950, 980, 980, 880, 1000, 980, 930, 650, 760, 810, 1000, 1000, 960, 960],
          [960, 940, 960, 940, 880, 800, 850, 880, 900, 840, 830, 790, 810, 880, 880, 830, 800, 790, 760, 800],
          [880, 880, 880, 860, 720, 720, 620, 860, 970, 950, 880, 910, 850, 870, 840, 840, 850, 840, 840, 840]
        ]
        let data = echarts.dataTool.prepareBoxplotData(fakeData)
        myChart.setOption({
          title: [
            {
              text: '用户准确率箱型图',
              left: 'center'
            }
          ],
          tooltip: {
            trigger: 'item',
            axisPointer: {
              type: 'shadow'
            }
          },
          grid: {
            left: '10%',
            right: '10%',
            bottom: '15%'
          },
          xAxis: {
            type: 'category',
            data: data.axisData,
            boundaryGap: true,
            nameGap: 30,
            splitArea: {
              show: false
            },
            axisLabel: {
              formatter: function (value) {
                console.log(value)
                if (value === '0') {
                  return '全部用户'
                } else if (value === '1') {
                  return '专家'
                } else {
                  return '普通工人'
                }
              }
            },
            splitLine: {
              show: false
            }
          },
          yAxis: {
            type: 'value',
            name: '准确率/%',
            splitArea: {
              show: true
            }
          },
          series: [
            {
              name: 'boxplot',
              type: 'boxplot',
              data: data.boxData,
              tooltip: {
                formatter: function (param) {
                  return [
                    'upper: ' + param.data[5],
                    'Q3: ' + param.data[4],
                    'median: ' + param.data[3],
                    'Q1: ' + param.data[2],
                    'lower: ' + param.data[1]
                  ].join('<br/>')
                }
              }
            },
            {
              name: 'outlier',
              type: 'scatter',
              data: data.outliers
            }
          ]
        })
        window.addEventListener('resize', function () {
          myChart.resize()
        })
        this.$ajax.get('/statistics/user/accuracy').then((res) => {
          let result = res.data
          if (result.code === 0) {
            let userAccuracy = result.data
            let data = []
            data.push(userAccuracy.user)
            data.push(userAccuracy.worker)
            data.push(userAccuracy.expert)

            let postData = echarts.dataTool.prepareBoxplotData(data)
            myChart.setOption({
              xAxis: {
                data: postData.axisData
              },
              series: [
                {
                  name: 'boxplot',
                  type: 'boxplot',
                  data: postData.boxData,
                  tooltip: {
                    formatter: function (param) {
                      return [
                        '最大值: ' + Math.round(param.data[5] * 100) + '%',
                        '第三四分位: ' + Math.round(param.data[4] * 100) + '%',
                        '中位数: ' + Math.round(param.data[3] * 100) + '%',
                        '第一四分位: ' + Math.round(param.data[2] * 100) + '%',
                        '最小值: ' + Math.round(param.data[1] * 100) + '%'
                      ].join('<br/>')
                    }
                  }
                },
                {
                  name: 'outlier',
                  type: 'scatter',
                  data: postData.outliers
                }
              ]
            })
          }
        }).catch(() => {
          this.$message.error('获取数据失败')
        })
      }
    }
  }
</script>

<style lang="stylus">

</style>
