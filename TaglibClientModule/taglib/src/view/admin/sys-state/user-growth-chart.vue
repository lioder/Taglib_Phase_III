<template>
  <div class='user-growth-chart'>
    <el-card>
      <div ref="userGrowthChart" style="width: 100%; height: 500px;"></div>
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
    name: 'user-growth-chart',
    mounted () {
      this.drawUserGrowthChart()
    },
    methods: {
      drawUserGrowthChart: function () {
        let myChart = this.$echarts.init(this.$refs.userGrowthChart)
        let defaultData = [['2000-06-05', 116], ['2000-06-06', 129], ['2000-06-07', 135], ['2000-06-08', 86], ['2000-06-09', 73], ['2000-06-10', 85], ['2000-06-11', 73], ['2000-06-12', 68], ['2000-06-13', 92], ['2000-06-14', 130], ['2000-06-15', 245], ['2000-06-16', 139], ['2000-06-17', 115], ['2000-06-18', 111], ['2000-06-19', 309], ['2000-06-20', 206], ['2000-06-21', 137], ['2000-06-22', 128], ['2000-06-23', 85], ['2000-06-24', 94], ['2000-06-25', 71], ['2000-06-26', 106], ['2000-06-27', 84], ['2000-06-28', 93], ['2000-06-29', 85], ['2000-06-30', 73], ['2000-07-01', 83], ['2000-07-02', 125], ['2000-07-03', 107], ['2000-07-04', 82], ['2000-07-05', 44], ['2000-07-06', 72], ['2000-07-07', 106], ['2000-07-08', 107], ['2000-07-09', 66], ['2000-07-10', 91], ['2000-07-11', 92], ['2000-07-12', 113], ['2000-07-13', 107], ['2000-07-14', 131], ['2000-07-15', 111], ['2000-07-16', 64], ['2000-07-17', 69], ['2000-07-18', 88], ['2000-07-19', 77], ['2000-07-20', 83], ['2000-07-21', 111], ['2000-07-22', 57], ['2000-07-23', 55], ['2000-07-24', 60]]
        let defaultDateList = defaultData.map(function (item) {
          return item[0]
        })
        let defaultValueList = defaultData.map(function (item) {
          return item[1]
        })
        myChart.setOption({
          visualMap: [{
            show: false,
            type: 'continuous',
            seriesIndex: 0,
            min: 0,
            max: 400
          }],
          title: [{
            left: 'center',
            text: '用户增长图'
          }],
          grid: {
            left: '3%',
            right: '4%',
            bottom: '5%',
            containLabel: true
          },
          tooltip: {
            trigger: 'axis'
          },
          xAxis: [{
            data: defaultDateList
          }],
          yAxis: [{
            name: '人数',
            splitLine: {show: false}
          }],
          series: [{
            type: 'line',
            showSymbol: false,
            data: defaultValueList
          }]
        })

        window.addEventListener('resize', function () {
          myChart.resize()
        })
        this.$ajax.get('/statistics/user/tendency', {
          params: {
            startDate: '2018-04-26',
            endDate: new Date().Format('yyyy-MM-dd'),
            interval: 1
          }
        }).then((res) => {
          let result = res.data
          if (result.code === 0) {
            let data = result.data
            let myData = []
            let dates = Object.keys(data)
            let count = 0
            dates.forEach((date) => {
              count += data[date]
              myData.push([date, count])
            })
            var dateList = myData.map(function (item) {
              return item[0]
            })
            var valueList = myData.map(function (item) {
              return item[1]
            })
            myChart.setOption({
              xAxis: [{
                data: dateList
              }],
              series: [
                {
                  data: valueList
                }
              ]
            })
          }
        })
      }
    }
  }
</script>

<style lang="stylus">

</style>
