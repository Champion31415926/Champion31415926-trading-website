<template>
  <div class="product-publish-container">
    <h1 class="publish-title">Publish Product</h1>

    <div class="publish-layout">
      <div class="form-section">
        <div class="form-card">
          <h2 class="card-title">Basic Information</h2>

          <div class="form-group">
            <label class="form-label">Product Name</label>
            <el-input
              v-model="product.name"
              placeholder="Please enter product name"
              class="form-input"
              clearable
            ></el-input>
          </div>

          <div class="form-group">
            <label class="form-label">Product Price</label>
            <el-input
              v-model="product.price"
              placeholder="Please enter product price"
              class="form-input"
              clearable
            >
              <template slot="prepend">¥</template>
            </el-input>
          </div>

          <div class="form-group">
            <label class="form-label">Condition</label>
            <div class="condition-slider">
              <el-slider
                v-model="oldLevel"
                :min="1"
                :max="10"
                show-stops
                @change="handleChange"
              ></el-slider>
              <div class="condition-value">{{ oldLevelText }}</div>
            </div>
          </div>

          <div class="form-group">
            <label class="form-label">Inventory</label>
            <el-input-number
              v-model="inventory"
              :min="1"
              :max="10000"
              @change="handleInventoryChange"
              class="inventory-input"
            ></el-input-number>
          </div>

          <div class="form-group">
            <label class="form-label">Support Bargain</label>
            <el-switch
              v-model="product.isBargain"
              active-text="Support Bargain"
              inactive-text="Not Support Bargain"
              active-color="#13ce66"
            ></el-switch>
          </div>
        </div>

        <div class="form-card">
          <h2 class="card-title">Category</h2>
          <div class="category-tags">
            <el-tag
              v-for="(category, index) in categoryList"
              :key="index"
              :type="categorySelected.id === category.id ? '' : 'info'"
              @click="categoryClick(category)"
              class="category-tag"
            >
              {{ category.name }}
            </el-tag>
          </div>
        </div>
      </div>

      <div class="upload-section">
        <div class="upload-card">
          <h2 class="card-title">Product Images</h2>
          <p class="upload-tip">
            Recommended to upload 1:1 ratio images, maximum 9 images
          </p>

          <el-upload
            action="http://localhost:21090/api/trading-website/v1.0/file/upload"
            list-type="picture-card"
            :limit="9"
            :on-success="handlePictureCardSuccess"
            :on-preview="handlePictureCardPreview"
            :on-remove="handleRemove"
            :file-list="fileList"
            class="image-uploader"
          >
            <i class="el-icon-plus"></i>
          </el-upload>

          <el-dialog :visible.sync="dialogVisible">
            < img width="100%" :src="dialogImageUrl" alt="">
          </el-dialog>
        </div>

        <div class="editor-card">
          <h2 class="card-title">Product Details</h2>
          <Editor
            height="300px"
            :receiveContent="product.detail"
            @on-receive="onReceive"
          />
        </div>

        <el-button
          type="primary"
          @click="postProduct"
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
  name: "PostProduct",
  data() {
    return {
      oldLevel: 9,
      inventory: 1,
      publishing: false,
      product: {
        name: "",
        price: "",
        oldLevel: 9,
        inventory: 1,
        isBargain: false,
        detail: "",
        categoryId: null,
        coverList: [],
      },
      categorySelected: {},
      dialogImageUrl: "",
      dialogVisible: false,
      coverList: [],
      categoryList: [],
      fileList: [],
      conditionMarks: {
        1: "Brand New",
        5: "Average",
        10: "Used",
      },
    };
  },
  computed: {
    oldLevelText() {
      if (this.oldLevel === 10) return "100% New";
      return `${this.oldLevel}0% New`;
    },
  },
  created() {
    this.fetchCategoryList();
  },
  methods: {
    async postProduct() {
      if (!this.validateForm()) return;

      this.publishing = true;
      this.product.coverList = this.coverList.join(",");
      this.product.oldLevel = this.oldLevel;
      this.product.inventory = this.inventory;

      try {
        const res = await this.$axios.post("/product/save", this.product);
        if (res.data.code === 200) {
          this.$notify.success({
            title: "Publish Success",
            message: res.data.msg,
            duration: 2000,
          });
          this.$router.push("/myProduct");
        }
      } catch (error) {
        this.$notify.error({
          title: "Publish Failed",
          message: error.message || "Error publishing product",
          duration: 2000,
        });
        console.error("新增商品异常:", error);
      } finally {
        this.publishing = false;
      }
    },

    validateForm() {
      if (!this.product.name) {
        this.$notify.warning({
          title: "Tips",
          message: "Please enter product name",
          duration: 1500,
        });
        return false;
      }

      if (!this.product.price || isNaN(this.product.price)) {
        this.$notify.warning({
          title: "Tips",
          message: "Please enter valid product price",
          duration: 1500,
        });
        return false;
      }

      if (this.coverList.length === 0) {
        this.$notify.warning({
          title: "Tips",
          message: "Please upload product images",
          duration: 1500,
        });
        return false;
      }

      if (!this.product.categoryId) {
        this.$notify.warning({
          title: "Tips",
          message: "Please select product category",
          duration: 1500,
        });
        return false;
      }

      return true;
    },

    onReceive(detail) {
      this.product.detail = detail;
    },

    categoryClick(category) {
      this.categorySelected = category;
      this.product.categoryId = category.id;
    },

    async fetchCategoryList() {
      try {
        const res = await this.$axios.post("/category/query", {});
        if (res.data.code === 200) {
          this.categoryList = res.data.data;
          if (this.categoryList.length > 0) {
            this.categoryClick(this.categoryList[0]);
          }
        }
      } catch (error) {
        console.error("查询商品类别信息异常:", error);
      }
    },

    handleChange() {
      this.product.oldLevel = this.oldLevel;
    },

    handleInventoryChange() {
      this.product.inventory = this.inventory;
    },

    handlePictureCardSuccess(file) {
      this.coverList.push(file.data);
    },

    handleRemove(file, fileList) {
      this.coverList = fileList.map((f) =>
        f.response ? f.response.data : f.url
      );
    },

    handlePictureCardPreview(file) {
      this.dialogImageUrl = file.url || (file.response && file.response.data);
      this.dialogVisible = true;
    },
  },
};
</script>

