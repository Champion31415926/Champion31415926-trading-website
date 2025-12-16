<template>
  <div class="browse-history">
    <!-- Header Action Bar -->
    <div class="history-header">
      <h2 class="page-title">My Browsing History</h2>
      <div class="action-buttons">
        <el-button
          type="text"
          icon="el-icon-delete"
          @click="clearView"
          class="clear-button"
        >
          Clear All History
        </el-button>
      </div>
    </div>

    <!-- Browsing History List -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>

    <div v-else-if="productList.length === 0" class="empty-container">
      <el-empty description="No browsing history">
        <el-button
          type="primary"
          @click="$router.push('/')"
          class="empty-button"
        >
          Browse Products
        </el-button>
      </el-empty>
    </div>

    <el-row :gutter="20" v-else>
      <el-col
        :xs="24"
        :sm="12"
        :md="8"
        :lg="6"
        v-for="(product, index) in productList"
        :key="index"
      >
        <div class="history-item">
          <!-- Product Cover -->
          <div class="product-cover" @click="route(product)">
            <img :src="getCoverImage(product)" alt="Product Cover" />
            <div class="bargain-tag" :class="{ active: product.isBargain }">
              {{ product.isBargain ? "Bargain Supported" : "No Bargain" }}
            </div>
          </div>

          <!-- Product Information -->
          <div class="product-info">
            <h3 class="product-title" @click="route(product)">
              {{ product.name }}
            </h3>

            <div class="price-section">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ product.price }}</span>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
export default {
  name: "BrowseHistory",
  data() {
    return {
      productList: [],
      loading: true,
    };
  },
  created() {
    this.fetchProduct();
  },
  methods: {
    // Clear browsing history
    async clearView() {
      try {
        await this.$confirm("Are you sure to clear all browsing history?", "Reminder", {
          confirmButtonText: "Confirm",
          cancelButtonText: "Cancel",
          type: "warning",
        });

        const response = await this.$axios.post("/interaction/batchDeleteView");

        if (response.data.code === 200) {
          this.$message.success(response.data.msg || "Browsing history cleared successfully");
          this.productList = [];
        }
      } catch (error) {
        if (error !== "cancel") {
          console.error("Exception in clearing browsing history:", error);
          this.$message.error("Failed to clear browsing history");
        }
      }
    },

    // Navigate to product detail page
    route(product) {
      this.$router.push(`/product-detail?productId=${product.id}`);
    },

    // Get product cover image
    getCoverImage(product) {
      if (!product.coverList) return "";
      const covers = product.coverList.split(",");
      return covers[0] || "";
    },

    // Get browsing history list
    async fetchProduct() {
      this.loading = true;
      try {
        const response = await this.$axios.post("/interaction/myView");

        if (response.data.code === 200) {
          this.productList = Array.isArray(response.data.data)
            ? response.data.data
            : [];
        }
      } catch (error) {
        console.error("Exception in querying browsing history:", error);
        this.$message.error("Failed to get browsing history list");
      } finally {
        this.loading = false;
      }
    },
  },
};
</script>

<style scoped lang="scss">
.browse-history {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.history-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
  border-bottom: 1px solid #eee;
  padding-bottom: 15px;

  .page-title {
    font-size: 24px;
    color: #333;
    margin: 0;
  }

  .clear-button {
    color: #f56c6c;
    font-size: 14px;
    padding: 5px 10px;

    &:hover {
      background-color: #fef0f0;
    }
  }
}

.loading-container {
  padding: 40px 0;
}

.empty-container {
  margin: 50px 0;

  .empty-button {
    margin-top: 20px;
  }
}

.history-item {
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  margin-bottom: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  transition: all 0.3s;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
  }
}

.product-cover {
  position: relative;
  cursor: pointer;

  img {
    width: 100%;
    height: 200px;
    object-fit: cover;
    display: block;
  }

  .bargain-tag {
    position: absolute;
    top: 10px;
    left: 10px;
    background: rgba(255, 230, 15, 0.9);
    color: #333;
    padding: 3px 8px;
    font-size: 12px;
    border-radius: 4px;
    font-weight: bold;

    &.active {
      background: rgba(255, 150, 0, 0.9);
      color: white;
    }
  }
}

.product-info {
  padding: 15px;

  .product-title {
    font-size: 16px;
    margin: 0 0 10px;
    color: #333;
    cursor: pointer;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;

    &:hover {
      color: #409eff;
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
    margin-left: 5px;
  }
}

@media (max-width: 768px) {
  .history-header {
    flex-direction: column;
    align-items: flex-start;

    .clear-button {
      margin-top: 15px;
    }
  }

  .product-cover img {
    height: 150px;
  }
}
</style>