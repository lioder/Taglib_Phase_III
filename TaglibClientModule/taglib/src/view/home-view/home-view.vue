<template>
  <div class="home-view" v-loading.fullscreen.lock="fullscreenLoading1 || fullscreenLoading2">
    <div class="hot-task-wrapper" v-show="hotTasks.length > 0">
      <el-carousel height="400px" class="hot-task" @change="changeCarousel" ref="carousel" indicator-position="none"
                   arrow="never">
        <el-carousel-item v-for="(item, index) in hotTasks" :key="index">
          <div class="carousel-item">
            <img :src="getBanner(item)" width="100%" @click="enterTask">
          </div>
        </el-carousel-item>
        <div class="task-info">
          <h1 v-for="(item,index) in hotTasks" :key="index" class="title" :class="{'active': index === hotTaskIndex}"
              @click="clickTitle(index)">{{ item.title }}</h1>
        </div>
      </el-carousel>
    </div>
    <div class="latest-wrapper" v-show="latestTasks.length > 0">
      <h1 class="header">最新任务</h1>
      <div class="card-content">
        <div class="task-card-wrapper" v-for="(item,index) in latestTasks" :key="index+7">
          <task-card :task-info="item" :state="'new'"></task-card>
        </div>
        <div class="task-card-wrapper" v-for="(item,index) in 3" :key="index+20">
          <div class="empty"></div>
        </div>
      </div>
    </div>
    <div class="recommend-wrapper" v-show="recommendTasks.length > 0">
      <h1 class="header">猜你喜欢</h1>
      <div class="card-content">
        <div class="task-card-wrapper" v-for="(item,index) in recommendTasks" :key="index">
          <task-card :task-info="item" :state="'new'"></task-card>
        </div>
        <div class="task-card-wrapper" v-for="(item,index) in 3" :key="index+20">
          <div class="empty"></div>
        </div>
        <div class="link">没有喜欢的任务？去
          <router-link to="/tasks">任务中心</router-link>
          看看吧
        </div>
      </div>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
  import TaskCard from '../../components/task-card/task-card'

  export default {
    components: {TaskCard},
    name: 'home-view',
    data () {
      return {
        hotTaskIndex: 0,
        recommendTasks: [],
        hotTasks: [{
          title: '默认标题'
        }],
        latestTasks: [],
        fullscreenLoading1: false,
        fullscreenLoading2: false
      }
    },
    mounted () {
      this.getHotTasks()
      this.getRecommendTasks()
      this.getLatestTasks()
    },
    methods: {
      getBanner: function (item) {
        if (item) {
          return '/show/' + item.id + '/' + item.picName
        } else {
          return '/static/image/default_banner.jpg'
        }
      },
      clickTitle: function (index) {
        this.$refs.carousel.setActiveItem(index)
      },
      changeCarousel: function (newValue, oldValue) {
        this.hotTaskIndex = newValue
      },
      getLatestTasks: function () {
        this.$ajax.get('/user/tasks/search', {
          params: {
            userId: Number(this.$store.getters.id),
            keyword: '',
            sortBy: '时间',
            isSec: true,
            size: 9,
            page: 1
          }
        }).then((response) => {
          response = response.data
          let pageVO = response.data
          this.latestTasks = pageVO.data
        }).catch(() => {
          this.$message.error('获取最新任务失败')
        }).finally(() => {
          this.fullscreenLoading = false
        })
      },
      getRecommendTasks: function () {
        this.fullscreenLoading1 = true
        this.$ajax.get('/recommend/user', {
          params: {
            userId: this.$store.getters.id
          }
        }).then((res) => {
          let result = res.data
          if (result.code === 0) {
            this.recommendTasks = result.data
          } else {
            this.$message.error(result.message)
          }
        }).finally(() => {
          this.fullscreenLoading1 = false
        })
      },
      getHotTasks: function () {
        this.fullscreenLoading2 = true
        this.$ajax.get('/recommend/hotTask', {
          params: {
            userId: this.$store.getters.id
          }
        }).then((res) => {
          let result = res.data
          if (result.code === 0) {
            this.hotTasks = result.data
          } else {
            this.$message.error(result.message)
          }
        }).finally(() => {
          this.fullscreenLoading2 = false
        })
      },
      enterTask: function () {
        localStorage.setItem('taskInfo', JSON.stringify(this.hotTasks[this.hotTaskIndex]))
        localStorage.setItem('taskState', 'new')
        this.$router.push('/task-detail')
      }
    }
  }
</script>

<style lang="stylus">
  .home-view
    .hot-task-wrapper
      width: 100%
      display relative
      .el-carousel__indicators
        margin-bottom 20px
        .el-carousel__button
          background-color #f6f6f7
          &.is-active
            background: #fff
      .task-info
        position absolute
        top: 70px
        right 170px
        z-index 100
        padding: 5px 15px
        box-sizing border-box
        background-color rgba(7, 17, 27, 0.7)
        .title
          padding 12px 5px
          font-size: 16px
          font-weight 400
          color: #ccc
          border-bottom 1px solid rgba(255, 255, 255, 0.3)
          cursor pointer
          &.active
            color: #fff
          &:last-child
            border none
    .recommend-wrapper
      padding 50px 9%
      background linear-gradient(#fff, #f5f6f7)
      @media (max-width 1244px)
        padding 50px 4%
      @media (max-width 990px)
        padding 50px 0.5%
      @media (max-width 855px)
        padding 50px 50px
      .header
        width 100%
        margin-bottom 50px
        text-align center
        color #333
        font-size 32px
        letter-spacing 7px
      .card-content
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
        .link
          width 100%
          text-align center
          letter-spacing 2px
          a
            color: #409eff
    .latest-wrapper
      padding 50px 9%
      background linear-gradient(#fff, #f5f6f7)
      @media (max-width 1244px)
        padding 50px 4%
      @media (max-width 990px)
        padding 50px 0.5%
      @media (max-width 855px)
        padding 50px 50px
      .header
        width 100%
        margin-bottom 50px
        text-align center
        color #333
        font-size 32px
        letter-spacing 7px
      .card-content
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
