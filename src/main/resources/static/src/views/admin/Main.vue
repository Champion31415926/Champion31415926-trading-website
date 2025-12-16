<template>
  <div class="dashboard-container">
    <div class="dashboard-left">
      <div class="stats-grid">
        <div 
          v-for="(stat, index) in staticCountList" 
          :key="index" 
          class="stat-card"
          :style="{ '--card-color': colorPalette[index % colorPalette.length] }">
          <div class="stat-card-highlight"></div>
          <div class="stat-title">{{ stat.name }}</div>
          <div class="stat-value">
            <el-statistic 
              group-separator="," 
              :precision="0" 
              :value="stat.count">
            </el-statistic>
          </div>
          <div class="stat-icon">
            <i :class="statIcons[index % statIcons.length]"></i>
          </div>
        </div>
      </div>

      <div class="chart-container">
        <div class="chart-header">
          <span class="chart-title">Product Shelving Trend</span>
          <div class="chart-header-right">
            <el-tag size="small" class="chart-period-tag">
              Time Range: Last {{days}} Days
            </el-tag>
          </div>
        </div>
        <LineChart 
          @on-selected="onSelected" 
          tag="" 
          :values="values" 
          :date="dates" 
          height="400px" />
      </div>
    </div>

    <div class="dashboard-right">
      <div class="product-header">
        <h2 class="product-header-title">
          <i class="el-icon-s-goods" style="margin-right: 8px; color: #4967ff;"></i>
          Latest Shelved Products
        </h2>
        <el-button 
          type="text" 
          icon="el-icon-refresh" 
          @click="fetchProductList"
          class="refresh-btn">
        </el-button>
      </div>
      
      <div class="product-list">
        <div 
          v-for="(product, index) in productList" 
          :key="index" 
          class="product-card"
          @click="route(product)">
          <div class="product-badge" v-if="product.isBargain">
            <span>Bargain</span>
          </div>
          <div class="product-cover">
            <img :src="coverListParse(product)" :alt="product.name" class="product-cover-img">
          </div>
          
          <div class="product-info">
            <div class="product-title">{{ product.name }}</div>
            
            <div class="product-meta">
              <div class="product-price">
                <span class="price-symbol">¥</span>
                <span class="price-value">{{ product.price }}</span>
              </div>
              <div class="product-likes">
                <i class="el-icon-star-on"></i>
                <span>{{ product.likeNumber }} people want</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import LineChart from "@/components/LineChart"

export default {
  components: { LineChart },
  data() {
    return {
      staticCountList: [],
      productList: [],
      values: [],
      dates: [],
      days: 365,
      colorPalette: [
        'linear-gradient(135deg, rgba(158, 174, 255, 0.9) 0%, rgba(117, 133, 255, 0.7) 100%)',
        'linear-gradient(135deg, rgba(133, 207, 255, 0.9) 0%, rgba(107, 191, 255, 0.7) 100%)'
      ],
      statIcons: [
        'el-icon-s-goods',
        'el-icon-s-shop',
        'el-icon-s-order',
        'el-icon-s-custom'
      ]
    }
  },
  created() {
    this.fetchStaticCount();
    this.fetchProductList();
    this.onSelected(this.days);
  },
  methods: {
    route(product) {
      this.$router.push('/product-detail1?productId=' + product.id);
    },
    
    coverListParse(product) {
      if (!product.coverList) return '';
      const newCoverList = product.coverList.split(',');
      return newCoverList[0] || '';
    },
    
    onSelected(day) {
      this.days = day;
      this.$axios.get(`/dashboard/productShelvesInfo/${day}`).then(res => {
        const { data } = res;
        if (data.code === 200) {
          this.values = data.data.map(entity => entity.count);
          this.dates = data.data.map(entity => entity.name);
        }
      }).catch(error => {
        console.error("Product shelving status exception:", error);
      })
    },
    
    fetchStaticCount() {
      this.$axios.get('/dashboard/staticCount').then(res => {
        const { data } = res;
        if (data.code === 200) {
          const nameMap = {
            '用户数': 'User Count',
            '收录商品': 'Product Count',
            '订单数': 'Order Count',
            '消息通知': 'Notification Count',
            '互动数据': 'Interaction Data',
            '商品评论': 'Product Comments',
            '分类数量': 'Category Count',
            '总销售额': 'Total Sales'
          };
          this.staticCountList = data.data.map(stat => ({
            ...stat,
            name: nameMap[stat.name] || stat.name
          }));
        }
      }).catch(error => {
        console.error("Basic data query exception:", error);
      })
    },
    
    fetchProductList() {
      const productQueryDto = { 
        size: 3, 
        current: 1 
      }
      this.$axios.post('/product/query', productQueryDto).then(res => {
        const { data } = res;
        if (data.code === 200) {
          this.productList = data.data;
        }
      }).catch(error => {
        console.error("Product information query exception:", error);
      })
    }
  }
};
</script>

<style scoped lang="scss">
$primary-color: #4967ff;
$light-primary: #9EAEFF;
$lighter-primary: rgba(158, 174, 255, 0.15);
$soft-purple: #bcaefe;
$border-color: rgba(158, 174, 255, 0.25);
$text-main: #1E293B;
$text-light: #64748B;
$text-lighter: #94A3B8;
$bg-light: #F8FAFC;

