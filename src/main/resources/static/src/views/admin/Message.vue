<template>
  <div class="card-container message-container">
    <el-card class="search-card" shadow="none">
      <div class="search-content">
        <div class="search-filters">
          <el-date-picker
            v-model="searchTime"
            type="daterange"
            range-separator="to"
            start-placeholder="Start Date"
            end-placeholder="End Date"
            size="medium"
            style="width: 360px; margin-right: 12px;"
            @change="fetchFreshData"
            value-format="yyyy-MM-dd"
            :picker-options="pickerOptions"
            class="date-picker-custom"
          ></el-date-picker>
          
          <div class="search-group">
            <el-input
              v-model="messageQueryDto.content"
              placeholder="Enter message content"
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

    <el-card class="data-card" shadow="none">
      <div class="message-list">
        <el-table
          :data="tableData"
          stripe
          style="width: 100%"
          class="custom-table"
          :header-cell-style="{
            background: 'rgba(158, 174, 255, 0.05)', 
            color: '#000000',
            fontWeight: 500,
            border: 'none'
          }"
        >
          <el-table-column prop="userAvatar" label="Avatar" width="80" align="center">
            <template #default="{row}">
              <el-avatar
                :size="40"
                :src="row.userAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"
                shape="square"
                class="avatar-custom"
              ></el-avatar>
            </template>
          </el-table-column>
          
          <el-table-column prop="userName" label="Recipient" width="150" align="center"></el-table-column>
          
          <el-table-column prop="content" label="Message Content" min-width="200" header-align="center">
            <template #default="{row}">
              <div class="message-content-col">
                {{ row.content }}
              </div>
            </template>
          </el-table-column>
          
          <el-table-column prop="isRead" label="Status" width="120" align="center">
            <template #default="{row}">
              <el-tag
                :type="row.isRead ? 'success' : 'warning'"
                effect="light"
                size="small"
                class="status-tag"
              >
                {{ row.isRead ? 'Read' : 'Unread' }}
              </el-tag>
            </template>
          </el-table-column>
          
          <el-table-column prop="createTime" label="Send Time" width="200" align="center">
            <template #default="{row}">
              <div class="time-display">
                <i class="el-icon-time"></i>
                {{ row.createTime }}
              </div>
            </template>
          </el-table-column>
          
          <el-table-column label="Operation" width="90" align="center">
            <template #default="{row}">
              <el-tooltip content="Delete" placement="top" effect="light">
                <el-button
                  type="danger"
                  icon="el-icon-delete"
                  circle
                  size="mini"
                  @click="handleDelete(row)"
                  class="delete-btn-custom"
                ></el-button>
              </el-tooltip>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div class="pagination-area">
        <el-pagination
          @current-change="handleCurrentChange"
          :current-page="currentPage"
          :page-size="pageSize"
          layout="prev, pager, next"
          :total="totalItems"
          class="category-pagination"
        ></el-pagination>
      </div>
    </el-card>
  </div>
</template>

<script>
export default {
  data() {
    return {
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      tableData: [],
      selectedRows: [],
      searchTime: [],
      messageQueryDto: {},
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
        shortcuts: [{
          text: 'Last Week',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: 'Last Month',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
            picker.$emit('pick', [start, end]);
          }
        }, {
          text: 'Last 3 Months',
          onClick(picker) {
            const end = new Date();
            const start = new Date();
            start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
            picker.$emit('pick', [start, end]);
          }
        }]
      }
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    formatTime(time) {
      if (!time) return '';
      return new Date(time).toLocaleString();
    },
    
    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message.warning('Please select at least one record');
        return;
      }
      
      try {
        const confirmed = await this.$confirm(
          `Are you sure to delete the selected ${this.selectedRows.length} message records? Deletion cannot be undone!`,
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
          const response = await this.$axios.post('/message/batchDelete', ids);
          
          if (response.data.code === 200) {
            this.$message.success('Deleted successfully');
            this.fetchFreshData();
            this.selectedRows = [];
          } else {
            this.$message.error(response.data.msg || 'Deletion failed');
          }
        }
      } catch (error) {
        console.error('Delete message exception:', error);
        this.$message.error('Deletion operation exception');
      }
    },
    
    async fetchFreshData() {
      let startTime = null;
      let endTime = null;
      
      if (this.searchTime && this.searchTime.length === 2) {
        startTime = `${this.searchTime[0]}T00:00:00`;
        endTime = `${this.searchTime[1]}T23:59:59`;
      }
      
      this.messageQueryDto.current = this.currentPage;
      this.messageQueryDto.size = this.pageSize;
      this.messageQueryDto.startTime = startTime;
      this.messageQueryDto.endTime = endTime;
      
      try {
        const res = await this.$axios.post('/message/query', this.messageQueryDto);
        const { data } = res;
        
        if (data.code === 200) {
          this.tableData = data.data;
          this.totalItems = data.total;
        } else {
          this.$message.error(data.msg || 'Query failed');
        }
      } catch (error) {
        console.error('Query message exception:', error);
        this.$message.error('Query operation exception');
      }
    },
    
    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
    },
    
    handleFilterClear() {
      this.messageQueryDto.content = '';
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
$danger-light: rgba(255, 107, 107, 0.1);
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
  max-width: 1400px;
  margin: 0 auto;
}

.message-container {
  .search-card {
    margin-bottom: 24px;
    border-radius: 16px;
    border: 1px solid $border-color;
    background: white;
    box-shadow: $shadow-light;
    padding: 20px;
    
    .search-content {
      .search-filters {
        display: flex;
        align-items: center;
        
        .date-picker-custom {
          ::v-deep .el-input__wrapper {
            border-radius: 12px;
            border: 1px solid $border-color !important;
            box-shadow: none !important;
            height: 40px;
            
            &:hover {
              border-color: $light-primary !important;
            }
          }
        }
        
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
  }

  .data-card {
    border-radius: 16px;
    border: 1px solid $border-color;
    background: white;
    box-shadow: $shadow-light;
    overflow: hidden;
    
    .message-list {
      padding: 0 20px;
      
      .custom-table {
        --el-table-header-text-color: $primary-color;
        --el-table-row-hover-bg-color: rgba(158, 174, 255, 0.05);
        --el-table-stripe-row-bg-color: rgba(248, 250, 252, 0.8);
        
        ::v-deep {
          .el-table__header-wrapper th {
            border-bottom: 1px solid $border-color;
            padding: 12px 0;
          }
          
          .el-table__body-wrapper td {
            border-bottom: 1px solid rgba(158, 174, 255, 0.1);
            padding: 16px 0;
          }
        }
        
        .avatar-custom {
          border: 2px solid rgba(158, 174, 255, 0.2);
          border-radius: 8px;
        }
        
        .message-content-col {
          padding: 8px 0;
          line-height: 1.5;
          color: #000 !important;
          font-weight: 400;
        }
        
        .status-tag {
          --el-tag-border-radius: 8px;
          --el-tag-success-color: #10b981;
          --el-tag-warning-color: #f59e0b;
          padding: 4px 12px;
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
        
        .delete-btn-custom {
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
          border-radius: 6px;
          margin: 0 2px;
          text-align: center;
          
          &.active {
            background-color: $light-primary;
            color: #fff;
            border: none;
          }
        }
        
        ::v-deep .el-pagination__prev,
        ::v-deep .el-pagination__next {
          width: 32px;
          height: 32px;
          border-radius: 6px;
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
          }
        }
      }
    }
  }
}
</style>