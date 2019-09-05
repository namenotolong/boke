<template>
  <div >
    <div class="form-container">
      <el-form label-position="left" :model="ruleForm" :rules="rules" ref="ruleForm" label-width="0px" class="demo-ruleForm login-container">
        <h3 class="title">用户登录</h3>
        <el-form-item prop="username">
          <el-input type="text" v-model="ruleForm.username" auto-complete="off" placeholder="账号"></el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input type="password" v-model="ruleForm.password" auto-complete="off" placeholder="密码" ></el-input>
        </el-form-item>
        <el-checkbox class="remember" v-model="rememberpwd">记住密码</el-checkbox>
        <span  @click="toRegister"><el-link href="#" class="register">我要注册</el-link></span>
        <div class="drag" ref="dragDiv">
          <div class="drag_bg"></div>
          <div class="drag_text" >{{confirmWords}}</div>
          <div ref="moveDiv" @mousedown="mousedownFn($event)" :class="{'handler_ok_bg':confirmSuccess}" class="handler handler_bg" style="position: absolute;top: 0px;left: 0px;"></div>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script>
  import { setCookie, getCookie, delCookie,Encrypt,Decrypt,delData } from '../utils/util'
  export default {
    name: 'test',
    data(){
      return {
        logining: false,
        // 记住密码
        rememberpwd: false,
        modified: false,
        codeimg: '',
        ruleForm: {
          // username和password默认为空
          username: '',
          password: '',
          code: '',
          randomStr: '',
        },
        rules: {
          username: [{ required: true, message: '请输入账号', trigger: 'blur' }],
          password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
          code: [{ required: true, message: '请输入验证码', trigger: 'blur' }]
        },
        beginClientX:0,           /*距离屏幕左端距离*/
        mouseMoveStata:false,     /*触发拖动状态  判断*/
        maxwidth:'',               /*拖动最大宽度，依据滑块宽度算出来的*/
        confirmWords:'拖动滑块验证',   /*滑块文字*/
        confirmSuccess:false           /*验证成功判断*/
      }
    },
    // 创建完毕状态(里面是操作)
    created () {
      let user = localStorage.getItem('token')
      if (null != user) {
        this.$axios.get('http://zhsdsb.top:8888/users/validate').then(response => {
          if (response.data) {
            this.$message.success("自动登陆成功")
            setTimeout(() => {
              window.location.href = '/index'
            }, 1000)
          } else {
            alert(response.data.name)
          }
        }).catch(function (error) {
          delData()
        })
      }
      this.$message({
        message: '欢迎您的到来，累了就在这人休息一会儿吧',
        type: 'success'
      })
      // 获取存在本地的用户名密码
      this.getuserpwd()
    },
    methods: {
      toRegister(){
        this.$router.push({path : '/register'})
      },
      init (info) {
        this.confirmWords = '拖动滑块验证';
        this.$message.error(info)
        // 获取图形验证码
        this.confirmSuccess = false
        document.getElementsByClassName('handler')[0].style.left = 0 + 'px';
        document.getElementsByClassName('drag_bg')[0].style.width = 0 + 'px';
        this.mouseMoveStata = false
        document.getElementsByTagName('html')[0].addEventListener('mousemove',this.mouseMoveFn);
        document.getElementsByTagName('html')[0].addEventListener('mouseup',this.moseUpFn)
      },
      // 获取用户名密码
      getuserpwd () {
        if (getCookie('user') != '' && getCookie('pwd') != '') {
          this.ruleForm.username = getCookie('user')
          this.ruleForm.password = getCookie('pwd')
          this.ruleForm.password = Decrypt(this.ruleForm.password)
          this.rememberpwd = true
        }
      },
      // 获取info列表
      submitForm (formName) {
        this.$refs[formName].validate(valid => {
          if (valid) {
            this.logining = true
            if (this.rememberpwd) {
              //保存帐号到cookie，有效期7天
              setCookie('user', this.ruleForm.username, 7)
              //保存密码到cookie，有效期7天
              setCookie('pwd', Encrypt(this.ruleForm.password), 7)
            } else {
              delCookie('user')
              delCookie('pwd')
            }
            this.confirmWords = '正在验证……'
            setTimeout(() => {
              this.logining = false
              let user = {name : this.ruleForm.username, password : Encrypt(this.ruleForm.password)}
              console.log(user)
              this.$axios.post("http://zhsdsb.top:8888/users/login", user).then(response => {
                if (response.data.success) {
                  setTimeout(() => {
                    this.confirmWords = response.data.name
                  },500)
                  // 缓存token
                  localStorage.setItem('token', response.headers.authorization)
                  localStorage.setItem('login', true)
                 // this.$store.commit('login', response.headers.authorization)
                  // 缓存用户个人信息
                  localStorage.setItem('userdata', JSON.stringify(user))
                  this.$router.push({ path: '/index' })
                } else {
                  this.init(response.data.name)
                }
              }).catch(function (error) {
                this.init("账户或密码错误")
                console.log(error)
              })
            }, 0)
          } else {
            this.init('请输入用户名密码')
            return false
          }
        })
      },
      mousedownFn:function (e) {
        if(!this.confirmSuccess){
          e.preventDefault && e.preventDefault();   //阻止文字选中等 浏览器默认事件
          this.mouseMoveStata = true;
          this.beginClientX = e.clientX;
        }
      },        //mousedoen 事件
      successFunction(){
        this.confirmSuccess = true
        this.confirmWords = '验证通过';
        if(window.addEventListener){
          document.getElementsByTagName('html')[0].removeEventListener('mousemove',this.mouseMoveFn);
          document.getElementsByTagName('html')[0].removeEventListener('mouseup',this.moseUpFn);
        }else {
          document.getElementsByTagName('html')[0].removeEventListener('mouseup',()=>{});
        }
        document.getElementsByClassName('drag_text')[0].style.color = '#fff'
        document.getElementsByClassName('handler')[0].style.left = this.maxwidth + 'px';
        document.getElementsByClassName('drag_bg')[0].style.width = this.maxwidth + 'px';
        this.submitForm('ruleForm')
      },                //验证成功函数
      mouseMoveFn(e){
        if(this.mouseMoveStata){
          let width = e.clientX - this.beginClientX;
          if(width>0 && width<=this.maxwidth){
            document.getElementsByClassName('handler')[0].style.left = width + 'px';
            document.getElementsByClassName('drag_bg')[0].style.width = width + 'px';
          }else if(width>this.maxwidth){
            this.successFunction();
          }
        }
      },                   //mousemove事件
      moseUpFn(e){
        this.mouseMoveStata = false;
        var width = e.clientX - this.beginClientX;
        if(width<this.maxwidth){
          document.getElementsByClassName('handler')[0].style.left = 0 + 'px';
          document.getElementsByClassName('drag_bg')[0].style.width = 0 + 'px';
        }
      }                       //mouseup事件
    },
    mounted(){
      this.maxwidth = this.$refs.dragDiv.clientWidth - this.$refs.moveDiv.clientWidth;
      document.getElementsByTagName('html')[0].addEventListener('mousemove',this.mouseMoveFn);
      document.getElementsByTagName('html')[0].addEventListener('mouseup',this.moseUpFn)
    }
  }
