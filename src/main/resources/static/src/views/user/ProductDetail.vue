<template>
  <div id="product-detail-page">
    <div class="user-header">
      <div class="user-info-group">
        <img
          :src="product.userAvatar || '/default-avatar.png'"
          class="user-avatar"
          alt="User Avatar"
        />
        <div class="user-meta">
          <div class="user-name-credit">
            <span class="user-name">{{ product.userName || "Seller Name" }}</span>
            <span class="user-credit">Excellent Credit</span>
          </div>
          <div class="user-stats">
            <span>{{ product.itemCount || 6 }} items</span>
            <span>{{ product.discussionCount || 84 }} discussions</span>
          </div>
        </div>
      </div>
      <button class="visit-page-btn" @click="visitSellerPage">VISIT THEIR PAGE</button>
    </div>

    <div class="content-container">
      <div class="main-content-wrapper">
        <div class="left-thumbnails">
          <div
            :class="['thumbnail-item', { active: coverIndex === index }]"
            @click="coverSelected(coverItem, index)"
            v-for="(coverItem, index) in coverList"
            :key="index"
          >
            <img :src="coverItem" alt="Product Thumbnail" />
          </div>
        </div>

        <div class="main-product-area">
          <img :src="coverItem || '/default-product.png'" class="main-product-img" alt="Product Main Image" />
        </div>

        <div class="right-content-main">
          <div class="price-display-and-prediction">
            <div class="price-display">
              <span class="price-symbol">¥</span>
              <span class="price-value">{{ product.price || 21000 }}</span>
            </div>

            <div class="prediction-box" v-if="predictionVisible">
              <div class="pred-price-row">
                <span class="pred-label">Predicted Price</span>
                <span class="pred-value">¥{{ predictedPriceDisplay }}</span>
                <button class="refresh-pred-btn" @click="getPrediction" :disabled="predicting" title="Refresh prediction">
                  <i v-if="!predicting" class="el-icon-refresh"></i>
                  <i v-else class="el-icon-loading"></i>
                </button>
              </div>
              <div class="pred-verdict-row" :class="verdictClass">
                {{ predictionVerdictDisplay }}
                <span class="pred-note" v-if="predictionNote">· {{ predictionNote }}</span>
              </div>
            </div>
          </div>

          <h2 class="product-title">{{ product.name || "Product Name" }}</h2>

          <div class="product-desc-container">
            <div class="product-desc" v-html="product.detail || 'No description'"></div>

            <div class="action-buttons">
              <button class="connect-seller-btn" @click="connectSeller">Contact Seller</button>
              <button class="buy-now-btn" @click="buyProduct">Buy it now</button>
              <button class="collect-btn" @click="saveOperation" :class="{ collected: saveFlag }">
                <i class="el-icon-star-on"></i>
              </button>
            </div>
          </div>
        </div>
      </div>

      <div class="bottom-layout-wrapper">
        <div class="related-products-section">
          <h2>Other Products</h2>
          <el-empty v-if="productList.length === 0" description="No product information available"></el-empty>
          <div class="product-grid" v-else>
            <div
              class="product-card"
              v-for="(item, index) in productList"
              :key="item.id"
              @click="routePust(item.id)"
              :style="{
                boxShadow: item.inventory === 0 ? '0 2px 10px rgba(142, 0, 0, 0.05)' : '',
                backgroundColor: item.inventory === 0 ? 'rgba(142, 0, 0,0.1)' : ''
              }"
            >
              <div class="product-image-container">
                <img :src="coverListParse1(item)" :alt="item.name" class="product-image" />
                <div class="bargain-tag" v-if="item.isBargain">Bargain Supported</div>
                <div class="like-count"><i class="el-icon-star-on"></i> {{ item.likeNumber }} people want it</div>
                <div class="stock-count"><i class="el-icon-star-on"></i> Stock {{ item.inventory }}</div>
              </div>
              <div class="product-info">
                <h3 class="product-title">{{ item.name }}</h3>
                <div class="price-section">
                  <span class="price-symbol">¥</span>
                  <span class="price-value">{{ item.price }}</span>
                </div>
                <div class="seller-info">
                  <el-avatar :src="item.userAvatar" size="small"></el-avatar>
                  <span class="seller-name">{{ item.userName }}</span>
                </div>
              </div>
            </div>
          </div>
        </div>

        <div class="comment-section-wrapper">
          <div class="comment-section">
            <Evaluations v-if="product.id && userInfo.id" :contentId="product.id" contentType="PRODUCT" />
            <div v-else style="padding: 20px; font-size: 14px; color: #999;">Please login to view and post comments</div>
          </div>
        </div>
      </div>
    </div>

    <el-dialog
      title="Place Order"
      :show-close="false"
      :visible.sync="dialogProductOperaion"
      width="70%"
      custom-class="order-dialog"
    >
      <div class="order-dialog-inner">
        <div class="order-info">
          <div class="order-meta-row">
            <span class="label">Price:</span>
            <span class="value price">¥{{ product.price || 0 }}</span>
          </div>
          <div class="order-meta-row">
            <span class="label">Category:</span>
            <span class="value">{{ product.categoryName || "Uncategorized" }}</span>
          </div>
          <div class="order-meta-row">
            <span class="label">Seller:</span>
            <img :src="product.userAvatar || '/default-avatar.png'" class="seller-avatar" alt="" />
            <span class="value">{{ product.userName || "Unknown" }}</span>
          </div>
          <div class="order-meta-row">
            <span class="label">Bargain:</span>
            <span class="value bargain-tag">{{ product.isBargain ? "Available" : "No" }}</span>
          </div>
          <div class="order-meta-row">
            <span class="label">Condition:</span>
            <span class="value">{{ product.oldLevel || 0 }}/10</span>
          </div>
          <div class="order-meta-row">
            <span class="label">Stock:</span>
            <span class="value">{{ product.inventory || 0 }} pcs</span>
          </div>
          <div class="order-meta-row">
            <span class="label">Product Name:</span>
            <span class="value">{{ product.name || "No Name" }}</span>
          </div>
        </div>

        <div class="order-form">
          <div class="form-item">
            <label>Order Quantity:</label>
            <el-input-number v-model="buyNumber" :min="1" :max="product.inventory || 1"></el-input-number>
          </div>
          <div class="form-item">
            <label>Remarks:</label>
            <el-input type="textarea" :rows="3" v-model="detail" placeholder="Add your remarks..."></el-input>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <button class="btn cancel-btn" @click="cannelBuy()">Cancel</button>
        <button class="btn confirm-btn" @click="buyConfirm()">Confirm Order</button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getUserInfo } from "@/utils/storage";
