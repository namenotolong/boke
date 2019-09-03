// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue';
// 引入element UI
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import App from './App';
// 引入路由
import router from './router';
// 引入状态管理
import store from './vuex/store';

import axios from 'axios';
Vue.prototype.$axios = axios;

Vue.config.productionTip = false;

// 使用element UI
Vue.use(ElementUI);
Vue.use(store)
// 过滤器
import * as custom from './utils/util'

Object.keys(custom).forEach(key => {
  Vue.filter(key, custom[key])
})
import $ from 'jquery'

Vue.config.productionTip = false
Vue.use($)



import VueQuillEditor from 'vue-quill-editor'
import 'quill/dist/quill.core.css'
import 'quill/dist/quill.snow.css'
import 'quill/dist/quill.bubble.css'

Vue.use(VueQuillEditor)

// 路由拦截器
router.beforeEach((to, from, next) => {
  if (to.matched.length != 0) {
    if (to.meta.requiredAuth) { // 判断该路由是否需要登录权限
      if (Boolean(localStorage.getItem("token"))) { // 通过vuex state获取当前的user是否存在
        next();
      } else {
        alert('您还未登陆，您还未登陆请先登录')
        next({
          path: '/'
        })
      }
    } else {
      next();
    }
  } else {
    next({
      path: '/login',
      query: { redirect: to.fullPath } // 将跳转的路由path作为参数，登录成功后跳转到该路由
    })
  }
})
//Axios请求拦截器，随着业务的复杂，Axios层的使用将会越来越复杂，写个精简版的就行了。
axios.interceptors.request.use(config => {
  let token = localStorage.getItem('token')
  let login = localStorage.getItem('login')
  if (login && token != null && token != '') {  // 判断是否存在token，如果存在的话，则每个http header都加上token
    config.headers.Authorization = token;
  }
  return config
}, error => {
  return Promise.reject(error)
})
/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>'
})
