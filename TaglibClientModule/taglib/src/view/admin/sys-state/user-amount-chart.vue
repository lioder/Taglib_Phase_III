<template>
  <div class='user-amount-chart'>
    <el-card class="user-amount">
      <div ref="userChart" id="userChart" style="width: 100%; height: 500px"></div>
    </el-card>
  </div>
</template>

<script type="text/ecmascript-6">
  // 引入基本模板
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
    name: 'user-amount-chart',
    mounted () {
      this.drawUserChart()
    },
    methods: {
      drawUserChart () {
        let myChart = this.$echarts.init(this.$refs.userChart)
        // 绘制图表
        myChart.setOption({
          title: {
            text: '用户数量',
            x: 'center'
          },
          legend: {
            show: true,
            orient: 'horizontal',
            bottom: 0,
            data: ['众包工人', '众包发起者']
          },
          tooltip: {
            trigger: 'item',
            position: 'inside',
            formatter: '{b} : {c} ({d}%)'
          },
          series: [
            {
              name: '用户数量',
              type: 'pie',
              radius: '65%',
              center: ['50%', '50%'],
              data: [],
              itemStyle: {
                emphasis: {
                  shadowBlur: 10,
                  shadowOffsetX: 0,
                  shadowColor: 'rgba(0, 0, 0, 0.5)'
                },
                normal: {
                  label: {
                    show: false
                  },
                  labelLine: {
                    show: false
                  }
                }
              }
            }
          ]
        })
        window.addEventListener('resize', function () {
          myChart.resize()
        })
        this.$ajax.get('/admin/list/worker', {
          params: {
            size: 9,
            page: 1,
            keyword: '',
            sortBy: '全部',
            isSec: true
          }
        }).then((res) => {
          let result = res.data
          if (result.code === 0) {
            var page1 = result.data
          }
          this.$ajax.get('/admin/list/publisher', {
            params: {
              size: 9,
              page: 1,
              keyword: '',
              sortBy: '全部',
              isSec: true
            }
          }).then((res) => {
            let result = res.data
            if (result.code === 0) {
              let page2 = result.data
              myChart.setOption({
                series: [
                  {
                    data: [{value: page1.totalItemNum, name: '众包工人'}, {value: page2.totalItemNum, name: '众包发起者'}]
                  }
                ]
              })
            }
          })
        })
      }
    }
  }
</script>

<style lang="stylus">
</style>
