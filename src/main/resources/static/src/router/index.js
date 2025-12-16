import Vue from "vue";
import VueRouter from "vue-router";
import ElementUI from 'element-ui';
import 'element-ui/lib/theme-chalk/index.css';
import { getToken } from "@/utils/storage.js";
import echarts from 'echarts';
Vue.prototype.$echarts = echarts;
Vue.use(ElementUI);
Vue.use(VueRouter);

const routes = [
  { path: "/", component: () => import(`@/views/user/Home.vue`) },
  { path: "/login", component: () => import(`@/views/login/Login.vue`) },
  { path: "/register", component: () => import(`@/views/register/Register.vue`) },
  {
    path: "/admin",
    component: () => import(`@/views/admin/Home.vue`),
    meta: { requireAuth: true },
    children: [
    { path: "/adminLayout", name: 'Dashboard', icon: 'el-icon-pie-chart', component: () => import(`@/views/admin/Main.vue`), meta: { requireAuth: true } },
    { path: "/userManage", name: 'User Management', icon: 'el-icon-user-solid', component: () => import(`@/views/admin/User.vue`), meta: { requireAuth: true } },
    { path: "/contentManage", name: 'Post Management', icon: 'el-icon-user-solid', component: () => import(`@/views/admin/ContentManage.vue`), meta: { requireAuth: true } },
    { path: "/productManage", name: 'Product Management', icon: 'el-icon-shopping-bag-1', component: () => import(`@/views/admin/Product.vue`), meta: { requireAuth: true } },
    { path: "/ordersManage", name: 'Order Management', icon: 'el-icon-document-copy', component: () => import(`@/views/admin/Orders.vue`), meta: { requireAuth: true } },
    { path: "/categoryManage", name: 'Category Management', icon: 'el-icon-document-checked', component: () => import(`@/views/admin/Category.vue`), meta: { requireAuth: true } },
    { path: "/messageManage", name: 'Message Management', icon: 'el-icon-chat-square', component: () => import(`@/views/admin/Message.vue`), meta: { requireAuth: true } },
    { path: "/interactionManage", name: 'Interaction Management', icon: 'el-icon-connection', component: () => import(`@/views/admin/Interaction.vue`), meta: { requireAuth: true } },
    { path: "/operationLogManage", name: 'Log Management', icon: 'el-icon-connection', component: () => import(`@/views/admin/OperationLog.vue`), meta: { requireAuth: true } },
    { path: "/evaluations", name: 'Comment Management', icon: 'el-icon-chat-dot-round', component: () => import(`@/views/admin/Evaluations.vue`), meta: { requireAuth: true } },
    { name: 'Product Details', show: false, path: "/product-detail1", component: () => import(`@/views/admin/ProductDetail.vue`), meta: { requireAuth: true } },
    { name: 'Post Details', show: false, path: "/content-detail", component: () => import(`@/views/admin/ContentDetail.vue`), meta: { requireAuth: true } },
]
  },
  {
    path: "/user",
    component: () => import(`@/views/user/Home.vue`),
    meta: { requireAuth: true },
    children: [
      { name: '商品', path: "/product", component: () => import(`@/views/user/Product.vue`), meta: { requireAuth: true } },
      { name: '我的商品', path: "/myProduct", component: () => import(`@/views/user/MyProduct.vue`), meta: { requireAuth: true } },
      { name: '我的动态', path: "/myContent", component: () => import(`@/views/user/MyContent.vue`), meta: { requireAuth: true } },
      { name: '我的收藏', path: "/mySave", component: () => import(`@/views/user/MySave.vue`), meta: { requireAuth: true } },
      { name: '动态', path: "/content", component: () => import(`@/views/user/Content.vue`), meta: { requireAuth: true } },
      { name: '足迹', path: "/myView", component: () => import(`@/views/user/MyView.vue`), meta: { requireAuth: true } },
      { name: '订单', path: "/orders", component: () => import(`@/views/user/Orders.vue`), meta: { requireAuth: true } },
      { name: '消息', path: "/message", component: () => import(`@/views/user/Message.vue`), meta: { requireAuth: true } },
      { name: '个人中心', path: "/myself", component: () => import(`@/views/user/Myself.vue`), meta: { requireAuth: true } },
      { name: '搜索页', path: "/search", show: false, auth: false, component: () => import(`@/views/user/Search.vue`), meta: { requireAuth: true } },
      { name: '我的收货地址', path: "/address", show: false, component: () => import(`@/views/user/Address.vue`), meta: { requireAuth: true } },
      { name: '我的收货地址', path: "/payOrder", show: false, component: () => import(`@/views/user/PayOrder.vue`), meta: { requireAuth: true } },
      { name: '发布商品', path: "/post-product", component: () => import(`@/views/user/PostProduct.vue`), meta: { requireAuth: true } },
      { name: '发布动态', path: "/post-content", component: () => import(`@/views/user/PostContent.vue`), meta: { requireAuth: true } },
      { name: '动态详情', path: "/content-detail-user", component: () => import(`@/views/user/ContentDetailUser.vue`), meta: { requireAuth: true } },
      { name: '编辑商品', path: "/edit-product", component: () => import(`@/views/user/EditProduct.vue`), meta: { requireAuth: true } },
      { name: '编辑动态', path: "/edit-content", component: () => import(`@/views/user/EditContent.vue`), meta: { requireAuth: true } },
      { name: '商品详情', path: "/product-detail", component: () => import(`@/views/user/ProductDetail.vue`), meta: { requireAuth: true } },
    ]
  }
];

const router = new VueRouter({
  routes,
  mode: 'history'
});
router.beforeEach((to, from, next) => {
  if (to.path === '/login' || to.path === '/register') {
    return next();
  }

  if (to.matched.some(record => record.meta.requireAuth)) {
    const token = getToken();

    if (!token) {
      return next({
        path: '/login',
        query: { redirect: to.fullPath } 
      });
    }

    try {
      const role = parseInt(sessionStorage.getItem('role'));
      
      if (to.matched[0].path === '/admin' && role !== 1) {
        clearToken();
        return next("/login"); 
      }

      if (to.matched[0].path === '/user' && role !== 2) {
        clearToken();
        return next("/login"); 
      }

      return next();
    } catch (error) {
      console.error('Permission check failed:', error);
      return next('/login');
    }
  }

  next();
});
export default router;
