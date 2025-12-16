const TOKEN_KEY = "token"
const USER_INFO = "userInfo";
const SEARCH_KEY = "searchKey";
const PRODUCT_INFO = "productInfo";
export function getProductInfo() {
    const jsonProduct = sessionStorage.getItem(PRODUCT_INFO);
    return JSON.parse(jsonProduct);
}
export function setProductInfo(info) {
    sessionStorage.setItem(PRODUCT_INFO, JSON.stringify(info));
}
export function getSearchKey() {
    return sessionStorage.getItem(SEARCH_KEY);
}
export function setSearchKey(key) {
    sessionStorage.setItem(SEARCH_KEY, key);
}
export function getUserInfo() {
    const jsonUserInfo = sessionStorage.getItem(USER_INFO);
    return JSON.parse(jsonUserInfo);
}
export function setUserInfo(userInfo) {
    sessionStorage.setItem(USER_INFO, JSON.stringify(userInfo));
}
export function getToken() {
    return sessionStorage.getItem(TOKEN_KEY);
}
export function setToken(token) {
    sessionStorage.setItem(TOKEN_KEY, token);
}
export function clearToken() {
    sessionStorage.setItem(TOKEN_KEY, null);
}