<template>
  <div>
    <div class="nav-header">
      <div class="nav">
        <el-menu :default-active="activeIndex2" mode="horizontal" @select="handleSelect" background-color="" text-color="black" active-text-color="red">
          <el-menu-item index="1"><a href="/index">首页</a></el-menu-item>
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
    <div class="content">
      <div :class="{user_content : true, is_top : top_temp}">
        <div class="user-info user-data-base">
          <a href="#"><li class="el-icon-user-solid" style="margin-left: 10%"></li><span>{{user}}</span></a>
          <el-button type="danger" icon="el-icon-star-off" circle style="margin-left: 20%" v-if="!isFollowed" @click="follow()">关注</el-button>
          <el-button type="danger" icon="el-icon-star-off" circle style="margin-left: 20%" v-else @click="cancelFollow">已关注</el-button>
        </div>

        <div class="user-data user-data-base">
          <span><a href="#">粉丝({{fansNums}})</a></span>
          <span><a href="#">喜欢({{followsNums}})</a></span>
        </div>
        <div class="user-data-base user-all-articles">
          <h3 style="margin-top: 5%">最新文章</h3>
          <div style="margin-left: 10%">
            <p v-for="value in articles" >
              <a href="#" class="titles" @click="goView(value._id, value.userName)">{{value.title}}</a>
            </p>
          </div>
        </div>
      </div>
      <div class="article-content">
        <div v-for="value in allArticles">
          <a href="#" @click="goView(value._id, value.userName)"><h3 style="margin-left: 1.5%" class="title">{{value.title}}</h3></a>
          <span style="color: #8a8a8a;margin-bottom: 4px;font-size: 14px;line-height: 24px;">{{value.introduction}}</span><br><br>
          <span>{{value.createTime}}</span>
          <span>评论数&nbsp;<a href="#" class="article-user">{{value.count}}</a></span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'userIndex',
    data() {
      return{
        activeIndex: '1',
        activeIndex2: '1',
        land: '登陆',
        user: '',
        temp: false,
        articles: null,
        allArticles: null,
        top_temp: false,
        articleUserName: this.$route.query.userName,
        isFollowed: false,
        fansNums: 0,
        followsNums: 0
      }
    },
    mounted () {
      //添加滑动监听
      window.addEventListener('scroll', this.watchScroll, true)
      if (localStorage.getItem('userdata') != null) {
        this.land = "退出"
      }
      this.user = localStorage.getItem('userdata')
      this.user = JSON.parse(this.user).name
      if (this.user != '' && this.user != null){
        this.getArticlesByName();
        this.followed();
      }
      this.getFansNums();
      this.getFollowsNums();
    },
    methods: {
      getFansNums(){
        this.$axios.get("http://localhost:8888/users/getFansNums", {
          params : {
            name : this.articleUserName,
          }
        })
          .then(response => {
            this.fansNums = response.data
          }).catch(function (error) {
          console.log(error)
        })
      },
      getFollowsNums(){
        this.$axios.get("http://localhost:8888/users/getFollowsNums", {
          params : {
            name : this.articleUserName,
          }
        })
          .then(response => {
            this.followsNums = response.data
          }).catch(function (error) {
          console.log(error)
        })
      },
      follow(){
        this.$axios.get("http://localhost:8888/users/follow", {
          params : {
            name : this.user,
            byFollowedName: this.articleUserName
          }
        })
          .then(response => {
            if (response.data) {
              this.isFollowed = true
              this.fansNums = this.fansNums + 1
            }
          }).catch(function (error) {
          console.log(error)
        })
      },
      cancelFollow(){
        this.$axios.get("http://localhost:8888/users/cancelFollow", {
          params : {
            name : this.user,
            byFollowedName: this.articleUserName
          }
        })
          .then(response => {
            if (response.data) {
              this.isFollowed = false
              this.fansNums = this.fansNums - 1
            }
          }).catch(function (error) {
          console.log(error)
        })
      },
      followed(){
        this.$axios.get("http://localhost:8888/users/getIsFollowed", {
          params : {
            name : this.user,
            byFollowedName: this.articleUserName
          }
        })
          .then(response => {
            if (response.data) {
              this.isFollowed = true
            }
          }).catch(function (error) {
          console.log(error)
        })
      },
      goView(id, name){
        window.location.href = '/articleComponent?id=' + id + '&userName=' + name
      },
      watchScroll : function (){
        let scrollTop = $(window).scrollTop();
        if (scrollTop > 50) {
          this.top_temp = true
        } else {
          this.top_temp = false
        }
      },
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
      getArticlesByName(){
        this.$axios.get("http://localhost:8888/article/getArticlesByName", {
          params : {
            userName : this.articleUserName
          }
        })
          .then(response => {
            if (response.data.success) {
              this.articles = response.data.data
              console.log(this.articles)
            }else {
              alert(response.data.name)
            }
          }).catch(function (error) {
          console.log(error)
        })
      }
    },
  }
</script>

<style scoped>
  .titles:hover{
    color: red;
  }
  .is_top{
    top: 5%;
  }
  h3{
    text-align: center;
  }
  h1{
    text-align: center;
  }
  .user-all-articles{
    min-height: 900px;
  }
  .user-data-base{
    background-color: #f5f6f7;
    margin-bottom: 6%;
  }
  .user_content{
    margin-left: 5%;
    width: 15%;
    float: left;
    position: fixed;
  }
  .content{
    font-size: 18px;
    line-height: 1.57142857;
    color: #333;
  }
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
