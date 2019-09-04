<template>
  <div>
    <div class="content">
      <div class=user_content>
        <div class="user-info user-data-base">
          <el-button round type="success" onclick="javascript:window.location.href='/index'">返回</el-button>
          <div>
            <a href="">未读消息</a>
          </div>
          <div>
            <a href="">全部消息</a>
          </div>
        </div>
      </div>
      <div class="article-content">
        hello
      </div>
    </div>
  </div>
</template>

<script>
  export default {
    name: 'message',
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

  }
</script>

<style scoped>
  .title:hover{
    color:red
  }
  .article-content{
    margin-left: 21%;
    margin-top: 3%;
    width: 60%;
  }
  .titles:hover{
    color: red;
  }
  .is_top{
    top: 5%;
  }
  .title-mid{
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
