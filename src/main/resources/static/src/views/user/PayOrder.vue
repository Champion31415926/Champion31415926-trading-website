<template>
  <div class="order-container">
    <el-card class="product-card" shadow="never">
      <div class="product-info">
        <div class="product-header">
          <img
            :src="product.coverList.split(',')[0]"
            class="product-image"
            alt="Product Image"
          />
          <div class="product-details">
            <h3 class="product-title">{{ product.name }}</h3>
            <div class="price-section">
              <span class="price">¥{{ product.price }}</span>
              <el-tag
                size="mini"
                :type="product.isBargain ? 'warning' : 'info'"
              >
                {{ product.isBargain ? "Bargain Available" : "No Bargain" }}
              </el-tag>
              <el-tag size="mini" type="info"
                >{{ product.oldLevel }}/10 Condition</el-tag
              >
            </div>
            <div class="product-meta">
              <span><i class="el-icon-user"></i> {{ product.userName }}</span>
              <span
                ><i class="el-icon-folder-opened"></i>
                {{ product.categoryName }}</span
              >
              <span
                ><i class="el-icon-box"></i> Stock
                {{ product.inventory }} units</span
              >
            </div>
          </div>
        </div>

        <div class="order-params">
          <el-form label-width="100px">
            <el-form-item label="Purchase Quantity" required>
              <el-input-number
                v-model="buyNumber"
                :min="1"
                :max="product.inventory"
                controls-position="right"
                size="medium"
                @change="handleQuantityChange"
              ></el-input-number>
              <span class="inventory-tip">{{ product.inventory }} units remaining</span>
            </el-form-item>
            <el-form-item label="Total Product Price">
              <span class="total-price"
                >¥{{ (product.price * buyNumber).toFixed(2) }}</span
              >
            </el-form-item>
            <el-form-item label="Remarks">
              <el-input
                type="textarea"
                :rows="3"
                placeholder="Please enter remarks (optional)"
                v-model="detail"
                resize="none"
                maxlength="100"
                show-word-limit
              ></el-input>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </el-card>

    <el-card class="address-card" shadow="never">
      <div class="card-header">
        <h3>Select Shipping Address</h3>
        <el-button type="text" @click="showAddDialog" icon="el-icon-plus"
          >Add Address</el-button
        >
      </div>

      <div class="address-list" v-if="addressList.length > 0">
        <el-radio-group v-model="selectedAddressId" class="address-radio-group">
          <div
            class="address-item"
            v-for="item in addressList"
            :key="item.id"
            :class="{ active: selectedAddressId === item.id }"
          >
            <el-radio :label="item.id" class="address-radio"></el-radio>
            <div class="address-content">
              <div class="address-info">
                <div class="receiver">{{ item.concatPerson }}</div>
                <div class="phone">{{ item.concatPhone }}</div>
                <el-tag v-if="item.isDefault" size="mini" type="success"
                  >Default</el-tag
                >
              </div>
              <div class="address-detail">{{ item.getAdr }}</div>
            </div>
            <div class="address-actions">
              <el-button size="mini" @click="handleEdit(item)">Edit</el-button>
              <el-button
                size="mini"
                type="text"
                @click="setDefault(item.id)"
                :disabled="item.isDefault"
              >
                Set as Default
              </el-button>
            </div>
          </div>
        </el-radio-group>
      </div>
      <div class="empty-address" v-else>
        <el-empty description="No shipping addresses available">
          <el-button type="primary" size="small" @click="showAddDialog"
            >Add Shipping Address</el-button
          >
        </el-empty>
      </div>
    </el-card>

    <div class="order-footer">
      <el-button
        type="info"
        size="medium"
        @click="goBack"
        icon="el-icon-arrow-left"
      >
        Back to Product Page
      </el-button>
      <div class="order-summary">
        <span class="total"
          >Total：<span class="price"
            >¥{{ (product.price * buyNumber).toFixed(2) }}</span
          ></span
        >
        <span class="quantity">Total {{ buyNumber }} units</span>
      </div>
      <el-button
        type="primary"
        size="medium"
        @click="submitOrder"
        :disabled="!selectedAddressId || submitting"
        :loading="submitting"
        icon="el-icon-sell"
      >
        Submit Order
      </el-button>
    </div>

    <el-dialog
      :title="dialogTitle"
      :visible.sync="dialogVisible"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form
        :model="addressForm"
        :rules="rules"
        ref="addressForm"
        label-width="100px"
        @submit.native.prevent
      >
        <el-form-item label="Recipient" prop="concatPerson">
          <el-input
            v-model="addressForm.concatPerson"
            placeholder="Please enter recipient's name"
            maxlength="20"
            show-word-limit
          ></el-input>
        </el-form-item>
        <el-form-item label="Contact Phone" prop="concatPhone">
          <el-input
            v-model="addressForm.concatPhone"
            placeholder="Please enter recipient's mobile phone number"
          ></el-input>
        </el-form-item>
        <el-form-item label="Shipping Address" prop="getAdr">
          <el-input
            type="textarea"
            v-model="addressForm.getAdr"
            placeholder="Please enter detailed shipping address"
            :rows="3"
            maxlength="100"
            show-word-limit
          ></el-input>
        </el-form-item>
        <el-form-item label="Default Address">
          <el-switch v-model="addressForm.isDefault"></el-switch>
          <span class="tip">After setting as default address, it will be prioritized for orders</span>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button
          type="primary"
          @click="submitAddressForm"
          :loading="addressSubmitting"
        >
          Confirm
        </el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import { getUserInfo } from "@/utils/storage";
