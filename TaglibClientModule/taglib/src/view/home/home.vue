<template>
  <div class="home">
    <header>
      <nav class="head-nav">
        <div class="fl">
          <a class="brand" href="/#/home">
            <img src="/static/image/logo.png" width="169" height="44" alt="" class="brand-img">
          </a>
          <div class="links">
            <router-link to="/home-view" class="link" v-if="userType === 0" @click.native="refresh">精选</router-link>
            <router-link to="/tasks" class="link" v-if="userType === 0" @click.native="refresh">任务中心</router-link>
            <router-link to="/publish" class="link" v-if="userType === 1">发布任务</router-link>
            <router-link to="/myTasks" class="link">我的任务</router-link>
            <router-link to="/rank" class="link">排行榜</router-link>
          </div>
          <el-input
            v-if="this.$store.getters.userType === 0"
            class="search-input"
            placeholder="搜索想要的任务"
            size="small"
            suffix-icon="el-icon-search"
            v-model="keyword"
            clearable
            ref="searchInput"
            @keyup.enter.native="search">
          </el-input>
        </div>
        <div class="fr">
          <i class="el-icon-bell" v-show="false"></i>
          <div class="user-wrapper">
            <img class="avatar" :src="getAvatar" width="30px" height="30px">
            <el-dropdown class="username" size="medium" placement="bottom" @command="handleCommand">
              <span class="el-dropdown-link">
                {{ username }}<i class="el-icon-arrow-down el-icon--right"></i>
              </span>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item></el-dropdown-item>
                <el-dropdown-item command="user">个人主页</el-dropdown-item>
                <el-dropdown-item divided command="logout">退出登录</el-dropdown-item>
              </el-dropdown-menu>
            </el-dropdown>
          </div>
        </div>
      </nav>
    </header>
    <div class="home-view">
      <router-view :keyword="keyword" ref="child">
      </router-view>
    </div>
  </div>
</template>

<script type="text/ecmascript-6">
  import {mapMutations, mapGetters} from 'vuex'

  export default {
    name: 'home',
    data () {
      return {
        keyword: ''
      }
    },
    computed: {
      ...mapGetters([
        'username',
        'userType',
        'avatar',
        'id'
      ]),
      getAvatar: function () {
        if (this.$store.getters.avatar === 'default_avatar.png') {
          return '/static/image/default_avatar.png'
        } else {
          return '/show/avatar/' + this.$store.getters.id + '/' + this.$store.getters.avatar
        }
      }
    },
    methods: {
      ...mapMutations({
        userLogout: 'logout'
      }),
      refresh: function () {
        if (!this.$store.getters.isLogin) {
          this.$router.push('/login')
        } else {
          this.$router.push('/white')
          this.$router.go(-1)
        }
      },
      handleCommand: function (command) {
        switch (command) {
          case 'logout':
            this.userLogout()
            this.$router.push('/login')
            break
          case 'user':
            this.$router.push('/user')
        }
      },
      search: function () {
        this.$router.push('/tasks')
        this.$refs.child.getTaskInfos(1)
      }
    }
  }
</script>

<style lang="stylus">
  .home
    background-color #f6f6f7
    .head-nav
      position fixed
      left 0
      top 0
      z-index: 1000
      box-sizing border-box
      width 100%
      height: 65px
      font-size 16px
      color: #888
      background-color #ffffff
      box-shadow 2px 0 5px #888888
      .app-name
        font-size 20px
      .fl
        float left
        .brand-img
          margin-left 20px
          padding 10px 0
        .links
          display inline-block
          vertical-align top
          margin-right 15px
          padding-top 25px
          .link
            margin-right 15px
          .active
            color: #409eff
        .search-input
          display inline-block
          padding-top 18px
          vertical-align top
          width: 200px
          .el-input__suffix
            line-height 68px
      .fr
        float right
        padding-top 18px
        padding-right 20px
        .el-icon-bell, .username
          display inline-block
          vertical-align top
          padding-top 8px
        .el-icon-bell
          margin-right 15px
          font-size 18px
        .user-wrapper
          display inline-block
          .avatar
            border-radius 50%
            margin-right 5px
          .username
            display inline-block
            vertical-align top
            font-size: 16px
    .home-view
      padding-top 33px
      background-color #f6f6f7

</style>
