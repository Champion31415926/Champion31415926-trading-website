<template>
  <div class="product-management">
    <div class="header-section">
      <h2 class="page-title"></h2>
    </div>

    <div class="product-list-container">
      <div v-if="loading" class="loading-container">
        <el-skeleton :rows="6" animated />
      </div>

      <div v-else-if="productList.length === 0" class="empty-container">
        <el-empty description="No products found">
        </el-empty>
      </div>

      <el-row :gutter="20" v-else>
        <el-col
          :xs="24"
          :sm="12"
          :md="8"
          :lg="6"
          v-for="(product, index) in productList"
          :key="product.id"
        >
          <div class="product-card" @click="goToProductDetail(product)">
            <div class="product-cover">
              <transition name="fade">
                <img :src="getProductCover(product)" :alt="product.name">
              </transition>
              <div class="bargain-tag" :class="{ active: product.isBargain }">
                {{
                  product.isBargain ? "Support Bargain" : "Not Support Bargain"
                }}
              </div>
            </div>

            <div class="product-info">
              <h3 class="product-title">{{ product.name }}</h3>

              <div class="price-section">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ product.price }}</span>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import { setProductInfo } from "@/utils/storage";

export default {
  name: "MyProductList",
  data() {
    return {
      productList: [],
      loading: true,
    };
  },
  created() {
    this.fetchProducts();
  },
  methods: {
    goToProductDetail(product) {
      this.$router.push(`/product-detail?productId=${product.id}`);
    },

    getProductCover(product) {
      if (!product.coverList) return "";
      const covers = product.coverList.split(",");
      return covers[0] || "";
    },

    async fetchProducts() {
      this.loading = true;
      try {
        const response = await this.$axios.post("/interaction/queryUser");

        if (response.data.code === 200) {
          this.productList = Array.isArray(response.data.data)
            ? response.data.data
            : [];
        }
      } catch (error) {
        console.error("商品查询异常:", error);
        this.$message.error("Failed to fetch product list");
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped lang="scss">
$primary-blue: #9eaeff;

.product-management {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.header-section {
  margin-bottom: 30px;

  .page-title {
    font-size: 24px;
    color: #0323ff;
    margin: 0;
  }
}

.loading-container {
  padding: 40px 0;
}

.empty-container {
  margin: 50px 0;
}

.product-card {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;
  cursor: pointer;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  }
}

.product-cover {
  position: relative;
  border: 3px solid $primary-blue;
  border-radius: 12px;
  padding: 4px;

  img {
    width: 100%;
    height: 200px;
    object-fit: cover;
    display: block;
    border-radius: 8px;
    transition: transform 0.3s;
  }

  &:hover img {
    transform: scale(1.03);
  }

  .bargain-tag {
    position: absolute;
    top: 10px;
    left: 10px;
    background: #cec3ff;
    color: #fff !important;
    padding: 3px 8px;
    font-size: 12px;
    border-radius: 4px;
    z-index: 1;

    &.active {
      background: #0323ff;
      color: white !important;
    }
  }
}

.product-info {
  padding: 15px;

  .product-title {
    font-size: 16px;
    margin: 0 0 10px;
    color: #333;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;

    &:hover {
      color: $primary-blue;
    }
  }
}

.price-section {
  display: flex;
  align-items: center;
  margin: 10px 0;

  .price-symbol {
    color: #ff4f24;
    font-weight: bold;
    font-size: 14px;
  }

  .price-value {
    color: #ff4f24;
    font-weight: bold;
    font-size: 20px;
    margin: 0 10px 0 5px;
  }
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.5s;
}

.fade-enter,
.fade-leave-to {
  opacity: 0;
}

@media (max-width: 768px) {
  .product-cover img {
    height: 150px;
  }
}
</style>