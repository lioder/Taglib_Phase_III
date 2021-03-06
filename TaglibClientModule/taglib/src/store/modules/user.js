const state = {
  isLogin: false,
  id: 0,
  userType: 0,
  username: '游客',
  avatar: 'default_avatar.png',
  applyState: 'NOT_YET'
}

const getters = {
  isLogin: state => state.isLogin,
  id: state => state.id,
  userType: state => state.userType,
  username: state => state.username,
  avatar: state => state.avatar,
  applyState: state => state.applyState
}

const actions = {}

const mutations = {
  login (state, payload) {
    state.isLogin = true
    state.id = payload.userId
    state.userType = payload.userType
    state.username = payload.username
    state.avatar = payload.avatar
    state.applyState = payload.applyState
  },
  logout (state) {
    state.isLogin = false
  },
  changeAvatar (state, avatar) {
    state.avatar = avatar
  }
}

export default {
  state,
  getters,
  actions,
  mutations
}
