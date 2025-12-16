<template>
  <div class="ecommerce-platform">
    <header class="header">
      <div class="header-container">
        <div class="main-nav">
          <div class="logo-container">
            <Logo name="" bag="rgb(51,51,51)" />
          </div>
          <nav class="nav-menu">
            <router-link
              v-for="(item, index) in navItems"
              :key="index"
              :to="item.path"
              class="nav-item"
              active-class="active"
              v-if="item.show"
            >
              {{ item.name }}
            </router-link>
          </nav>
          <div class="search-box">
            <input
              type="text"
              placeholder="Enter Keywords..."
              v-model="key"
              @keyup.enter="fetch"
            />
            <button class="search-btn" @click="fetch">
              <i class="el-icon-search"></i>
            </button>
          </div>
        </div>

        <div class="user-actions">
          <template v-if="loginStatus">
            <el-dropdown trigger="click" class="user-dropdown">
              <div class="user-avatar">
                <img
                  :src="userInfo.userAvatar || '/mainIcon.png'"
                  alt="User Avatar"
                />
              </div>
              <el-dropdown-menu slot="dropdown">
                <el-dropdown-item @click.native="handleRouteSelect('/myself')"
                  >Profile</el-dropdown-item
                >
                <el-dropdown-item
                  @click.native="handleRouteSelect('/myContent')"
                  >My Threads</el-dropdown-item
                >
                <el-dropdown-item @click.native="handleRouteSelect('/myView')"
                  >Footprints</el-dropdown-item
                >
                <el-dropdown-item @click.native="logout"
                  >Logout</el-dropdown-item
                >
              </el-dropdown-menu>
            </el-dropdown>

            <div class="action-item" @click="handleRouteSelect('/orders')">
              <i class="el-icon-document"></i>
              <span>Orders</span>
            </div>
            <div class="action-item" @click="handleRouteSelect('/message')">
              <i class="el-icon-bell"></i>
              <span>Messages</span>
            </div>
            <div
              class="action-item highlight"
              @click="handleRouteSelect('/post-product')"
            >
              <i class="el-icon-plus"></i>
              <span>Post</span>
            </div>
          </template>

          <template v-else>
            <button class="login-btn" @click="loginOperation">
              Login / Register
            </button>
          </template>
        </div>
      </div>
    </header>

    <main class="main-content">
      <router-view></router-view>
    </main>
  </div>
</template>

<script>
import Logo from "@/components/Logo";
import {
  getToken,
  setUserInfo,
  setSearchKey,
  removeToken,
} from "@/utils/storage";

export default {
  name: "EcommercePlatform",
  components: {
    Logo,
  },
  data() {
    return {
      key: null,
      loginStatus: false,
      userInfo: {},
      productInfoList: [],
      searchPath: "/search",
      navItems: [
        { name: "BEST SELLING", path: "/product", show: true },
        { name: "THREAD", path: "/content", show: false },
        { name: "MY POSTS", path: "/myProduct", show: false },
        { name: "MY SAVED", path: "/mySave", show: false },
      ],
    };
  },
  created() {
    this.loadLoginStatus();
    this.handleRouteSelect("/product");
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
          console.error("商品指标查询异常：", error);
        });
    },

    loginOperation() {
      this.$router.push("/login");
    },

    logout() {
      sessionStorage.setItem("token", "");
      this.loginStatus = false;
      this.$router.push("/login");
    },

    fetch() {
      if (!this.key) return;
      setSearchKey(this.key);
      this.handleRouteSelect(this.searchPath);
    },

    handleRouteSelect(path) {
      if (this.$router.currentRoute.fullPath !== path) {
        this.$router.push(path);
      }
    },

    loadLoginStatus() {
      const token = getToken();
      if (!token) {
        this.loginStatus = false;
        return;
      }
      this.auth();
    },

    auth() {
      this.$axios
        .get(`/user/auth`)
        .then((res) => {
          const { data } = res;
          if (data.code === 200) {
            setUserInfo(data.data);
            this.userInfo = data.data;
            this.queryProductInfo();
            this.navItems = this.navItems.map((item) => ({
              ...item,
              show: item.path === "/product" || true,
            }));
          }
          this.loginStatus = data.code === 200;
        })
        .catch((error) => {
          console.error("token检验异常：", error);
        });
    },
  },
};
</script>

