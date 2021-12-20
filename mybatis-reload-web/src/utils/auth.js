import Cookies from 'js-cookie'

const TokenKey = 'token'

const UserInfo = "userinfo"

export function getToken() {
  return Cookies.get(TokenKey)
}

export function setToken(token) {
  return Cookies.set(TokenKey, token)
}

export function removeToken() {
  return Cookies.remove(TokenKey)
}


export function getUserInfo() {
  let userInfoJson = Cookies.get(UserInfo)
  if(userInfoJson!==undefined){
    return JSON.parse(userInfoJson)
  }
  return {}
}

export function setUserInfo(userinfo) {
  console.log(userinfo)
  let userInfoJson = JSON.stringify(userinfo)
  return Cookies.set(UserInfo, userInfoJson)
}

export function removeInfo() {
  return Cookies.remove(UserInfo)
}