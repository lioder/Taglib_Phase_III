<template>
  <div class='tasks' v-loading.fullscreen.lock="fullscreenLoading">
    <div id="head"></div>
    <div class="task-wrapper">
      <div class="task-head" style="height: 70px;">
        <h1 class="title" style="float: left">任务列表</h1>
        <div class="seq-filter" style="float: right">
          <el-radio-group v-model="sortBy" size="medium">
            <el-radio-button label="全部">全部</el-radio-button>
            <el-radio-button label="时间" @click.native="changeSec">时间 <i
              :class="{'el-icon-caret-bottom': isSec,'el-icon-caret-top': !isSec}" v-if="sortBy === '时间'"></i>
            </el-radio-button>
            <el-radio-button label="好评率" @click.native="changeSec">好评率 <i
              :class="{'el-icon-caret-bottom': isSec,'el-icon-caret-top': !isSec}" v-if="sortBy === '好评率'"></i>
            </el-radio-button>
            <el-radio-button label="奖励" @click.native="changeSec">奖励 <i
              :class="{'el-icon-caret-bottom': isSec,'el-icon-caret-top': !isSec}" v-if="sortBy === '奖励'"></i>
            </el-radio-button>
            <el-radio-button label="任务量" @click.native="changeSec">任务量 <i
              :class="{'el-icon-caret-bottom': isSec,'el-icon-caret-top': !isSec}" v-if="sortBy === '任务量'"></i>
            </el-radio-button>
          </el-radio-group>
        </div>
        <div class="filters">
          <div class="cla-filter" v-show="false">
            <label>选择类别:</label>
            <span class="cla-item">动物</span>
            <span class="cla-item">植物</span>
            <span class="cla-item">车辆</span>
          </div>
        </div>
      </div>
      <div class="tasks" id="tasks">
        <div class="task-card" v-for="(taskInfo,index) in taskInfos" :key="index" @click="enterTask(index)">
          <el-card :body-style="{ padding: '0px' }">
            <div class="image-wrapper">
              <img class="image" :src="'/show/'+ taskInfo.id + '/' + taskInfo.picName">
            </div>
            <div class="content">
              <div class="task-title">{{ taskInfo.title }}</div>
              <star :score="taskInfo.rating" :size="24" class="hotrank"></star>
              <div class="price">{{ taskInfo.price }} T币</div>
              <div class="topics">
                <topic class="item" v-for="(item, index) in taskInfo.topics" :key="index" :text="item"></topic>
              </div>
              <div class="info-blocks">
                <div class="block">
                  <div class="label">任务量</div>
                  <div class="num"><strong>{{ taskInfo.picNum }}</strong><span class="unit">题</span></div>
                </div>
                <div class="block">
                  <div class="label">类型</div>
                  <div class="num"><strong>{{ taskType(taskInfo.taskType) }}</strong></div>
                </div>
              </div>
              <div class="startDate"><i class="iconfont">&#xe63b;</i> {{ taskInfo.startDate }} 发布</div>
            </div>
          </el-card>
        </div>
        <div class="task-card" v-for="item in 3" :key="item-3">
          <div class="empty"></div>
        </div>
      </div>
      <div v-show="totalItemNum === 0">
        <i class="el-icon-warning"></i>
        当前暂无可接受的任务
      </div>
      <div class="page" v-show="totalItemNum > 0">
        <el-pagination
          ref="pagination"
          background
          layout="prev, pager, next"
          :page-size="9"
          :current-page="page"
          :total="this.totalItemNum"
          prev-text="上一页"
          next-text="下一页"
          @current-change="changePage">
        </el-pagination>
      </div>
    </div>
  </div>

</template>

