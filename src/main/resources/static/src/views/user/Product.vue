<template>
  <div class="product-page">
    <div class="filter-section">
      <div class="custom-filter-header" @click="toggleCollapse">
        <span class="filter-title">FILTER</span>
        <i
          :class="isCollapsed ? 'el-icon-arrow-down' : 'el-icon-arrow-up'"
          class="header-icon"
        ></i>
      </div>

      <div v-if="!isCollapsed" class="filter-content">
        <div class="filter-container">
          <div class="category-filter">
            <div class="filter-label">Category：</div>
            <div class="category-tags">
              <el-tag
                v-for="(category, index) in isUseCategoryList"
                :key="index"
                :type="categorySelectedItem.id === category.id ? '' : 'info'"
                :effect="
                  categorySelectedItem.id === category.id ? 'dark' : 'plain'
                "
                @click="categorySelected(category)"
                class="category-tag"
              >
                {{ category.name }}
              </el-tag>
            </div>
          </div>

          <div class="advanced-filters">
            <div class="filter-label">Bargain Available：</div>
            <div class="filter-group">
              <el-tag
                v-for="(bargain, index) in bargainStatus"
                :key="index"
                :type="
                  bargainSelectedItem.isBargain === bargain.isBargain
                    ? ''
                    : 'info'
                "
                :effect="
                  bargainSelectedItem.isBargain === bargain.isBargain
                    ? 'dark'
                    : 'plain'
                "
                @click="bargainSelected(bargain)"
                class="filter-tag"
              >
                {{ bargain.name }}
              </el-tag>
            </div>
          </div>
        </div>
      </div>
    </div>

    <div class="product-list-container">
      <el-empty
        v-if="productList.length === 0"
        description="No products found"
      ></el-empty>
      <transition-group
        key="model-7"
        v-else
        name="product-list"
        tag="div"
        class="product-grid"
      >
        <div
          v-for="(product, index) in productList"
          :key="product.id"
          class="product-card"
          @click="route(product)"
        >
          <div class="product-image-container">
            <transition name="fade">
              <img :src="coverListParse(product)" :alt="product.name"
              class="product-image">
            </transition>
          </div>
          <div class="product-info">
            <h3 class="product-title">{{ product.name }}</h3>
            <div class="price-section">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ product.price }}</span>
            </div>
            <div class="seller-info">
              <el-avatar :src="product.userAvatar" size="small"></el-avatar>
              <span class="seller-name">{{ product.userName }}</span>
            </div>
          </div>
        </div>
      </transition-group>
    </div>
  </div>
</template>

<script>
export default {
  name: "Product",
  data() {
    return {
      isCollapsed: false,
      categoryList: [],
      isUseCategoryList: [],
      categorySelectedItem: {},
      productQueryDto: {},
      productList: [],
      bargainSelectedItem: {},
      searchTime: [],
      bargainStatus: [
        { isBargain: null, name: "All" },
        { isBargain: true, name: "Support Bargain" },
        { isBargain: false, name: "Not Support Bargain" },
      ],
    };
  },
  created() {
    this.fetchFreshData();
    this.fetchCategoryList();
    this.bargainSelected(this.bargainStatus[0]);
  },
  methods: {
    toggleCollapse() {
      this.isCollapsed = !this.isCollapsed;
    },
    route(product) {
      this.$router.push("/product-detail?productId=" + product.id);
    },
    coverListParse(product) {
      if (!product.coverList) return "";
      const newCoverList = product.coverList.split(",");
      return newCoverList[0] || "";
    },
    bargainSelected(bargain) {
      this.bargainSelectedItem = bargain;
      this.productQueryDto.isBargain = bargain.isBargain;
      this.fetchFreshData();
    },
    async fetchFreshData() {
      let startTime = null;
      let endTime = null;
      if (this.searchTime && this.searchTime.length === 2) {
        startTime = `${this.searchTime[0]}T00:00:00`;
        endTime = `${this.searchTime[1]}T23:59:59`;
      }
      this.productQueryDto = { ...this.productQueryDto, startTime, endTime };
      try {
        const res = await this.$axios.post(
          "/product/query",
          this.productQueryDto
        );
        if (res.data.code === 200) this.productList = res.data.data;
      } catch (error) {
        console.error("Product query error:", error);
        this.$message.error("Failed to fetch product list");
      }
    },
    categorySelected(category) {
      this.categorySelectedItem = category;
      this.productQueryDto.categoryId = category.id;
      this.fetchFreshData();
    },
    fetchCategoryList() {
      this.$axios
        .post("/category/query", {})
        .then((res) => {
          if (res.data.code === 200) {
            this.categoryList = res.data.data;
            this.isUseCategoryList = res.data.data.filter(
              (category) => category.isUse
            );
            this.isUseCategoryList.unshift({ id: null, name: "All" });
            this.categorySelected(this.isUseCategoryList[0]);
          }
        })
        .catch((error) =>
          console.error("Product category query error:", error)
        );
    },
  },
};
</script>

