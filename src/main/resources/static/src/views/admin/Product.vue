<template>
    <div class="card-container">
        <el-card class="filter-card" shadow="none">
            <div class="filter-content">
                <div class="filter-tabs">
                    <el-radio-group v-model="bargainSelectedItem" @change="bargainSelected" size="small">
                        <el-radio-button v-for="(bargain, index) in bargainStatus" :key="index" :label="bargain">
                            {{ bargain.name }}
                        </el-radio-button>
                    </el-radio-group>
                </div>

                <div class="filter-controls">
                    <el-select class="filter-item select-item" @change="fetchFreshData" v-model="productQueryDto.categoryId"
                        placeholder="Product Category" size="medium">
                        <el-option v-for="item in categoryList" :key="item.id" :label="item.name" :value="item.id">
                        </el-option>
                    </el-select>

                    <el-date-picker class="filter-item date-picker" @change="fetchFreshData" v-model="searchTime" type="daterange"
                        range-separator="to" start-placeholder="Publish Start" end-placeholder="Publish End" size="medium">
                    </el-date-picker>

                    <div class="search-group">
                        <el-input 
                            v-model="productQueryDto.name" 
                            placeholder="Product Name" 
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

        <el-card class="product-card" shadow="none">
            <div v-for="product in tableData" :key="product.id" class="product-item">
                <div class="product-header">
                    <div class="product-name">Product: {{ product.name }}</div>
                    <div class="product-time">{{ product.createTime }}</div>
                </div>
                
                <div class="product-content">
                    <div class="product-user">
                        <el-avatar :size="40" :src="product.userAvatar"></el-avatar>
                        <div class="user-name">{{ product.userName }}</div>
                    </div>
                    
                    <div class="product-info-wrap">
                        <div class="product-images" v-if="product.coverList">
                            <el-image :src="product.coverList.split(',')[0]" 
                                      :preview-src-list="product.coverList.split(',')"
                                      fit="cover"
                                      class="product-image">
                            </el-image>
                        </div>
                        
                        <div class="product-details">
                            <div class="product-meta">
                                <el-tag size="small" class="status-tag category-tag">{{ product.categoryName }}</el-tag>
                                <el-tag size="small" class="status-tag level-tag">{{ product.oldLevel }}% New</el-tag>
                                <el-tag size="small" :type="product.isBargain ? 'success' : 'warning'" class="status-tag bargain-tag">
                                    {{ product.isBargain ? 'Support Bargain' : 'No Bargain' }}
                                </el-tag>
                            </div>
                            <div class="product-price">
                                <span class="price">Price: {{ product.price }} CNY</span>
                                <span class="inventory">Inventory: {{ product.inventory }}</span>
                            </div>
                        </div>
                    </div>
                    
                    <div class="product-actions">
                        <el-button class="detail-btn" size="mini" @click="handleEdit(product)">Details</el-button>
                        <el-button class="delete-btn" size="mini" @click="handleDelete(product)">Delete</el-button>
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

        <el-drawer title="Product Details" 
                   :visible.sync="drawerProductOperaion" 
                   :direction="direction"
                   size="40%"
                   :wrapper-closable="false"
                   class="product-detail-drawer">
            <div class="drawer-content">
                <el-card shadow="none" class="detail-card">
                    <div slot="header" class="card-header">
                        <span>Product Cover Images</span>
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
                
                <el-card shadow="none" class="detail-card info-card">
                    <div slot="header" class="card-header">
                        <span>Product Information</span>
                    </div>
                    <div class="info-item" v-for="(item, index) in infoItems" :key="index">
                        <div class="info-label">{{ item.label }}:</div>
                        <div class="info-value">{{ item.value }}</div>
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
            productQueryDto: {},
            direction: 'rtl',
            categoryList: [],
            bargainSelectedItem: {},
            bargainStatus: [
                { isBargain: null, name: 'All' },
                { isBargain: true, name: 'Support Bargain' },
                { isBargain: false, name: 'No Bargain' }
            ]
        };
    },
    created() {
        this.fetchCategoryList();
        this.fetchFreshData();
        this.bargainSelected(this.bargainStatus[0]);
    },
    computed: {
        infoItems() {
            return [
                { label: 'Product Name', value: this.data.name || '-' },
                { label: 'Category', value: this.data.categoryName || '-' },
                { label: 'Condition', value: `${this.data.oldLevel || '-'}% New` },
                { label: 'Price', value: `${this.data.price || '-'} CNY` },
                { label: 'Inventory', value: this.data.inventory || '-' },
                { label: 'Bargain', value: this.data.isBargain ? 'Support' : 'Not Support' },
                { label: 'Description', value: this.data.detail || 'No description' }
            ];
        }
    },
    methods: {
        bargainSelected(bargain) {
            this.bargainSelectedItem = bargain;
            this.productQueryDto.isBargain = bargain.isBargain;
            this.fetchFreshData();
        },
        fetchCategoryList() {
            this.$axios.post('/category/query', {}).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    this.categoryList = data.data;
                    this.categoryList.unshift({ id: null, name: 'All' });
                }
            }).catch(error => {
                console.log("Product category query exception：", error);
            })
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
                'Delete Product Data',
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
                    const response = await this.$axios.post(`/product/batchDelete`, ids);
                    if (response.data.code === 200) {
                        this.$message.success('Deleted successfully');
                        this.fetchFreshData();
                        this.delectedRows = [];
                        return;
                    }
                } catch (error) {
                    this.$message.error("Product information deletion exception");
                    console.error(`Product information deletion exception：`, error);
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
            this.productQueryDto.current = this.currentPage;
            this.productQueryDto.size = 10;
            this.productQueryDto.startTime = startTime;
            this.productQueryDto.endTime = endTime;
            this.$axios.post('/product/query', this.productQueryDto).then(res => {
                const { data } = res;
                if (data.code === 200) {
                    this.tableData = data.data;
                    this.totalItems = data.total;
                }
            }).catch(error => {
                this.$message.error('Failed to query product data');
                console.error('Product query exception：', error);
            })
        },
        handleFilter() {
            this.currentPage = 1;
            this.fetchFreshData();
        },
        handleFilterClear() {
            this.productQueryDto.name = '';
            this.handleFilter();
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.fetchFreshData();
        },
        handleEdit(row) {
            this.data = row;
            this.coverList = row.coverList ? row.coverList.split(',') : [];
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
        flex-wrap: wrap;
        
        .filter-item {
            flex: 1;
            min-width: 200px;
            
            &.select-item {
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
        }
        
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

.product-card {
    border-radius: 16px;
    border: 1px solid $border-color;
    background: white;
    box-shadow: $shadow-light;
    overflow: hidden;
    
    .product-item {
        padding: 24px;
        border-bottom: 1px solid rgba(158, 174, 255, 0.1);
        transition: background-color 0.2s;
        
        &:last-child {
            border-bottom: none;
        }
        
        &:hover {
            background-color: rgba(158, 174, 255, 0.03);
        }
        
        .product-header {
            display: flex;
            justify-content: space-between;
            margin-bottom: 20px;
            padding-bottom: 12px;
            border-bottom: 1px dashed $border-color;
            
            .product-name {
                font-weight: 600;
                color: $text-main;
                font-size: 15px;
            }
            
            .product-time {
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
        
        .product-content {
            display: flex;
            gap: 24px;
            align-items: center;
            
            .product-user {
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
            
            .product-info-wrap {
                flex: 1;
                display: flex;
                gap: 16px;
                
                .product-images {
                    flex: 0 0 80px;
                    
                    .product-image {
                        width: 80px;
                        height: 80px;
                        border-radius: 12px;
                        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.08);
                    }
                }
                
                .product-details {
                    flex: 1;
                    
                    .product-meta {
                        margin-bottom: 10px;
                        
                        .status-tag {
                            --el-tag-border-radius: 8px;
                            padding: 4px 12px;
                            font-weight: 500;
                            margin-right: 8px;
                            
                            &.bargain-tag {
                                margin-top: 0 !important;
                            }
                        }
                    }
                    
                    .product-price {
                        font-size: 14px;
                        
                        .price {
                            margin-right: 20px;
                            color: $danger-color;
                            font-weight: 600;
                        }
                        
                        .inventory {
                            color: $text-light;
                        }
                    }
                }
            }
            
            .product-actions {
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

.product-detail-drawer {
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
            
            &.info-card {
                .info-item {
                    display: flex;
                    padding: 12px 0;
                    border-bottom: 1px solid rgba(158, 174, 255, 0.1);
                    
                    &:last-child {
                        border-bottom: none;
                    }
                    
                    .info-label {
                        width: 120px;
                        font-weight: 500;
                        color: $text-light;
                        font-size: 14px;
                    }
                    
                    .info-value {
                        flex: 1;
                        font-size: 14px;
                        color: $text-main;
                    }
                }
            }
        }
    }
}
</style>