<template>
  <div class="content-list-container">
    <div class="filter-section">
      <div class="filter-container">
        <div class="search-group" style="flex: 1; max-width: 400px;">
          <el-input 
            v-model="contentQueryDto.name" 
            placeholder="Search content title" 
            clearable 
            size="medium"
            class="search-input" 
            @clear="handleFilterClear" 
            @keyup.enter="fetchFreshData">
          </el-input>
          <i class="el-icon-search search-icon" @click="fetchFreshData"></i>
        </div>
      </div>
    </div>

    <div class="content-list">
      <el-empty v-if="contentList.length === 0 && !loading" description="No content available"></el-empty>
      
      <div class="loading-wrap" v-if="loading">
        <el-skeleton :rows="6" animated />
      </div>

      <transition-group key="model-1" v-else name="content-list" tag="div" class="content-grid">
        <div 
          v-for="content in contentList" 
          :key="content.id" 
          class="content-card">
          <div class="content-image-container" @click="viewContentDetail(content)">
            <img 
              :src="getCoverImage(content)" 
              :alt="content.name" 
              class="content-image"
              @error="handleImageError(content.id)">
            <div class="image-placeholder" v-if="!content.cover && imageErrorIds.includes(content.id)">
              <i class="el-icon-picture-outline"></i>
            </div>
          </div>

          <div class="content-info">
            <h3 class="content-title" @click="viewContentDetail(content)">{{ content.name }}</h3>

            <div class="author-info">
              <el-avatar :src="content.userAvatar || defaultAvatar" size="small"></el-avatar>
              <span class="author-name">{{ content.userName || 'Anonymous User' }}</span>
            </div>

            <div class="action-buttons">
              <el-button 
                size="mini" 
                type="danger" 
                class="delete-btn"
                @click.stop="confirmDelete(content)"
                icon="el-icon-delete">
                Delete
              </el-button>
            </div>
          </div>
        </div>
      </transition-group>
    </div>

    <div class="pagination-area" v-if="totalItems > 0">
      <el-pagination 
        @current-change="handleCurrentChange" 
        :current-page="currentPage"
        :page-size="pageSize" 
        layout="prev, pager, next"
        :total="totalItems" 
        class="content-pagination"
        prev-text="<"
        next-text=">">
      </el-pagination>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ContentList',
  data() {
    return {
      contentQueryDto: {
        name: ''
      },
      contentList: [],
      currentPage: 1,
      pageSize: 10,
      totalItems: 0,
      loading: false,
      imageErrorIds: [],
      defaultAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      defaultImage: 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg'
    };
  },
  created() {
    this.fetchFreshData();
  },
  methods: {
    viewContentDetail(content) {
      this.$router.push(`/content-detail?id=${content.id}`);
    },

    getCoverImage(content) {
      return content.cover || this.defaultImage;
    },

    handleImageError(contentId) {
      this.imageErrorIds.push(contentId);
    },

    handleFilterClear() {
      this.contentQueryDto.name = '';
      this.currentPage = 1;
      this.fetchFreshData();
    },

    handleCurrentChange(val) {
      this.currentPage = val;
      this.fetchFreshData();
      window.scrollTo({ top: 0, behavior: 'smooth' });
    },

    async fetchFreshData() {
      try {
        this.loading = true;
        const params = {
          current: this.currentPage,
          size: this.pageSize,
          name: this.contentQueryDto.name.trim()
        };
        const res = await this.$axios.post('/content/query', params, { timeout: 10000 });
        if (res.data.code === 200) {
          this.contentList = res.data.data || [];
          this.totalItems = res.data.total || 0;
          
          if (this.contentList.length === 0 && this.currentPage > 1) {
            this.currentPage--;
            this.fetchFreshData();
          }
        }
      } catch (error) {
        console.error("Content query exception：", error);
        this.$message.error(error.code === 'ECONNABORTED' ? 'Request timed out, please try again' : 'Failed to get content list');
      } finally {
        this.loading = false;
      }
    },

    confirmDelete(content) {
      this.$confirm(
        'Are you sure you want to delete this content? It cannot be recovered after deletion', 
        'Deletion Confirmation', 
        {
          confirmButtonText: 'Confirm',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }
      ).then(() => {
        this.deleteContent(content.id);
      }).catch(() => {
        this.$message.info('Deletion cancelled');
      });
    },

    async deleteContent(contentId) {
      try {
        this.loading = true;
        const res = await this.$axios.post('/content/batchDelete', [contentId]);
        if (res.data.code === 200) {
          this.$message.success('Deletion successful');
          const curLen = this.contentList.length;
          if (curLen === 1 && this.currentPage > 1) {
            this.currentPage--;
          }
          this.fetchFreshData();
        } else {
          this.$message.error(res.data.msg || 'Deletion failed');
        }
      } catch (error) {
        console.error("Content deletion exception：", error);
        this.$message.error('Failed to delete, please try again later');
      } finally {
        this.loading = false;
      }
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
$hover-shadow: 0 8px 20px rgba(158, 174, 255, 0.15);

.content-list-container {
  max-width: 1200px;
  margin: 20px auto;
  padding: 25px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

  .filter-section {
    background-color: #fff;
    border-radius: 8px;
    border: 1px solid $border-color;
    margin-bottom: 25px;
    padding: 20px;

    .filter-container {
      display: flex;
      align-items: center;
      gap: 15px;

      .search-group {
        position: relative;
        width: 100%;
        
        .search-input {
          --el-input-border-radius: 8px;
          
          ::v-deep .el-input__wrapper {
            border: 1px solid $black !important;
            box-shadow: none !important;
            outline: none !important;
            border-color: $black !important;
            padding-right: 40px !important;
            height: 40px;
          }
        }

        .search-icon {
          color: $black;
          font-size: 18px;
          cursor: pointer;
          position: absolute;
          right: 12px;
          top: 50%;
          transform: translateY(-50%);
          z-index: 2;
          width: 24px;
          height: 24px;
          display: flex;
          align-items: center;
          justify-content: center;
          
          &:hover {
            color: $primary-color;
          }
        }
      }
    }
  }

  .content-list {
    .loading-wrap {
      padding: 40px 0;
      ::v-deep .el-skeleton {
        width: 100%;
      }
    }

    .content-grid {
      display: grid;
      grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
      gap: 20px;
      margin-bottom: 25px;

      .content-list-move {
        transition: transform 0.3s ease;
      }
      .content-list-enter-active,
      .content-list-leave-active {
        transition: all 0.3s ease;
      }
      .content-list-enter,
      .content-list-leave-to {
        opacity: 0;
        transform: translateY(10px);
      }
    }

    .content-card {
      background-color: #fff;
      border-radius: 12px;
      overflow: hidden;
      box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
      transition: all 0.3s ease;
      border: 1px solid $border-color;

      &:hover {
        transform: translateY(-5px);
        box-shadow: $hover-shadow;
        border-color: rgba(158, 174, 255, 0.3);

        .content-image {
          transform: scale(1.03);
        }
      }

      .content-image-container {
        position: relative;
        height: 200px;
        overflow: hidden;
        background-color: #f5f7fa;
        cursor: pointer;

        .content-image {
          width: 100%;
          height: 100%;
          object-fit: cover;
          transition: transform 0.5s ease;
        }

        .image-placeholder {
          position: absolute;
          top: 0;
          left: 0;
          width: 100%;
          height: 100%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 48px;
          color: #ccc;
        }
      }

      .content-info {
        padding: 15px;

        .content-title {
          margin: 0 0 10px 0;
          font-size: 16px;
          color: $text-color;
          white-space: nowrap;
          overflow: hidden;
          text-overflow: ellipsis;
          cursor: pointer;
          transition: color 0.3s;

          &:hover {
            color: $primary-color;
          }
        }

        .author-info {
          display: flex;
          align-items: center;
          gap: 8px;
          margin-bottom: 15px;

          .author-name {
            font-size: 14px;
            color: $text-light-color;
          }
        }

        .action-buttons {
          display: flex;
          gap: 10px;
          justify-content: flex-end;
          padding-top: 10px;
          border-top: 1px dashed $border-color;

          .delete-btn {
            --el-button-border-radius: 8px;
            background-color: rgba(255, 107, 107, 0.1);
            color: $danger-color;
            border-color: transparent;
            padding: 6px 12px;

            &:hover {
              background-color: $danger-color;
              color: #fff;
            }
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
    
    .content-pagination {
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

@media (max-width: 768px) {
  .content-list-container {
    padding: 15px;
    margin: 10px;

    .filter-section {
      padding: 15px;

      .filter-container {
        flex-direction: column;
        align-items: stretch;

        .search-group {
          max-width: 100%;
        }
      }
    }

    .content-grid {
      grid-template-columns: 1fr !important;
      gap: 15px;
    }

    .content-card {
      .content-image-container {
        height: 180px;
      }
    }
  }
}

@media (max-width: 480px) {
  .content-list-container {
    padding: 10px;
    margin: 5px;
  }
}
</style>