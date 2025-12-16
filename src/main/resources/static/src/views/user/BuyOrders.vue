<template>
  <div class="order-container">
    <div class="filter-section">
      <div class="status-tabs">
        <el-tag
          v-for="(tradeStatus, index) in tradeStatusList"
          :key="index"
          :type="
            tradeStatusSelectedItem.tradeStatus === tradeStatus.tradeStatus
              ? ''
              : 'info'
          "
          :effect="
            tradeStatusSelectedItem.tradeStatus === tradeStatus.tradeStatus
              ? 'dark'
              : 'plain'
          "
          @click="tradeStatusSelected(tradeStatus)"
          class="status-tab"
        >
          {{ tradeStatus.name }}
        </el-tag>
      </div>

      <el-input
        v-model="ordersQueryDto.code"
        placeholder="Search Order No."
        clearable
        @clear="handleFilterClear"
        @keyup.enter.native="fetchOrders"
        class="search-input"
      >
        <el-button
          slot="append"
          icon="el-icon-search"
          @click="fetchOrders"
        ></el-button>
      </el-input>
    </div>

    <div v-if="ordersList.length === 0" class="empty-state">
      <el-empty description="No order data available"></el-empty>
    </div>

    <transition-group v-else name="order-list" tag="div" class="order-list">
      <div
        v-for="(orderInfo, index) in ordersList"
        :key="orderInfo.id"
        class="order-card"
        :class="getOrderCardClass(orderInfo)"
      >
        <div class="order-header">
          <div class="order-meta">
            <span class="order-number"
              >Order Status: {{
                orderInfo.tradeStatus === 1
                  ? "Pending Payment"
                  : orderInfo.tradeStatus === 2
                  ? "Paid"
                  : "Cancelled"
              }}</span
            >
            <span class="order-number"
              >Refund Status: {{
                orderInfo.refundStatus === 1
                  ? "No Refund"
                  : orderInfo.refundStatus === 2
                  ? "Refunding, Pending Merchant Review"
                  : orderInfo.refundStatus === 3
                  ? "Refund Rejected"
                  : "Refunded"
              }}</span
            >
            <span class="order-number">Order No: {{ orderInfo.code }}</span>
            <span class="order-time">Creation Time: {{ orderInfo.createTime }}</span>
          </div>
        </div>

        <div class="order-body">
          <div class="product-image">
            <img
              :src="orderInfo.cover"
              style="width: 80px;height: 80px;"
              :alt="orderInfo.productTitle"
            />
          </div>

          <div class="product-info">
            <h3 class="product-title">{{ orderInfo.productTitle }}</h3>
            <div class="product-detail">
              <span class="quantity">Qty x{{ orderInfo.buyNumber }}</span>
              <span class="price">
                <span class="symbol">¥</span>
                <span class="value">{{ totalPrice(orderInfo) }}</span>
              </span>
            </div>
            <p class="remark" v-if="orderInfo.detail">
              Remarks: {{ orderInfo.detail }}
            </p>
            <div class="timeline">
              <div v-if="orderInfo.tradeTime" class="timeline-item">
                <i class="el-icon-success"></i>
                <span>Payment Time: {{ orderInfo.tradeTime }}</span>
              </div>
              <div v-if="orderInfo.isDeliver" class="timeline-item">
                <i class="el-icon-refresh"></i>
                <span>Seller Shipping Time: {{ orderInfo.deliverTime }}</span>
              </div>
              <div v-if="orderInfo.isConfirm" class="timeline-item">
                <i class="el-icon-refresh"></i>
                <span>Buyer Receipt Confirmation Time: {{ orderInfo.isConfirmTime }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="order-footer">
          <div class="total-price">
            Actual Payment: <span class="price">¥{{ totalPrice(orderInfo) }}</span>
          </div>
          <div class="action-buttons">
            <el-button
              type="primary"
              v-if="orderInfo.tradeStatus === 1"
              @click="placeAnOrder(orderInfo)"
              >Place Order Now</el-button
            >
            <el-button
              type="primary"
              v-if="orderInfo.isDeliver && !orderInfo.isConfirm"
              @click="getGoods(orderInfo)"
              >Confirm Receipt Now</el-button
            >
            <el-button
              type="primary"
              v-if="
                (orderInfo.refundStatus === 1 ||
                  orderInfo.refundStatus === 3) &&
                  orderInfo.tradeStatus === 2
              "
              @click="toRefund(orderInfo)"
              >Apply for Refund</el-button
            >
            <el-button
              type="primary"
              v-if="orderInfo.tradeStatus === 1 && orderInfo.tradeStatus !== 3"
              @click="cannelOrder(orderInfo)"
              >Cancel Order</el-button
            >
            <el-button type="danger" @click="del(orderInfo)"
              >Delete Order</el-button
            >
          </div>
        </div>
      </div>
    </transition-group>

    <el-dialog
      title="Place Order"
      :visible.sync="dialogPay"
      width="400px"
      :close-on-click-modal="false"
    >
      <div class="product-info-area">
        <h3 class="product-title">{{ orderPay.productTitle }}</h3>
        <div class="product-meta">
          <span class="quantity">Qty x{{ orderPay.buyNumber }}, </span>
          <span class="price">
            <span class="symbol">Amount ¥</span>
            <span class="value">{{ totalPrice(orderPay) }}</span>
          </span>
        </div>
        <div class="product-remark" v-if="orderPay.detail">
          <span>Remarks: {{ orderPay.detail }}</span>
        </div>
      </div>

      <p>Please make payment</p>
      <div
        style="display: flex;align-items: center;justify-content: center;margin: 10px 0 ;"
      >
        <img style="width: 120px;height: 120px;" src="/code.png" alt="Payment QR Code" />
      </div>

      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="comfirmPayOrder"
          >I have completed payment</el-button
        >
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "BuyOrders",
  data() {
    return {
      ordersList: [],
      dialogPay: false,
      orderPay: {},
      ordersQueryDto: {},
      tradeStatusSelectedItem: {},
      tradeStatusList: [
        { tradeStatus: null, name: "All" },
        { tradeStatus: 2, name: "Paid" },
        { tradeStatus: 1, name: "Unpaid" },
        { tradeStatus: 3, name: "Cancelled" },
      ],
    };
  },
  created() {
    this.fetchOrders();
    this.tradeStatusSelected(this.tradeStatusList[0]);
  },
  methods: {
    getOrderCardClass(orderInfo) {
      if (orderInfo.isRefundConfirm) return "refunded";
      if (orderInfo.refundStatus) return "refunding";
      if (orderInfo.isConfirm) return "completed";
      if (orderInfo.tradeStatus) return "paid";
      return "unpaid";
    },

    async comfirmPayOrder() {
      try {
        await this.$confirm("Has this order been paid?", "Confirm Payment", {
          confirmButtonText: "Confirm",
          cancelButtonText: "Cancel",
          type: "warning",
        });

        const response = await this.$axios.post(
          `/product/placeAnOrder/${this.orderPay.id}`
        );
        if (response.data.code === 200) {
          this.$message.success("Payment Successful");
          this.fetchOrders();
          this.dialogPay = false;
          this.orderPay = {};
        }
      } catch (error) {
        if (error !== "cancel") {
          console.error("Order placement exception:", error);
        }
      }
    },

    getStatusTagType(orderInfo) {
      if (orderInfo.isRefundConfirm) return "danger";
      if (orderInfo.refundStatus) return "warning";
      if (orderInfo.isConfirm) return "success";
      if (orderInfo.tradeStatus) return "primary";
      return "info";
    },

    getStatusText(orderInfo) {
      if (orderInfo.isRefundConfirm) {
        return "Refunded";
      }
      if (orderInfo.refundStatus !== null && !orderInfo.refundStatus) {
        return "Refunding";
      }

      if (orderInfo.isConfirm) {
        return "Completed";
      }

      if (orderInfo.tradeStatus) {
        return "Pending Receipt";
      }

      return "Pending Payment";
    },

    tradeStatusSelected(tradeStatusItem) {
      this.tradeStatusSelectedItem = tradeStatusItem;
      this.ordersQueryDto.tradeStatus = tradeStatusItem.tradeStatus;
      this.fetchOrders();
    },

    handleFilterClear() {
      this.ordersQueryDto.code = "";
      this.fetchOrders();
    },
    async toRefund(orderInfo) {
      try {
        await this.$confirm("Confirm to apply for refund?", "Confirm Refund Application", {
          confirmButtonText: "Confirm",
          cancelButtonText: "Cancel",
          type: "warning",
        });
        const orders = {
          id: orderInfo.id,
          refundStatus: 2,
        };
        await this.$axios.put(`/orders/update`, orders);
        this.$message.success("Order refund application successful, please wait for merchant review");
        this.fetchOrders();
      } catch (error) {
        console.log("Order refund application exception");
      }
    },
    async cannelOrder(orderInfo) {
      try {
        await this.$confirm("Confirm to cancel order?", "Confirm Cancellation", {
          confirmButtonText: "Confirm",
          cancelButtonText: "Cancel",
          type: "warning",
        });
        const orders = {
          id: orderInfo.id,
          tradeStatus: 3,
        };
        await this.$axios.put(`/orders/update`, orders);
        this.$message.success("Order cancelled successfully");
        this.fetchOrders();
      } catch (error) {
        console.log("Order cancellation exception");
      }
    },
    async getGoods(orderInfo) {
      try {
        await this.$confirm("Have you received the goods?", "Confirm Receipt", {
          confirmButtonText: "Confirm",
          cancelButtonText: "Cancel",
          type: "warning",
        });

        const response = await this.$axios.post(
          `/product/getGoods/${orderInfo.id}`
        );
        if (response.data.code === 200) {
          this.$message.success(response.data.msg);
          this.fetchOrders();
        } else {
          this.$message.info(response.data.msg);
        }
      } catch (error) {
        if (error !== "cancel") {
          console.error("Receipt confirmation exception:", error);
        }
      }
    },

    async del(orderInfo) {
      try {
        await this.$confirm("Cannot be recovered after deletion, continue?", "Delete Order", {
          confirmButtonText: "Confirm",
          cancelButtonText: "Cancel",
          type: "warning",
        });

        const response = await this.$axios.post(`/orders/batchDelete`, [
          orderInfo.id,
        ]);
        if (response.data.code === 200) {
          this.$message.success("Deletion Successful");
          this.fetchOrders();
        }
      } catch (error) {
        if (error !== "cancel") {
          this.$message.error("Order deletion exception");
          console.error("Order deletion exception:", error);
        }
      }
    },

    totalPrice(orderInfo) {
      const totalPrice = orderInfo.buyNumber * orderInfo.buyPrice;
      return parseFloat(totalPrice).toFixed(2);
    },

    async refund(orderInfo) {
      try {
        await this.$confirm("Confirm to apply for refund?", "Apply for Refund", {
          confirmButtonText: "Confirm",
          cancelButtonText: "Cancel",
          type: "warning",
        });

        const response = await this.$axios.post(
          `/product/refund/${orderInfo.id}`
        );
        if (response.data.code === 200) {
          this.$message.success(response.data.msg);
          this.fetchOrders();
        } else {
          this.$message.info(response.data.msg);
        }
      } catch (error) {
        if (error !== "cancel") {
          console.error("Refund exception:", error);
        }
      }
    },

    async placeAnOrder(orderInfo) {
      this.dialogPay = true;
      this.orderPay = orderInfo;
    },

    coverParse(coverList) {
      if (!coverList.productCover) return "";
      return coverList.productCover.split(",")[0] || "";
    },

    fetchOrders() {
      this.$axios
        .post("/orders/queryUser", this.ordersQueryDto)
        .then((res) => {
          const { data } = res;
          if (data.code === 200) {
            this.ordersList = data.data.map((order) => ({
              ...order,
              cover: this.coverParse(order),
            }));
          }
        })
        .catch((error) => {
          console.error("Order query exception:", error);
        });
    },
  },
};
</script>

