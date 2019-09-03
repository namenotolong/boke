<template>
  <div>
    <div class="nav-header">
      <div class="nav">
        <el-menu :default-active="activeIndex2" class="el-menu-demo mine" mode="horizontal" @select="handleSelect" background-color="" text-color="black" active-text-color="red">
        <el-menu-item index="1"><a href="">首页</a></el-menu-item>
        <el-submenu index="2">
          <template slot="title">我的工作台</template>
          <el-menu-item index="2-2">我的收藏</el-menu-item>
          <el-menu-item index="2-5" @click="showMyArticles()">我的博客</el-menu-item>
          <el-menu-item index="2-6" @click="exit()">{{land}}</el-menu-item>
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
    <div :class="{class_nums : true, temp_class : temp}">
      <ol style="text-align: center">
        <li>
          <el-link href="" :class="{class_li : true, click_color : is_red_color === '今日推荐'}"><span class="class-num">今日推荐</span></el-link>
          <div @click="showMyFollow">
            <el-link href="#" :class="{class_li : true, click_color : is_red_color === '关注'}" ><span class="class-num">关注</span></el-link>
          </div>
        </li>
        <li v-for="value in info">
          <div @click="changeContent(value.name)">
            <el-link href="#" :class="{class_li : true, click_color : is_red_color === value.name}">
              <span class="class-num">{{value.name}}</span>
            </el-link>
          </div>
        </li>
      </ol>
    </div>
    <div class="content" >
        <div v-for="value in articles">
          <a href="#" @click="goView(value._id, value.userName)"><h3 style="margin-left: 1.5%" class="title">{{value.title}}</h3></a>
          <span style="color: #8a8a8a;margin-bottom: 4px;font-size: 14px;line-height: 24px;">{{value.introduction}}</span><br><br>
          <a href="#" class="article-user" @click="goView1(value.userName)"><span >{{value.userName}}</span></a>
          <span>|</span>
          <span>{{value.createTime}}</span>
          <span>评论数&nbsp;<a :href="value.articleComponent" class="article-user" >{{value.count}}</a></span>
        </div>
    </div>
  </div>
</template>

<script>

  export default {
    data() {
      return {
        activeIndex: '1',
        activeIndex2: '1',
        //显示的文章
        articles: null,
        //记录红色目录标签
        is_red_color: '今日推荐',
        //记录是否登陆
        land: '登陆',
        //用户名
        user: '',
        temp: false,
        //记录当前页数
        count: 0,
        //判断当前是否滑到屏幕
        bottom: false,
        //目录
        catalog: null,
        info: null,
        //当前的文章是否是关注人的
        pageIsFollow: false,
        //当前显示的文章是否是自己的
        pageIsIsMine: false
      };
    },
    methods: {
      showMyArticles(){
        this.articles = null
        this.pageIsFollow = false
        this.showMyArticlesMid();
      },
      showMyArticlesMid(){
        if (this.pageIsIsMine === false){
          this.count = 0
        }
        this.$axios.get("http://localhost:8888/article/getOwnArticles", {
          params: {
            name : this.user,
            count : this.count
          }
        }).then(response =>{
          if (response.data.success){
            console.log(response.data)
            if (this.pageIsIsMine === false){
              this.pageIsIsMine = true
              this.articles = response.data.data
            } else {
              this.articles = this.articles.concat(response.data.data)
            }
          } else {
            alert(response.data.name)
          }
        }).catch(function (error) {
          console.log(error)
        })
      },
      showMyFollow(){
        this.count = 0
        this.is_red_color  = '关注'
        this.articles = null
        this.pageIsIsMine = false
        this.getMyFollowArticles()
      },
      getMyFollowArticles(){
        this.$axios.get("http://localhost:8888/article/getArticlesOfFollowers", {
          params: {
            name : this.user,
            count : this.count
          }
        }).then(response => {
          if (response.data.success){
            if (this.pageIsFollow){
              this.articles = this.articles.concat(response.data.data)
            } else {
              this.articles = response.data.data
              this.pageIsFollow = true
            }
            this.count = this.count + 1
          } else {
            alert(response.data.name)
          }
        }).catch(function (error) {
          console.log(error)
        })
      },
      watchScroll : function (){
        let scrollTop = $(window).scrollTop();
        if (scrollTop > 50) {
          this.temp = true
        } else {
          this.temp = false
        }
        let windowHeight = document.documentElement.clientHeight || document.body.clientHeight;
        //变量scrollHeight是滚动条的总高度
        let scrollHeight = document.documentElement.scrollHeight||document.body.scrollHeight;
        //滚动条到底部的条件
        if(scrollTop+windowHeight >= scrollHeight - 1){
          if (!this.bottom && !this.pageIsFollow && !this.pageIsIsMine) {
            this.getArticle()
          } else if (!this.bottom && this.pageIsFollow && !this.pageIsIsMine){
            this.getMyFollowArticles()
          } else if (!this.bottom && this.pageIsIsMine){
            this.showMyArticlesMid()
          }
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
      goView(id, userName){
        window.location.href = '/articleComponent?id=' + id + '&userName=' + userName
      },
      goView1(userName){
        window.location.href = '/userIndex?userName=' + userName
      },
      changeContent(name){
        this.is_red_color = name
        this.count = 0
        this.catalog = name;
        this.articles = null
        this.bottom = false
        this.pageIsFollow = false
        this.pageIsIsMine = false
        this.getArticle()
      },
      getArticle(){
        this.$axios.get('http://localhost:8888/article/getArticles', {
          params:{
            count : this.count,
            name : this.catalog
          }
        })
          .then(response => {
            console.log(response.data)
            if (response.data.success){
              if (this.articles == null) {
                this.articles = response.data.data
              } else {
                this.articles = this.articles.concat(response.data.data)
              }
              this.count = this.count + 1
            } else {
              this.$message.success(response.data.name)
              this.bottom = true
            }
          })
          .catch(function (error) {
            console.log(error)
          })
      }
    },
    mounted () {
      this.getArticle()
      window.addEventListener('scroll', this.watchScroll, true),
      this.$axios.get('http://localhost:8888/getCatalogs')
        .then(response => {
          console.log(response)
          this.info = response.data
        })
        .catch(function (error) { // 请求失败处理
          console.log(error);
        });

      if (localStorage.getItem('userdata') != null) {
        this.land = "退出"
      }
      this.user = localStorage.getItem('userdata')
      this.user = JSON.parse(this.user).name
    }
  }
</script>
<style>
  .click_color{
    background-color: red !important;
    color: white !important;
  }
  .article-user{
    padding-top: 30%;
  }
  .title:hover{
    color:red
  }
  .article-user:hover{
    color: #157dcf;
  }
  a{
    text-decoration-line: none;
    color:black;
  }
  li{
    list-style-type:none;
  }
  .class_li:hover{
    background-color: red !important;
    color: white !important;
  }

  .class_nums{
    width: 10%;
    float: left;
    position: fixed;
  }
  .temp_class{
    top: 0px;
  }

  span{
    padding: 10px ;
    margin: 10px ;
    width: 100px;
  }
  .el-menu-item:hover{
    color: red !important;
  }
  .content{
     margin-left: 20%;
     margin-top: 3%;
   }
</style>