export default {
  data() {
    return {
      product: {
        id: null,
        name: "",
        price: 0,
        inventory: 0,
        coverList: "",
        isBargain: false,
        oldLevel: "",
        userName: "",
        categoryName: "",
      },
      buyNumber: 1,
      detail: "",
      selectedAddressId: null,
      addressList: [],
      submitting: false,
      addressSubmitting: false,

      dialogVisible: false,
      dialogTitle: "Add Address",
      addressForm: {
        id: null,
        concatPerson: "",
        concatPhone: "",
        getAdr: "",
        isDefault: false,
      },
      rules: {
        concatPerson: [
          { required: true, message: "Please enter recipient's name", trigger: "blur" },
          { min: 2, max: 20, message: "Length must be 2 to 20 characters", trigger: "blur" },
        ],
        concatPhone: [
          { required: true, message: "Please enter contact phone number", trigger: "blur" },
          {
            pattern: /^1[3-9]\d{9}$/,
            message: "Please enter a valid mobile phone number",
            trigger: "blur",
          },
        ],
        getAdr: [
          { required: true, message: "Please enter shipping address", trigger: "blur" },
          { min: 5, max: 100, message: "Length must be 5 to 100 characters", trigger: "blur" },
        ],
      },
    };
  },
  created() {
    this.initData();
  },
  methods: {
    async initData() {
      await this.fetchProduct();
      await this.fetchAddressList();
    },

    async fetchProduct() {
      const productId = this.$route.query.productId;
      if (!productId) {
        this.$message.error("Failed to get product information");
        this.$router.go(-1);
        return;
      }

      try {
        const res = await this.$axios.post("/product/query", { id: productId });
        if (res.data.code === 200 && res.data.data.length > 0) {
          this.product = res.data.data[0];
          this.buyNumber = Math.min(1, this.product.inventory);
        } else {
          this.$message.error("Failed to get product information");
          this.$router.go(-1);
        }
      } catch (error) {
        this.$message.error("Failed to get product information");
        console.error("Failed to get product information:", error);
        this.$router.go(-1);
      }
    },

    async fetchAddressList() {
      try {
        const userInfo = getUserInfo();
        if (userInfo === null) {
          this.$notify({
            duration: 1000,
            title: "Not Logged In",
            message: "You need to log in to place orders",
            type: "info",
          });
          this.$router.push("/login");
          return;
        }

        const res = await this.$axios.post("/address/queryUser", {
          current: 1,
          size: 100,
        });

        if (res.data.code === 200) {
          this.addressList = res.data.data || [];
          const defaultAddress = this.addressList.find(
            (item) => item.isDefault
          );
          if (defaultAddress) {
            this.selectedAddressId = defaultAddress.id;
          } else if (this.addressList.length > 0) {
            this.selectedAddressId = this.addressList[0].id;
          }
        }
      } catch (error) {
        console.error("Failed to get address list:", error);
        this.$message.error("Failed to get address list");
      }
    },

    handleQuantityChange(value) {
      if (value > this.product.inventory) {
        this.buyNumber = this.product.inventory;
        this.$message.warning(`Purchase quantity cannot exceed inventory`);
      }
    },

    async submitOrder() {
      if (!this.selectedAddressId) {
        this.$message.error("Please select a shipping address");
        return;
      }

      if (this.buyNumber <= 0) {
        this.$message.error("Purchase quantity must be greater than 0");
        return;
      }

      if (this.buyNumber > this.product.inventory) {
        this.$message.error("Purchase quantity cannot exceed inventory");
        return;
      }

      this.submitting = true;

      try {
        const orderData = {
          productId: this.product.id,
          buyNumber: this.buyNumber,
          detail: this.detail,
          addressId: this.selectedAddressId,
        };

        const res = await this.$axios.post("/orders/save", orderData);

        if (res.data.code === 200) {
          this.$message.success("Order placed successfully");
          this.$router.push({
            path: "/orders",
            query: { orderId: res.data.data },
          });
        } else {
          this.$message.error(res.data.msg || "Failed to place order");
        }
      } catch (error) {
        console.error("Failed to place order:", error);
        this.$message.error("Failed to place order, please try again later");
      } finally {
        this.submitting = false;
      }
    },

    goBack() {
      this.$router.go(-1);
    },

    showAddDialog() {
      this.dialogTitle = "Add Address";
      this.addressForm = {
        id: null,
        concatPerson: "",
        concatPhone: "",
        getAdr: "",
        isDefault: false,
      };
      this.dialogVisible = true;

      this.$nextTick(() => {
        if (this.$refs.addressForm) {
          this.$refs.addressForm.clearValidate();
        }
      });
    },

    handleEdit(row) {
      this.dialogTitle = "Edit Address";
      this.addressForm = JSON.parse(JSON.stringify(row));
      this.dialogVisible = true;

      this.$nextTick(() => {
        if (this.$refs.addressForm) {
          this.$refs.addressForm.clearValidate();
        }
      });
    },

    async submitAddressForm() {
      this.$refs.addressForm.validate(async (valid) => {
        if (!valid) return;

        this.addressSubmitting = true;

        try {
          const formData = {
            ...this.addressForm,
          };

          const api = formData.id ? "/address/update" : "/address/save";
          const res = await this.$axios.post(api, formData);

          if (res.data.code === 200) {
            this.$message.success(
              formData.id ? "Address updated successfully" : "Address added successfully"
            );
            this.dialogVisible = false;
            await this.fetchAddressList();
          } else {
            this.$message.error(res.data.msg || "Operation failed");
          }
        } catch (error) {
          console.error("Address operation failed:", error);
          this.$message.error("Operation failed, please try again later");
        } finally {
          this.addressSubmitting = false;
        }
      });
    },

    async setDefault(id) {
      try {
        await this.$confirm("Confirm set as default address?", "Reminder", {
          confirmButtonText: "Confirm",
          cancelButtonText: "Cancel",
          type: "warning",
        });

        const res = await this.$axios.put("/address/isDefault", {
          id: id,
          isDefault: true,
        });

        if (res.data.code === 200) {
          this.$message.success("Setting successful");
          await this.fetchAddressList();
        } else {
          this.$message.info(res.data.msg);
        }
      } catch (error) {
      }
    },
  },
};
</script>