.dashboard-container {
  display: flex;
  gap: 24px;
  padding: 24px;
  background-color: $bg-light;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.05);
  margin: 16px;
  min-height: calc(100vh - 80px);
  box-sizing: border-box;
}

.dashboard-left {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.dashboard-right {
  width: 380px;
  flex-shrink: 0;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
  gap: 20px;
}

.stat-card {
  position: relative;
  padding: 20px;
  border-radius: 16px;
  background: var(--card-color);
  color: white;
  box-shadow: 0 6px 16px rgba(158, 174, 255, 0.2);
  overflow: hidden;
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: 1px solid rgba(255, 255, 255, 0.15);

  .stat-card-highlight {
    position: absolute;
    top: 0;
    right: 0;
    width: 80px;
    height: 80px;
    background: linear-gradient(135deg, rgba(255,255,255,0.2) 0%, rgba(255,255,255,0) 100%);
    border-radius: 0 0 0 50%;
  }

  &:hover {
    transform: translateY(-4px);
    box-shadow: 0 12px 24px rgba(158, 174, 255, 0.3);
  }

  .stat-title {
    font-size: 16px;
    font-weight: 500;
    margin-bottom: 12px;
    opacity: 0.9;
    letter-spacing: 0.2px;
  }

  .stat-value {
    font-size: 32px;
    font-weight: 700;
    line-height: 1.2;

    ::v-deep .el-statistic__content {
      color: white;
      font-size: inherit;
      font-weight: inherit;
    }
  }

  .stat-icon {
    position: absolute;
    right: 20px;
    bottom: 20px;
    font-size: 36px;
    opacity: 0.2;
  }
}

.chart-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  background: white;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.03);
  border: 1px solid $border-color;

  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 20px;

    .chart-title {
      font-size: 18px;
      font-weight: 600;
      color: $text-main;
      letter-spacing: 0.3px;
    }

    .chart-header-right {
      display: flex;
      align-items: center;
      gap: 12px;
    }

    .chart-period-tag {
      --el-tag-bg-color: $lighter-primary;
      --el-tag-text-color: $primary-color;
      --el-tag-border-radius: 10px;
      --el-tag-font-size: 13px;
      padding: 4px 12px;
      border: none;
      font-weight: 500;
    }
  }
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 0 4px;

  .product-header-title {
    margin: 0;
    font-size: 18px;
    font-weight: 600;
    color: $text-main;
    display: flex;
    align-items: center;
  }

  .refresh-btn {
    color: #7289ff;
    font-size: 20px;
    padding: 6px 8px;
    
    &:hover {
      color: $primary-color;
      background-color: $lighter-primary;
      border-radius: 8px;
    }
  }
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-card {
  position: relative;
  background: white;
  border-radius: 16px;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.04);
  transition: all 0.3s cubic-bezier(0.25, 0.8, 0.25, 1);
  border: 1px solid $border-color;
  cursor: pointer;

  &:hover {
    transform: translateY(-5px);
    box-shadow: 0 12px 24px rgba(158, 174, 255, 0.2);
    border-color: $light-primary;
  }

  .product-badge {
    position: absolute;
    top: 12px;
    right: 12px;
    background: linear-gradient(135deg, #9EAEFF 0%, #CEC3FF 100%);
    color: white;
    padding: 5px 14px;
    border-radius: 20px;
    font-size: 12px;
    font-weight: 600;
    z-index: 2;
    box-shadow: 0 3px 8px rgba(73, 103, 255, 0.3);
  }

  .product-cover {
    height: 180px;
    overflow: hidden;

    .product-cover-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      transition: transform 0.5s ease;
      border-radius: 16px 16px 0 0;
    }

    &:hover .product-cover-img {
      transform: scale(1.06);
    }
  }

  .product-info {
    padding: 20px;

    .product-title {
      font-size: 16px;
      font-weight: 500;
      color: $text-main;
      margin-bottom: 14px;
      display: -webkit-box;
      display: box;
      -webkit-line-clamp: 2;
      line-clamp: 2;
      -webkit-box-orient: vertical;
      box-orient: vertical;
      overflow: hidden;
      text-overflow: ellipsis;
      line-height: 1.5;
    }

    .product-meta {
      display: flex;
      justify-content: space-between;
      align-items: center;

      .product-price {
        .price-symbol {
          font-size: 14px;
          color: $primary-color;
          font-weight: 600;
        }
        
        .price-value {
          font-size: 22px;
          color: $primary-color;
          font-weight: 700;
        }
      }

      .product-likes {
        display: flex;
        align-items: center;
        gap: 6px;
        font-size: 14px;
        color: $text-lighter;
        
        i {
          color: $light-primary;
          font-size: 16px;
        }
      }
    }
  }
}

@media (max-width: 1200px) {
  .dashboard-container {
    flex-direction: column;
  }
  
  .dashboard-right {
    width: 100%;
    margin-top: 24px;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .dashboard-container {
    padding: 16px;
    margin: 8px;
  }
  
  .stat-card {
    padding: 18px;
    
    .stat-value {
      font-size: 28px;
    }
    
    .stat-icon {
      font-size: 32px;
    }
  }

  .product-cover {
    height: 160px;
  }
}
</style>