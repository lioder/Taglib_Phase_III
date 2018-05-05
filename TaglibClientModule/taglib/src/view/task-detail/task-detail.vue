<template>
  <div class="task-detail">
    <div class="breadcrumb-nav">
      <el-breadcrumb separator-class="el-icon-arrow-right">
        <el-breadcrumb-item v-if="fromPath === '/tasks'" :to="{ path: '/tasks' }">任务中心</el-breadcrumb-item>
        <el-breadcrumb-item v-if="fromPath === '/myTasks'" :to="{path: '/myTasks' }">我的任务</el-breadcrumb-item>
        <el-breadcrumb-item>任务详情</el-breadcrumb-item>
      </el-breadcrumb>
    </div>
    <el-row :gutter="30">
      <el-col :span="15">
        <el-card>
          <div class="task-label-wrapper">
            <h1 class="title">{{ taskInfo.title }}</h1>
            <div class="col-left">
              <div class="line">
                <span class="label"><i class="el-icon-document"></i>任务类型: </span>
                <span class="task-type">{{ taskType(taskInfo.taskType) }}标注</span>
              </div>
              <div class="line">
                <span class="label"><i class="el-icon-time"></i>发布时间: </span>
                <span class="start-date">{{ taskInfo.startDate}}</span>
              </div>
            </div>
            <div class="col-right">
              <div class="line">
                <span class="label"><i class="el-icon-star-off"></i>任务评价: </span>
                <star :score="taskInfo.rating" :size="24" class="hotrank"></star>
              </div>
              <div class="line">
                <span class="label"><i class="el-icon-date"></i>结束时间: </span>
                <span>{{ taskInfo.endDate }}</span>
              </div>
            </div>
            <div>
              <el-tag class="topics" v-for="(topic,index) in taskInfo.topics" :key="index">{{ topic }}</el-tag>
            </div>
          </div>
          <div class="task-desc-wrapper">
            <span class="header">任务描述</span>
            <p class="task-desc">{{ taskInfo.description}}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="9">
        <el-card class="reward-box">
          <div>
            <div class="block">
              <div class="label">任务奖励</div>
              <div class="text">{{ taskInfo.price }} T币</div>
            </div>
            <div class="block">
              <div class="label">任务题量</div>
              <div class="text">{{ taskInfo.picNum }} 图</div>
            </div>
            <div class="worker-button" v-if="this.$store.getters.userType === 0">
              <el-button round @click="continueTask" type="danger" v-if="state === 'PROCESSING'">继续任务</el-button>
              <el-button round @click="acceptTask" type="danger" v-if="state === 'new'">开始任务</el-button>
            </div>
          </div>
        </el-card>
        <el-card class="progress-card" v-if="this.state === 'PROCESSING'">
          <h1 class="header">任务进度</h1>
          <el-progress :percentage="progress"></el-progress>
        </el-card>
        <el-card class="rate-card" v-if="this.$store.getters.userType === 0 && state === 'PASS'">
          <h1 class="header">我的评价</h1>
          <el-rate
            v-model="rateValue"
            :disabled="rateOnlyRead"
            :colors="['#99A9BF', '#F7BA2A', '#FF9900']"
            @change="rate"
            text-color="#ff9900"
            :show-score="true">
          </el-rate>
        </el-card>
      </el-col>
    </el-row>
    <el-row>
      <el-card class="taskpub-rate-card" v-if="this.$store.getters.userType === 1">
        <h1 class="header">任务分析</h1>
        <el-row>
          <el-col :span="12">
            <div ref="rateChart" style="width: 100%; height: 300px;"></div>
          </el-col>
          <el-col :span="12">
            <div ref="workerChart" style="width: 100%; height: 300px;"></div>
          </el-col>
        </el-row>
      </el-card>
    </el-row>
  </div>
</template>

