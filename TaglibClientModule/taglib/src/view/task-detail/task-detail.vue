<template>
  <div class="task-detail">
    <el-row :gutter="30" class="task-detail-row">
      <!--<div class="section-header">任务详情</div>-->
      <div class="breadcrumb-nav">
        <el-breadcrumb separator-class="el-icon-arrow-right">
          <el-breadcrumb-item v-if="fromPath === '/tasks'" :to="{ path: '/tasks' }">任务中心</el-breadcrumb-item>
          <el-breadcrumb-item v-if="fromPath === '/myTasks'" :to="{path: '/myTasks' }">我的任务</el-breadcrumb-item>
          <el-breadcrumb-item>任务详情</el-breadcrumb-item>
        </el-breadcrumb>
      </div>
      <el-col :span="15">
        <el-card class="col-left-card">
          <div class="task-label-wrapper">
            <div>
              <h1 class="title">{{ taskInfo.title }}</h1>
              <div class="topic-wrapper">
                <el-tag class="topics" v-for="(topic,index) in taskInfo.topics" :key="index">{{ topic }}</el-tag>
              </div>
            </div>
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
          </div>
          <div class="task-desc-wrapper">
            <span class="header">任务描述</span>
            <p class="task-desc">{{ taskInfo.description}}</p>
          </div>
        </el-card>
      </el-col>
      <el-col :span="9">
        <el-card class="reward-box">
          <div class="num-box">
            <div class="block">
              <div class="label">任务奖励</div>
              <div class="text">{{ taskInfo.price }} T币</div>
            </div>
            <div class="block">
              <div class="label">任务题量</div>
              <div class="text">{{ taskInfo.picNum }} 图</div>
            </div>
          </div>
          <div class="worker-btn" v-if="this.$store.getters.userType === 0">
            <el-button round @click="continueTask" type="danger" v-if="state === 'PROCESSING'">继续任务</el-button>
            <el-button round @click="acceptTask" type="danger" v-if="state === 'new'">开始任务</el-button>
          </div>
          <div class="publisher-btn" v-if="this.$store.getters.userType === 1" style="width: 100%">
            <el-button round type="plain" class="download-btn" v-if="state === 'DONE' || state=== 'OVERTIME'">
              <a :href="'/download/tag-data/' + taskInfo.id">
                <i class="el-icon-download" style="display: inline-block; vertical-align: top; font-size: 16px;"></i><span style="display: inline-block; vertical-align: top; font-size: 15px">下载标注数据</span>
              </a>
            </el-button>
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
    <el-row class="task-statistic-row" v-if="this.$store.getters.userType === 1">
      <el-card class="taskpub-rate-card">
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
    <div class="itemRE" v-if="this.state === 'new'">
      <div class="title">
        相似推荐
      </div>
      <div class="similar-task-wrapper">
        <div class="task-card-wrapper" v-for="(item,index) in similarTasks" :key="index">
          <task-card :task-info="item" :state="'new'"></task-card>
        </div>
        <div class="task-card-wrapper" v-for="(item,index) in 3" :key="index+20">
          <div class="empty"></div>
        </div>
      </div>
      <div class="no-similar-task" v-show="similarTasks.length === 0">没有相似的任务</div>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
  /* eslint-disable */
  import Star from '../../components/star/star'
  import TaskCard from '../../components/task-card/task-card'

  var echarts = require('echarts/lib/echarts')
  require('echarts/lib/chart/bar')
  require('echarts/lib/chart/radar')
  require('echarts/lib/component/title');

  export default {
    name: 'task-detail',
    components: {
      TaskCard,
      Star
    },
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
        },
        similarTasks: []
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
      } else if (this.$store.getters.userType === 0) { // 是工人
        if (this.state === 'PROCESSING') {
          this.drawProgress()
        }
        if (this.state === 'PASS') {
          // 处理评分
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
        // 获得相似任务推荐
        this.$ajax.get('/recommend/item', {
          params: {
            taskPublisherId: this.taskInfo.id,
            userId: this.$store.getters.id
          }
        }).then((res) => {
          let result = res.data
          if (result.code === 0) {
            this.similarTasks = result.data
          }
        }).catch(() => {
          this.$message.error('获取相似任务失败')
        })
      }
    }
  }
</script>

<style lang="stylus">
  .task-detail
    position relative
    min-height: 100vh
    font-size: 15px
    font-weight 400
    margin-top 40px
    background-color #fff
    .header
      display inline-block
      margin-bottom: 25px
      padding-bottom 10px
      border-bottom 3px solid rgba(7, 17, 27, 0.3)
      width auto
      color #888
      font-size 20px
      font-weight: 400
    .breadcrumb-nav
      margin-bottom 20px
      transform translateX(15px)
      .el-breadcrumb__inner
        font-size 18px
        font-weight 400
      .el-breadcrumb__item:last-child .el-breadcrumb__inner, .el-breadcrumb__item:last-child .el-breadcrumb__inner a, .el-breadcrumb__item:last-child .el-breadcrumb__inner a:hover, .el-breadcrumb__item:last-child .el-breadcrumb__inner:hover
        color: #409eff
        font-weight 400
    .task-detail-row
      padding 40px 100px 60px 100px
      background linear-gradient(to bottom, #f5f6f7 0%, #fff 100%)

      .task-desc-wrapper
        margin 0 10px 10px 10px
      .progress-card, .rate-card
        width 100%
        padding 10px
      .task-label-wrapper
        margin 10px 10px 40px 10px
        .title
          display inline-block
          vertical-align top
          margin-bottom 30px
          margin-right 15px
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
        .topic-wrapper
          display inline-block
          vertical-align top
          transform translateY(2px)
          .topics
            margin-right 10px
            border-radius 15px
            padding 0 13px
            height 25px
            line-height 23px
      .reward-box
        width: 100%
        margin-bottom 30px
        padding 10px 10px 0 10px
        .num-box
          width 100%
          display flex
          text-align center
          .block
            flex: 1
            border-right 1px solid rgba(7, 17, 27, 0.2)
            &:last-child
              border none
            .label
              margin-bottom 12px
              font-weight: 350
              color: #888
            .text
              color #666
              font-weight: 700
              font-size: 24px
        .worker-btn, .publisher-btn
          .el-button
            display block
            width: 100%
            margin-top: 30px
            border none
          .download-btn
            width: 100%
            border: 2px solid #1ECD97
            a
              color: #1ECD97
            &:hover
              background-color #1ECD97
              a
                color: #fff
    .task-statistic-row
      padding 0 100px 50px 100px
    .itemRE
      padding 50px 100px 50px 100px
      background-image: linear-gradient(to top, #fff 0%, #f5f6f7 100%);
      .title
        width 100%
        margin-bottom 50px
        text-align center
        color #333
        font-size 32px
        letter-spacing 7px
      .similar-task-wrapper
        display flex
        flex-flow row wrap
        justify-content space-between
        align-items flex-start
        align-content flex-start
        width: 100%
        margin 0 auto
        @media (max-width 1244px)
          width: 100%
        @media (max-width 1110px)
          width: 80%
        @media (max-width 855px)
          width: 300px
        .task-card-wrapper
          flex 0 0 320px
          margin 0 10px
          .empty
            width: 0
            height 0
            visibility hidden
</style>
