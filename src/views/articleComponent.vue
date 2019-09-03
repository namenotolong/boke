<template>
    <div>
      <div class="nav-header">
        <div class="nav">
          <el-menu :default-active="activeIndex2" mode="horizontal" @select="handleSelect" background-color="" text-color="black" active-text-color="red">
            <el-menu-item index="1"><a href="/index">首页</a></el-menu-item>
            <el-submenu index="2">
              <template slot="title">我的工作台</template>
              <el-menu-item index="2-2">我的收藏</el-menu-item>
              <el-menu-item index="2-4" v-if="land == '退出'">我的博客</el-menu-item>
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
            <div>
              <h1>{{article.title}}</h1>
            </div>
          <div style="color: #858585;font-size: 14px;">
            <el-button v-if="isStore" @click="cancelStore()" type="success">已收藏</el-button>
            <el-button v-else @click="storeThis()" type="" type="danger">收藏</el-button>
            <span>创作时间：{{article.modifiedTime}}</span>
            <span>作者：{{article.userName}}</span>
          </div>
          <div v-html="article.content">

          </div>
          <div class="comment">
            <div class="common">
              <el-input
                :placeholder="msg"
                v-model="input"
                clearable>
              </el-input>
              <el-button type="danger" @click="doCommonOfArticle" v-if="noClickReply">评论</el-button>
              <el-button type="danger" @click="doReply" v-else>回复</el-button>
              <el-button type="" @click="cancelShowReply" v-if="!noClickReply">取消回复</el-button>
            </div>
            <div class="comment-content" v-if="articleCommons != null">
              <div v-for="(common, index) in articleCommons" v-bind:key="common._id">
                <p>
                  <span>{{common.userName}}:</span>
                  <small><span>{{common.content}}</span></small>
                  <span class="common-do">
                  <span>（{{common.createTime}}</span>
                  <span>#{{index + 1}}楼）</span>
                  <a href="#" style="margin-left: 5%;color: blue" @click="showReply(common.userName,common._id, index)">回复</a>
                  <a href="#" v-if="common.replied && common._id != showCount" style="margin-left: 5%;color: blue" @click="showAllReply(common._id)">查看回复</a>
                  <a href="#" v-if="common._id == showCount" style="margin-left: 5%;color: blue" @click="throwAllReply">收起回复</a>
                  </span>
                </p>
                <div v-if="common._id == showCount">
                  <div v-for="reply in replies" v-bind:key="reply._id">
                    <p style="margin-left: 3%">
                      <span>|</span>
                      <small><span >{{reply.userName}}回复{{reply.replyUser}}：</span></small>
                      <small><span>{{reply.content}}</span></small>
                      <span class="common-do">
                        <span>{{reply.createTime}}</span>
                      </span>
                      <a href="#" class="common-do" style="margin-left: 5%;color: blue" @click="showCommonReply(reply.userName,reply._id, reply.content)">回复</a>
                    </p>
                  </div>
                </div>
              </div>
            </div>
            <div style="text-align: center;" id="showALl">
              <a href="#" @click="showAllCommons"><span style="color: #157dcf;font-size: 12px;">查看所有评论<li class="el-icon-arrow-down"></li></span></a>
            </div>
          </div>
        </div>
      </div>
    </div>
</template>

