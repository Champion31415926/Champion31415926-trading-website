<template>
  <div class="address-container">
    <el-card class="search-card">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="Default Address">
          <el-select
            v-model="queryParams.isDefault"
            placeholder="Please select"
            clearable
          >
            <el-option label="Yes" :value="true"></el-option>
            <el-option label="No" :value="false"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">Query</el-button>
          <el-button @click="resetSearch">Reset</el-button>
          <el-button type="success" @click="showAddDialog"
            >Add Address</el-button
          >
        </el-form-item>
      </el-form>
    </el-card>

    <el-card class="table-card">
      <el-table :data="addressList" border style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column
          prop="userId"
          label="User ID"
          width="100"
        ></el-table-column>
        <el-table-column
          prop="concatPerson"
          label="Recipient"
          width="120"
        ></el-table-column>
        <el-table-column
          prop="concatPhone"
          label="Contact Phone"
          width="150"
        ></el-table-column>
        <el-table-column
          prop="getAdr"
          label="Shipping Address"
        ></el-table-column>
        <el-table-column label="Default Address" width="120">
          <template slot-scope="scope">
            <el-tag :type="scope.row.isDefault ? 'success' : 'info'">
              {{ scope.row.isDefault ? "Yes" : "No" }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="Operation" width="280">
          <template slot-scope="scope">
            <el-button size="mini" @click="handleEdit(scope.row)"
              >Edit</el-button
            >
            <el-button
              size="mini"
              type="primary"
              @click="setDefault(scope.row.id)"
              :disabled="scope.row.isDefault"
            >
              Set as Default
            </el-button>
            <el-button
              size="mini"
              type="danger"
              @click="handleDelete(scope.row.id)"
              >Delete</el-button
            >
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
        :current-page="queryParams.current"
        :page-sizes="[10, 20, 30]"
        :page-size="queryParams.size"
        layout="total, sizes, prev, pager, next, jumper"
        :total="total"
        class="pagination"
      ></el-pagination>
    </el-card>

    <el-dialog :title="dialogTitle" :visible.sync="dialogVisible" width="50%">
      <el-form
        :model="addressForm"
        :rules="rules"
        ref="addressForm"
        label-width="100px"
      >
        <el-form-item label="Recipient" prop="concatPerson">
          <el-input v-model="addressForm.concatPerson"></el-input>
        </el-form-item>
        <el-form-item label="Contact Phone" prop="concatPhone">
          <el-input v-model="addressForm.concatPhone"></el-input>
        </el-form-item>
        <el-form-item label="Shipping Address" prop="getAdr">
          <el-input type="textarea" v-model="addressForm.getAdr"></el-input>
        </el-form-item>
        <el-form-item label="Default Address">
          <el-switch v-model="addressForm.isDefault"></el-switch>
        </el-form-item>
      </el-form>
      <span slot="footer" class="dialog-footer">
        <el-button @click="dialogVisible = false">Cancel</el-button>
        <el-button type="primary" @click="submitForm">Confirm</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "Address",
  data() {
    return {
      queryParams: {
        userId: null, 
        isDefault: null,
        current: 1,
        size: 10,
      },
      addressList: [],
      total: 0,
      dialogVisible: false,
      dialogTitle: "Add Address",
      addressForm: {
        id: null,
        userId: null,
        concatPerson: "",
        concatPhone: "",
        getAdr: "",
        isDefault: false,
      },
      rules: {
        concatPerson: [
          {
            required: true,
            message: "Please enter recipient's name",
            trigger: "blur",
          },
          {
            min: 2,
            max: 20,
            message: "Length must be 2 to 20 characters",
            trigger: "blur",
          },
        ],
        concatPhone: [
          {
            required: true,
            message: "Please enter contact phone number",
            trigger: "blur",
          },
          {
            pattern: /^1[3-9]\d{9}$/,
            message: "Please enter a valid mobile phone number",
            trigger: "blur",
          },
        ],
        getAdr: [
          {
            required: true,
            message: "Please enter shipping address",
            trigger: "blur",
          },
          {
            min: 5,
            max: 100,
            message: "Length must be 5 to 100 characters",
            trigger: "blur",
          },
        ],
      },
    };
  },
  created() {
    this.fetchAddressList();
  },
  methods: {
    fetchAddressList() {
      this.$axios
        .post("/address/queryUser", this.queryParams)
        .then((res) => {
          const { data } = res;
          if (data.code === 200) {
            this.addressList = data.data;
            this.total = data.total || 0;
          } else {
            this.addressList = [];
            this.total = 0;
          }
        })
        .catch((e) => {
          this.$message.error(
            "Failed to get address list, please check the interface and network"
          );
          console.error(e);
        });
    }, 
    handleSearch() {
      this.queryParams.current = 1;
      this.fetchAddressList();
    }, 
    resetSearch() {
      this.queryParams = {
        userId: this.queryParams.userId,
        isDefault: null,
        current: 1,
        size: 10,
      };
      this.fetchAddressList();
    }, 
    handleSizeChange(size) {
      this.queryParams.size = size;
      this.fetchAddressList();
    }, 
    handleCurrentChange(current) {
      this.queryParams.current = current;
      this.fetchAddressList();
    }, 
    showAddDialog() {
      this.dialogTitle = "Add Address";
      this.addressForm = {
        id: null,
        userId: this.queryParams.userId,
        concatPerson: "",
        concatPhone: "",
        getAdr: "",
        isDefault: false,
      };
      this.dialogVisible = true;
      this.$nextTick(() => {
        if (this.$refs.addressForm) {
          this.$refs.addressForm.clearValidate();
        }
      });
    }, 
    handleEdit(row) {
      this.dialogTitle = "Edit Address";
      this.addressForm = {
        id: row.id,
        userId: row.userId,
        concatPerson: row.concatPerson,
        concatPhone: row.concatPhone,
        getAdr: row.getAdr,
        isDefault: row.isDefault,
      };
      this.dialogVisible = true;
      this.$nextTick(() => {
        if (this.$refs.addressForm) {
          this.$refs.addressForm.clearValidate();
        }
      });
    }, 
    submitForm() {
      this.$refs.addressForm.validate((valid) => {
        if (valid) {
          const addressData = {
            ...this.addressForm,
          };

          const url = addressData.id ? "/address/update" : "/address/save";
          const method = addressData.id ? "put" : "post";
          const msg = addressData.id
            ? "Updated successfully"
            : "Added successfully";

          this.$axios[method](url, addressData)
            .then((res) => {
              this.handleResponse(res, msg);
            })
            .catch(() => {
              this.$message.error(
                addressData.id ? "Update failed" : "Add failed"
              );
            });
        }
      });
    }, 
    handleResponse(res, msg) {
      const { data } = res;
      if (data.code === 200) {
        this.$message.success(msg);
        this.dialogVisible = false;
        this.fetchAddressList();
      } else {
        this.$message.error(data.msg || "Operation failed");
      }
    }, 
    handleDelete(id) {
      this.$confirm("Are you sure to delete this address?", "Prompt", {
        confirmButtonText: "Confirm",
        cancelButtonText: "Cancel",
        type: "warning",
      })
        .then(() => {
          this.$axios
            .post("/address/batchDelete", [id])
            .then((res) => {
              this.handleResponse(res, "Deleted successfully");
            })
            .catch((e) => {
              this.$message.error("Deletion failed");
            });
        })
        .catch(() => {});
    }, 
    setDefault(id) {
      this.$confirm("Are you sure to set as default address?", "Prompt", {
        confirmButtonText: "Confirm",
        cancelButtonText: "Cancel",
        type: "warning",
      })
        .then(() => {
          const params = {
            id: id,
            isDefault: true,
          };
          this.$axios
            .put("/address/isDefault", params)
            .then((res) => {
              const { data } = res;
              if (data.code === 200) {
                this.$message.success("Set successfully");
                this.fetchAddressList();
              } else {
                this.$message.info(data.msg || "Setting failed");
              }
            })
            .catch((e) => {
              this.$message.error("Setting failed");
            });
        })
        .catch(() => {});
    },
  },
};
</script>

<style scoped>
.search-card {
  margin-bottom: 20px;
  padding: 20px;
}

.search-form {
  display: flex;
  align-items: center;
}

.table-card {
  margin-bottom: 20px;
}

.pagination {
  margin-top: 20px;
  text-align: right;
}
</style>
