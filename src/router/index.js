import Vue from 'vue'
import Router from 'vue-router'
import login from '@/views/login'
import index from '@/views/index'
import write from '@/views/write'
import articleComponent from '@/views/articleComponent'
import userIndex from '@/views/userIndex'
import message from '@/views/message'
import register from '@/views/register'

Vue.use(Router)

export default new Router({
  mode: 'history',
  routes: [
    {
      path: '/',
      name: 'login',
      component: login,
      meta: {
        requiredAuth: false
      }
    },
    {
      path: '/index',
      name: 'index',
      component: index,
      meta: {
        requiredAuth: false
      }
    },
    {
      path: '/write',
      name: 'write',
      component: write,
      meta: {
        requiredAuth: true
      }
    },
    {
      path: '/articleComponent',
      name: 'articleComponent',
      component: articleComponent,
      meta: {
        requiredAuth: false
      }
    },
    {
      path: '/userIndex',
      name: 'userIndex',
      component: userIndex,
      meta: {
        requiredAuth: false
      }
    },
    {
      path: '/message',
      name: 'message',
      component: message,
      meta: {
        requiredAuth: true
      }
    },
    {
      path: '/register',
      name: 'register',
      component: register,
      meta: {
        requiredAuth: false
      }
    },
  ]
})