<script>
  export default {
    name: 'articleComponent',
    data() {
      return{
        activeIndex: '1',
        activeIndex2: '1',
        land: '登陆',
        user: '',
        temp: false,
        //获取的文章id参数
        id: this.$route.query.id,
        //显示的文章内容
        article: null,
        //作者最近的文章信息
        articles: null,
        //当前滑动是否是处于顶部   用于侧边栏动态上移
        top_temp: false,
        //获取的作者名称
        articleUserName: this.$route.query.userName,
        //是否已经关注作者
        isFollowed: false,
        //粉丝树木
        fansNums: 0,
        followsNums: 0,
        input: '',
        //是否已经收藏
        isStore: false,
        //文章的评论
        articleCommons: null,
        //当前评论的数量/10
        commonsCount: 0,
        //当前是否点击了回复
        noClickReply: true,
        msg: '想对作者说些什么吗？',
        //当前回复的人
        replyUser: '',
        //当前回复的评论id
        replyCommonId: '',
        //当前回复的楼数，用于改变数组的状态，改变是否有人回复
        index: 0,
        //展示的评论回复编号
        showCount: -1,
        //展开的回复
        replies: '',
        //回复的是否是评论的回复
        isReplyCommon: false
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
      this.getArticle()
      if (this.user != '' && this.user != null){
        this.getArticlesByName();
        this.followed();
      }
      this.getFansNums();
      this.getFollowsNums();
      this.getIsStored();
      this.getCommonsOfArticle()
    },
    methods: {
      //收起回复
      throwAllReply(){
        this.showCount = -1
      },
      //展示某一评论的所有回复
      showAllReply(index){
        this.showCount = index
        this.$axios.get("http://localhost:8888/common/getCommonsOfCommon", {
          params: {
            commonId : index
          }
        }).then(response => {
          if (response.data.success){
            this.replies = response.data.data
          } else {
            console.log(response.data.data)
          }
        }).catch(function (error) {
          console.log(error)
        })
      },
      //显示所有评论
      showAllCommons(){
        $("#showALl").css('display', 'none')
        this.$axios.get("http://localhost:8888/common/getCommonsOfArticle",{
          params : {
            _id : this.id,
          }
        }).then(response => {
          if (response.data.success){
            this.articleCommons = response.data.data
          } else {
            console.log(response.data)
          }
        }).catch(function (error) {
          console.log(error)
        })
      },
      //显示回复匡，隐藏评论匡
      showReply(userName,id, index){
        this.input = ''
        this.noClickReply = false
        this.msg = '回复：' + userName + ':hello world'
        this.replyUser = userName
        this.replyCommonId = id
        this.index = index
        this.isReplyCommon = false
      },
      //回复评论的回复
      showCommonReply(userName, id, content){
        this.input = ''
        this.noClickReply = false
        this.msg = '回复：' + userName + ':' + content
        this.replyUser = userName
        this.replyCommonId = id
        this.isReplyCommon = true
      },
      cancelShowReply(){
        this.input = ''
        this.noClickReply = true
        this.msg = '想对作者说些什么吗？'
        this.isReplyCommon = false
      },
      //回复评论
      doReply() {
        if ($.trim(this.input) == ''){
          this.$message.error('请输入具体内容')
          return
        }
        let common = {articleId: this.id, userName: 1, content: this.input,replyUser: this.replyUser, commonId: this.replyCommonId}
        this.$axios.post("http://localhost:8888/common/doCommonOfCommon", common)
          .then(response => {
            if (response.data.success){
              this.$message.success("回复成功")
              let temp = this.articleCommons[this.index]
              temp.replied = true
              this.$set(this.articleCommons, this.index, temp)
              if (this.replyCommonId == this.showCount || this.isReplyCommon){
                this.replies = this.replies.concat(response.data.data)
              }
            this.input = ''
            } else {
              console.log(response.data.data)
            }
          }).catch(function (error) {
          console.log(error)
        })
      },
      //评论文章
      doCommonOfArticle(){
        if ($.trim(this.input) == ''){
          this.$message.error('请输入具体内容')
          return
        }
        let common = {articleId: this.id, userName: 1, content: this.input,replyUser: this.articleUserName}
        this.$axios.post("http://localhost:8888/common/doCommonOfArticle", common)
          .then(response => {
            if (response.data.success){
              this.$message.success("评论成功")
              this.input = ''
              this.articleCommons = this.articleCommons.concat(response.data.data);
            } else {
              console.log(response.data.data)
            }
          }).catch(function (error) {
          console.log(error)
        })
      },
      //获取文章的评论
      getCommonsOfArticle(){
        this.$axios.get("http://localhost:8888/common/getCommonsOfArticle",{
          params : {
            _id : this.id,
            count : this.commonsCount
          }
        }).then(response => {
          if (response.data.success){
             this.articleCommons = response.data.data
          } else {
            console.log(response.data)
          }
        }).catch(function (error) {
          console.log(error)
        })
      },
      //判断是否收藏
      getIsStored(){
        this.$axios.get("http://localhost:8888/article/checkStored", {
          params : {
            _id : this.id,
            username : this.user
          }
        }).then(response => {
          if (response.data.success){
             this.isStore = true
          } else {
            this.isStore = false
          }
        }).catch(function (error) {
          console.log(error)
        })
      },
      storeThis(){
        this.$axios.get("http://localhost:8888/article/store", {
          params : {
            _id : this.id,
            username : this.user
          }
        }).then(response => {
          if (response.data.success){
            this.isStore = true
          } else {
            alert(response.data.data)
          }
        }).catch(function (error) {
          console.log(error)
        })
      },
      cancelStore(){
        this.$axios.get("http://localhost:8888/article/unStore", {
          params : {
            _id : this.id,
            username : this.user
          }
        }).then(response => {
          if (response.data.success){
            this.isStore = false
          } else {
            alert(response.data.data)
          }
        }).catch(function (error) {
          console.log(error)
        })
      },
      //获取粉丝数目
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
      getArticle(){
        this.$axios.get("http://localhost:8888/article/getArticleById", {
          params : {
            id : this.id,
          }
        })
          .then(response => {
            if (response.data.success) {
              this.article = response.data.data
              console.log(this.article)
            }else {
              alert(response.data.name)
            }
          }).catch(function (error) {
            console.log(error)
        })
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
  .common-do{
    font-size: 12px;
    color: #999;
    vertical-align: top;
  }

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
  .article-content{
    margin-left: 21%;
    margin-top: 3%;
    width: 60%;
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
