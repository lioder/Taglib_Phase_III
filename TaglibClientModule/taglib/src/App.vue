<template>
  <div id="app">
    <router-view class="main-view"></router-view>
  </div>
</template>

<script type="text/ecmascript-6">
  import store from './store/index'
  export default {
    name: 'App',
    store,
    data () {
      return {}
    },
    methods: {}
  }
</script>

<style lang="stylus">
  @import "../static/css/reset.css"

  #app
    font-family 'PingFang SC', 'Microsoft YaHei', Helvetica, Arial, sans-serif
    -webkit-font-smoothing antialiased
    -moz-osx-font-smoothing grayscale
    width 100%
    min-height 100vh
    font-size 14px
    background #f6f6f7
    .main-view
      width: 100%
</style>