<style scoped lang="scss">
.price {
  margin-left: 10px;

  .value {
    font-size: 30px;
    font-weight: bold;
  }
}

.product-info-area {
  h3 {
    font-size: 30px;
  }

  .product-meta {
    .price {
      .value {
        font-size: 30px;
        font-weight: bold;
      }
    }
  }

  .product-remark {
    padding: 10px;
    box-sizing: border-box;
    background-color: rgb(240, 240, 240);
  }
}

.order-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;

  .filter-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    padding: 15px;
    background-color: #fff;
    border-radius: 8px;

    .status-tabs {
      display: flex;
      gap: 10px;

      .status-tab {
        cursor: pointer;
        border-radius: 20px;
        transition: all 0.3s;

        &:hover {
          transform: translateY(-2px);
        }
      }
    }

    .search-input {
      width: 300px;
    }
  }

  .empty-state {
    padding: 40px 0;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  }

  .order-list {
    display: flex;
    flex-direction: column;
    gap: 20px;

    .order-list-enter-active,
    .order-list-leave-active {
      transition: all 0.5s;
    }

    .order-list-enter,
    .order-list-leave-to {
      opacity: 0;
      transform: translateY(20px);
    }

    .order-list-move {
      transition: transform 0.5s;
    }
  }

  .order-card {
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    transition: all 0.3s;
    overflow: hidden;

    &:hover {
      cursor: pointer;
    }

    .order-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 15px 20px;
      background-color: #f9f9f9;
      border-bottom: 1px solid #eee;

      .order-meta {
        display: flex;
        flex-direction: column;
        gap: 5px;

        .order-number {
          font-size: 16px;
          font-weight: 600;
          color: #333;
        }

        .order-time {
          font-size: 12px;
          color: #999;
        }
      }
    }

    .order-body {
      display: flex;
      padding: 20px;
      gap: 20px;

      .product-image-container {
        width: 180px;
        height: 180px;
        border-radius: 8px;
        overflow: hidden;
        flex-shrink: 0;

        .product-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 0.5s;
        }

        &:hover .product-image {
          transform: scale(1.05);
        }
      }

      .product-info {
        flex: 1;

        .product-title {
          margin: 0 0 10px 0;
          font-size: 18px;
          color: #333;
        }

        .product-meta {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 10px;

          .quantity {
            font-size: 14px;
            color: #666;
          }

          .price {
            .symbol {
              font-size: 14px;
              color: #ff4f24;
            }

            .value {
              font-size: 20px;
              font-weight: bold;
              color: #ff4f24;
            }
          }
        }

        .product-remark {
          margin: 10px 0;
          font-size: 14px;
          color: #666;
          padding: 8px;
          background-color: #f5f7fa;
          border-radius: 4px;
        }

        .timeline {
          margin-top: 15px;
          padding-left: 20px;
          position: relative;

          &::before {
            content: "";
            position: absolute;
            left: 0;
            top: 0;
            bottom: 0;
            width: 2px;
            background-color: #e4e7ed;
          }

          .timeline-item {
            position: relative;
            padding-bottom: 15px;

            &:last-child {
              padding-bottom: 0;
            }

            i {
              position: absolute;
              left: -26px;
              top: 2px;
              font-size: 16px;
              color: #67c23a;
              background-color: #fff;
            }

            span {
              font-size: 13px;
              color: #666;
            }
          }
        }
      }
    }

    .order-footer {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding: 15px 20px;
      border-top: 1px solid #eee;
      background-color: #f9f9f9;

      .total-price {
        font-size: 16px;

        .price {
          font-weight: bold;
          color: #ff4f24;
        }
      }

      .action-buttons {
        display: flex;
        gap: 10px;

        .action-button {
          transition: all 0.3s;

          &:hover {
            transform: translateY(-2px);
          }
        }
      }
    }
  }
}
</style>