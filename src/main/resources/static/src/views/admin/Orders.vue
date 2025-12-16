<template>
    <div class="card-container">
      <el-card class="filter-card" shadow="none">
        <div class="filter-content">
          <div class="filter-tabs">
            <el-radio-group v-model="ordersQueryDto.tradeStatus" @change="fetchFreshData" size="small">
              <el-radio-button :label="null">all</el-radio-button>
              <el-radio-button :label="1">Unpaid</el-radio-button>
              <el-radio-button :label="2">Paid</el-radio-button>
              <el-radio-button :label="3">Cancelled</el-radio-button>
            </el-radio-group>

            <el-radio-group v-model="ordersQueryDto.refundStatus" @change="fetchFreshData" size="small" style="margin-left: 10px;">
              <el-radio-button :label="null">All</el-radio-button>
              <el-radio-button :label="1">NoRefund</el-radio-button>
              <el-radio-button :label="2">ApplyRefund</el-radio-button>
              <el-radio-button :label="3">RejectRefund</el-radio-button>
              <el-radio-button :label="4">Refund</el-radio-button>
            </el-radio-group>
          </div>
          
          <div class="filter-controls">
            <el-date-picker class="filter-item date-picker" 
                            @change="fetchFreshData" 
                            v-model="searchTime" 
                            type="daterange" 
                            range-separator="to" 
                            start-placeholder="Order Start"
                            end-placeholder="Order End"
                            size="medium">
            </el-date-picker>
            
            <div class="search-group">
              <el-input 
                v-model="ordersQueryDto.code" 
                placeholder="Order No." 
                clearable
                size="medium"
                class="search-input"
                @clear="handleFilterClear"
                @keyup.enter="handleFilter"
              >
                <template #suffix>
                  <i class="el-icon-search search-icon" @click="handleFilter"></i>
                </template>
              </el-input>
            </div>
          </div>
        </div>
      </el-card>
  
      <el-card class="order-card" shadow="none">
        <div v-for="order in tableData" :key="order.id" class="order-item">
          <div class="order-header">
            <div class="order-code">Order No.: {{ order.code }}</div>
            <div class="order-time">{{ order.createTime }}</div>
          </div>
          
          <div class="order-content">
            <div class="order-user">
              <el-avatar :size="40" :src="order.userAvatar"></el-avatar>
              <div class="user-name">{{ order.userName }}</div>
            </div>
            
            <div class="order-product">
              <el-image v-if="order.productCover" 
                        :src="order.productCover.split(',')[0]" 
                        :preview-src-list="order.productCover.split(',')"
                        fit="cover"
                        class="product-image">
              </el-image>
              <div class="product-info">
                <div class="product-title">{{ order.productTitle }}</div>
                <div class="product-meta">
                  <span class="price">Unit Price: {{ order.buyPrice }} CNY</span>
                  <span class="quantity">Quantity: {{ order.buyNumber }}</span>
                </div>
                <div class="product-remark" v-if="order.detail">Remark: {{ order.detail }}</div>
              </div>
            </div>
            
            <div class="order-status">
              <el-tag
                :type="order.tradeStatus === 2 ? 'success' : order.tradeStatus === 1 ? 'warning' : 'info'"
                size="small"
              >
                {{
                  order.tradeStatus === 2
                    ? 'Paid'
                    : order.tradeStatus === 1
                    ? 'Unpaid'
                    : 'Cancelled'
                }}
              </el-tag>

              <el-tag
                :type="order.refundStatus === 4 ? 'danger' : order.refundStatus === 2 ? 'warning' : 'info'"
                size="small"
                style="margin-top: 5px;"
              >
                {{
                  order.refundStatus === 4
                    ? 'Refund'
                    : order.refundStatus === 2
                    ? 'ApplyRefund'
                    : order.refundStatus === 3
                    ? 'RejectRefund'
                    : 'NoRefund'
                }}
              </el-tag>
            </div>
            
            <div class="order-actions">
              <el-button class="detail-btn" size="mini" @click="handleEdit(order)">Details</el-button>
              <el-button class="delete-btn" size="mini" @click="handleDelete(order)">Delete</el-button>
            </div>
          </div>
        </div>
        
        <div class="pagination-area">
          <el-pagination 
            @current-change="handleCurrentChange"
            :current-page="currentPage" 
            :page-size="10"  
            layout="prev, pager, next"  
            :total="totalItems" 
            class="category-pagination"  
            prev-text="<"
            next-text=">"
          />
        </div>
      </el-card>
  
      <el-drawer title="Order Details" 
                 :visible.sync="drawerProductOperaion" 
                 :direction="direction"
                 size="40%"
                 :wrapper-closable="false"
                 class="order-detail-drawer">
        <div class="drawer-content">
          <el-card shadow="none" class="detail-card">
            <div slot="header" class="card-header">
              <span>Product Information</span>
            </div>
            <div class="product-images">
              <el-image v-for="(cover, index) in coverList" 
                        :key="index" 
                        :src="cover" 
                        :preview-src-list="coverList"
                        fit="cover"
                        class="detail-image">
              </el-image>
            </div>
          </el-card>
          
          <el-card shadow="none" class="detail-card status-card">
            <div slot="header" class="card-header">
              <span>Order Status</span>
            </div>
            <div class="status-item" v-for="(item, index) in statusItems" :key="index">
              <div class="status-label">{{ item.label }}:</div>
              <div class="status-value">
                <i :class="item.icon" :style="{color: item.color}"></i>
                <span>{{ item.text }}</span>
                <span v-if="item.time" class="status-time">{{ item.time }}</span>
              </div>
            </div>
          </el-card>
        </div>
      </el-drawer>
    </div>
  </template>
  
  <script>
  export default {
    data() {
      return {
        data: {},
        currentPage: 1,
        coverList: [],
        searchTime: [],
        totalItems: 0,
        drawerProductOperaion: false,
        tableData: [],
        delectedRows: [],
        ordersQueryDto: {},
        direction: 'rtl',
        tradeSelectedItem: {},
        refundSelectedItem: {},
        refundStatus: [
          { refundStatus: null, name: 'All' }, 
          { refundStatus: true, name: 'Refunded' }, 
          { refundStatus: false, name: 'Not Refunded' }
        ],
        tradeStatus: [
          { tradeStatus: null, name: 'All' }, 
          { tradeStatus: true, name: 'Ordered' }, 
          { tradeStatus: false, name: 'Not Ordered' }
        ]
      };
    },
    created() {
      this.fetchFreshData();
      this.tradeSelected(this.tradeStatus[0]);
    },
    computed: {
      statusItems() {
        return [
          {
            label: 'Payment Status',
            icon: this.data.tradeStatus ? 'el-icon-success' : 'el-icon-warning',
            color: this.data.tradeStatus ? '#10b981' : '#f59e0b',
            text: this.data.tradeStatus ? 'Paid' : 'Unpaid',
            time: this.data.tradeStatus ? `Payment Time: ${this.data.tradeTime}` : ''
          },
          {
            label: 'Refund Status',
            icon: this.data.refundStatus ? 'el-icon-success' : 'el-icon-warning',
            color: this.data.refundStatus ? '#ff6b6b' : '#4967ff',
            text: this.data.refundStatus ? 'Refund Applied' : 'No Refund Applied',
            time: this.data.refundStatus ? `Refund Time: ${this.data.refundTime}` : ''
          },
          {
            label: 'Refund Review',
            icon: this.data.isRefundConfirm ? 'el-icon-success' : 'el-icon-warning',
            color: this.data.isRefundConfirm ? '#10b981' : '#f59e0b',
            text: this.data.isRefundConfirm ? 'Reviewed' : 'Not Reviewed',
            time: ''
          }
        ];
      }
    },
    methods: {
      tradeSelected(trade) {
        this.tradeSelectedItem = trade;
        this.ordersQueryDto.tradeStatus = trade.tradeStatus;
        this.fetchFreshData();
      },
      refundSelected(refund) {
        this.refundSelectedItem = refund;
        this.ordersQueryDto.refundStatus = refund.refundStatus;
        this.fetchFreshData(); 
      },
      cannel() {
        this.data = {};
        this.drawerProductOperaion = false;
      },
      async batchDelete() {
        if (!this.delectedRows.length) {
          this.$message.warning('No data selected');
          return;
        }
        const confirmed = await this.$confirm(
          'Cannot be recovered after deletion, continue?',
          'Delete Order Data',
          {
            confirmButtonText: 'Confirm',
            cancelButtonText: 'Cancel',
            type: 'warning',
            confirmButtonColor: '#ff6b6b',
            cancelButtonColor: '#94A3B8'
          }
        );
        if (confirmed) {
          try {
            let ids = this.delectedRows.map(entity => entity.id);
            const response = await this.$axios.post(`/orders/batchDelete`, ids);
            if (response.data.code === 200) {
              this.$message.success('Deleted successfully');
              this.fetchFreshData();
              this.delectedRows = [];
              return;
            }
          } catch (error) {
            this.$message.error("Order information deletion exception");
            console.error(`Order information deletion exception：`, error);
          }
        }
      },
      async fetchFreshData() {
        let startTime = null;
        let endTime = null;
        if (this.searchTime != null && this.searchTime.length === 2) {
          const [startDate, endDate] = await Promise.all(this.searchTime.map(date => date.toISOString()));
          startTime = `${startDate.split('T')[0]}T00:00:00`;
          endTime = `${endDate.split('T')[0]}T23:59:59`;
        }
        this.ordersQueryDto.current = this.currentPage;
        this.ordersQueryDto.size = 10;
        this.ordersQueryDto.startTime = startTime;
        this.ordersQueryDto.endTime = endTime;
        this.$axios.post('/orders/query', this.ordersQueryDto).then(res => {
          const { data } = res;
          if (data.code === 200) {
            this.tableData = data.data;
            this.totalItems = data.total;
          }
        }).catch(error => {
          this.$message.error('Failed to query order data');
          console.error('Order query exception：', error);
        })
      },
      handleFilter() {
        this.currentPage = 1;
        this.fetchFreshData();
      },
      handleFilterClear() {
        this.ordersQueryDto.code = '';
        this.handleFilter();
      },
      handleCurrentChange(val) {
        this.currentPage = val;
        this.fetchFreshData();
      },
      handleEdit(row) {
        this.data = row;
        this.coverList = row.productCover ? row.productCover.split(',') : [];
        this.drawerProductOperaion = true;
      },
      handleDelete(row) {
        this.delectedRows = [row];
        this.batchDelete();
      }
    },
  };
  </script>
  
  <style scoped lang="scss">
  $primary-color: #4967ff;
  $light-primary: #9EAEFF;
  $success-color: #10b981;
  $warning-color: #f59e0b;
  $danger-color: #ff6b6b;
  $danger-light-color: rgba(255, 107, 107, 0.1);
  $danger-disabled-color: rgba(255, 107, 107, 0.05);
  $info-color: #4967ff;
  $border-color: rgba(158, 174, 255, 0.25);
  $text-main: #1E293B;
  $text-light: #64748B;
  $text-lighter: #94A3B8;
  $bg-light: #F8FAFC;
  $shadow-light: 0 4px 16px rgba(0, 0, 0, 0.03);

  .card-container {
    padding: 24px;
    background-color: $bg-light;
    min-height: calc(100vh - 80px);
    box-sizing: border-box;
  }
  
  .filter-card {
    margin-bottom: 24px;
    border-radius: 16px;
    border: 1px solid $border-color;
    background: white;
    box-shadow: $shadow-light;
    padding: 20px;
    
    .filter-content {
      display: flex;
      flex-direction: column;
      gap: 20px;
    }
    
    .filter-tabs {
      display: flex;
      flex-wrap: wrap;
      gap: 15px;
      
      ::v-deep .el-radio-button__inner {
        border-radius: 8px !important;
        padding: 4px 16px;
        transition: all 0.2s;
        
        &:hover {
          color: $primary-color;
        }
        
        &.is-active {
          background-color: $primary-color;
          border-color: $primary-color;
        }
      }
    }
    
    .filter-controls {
      display: flex;
      gap: 15px;
      align-items: center;
      
      .date-picker {
        flex: 1;
        min-width: 200px;
        
        ::v-deep .el-input__wrapper {
          border-radius: 12px;
          border: 1px solid $border-color;
          
          &:hover {
            border-color: $light-primary;
          }
          
          &:focus-within {
            border-color: $primary-color;
            box-shadow: 0 0 0 2px rgba(73, 103, 255, 0.1);
          }
        }
      }
      
      .search-group {
        position: relative;
        width: 300px;
        
        .search-input {
          --el-input-border-radius: 12px;
          
          ::v-deep .el-input__wrapper {
            border: 1px solid $border-color !important;
            box-shadow: none !important;
            outline: none !important;
            padding-right: 40px !important;
            height: 40px;
            transition: border-color 0.2s;
            
            &:hover {
              border-color: $light-primary !important;
            }
            
            &:focus-within {
              border-color: $primary-color !important;
              box-shadow: 0 0 0 2px rgba(73, 103, 255, 0.1);
            }
          }
        }
        
        .search-icon {
          color: #000000;
          font-size: 18px;
          cursor: pointer;
          position: absolute;
          right: 14px;
          top: 50%;
          transform: translateY(-50%);
          z-index: 2;
          transition: color 0.2s;
          
          &:hover {
            color: $primary-color;
          }
        }
      }
    }
  }
  
  .order-card {
    border-radius: 16px;
    border: 1px solid $border-color;
    background: white;
    box-shadow: $shadow-light;
    overflow: hidden;
    
    .order-item {
      padding: 24px;
      border-bottom: 1px solid rgba(158, 174, 255, 0.1);
      transition: background-color 0.2s;
      
      &:last-child {
        border-bottom: none;
      }
      
      &:hover {
        background-color: rgba(158, 174, 255, 0.03);
      }
      
      .order-header {
        display: flex;
        justify-content: space-between;
        margin-bottom: 20px;
        padding-bottom: 12px;
        border-bottom: 1px dashed $border-color;
        
        .order-code {
          font-weight: 600;
          color: $text-main;
          font-size: 15px;
        }
        
        .order-time {
          font-size: 13px;
          color: $text-lighter;
          display: flex;
          align-items: center;
          gap: 6px;
          
          &::before {
            content: '';
            display: inline-block;
            width: 4px;
            height: 4px;
            border-radius: 50%;
            background-color: $light-primary;
          }
        }
      }
      
      .order-content {
        display: flex;
        gap: 24px;
        align-items: center;
        
        .order-user {
          flex: 0 0 100px;
          display: flex;
          flex-direction: column;
          align-items: center;
          
          ::v-deep .el-avatar {
            border: 2px solid rgba(158, 174, 255, 0.2);
          }
          
          .user-name {
            margin-top: 8px;
            font-size: 14px;
            color: $text-light;
            font-weight: 500;
          }
        }
        
        .order-product {
          flex: 1;
          display: flex;
          gap: 16px;
          
          .product-image {
            width: 80px;
            height: 80px;
            border-radius: 12px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
          }
          
          .product-info {
            flex: 1;
            
            .product-title {
              margin: 0 0 10px 0;
              font-size: 15px;
              color: $text-main;
              font-weight: 500;
              display: -webkit-box;
              -webkit-line-clamp: 2;
              line-clamp: 2;
              -webkit-box-orient: vertical;
              overflow: hidden;
              line-height: 1.5;
            }
            
            .product-meta {
              margin-bottom: 8px;
              font-size: 14px;
              
              .price {
                margin-right: 20px;
                color: $danger-color;
                font-weight: 600;
              }
              
              .quantity {
                color: $text-light;
              }
            }
            
            .product-remark {
              font-size: 13px;
              color: $text-lighter;
              background-color: rgba(158, 174, 255, 0.08);
              padding: 4px 8px;
              border-radius: 4px;
              display: inline-block;
            }
          }
        }
        
        .order-status {
          flex: 0 0 80px;
          display: flex;
          flex-direction: column;
          align-items: center;
          gap: 8px;
          
          .status-tag {
            --el-tag-border-radius: 8px;
            padding: 4px 12px;
            font-weight: 500;
            
            &.refund-tag {
              margin-top: 0 !important;
              --el-tag-danger-color: $danger-color;
            }
          }
        }
        
        .order-actions {
          flex: 0 0 120px;
          display: flex;
          flex-direction: column;
          align-items: flex-end;
          gap: 8px;
          
          .detail-btn {
            --el-button-bg-color: rgba(73, 103, 255, 0.1);
            --el-button-text-color: $primary-color;
            --el-button-border-color: transparent;
            --el-button-hover-bg-color: $primary-color;
            --el-button-hover-text-color: white;
            border-radius: 8px;
            transition: all 0.3s ease;
          }
          
          .delete-btn {
            border-radius: 8px;
            background-color: $danger-light-color;
            color: $danger-color;
            border-color: transparent;
            transition: all 0.3s ease;
            
            &:disabled {
              background-color: $danger-disabled-color;
              color: #ff9c9c;
              cursor: not-allowed;
            }
            
            &:hover:not(:disabled) {
              background-color: $danger-color;
              color: #fff;
              border-color: transparent;
            }
          }
        }
      }
    }
    
    .pagination-area {
      padding: 15px 20px;
      display: flex;
      justify-content: center;
      align-items: center;
      
      .category-pagination {
        ::v-deep .el-pagination {
          display: flex;
          align-items: center;
          gap: 8px !important;
          --el-pagination-item-border-radius: 6px;
        }
        
        ::v-deep .el-pager li {
          width: 32px !important;
          height: 32px !important;
          line-height: 32px !important;
          border-radius: 6px !important;
          margin: 0 2px !important;
          text-align: center;
          
          &.active {
            background-color: $light-primary;
            color: #fff;
            border: none;
          }
          
          &:hover:not(.active) {
            color: $primary-color;
            border-color: $light-primary;
          }
        }
        
        ::v-deep .el-pagination__prev,
        ::v-deep .el-pagination__next {
          width: 32px !important;
          height: 32px !important;
          border-radius: 6px !important;
          display: flex;
          align-items: center;
          justify-content: center;
          border: 1px solid $border-color;
          font-size: 16px !important;
          
          &:hover {
            border-color: $light-primary;
            color: $primary-color;
          }
          
          &.is-disabled {
            border-color: #e4e7ed;
            color: #c0c4cc;
            cursor: not-allowed;
          }
        }
      }
    }
  }
  
  .order-detail-drawer {
    ::v-deep .el-drawer__header {
      border-bottom: 1px solid $border-color;
      padding-bottom: 12px;
      margin-bottom: 20px;
      
      .el-drawer__title {
        font-size: 18px;
        font-weight: 600;
        color: $text-main;
      }
    }
    
    .drawer-content {
      padding: 0 20px 20px;
      
      .detail-card {
        border-radius: 12px;
        border: 1px solid $border-color;
        margin-bottom: 20px;
        
        .card-header {
          font-weight: 500;
          color: $primary-color;
          border-bottom: none;
          padding-bottom: 0;
        }
        
        .product-images {
          display: flex;
          flex-wrap: wrap;
          gap: 12px;
          padding: 16px 0;
          
          .detail-image {
            width: 100px;
            height: 100px;
            border-radius: 8px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
            transition: transform 0.2s;
            
            &:hover {
              transform: scale(1.04);
            }
          }
        }
        
        &.status-card {
          .status-item {
            display: flex;
            padding: 12px 0;
            border-bottom: 1px solid rgba(158, 174, 255, 0.1);
            
            &:last-child {
              border-bottom: none;
            }
            
            .status-label {
              width: 100px;
              font-weight: 500;
              color: $text-light;
              font-size: 14px;
            }
            
            .status-value {
              flex: 1;
              font-size: 14px;
              
              i {
                margin-right: 8px;
                font-size: 16px;
              }
              
              .status-time {
                margin-left: 15px;
                font-size: 13px;
                color: $text-lighter;
              }
            }
          }
        }
      }
    }
  }
  </style>