<script type="text/ecmascript-6">
  import Star from '../../components/star/star'

  var echarts = require('echarts/lib/echarts')
  require('echarts/lib/chart/bar')
  require('echarts/lib/chart/radar')
  require('echarts/lib/component/title')

  export default {
    name: 'task-detail',
    components: {Star},
    data () {
      return {
        state: '',
        rateValue: 0,
        rateOnlyRead: false,
        progress: 0,
        fromPath: '/',
        taskInfo: {
          id: 0,
          title: '',
          taskType: 0,
          startDate: '',
          endDate: '',
          price: 0
        }
      }
    },
    beforeRouteEnter (to, from, next) {
      next(vm => vm.setFrom(from.path))
    },
    methods: {
      setFrom: function (path) {
        this.fromPath = path
      },
      taskType: function (t) {
        let type = ['分类', '标框', '区域']
        return type[t]
      },
      acceptTask: function () {
        this.$ajax.get('/user/new/' + this.taskInfo.id, {
          params: {
            userId: this.$store.getters.id
          }
        }).then((res) => {
          let result = res.data
          if (result.code === 0) {
            let taskWorker = result.data
            localStorage.setItem('taskWorker', JSON.stringify(taskWorker))
            localStorage.setItem('boardState', 'edit')
            this.$router.push('/board')
          } else {
            this.$message.error('接受任务失败')
          }
        }).catch(() => {
          this.$message.error('接受任务失败')
        })
      },
      continueTask: function () {
        localStorage.setItem('boardState', 'edit')
        this.$ajax.get('/user/' + this.taskInfo.id).then((res) => {
          let result = res.data
          if (result.code === 0) {
            let taskWorker = result.data
            localStorage.setItem('taskWorker', JSON.stringify(taskWorker))
            localStorage.setItem('boardState', 'edit')
            this.$router.push('/board')
          } else {
            this.$message.error('接受任务失败')
          }
        }).catch(() => {
          this.$message.error('接受任务失败')
        })
      },
      rate: function () {
        this.rateOnlyRead = true
        this.$ajax.post('/user/rating', this.$qs.stringify({
          taskWorkerId: this.taskInfo.id,
          rating: this.rateValue
        })).then((res) => {
          let result = res.data
          if (result.code === 0) {
            this.$message.success('感谢您的评价！')
          } else {
            this.$message.error('评价失败')
          }
        }).catch(() => {
          this.$message.error('评价失败')
        })
      },
      drawRateChart: function () {
        let myChart = echarts.init(this.$refs.rateChart)
        myChart.setOption({
          title: {
            text: '任务评价统计',
            x: 'center'
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'shadow'
            },
            formatter: function (params) {
              let relVal = params[0].name
              let sum = 0
              myChart.getOption().series[0].data.forEach((item) => {
                sum += item
              })
              for (let i = 0, l = params.length; i < l; i++) {
                relVal += '<br/>' + params[i].value + '人(' + (params[i].value / sum * 100).toFixed(2) + '%)'
              }
              return relVal
            }
          },
          legend: {
            data: ['5星', '4星', '3星', '2星', '1星'].reverse()
          },
          grid: {
            left: '3%',
            right: '15%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: [
            {
              // show: false,
              name: '人数(人)',
              type: 'value'
            }
          ],
          yAxis: [
            {
              type: 'category',
              axisTick: {show: false},
              data: ['5星', '4星', '3星', '2星', '1星'].reverse()
            }
          ],
          series: [
            {
              data: [200, 22, 35, 10, 90].reverse(),
              type: 'bar',
              itemStyle: {
                normal: {
                  color: function (params) {
                    let colorList = ['#FF8607', '#FF9900', '#ffc001', '#F7BA2A', '#99A9BF'].reverse()
                    return colorList[params.dataIndex]
                  },
                  label: {
                    show: true,
                    position: 'right'
                  }
                }
              }
            }
          ]
        })
        window.addEventListener('resize', function () {
          myChart.resize()
        })
        this.$ajax.get('/statistics/taskPublisher/rating', {
          params: {
            taskPublisherId: this.taskInfo.id
          }
        }).then((res) => {
          let result = res.data
          if (result.code === 0) {
            let map = result.data
            let ratings = []
            for (let i = 1; i <= 5; i++) {
              ratings.push(map[i])
            }
            myChart.setOption({
              series: [{
                data: ratings
              }]
            })
          }
        })
      },
      drawWorkerChart: function () {
        let myChart = echarts.init(this.$refs.workerChart)
        myChart.setOption({
          title: {
            text: '工人质量分析',
            x: 'center'
          },
          tooltip: {
            formatter (params) {
              let value = params.value
              return `工人平均质量<br>
              等级：Lv.${value[0]}<br>
              经验：${value[1]}<br>
              积分：${value[2]}<br>
              准确率：${value[3]}%<br>
              准时率：${value[4]}%`
            }
          },
          radar: [
            {
              indicator: [
                {text: '等级', max: 7},
                {text: '经验', max: 8000},
                {text: '积分', max: 8000},
                {text: '准确率', max: 100},
                {text: '准时率', max: 100}
              ],
              radius: 100,
              center: ['50%', '60%']
            }
          ],
          series: [
            {
              type: 'radar',
              tooltip: {
                trigger: 'item'
              },
              itemStyle: {
                normal: {
                  areaStyle: {
                    type: 'default'
                  }
                }
              },
              data: [{
                value: [4, 800, 23, 78, 100]
              }]
            }
          ]
        })
        window.addEventListener('resize', function () {
          myChart.resize()
        })
        this.$ajax.get('/statistics/taskPublisher/workerInfo', {
          params: {
            taskPublisherId: this.taskInfo.id
          }
        }).then((res) => {
          let result = res.data
          if (result.code === 0) {
            let workerInfo = result.data
            myChart.setOption({
              radar: {
                indicator: [
                  {text: '等级', max: 7},
                  {text: '经验', max: workerInfo.exp < 500 ? 500 : workerInfo.exp * 1.2},
                  {text: '积分', max: workerInfo.point < 500 ? 500 : workerInfo.point * 1.2},
                  {text: '准确率', max: 100},
                  {text: '准时率', max: 100}
                ]
              },
              series: [{
                data: [{
                  value: [workerInfo.level, workerInfo.exp, workerInfo.point,
                    workerInfo.accuracyRate, workerInfo.punctualityRate]
                }]
              }]
            })
          }
        })
      },
      drawProgress: function () {
        let url = ''
        let params = {}
        if (this.$store.getters.userType === 1) {
          url = '/statistics/taskPublisher/progress'
          params = {
            taskPublisherId: this.taskInfo.id
          }
        } else if (this.$store.getters.userType === 0) {
          url = '/statistics/taskWorker/progress'
          params = {
            taskWorkerId: this.taskInfo.id
          }
        }
        this.$ajax.get(url, {
          params: params
        }).then((res) => {
          let result = res.data
          if (result.code === 0) {
            this.progress = (result.data * 100).toFixed(0)
          }
        })
      }
    },
    mounted () {
      let taskInfo = localStorage.getItem('taskInfo')
      let state = localStorage.getItem('taskState')
      if (taskInfo) {
        this.taskInfo = JSON.parse(taskInfo)
        this.state = state
      }

      // 是发起者
      if (this.$store.getters.userType === 1) {
        this.drawProgress()
        this.drawRateChart()
        this.drawWorkerChart()
      } else if (this.$store.getters.userType === 0) {
        this.drawProgress()
        if (this.state === 'PASS') {
          this.$ajax.get('/user/' + this.taskInfo.id).then((res) => {
            let result = res.data
            if (result.code === 0) {
              let taskWorker = result.data
              this.rateValue = taskWorker.rating
              if (this.rateValue !== 0) {
                this.rateOnlyRead = true
              }
            }
          })
        }
      }
    }
  }
