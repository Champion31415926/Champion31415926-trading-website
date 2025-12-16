<template>
  <div class="content-publish-container">
    <h1 class="publish-title">Publish Content</h1>

    <div class="publish-layout">
      <div class="form-section">
        <div class="form-card">
          <h2 class="card-title">Basic Content Information</h2>

          <div class="form-group">
            <label class="form-label">Title</label>
            <el-input
              v-model="content.name"
              placeholder="Please enter title"
              class="form-input"
              clearable
            ></el-input>
          </div>
        </div>
      </div>

      <div class="upload-section">
        <div class="form-card">
          <h2 class="card-title">Cover Image</h2>
          <el-upload
            class="avatar-uploader"
            action="http://localhost:21090/api/trading-website/v1.0/file/upload"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
          >
            <img v-if="content.cover" :src="content.cover" class="avatar" />
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </div>

        <div class="form-card">
          <h2 class="card-title">Content Details</h2>
          <Editor
            height="400px"
            :receiveContent="content.detail"
            @on-receive="onReceive"
          />
        </div>

        <el-button
          type="primary"
          @click="postContent"
          class="publish-button"
          :loading="publishing"
        >
          Publish Now
        </el-button>
      </div>
    </div>
  </div>
</template>

<script>
import Editor from "@/components/Editor";

export default {
  components: { Editor },
  name: "PostContent",
  data() {
    return {
      publishing: false,
      content: {
        name: "",
        detail: "",
        cover: "",
      },
    };
  },
  methods: {
    handleAvatarSuccess(res) {
      if (res.code === 200) {
        this.content.cover = res.data;
        this.$message.success("Cover image uploaded successfully");
      } else {
        this.$message.error("Upload failed");
      }
    },

    async postContent() {
      if (!this.content.name) {
        this.$message.warning("Please enter title");
        return;
      }

      if (!this.content.cover) {
        this.$message.warning("Please upload cover image");
        return;
      }

      this.publishing = true;
      try {
        const res = await this.$axios.post("/content/save", this.content);
        if (res.data.code === 200) {
          this.$message.success("Published successfully");
          this.$router.push("/mycontent");
        } else {
          this.$message.warning(res.data.msg || "Publish failed");
        }
      } catch (error) {
        console.error("Exception in publishing content:", error);
        this.$message.error("Publish failed");
      } finally {
        this.publishing = false;
      }
    },

    onReceive(detail) {
      this.content.detail = detail;
    },
  },
};
</script>

<style scoped lang="scss">
.content-publish-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;

  .publish-title {
    font-size: 24px;
    color: #333;
    margin-bottom: 30px;
    text-align: center;
    font-weight: 600;
  }

  .publish-layout {
    display: flex;
    gap: 20px;

    .form-section {
      flex: 1;
      min-width: 400px;
    }

    .upload-section {
      flex: 1;
      min-width: 400px;
    }
  }

  .form-card {
    background-color: #fff;
    border-radius: 12px;
    padding: 20px;
    margin-bottom: 20px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

    .card-title {
      font-size: 18px;
      color: #333;
      margin-bottom: 20px;
      font-weight: 600;
      position: relative;
      padding-left: 10px;

      &::before {
        content: "";
        position: absolute;
        left: 0;
        top: 4px;
        height: 16px;
        width: 4px;
        background-color: #409eff;
        border-radius: 2px;
      }
    }
  }

  .form-group {
    margin-bottom: 20px;

    .form-label {
      display: block;
      font-size: 14px;
      color: #606266;
      margin-bottom: 10px;
      font-weight: 500;
    }

    .form-input {
      width: 100%;
    }
  }

  .avatar-uploader {
    display: flex;
    justify-content: center;
    margin-bottom: 20px;

    .avatar {
      width: 200px;
      height: 150px;
      object-fit: cover;
      border-radius: 6px;
    }

    .avatar-uploader-icon {
      width: 200px;
      height: 150px;
      line-height: 150px;
      text-align: center;
      border: 1px dashed #d9d9d9;
      border-radius: 6px;
      color: #8c939d;
      font-size: 28px;
      transition: all 0.3s;

      &:hover {
        border-color: #409eff;
      }
    }
  }

  .publish-button {
    width: 100%;
    height: 50px;
    font-size: 16px;
    margin-top: 20px;
    border-radius: 8px;
    background: linear-gradient(90deg, #409eff, #66b1ff);
    border: none;
    box-shadow: 0 4px 12px rgba(64, 158, 255, 0.3);
    transition: all 0.3s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 6px 16px rgba(64, 158, 255, 0.4);
    }

    &:active {
      transform: translateY(0);
    }
  }
}

@media (max-width: 992px) {
  .publish-layout {
    flex-direction: column;

    .form-section,
    .upload-section {
      min-width: 100% !important;
    }
  }
}
</style>