import Vue from 'vue';
import Vuex from 'vuex';

Vue.use(Vuex);
// 登录验证
export default new Vuex.Store({
    state: {
        token: '',
        login: false
    },
    mutations: {
        // 登录
        login(state, token) {
            state.token = token;
            state.login = true
            localStorage.setItem("token", token);
        },
        // 退出
        logout(state, token) {
            state.token = "";
            state.login = false
            localStorage.setItem("token", "");
        }
    }
})
