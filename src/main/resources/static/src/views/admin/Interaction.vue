<template>
  <div class="interaction-management-container">
    <el-card class="search-card" shadow="never">
      <div class="search-content">
        <div class="search-group">
          <el-input
            v-model="interactionQueryDto.productId"
            placeholder="Please enter product ID"
            clearable
            size="medium"
            style="width: 250px; height: 35px;"
            @clear="handleFilterClear"
            @keyup.enter="handleFilter"
            class="search-input"
          >
            <template #suffix>
              <i class="el-icon-search search-icon" @click="handleFilter"></i>
            </template>
          </el-input>
        </div>
      </div>
    </el-card>

    <el-card class="data-card" shadow="never">
      <el-table
        :data="tableData"
        stripe
        style="width: 100%"
        class="interaction-table"
        :header-cell-style="{background: '#f7f9fc', color: '#333', fontWeight: '500'}"
      >
        <el-table-column prop="userAccount" label="Account" width="120" align="center"></el-table-column>
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
        <el-table-column prop="productTitle" label="Product Name" min-width="200" align="center"></el-table-column>
        <el-table-column prop="createTime" label="Interaction Time" width="180" align="center" sortable>
          <template #default="{row}">
            <div class="time-display">
              <i class="el-icon-time"></i>
              {{ formatTime(row.createTime) }}
            </div>
          </template>
        </el-table-column>
        <el-table-column label="Operation" width="100" align="center">
          <template #default="{row}">
            <el-tooltip content="Delete" placement="top">
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
      return map[type] || '';
    },
    
    getActionTypeText(type) {
      const map = {
        1: 'Collect',
        2: 'Browse',
        3: 'Want'
      };
      return map[type] || 'Unknown';
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
            type: 'warning'
          }
        );
        
        if (confirmed) {
          const ids = this.selectedRows.map(item => item.id);
          const response = await this.$axios.post('/interaction/batchDelete', ids);
          
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
      
      this.$axios.post('/interaction/query', this.interactionQueryDto)
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
$danger-color: #ff6b6b;
$border-color: #e4e7ed;
$text-color: #333;
$text-light-color: #666;
$black: #000;

.interaction-management-container {
  padding: 25px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin: 20px;

  .search-card {
    margin-bottom: 10px;
    border-radius: 8px;
    border: 1px solid $border-color;
    
    .search-content {
      padding: 5px 20px;
      
      .search-group {
        position: relative;
        
        .search-input {
          --el-input-border-radius: 8px;
          
          ::v-deep .el-input__wrapper {
            border: 1px solid $black !important;
            box-shadow: none !important;
            outline: none !important;
            padding-right: 35px !important;
            height: 35px;
          }
        }
        
        .search-icon {
          color: $black;
          font-size: 16px;
          cursor: pointer;
          position: absolute;
          right: 12px;
          top: 50%;
          transform: translateY(-50%);
          z-index: 2;
          
          &:hover {
            color: #333;
          }
        }
      }
    }
  }

  .data-card {
    border-radius: 8px;
    border: 1px solid $border-color;
    overflow: hidden;
    
    .interaction-table {
      --el-table-border-radius: 8px;
      
      ::v-deep .el-table__header-wrapper {
        th {
          border-bottom: 1px solid $border-color;
        }
      }
      
      ::v-deep .el-table__body-wrapper {
        tr {
          &:hover > td {
            background-color: rgba(158, 174, 255, 0.03);
          }
        }
      }
      
      .action-tag {
        --el-tag-border-radius: 6px;
        padding: 0 10px;
        height: 26px;
        line-height: 26px;
      }
      
      .time-display {
        color: $text-light-color;
        font-size: 13px;
        display: flex;
        align-items: center;
        gap: 5px;
        
        i {
          color: $light-primary;
        }
      }
      
      .delete-btn {
        background-color: rgba(255, 107, 107, 0.1);
        color: $danger-color;
        border-color: transparent;
        width: 28px;
        height: 28px;
        
        &:hover {
          background-color: $danger-color;
          color: #fff;
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
}
</style>