<style scoped lang="scss">
.product-publish-container {
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

  .form-card,
  .upload-card,
  .editor-card {
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
        background-color: #9eaeff;
        border-radius: 2px;
      }
    }
  }

  .form-group .condition-slider {
    ::v-deep .el-slider__bar {
      background-color: #9eaeff;
    }
    ::v-deep .el-slider__button {
      background-color: #ffffff;
      border-color: #9eaeff;
    }
  }
  ::v-deep .el-switch__label.is-active {
    color: #9eaeff;
  }
  ::v-deep .el-switch__label {
    color: #606266;
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

    .condition-slider {
      display: flex;
      align-items: center;
      gap: 20px;

      .el-slider {
        flex: 1;
      }

      .condition-value {
        min-width: 60px;
        text-align: center;
        font-size: 14px;
        color: #9eaeff;
        font-weight: 500;
      }
    }

    .inventory-input {
      width: 150px;
    }
  }

  .category-tags {
    display: flex;
    flex-wrap: wrap;
    gap: 10px;

    .category-tag {
      cursor: pointer;
      transition: all 0.3s;
      padding: 0 20px;
      height: 32px;
      line-height: 32px;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
      }
    }
  }

  .upload-tip {
    font-size: 12px;
    color: #909399;
    margin-bottom: 15px;
  }

  .editor-card {
    min-height: 380px;
  }

  .publish-button {
    width: 100%;
    height: 50px;
    font-size: 16px;
    margin-top: 20px;
    border-radius: 8px;
    background: linear-gradient(180deg, #cec3ff, #9eaeff);
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

.image-uploader ::v-deep .el-upload {
  width: 100px;
  height: 100px;
  line-height: 100px;
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  transition: all 0.3s;

  &:hover {
    border-color: #9eaeff;
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