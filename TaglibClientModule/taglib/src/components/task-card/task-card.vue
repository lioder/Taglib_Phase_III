<template>
  <div class="task-card" @click="enterTask">
    <el-card :body-style="{ padding: '0px' }" v-if="taskPublisherId !== -1">
      <div class="image-wrapper">
        <img class="image" :src="getBanner">
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
      }
    },
    data () {
      return {
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
      if (this.$store.getters.usberType === 1 || this.state === 'new') {
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
        localStorage.setItem('taskInfo', JSON.stringify(this.taskInfo))
        localStorage.setItem('taskState', this.state)
        this.$router.push('/task-detail')
      },
      taskType: function (t) {
        let type = ['分类', '标框', '区域']
        return type[t]
      }
    }
  }
</script>

<style lang="stylus">
  .task-card
    margin-bottom 50px
    width 100%
    cursor: pointer
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
