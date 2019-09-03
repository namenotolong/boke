<template>
    <div>
      <div class="nav-header">
        <div class="nav">
          <el-menu :default-active="activeIndex2" mode="horizontal" @select="handleSelect" background-color="" text-color="black" active-text-color="red">
            <el-menu-item index="1"><a href="">首页</a></el-menu-item>
            <el-submenu index="2">
              <template slot="title">我的工作台</template>
              <el-menu-item index="2-1">我的关注</el-menu-item>
              <el-menu-item index="2-2">我的收藏</el-menu-item>
              <el-menu-item index="2-3">个人中心</el-menu-item>
              <el-menu-item index="2-3">账号设置</el-menu-item>
              <el-menu-item index="2-4">我的博客</el-menu-item>
              <el-menu-item index="2-5" @click="exit()">{{land}}</el-menu-item>
            </el-submenu>
            <el-menu-item index="3">消息中心</el-menu-item>
            <el-menu-item index="4"><a href="/write" >写博客</a></el-menu-item>
            <el-menu-item index="5">关于</el-menu-item>
            <el-menu-item index="6">文档</el-menu-item>
            <el-menu-item index="7">客服</el-menu-item>
            <el-menu-item index="8">福利</el-menu-item>
            <el-menu-item index="9">空间</el-menu-item>
            <el-menu-item index="10">今日要闻</el-menu-item>
            <el-menu-item index="11">历史纪录</el-menu-item>
            <el-menu-item index="12" v-if="land == '退出'">欢迎您：{{user}}</el-menu-item>
          </el-menu>
        </div>
      </div>
    </div>
</template>

<script>
  export default {
    name: 'content',
    data() {
      return{
        activeIndex: '1',
        activeIndex2: '1',
        land: '登陆',
        user: '',
        temp: false,
      }
    },
    mounted () {
      if (localStorage.getItem('userdata') != null) {
        this.land = "退出"
      }
      this.user = localStorage.getItem('userdata')
      this.user = JSON.parse(this.user).name
    },
    methods: {
      exit(){
        if (this.land == '退出') {
          this.$confirm('真的要离开我，并且消除浏览器记录吗？')
            .then(_ => {
              alert("退出成功")
              localStorage.removeItem('token')
              localStorage.removeItem("userdata")
              localStorage.setItem('login', false)
              window.location.href = '/'
            })
            .catch(_ => {});
        } else {
          window.location.href = '/'
        }
      },
      handleSelect(key, keyPath) {
        console.log(key, keyPath);
      },
      handleOpen(key, keyPath) {
        console.log(key, keyPath);
      },
      handleClose(key, keyPath) {
        console.log(key, keyPath);
      },
    },
  }
</script>

<style scoped>
  a{
    text-decoration-line: none;
    color:black;
  }
  li{
    list-style-type:none;
  }
  .el-menu-item:hover{
    color: red !important;
  }
  span{
    padding: 10px ;
    margin: 10px ;
    width: 100px;
  }
</style>
