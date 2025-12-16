<template>
    <div class="category-management-container">
        <div class="search-operation-area">
            <div class="search-group">
                <el-input 
                    v-model="categoryQueryDto.name" 
                    placeholder="Please enter product category" 
                    @keyup.enter="handleFilter" 
                    size="medium" 
                    class="search-input"
                    style="width: 250px; height: 35px;">
                    <template #suffix>
                        <i class="el-icon-search search-icon" @click="handleFilter"></i>
                    </template>
                </el-input>
                <el-button 
                    v-if="categoryQueryDto.name" 
                    icon="el-icon-refresh-left" 
                    @click="handleReturn"
                    size="medium"
                    class="return-btn"
                    type="text">
                </el-button>
            </div>
            <div class="operation-buttons">
                <el-button 
                    type="primary" 
                    icon="el-icon-plus" 
                    @click="add" 
                    size="medium"
                    class="add-btn">
                    Add Product Category
                </el-button>
                <el-button 
                    icon="el-icon-delete" 
                    @click="batchDelete" 
                    size="medium"
                    class="batch-delete-btn"
                    :disabled="!selectedRows.length">
                    Batch Delete
                </el-button>
            </div>
        </div>

        <div class="table-area">
            <el-table 
                :data="tableData" 
                stripe 
                border 
                style="width: 100%" 
                @selection-change="handleSelectionChange"
                class="category-table"
                :header-cell-style="{background: '#f8f9fa', color: '#333', fontWeight: '500'}">
                <el-table-column type="selection" width="55" align="center"></el-table-column>
                <el-table-column prop="id" label="ID" width="100" align="center"></el-table-column>
                <el-table-column prop="name" label="Product Category Name" min-width="200"></el-table-column>
                <el-table-column prop="isUse" label="Status" width="120" align="center">
                    <template #default="{ row }">
                        <el-tag 
                            :type="row.isUse ? 'success' : 'info'" 
                            size="medium"
                            class="status-tag">
                            {{ row.isUse ? 'Enabled' : 'Disabled' }}
                        </el-tag>
                    </template>
                </el-table-column>
                <el-table-column label="Operation" width="180" align="center">
                    <template #default="{ row }">
                        <el-button  
                            type="text" 
                            size="mini" 
                            icon="el-icon-edit" 
                            @click="handleEdit(row)"
                            class="edit-btn">
                            Edit
                        </el-button>
                        <el-button  
                            type="text" 
                            size="mini" 
                            icon="el-icon-delete" 
                            @click="handleDelete(row)"
                            class="delete-btn">
                            Delete
                        </el-button>
                    </template>
                </el-table-column>
            </el-table>
        </div>

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

        <el-dialog 
            title="Product Category Operation"
            :visible.sync="dialogCategoryOperation" 
            width="450px"
            :close-on-click-modal="false"
            class="category-dialog"
            :modal-style="{background: 'rgba(0, 0, 0, 0.1)'}">
            <el-form 
                :model="data" 
                label-width="120px" 
                label-position="right" 
                class="category-form"
                :rules="rules"
                ref="categoryForm">
                <el-form-item label="Product Category Name" prop="name">
                    <el-input 
                        v-model="data.name" 
                        placeholder="Please enter product category name" 
                        clearable 
                        maxlength="50"
                        show-word-limit
                        class="form-input">
                    </el-input>
                </el-form-item>
                <el-form-item label="Status" prop="isUse">
                    <el-switch 
                        v-model="data.isUse" 
                        active-text="Enabled" 
                        inactive-text="Disabled" 
                        active-color="#9EAEFF"
                        inactive-color="#909399">
                    </el-switch>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="cannel" size="medium" class="cancel-btn">Cancel</el-button>
                <el-button 
                    type="primary" 
                    @click="isOperation ? updateOperation() : addOperation()" 
                    size="medium"
                    class="confirm-btn">
                    Confirm
                </el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