</script>

<style scoped>
  .register{
    margin-left: 50%;
    color: dodgerblue;
  }
  .form-container{
    margin-top: 5%;
  }
  .drag{
    position: relative;
    background-color: #e8e8e8;
    width: 100%;
    height: 34px;
    line-height: 34px;
    text-align: center;
  }
  .handler{
    width: 40px;
    height: 32px;
    border: 1px solid #ccc;
    cursor: move;
  }
  .handler_bg{
    background: #fff url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA3hpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDIxIDc5LjE1NTc3MiwgMjAxNC8wMS8xMy0xOTo0NDowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDo0ZDhlNWY5My05NmI0LTRlNWQtOGFjYi03ZTY4OGYyMTU2ZTYiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NTEyNTVEMURGMkVFMTFFNEI5NDBCMjQ2M0ExMDQ1OUYiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NTEyNTVEMUNGMkVFMTFFNEI5NDBCMjQ2M0ExMDQ1OUYiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTQgKE1hY2ludG9zaCkiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDo2MTc5NzNmZS02OTQxLTQyOTYtYTIwNi02NDI2YTNkOWU5YmUiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6NGQ4ZTVmOTMtOTZiNC00ZTVkLThhY2ItN2U2ODhmMjE1NmU2Ii8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+YiRG4AAAALFJREFUeNpi/P//PwMlgImBQkA9A+bOnfsIiBOxKcInh+yCaCDuByoswaIOpxwjciACFegBqZ1AvBSIS5OTk/8TkmNEjwWgQiUgtQuIjwAxUF3yX3xyGIEIFLwHpKyAWB+I1xGSwxULIGf9A7mQkBwTlhBXAFLHgPgqEAcTkmNCU6AL9d8WII4HOvk3ITkWJAXWUMlOoGQHmsE45ViQ2KuBuASoYC4Wf+OUYxz6mQkgwAAN9mIrUReCXgAAAABJRU5ErkJggg==") no-repeat center;
  }
  .handler_ok_bg{
    background: #fff url("data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAAQCAYAAAAf8/9hAAAAGXRFWHRTb2Z0d2FyZQBBZG9iZSBJbWFnZVJlYWR5ccllPAAAA3hpVFh0WE1MOmNvbS5hZG9iZS54bXAAAAAAADw/eHBhY2tldCBiZWdpbj0i77u/IiBpZD0iVzVNME1wQ2VoaUh6cmVTek5UY3prYzlkIj8+IDx4OnhtcG1ldGEgeG1sbnM6eD0iYWRvYmU6bnM6bWV0YS8iIHg6eG1wdGs9IkFkb2JlIFhNUCBDb3JlIDUuNS1jMDIxIDc5LjE1NTc3MiwgMjAxNC8wMS8xMy0xOTo0NDowMCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0UmVmPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VSZWYjIiB4bWxuczp4bXA9Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC8iIHhtcE1NOk9yaWdpbmFsRG9jdW1lbnRJRD0ieG1wLmRpZDo0ZDhlNWY5My05NmI0LTRlNWQtOGFjYi03ZTY4OGYyMTU2ZTYiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6NDlBRDI3NjVGMkQ2MTFFNEI5NDBCMjQ2M0ExMDQ1OUYiIHhtcE1NOkluc3RhbmNlSUQ9InhtcC5paWQ6NDlBRDI3NjRGMkQ2MTFFNEI5NDBCMjQ2M0ExMDQ1OUYiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIENDIDIwMTQgKE1hY2ludG9zaCkiPiA8eG1wTU06RGVyaXZlZEZyb20gc3RSZWY6aW5zdGFuY2VJRD0ieG1wLmlpZDphNWEzMWNhMC1hYmViLTQxNWEtYTEwZS04Y2U5NzRlN2Q4YTEiIHN0UmVmOmRvY3VtZW50SUQ9InhtcC5kaWQ6NGQ4ZTVmOTMtOTZiNC00ZTVkLThhY2ItN2U2ODhmMjE1NmU2Ii8+IDwvcmRmOkRlc2NyaXB0aW9uPiA8L3JkZjpSREY+IDwveDp4bXBtZXRhPiA8P3hwYWNrZXQgZW5kPSJyIj8+k+sHwwAAASZJREFUeNpi/P//PwMyKD8uZw+kUoDYEYgloMIvgHg/EM/ptHx0EFk9I8wAoEZ+IDUPiIMY8IN1QJwENOgj3ACo5gNAbMBAHLgAxA4gQ5igAnNJ0MwAVTsX7IKyY7L2UNuJAf+AmAmJ78AEDTBiwGYg5gbifCSxFCZoaBMCy4A4GOjnH0D6DpK4IxNSVIHAfSDOAeLraJrjgJp/AwPbHMhejiQnwYRmUzNQ4VQgDQqXK0ia/0I17wJiPmQNTNBEAgMlQIWiQA2vgWw7QppBekGxsAjIiEUSBNnsBDWEAY9mEFgMMgBk00E0iZtA7AHEctDQ58MRuA6wlLgGFMoMpIG1QFeGwAIxGZo8GUhIysmwQGSAZgwHaEZhICIzOaBkJkqyM0CAAQDGx279Jf50AAAAAABJRU5ErkJggg==") no-repeat center;
  }
  .drag_bg{
    background-color: #7ac23c;
    height: 34px;
    width: 0px;
  }
  .drag_text{
    position: absolute;
    top: 0px;
    width: 100%;text-align: center;
    -moz-user-select: none;
    -webkit-user-select: none;
    user-select: none;
    -o-user-select:none;
    -ms-user-select:none;
  }
  .login-container {
    border-radius: 10px;
    margin: 0px auto;
    width: 350px;
    padding: 30px 35px 15px 35px;
    background: #fff;
    border: 1px solid #eaeaea;
    text-align: left;
    box-shadow: 0 0 20px 2px rgba(0, 0, 0, 0.1);
  }
  .title {
    margin: 0px auto 40px auto;
    text-align: center;
    color: #505458;
  }
  .remember {
    margin: 0px 0px 35px 0px;
  }
</style>