<style scoped lang="scss">
$primary-blue: #9eaeff;
$target-filter-color: #4967ff;

.product-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;

  .filter-section {
    margin-bottom: 20px;

    .custom-filter-header {
      display: flex;
      align-items: center;
      gap: 8px;
      font-size: 18px;
      font-weight: 700;
      color: $target-filter-color;
      cursor: pointer;
      background: transparent;
      border: none;
      padding: 0;
    }

    .header-icon {
      font-size: 16px;
      color: $target-filter-color;
      transition: transform 0.3s;
    }

    .filter-content {
      padding: 10px 0;
      margin-top: 8px;
    }

    .filter-container {
      .category-filter {
        margin-bottom: 15px;
      }
      .filter-label {
        font-weight: 500;
        color: #555;
        margin-bottom: 8px;
      }
      .category-tags,
      .filter-group {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
        ::v-deep .el-tag {
          cursor: pointer;
          transition: all 0.3s;
          border-radius: 20px;
          &.el-tag--dark:not(.el-tag--info) {
            background: $primary-blue !important;
            border-color: $primary-blue !important;
            color: #fff !important;
          }
          &.el-tag--info.el-tag--plain {
            background: #f4f6ff !important;
            border-color: #e4e7ed !important;
          }
        }
      }
    }
  }

  .product-list-container {
    .product-grid {
      display: grid;
      grid-template-columns: repeat(4, 1fr);
      gap: 25px;

      .product-list-move {
        transition: transform 0.5s;
      }

      .product-list-enter-active,
      .product-list-leave-active {
        transition: all 0.5s;
      }

      .product-list-enter,
      .product-list-leave-to {
        opacity: 0;
        transform: translateY(20px);
      }

      .product-card {
        overflow: hidden;
        transition: all 0.3s;
        cursor: pointer;

        &:hover {
          .product-image {
            transform: scale(1.03);
          }
          .product-title {
            color: $primary-blue;
          }
        }

        .product-image-container {
          position: relative;
          padding-top: 100%;
          height: 0;
          overflow: hidden;
          background-color: #f5f5f5;
          border: 3px solid $primary-blue;
          border-radius: 12px;

          .product-image {
            position: absolute;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            object-fit: cover;
            transition: transform 0.5s;
            border-radius: 8px;
          }
        }

        .product-info {
          padding: 15px;

          .product-title {
            margin: 0 0 10px 0;
            font-size: 18px;
            color: #333;
            font-weight: 600;
            white-space: nowrap;
            overflow: hidden;
            text-overflow: ellipsis;
            transition: color 0.3s ease;

            &:hover {
              color: $primary-blue;
            }
          }

          .price-section {
            margin-bottom: 15px;

            .price-symbol {
              font-size: 14px;
              color: #ff4f24;
              font-weight: bold;
            }

            .price-value {
              font-size: 20px;
              color: #ff4f24;
              font-weight: bold;
            }
          }

          .seller-info {
            display: flex;
            align-items: center;
            gap: 8px;

            .seller-name {
              font-size: 14px;
              color: #666;
              transition: color 0.3s ease;

              &:hover {
                color: $primary-blue;
              }
            }
          }
        }
      }
    }
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
</style>