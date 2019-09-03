import CryptoJS from 'crypto-js/crypto-js'
const KEY = CryptoJS.enc.Utf8.parse("1234567890123456");
const IV = CryptoJS.enc.Utf8.parse('1234567890123456');
/**
 * 时间戳
 * @param {*} timestamp  时间戳
 */
const timestampToTime = (timestamp) => {
  let date = new Date(timestamp) // 时间戳为10位需*1000，时间戳为13位的话不需乘1000
  let Y = date.getFullYear() + '-'
  let M =
        (date.getMonth() + 1 < 10
          ? '0' + (date.getMonth() + 1)
          : date.getMonth() + 1) + '-'
  let D =
        (date.getDate() < 10 ? '0' + date.getDate() : date.getDate()) + ' '
  let h =
        (date.getHours() < 10 ? '0' + date.getHours() : date.getHours()) + ':'
  let m =
        (date.getMinutes() < 10 ? '0' + date.getMinutes() : date.getMinutes()) +
        ':'
  let s =
        date.getSeconds() < 10 ? '0' + date.getSeconds() : date.getSeconds()
  return Y + M + D + h + m + s
}
/**
 * 存储localStorage
 */
const setStore = (name, content) => {
  if (!name) return
  if (typeof content !== 'string') {
    content = JSON.stringify(content)
  }
  window.localStorage.setItem(name, content)
}

/**
 * 获取localStorage
 */
const getStore = name => {
  if (!name) return
  return window.localStorage.getItem(name)
}

/**
 * 删除localStorage
 */
const removeStore = name => {
  if (!name) return
  window.localStorage.removeItem(name)
}

/**
 * 设置cookie
 **/
function setCookie (name, value, day) {
  let date = new Date()
  date.setDate(date.getDate() + day)
  document.cookie = name + '=' + value + ';expires=' + date
};

/**
 * 获取cookie
 **/
function getCookie (name) {
  let reg = RegExp(name + '=([^;]+)')
  let arr = document.cookie.match(reg)
  if (arr) {
    return arr[1]
  } else {
    return ''
  }
};

/**
 * 删除cookie
 **/
function delCookie (name) {
  setCookie(name, null, -1)
};
/**
 * 删除用户信息
 **/
function delData () {
  alert("身份过期，请重新登陆")
  localStorage.removeItem('token')
  localStorage.removeItem("userdata")
  window.location.href = '/'
  console.log(error);
};

/**
 * aes加密
 * @param word
 * @param keyStr
 * @param ivStr
 * @returns {*|string}
 * @constructor
 */
export function Encrypt(word, keyStr, ivStr) {
  let key = KEY
  let iv = IV

  if (keyStr) {
    key = CryptoJS.enc.Utf8.parse(keyStr);
    iv = CryptoJS.enc.Utf8.parse(ivStr);
  }

  let srcs = CryptoJS.enc.Utf8.parse(word);
  var encrypted = CryptoJS.AES.encrypt(srcs, key, {
    iv: iv,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.ZeroPadding
  });
  // console.log("-=-=-=-", encrypted.ciphertext)
  return CryptoJS.enc.Base64.stringify(encrypted.ciphertext);

}
/**
 * AES 解密 ：字符串 key iv  返回base64
 *
 */
export function Decrypt(word, keyStr, ivStr) {
  let key  = KEY
  let iv = IV

  if (keyStr) {
    key = CryptoJS.enc.Utf8.parse(keyStr);
    iv = CryptoJS.enc.Utf8.parse(ivStr);
  }

  let base64 = CryptoJS.enc.Base64.parse(word);
  let src = CryptoJS.enc.Base64.stringify(base64);

  var decrypt = CryptoJS.AES.decrypt(src, key, {
    iv: iv,
    mode: CryptoJS.mode.CBC,
    padding: CryptoJS.pad.ZeroPadding
  });

  var decryptedStr = decrypt.toString(CryptoJS.enc.Utf8);
  return decryptedStr.toString();
}

/**
 * 导出
 **/
export {
  timestampToTime,
  setStore,
  getStore,
  removeStore,
  setCookie,
  getCookie,
  delCookie,
  delData
}