export default {
    data() {
        return {
            data: {
                name: '',
                isUse: true
            },
            currentPage: 1,
            totalItems: 0,
            dialogCategoryOperation: false,
            isOperation: false,
            tableData: [],
            selectedRows: [],
            categoryQueryDto: {
                name: '',
                current: 1,
                size: 10  
            },
            rules: {
                name: [
                    { required: true, message: 'Please enter product category name', trigger: 'blur' },
                    { min: 2, max: 50, message: 'Length should be between 2 and 50 characters', trigger: 'blur' }
                ],
                isUse: [
                    { required: true, message: 'Please select status', trigger: 'change' }
                ]
            }
        }
    },
    created() {
        this.fetchFreshData()
    },
    methods: {
        cannel() {
            this.data = {
                name: '',
                isUse: true
            }
            this.dialogCategoryOperation = false
            this.isOperation = false
            this.$refs.categoryForm.resetFields()
        },
        async batchDelete() {
            if (!this.selectedRows.length) {
                this.$message.warning('Please select at least one record')
                return
            }

            try {
                const confirmed = await this.$confirm(
                    `Are you sure to delete the selected ${this.selectedRows.length} product category records? Deletion cannot be undone!`,
                    'Delete Confirmation',
                    {
                        confirmButtonText: 'Confirm',
                        cancelButtonText: 'Cancel',
                        type: 'warning',
                        confirmButtonColor: '#ff6b6b'
                    }
                )

                if (confirmed) {
                    const ids = this.selectedRows.map(item => item.id)
                    const response = await this.$axios.post('/category/batchDelete', ids)

                    if (response.data.code === 200) {
                        this.$message.success('Deleted successfully')
                        this.fetchFreshData()
                        this.selectedRows = []
                    } else {
                        this.$message.error(response.data.msg || 'Deletion failed')
                    }
                }
            } catch (error) {
                console.error('Product category deletion exception:', error)
                this.$message.error('Deletion operation exception')
            }
        },
        async updateOperation() {
            try {
                await this.$refs.categoryForm.validate()
                const response = await this.$axios.put('/category/update', this.data)
                
                if (response.data.code === 200) {
                    this.$message.success('Modified successfully')
                    this.fetchFreshData()
                    this.cannel()
                } else {
                    this.$message.error(response.data.msg || 'Modification failed')
                }
            } catch (error) {
                console.error('Product category modification exception:', error)
                this.$message.error('Modification operation exception')
            }
        },
        async addOperation() {
            try {
                await this.$refs.categoryForm.validate()
                const response = await this.$axios.post('/category/save', this.data)
                
                if (response.data.code === 200) {
                    this.$message.success('Added successfully')
                    this.fetchFreshData()
                    this.cannel()
                } else {
                    this.$message.error(response.data.msg || 'Addition failed')
                }
            } catch (error) {
                console.error('Product category addition exception:', error)
                this.$message.error('Addition operation exception')
            }
        },
        fetchFreshData() {
            this.categoryQueryDto.current = this.currentPage
            this.categoryQueryDto.size = 10

            this.$axios.post('/category/query', this.categoryQueryDto)
                .then(res => {
                    if (res.data.code === 200) {
                        this.tableData = res.data.data
                        this.totalItems = res.data.total
                    } else {
                        this.$message.error(res.data.msg || 'Query failed')
                    }
                })
                .catch(error => {
                    console.error('Product category query exception:', error)
                    this.$message.error('Query operation exception')
                })
        },
        add() {
            this.dialogCategoryOperation = true
            this.isOperation = false
        },
        handleFilter() {
            this.currentPage = 1
            this.fetchFreshData()
        },
        handleReturn() {
            this.categoryQueryDto.name = '' 
            this.currentPage = 1 
            this.fetchFreshData() 
        },
        handleCurrentChange(val) {
            this.currentPage = val
            this.fetchFreshData()
        },
        handleEdit(row) {
            this.dialogCategoryOperation = true
            this.isOperation = true
            this.data = { ...row }
        },
        handleDelete(row) {
            this.selectedRows = [row]
            this.batchDelete()
        },
        handleSelectionChange(rows) {
            this.selectedRows = rows
        }
    }
}
</script>