<style scoped lang="scss">
.order-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.product-card {
  margin-bottom: 20px;
  border-radius: 8px;

  .product-info {
    padding: 20px;
  }

  .product-header {
    display: flex;
    margin-bottom: 20px;
  }

  .product-image {
    width: 120px;
    height: 120px;
    border-radius: 4px;
    margin-right: 20px;
    object-fit: cover;
  }

  .product-details {
    flex: 1;

    .product-title {
      margin: 0 0 10px 0;
      font-size: 18px;
      color: #333;
    }

    .price-section {
      margin-bottom: 10px;

      .price {
        font-size: 24px;
        color: #ff5000;
        margin-right: 10px;
        font-weight: bold;
      }

      .el-tag {
        margin-right: 8px;
      }
    }

    .product-meta {
      color: #666;
      font-size: 14px;

      span {
        margin-right: 15px;

        i {
          margin-right: 5px;
        }
      }
    }
  }

  .order-params {
    margin-top: 20px;

    .el-form-item {
      margin-bottom: 15px;
    }

    .inventory-tip {
      margin-left: 10px;
      color: #999;
      font-size: 12px;
    }

    .total-price {
      font-size: 18px;
      color: #ff5000;
      font-weight: bold;
    }
  }
}

.address-card {
  margin-bottom: 20px;
  border-radius: 8px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 15px 20px;
    border-bottom: 1px solid #f0f0f0;

    h3 {
      margin: 0;
      font-size: 16px;
      color: #333;
    }
  }

  .address-list {
    padding: 10px;
  }

  .address-item {
    display: flex;
    align-items: center;
    padding: 15px;
    margin-bottom: 10px;
    border: 1px solid #e6e6e6;
    border-radius: 4px;
    transition: all 0.3s;
    cursor: pointer;

    &:hover {
      border-color: #409eff;
    }

    &.active {
      border-color: #409eff;
      background-color: #f5f9ff;
    }

    .address-radio {
      margin-right: 15px;
    }

    .address-content {
      flex: 1;

      .address-info {
        margin-bottom: 5px;
        display: flex;
        align-items: center;
        justify-content: left;

        .receiver {
          font-weight: bold;
          margin-right: 10px;
          font-size: 14px;
        }

        .phone {
          margin-right: 10px;
          color: #666;
          font-size: 14px;
        }
      }

      .address-detail {
        color: #666;
        font-size: 14px;
      }
    }

    .address-actions {
      margin-left: 15px;
    }
  }

  .empty-address {
    padding: 40px 0;
  }
}

.order-footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: #fff;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.1);
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  z-index: 100;

  .order-summary {
    text-align: right;

    .total {
      font-size: 16px;
      margin-right: 15px;

      .price {
        font-size: 20px;
        color: #ff5000;
        font-weight: bold;
      }
    }

    .quantity {
      color: #666;
      font-size: 14px;
    }
  }
}

.dialog-footer {
  text-align: right;
}

.tip {
  margin-left: 10px;
  color: #999;
  font-size: 12px;
}
</style>