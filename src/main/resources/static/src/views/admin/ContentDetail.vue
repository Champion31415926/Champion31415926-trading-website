<template>
  <div class="content-detail-container">
    <el-button 
      type="text" 
      icon="el-icon-arrow-left" 
      class="back-button"
      @click="$router.back()">
      Back to previous page
    </el-button>

    <div class="loading-container" v-if="loading">
      <el-skeleton :rows="8" animated />
    </div>

    <el-empty v-if="!loading && !content.id" description="Content not found"></el-empty>

    <div class="content-main" v-if="!loading && content.id">
      <div class="content-header">
        <h1 class="content-title">{{ content.name }}</h1>
        <div class="meta-info">
          <el-avatar :src="content.userAvatar || defaultAvatar" size="small" class="author-avatar"></el-avatar>
          <span class="author-name">{{ content.userName || 'Anonymous User' }}</span>
          <span class="create-time">{{ formatTime(content.createTime) }}</span>
        </div>
      </div>

      <div class="content-cover" v-if="content.cover">
        <img 
          :src="content.cover" 
          :alt="content.name" 
          class="cover-image"
          @error="handleCoverError">
      </div>

      <div class="content-body" v-html="content.detail"></div>

      <div class="interaction-section">
        <div 
          class="like-button" 
          @click="toggleLike"
          :class="{ liked: isLiked }">
          <i class="el-icon-heart" :class="{ liked: isLiked }"></i>
          <span>{{ isLiked ? content.likeNumber + 1 : content.likeNumber }}</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'ContentDetail',
  data() {
    return {
      content: {
        id: null,
        name: '',
        detail: '',
        cover: '',
        userAvatar: '',
        userName: '',
        createTime: '',
        likeNumber: 0
      },
      isLiked: false,
      loading: false,
      defaultAvatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
      defaultCover: 'https://fuss10.elemecdn.com/e/5d/4a731a90594a4af544c0c25941171jpeg.jpeg'
    }
  },
  created() {
    this.fetchContentDetail();
  },
  methods: {
    async fetchContentDetail() {
      const contentId = this.$route.query.id;
      if (!contentId) {
        this.$message.error('Content ID is missing');
        return this.$router.back();
      }

      try {
        this.loading = true;
        const res = await this.$axios.post('/content/query', { id: contentId }, { timeout: 10000 });
        if (res.data.code === 200 && res.data.data.length) {
          this.content = res.data.data[0];
          this.likeCount = this.content.likeNumber || 0;
        } else {
          this.$message.error(res.data.msg || 'Failed to get content details');
          this.$router.back();
        }
      } catch (error) {
        console.error("Content detail query exception:", error);
        this.$message.error(error.code === 'ECONNABORTED' ? 'Request timed out, please try again' : 'Failed to get content details');
        this.$router.back();
      } finally {
        this.loading = false;
      }
    },

    formatTime(time) {
      if (!time) return 'Unknown time';
      try {
        const date = new Date(time);
        return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
      } catch (e) {
        return 'Unknown time';
      }
    },

    handleCoverError(e) {
      e.target.src = this.defaultCover;
    },

    async toggleLike() {
      try {
        const res = await this.$axios.post('/content/like', { 
          id: this.content.id,
          action: this.isLiked ? 'unlike' : 'like'
        });
        
        if (res.data.code === 200) {
          this.isLiked = !this.isLiked;
          if (this.isLiked) {
            this.content.likeNumber = (this.content.likeNumber || 0) + 1;
          } else {
            this.content.likeNumber = Math.max(0, (this.content.likeNumber || 0) - 1);
          }
          this.$message.success(this.isLiked ? 'Liked successfully' : 'Unliked successfully');
        } else {
          this.$message.warning(res.data.msg || 'Operation failed');
        }
      } catch (error) {
        console.error("Like operation exception:", error);
        this.$message.error('Operation failed, please try again later');
      }
    }
  }
}
</script>