import Evaluations from "@/components/Evaluations";

export default {
  name: "ProductDetail",
  components: { Evaluations },
  data() {
    return {
      productId: null,
      product: {},
      coverList: ["/default-product.png"],
      coverIndex: 0,
      coverItem: "/default-product.png",
      keyInterval: null,
      saveFlag: false,
      dialogProductOperaion: false,
      buyNumber: 1,
      detail: "",
      userInfo: {},
      productList: [],

      predictedPrice: null,
      predictionVerdict: '',
      predictionNote: '',
      predicting: false,
      predictionVisible: false
    };
  },
  computed: {
    predictedPriceDisplay() {
      return this.predictedPrice == null ? '--' : Number(this.predictedPrice).toFixed(2);
    },
    predictionVerdictDisplay() {
      const v = (this.predictionVerdict || '').toString();
      if (!v) return 'For reference only';
      if (v.includes('可以入手') || v.toLowerCase().includes('good')) return 'Good to buy';
      if (v.includes('可考虑') || v.toLowerCase().includes('consider')) return 'Consider';
      if (v.includes('偏高') || v.includes('高') || v.toLowerCase().includes('high') || v.toLowerCase().includes('expensive')) return 'Price seems high';
      return v;
    },
    verdictClass() {
      const v = (this.predictionVerdict || '').toString();
      if (!v) return '';
      if (v.includes('可以入手') || v.toLowerCase().includes('good') || v.toLowerCase().includes('buy')) return 'verdict-good';
      if (v.includes('可考虑') || v.toLowerCase().includes('consider')) return 'verdict-neutral';
      if (v.includes('偏高') || v.includes('高') || v.toLowerCase().includes('high') || v.toLowerCase().includes('expensive')) return 'verdict-bad';
      return '';
    }
  },
  created() {
    this.userInfo = getUserInfo() || {};
    this.getParam();
    this.viewOperation();
    this.getProductUser();
  },
  beforeDestroy() {
    this.clearBanner();
  },
  methods: {
    routePust(id) {
      this.productId = id;
      this.fetchProduct(this.productId);
      window.scrollTo({ top: 0, behavior: "smooth" });
    },
    getProductUser() {
      this.$axios.get(`/product/queryProductList/${this.productId}`).then((res) => {
        if (res.data.code === 200) this.productList = res.data.data;
      }).catch((err) => {
        console.log("Error loading user's products:", err);
        this.$notify.error({ title: "Error", message: "Failed to load related products" });
      });
    },
    viewOperation() {
      if (!this.userInfo.id) return;
      this.$axios.post(`/interaction/view/${this.productId}`).catch((err) => {
        console.log("View record error:", err);
      });
    },
    buyConfirm() {
      const ordersDTO = { productId: this.product.id, buyNumber: this.buyNumber, detail: this.detail };
      this.$axios.post(`/product/buyProduct`, ordersDTO).then((res) => {
        if (res.data.code === 200) {
          this.$notify.success({ title: "Success", message: res.data.msg });
          this.fetchProduct(this.product.id);
          this.cannelBuy();
        } else {
          this.$notify.error({ title: "Error", message: res.data.msg });
        }
      }).catch((err) => {
        console.log("Order error:", err);
        this.$notify.error({ title: "Error", message: "Failed to place order" });
      });
    },
    cannelBuy() {
      this.dialogProductOperaion = false;
      this.buyNumber = 1;
    },
    buyProduct() {
      if (!this.userInfo.id) {
        this.$notify.info({ title: "Tip", message: "Please login first" });
        this.$router.push("/login");
        return;
      }
      if (this.userInfo.id === this.product.userId) {
        this.$notify.warning({ title: "Warning", message: "Cannot buy your own product" });
        return;
      }
      this.dialogProductOperaion = true;
    },
    saveOperation() {
      if (!this.userInfo.id) {
        this.$notify.info({ title: "Tip", message: "Please login first" });
        this.$router.push("/login");
        return;
      }
      this.$axios.post(`/interaction/saveOperation/${this.product.id}`).then((res) => {
        if (res.data.code === 200) {
          this.saveFlag = res.data.data;
          this.$notify.success({ title: "Success", message: res.data.msg });
        }
      }).catch((err) => {
        console.log("Save/favorite error:", err);
      });
    },
    clearBanner() {
      if (this.keyInterval) clearInterval(this.keyInterval);
    },
    startBanner() {
      this.keyInterval = setInterval(() => {
        this.coverIndex = this.coverIndex === this.coverList.length - 1 ? 0 : this.coverIndex + 1;
        this.coverItem = this.coverList[this.coverIndex];
      }, 5000);
    },
    coverSelected(coverItem, index) {
      this.coverItem = coverItem;
      this.coverIndex = index;
    },
    getParam() {
      const param = this.$route.query;
      this.productId = Number(param.productId);
      this.fetchProduct(this.productId);
    },
    coverListParse1(product) {
      if (!product.coverList) return "/default-product.png";
      return product.coverList.split(",")[0] || "/default-product.png";
    },
    coverListParse(product) {
      if (!product.coverList) {
        this.coverList = ["/default-product.png"];
        this.coverItem = "/default-product.png";
        return;
      }
      this.coverList = product.coverList.split(",");
      this.coverItem = this.coverList[0];
      this.startBanner();
    },
    fetchProduct(productId) {
      this.$axios.post("/product/query", { id: productId }).then((res) => {
        if (res.data.code === 200) {
          this.product = res.data.data[0] || {};
          this.coverListParse(this.product);
          this.querySaveStatus();
          this.getPrediction();
        }
      }).catch((err) => {
        console.log("Product query error:", err);
        this.$notify.error({ title: "Error", message: "Failed to load product details" });
      });
    },
    querySaveStatus() {
      if (!this.userInfo.id) return;
      this.$axios.post("/interaction/query", {
        userId: this.userInfo.id,
        productId: this.product.id,
        type: 1
      }).then((res) => {
        this.saveFlag = res.data.total !== 0;
      }).catch((err) => {
        console.log("Save status query error:", err);
      });
    },
    visitSellerPage() {
      this.$router.push({ path: `/seller/${this.product.userId}` });
    },
    connectSeller() {
      this.$router.push({ path: "/message", query: { userId: this.product.userId } });
    },
    async getPrediction() {
      if (!this.product || !this.product.name) return;
      this.predicting = true;
      this.predictionVerdict = '';
      this.predictionNote = '';
      try {
        const payload = {
          name: this.product.name,
          description: this.product.detail || '',
          sellerPrice: this.product.price || null
        };
        const res = await this.$axios.post('/prediction/price', payload);
        if (res && res.data && res.data.code === 200 && res.data.data) {
          const d = res.data.data;
          this.predictedPrice = d.predictedPrice;
          this.predictionVerdict = d.verdict;
          this.predictionNote = d.note;
          this.predictionVisible = true;
        } else {
          this.$notify.warning({ title: 'Prediction Failed', message: (res.data && res.data.msg) || 'Prediction did not return a valid result' });
          this.predictionVisible = false;
        }
      } catch (err) {
        console.error('Prediction error:', err);
        this.$notify.error({ title: 'Error', message: 'Failed to request predicted price' });
        this.predictionVisible = false;
      } finally {
        this.predicting = false;
      }
    }
  }
};
</script>

