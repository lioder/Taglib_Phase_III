<template>
  <div class="home-view">
    <div class="hot-task-wrapper">
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
    <div class="recommend-wrapper">
      <h1 class="header">精彩推荐</h1>
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
        }]
      }
    },
    mounted () {
      this.getHotTasks()
      this.getRecommendTasks()
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
      getRecommendTasks: function () {
        this.$ajax.get('/recommend/tasks', {
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
        })
      },
      getHotTasks: function () {
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
</style>