<style scoped lang="scss">
$primary-color: #4967ff;
$light-primary: #9EAEFF;
$danger-color: #ff6b6b;
$border-color: #e4e7ed;
$text-color: #333;
$text-light-color: #666;
$text-gray-color: #909399;
$hover-shadow: 0 8px 20px rgba(158, 174, 255, 0.15);

.content-detail-container {
  max-width: 800px;
  margin: 20px auto;
  padding: 25px;
  background-color: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

  .loading-container {
    padding: 40px 20px;
    ::v-deep .el-skeleton {
      width: 100%;
    }
  }

  .back-button {
    margin-bottom: 25px;
    color: $text-gray-color;
    font-size: 14px;
    transition: all 0.3s ease;
    --el-button-text-color: $text-gray-color;

    &:hover {
      color: $primary-color;
      --el-button-text-color: $primary-color;
      transform: translateX(-3px);
    }
  }

  .content-main {
    background-color: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    padding: 30px;
    border: 1px solid $border-color;

    .content-header {
      margin-bottom: 30px;

      .content-title {
        font-size: 24px;
        color: $text-color;
        margin-bottom: 15px;
        font-weight: 600;
        line-height: 1.4;
      }

      .meta-info {
        display: flex;
        align-items: center;
        gap: 15px;
        color: $text-gray-color;
        font-size: 14px;

        .author-avatar {
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
          --el-avatar-border-radius: 50%;
        }

        .author-name {
          color: $text-light-color;
          font-weight: 500;
        }

        .create-time {
          color: $text-gray-color;
        }
      }
    }

    .content-cover {
      margin-bottom: 30px;
      border-radius: 8px;
      overflow: hidden;
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);

      .cover-image {
        width: 100%;
        max-height: 400px;
        object-fit: cover;
        transition: transform 0.3s ease;

        &:hover {
          transform: scale(1.01);
        }
      }
    }

    .content-body {
      line-height: 1.8;
      color: $text-color;
      margin-bottom: 40px;
      font-size: 15px;

      ::v-deep img {
        max-width: 100%;
        border-radius: 8px;
        margin: 15px 0;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }

      ::v-deep p {
        margin-bottom: 1.5em;
      }

      ::v-deep h1, ::v-deep h2, ::v-deep h3 {
        margin: 1.5em 0 0.8em;
        color: $text-color;
        font-weight: 600;
      }

      ::v-deep a {
        color: $primary-color;
        text-decoration: underline;
        &:hover {
          color: #395afc;
        }
      }
    }

    .interaction-section {
      border-top: 1px solid $border-color;
      padding-top: 20px;
      display: flex;
      justify-content: center;

      .like-button {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 8px 24px;
        border-radius: 20px;
        background-color: #f5f7fa;
        cursor: pointer;
        transition: all 0.3s ease;
        user-select: none;
        border: 1px solid transparent;

        &:hover {
          background-color: rgba(255, 107, 107, 0.08);
          transform: translateY(-2px);
          border-color: rgba(255, 107, 107, 0.2);
        }

        &.liked {
          background-color: rgba(255, 107, 107, 0.1);
        }

        i {
          font-size: 18px;
          color: $text-gray-color;
          transition: all 0.3s ease;

          &.liked {
            color: $danger-color;
            transform: scale(1.1);
          }
        }

        span {
          font-size: 14px;
          color: $text-light-color;
          font-weight: 500;
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .content-detail-container {
    padding: 15px;
    margin: 10px;

    .content-main {
      padding: 20px;

      .content-header {
        .content-title {
          font-size: 20px;
        }
      }

      .content-cover {
        margin-bottom: 20px;

        .cover-image {
          max-height: 300px;
        }
      }

      .content-body {
        font-size: 14px;
        line-height: 1.7;
      }
    }
  }
}

@media (max-width: 480px) {
  .content-detail-container {
    padding: 10px;
    margin: 5px;

    .content-main {
      padding: 15px;
    }
  }
}
</style>