<style scoped lang="scss">
.ecommerce-platform {
  display: flex;
  flex-direction: column;
  min-height: 100vh;
  background-color: #fff;

  .header {
    background-image: linear-gradient(
      to right,
      rgba(151, 169, 255, 0.9) 0%,
      rgba(73, 103, 255, 0.9) 40%,
      rgba(73, 103, 255, 0.9) 60%,
      rgba(151, 169, 255, 0.9) 100%
    );
    position: sticky;
    top: 0;
    z-index: 1000;

    .header-container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 10px;
      height: 80px;
      display: flex;
      justify-content: space-between;
      align-items: center;

      .main-nav {
        display: flex;
        align-items: center;
        gap: 35px;
        flex: 1;

        .logo-container {
          margin-right: 10px;
          cursor: pointer;
        }

        .nav-menu {
          display: flex;
          gap: 35px;

          .nav-item {
            color: #ffffff;
            font-size: 20px;
            font-weight: 500;
            text-decoration: none;
            padding: 5px 0;
            position: relative;
            transition: color 0.3s;

            &.active {
              color: #ffffff;

              &::after {
                content: "";
                position: absolute;
                bottom: 0;
                left: 0;
                width: 100%;
                height: 2px;
                background-color: #ffffff;
              }
            }
          }
        }

        .search-box {
          display: flex;
          align-items: center;
          background-color: #eafd6f;
          border-radius: 10px;
          padding: 5px 5px 5px 12px;
          width: 200px;
          transition: all 0.3s;

          input {
            border: none;
            background: transparent;
            outline: none;
            flex-grow: 1;
            width: auto;
            font-size: 13px;
            color: #333;

            &::placeholder {
              color: #9eaeff;
            }
          }

          .search-btn {
            background: none;
            border: none;
            cursor: pointer;
            color: #333;
            font-size: 20px;
            margin-left: auto;
            transition: color 0.3s;

            &:hover {
              color: #4967ff;
            }
          }
        }
      }

      .user-actions {
        display: flex;
        align-items: center;
        gap: 10px;

        .action-item {
          display: flex;
          flex-direction: row;
          align-items: center;
          cursor: pointer;
          padding: 2px 5px;
          border-radius: 4px;
          transition: all 0.3s;
          gap: 3px;

          i {
            font-size: 16px;
            color: #ffffff;
            margin-bottom: 0;
          }

          span {
            font-size: 12px;
            color: #ffffff;
          }

          &:hover {
            background-color: #f5f7fa;

            i,
            span {
              color: #4967ff;
            }
          }
        }

        .highlight {
          background-color: #4967ff;
          border-radius: 15px;
          padding: 4px 10px;

          i,
          span {
            color: #ffffff;
          }

          &:hover {
            background-color: #ffffff;

            i,
            span {
              color: #4967ff !important;
            }
          }

          &:active {
            background-color: #f0f5ff; 
            i,
            span {
              color: #4967ff !important;
            }
          }
        }

        .login-btn {
          background-color: #ff6b6b;
          color: white;
          border: none;
          border-radius: 15px;
          padding: 4px 15px;
          font-size: 13px;
          cursor: pointer;
          transition: background-color 0.3s;

          &:hover {
            background-color: #ff5252;
          }
        }

        .user-dropdown {
          cursor: pointer;

          .user-avatar {
            width: 28px;
            height: 28px;
            border-radius: 50%;
            overflow: hidden;
            border: 2px solid #eee;
            transition: border-color 0.3s;

            img {
              width: 100%;
              height: 100%;
              object-fit: cover;
            }

            &:hover {
              border-color: #002aff;
            }
          }
        }
      }
    }
  }

  .user-panel {
    background: linear-gradient(135deg, #4967ff 0%, #002aff 100%);
    color: white;
    padding: 30px 0;

    .user-container {
      max-width: 1200px;
      margin: 0 auto;
      padding: 0 20px;
      display: flex;
      align-items: center;

      .user-avatar-large {
        width: 100px;
        height: 100px;
        border-radius: 50%;
        overflow: hidden;
        border: 3px solid rgba(255, 255, 255, 0.3);

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
        }
      }

      .user-info {
        margin-left: 30px;

        .user-name {
          display: flex;
          align-items: center;
          margin-bottom: 10px;

          h2 {
            margin: 0;
            font-size: 24px;
            font-weight: 600;
          }

          .user-stats {
            margin-left: 20px;
            display: flex;
            gap: 10px;

            .stat-item {
              background-color: rgba(255, 255, 255, 0.2);
              border-radius: 12px;
              padding: 3px 10px;
              font-size: 12px;
            }
          }
        }

        .user-meta {
          p {
            margin: 5px 0;
            font-size: 14px;
            opacity: 0.9;

            i {
              margin-right: 5px;
            }
          }
        }
      }
    }
  }

  .main-content {
    flex: 1;
    max-width: 1100px;
    width: 100%;
    margin: 30px auto;
    padding: 0 20px;
  }
}
</style>
