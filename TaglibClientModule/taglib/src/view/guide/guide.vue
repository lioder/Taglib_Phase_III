<template>
  <div class='guide'>
    <section class="carousel">
      <el-carousel height="580px" :interval="3000" indicator-position="none" arrow="never">
        <el-carousel-item v-for="item in 2" :key="item">
          <img :src="getBanner(item)">
        </el-carousel-item>
      </el-carousel>
      <div class="slogan">
        高品质众包标注平台
      </div>
      <div class="sub-slogan">
        专业的标注平台，为你保障数据质量
      </div>
      <div class="btn-bar">
        <el-button round type="plain" class="btn1" @click="toLogin">加入成为工人</el-button>
        <el-button round type="primary" @click="toLogin">快速发布任务</el-button>
      </div>
    </section>
    <section class="feature">
      <div class="title">
        平台优势
      </div>
      <div style="border-bottom: 1px solid rgba(7, 17, 27, 0.1); margin-bottom: 50px">
        <div class="sub-title">如果你想成为众包工人</div>
        <div class="content">
          <div class="block">
            <div class="image-box"><img src="./check.png"></div>
            <div class="block-title">强壮的审核机制</div>
            <ul>
              <li>每一个发布的项目都必须通过人工审核，确保项目靠谱真实</li>
              <li>每一位专家都经过管理员的认真审核，确保专家认真负责</li>
              <li>每一位工人的标注都会经过算法自动审核，确保奖励公正公平</li>
            </ul>
          </div>
          <div class="block">
            <div class="image-box"><img src="./label.png"/></div>
            <div class="block-title">优质的标注体验</div>
            <ul>
              <li>精美的UI界面和动画，带给你丝滑的标注体验</li>
              <li>快捷的标注工具，让你可以高效的完成任务</li>
              <li>多种任务选择入口，帮助您找到最喜欢最拿手的任务</li>
            </ul>
          </div>
          <div class="block">
            <div class="image-box">
              <img src="./point.png" width="49" height="49"/>
            </div>
            <div class="block-title">丰厚的奖励回报</div>
            <ul>
              <li>每一次成功完成任务，都会有不定数额的积分奖励</li>
              <li>准确率越高的用户，将在任务结束后分得更多的奖励</li>
              <li>努力成为专家用户，参与回报更丰富的专家任务</li>
            </ul>
          </div>
        </div>
      </div>
      <div>
        <div class="sub-title">如果你想成为众包发起者</div>
        <div class="content">
          <div class="block">
            <div class="image-box"><img src="./publish.png" width="49" height="49"></div>
            <div class="block-title">便捷的任务发布</div>
            <ul>
              <li></li>
              <li>支持压缩包上传，免去多选图片的繁琐</li>
              <li>支持自由定价，人数价格由您掌控</li>
              <li>快捷的标签选择，让您的任务更有个性</li>
            </ul>
          </div>
          <div class="block">
            <div class="image-box"><img src="./point.png" width="49" height="49"/></div>
            <div class="block-title">高效的资金利用</div>
            <ul>
              <li>平台已接入支付宝，快捷支付奖赏资金</li>
              <li>平台丰富的奖惩机制，确保尽心尽责的工人获得更多的回报</li>
              <li>兴趣推荐，让您的任务被更专业的工人完成</li>
            </ul>
          </div>
          <div class="block">
            <div class="image-box">
              <img src="./result.png" width="49" height="49"/>
            </div>
            <div class="block-title">优质的标注结果</div>
            <ul>
              <li>强大的标注审核算法，自动生成最优标注结果</li>
              <li>专家先标，确保优质的工人答案进入算法，减少干扰</li>
              <li>一键下载标注数据，可视化展示任务进度，让您省时放心</li>
            </ul>
          </div>
        </div>
      </div>
    </section>
    <footer>2018 © Crafted by Horizon</footer>
  </div>
</template>

<script type="text/ecmascript-6">
  export default {
    name: 'guide',
    methods: {
      getBanner: function (item) {
        if (item) {
          return '/static/image/banner' + item + '.jpg'
        }
      },
      toLogin: function () {
        this.$router.push('/login')
      }
    }
  }
</script>

<style lang="stylus">
  .guide
    width: 100%
    height: 100%
    .carousel
      position relative
      height 580px
      .layer
        z-index: 10
        position absolute
        left 0
        top 0
        width: 100%
        height 580px
        background-color rgba(255, 255, 255, 0.5)
      .slogan
        z-index: 100
        position absolute
        left 0
        top 200px
        width: 100%
        text-align center
        color #fff
        font-size 50px
        letter-spacing 10px
      .sub-slogan
        z-index: 100
        position absolute
        left 0
        top 280px
        width: 100%
        text-align center
        color #fff
        font-size 26px
      .btn-bar
        z-index: 100
        position absolute
        left 0
        top 400px
        width: 100%
        text-align center
        transform translateX(10px)
        .el-button
          z-index: 100
          line-height 56px
          font-size 18px
          padding 0 25px
          margin-right 10px
          border-radius 30px
        .btn1
          background-color transparent
          color #fff
          border 2px #fff solid

    .feature
      padding 50px 100px
      .title
        margin-bottom 50px
        width 100%
        text-align center
        font-size 36px
        letter-spacing 7px
      .sub-title
        margin-bottom 50px
        width 100%
        text-align center
        font-size 28px
      .content
        display flex
        .block
          flex 1
          padding-bottom 50px
          .image-box
            margin auto
            width 110px
            height 110px
            padding-top 30px
            box-sizing border-box
            border-radius 50%
            background-color #A4DEF9
            text-align center
          .block-title
            margin 30px 0
            width 100%
            text-align center
            font-size 20px
          ul
            text-align center
            opacity 0.7
            li
              margin-bottom 10px
    footer
      width: 100%
      height 50px
      padding 20px 0 0 0
      text-align center
</style>
