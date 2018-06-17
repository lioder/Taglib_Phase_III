<template>
  <div class="admin">
    <div class="left">
      <el-menu
        ref="menu"
        :router="true"
        default-active="system-state-user-growth"
        class="menu"
        background-color="#545c64"
        text-color="#fff"
        active-text-color="#ffd04b">
        <el-submenu index="system-state">
          <template slot="title">
            <i class="el-icon-location"></i>
            <span>查看系统统计</span>
          </template>
          <el-menu-item-group title="用户统计">
            <el-menu-item index="system-state-user-growth">用户增长趋势</el-menu-item>
            <el-menu-item index="system-state-user-accuracy">用户准确率</el-menu-item>
            <el-menu-item index="system-state-user-amount">用户数量</el-menu-item>
          </el-menu-item-group>
          <el-menu-item-group title="任务统计">
            <el-menu-item index="system-state-task-type">任务类型</el-menu-item>
            <el-menu-item index="system-state-task-state">任务状态</el-menu-item>
            <el-menu-item index="system-state-task-timeline">任务生命周期</el-menu-item>
          </el-menu-item-group>
        </el-submenu>
        <el-menu-item index="check-task">
          <i class="el-icon-document"></i>
          <span slot="title">审核发起的任务</span>
        </el-menu-item>
        <el-menu-item index="check-expert">
          <i class="iconfont" style="display: inline-block; margin: 0 6px">&#xe61e;</i>
          <span slot="title">审核专家申请</span>
        </el-menu-item>
      </el-menu>
    </div>
    <div class="right">
      <router-view></router-view>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
  export default {
    name: 'admin',
    mounted () {
      let fromRoute = localStorage.getItem('fromRoute')
      if (fromRoute === '/board') {
        this.$refs.menu.activeIndex = 'check-tag'
        this.$router.push('/check-tag')
        localStorage.setItem('fromRoute', '')
      } else {
        this.$router.push('/system-state-user-growth')
      }
    }
  }
</script>

<style lang="stylus">
  .admin
    min-height: 100vh
    max-width 100vw
    display flex
    .left
      flex: 0 0 200px
      background-color #545c64
      .menu
        min-height 100vh
    .right
      flex: 1

</style>
