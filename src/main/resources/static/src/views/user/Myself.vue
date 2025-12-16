<template>
  <div class="user-center">
    <div class="user-panel" v-if="loginStatus">
      <div class="user-container">
        <div class="user-avatar-large">
          <img :src="userInfo.userAvatar || '/logo.png'" alt="User Avatar" />
        </div>
        <div class="user-info">
          <div class="user-name">
            <h2>{{ userInfo.userName }}</h2>
            <div class="user-stats">
              <span
                v-for="(info, index) in productInfoList"
                :key="index"
                class="stat-item"
              >
                {{ info.name }}·{{ info.count }}
              </span>
            </div>
          </div>
          <div class="user-meta">
            <p>
              <i class="el-icon-time"></i> Last Login:
              {{ formatDate(userInfo.lastLoginTime) }}
            </p>
            <p>
              <i class="el-icon-user"></i> Registered On:
              {{ formatDate(userInfo.createTime) }}
            </p>
          </div>
        </div>
      </div>
    </div>

    <div class="user-main">
      <div class="user-nav">
        <div
          v-for="(item, index) in navItems"
          :key="index"
          class="nav-item"
          :class="{ active: activeNav === item.key }"
          @click="switchNav(item.key)"
        >
          <i :class="item.icon"></i>
          <span>{{ item.title }}</span>
          <i class="el-icon-arrow-right"></i>
        </div>
      </div>

      <div class="user-content">
        <transition name="fade" mode="out-in">
          <keep-alive>
            <component :is="currentComponent"></component>
          </keep-alive>
        </transition>
      </div>
    </div>
  </div>
</template>

<script>
import ResetPwd from "@/views/user/ResetPwd";
import Self from "@/views/user/Self";
import Address from "@/views/user/Address.vue";
export default {
  components: { ResetPwd, Self, Address },
  data() {
    return {
      loginStatus: false,
      userInfo: {},
      productInfoList: [],
      defaultAvatar:
        "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
      activeGoods: {},
      activeNav: "profile",
      navItems: [
        {
          key: "profile",
          title: "Personal Profile",
          icon: "el-icon-user",
          component: "Self",
        },
        {
          key: "address",
          title: "Shipping/Delivery Address",
          icon: "el-icon-location-outline",
          component: "Address",
        },
        {
          key: "security",
          title: "Account Security",
          icon: "el-icon-lock",
          component: "ResetPwd",
        },
        {
          key: "logout",
          title: "Logout",
          icon: "el-icon-switch-button",
          component: null,
        },
      ],
    };
  },
  computed: {
    currentComponent() {
      const item = this.navItems.find((item) => item.key === this.activeNav);
      return item ? item.component : null;
    },
  },
  created() {
    this.fetchUserInfo();
    this.queryProductInfo();
  },
  methods: {
    formatDate(date) {
      if (!date) return "--";
      return new Date(date).toLocaleString();
    },

    queryProductInfo() {
      this.$axios
        .post(`/product/queryProductInfo`, {})
        .then((res) => {
          const { data } = res;
          if (data.code === 200) {
            this.productInfoList = data.data;
          }
          this.loginStatus = data.code === 200;
        })
        .catch((error) => {
          console.error("Product metrics query exception: ", error);
          this.loginStatus = false;
        });
    },

    async fetchUserInfo() {
      try {
        const res = await this.$axios.get("/user/auth");
        if (res.data.code === 200) {
          this.userInfo = res.data.data;
        }
      } catch (error) {
        console.error("Failed to get user info: ", error);
      }
    },

    switchNav(navKey) {
      if (navKey === "logout") {
        this.logout();
        return;
      }
      this.activeNav = navKey;
    },

    logout() {
      this.$confirm("Are you sure to log out?", "Prompt", {
        confirmButtonText: "Confirm",
        cancelButtonText: "Cancel",
        type: "warning",
      }).then(() => {
        sessionStorage.clear();
        this.$router.push("/login");
        this.$message.success("Logged out successfully");
      });
    },
  },
};
</script>

<style scoped>
.user-center {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
}

.user-panel {
  background: linear-gradient(135deg, #4967ff 0%, #002aff 100%);
  color: white;
  padding: 30px 0;
}

.user-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  align-items: center;
}

.user-avatar-large {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  overflow: hidden;
  border: 3px solid rgba(255, 255, 255, 0.3);
}

.user-avatar-large img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.user-info {
  margin-left: 30px;
}

.user-name {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.user-name h2 {
  margin: 0;
  font-size: 24px;
  font-weight: 600;
}

.user-stats {
  margin-left: 20px;
  display: flex;
  gap: 10px;
}

.stat-item {
  background-color: rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  padding: 3px 10px;
  font-size: 12px;
}

.user-meta p {
  margin: 5px 0;
  font-size: 14px;
  opacity: 0.9;
}

.user-meta p i {
  margin-right: 5px;
}

.user-main {
  display: flex;
  gap: 20px;
  margin-top: 20px;
}

.user-nav {
  width: 240px;
  background-color: #fff;
  border-radius: 8px;
  padding: 10px 0;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.nav-item {
  display: flex;
  align-items: center;
  padding: 15px 20px;
  cursor: pointer;
  transition: all 0.3s;
  border-left: 3px solid transparent;
}

.nav-item i:first-child {
  margin-right: 10px;
  font-size: 18px;
  color: #606266;
}

.nav-item span {
  flex: 1;
  font-size: 16px;
  color: #303133;
}

.nav-item i:last-child {
  color: #909399;
  font-size: 14px;
}

.nav-item:hover {
  background-color: #f5f7fa;
  border-left-color: #409eff;
}

.nav-item:hover i:first-child,
.nav-item:hover span {
  color: #409eff;
}

.nav-item.active {
  background-color: #ecf5ff;
  border-left-color: #409eff;
}

.nav-item.active i:first-child,
.nav-item.active span {
  color: #409eff;
}

.user-content {
  flex: 1;
  min-height: 600px;
  background-color: #fff;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .user-main {
    flex-direction: column;
  }

  .user-nav {
    width: 100%;
    margin-bottom: 15px;
  }
}
</style>