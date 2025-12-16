<template>
  <div class="user-profile-container">
    <div class="avatar-section">
      <label class="section-label">*Avatar</label>

      <div class="avatar-wrapper">
        <img
          :src="userAvatar || defaultAvatar"
          class="avatar-image"
          alt="User Avatar"
        />

        <el-upload
          class="avatar-uploader-camera"
          action="http://localhost:21090/api/trading-website/v1.0/file/upload"
          :show-file-list="false"
          :on-success="handleAvatarSuccess"
          :before-upload="beforeAvatarUpload"
        >
          <i class="el-icon-camera-solid"></i>
        </el-upload>
      </div>
    </div>

    <div class="info-section">
      <div class="form-item">
        <label class="section-label">*Nickname</label>
        <el-input
          v-model="userInfo.userName"
          placeholder="Please enter nickname"
          class="info-input"
          clearable
        >
        </el-input>
      </div>

      <div class="form-item">
        <label class="section-label">*Email</label>
        <el-input
          v-model="userInfo.userEmail"
          placeholder="Please enter email"
          class="info-input"
          clearable
        >
        </el-input>
      </div>
    </div>

    <div class="status-section">
      <div class="status-item">
        <div class="status-header">
          <label class="section-label">*Account Status</label>
          <el-tooltip
            effect="dark"
            content="Once banned, you cannot log in or use system functions"
            placement="right"
          >
            <i class="el-icon-info info-icon"></i>
          </el-tooltip>
        </div>
        <div class="status-value" :class="{ banned: userInfo.isLogin }">
          <i
            :class="
              userInfo.isLogin
                ? 'el-icon-warning-outline'
                : 'el-icon-circle-check'
            "
          ></i>
          {{ userInfo.isLogin ? "Banned" : "Normal" }}
        </div>
      </div>

      <div class="status-item">
        <div class="status-header">
          <label class="section-label">*Message Status</label>
          <el-tooltip
            effect="dark"
            content="After being muted, interactive functions are restricted"
            placement="right"
          >
            <i class="el-icon-info info-icon"></i>
          </el-tooltip>
        </div>
        <div class="status-value" :class="{ banned: userInfo.isWord }">
          <i
            :class="
              userInfo.isWord
                ? 'el-icon-warning-outline'
                : 'el-icon-circle-check'
            "
          ></i>
          {{ userInfo.isWord ? "Banned" : "Normal" }}
        </div>
      </div>
    </div>

    <div class="submit-section">
      <el-button
        type="primary"
        @click="postInfo"
        class="submit-button"
        :loading="submitting"
      >
        Modify Now
      </el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: "Self",
  data() {
    return {
      userInfo: {
        userName: "",
        userEmail: "",
        isLogin: false,
        isWord: false,
      },
      userAvatar: "",
      submitting: false,
      defaultAvatar:
        "https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png",
    };
  },
  created() {
    this.auth();
  },
  methods: {
    async postInfo(isAvatarUpdate = false) {
      this.submitting = true;

      try {
        const userUpdateDTO = {
          userAvatar: this.userAvatar,
          userName: this.userInfo.userName,
          userEmail: this.userInfo.userEmail,
        };

        const { data } = await this.$axios.put("/user/update", userUpdateDTO);

        const title = isAvatarUpdate ? "Avatar Update" : "Edit Personal Information";
        const message = data.code === 200 ? "Updated successfully" : "Update failed";

        this.$notify({
          position: "bottom-right",
          duration: 1000,
          title: title,
          message: message,
          type: data.code === 200 ? "success" : "error",
        });

        if (data.code === 200) {
          await this.auth();
        }
      } catch (error) {
        console.error("Failed to modify information:", error);
      } finally {
        this.submitting = false;
      }
    },

    async handleAvatarSuccess(res) {
      this.$notify({
        duration: 1500,
        position: "bottom-right",
        title: "Avatar Upload",
        message: res.code === 200 ? "Uploaded successfully" : "Upload failed",
        type: res.code === 200 ? "success" : "error",
      });

      if (res.code === 200) {
        this.userAvatar = res.data;
        await this.postInfo(true);
      }
    },

    beforeAvatarUpload(file) {
      const isJPG = file.type === "image/jpeg" || file.type === "image/png";
      const isLt2M = file.size / 1024 / 1024 < 2;

      if (!isJPG) {
        this.$message.error("The uploaded avatar image can only be in JPG/PNG format!");
      }
      if (!isLt2M) {
        this.$message.error("The uploaded avatar image size cannot exceed 2MB!");
      }
      return isJPG && isLt2M;
    },

    async auth() {
      try {
        const { data } = await this.$axios.get("/user/auth");

        if (data.code !== 200) {
          this.$router.push("/");
        } else {
          this.userInfo = data.data;
          this.userAvatar = data.data.userAvatar || this.defaultAvatar;
        }
      } catch (error) {
        console.error("Failed to get user information:", error);
        this.$router.push("/");
      }
    },
  },
};
</script>

<style scoped>
.user-profile-container {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
}

.section-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.avatar-section {
  margin-bottom: 20px;
  text-align: center;
}

.avatar-wrapper {
  display: inline-block;
  position: relative;
}

.avatar-image {
  width: 88px;
  height: 88px;
  border-radius: 50%;
  object-fit: cover;
  display: block;
}

.avatar-uploader-camera {
  position: absolute;
  bottom: 0;
  right: 0;
  background-color: #fff;
  width: 32px;
  height: 32px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  cursor: pointer;
}

.avatar-uploader-camera /deep/ .el-upload {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100%;
  border: none;
  border-radius: 50%;
}

.avatar-uploader-camera i {
  color: #409eff;
  font-size: 16px;
}

.info-section {
  margin-bottom: 20px;
}

.form-item {
  margin-bottom: 20px;
}

.info-input {
  width: 100%;
}

.status-section {
  margin-bottom: 30px;
}

.status-item {
  margin-bottom: 20px;
}

.status-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.info-icon {
  margin-left: 5px;
  color: #909399;
  cursor: pointer;
}

.status-value {
  font-size: 14px;
  color: #67c23a;
}

.status-value i {
  margin-right: 6px;
}

.status-value.banned {
  color: #f56c6c;
  text-decoration: underline;
  text-decoration-style: dashed;
}

.submit-section {
  text-align: center;
}

.submit-button {
  width: 200px;
  padding: 12px 0;
  font-size: 16px;
  border-radius: 20px;
}
</style>