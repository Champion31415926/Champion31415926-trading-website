<template>
  <div class="product-management">
    <div class="header-section">
      <h2 class="page-title">My Product</h2>
      <el-button
        type="primary"
        icon="el-icon-plus"
        @click="$router.push('/post-product')"
        class="add-button"
      >
        Publish New Product
      </el-button>
    </div>

    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="6" animated />
    </div>

    <div v-else-if="productList.length === 0" class="empty-container">
      <el-empty description="No products found">
        <el-button
          type="primary"
          @click="$router.push('/post-product')"
          class="empty-button"
        >
          Publish My First Product
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
        <div class="product-card">
          <div class="product-cover" @click="route(product)">
            <img :src="getCoverImage(product)" alt="Product cover" />
            <div class="bargain-tag" :class="{ active: product.isBargain }">
              {{
                product.isBargain ? "Support Bargain" : "Not Support Bargain"
              }}
            </div>
          </div>

          <div class="product-info">
            <h3 class="product-title" @click="route(product)">
              {{ product.name }}
            </h3>

            <div class="price-section">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ product.price }}</span>
              <span class="like-count"
                >{{ product.likeNumber }} people want</span
              >
            </div>

            <div class="action-buttons">
              <el-button
                size="mini"
                @click="handleEdit(product)"
                class="delete-button customer"
              >
                Edit
              </el-button>
              <el-button
                size="mini"
                @click="handleDelete(product)"
                class="delete-button customer"
              >
                Delete
              </el-button>
            </div>
          </div>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { setProductInfo } from "@/utils/storage";

export default {
  name: "MyProduct",
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
    route(product) {
      this.$router.push(`/product-detail?productId=${product.id}`);
    },

    handleEdit(product) {
      setProductInfo(product);
      this.$router.push("/edit-product");
    },

    async handleDelete(product) {
      try {
        await this.$confirm(
          `Are you sure to delete the product [${product.name}]?`,
          "Confirmation",
          {
            confirmButtonText: "Confirm",
            cancelButtonText: "Cancel",
            type: "warning",
          }
        );

        const response = await this.$axios.post("/product/batchDelete", [
          product.id,
        ]);

        if (response.data.code === 200) {
          this.$message.success("Product deleted successfully");
          this.fetchProduct();
        }
      } catch (error) {
        if (error !== "cancel") {
          this.$message.error("Deletion failed: " + (error.message || error));
          console.error("Product deletion error:", error);
        }
      }
    },

    getCoverImage(product) {
      if (!product.coverList) return "";
      const covers = product.coverList.split(",");
      return covers[0] || "";
    },

    async fetchProduct() {
      this.loading = true;
      try {
        const response = await this.$axios.post("/product/queryUser", {});

        if (response.data.code === 200) {
          this.productList = Array.isArray(response.data.data)
            ? response.data.data
            : [];
        }
      } catch (error) {
        console.error("Product query error:", error);
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;

  .page-title {
    font-size: 24px;
    color: #333;
    margin: 0;
  }

  .add-button {
    padding: 10px 20px;
    background-color: $primary-blue !important;
    color: #fff !important;
    border-color: $primary-blue !important;
  }
}

.loading-container {
  padding: 40px 0;
}

.empty-container {
  margin: 50px 0;

  .empty-button {
    margin-top: 20px;
    background-color: $primary-blue !important;
    color: #fff !important;
    border-color: $primary-blue !important;
  }
}

.product-card {
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
  border: 3px solid $primary-blue;
  border-radius: 12px;
  padding: 4px;

  img {
    width: 100%;
    height: 200px;
    object-fit: cover;
    display: block;
    border-radius: 8px;
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
    cursor: pointer;
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

  .like-count {
    color: #999;
    font-size: 13px;
  }
}

.action-buttons {
  display: flex;
  justify-content: space-between;
  margin-top: 15px;

  .delete-button {
    flex: 1;
    color: #9eaeff;
    border-color: #9eaeff;

    &:hover {
      background: #fef0f0;
    }
  }
}

@media (max-width: 768px) {
  .header-section {
    flex-direction: column;
    align-items: flex-start;

    .add-button {
      margin-top: 15px;
      width: 100%;
    }
  }

  .product-cover img {
    height: 150px;
  }
}
</style>