</script>

<style lang="stylus">
  .task-detail
    position relative
    padding 50px 100px
    min-height: 100vh
    background #fff
    font-size: 15px
    font-weight 400
    background-color #f6f6f7
    .breadcrumb-nav
      margin-bottom 20px
      .el-breadcrumb__inner
        font-size 18px
        font-weight 400
      .el-breadcrumb__item:last-child .el-breadcrumb__inner, .el-breadcrumb__item:last-child .el-breadcrumb__inner a, .el-breadcrumb__item:last-child .el-breadcrumb__inner a:hover, .el-breadcrumb__item:last-child .el-breadcrumb__inner:hover
        color: #409eff
        font-weight 400
    .el-row
      margin-bottom: 20px
    .header
      display inline-block
      margin-bottom: 25px
      padding-bottom 10px
      border-bottom 3px solid rgba(7, 17, 27, 0.3)
      width auto
      color #888
      font-size 20px
      font-weight: 400
    .task-label-wrapper
      margin-bottom 60px
      .title
        margin-bottom 30px
        font-size: 28px
        font-weight: 700
      .col-left, .col-right
        display inline-block
        margin-right 25px
      .line
        margin-bottom 20px
        .label
          color: #888
        .task-type, .start-date
          margin-right 20px
        .hotrank
          display inline-block
          vertical-align top
          margin-top 2px
      .topics
        margin-right 10px
        border-radius 15px
        padding 0 13px
    .reward-box
      margin-bottom 30px
      .block
        margin-bottom 20px
        .label
          margin-bottom 12px
          font-weight: 350
          color: #888
        .text
          color #666
          font-weight: 700
          font-size: 24px
      .el-button
        width: 100%
        border none
</style>
