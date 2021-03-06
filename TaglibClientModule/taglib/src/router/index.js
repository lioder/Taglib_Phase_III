import Vue from 'vue'
import Router from 'vue-router'
import store from '../store/index'

import Login from '../view/login/login.vue'
import Home from '../view/home/home.vue'
import TagBoard from '../view/tag-board/tag-board.vue'
import Publish from '../view/publish/publish'
import Tasks from '../view/tasks/tasks'
import User from '../view/user/user'
import MyTask from '../view/my-task/my-task'
import TaskDetail from '../view/task-detail/task-detail'
import Rank from '../view/rank/rank'
import Admin from '../view/admin/admin'
import CheckTask from '../view/admin/check-task/check-task'
import CheckTag from '../view/admin/check-tag/check-tag'
import CheckExpert from '../view/admin/check-expert/check-expert'
import SysStateUser from '../view/admin/sys-state/sys-state-user'
import UserGrowthChart from '../view/admin/sys-state/user-growth-chart'
import UserAccuracyChart from '../view/admin/sys-state/user-accuracy-chart'
import UserAmountChart from '../view/admin/sys-state/user-amount-chart'
import TaskTypeChart from '../view/admin/sys-state/task-type-chart'
import TaskStateChart from '../view/admin/sys-state/task-state-chart'
import TaskTimelineChart from '../view/admin/sys-state/task-timeline-chart'

import HomeView from '../view/home-view/home-view'
import WhitePage from '../view/white-page'
import Alipay from '../view/alipay'
import Guide from '../view/guide/guide'

import Signin from 'components/signin/signin.vue'
import Signup from 'components/signup/signup.vue'

Vue.use(Router)

const routes = [
  {
    path: '/',
    redirect: '/guide'
  },
  {
    path: '/alipay',
    name: 'Alipay',
    component: Alipay
  },
  {
    path: '/white',
    component: WhitePage
  },
  {
    path: '/admin',
    component: Admin,
    children: [
      {
        path: '/check-task',
        component: CheckTask
      },
      {
        path: '/check-tag',
        component: CheckTag
      },
      {
        path: '/system-state-user',
        component: SysStateUser,
        children: [
          {
            path: '/system-state-user-growth',
            component: UserGrowthChart
          },
          {
            path: '/system-state-user-accuracy',
            component: UserAccuracyChart
          },
          {
            path: '/system-state-user-amount',
            component: UserAmountChart
          },
          {
            path: '/system-state-task-type',
            component: TaskTypeChart
          },
          {
            path: '/system-state-task-state',
            component: TaskStateChart
          },
          {
            path: '/system-state-task-timeline',
            component: TaskTimelineChart
          }
        ]
      },
      {
        path: '/check-expert',
        component: CheckExpert
      }
    ]
  },
  {
    path: '/home',
    component: Home,
    redirect: '/myTasks',
    children: [
      {
        path: '/guide',
        name: 'Guide',
        component: Guide
      },
      {
        path: '/publish',
        component: Publish
      },
      {
        path: '/home-view',
        component: HomeView
      },
      {
        path: '/tasks',
        component: Tasks
      },
      {
        path: '/user',
        component: User
      },
      {
        path: '/myTasks',
        component: MyTask
      },
      {
        path: '/task-detail',
        component: TaskDetail
      },
      {
        path: '/rank',
        component: Rank
      }
    ]
  },
  {
    name: 'board',
    path: '/board',
    component: TagBoard
  },
  {
    path: '/login',
    component: Login,
    children: [
      {
        path: '/',
        name: 'Login',
        component: Signin
      },
      {
        path: '/sign',
        name: 'Sign',
        component: Signup
      }
    ]
  }
]
const router = new Router({
  routes,
  linkActiveClass: 'active'
})

router.beforeEach((to, from, next) => {
  if (to.name === 'Login') {
    if (store.getters.isLogin) {
      // 已经登录，跳转到主页
      return next('/home')
    } else {
      // 未登录，继续登录
      return next()
    }
  } else if (to.name === 'Sign' || to.name === 'Alipay' || to.name === 'Guide') {
    // 如果是注册，支付宝支付完成的跳转, 首页，放行
    return next()
  } else {
    if (store.getters.isLogin) {
      // 已经登录，继续
      return next()
    } else {
      // 未登录，强制登录
      return next('/login')
    }
  }
})

export default router
