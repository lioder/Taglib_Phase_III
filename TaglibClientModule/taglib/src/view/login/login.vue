<template>
  <div class="login">
    <div class="form-wrapper">
      <div class="form">
        <div class="tab-head" slot="head">
          <img class="logo" src="/static/image/pure-logo.png" width="85px" height="84px">
          <router-link to="/login">登录</router-link>
          <router-link to="/sign">注册</router-link>
        </div>
        <router-view class="form-body"></router-view>
      </div>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
  export default {
    name: 'login'
  }
</script>

<style lang="stylus">
  .login
    width: 100%
    height 100vh
    display flex
    justify-content center
    align-items center
    background-image url("/static/image/login-bg.jpg")
    background-repeat no-repeat
    background-size 100% 100%
    .form-wrapper
      width 100%
      padding 0 32%
      .form
        padding 10px 70px
        background #fff
        box-sizing border-box
        .tab-head
          padding 2% 4% 0 4%
          margin 0 auto 15px auto
          text-align center
          .logo
            margin 0 auto 15px auto
            display block
          a
            margin-right: 10px
            font-size 20px
            &.active
              color #409eff
</style>