<style lang="scss">
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
  font-family: Arial, sans-serif;
}

#product-detail-page {
  max-width: 1400px;
  margin: 0 auto;
  padding: 20px;
}

.user-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: #fff;
  border: 3px solid #9eaeff;
  border-radius: 20px;
  margin-bottom: 20px;

  .user-info-group {
    display: flex;
    align-items: center;
    gap: 15px;

    .user-avatar {
      width: 50px;
      height: 50px;
      border-radius: 50%;
      object-fit: cover;
    }

    .user-meta {
      display: flex;
      align-items: center;
      gap: 30px;

      .user-name-credit {
        display: flex;
        flex-direction: column;

        .user-name {
          font-size: 16px;
          font-weight: 600;
          color: #333;
        }

        .user-credit {
          font-size: 14px;
          color: #002aff;
          margin-top: 2px;
        }
      }

      .user-stats {
        font-size: 12px;
        color: #999;
        display: flex;
        gap: 15px;
      }
    }
  }

  .visit-page-btn {
    background: transparent;
    border: none;
    color: #333;
    padding: 8px 16px;
    cursor: pointer;
    transition: color 0.3s;
    border-radius: 4px;

    &:hover {
      color: #9eaeff;
    }
  }
}

.content-container {
  padding: 40px 20px;
  border-radius: 20px;
  background: linear-gradient(
    to bottom,
    rgba(158, 174, 255, 0.6) 2%,
    rgba(232, 236, 255, 0.6) 20%,
    rgba(255, 255, 255, 0.6) 100%
  );
  box-shadow: 0 4px 20px 0 #2f51ff;
  margin-bottom: 20px;
}

