<template>
  <div class="task-card" @click="enterTask">
    <el-card :body-style="{ padding: '0px' }" v-if="taskPublisherId !== -1" style="position: relative"
             @mouseenter.native="toggleRate" @mouseleave.native="toggleRate">
      <div class="image-wrapper">
        <img class="image" :src="getBanner">
      </div>
      <transition name="slide" v-if="inRecommend">
        <div class="rate-wrapper" v-show="showRate"  @click.stop="clickNotLikeBtn">
          <div class="text">
            <i class="iconfont" style="font-size: 28px; display: inline-block; vertical-align: top; margin-top: -1px">&#xe73f;</i>
            <span style="display: inline-block; vertical-align: top">不感兴趣</span></div>
        </div>
      </transition>
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
</template>

<script type="text/ecmascript-6">
  import Topic from '../../components/topic/topic'
  import Star from '../../components/star/star'

  export default {
    name: 'task-card',
    components: {Topic, Star},
    props: {
      state: {
        type: String
      },
      taskInfo: {
        type: Object
      },
      inRecommend: {
        type: Boolean,
        default: false
      }
    },
    data () {
      return {
        showRate: false,
        taskPublisherId: 0
      }
    },
    computed: {
      getBanner: function () {
        if (this.taskPublisherId === 0) {
          return '/static/image/default_banner.jpg'
        } else {
          return '/show/' + this.taskPublisherId + '/' + this.taskInfo.picName
        }
      }
    },
    mounted () {
      if (this.$store.getters.userType === 1 || this.state === 'new') {
        this.taskPublisherId = this.taskInfo.id
      } else {
        this.$ajax.get('/user/' + this.taskInfo.id).then((res) => {
          let result = res.data
          this.taskPublisherId = result.data.taskId
        })
      }
    },
    methods: {
      enterTask: function () {
        this.$ajax.get('/recommend/' + this.$store.getters.id + '/view', {
          params: {
            topics: this.taskInfo.topics.join(",")
          }
        }).then((res) => {
        }).catch(() => {
          console.log('更新查看因子失败')
        })
        localStorage.setItem('taskInfo', JSON.stringify(this.taskInfo))
        localStorage.setItem('taskState', this.state)
        if (this.$route.path === '/task-detail') {
          this.$router.push('/white')
          this.$router.go(-1)
        } else {
          this.$router.push('/task-detail')
        }
      },
      taskType: function (t) {
        let type = ['分类', '标框', '区域']
        return type[t]
      },
      clickNotLikeBtn: function () {
        alert('hhh')
      },
      toggleRate: function () {
        this.showRate = !this.showRate
      }
    }
  }
</script>

<style lang="stylus">
  .task-card
    margin-bottom 50px
    width 100%
    cursor: pointer
    .slide-enter-active, .slide-leave-active
      transition: all .5s
    .slide-enter, .slide-leave-to
      opacity: 0
    .image-wrapper
      width 100%
      height 150px
      overflow hidden
      .image
        width: 100%
    .rate-wrapper
      position absolute
      top: 0
      left: 0
      width 100%
      height 150px
      background-color rgba(255, 255, 255, 0.8)
      text-align center
      .text
        line-height 150px
        font-size 18px
        &:hover
          color: #ff383a
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
</style>
