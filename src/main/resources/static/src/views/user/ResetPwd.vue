<template>
    <div class="password-reset-container">
      <div class="password-form">
        <div class="form-item">
          <label class="form-label">*Original Password</label>
          <el-input
            v-model="oldPwd"
            type="password"
            placeholder="Please enter original password"
            show-password
            clearable
            class="password-input"
          ></el-input>
        </div>
  
        <div class="form-item">
          <label class="form-label">*New Password</label>
          <el-input
            v-model="newPwd"
            type="password"
            placeholder="Please enter new password"
            show-password
            clearable
            class="password-input"
          ></el-input>
        </div>
  
        <div class="form-item">
          <label class="form-label">*Confirm Password</label>
          <el-input
            v-model="againPwd"
            type="password"
            placeholder="Please re-enter new password"
            show-password
            clearable
            class="password-input"
            @keyup.enter.native="postInfo"
          ></el-input>
        </div>
  
        <div class="password-tips">
          <p>Password Requirements:</p>
          <ul>
            <li>8-20 characters in length</li>
            <li>Contains letters and numbers</li>
            <li>It is recommended to use special characters to increase security</li>
          </ul>
        </div>
  
        <div class="submit-btn">
          <el-button
            type="primary"
            round
            @click="postInfo"
            :loading="submitting"
            :disabled="!formValid"
          >
            Modify Now
          </el-button>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import { clearToken } from "@/utils/storage";
  
  export default {
    name: "ResetPwd",
    data() {
      return {
        oldPwd: "",
        newPwd: "",
        againPwd: "",
        submitting: false
      };
    },
    computed: {
      formValid() {
        return (
          this.oldPwd.length >= 8 &&
          this.newPwd.length >= 8 &&
          this.againPwd.length >= 8 &&
          this.newPwd === this.againPwd
        );
      }
    },
    methods: {
      async postInfo() {
        if (!this.validatePassword()) {
          return;
        }
  
        this.submitting = true;
  
        try {
          const userUpdatePwdDTO = {
            oldPwd: this.$md5(this.$md5(this.oldPwd)),
            newPwd: this.$md5(this.$md5(this.newPwd)),
            againPwd: this.$md5(this.$md5(this.againPwd))
          };
  
          const { data } = await this.$axios.put("/user/updatePwd", userUpdatePwdDTO);
  
          this.$notify({
            duration: 2000,
            title: "Modify Password",
            message: data.code === 200 ? "Modified successfully, please log in again" : data.msg,
            type: data.code === 200 ? "success" : "error"
          });
  
          if (data.code === 200) {
            clearToken();
            this.$router.push("/login");
          }
        } catch (error) {
          console.error("Failed to modify password:", error);
          this.$notify.error({
            title: "Error",
            message: "Failed to modify password, please try again later"
          });
        } finally {
          this.submitting = false;
        }
      },
  
      validatePassword() {
        if (this.oldPwd.length < 8) {
          this.$message.warning("The length of the original password cannot be less than 8 characters");
          return false;
        }
  
        if (this.newPwd.length < 8) {
          this.$message.warning("The length of the new password cannot be less than 8 characters");
          return false;
        }
  
        if (this.newPwd !== this.againPwd) {
          this.$message.warning("The two entries of the new password do not match");
          return false;
        }
  
        const hasNumber = /\d/.test(this.newPwd);
        const hasLetter = /[a-zA-Z]/.test(this.newPwd);
  
        if (!hasNumber || !hasLetter) {
          this.$message.warning("The password should contain letters and numbers");
          return false;
        }
  
        return true;
      }
    }
  };
  </script>
  
  <style scoped lang="scss">
  .password-reset-container {
    max-width: 500px;
    margin: 0 auto;
    padding: 30px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1);
  }
  
  .form-label {
    display: block;
    margin-bottom: 8px;
    font-size: 14px;
    color: #606266;
    font-weight: 500;
  }
  
  .form-item {
    margin-bottom: 20px;
  }
  
  .password-input {
    width: 100%;
  }
  
  .password-tips {
    margin: 20px 0;
    padding: 10px;
    background-color: #f8f8f8;
    border-radius: 4px;
    font-size: 13px;
    color: #909399;
  
    p {
      margin-bottom: 5px;
      font-weight: 500;
    }
  
    ul {
      margin: 0;
      padding-left: 20px;
    }
  }
  
  .submit-btn {
    text-align: center;
    margin-top: 30px;
  
    .el-button {
      width: 200px;
      padding: 12px 0;
      font-size: 16px;
    }
  }
  </style>