.main-content-wrapper {
  display: flex;
  justify-content: center;
  align-items: flex-start;
  gap: 40px;
  margin-bottom: 60px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.left-thumbnails {
  display: flex;
  flex-direction: column;
  gap: 10px;
  margin-left: 20px;
}

.thumbnail-item {
  width: 60px;
  height: 60px;
  border-radius: 10px;
  overflow: hidden;
  cursor: pointer;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  &.active,
  &:hover {
    opacity: 0.9;
  }
}

.main-product-area {
  width: 380px;
  height: 480px;

  .main-product-img {
    width: 100%;
    height: 100%;
    border-radius: 10px;
    object-fit: cover;
  }
}

.right-content-main {
  width: 500px;
  height: 480px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  padding: 0 8px;
}
.price-display-and-prediction {
  display: flex;
  align-items: center;
  gap: 16px;
}

.prediction-box {
  background: #fff;
  border-radius: 8px;
  padding: 8px 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  display: flex;
  flex-direction: column;
  min-width: 220px;
}

.pred-price-row {
  display: flex;
  align-items: center;
  gap: 8px;
}
.pred-label {
  font-size: 12px;
  color: #888;
}
.pred-value {
  font-size: 18px;
  font-weight: 700;
  color: #ff4f24;
}
.refresh-pred-btn {
  margin-left: auto;
  border: none;
  background: transparent;
  cursor: pointer;
  font-size: 14px;
}
.pred-verdict-row {
  margin-top: 6px;
  font-size: 13px;
  padding: 4px 8px;
  border-radius: 6px;
  display: inline-block;
}
.pred-note { color:#666; font-size:12px; margin-left:6px; }

.verdict-good { background: #e6ffef; color:#118b4b; }
.verdict-neutral { background: #fff6e6; color:#8b6a00; }
.verdict-bad { background: #ffecec; color:#b30000; }

.price-display {
  .price-symbol,
  .price-value {
    font-size: 28px;
    font-weight: 800;
    color: #ff4f24;
    line-height: 1;
  }
  .price-symbol {
    margin-right: 4px;
  }
}

.product-title {
  font-size: 20px;
  color: #333;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.product-desc-container {
  position: relative;
  width: 100%;
  height: 414px;
  flex: 1;

  .product-desc {
    font-size: 16px;
    color: #666;
    line-height: 1.6;
    width: 100%;
    height: 100%;
    overflow-y: auto;
    padding-bottom: 0;

    &::-webkit-scrollbar {
      width: 6px;
    }
    &::-webkit-scrollbar-track {
      background: #f5f5f5;
      border-radius: 3px;
    }
    &::-webkit-scrollbar-thumb {
      background: #ddd;
      border-radius: 3px;
    }
  }

  .action-buttons {
    position: absolute;
    bottom: 30px;
    left: 50%;
    transform: translateX(-50%);
    width: 354px;
    height: 50px;
    display: flex;
    background: #eafd6f;
    align-items: center;
    box-shadow: 0 2px 10px #4967ff;
    border-radius: 10px;

    .connect-seller-btn,
    .buy-now-btn {
      flex: 1;
      height: 100%;
      background: transparent;
      border: none;
      color: #333;
      font-weight: 600;
      font-size: 14px;
      cursor: pointer;
      border-right: 1px solid #333;
    }

    .collect-btn {
      padding: 0 16px;
      background: transparent;
      border: none;
      color: #333;
      font-size: 16px;
      cursor: pointer;

      &.collected {
        color: #ffcc00;
      }
    }
  }
}

.bottom-layout-wrapper {
  display: flex;
  gap: 0;
  align-items: flex-start;
  justify-content: center;
  max-width: none;
  margin: 0 auto;
}

.related-products-section {
  width: 440px;
  margin: 0;
  padding: 0;
  margin-left: 120px;

  h2 {
    font-size: 20px;
    color: #333;
    margin-bottom: 20px;
  }

  .product-grid {
    display: grid;
    grid-template-columns: 1fr;
    gap: 20px;
    width: 100%;
  }

  .product-card {
    width: 300px;
    max-width: 300px;
    min-width: 300px;
    background: #fff;
    border-radius: 12px;
    overflow: hidden;
    cursor: pointer;
    transition: box-shadow 0.3s;

    &:hover {
      box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
      .product-image {
        transform: scale(1.05);
      }
    }

    .product-image-container {
      position: relative;
      height: 180px;
      overflow: hidden;

      .product-image {
        width: 100%;
        height: 100%;
        object-fit: cover;
        transition: transform 0.5s;
      }

      .bargain-tag {
        position: absolute;
        top: 10px;
        left: 10px;
        background: #CEC3FF;
        color: #FFFFFF;
        font-size: 12px;
        font-weight: bold;
        padding: 2px 6px;
        border-radius: 12px;
      }

      .like-count,
      .stock-count {
        position: absolute;
        bottom: 10px;
        background: rgba(0, 0, 0, 0.6);
        color: #fff;
        font-size: 12px;
        padding: 4px 10px;
        border-radius: 12px;
        display: flex;
        align-items: center;

        i {
          margin-right: 5px;
          color: #ffcc00;
        }
      }

      .like-count {
        right: 10px;
      }

      .stock-count {
        left: 10px;
      }
    }

    .product-info {
      padding: 10px;

      .product-title {
        font-size: 14px;
        color: #333;
        white-space: nowrap;
        overflow: hidden;
        text-overflow: ellipsis;
        margin-bottom: 8px;
      }

      .price-section {
        margin-bottom: 8px;

        .price-symbol {
          font-size: 12px;
          color: #ff4f24;
          font-weight: bold;
        }

        .price-value {
          font-size: 16px;
          color: #ff4f24;
          font-weight: bold;
        }
      }

      .seller-info {
        display: flex;
        align-items: center;
        gap: 6px;

        .seller-name {
          font-size: 12px;
          color: #666;
        }
      }
    }
  }
}

.comment-section-wrapper {
  flex: 1;
  max-width: 562px;
  margin: 0;
  margin-right: 50px;

  .comment-section {
    width: 100%;
    padding: 0;
  }
}

.order-dialog {
  z-index: 9999 !important;

  .order-dialog-inner {
    padding: 20px;
  }

  .order-info {
    margin-bottom: 20px;

    .order-meta-row {
      display: flex;
      align-items: center;
      margin-bottom: 10px;
      gap: 10px;

      .label {
        width: 80px;
        font-size: 14px;
        color: #666;
        font-weight: bold;
      }

      .value {
        font-size: 14px;
        color: #333;
      }

      .price {
        color: #ff4f24;
        font-weight: bold;
        font-size: 16px;
      }

      .bargain-tag {
        background: #ffcc00;
        padding: 2px 6px;
        border-radius: 4px;
        color: #333;
        font-size: 12px;
      }

      .seller-avatar {
        width: 20px;
        height: 20px;
        border-radius: 50%;
        margin-right: 5px;
      }
    }
  }

  .order-form {
    .form-item {
      margin-bottom: 15px;

      label {
        display: block;
        margin-bottom: 8px;
        font-size: 14px;
        color: #333;
      }

      .el-input-number {
        width: 200px;
      }
    }
  }

  .dialog-footer {
    display: flex;
    justify-content: flex-end;
    padding: 10px 20px;
    border-top: 1px solid #eee;

    .btn {
      padding: 8px 20px;
      border-radius: 4px;
      cursor: pointer;
      font-size: 14px;
      border: none;
      margin-left: 10px;
    }

    .cancel-btn {
      background: #f5f5f5;
      color: #666;
    }

    .confirm-btn {
      background: #4967ff;
      color: #fff;
    }
  }
}
</style>