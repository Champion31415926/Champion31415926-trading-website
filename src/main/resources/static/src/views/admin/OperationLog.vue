<template>
  <div class="interaction-management-container">
    <el-card class="search-card" shadow="none">
      <div class="search-content">
        <div class="search-group">
          <el-input
            v-model="interactionQueryDto.productId"
            placeholder="Please enter product ID"
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
    </el-card>

    <el-card class="data-card" shadow="none">
      <el-table
        :data="tableData"
        stripe
        style="width: 100%"
        class="interaction-table"
        :header-cell-style="{
          background: 'rgba(158, 174, 255, 0.05)', 
          color: '#000000',
          fontWeight: 500,
          border: 'none'
        }"
        :row-class-name="({row}) => 'interaction-row'"
      >
        <el-table-column prop="userAccount" label="Account" width="120" align="center">
          <template #default="{row}">
            <div class="cell-content account-cell">
              <i class="el-icon-user" style="margin-right: 6px;"></i>
              {{ row.userAccount }}
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="Username" width="160" align="center"></el-table-column>
        <el-table-column prop="productId" label="Product ID" width="120" align="center" sortable></el-table-column>
        <el-table-column prop="type" label="Action Type" width="140" align="center">
          <template #default="{row}">
            <el-tag
              :type="getActionTypeTag(row.type)"
              effect="light"
              size="small"
              class="action-tag"
            >
              {{ getActionTypeText(row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="productTitle" label="Product Name" min-width="180" align="center"></el-table-column>
        <el-table-column prop="createTime" label="Interaction Time" width="200" align="center" sortable>
          <template #default="{row}">
            <div class="time-display">
              <i class="el-icon-time"></i>
              {{ formatTime(row.createTime) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="Operation" width="100" align="center">
          <template #default="{row}">
            <el-tooltip content="Delete" placement="top" effect="light">
              <el-button
                type="danger"
                icon="el-icon-delete"
                circle
                size="mini"
                @click="handleDelete(row)"
                class="delete-btn"
              ></el-button>
            </el-tooltip>
          </template>
        </el-table-column>
      </el-table>

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
  </div>
</template>

<script>
export default {
  data() {
    return {
      data: {},
      currentPage: 1,
      totalItems: 0,
      tableData: [],
      selectedRows: [],
      interactionQueryDto: {
        productId: ''
      }
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    getActionTypeTag(type) {
      const map = {
        1: 'success', 
        2: 'primary', 
        3: 'warning' 
      };
      const key = typeof type === 'string' ? parseInt(type) : type;
      return map[key] || '';
    },
    
    getActionTypeText(type) {
      const map = {
        1: 'Collect',
        2: 'Browse',
        3: 'Want'
      };
      const key = typeof type === 'string' ? parseInt(type) : type;
      return map[key] || 'Unknown';
    },
    
    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
    },
    
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message.warning('Please select at least one record');
        return;
      }
      
      try {
        const confirmed = await this.$confirm(
          `Are you sure to delete the selected ${this.selectedRows.length} interaction records? Deletion cannot be undone!`,
          'Delete Confirmation',
          {
            confirmButtonText: 'Confirm',
            cancelButtonText: 'Cancel',
            type: 'warning',
            confirmButtonColor: '#4967ff',
            cancelButtonColor: '#94A3B8'
          }
        );
        
        if (confirmed) {
          const ids = this.selectedRows.map(item => item.id);
          const response = await this.$axios.post('/operationLog/batchDelete', ids);
          
          if (response.data.code === 200) {
            this.$message.success('Deleted successfully');
            this.fetchFreshData();
            this.selectedRows = [];
          } else {
            this.$message.error(response.data.msg || 'Deletion failed');
          }
        }
      } catch (error) {
        console.error('Delete interaction records exception:', error);
        this.$message.error('Deletion operation exception');
      }
    },
    
    fetchFreshData() {
      this.interactionQueryDto.current = this.currentPage;
      this.interactionQueryDto.size = 10; 
      
      this.$axios.post('/operationLog/query', this.interactionQueryDto)
        .then(res => {
          const { data } = res;
          if (data.code === 200) {
            this.tableData = data.data;
            this.totalItems = data.total;
          } else {
            this.$message.error(data.msg || 'Query failed');
          }
        })
        .catch(error => {
          console.error('Query interaction records exception:', error);
          this.$message.error('Query operation exception');
        });
    },
    
    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
    },
    
    handleFilterClear() {
      this.interactionQueryDto.productId = '';
      this.handleFilter();
    },
    
    handleCurrentChange(val) {
      this.currentPage = val;
      this.fetchFreshData();
    },
    
    handleDelete(row) {
      this.selectedRows = [row];
      this.batchDelete();
    }
  }
};
</script>

<style scoped lang="scss">
$primary-color: #4967ff;
$light-primary: #9EAEFF;
$soft-purple: #CEC3FF;
$border-color: rgba(158, 174, 255, 0.25);
$text-main: #1E293B;
$text-light: #64748B;
$text-lighter: #94A3B8;
$bg-light: #F8FAFC;
$danger-color: #ff6b6b;
$danger-light: rgba(255, 107, 107, 0.1);
$shadow-light: 0 4px 16px rgba(0, 0, 0, 0.03);

.interaction-management-container {
  padding: 24px;
  background-color: $bg-light;
  min-height: calc(100vh - 80px);
  box-sizing: border-box;

  .search-card {
    margin-bottom: 24px;
    border-radius: 16px;
    border: 1px solid $border-color;
    background: white;
    box-shadow: $shadow-light;
    padding: 20px;
    
    .search-content {
      .search-group {
        position: relative;
        
        .search-input {
          width: 300px;
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

  .data-card {
    border-radius: 16px;
    border: 1px solid $border-color;
    background: white;
    box-shadow: $shadow-light;
    overflow: hidden;
    
    .interaction-table {
      --el-table-header-text-color: $primary-color;
      --el-table-row-hover-bg-color: rgba(158, 174, 255, 0.05);
      --el-table-stripe-row-bg-color: rgba(248, 250, 252, 0.8);
      
      ::v-deep {
        .el-table__header-wrapper {
          th {
            border-bottom: 1px solid $border-color;
            font-size: 14px;
            padding: 12px 0;
          }
        }
        
        .el-table__body-wrapper {
          .interaction-row {
            td {
              border-bottom: 1px solid rgba(158, 174, 255, 0.1);
              padding: 16px 0;
              color: $text-main;
              
              .cell-content {
                display: flex;
                align-items: center;
                justify-content: center;
                
                &.account-cell {
                  color: $text-light;
                  i {
                    color: $light-primary;
                    font-size: 14px;
                  }
                }
              }
            }
          }
        }
      }
      
      .action-tag {
        --el-tag-border-radius: 8px;
        --el-tag-primary-color: $primary-color;
        --el-tag-success-color: #10b981;
        --el-tag-warning-color: #f59e0b;
        padding: 4px 12px;
        height: 28px;
        line-height: 20px;
        font-weight: 500;
      }
      
      .time-display {
        color: $text-lighter;
        font-size: 13px;
        display: flex;
        align-items: center;
        justify-content: center;
        gap: 6px;
        
        i {
          color: $light-primary;
          font-size: 14px;
        }
      }
      
      .delete-btn {
        width: 28px;
        height: 28px;
        display: inline-flex;
        align-items: center;
        justify-content: center; 
        border-radius: 50%;
        transition: all 0.2s;
        background-color: $danger-light;
        color: $danger-color;
        border-color: transparent;
    
        &:hover {
          background-color: $danger-color;
          color: #fff;
          border-color: transparent;
        }

        ::v-deep i { 
          font-size: 14px; 
          line-height: 1;  
          margin: 0;       
          padding: 0;      
          display: block;  
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
        }
        
        ::v-deep .el-pager li {
          width: 32px;
          height: 32px;
          line-height: 32px;
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
          width: 32px;
          height: 32px;
          border-radius: 6px !important;
          display: flex;
          align-items: center;
          justify-content: center;
          border: 1px solid $border-color;
          
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
}
</style>