<script type="text/ecmascript-6">
  import Topic from '../../components/topic/topic'
  import Star from '../../components/star/star'

  export default {
    components: {Topic, Star},
    props: {
      keyword: {
        type: String
      }
    },
    name: 'tasks',
    watch: {
      'sortBy': function () {
        this.isSec = true
        this.getTaskInfos(1)
      },
      'isSec': function () {
        this.getTaskInfos(1)
      }
    },
    data () {
      return {
        secCount: 0,
        page: 1,
        sortBy: '全部',
        isSec: true,
        totalItemNum: 0,
        taskInfos: [],
        fullscreenLoading: false
      }
    },
    mounted () {
      this.getTaskInfos(1)
    },
    methods: {
      enterTask: function (index) {
        let taskInfo = this.taskInfos[index]
        localStorage.setItem('taskInfo', JSON.stringify(taskInfo))
        localStorage.setItem('taskState', 'new')
        this.$router.push('/task-detail')
      },
      taskType: function (t) {
        let type = ['分类', '标框', '区域']
        return type[t]
      },
      changeSec: function () {
        if (this.secCount % 2 === 0) {
          this.isSec = !this.isSec
        }
        this.secCount++
      },
      changePage: function (val) {
        this.page = val
        this.getTaskInfos(val)
      },
      getTaskInfos: function (page) {
        this.fullscreenLoading = true
        this.$ajax.get('/user/tasks/search', {
          params: {
            userId: Number(this.$store.getters.id),
            keyword: this.keyword,
            sortBy: this.sortBy,
            isSec: this.isSec,
            size: 9,
            page: page
          }
        }).then((response) => {
          this.page = page
          response = response.data
          let pageVO = response.data
          this.taskInfos = pageVO.data
          this.totalItemNum = pageVO.totalItemNum
          this.scrollTop()
        }).catch(() => {
          this.$message.error('查询失败')
        }).finally(() => {
          setTimeout(() => {
            this.fullscreenLoading = false
          }, 500)
        })
      },
      scrollTop: function () {
        let gotoTop = function () {
          let currentPosition = document.documentElement.scrollTop || document.body.scrollTop
          currentPosition -= 10
          if (currentPosition > 0) {
            window.scrollTo(0, currentPosition)
          } else {
            window.scrollTo(0, 0)
            clearInterval(timer)
            timer = null
          }
        }
        var timer = setInterval(gotoTop, 1)
      }
    }
  }
</script>

<style lang="stylus">
  .tasks
    .el-radio-button:focus:not(.is-focus):not(:active)
      -webkit-box-shadow: none
      box-shadow: none
    .task-wrapper
      padding 60px 9%
      @media(max-width 1110px)
        padding 60px 60px
      @media(max-width 1014px)
        padding 60px 100px
      .task-head
        .title
          margin-bottom 20px
          color: #333
          font-size 25px
        .filters
          margin-bottom 20px
          .el-icon-arrow-down, .el-icon-arrow-up
            font-weight 700
          .cla-filter
            margin-bottom 15px
            padding 10px 20px
            background-color #fff
            label
              margin-right 20px
              color: #888888
            .cla-item
              margin-right 15px

      .tasks
        display flex
        flex-wrap: wrap
        justify-content space-between
        align-items flex-start
        align-content flex-start
        @media(max-width 1014px)
          width 700px
        @media(max-width 815px)
          width 300px
        #head
          display none
          width: 0
          height 0

        .task-card
          margin-bottom 50px
          width 28.5%
          flex 0 0 300px
          &.empty
            visibility hidden
            width 0
            height 0
          .image-wrapper
            width 100%
            height 150px
            overflow hidden
            .image
              width: 100%
          .content
            position: relative;
            margin 20px 20px
            text-align center
            .task-title
              margin-bottom 8px
              font-size 20px
              font-weight 700
              color #333
            .hotrank
              transform translateX(0)
              margin-bottom 10px
            .price
              margin-bottom 15px
              color #ff383a
              font-size 16px
            .topics
              margin-bottom 15px
              .item
                margin-right 10px
                &:last-child
                  margin-right none
            .info-blocks
              display flex
              margin-bottom 15px
              .block
                flex: 1
                border-right 1px solid rgba(7, 17, 27, 0.1)
                &:last-child
                  border-right 0
                .label
                  margin-bottom 10px
                  color #999
                  font-size 14px
                .num
                  color #000
                  font-size 16px
                  .unit
                    font-size 12px

            .startDate
              color: #888
      .page
        text-align center
        .btn-next, .btn-prev
          padding 0 10px
          border 1px solid rgba(7, 17, 27, 0.5)
          &.disabled
            border 1px solid #c0c4cc
        .el-pager li
          border 1px solid rgba(7, 17, 27, 0.5)
          &.active
            border none
</style>
