<template>
  <div class="orders-page">
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
        placeholder="Search Order Number"
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

    <div v-if="ordersList.length === 0" class="empty-container">
      <el-empty description="No orders matching the criteria were found"></el-empty>
    </div>

    <transition-group
      key="model-6"
      v-else
      name="order-list"
      tag="div"
      class="orders-container"
    >
      <div
        v-for="(orderInfo, index) in ordersList"
        :key="orderInfo.id"
        class="order-card"
        :class="{
          paid: orderInfo.tradeStatus,
          unpaid: !orderInfo.tradeStatus,
          refunded: orderInfo.refundStatus,
        }"
      >
        <div class="order-header">
          <div class="order-meta">
            <span class="order-number"
              >Order Status：{{
                orderInfo.tradeStatus === 1
                  ? "Pending Payment"
                  : orderInfo.tradeStatus === 2
                  ? "Paid"
                  : "Cancelled"
              }}</span
            >
            <span class="order-number"
              >Refund Status：{{
                orderInfo.refundStatus === 1
                  ? "No Refund"
                  : orderInfo.refundStatus === 2
                  ? "Buyer applied for refund, please review as soon as possible"
                  : orderInfo.refundStatus === 3
                  ? "Refund Rejected"
                  : "Refunded"
              }}</span
            >
            <span class="order-number">Order No.: {{ orderInfo.code }}</span>
            <span class="order-time">{{ orderInfo.createTime }}</span>
          </div>

          <div class="order-status">
          </div>
        </div>

        <div class="order-body">
          <div class="product-image">
            <img :src="orderInfo.cover" :alt="orderInfo.productTitle" />
          </div>

          <div class="product-info">
            <h3 class="product-title">{{ orderInfo.productTitle }}</h3>
            <div class="product-detail">
              <span class="quantity">x{{ orderInfo.buyNumber }}</span>
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
                <span>Buyer Receipt Time: {{ orderInfo.isConfirmTime }}</span>
              </div>
            </div>
          </div>
        </div>

        <div class="order-footer">
          <div class="total-price">
            Actual Payment: <span class="price">¥{{ totalPrice(orderInfo) }}</span>
          </div>

          <div class="actions">
            <el-button
              type="primary"
              v-if="orderInfo.refundStatus === 2"
              @click="okRefund(orderInfo)"
              >Approve Refund</el-button
            >
            <el-button
              type="primary"
              v-if="orderInfo.refundStatus === 2"
              @click="rejectRefund(orderInfo)"
              >Reject Refund</el-button
            >
            <el-button
              type="success"
              v-if="!orderInfo.isDeliver && orderInfo.tradeStatus === 2"
              @click="openDeliver(orderInfo)"
              >Go to Ship</el-button
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
      title="Ship Products"
      :visible.sync="dialogDeliverGoods"
      width="600px"
      :close-on-click-modal="false"
    >
      <div v-if="!deliverAdrList.length">
        <el-empty
          description="You have no shipping addresses under your account, please add one first"
        ></el-empty>
        <div style="display: flex;justify-content: center;align-items: center;">
          <el-button
            style="margin-bottom: 30px;"
            type="primary"
            @click="toAddAdr"
            >Add Address First</el-button
          >
        </div>
      </div>

      <div>
        <div v-for="adr in deliverAdrList" :key="adr.id">
          <div
            :style="{
              border:
                adr.id === address.id ? '1px solid rgb(99, 101, 255)' : '',
            }"
            class="adr-item"
            @click="selectedAdr(adr)"
          >
            <div>{{ adr.concatPerson }}</div>
            <div>{{ adr.concatPhone }}</div>
            <div>{{ adr.getAdr }}</div>
          </div>
        </div>
      </div>

      <span slot="footer" class="dialog-footer">
        <div>
          <el-button type="success" @click="goDeliver">Ship Now</el-button>
          <el-button type="danger" @click="cannelDeliver">Cancel</el-button>
        </div>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "BuyOrders",
  data() {
    return {
      address: {},
      ordersList: [],
      ordersQueryDto: {},
      orderInfo: {},
      dialogDeliverGoods: false,
      tradeStatusSelectedItem: {},
      tradeStatusList: [
        { tradeStatus: null, name: "All" },
        { tradeStatus: 2, name: "Paid" },
        { tradeStatus: 1, name: "Unpaid" },
      ],
      deliverAdrList: [],
    };
  },
  created() {
    this.fetchOrders();
    this.tradeStatusSelected(this.tradeStatusList[0]);
  },
  methods: {
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
          this.$message.error("Exception in order deletion");
          console.error("Exception in order deletion:", error);
        }
      }
    },
    async cannelOrder(orderInfo) {
      try {
        await this.$confirm("Are you sure to cancel the order?", "Confirm Cancellation", {
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
        console.log("Exception in canceling order");
      }
    },
    async okRefund(orderInfo) {
      try {
        await this.$confirm("Are you sure to approve the buyer's refund application?", "Confirm Refund", {
          confirmButtonText: "Confirm",
          cancelButtonText: "Cancel",
          type: "warning",
        });
        const orders = {
          id: orderInfo.id,
          refundStatus: 4,
          isRefundComfirm: true,
        };
        await this.$axios.put(`/orders/update`, orders);
        this.$message.success("Order refunded successfully");
        this.fetchOrders();
      } catch (error) {
        console.log("Exception in order refund");
      }
    },
    async rejectRefund(orderInfo) {
      try {
        await this.$confirm("Reject the buyer's refund application?", "Confirm Rejection", {
          confirmButtonText: "Confirm",
          cancelButtonText: "Cancel",
          type: "warning",
        });
        const orders = {
          id: orderInfo.id,
          refundStatus: 3, 
          isRefundComfirm: true,
        };
        await this.$axios.put(`/orders/update`, orders);
        this.$message.success("Order refund rejected successfully");
        this.fetchOrders();
      } catch (error) {
        console.log("Exception in rejecting order refund");
      }
    },
    cannelDeliver() {
      this.dialogDeliverGoods = false;
    },
    async goDeliver() {
      try {
        const ordersDeliverDto = {
          ordersId: this.orderInfo.id,
          deliverAdrId: this.address.id,
        };
        const { data } = await this.$axios.post(
          `/product/deliverGoods`,
          ordersDeliverDto
        );
        this.$message.success(data.msg);
        this.dialogDeliverGoods = false;
        this.fetchOrders();
      } catch (error) {
        console.log("Shipping exception");
        this.$message.error("Shipping exception, exception in querying shipping address");
      }
    },
    selectedAdr(adr) {
      this.address = adr;
    },
    toAddAdr() {
      this.$router.push("/address");
    },
    async openDeliver(orderInfo) {
      try {
        const { data } = await this.$axios.post(`/address/queryUser`, {});
        this.deliverAdrList = data.data;
        if (this.deliverAdrList.length > 0) {
          const defaultAdr = this.deliverAdrList.find((adr) => adr.isDefault);
          if (defaultAdr === undefined) {
            this.address = defaultAdr;
            return;
          }
          this.address = this.deliverAdrList[0];
        }
        this.orderInfo = orderInfo;
        this.dialogDeliverGoods = true;
      } catch (error) {
        console.log("Shipping exception");
        this.$message.error("Shipping exception, exception in querying shipping address");
      }
    },
    async deliver(orderInfo) {
      try {
        const orders = {
          id: orderInfo.id,
        };
        const response = await this.$axios.put(`/orders/update`);
        if (response.data.code === 200) {
          this.$message.success("Deletion Successful");
          this.fetchOrders();
        }
      } catch (error) {
        console.log("Shipping exception");
      }
    },
    getOrderStatusText(order) {
      if (order.refundStatus && order.refundTime !== null) return "Refunded";
      if (!order.refundStatus && order.refundTime === null) return "Refunding";
      return order.tradeStatus ? "Paid" : "Unpaid";
    },

    tradeStatusSelected(tradeStatusItem) {
      console.log(tradeStatusItem);

      this.tradeStatusSelectedItem = tradeStatusItem;
      this.ordersQueryDto.tradeStatus = tradeStatusItem.tradeStatus;
      this.fetchOrders();
    },

    handleFilterClear() {
      this.ordersQueryDto.code = "";
      this.fetchOrders();
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
          this.$message.error("Exception in order deletion");
          console.error("Exception in order deletion:", error);
        }
      }
    },

    totalPrice(orderInfo) {
      const totalPrice = orderInfo.buyNumber * orderInfo.buyPrice;
      return parseFloat(totalPrice).toFixed(2);
    },

    async returnMoney(orders) {
      try {
        await this.$confirm("Are you sure to refund?", "Confirm Refund", {
          confirmButtonText: "Confirm",
          cancelButtonText: "Cancel",
          type: "warning",
        });

        const response = await this.$axios.post(
          `/orders/returnMoney/${orders.id}`
        );
        if (response.data.code === 200) {
          this.$message.success("Refund Successful");
          this.fetchOrders();
        }
      } catch (error) {
        if (error !== "cancel") {
          this.$message.error("Exception in refund");
          console.error("Exception in refund:", error);
        }
      }
    },

    coverParse(coverList) {
      if (!coverList.productCover) return "";
      return coverList.productCover.split(",")[0] || "";
    },

    fetchOrders() {
      this.$axios
        .post("/orders/queryOrdersList", this.ordersQueryDto)
        .then((res) => {
          const { data } = res;
          if (data.code === 200) {
            this.ordersList = data.data.map((order) => ({
              id: order.id,
              code: order.code,
              productTitle: order.productTitle,
              detail: order.detail,
              buyPrice: order.buyPrice,
              buyNumber: order.buyNumber,
              tradeStatus: order.tradeStatus,
              tradeTime: order.tradeTime,
              refundStatus: order.refundStatus,
              refundTime: order.refundTime,
              isRefundConfirm: order.isRefundConfirm,
              createTime: order.createTime,
              cover: this.coverParse(order),
              isDeliver: order.isDeliver,
              deliverTime: order.deliverTime,
            }));
          }
        })
        .catch((error) => {
          console.error("Exception in order query:", error);
        });
    },
  },
};
</script>

<style scoped lang="scss">
.orders-page {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;

  .filter-section {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;
    background-color: #fff;
    padding: 15px;
    border-radius: 8px;

    .status-tabs {
      display: flex;
      gap: 10px;

      .status-tab {
        cursor: pointer;
        transition: all 0.3s;
        border-radius: 20px;

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        }
      }
    }

    .search-input {
      width: 300px;
    }
  }

  .empty-container {
    background-color: #fff;
    border-radius: 8px;
    padding: 40px 0;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  }

  .orders-container {
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

      .product-image {
        width: 120px;
        height: 120px;
        border-radius: 4px;
        overflow: hidden;
        flex-shrink: 0;

        img {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 0.5s;
        }

        &:hover img {
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

        .product-detail {
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

        .remark {
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

      .actions {
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

.adr-item {
  display: flex;
  gap: 10px;
  font-size: 16px;
  padding: 10px 20px;

  &:hover {
    background-color: rgb(240, 240, 240);
  }
}
</style>