<style scoped lang="scss">
$primary-color: #4967ff;
$light-primary: #9EAEFF;
$danger-color: #ff6b6b;
$danger-light-color: rgba(255, 107, 107, 0.1); 
$danger-disabled-color: rgba(255, 107, 107, 0.05); 
$light-bg: #f8f9fa;
$border-color: #e4e7ed;
$text-color: #333;
$text-light-color: #666;
$black: #000; 

.category-management-container {
    padding: 25px;
    background-color: #fff;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    margin: 20px;

    .search-operation-area {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 25px;
        padding-bottom: 20px;
        border-bottom: 1px solid $border-color;

        .search-group {
            display: flex;
            align-items: center;
            gap: 10px; 

            .search-input {
                width: 200px; 
                --el-input-border-radius: 8px;
                
                ::v-deep .el-input__wrapper {
                    border: 1px solid $black !important;
                    box-shadow: none !important; 
                    outline: none !important; 
                    border-color: $black !important; 
                    position: relative;
                    padding-right: 35px !important; 
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

        .operation-buttons {
            display: flex;
            gap: 12px;

            .add-btn {
                --el-button-border-radius: 8px;
                background-color: $primary-color;
                border-color: $primary-color;
                padding: 8px 20px;
                transition: all 0.3s ease;
            }

            .batch-delete-btn {
                --el-button-border-radius: 8px;
                background-color: $danger-light-color;
                color: $danger-color;
                border-color: transparent;
                padding: 8px 20px;
                transition: all 0.3s ease;

                &:disabled {
                    background-color: $danger-disabled-color;
                    color: #ff9c9c;
                    border-color: transparent;
                    cursor: not-allowed;
                }

                &:hover:not(:disabled) {
                    background-color: $danger-color;
                    color: #fff;
                    border-color: transparent;
                }
            }
        }
    }

    .table-area {
        margin-bottom: 25px;

        .category-table {
            --el-table-border-radius: 8px;
            border-radius: 8px;
            overflow: hidden;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.04);

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

            .status-tag {
                --el-tag-border-radius: 6px;
                padding: 0 12px;
                display: inline-flex;
                align-items: center;
                justify-content: center;
                height: 28px;
                line-height: 28px;
                text-align: center;
            }

            .edit-btn {
                color: $light-primary;
                transition: all 0.3s ease;
                
                &:hover {
                    color: #8ca0ff;
                }
            }

            .delete-btn {
                color: $danger-color;
                transition: all 0.3s ease;
                
                &:hover {
                    color: #ff5252;
                    background-color: transparent;
                }
            }
        }
    }

    .pagination-area {
        display: flex;
        justify-content: center; 
        align-items: center; 
        margin-top: 15px;
        padding: 15px 20px 0;

        .category-pagination {
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

    .category-dialog {
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

        .category-form {
            .form-input {
                --el-input-border-radius: 8px;
                
                ::v-deep .el-input__wrapper {
                    border-radius: 8px;
                }
            }
        }

        .dialog-footer {
            padding: 15px 20px 20px;
            border-top: 1px solid $border-color;
            text-align: right;

            .cancel-btn {
                --el-button-border-radius: 8px;
                margin-right: 10px;
                padding: 8px 20px;
                transition: all 0.3s ease;
            }

            .confirm-btn {
                --el-button-border-radius: 8px;
                background-color: $light-primary;
                border-color: $light-primary;
                padding: 8px 20px;
                transition: all 0.3s ease;
                
                &:hover {
                    background-color: $primary-color;
                    border-color: $primary-color;
                }
            }
        }
    }
}
</style>