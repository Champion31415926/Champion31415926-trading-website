<template>
  <div class="user-management-container">
    <el-card class="search-card" shadow="never">
      <div class="search-content">
        <el-select v-model="userQueryDto.isLogin" placeholder="Login Status" size="medium"
          style="width: 150px; margin-right: 12px;" @change="fetchFreshData" clearable>
          <el-option v-for="item in loginStatuList" :key="item.value" :label="item.label"
            :value="item.value"></el-option>
        </el-select>

        <el-select v-model="userQueryDto.isWord" placeholder="Mute Status" size="medium"
          style="width: 150px; margin-right: 12px;" @change="fetchFreshData" clearable>
          <el-option v-for="item in wordStatuList" :key="item.value" :label="item.label"
            :value="item.value"></el-option>
        </el-select>

        <el-date-picker v-model="searchTime" type="daterange" range-separator="To" start-placeholder="Registration Start Date"
          end-placeholder="Registration End Date" size="medium" style="width: 420px; margin-right: 12px;" @change="fetchFreshData"
          value-format="yyyy-MM-dd" :picker-options="pickerOptions"></el-date-picker>

        <div class="search-group" style="position: relative;">
          <el-input v-model="userQueryDto.userName" placeholder="Enter Username" clearable size="medium" 
            style="width: 220px; height: 35px;"
            @clear="handleFilterClear" @keyup.enter="handleFilter">
          </el-input>
          <i class="el-icon-search search-icon" @click="handleFilter"></i>
          
          <el-button 
            v-if="userQueryDto.userName" 
            icon="el-icon-refresh-left" 
            @click="handleReturn"
            size="medium"
            class="return-btn"
            type="text"
            style="margin-left: 10px;">
          </el-button>
        </div>

        <el-button type="primary" class="add-btn" icon="el-icon-plus" style="margin-left: 12px;"
          @click="add">Add User</el-button>
      </div>
    </el-card>

    <div class="user-cards-container">
      <el-card v-for="(user, index) in tableData" :key="index" class="user-card" shadow="hover">
        <div class="user-header">
          <el-avatar :src="user.userAvatar || 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png'"
            size="medium"></el-avatar>
          <div class="user-info">
            <div class="user-name">{{ user.userName }}</div>
            <div class="user-account">{{ user.userAccount }}</div>
          </div>
        </div>

        <div class="user-details">
          <div class="detail-item">
            <i class="el-icon-message"></i>
            <span>{{ user.userEmail || 'Not Set' }}</span>
          </div>
          <div class="detail-item">
            <i class="el-icon-time"></i>
            <span>{{ formatTime(user.createTime) }}</span>
          </div>
        </div>

        <div class="user-status">
          <el-tag :type="user.userRole === 1 ? 'danger' : 'success'" size="small" class="status-tag">
            {{ user.userRole === 1 ? 'Administrator' : 'User' }}
          </el-tag>

          <el-tag :type="user.isLogin ? 'danger' : 'success'" size="small" class="status-tag">
            {{ user.isLogin ? 'Account Banned' : 'Account Normal' }}
          </el-tag>

          <el-tag :type="user.isWord ? 'warning' : 'success'" size="small" class="status-tag">
            {{ user.isWord ? 'Muted' : 'Comment Normal' }}
          </el-tag>
        </div>

        <div class="user-actions">
          <el-tooltip content="Change Status" placement="top">
            <el-button type="primary" icon="el-icon-setting" circle size="mini" @click="handleStatus(user)" class="action-btn setting-btn"></el-button>
          </el-tooltip>

          <el-tooltip content="Edit User" placement="top">
            <el-button type="warning" icon="el-icon-edit" circle size="mini" @click="handleEdit(user)" class="action-btn edit-btn"></el-button>
          </el-tooltip>

          <el-tooltip content="Delete User" placement="top">
            <el-button type="danger" icon="el-icon-delete" circle size="mini" @click="handleDelete(user)" class="action-btn delete-btn"></el-button>
          </el-tooltip>
        </div>
      </el-card>
    </div>

    <div class="pagination-area">
      <el-pagination 
        @current-change="handleCurrentChange" 
        :current-page="currentPage"
        :page-size="pageSize" 
        layout="prev, pager, next"
        :total="totalItems" 
        class="user-pagination"
        prev-text="<"
        next-text=">">
      </el-pagination>
    </div>

    <el-dialog :title="isOperation ? 'Edit User' : 'Add User'" :visible.sync="dialogUserOperaion" width="500px"
      :close-on-click-modal="false" class="user-dialog" :modal-style="{background: 'rgba(0, 0, 0, 0.1)'}">
      <el-form label-width="100px" class="user-form">
        <el-form-item label="User Avatar">
          <el-upload class="avatar-uploader" action="http://localhost:21090/api/trading-website/v1.0/file/upload"
            :show-file-list="false" :on-success="handleAvatarSuccess">
            <img v-if="userAvatar" :src="userAvatar" class="avatar" alt="User Avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>

        <el-form-item label="Username">
          <el-input style="width: 90%;" v-model="data.userName" placeholder="Enter Username" class="form-input"></el-input>
        </el-form-item>

        <el-form-item label="Account">
          <el-input style="width: 90%;" v-model="data.userAccount" placeholder="Enter Account" class="form-input"></el-input>
        </el-form-item>

        <el-form-item label="Email">
          <el-input style="width: 90%;" v-model="data.userEmail" placeholder="Enter Email" class="form-input"></el-input>
        </el-form-item>

        <el-form-item label="Password">
          <el-input style="width: 90%;" v-model="userPwd" type="password" placeholder="Enter Password" class="form-input"></el-input>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button class="cancel-btn" @click="cannel">Cancel</el-button>
        <el-button class="confirm-btn" type="primary" @click="isOperation ? updateOperation() : addOperation()">
          Confirm
        </el-button>
      </span>
    </el-dialog>

    <el-dialog title="User Status Settings" :visible.sync="dialogStatusOperation" width="400px" :close-on-click-modal="false"
      class="status-dialog" :modal-style="{background: 'rgba(0, 0, 0, 0.1)'}">
      <el-form label-width="120px" class="status-form">
        <el-form-item label="Ban Status">
          <el-switch v-model="data.isLogin" active-text="Banned" inactive-text="Normal" active-color="#9EAEFF"
            inactive-color="#909399" :active-value="1" :inactive-value="0"></el-switch>
        </el-form-item>

        <el-form-item label="Mute Status">
          <el-switch v-model="data.isWord" active-text="Muted" inactive-text="Normal" active-color="#9EAEFF"
            inactive-color="#909399" :active-value="1" :inactive-value="0"></el-switch>
        </el-form-item>

        <el-form-item label="Administrator Setting">
          <el-switch v-model="isAdmin" active-text="Administrator" inactive-text="Regular User" active-color="#9EAEFF"
            inactive-color="#909399"></el-switch>
        </el-form-item>
      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button class="cancel-btn" @click="cannel">Cancel</el-button>
        <el-button class="confirm-btn" type="primary" @click="comfirmStatus">Confirm</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  data() {
    return {
      userPwd: '',
      userAvatar: '',
      data: {},
      filterText: '',
      isAdmin: false,
      currentPage: 1,
      pageSize: 12,
      totalItems: 0,
      dialogStatusOperation: false,
      dialogUserOperaion: false,
      isOperation: false,
      tableData: [],
      searchTime: [],
      selectedRows: [],
      status: null,
      userQueryDto: {
        isLogin: null,
        isWord: null,
        userName: ''
      },
      loginStatuList: [
        { value: null, label: 'All' },
        { value: 0, label: 'Normal' },
        { value: 1, label: 'Banned' }
      ],
      wordStatuList: [
        { value: null, label: 'All' },
        { value: 0, label: 'Normal' },
        { value: 1, label: 'Muted' }
      ],
      rolesList: [
        { value: null, label: 'All' },
        { value: 2, label: 'User' },
        { value: 1, label: 'Administrator' }
      ],
      pickerOptions: {
        disabledDate(time) {
          return time.getTime() > Date.now();
        },
        shortcuts: [
          {
            text: 'Last Week',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 7);
              picker.$emit('pick', [start, end]);
            }
          },
          {
            text: 'Last Month',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 30);
              picker.$emit('pick', [start, end]);
            }
          },
          {
            text: 'Last Three Months',
            onClick(picker) {
              const end = new Date();
              const start = new Date();
              start.setTime(start.getTime() - 3600 * 1000 * 24 * 90);
              picker.$emit('pick', [start, end]);
            }
          }
        ]
      }
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    formatTime(time) {
      if (!time) return '';
      const date = new Date(time);
      return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')}`;
    },

    cannel() {
      this.data = {};
      this.userAvatar = '';
      this.userPwd = '';
      this.dialogUserOperaion = false;
      this.dialogStatusOperation = false;
      this.isOperation = false;
    },

    comfirmStatus() {
      const userUpdateDto = {
        id: this.data.id,
        isLogin: this.data.isLogin,
        userRole: this.isAdmin ? 1 : 2,
        isWord: this.data.isWord
      };

      this.$axios.put(`/user/backUpdate`, userUpdateDto)
        .then(res => {
          if (res.data.code === 200) {
            this.$message.success('Status updated successfully');
            this.cannel();
            this.fetchFreshData();
          } else {
            this.$message.error(res.data.msg || 'Status update failed');
          }
        })
        .catch(error => {
          console.error("Error updating status:", error);
          this.$message.error('Status update error');
        });
    },

    handleStatus(data) {
      this.isAdmin = data.userRole === 1;
      this.dialogStatusOperation = true;
      this.data = { ...data };
    },

    handleAvatarSuccess(res, file) {
      if (res.code === 200) {
        this.userAvatar = res.data;
        this.$message.success('Avatar uploaded successfully');
      } else {
        this.$message.error('Avatar upload failed');
      }
    },

    async batchDelete() {
      if (!this.selectedRows.length) {
        this.$message.warning('Please select at least one item');
        return;
      }

      try {
        const confirmed = await this.$confirm(
          `Are you sure you want to delete ${this.selectedRows.length} user records? This action cannot be undone!`,
          'Deletion Confirmation',
          {
            confirmButtonText: 'Confirm',
            cancelButtonText: 'Cancel',
            type: 'warning'
          }
        );

        if (confirmed) {
          const ids = this.selectedRows.map(item => item.id);
          const response = await this.$axios.post(`/user/batchDelete`, ids);

          if (response.data.code === 200) {
            this.$message.success('Deleted successfully');
            this.fetchFreshData();
            this.selectedRows = [];
          } else {
            this.$message.error(response.data.msg || 'Deletion failed');
          }
        }
      } catch (error) {
        console.error('Error deleting users:', error);
        this.$message.error('Deletion operation error');
      }
    },

    async updateOperation() {
      if (this.userPwd !== '') {
        const pwd = this.$md5(this.$md5(this.userPwd));
        this.data.userPwd = pwd;
      } else {
        this.data.userPwd = null;
      }
      this.data.userAvatar = this.userAvatar;

      try {
        const response = await this.$axios.put('/user/backUpdate', this.data);
        if (response.data.code === 200) {
          this.$message.success('User information updated successfully');
          this.cannel();
          this.fetchFreshData();
        } else {
          this.$message.error(response.data.msg || 'Update failed');
        }
      } catch (error) {
        console.error('Update error:', error);
        this.$message.error('Update operation error');
      }
    },

    async addOperation() {
      if (this.userPwd !== '') {
        this.data.userPwd = this.$md5(this.$md5(this.userPwd));
      } else {
        this.data.userPwd = null;
      }
      this.data.userAvatar = this.userAvatar;

      try {
        const response = await this.$axios.post('/user/insert', this.data);
        if (response.data.code === 200) {
          this.$message.success('User added successfully');
          this.cannel();
          this.fetchFreshData();
        } else {
          this.$message.error(response.data.msg || 'Add failed');
        }
      } catch (error) {
        console.error('Add information error:', error);
        this.$message.error('Submission failed, please try again later!');
      }
    },

    async fetchFreshData() {
      try {
        let startTime = '';
        let endTime = '';
        if (this.searchTime && this.searchTime.length === 2) {
          startTime = `${this.searchTime[0]}T00:00:00`;
          endTime = `${this.searchTime[1]}T23:59:59`;
        }

        const params = {
          current: this.currentPage,
          size: this.pageSize,
          key: this.userQueryDto.userName,
          startTime: startTime,
          endTime: endTime,
          ...this.userQueryDto
        };

        const response = await this.$axios.post('/user/query', params);
        const { data } = response;

        if (data.code === 200) {
          if (data.data.length === 0 && this.currentPage > 1) {
            this.currentPage--;
            this.fetchFreshData();
          } else {
            this.tableData = data.data;
            this.totalItems = data.total;
          }
        } else {
          this.$message.error(data.msg || 'Query failed');
        }
      } catch (error) {
        console.error('Error querying user information:', error);
        this.$message.error('Query operation error');
      }
    },

    add() {
      this.dialogUserOperaion = true;
      this.isOperation = false;
      this.data = {};
      this.userAvatar = '';
      this.userPwd = '';
    },

    handleFilter() {
      this.currentPage = 1;
      this.fetchFreshData();
    },

    handleFilterClear() {
      this.userQueryDto.userName = '';
      this.handleFilter();
    },

    handleReturn() {
      this.userQueryDto.userName = '';
      this.currentPage = 1;
      this.fetchFreshData();
    },

    handleCurrentChange(val) {
      this.currentPage = val;
      this.fetchFreshData();
    },

    handleEdit(row) {
      this.dialogUserOperaion = true;
      this.isOperation = true;
      this.userAvatar = row.userAvatar;
      this.data = { ...row };
      this.userPwd = '';
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
$light-bg: #f8f9fa;
$border-color: #e4e7ed;
$text-color: #333;
$text-light-color: #666;
$black: #000;

.user-management-container {
  padding: 25px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
  margin: 20px;

  .search-card {
    margin-bottom: 25px;
    border-radius: 8px;
    border: 1px solid $border-color;
    background-color: #fff;

    .search-content {
      display: flex;
      align-items: center;
      padding: 15px 0;

      .search-group {
        display: flex;
        align-items: center;
        position: relative;
        
        .el-input {
          width: 220px;
          --el-input-border-radius: 8px;
          
          ::v-deep .el-input__wrapper {
            border: 1px solid $black !important;
            box-shadow: none !important;
            outline: none !important;
            border-color: $black !important;
            position: relative;
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

        .return-btn {
          color: $black;
          font-size: 18px;
          padding: 6px;
          height: 35px;
          width: 35px;
          display: flex;
          align-items: center;
          justify-content: center;
          
          &:hover {
            color: #333;
            background-color: transparent;
          }
          
          ::v-deep .el-button--text {
            border: none !important;
            background: none !important;
            padding: 0;
          }
          
          ::v-deep .el-icon {
            margin-right: 0 !important;
          }
        }
      }

      .el-select,
      .el-date-picker {
        --el-select-border-radius: 8px;
        --el-datepicker-border-radius: 8px;

        ::v-deep .el-input__wrapper {
          border: 1px solid #ccc;
        }
      }

      .add-btn {
        --el-button-border-radius: 8px;
        background-color: $primary-color;
        border-color: $primary-color;
        padding: 8px 20px;

        &:hover {
          background-color: #395afc;
          border-color: #395afc;
        }
      }
    }
  }

  .user-cards-container {
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
    gap: 20px;
    margin-bottom: 25px;

    .user-card {
      border-radius: 12px;
      transition: all 0.3s ease;
      overflow: hidden;
      border: 1px solid $border-color;

      &:hover {
        transform: translateY(-5px);
        box-shadow: 0 8px 16px rgba(158, 174, 255, 0.15);
        border-color: rgba(158, 174, 255, 0.3);
      }

      .user-header {
        display: flex;
        align-items: center;
        margin-bottom: 15px;
        padding-bottom: 10px;
        border-bottom: 1px dashed $border-color;

        .el-avatar {
          width: 60px;
          height: 60px;
          border: 2px solid rgba(158, 174, 255, 0.2);
        }

        .user-info {
          margin-left: 15px;

          .user-name {
            font-weight: 500;
            font-size: 16px;
            color: $text-color;
          }

          .user-account {
            font-size: 12px;
            color: $text-light-color;
            margin-top: 4px;
          }
        }
      }

      .user-details {
        margin-bottom: 15px;

        .detail-item {
          display: flex;
          align-items: center;
          margin-bottom: 8px;
          font-size: 13px;
          color: $text-light-color;

          i {
            margin-right: 8px;
            color: $light-primary;
            font-size: 14px;
          }
        }
      }

      .user-status {
        display: flex;
        flex-wrap: wrap;
        gap: 8px;
        margin-bottom: 15px;
        padding-bottom: 10px;
        border-bottom: 1px dashed $border-color;

        .status-tag {
          --el-tag-border-radius: 6px;
          padding: 0 10px;
          height: 26px;
          line-height: 26px;
        }
      }

      .user-actions {
        display: flex;
        justify-content: flex-end;
        gap: 10px;

        .action-btn {
          width: 32px;
          height: 32px;
          display: flex;
          align-items: center;
          justify-content: center;
          border-radius: 50%;
          transition: all 0.2s;
        }

        .setting-btn {
          background-color: rgba(158, 174, 255, 0.1);
          color: $light-primary;
          border-color: transparent;

          &:hover {
            background-color: $light-primary;
            color: #fff;
          }
        }

        .edit-btn {
          background-color: rgba(255, 193, 7, 0.1);
          color: #ffb74d;
          border-color: transparent;

          &:hover {
            background-color: #ffb74d;
            color: #fff;
          }
        }

        .delete-btn {
          background-color: rgba(255, 107, 107, 0.1);
          color: $danger-color;
          border-color: transparent;

          &:hover {
            background-color: $danger-color;
            color: #fff;
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
    
    .user-pagination {
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

  .user-dialog, .status-dialog {
    ::v-deep .el-dialog {
      border-radius: 12px;
      box-shadow: 0 5px 20px rgba(0, 0, 0, 0.08);
    }

    ::v-deep .el-dialog__header {
      padding: 20px 20px 15px;
      border-bottom: 1px solid $border-color;

      .el-dialog__title {
        font-size: 18px;
        font-weight: 500;
        color: $text-color;
      }
    }

    ::v-deep .el-dialog__body {
      padding: 20px;
    }

    .form-input {
      --el-input-border-radius: 8px;
    }

    .dialog-footer {
      padding: 15px 20px 20px;
      border-top: 1px solid $border-color;
      text-align: right;

      .cancel-btn {
        --el-button-border-radius: 8px;
        margin-right: 10px;
        padding: 8px 20px;
      }

      .confirm-btn {
        --el-button-border-radius: 8px;
        background-color: $light-primary;
        border-color: $light-primary;
        padding: 8px 20px;

        &:hover {
          background-color: $primary-color;
          border-color: $primary-color;
        }
      }
    }
  }

  .avatar-uploader {
    ::v-deep .el-upload {
      border: 1px dashed $border-color;
      border-radius: 8px;
      cursor: pointer;
      position: relative;
      overflow: hidden;

      &:hover {
        border-color: $light-primary;
      }
    }

    .avatar-uploader-icon {
      font-size: 28px;
      color: #8c939d;
      width: 120px;
      height: 120px;
      line-height: 120px;
      text-align: center;
    }

    .avatar {
      width: 120px;
      height: 120px;
      display: block;
      border-radius: 8px;
      object-fit: cover;
    }
  }
}
</style>