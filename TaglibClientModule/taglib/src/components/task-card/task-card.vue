<template>
  <div class="task-card" @click="enterTask">
    <section class="container">
      <div id="card" :class="{'flipped': !front}">
        <figure class="front">
          <transition name="drop">
            <div class="ribbon" v-show="showRibbon">
              <div class="wrap">
                <span>领取奖励</span>
              </div>
            </div>
          </transition>
          <el-card :body-style="{ padding: '0px' }" v-if="taskPublisherId !== -1" style="position: relative"
                   @mouseenter.native="toggleRate" @mouseleave.native="toggleRate">
            <div class="image-wrapper">
              <img class="image" :src="getBanner" v-show="showImage">
            </div>
            <transition name="slide" v-if="inRecommend">
              <div class="rate-wrapper" v-show="showRate" @click.stop="clickNotLikeBtn">
                <div class="text">
                  <i class="iconfont"
                     style="font-size: 28px; display: inline-block; vertical-align: top; margin-top: -1px">&#xe73f;</i>
                  <span style="display: inline-block; vertical-align: top">不感兴趣</span></div>
              </div>
            </transition>
            <div class="content">
              <div class="task-title">{{ taskInfo.title }}</div>
              <el-rate v-model="taskInfo.rating" disabled class="hotrank"></el-rate>
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
        </figure>
        <figure class="back">
          <div class="reward-wrapper"
               style="padding: 0 20px; background-image: linear-gradient(to top, #e6e9f0 0%, #f8fbff 100%);">
            <img src="../../../static/image/pure-logo.png" style="display: inline-block" width="55" height="55">
            <div style="margin: 30px 0 10px 0; width: 120px;overflow: hidden;display: inline-block">
              <img src="../../../static/image/logo.png" style="transform: translateX(-45px) translateY(4px)">
            </div>
            <div style="color: #409eff; font-size: 24px; margin-bottom: 35px">任务卡</div>
            <div style="color: #777">本次任务收获</div>
            <div style="margin: 20px 0; display: flex">
              <div class="block" style="flex: 1">
                <div style="font-size: 30px; color: #409eff; margin-bottom: 8px">3</div>
                <div style="color: #777">T币</div>
              </div>
              <div class="block" style="flex: 1">
                <div style="font-size: 30px; color: #409eff; margin-bottom: 8px">8</div>
                <div style="color: #777">经验</div>
              </div>
              <div class="block" style="flex: 1">
                <div style="font-size: 30px; color: #409eff; margin-bottom: 8px">78%</div>
                <div style="color: #777">准确率</div>
              </div>
            </div>
            <div style="line-height: 20px; color: #777">打败了 <span style="color: #409eff">89.9%</span> 的标注者<br>要继续努力哦！
            </div>
            <el-button round>领取奖励</el-button>
          </div>
        </figure>
      </div>
    </section>
  </div>
</template>

<script type="text/ecmascript-6">
  import Topic from '../../components/topic/topic'

  export default {
    name: 'task-card',
    components: {Topic},
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
        front: true,
        showImage: true,
        showRibbon: true,
        showRate: false,
        taskPublisherId: 0,
        taskRecord: {
          taskRecordId: 0
        }
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
          // 异步任务，必须先等 taskPublisherId 取得
          if (this.$store.getters.userType === 0 && this.state === 'PASS') {
            this.$ajax.get('/user/taskRecord', {
              params: {
                taskPublisherId: this.taskPublisherId,
                userId: this.$store.getters.id
              }
            }).then((res) => {
              let result = res.data
              if (result.code === 0) {
                let taskRecord = result.data
                this.showRibbon = !taskRecord.haveSeen
                this.taskRecord = taskRecord
              }
            }).catch(() => {
              this.$message.error('获取审核奖励失败')
            })
          }
        })
      }
    },
    methods: {
      enterTask: function () {
        // 如果有奖励可以领取，则不进入任务详情界面，而是弹出宝箱
        if (this.showRibbon) {
          this.$ajax.get('/user/task-record/open-reward/' + this.taskRecord.taskRecordId).then((res) => {
            let result = res.data
            if (result.code === 0) {
              this.showRibbon = false
            }
          }).catch(() => {
            this.$message.error('领取奖励失败')
          })
        }
        setTimeout(() => {
          this.front = !this.front
        }, 500)

        setTimeout(() => {
          this.showImage = !this.showImage
        }, 800)
        /*else {
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
        }*/
      },
      taskType: function (t) {
        let type = ['分类', '标框', '区域']
        return type[t]
      },
      clickNotLikeBtn: function () {
        this.$ajax.get('/recommend/' + this.$store.getters.id + '/not-like', {
          params: {
            topics: this.taskInfo.topics.join(",")
          }
        }).then((res) => {
          let result = res.data
          if (result.code === 0) {
            this.$emit('notlike', this.taskPublisherId)
            this.$message.success('反馈成功，将为您减少此类话题的任务推荐')
          }
        }).catch(() => {
          this.$message.error('评价失败，网络似乎出了问题')
        })
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
    .drop-enter-active, .drop-leave-active
      transition: all .5s
    .drop-enter, .drop-leave-to
      transform translate3d(40px, -40px, 0) scale3d(1, 0, 0)
      opacity: 0
    .container
      position relative
      width: 280px
      height 390px
      perspective 800px
      #card
        width: 100%;
        height: 100%;
        position: absolute;
        transform-style: preserve-3d;
        transition: transform 1s;
        figure
          display: block;
          position: absolute;
          width: 100%;
          height: 100%;
          backface-visibility: hidden;
        .back
          transform rotateY(180deg)
          .reward-wrapper
            text-align center
            position: relative
            width 240px
            height 100%
            .el-button
              position absolute
              left 90px
              bottom 30px
              border 1px solid #409eff
              color #409eff
            .el-button:hover
              background-color #409eff
              color white
      .flipped
        transform: rotateY(180deg)
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
    .ribbon
      position: relative
      z-index 100
      animation-duration: 0.5s;
      .wrap
        overflow hidden
        position: absolute
        top -8px
        left 8px
        width 100%
        height 188px
        span
          display inline-block
          position: absolute
          top 30px
          right -50px
          width: 200px
          height 40px
          line-height 40px
          overflow hidden
          border 1px dashed
          text-align center
          transform rotate(45deg)
          color white
          background #ff383a
          box-shadow 0 0 0 3px #ff383a, 0px 21px 5px -18px rgba(0, 0, 0, 0.6)
          letter-spacing 5px
        &:before
          content: "";
          display: block;
          border-radius: 8px 8px 0px 0px
          width: 40px;
          height: 10px;
          position: absolute;
          top: 0px
          right: 100px;
          background: #a72224;
          box-sizing border-box
        &:after
          z-index: -10
          content: "";
          display: block;
          border-radius: 0px 8px 8px 0px;
          width: 8px;
          height: 43px;
          position: absolute;
          right: 1px;
          top: 99px;
          background: #a72224;
          box-sizing border-box
</style>
