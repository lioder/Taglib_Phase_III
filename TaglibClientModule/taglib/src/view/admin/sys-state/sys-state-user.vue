<template>
  <div class="sys-state-user">
    <router-view></router-view>
  </div>
</template>

<script type="text/ecmascript-6">
  export default {
    name: 'sys-state'
  }
</script>

<style lang="stylus">
  .sys-state-user
    padding 25px 65px
    .tab-header
      position relative
      margin-bottom 30px
      .tab
        z-index: 100
        display inline-block
        margin 0 20px 0 0
        color: #303133
        height 40px
        line-height 40px
        font-size 14px
        font-weight 500
        &:hover
          color: #409eff
        &.active
          color: #409eff
          border-bottom 2px solid #409eff
      .line
        z-index: 0
        position absolute
        left 0
        bottom 0
        width 100%
        height 2px
        background-color #e4e7ed
      .active-line
        position absolute
        left 0
        bottom 0
        width 55px
        height 2px
        background-color #409eff

</style>
