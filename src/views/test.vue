<template>
  <div>
    原数据： <el-input v-model="d1" placeholder="请输入内容"></el-input>
    <el-button type="primary" @click="jiami" plain>加密</el-button>
    <el-button type="primary" @click="jiemi" plain>解密</el-button>
  </div>
</template>
<script>
  import  {Decrypt,Encrypt}  from '../utils/util'
  export default{
    name : 'test',
    data() {
      return {
        d1: ""
      }
    },
    methods : {
      jiami(){
        let dd = Encrypt(this.d1)
        this.d1= dd
      },
      jiemi(){
        this.d1= Decrypt(this.d1)
      },
    }
  }
</script>
<style scoped>
</style>
