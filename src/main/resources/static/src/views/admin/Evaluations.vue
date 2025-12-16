<template>
    <div class="evaluation-management-container">
        <el-card class="search-card" shadow="never">
            <div class="search-content">
                <el-date-picker 
                    size="small" 
                    style="width: 400px; margin-right: 20px;" 
                    v-model="searchTime" 
                    type="daterange"
                    range-separator="to" 
                    start-placeholder="Comment Start" 
                    end-placeholder="Comment End" 
                    @change="handleFilter"
                    clearable>
                </el-date-picker>
                
                <div class="search-group-custom">
                    <el-input 
                        size="medium" 
                        style="width: 250px; height: 35px;"
                        v-model="evalustionsQueryDto.content" 
                        placeholder="Comment Content" 
                        clearable 
                        @keyup.enter.native="handleFilter"
                        class="search-input-custom">
                        <template #suffix>
                            <i class="el-icon-search search-icon-custom" @click="handleFilter"></i>
                        </template>
                    </el-input>
                    
                    <el-button 
                        v-if="evalustionsQueryDto.content || (searchTime && searchTime.length > 0)" 
                        icon="el-icon-refresh-left" 
                        @click="handleReturn"
                        size="medium"
                        class="return-btn-custom"
                        type="text">
                    </el-button>
                </div>
                
                <el-button 
                    icon="el-icon-delete" 
                    size="small" 
                    :disabled="selectedRows.length === 0"
                    @click="batchDelete"
                    class="batch-delete-btn">
                    Batch Delete ({{ selectedRows.length }})
                </el-button>
            </div>
        </el-card>

        <el-row class="data-table-area">
            <el-table 
                row-key="id" 
                @selection-change="handleSelectionChange" 
                :data="tableData" 
                style="width: 100%" 
                border 
                class="custom-table"
                :header-cell-style="{background: '#f7f9fc', color: '#333', fontWeight: '500'}">
                
                <el-table-column type="selection" width="55" align="center"></el-table-column>

                <el-table-column prop="content" label="Text" min-width="100" header-align="center">
                    <template slot-scope="scope">
                        <el-tooltip class="item" effect="dark" :content="scope.row.content" placement="bottom">
                            <div class="cell-content">{{ scope.row.content }}</div>
                        </el-tooltip>
                    </template>
                </el-table-column>
                
                <el-table-column prop="upvoteCount" width="80" label="Likes" align="center">
                    <template slot-scope="scope">
                        <span class="like-count">
                            {{ scope.row.upvoteCount !== null ? scope.row.upvoteCount : 0 }}
                        </span>
                    </template>
                </el-table-column>
                
                <el-table-column prop="contentType" width="140" label="Source" align="center">
                    <template slot-scope="scope">
                        <el-tag size="small" type="info" class="source-tag">{{ scope.row.contentType || 'Unknown' }}</el-tag>
                    </template>
                </el-table-column>
                
                <el-table-column prop="createTime" width="160" label="Commented On" align="center">
                    <template slot-scope="scope">
                        <span>{{ scope.row.createTime }}</span>
                    </template>
                </el-table-column>
                
                <el-table-column prop="userName" width="120" label="Commenter" align="center">
                    <template slot-scope="scope">
                        <span v-html="highlightKeyword(scope.row.userName)"></span>
                    </template>
                </el-table-column>
                
                <el-table-column prop="replierName" width="120" label="Replied To" align="center">
                    <template slot-scope="scope">
                        <span v-html="highlightKeyword(scope.row.replierName)"></span>
                    </template>
                </el-table-column>
                
                <el-table-column prop="parentId" width="100" label="Level" align="center">
                    <template slot-scope="scope">
                        <el-tag 
                            size="mini" 
                            :type="scope.row.parentId === null ? '' : 'warning'"
                            :color="scope.row.parentId === null ? 'rgba(73, 103, 255, 0.1)' : 'rgba(253, 199, 50, 0.1)'"
                            :style="{ 'color': scope.row.parentId === null ? '#4967ff' : '#fdc732' }"
                            effect="light">
                            {{ scope.row.parentId === null ? 'Parent' : 'Child' }}
                        </el-tag>
                    </template>
                </el-table-column>
                
                <el-table-column label="Operation" width="90px" fixed="right" align="center">
                    <template slot-scope="scope">
                        <el-tooltip content="Delete" placement="top">
                            <el-button 
                                type="default" 
                                icon="el-icon-delete" 
                                circle 
                                size="mini" 
                                @click="handleDelete(scope.row)" 
                                class="action-btn delete-btn-custom"></el-button>
                        </el-tooltip>
                    </template>
                </el-table-column>
            </el-table>
            
            <div class="pagination-area">
                <el-pagination 
                    @current-change="handleCurrentChange"
                    :current-page="currentPage" 
                    :page-size="pageSize"
                    layout="prev, pager, next"
                    :total="totalItems" 
                    class="category-pagination"
                    prev-text="<"
                    next-text=">"
                />
            </div>
        </el-row>
    </div>
</template>

<script>
import Editor from "@/components/Editor";
export default {
    components: {
        Editor
    },
    data() {
        return {
            data: { cover: '' },
            reportsDate: [],
            filterText: '',
            tableData: [],
            currentPage: 1,
            pageSize: 10, 
            totalItems: 0,
            dialogOperaion: false,
            isOperation: false,
            searchTime: [],
            selectedRows: [],
            html: {},
            fileList: [],
            dynamicTags: ['Health', 'Wellness'], 
            inputVisible: false,
            inputValue: '',
            coverDialog: false,
            commentDialog: false,
            evaluationsList: [],
            count: 0,
            evaluationsDelete: false,
            idToDelete: null,
            contentDialog: false,
            reportDialog: false,
            types: [],
            values: [],
            evalustionsQueryDto: { 
                content: ''
            },
        };
    },
    created() {
        this.fetchFreshData();
    },
    methods: {
        keySearch() {
            this.fetchFreshData();
        },
        reportsList(row) {
            this.reportDialog = true;
            this.reportList(row.id);
        },
        async reportList(id) {
            const response = await this.$axios(`/evaluations-reports/reportCount/${id}`);
            const { data } = response;
            this.reportsDate = data.data;
            this.types = this.reportsDate.map(entity => entity.name);
            this.values = this.reportsDate.map(entity => entity.count);
        },
        showContent(news) {
            this.data = news;
            this.contentDialog = true;
        },
        async removeEvaluations(id) {
            this.evaluationsDelete = true;
            this.idToDelete = id;
        },
        confirmDelete() {
            this.$axios.delete(`/evaluations/delete/${this.idToDelete}`).then((response) => {
                this.$message[response.data.code === 200 ? 'success' : 'error'](response.data.msg);
                if (response.data.code === 200) {
                    this.evaluationsDelete = false;
                    this.loadEvaluationsList();
                }
            }).catch((error) => {
                console.log(error);
            });
        },
        handleEvaluationsClose() {
            this.commentDialog = false;
        },
        commentList(news) {
            this.data = news;
            this.commentDialog = true;
            this.loadEvaluationsList();
        },
        async loadEvaluationsList() {
            try {
                const response = await this.$axios.get(`/evaluations/list/${this.data.id}/evaluations`);
                this.evaluationsList = response.data.data.data;
                this.count = response.data.data.count;
            } catch (error) {
                console.error(`Error loading comment list:`, error);
            }
        },
        showPic(news) {
            this.data = news;
            this.coverDialog = true;
        },
        handleClose(tag) {
            this.dynamicTags.splice(this.dynamicTags.indexOf(tag), 1);
        },
        showInput() {
            this.inputVisible = true;
            this.$nextTick(_ => {
                this.$refs.saveTagInput.$refs.input.focus();
            });
        },
        handleInputConfirm() {
            let inputValue = this.inputValue;
            if (inputValue) {
                this.dynamicTags.push(inputValue);
            }
            this.inputVisible = false;
            this.inputValue = '';
        },
        goBack() {
            this.data = {};
            this.dialogOperaion = false;
        },
        goBackPage() {
            this.data = {};
            this.commentDialog = false;
        },
        handleSelectionChange(selection) {
            this.selectedRows = selection;
        },
        async batchDelete() {
            if (!this.selectedRows.length) {
                this.$message(`No data selected`);
                return;
            }
            const confirmed = await this.$swalConfirm({
                title: 'Delete Comment Data',
                text: `This action cannot be undone. Continue?`,
                icon: 'warning',
            });
            if (confirmed) {
                try {
                    let ids = this.selectedRows.map(entity => entity.id);
                    const response = await this.$axios.post(`/evaluations/batchDelete`, ids);
                    if (response.data.code === 200) {
                        this.$swal.fire({
                            title: 'Delete Notice',
                            text: response.data.msg,
                            icon: 'success',
                            showConfirmButton: false,
                            timer: 2000,
                        });
                        this.fetchFreshData();
                        return;
                    }
                } catch (e) {
                    this.$swal.fire({
                        title: 'Error Notice',
                        text: e,
                        icon: 'error',
                        showConfirmButton: false,
                        timer: 2000,
                    });
                    console.error(`Error deleting comment info:`, e);
                }
            }
        },
        highlightKeyword(text) {
            if (text == null) {
                return;
            }
            const keyword = this.evalustionsQueryDto.content; 
            if (keyword) {
                const regex = new RegExp(keyword, 'gi');
                return text.replace(regex, '<span class="highlight-custom">$&</span>');
            }
            return text;
        },
        resetQueryCondition() {
            this.evalustionsQueryDto.content = '';
            this.searchTime = [];
            this.fetchFreshData();
        },
        handleReturn() {
            this.evalustionsQueryDto.content = '';
            this.searchTime = [];
            this.handleFilter();
        },
        async updateOperation() {
            try {
                const response = await this.$axios.put('/evaluations/update', this.data);
                this.$message[response.data.code === 200 ? 'success' : 'error'](response.data.msg);
                if (response.data.code === 200) {
                    this.closeDialog();
                    this.fetchFreshData();
                    this.clearFormData();
                }
            } catch (error) {
                console.error('Error submitting form:', error);
                this.$message.error('Submission failed, please try again later!');
            }
        },
        async addOperation() {
            this.data.tag = this.dynamicTags.join(',');
            try {
                const response = await this.$axios.post('/evaluations/save', this.data);
                this.$message[response.data.code === 200 ? 'success' : 'error'](response.data.msg);
                if (response.data.code === 200) {
                    this.closeDialog();
                    this.fetchFreshData();
                    this.clearFormData();
                }
            } catch (error) {
                console.error('Error submitting form:', error);
                this.$message.error('Submission failed, please try again later!');
            }
        },
        closeDialog() {
            this.dialogOperaion = false;
        },
        clearFormData() {
            this.data = {};
            this.html = '';
            this.fileList = [];
        },
        async fetchFreshData() {
            try {
                let startTime = '';
                let endTime = '';
                if (this.searchTime !== null && this.searchTime.length === 2) {
                    const [startDate, endDate] = this.searchTime;
                    startTime = `${new Date(startDate).toISOString().split('T')[0]}T00:00:00`;
                    endTime = `${new Date(endDate).toISOString().split('T')[0]}T23:59:59`;
                }
                const params = {
                    current: this.currentPage,
                    size: this.pageSize, 
                    startTime: startTime,
                    endTime: endTime,
                    ...this.evalustionsQueryDto,
                };
                let response = await this.$axios.post('/evaluations/query', params);
                const { data } = response;
                this.tableData = data.data;
                this.totalItems = data.total;
            } catch (error) {
                console.error('Fetch data failed:', error);
            }
        },
        add() {
            this.dialogOperaion = true;
        },
        handleFilter() {
            this.currentPage = 1;
            this.fetchFreshData();
        },
        handleFilterClear() {
            this.evalustionsQueryDto.content = ''
            this.currentPage = 1
            this.fetchFreshData()
        },
        handleCurrentChange(val) {
            this.currentPage = val;
            this.fetchFreshData();
        },
        handleEdit(row) {
            this.dialogOperaion = true;
            this.isOperation = true;
            this.data = row;
            console.log(this.data);
        },
        async handleDelete(row) {
            this.selectedRows = [row]; 
            this.batchDelete();
        }
    },
};
</script>
<style scoped lang="scss">
$primary-color: #4967ff;
$light-primary: #9EAEFF;
$danger-color: #ff6b6b;
$border-color: #e4e7ed;
$text-color: #333;
$highlight-bg: #9EAEFF;
$black: #000;
$shadow-light: 0 4px 16px rgba(0, 0, 0, 0.03);

.evaluation-management-container {
    padding: 20px;
    background-color: #fff;
    border-radius: 8px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);

    .search-card {
        margin-bottom: 20px;
        border-radius: 8px;
        border: 1px solid $border-color;
        
        ::v-deep .el-card__body {
            padding: 15px 20px;
        }

        .search-content {
            display: flex;
            align-items: center;
            gap: 20px; 

            .search-group-custom {
                display: flex;
                align-items: center;
                gap: 5px; 
                
                .search-input-custom {
                    width: 250px; 
                    height: 35px; 

                    ::v-deep .el-input__wrapper {
                        border: 1px solid $black !important;
                        box-shadow: none !important;
                        outline: none !important;
                        height: 35px !important; 
                        padding-right: 35px !important;
                    }
                    
                    .search-icon-custom {
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
                
                .return-btn-custom {
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
                    
                    ::v_deep .el-icon {
                        margin-right: 0 !important;
                    }
                }
            }

            ::v-deep .el-date-editor .el-range-separator,
            ::v-deep .el-range-input {
                border-radius: 4px;
                border-color: #dcdfe6;
            }
            
            .batch-delete-btn {
                border-radius: 8px;
                background-color: rgba(255, 107, 107, 0.1);
                color: $danger-color;
                border-color: transparent;
                padding: 8px 16px;
                transition: all 0.3s ease;

                &:disabled {
                    background-color: rgba(255, 107, 107, 0.05);
                    color: #ff9c9c;
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

    .data-table-area {
        margin: 0 5px;
        border-top: none; 

        .custom-table {
            border: 1px solid $border-color;
            border-radius: 8px;
            overflow: hidden;

            ::v-deep .el-table__header-wrapper th,
            ::v-deep .el-table__fixed-right .el-table__header-wrapper th {
                background-color: #f7f9fc !important; 
                color: $text-color !important;
                font-weight: 500;
                border-bottom: 1px solid $border-color;
                box-shadow: none !important;
            }

            ::v-deep .el-table__body-wrapper td,
            ::v-deep .el-table__fixed-right .el-table__fixed-body-wrapper tr td {
                background-color: #fff !important;
                color: $text-color;
                border-left: 1px solid $border-color !important;
            }
            
            ::v-deep .el-table__body-wrapper td:last-child,
            ::v-deep .el-table__fixed-right .el-table__body-wrapper .el-table__row td:last-child {
                .cell {
                    display: flex; 
                    align-items: center;
                    justify-content: center;
                    height: 100%;
                }
            }
            
            .cell-content {
                white-space: nowrap;
                overflow: hidden;
                text-overflow: ellipsis;
                max-width: 100%;
            }

            .like-count {
                font-weight: 600;
                color: $primary-color; 
            }

            .action-btn {
                width: 28px;
                height: 28px;
                display: inline-flex;
                align-items: center;
                justify-content: center; 
                border-radius: 50%;
                transition: all 0.2s;
                
                ::v-deep i { 
                    font-size: 14px; 
                    line-height: 1;  
                    margin: 0;       
                    padding: 0;      
                    display: block;  
                }
            }

            .delete-btn-custom {
                background-color: rgba(255, 107, 107, 0.1);
                color: $danger-color;
                border-color: transparent;
    
                &:hover {
                    background-color: $danger-color;
                    color: #fff;
                }
            }

            .highlight-custom {
                background-color: $highlight-bg;
                color: $text-color;
                padding: 1px 2px;
                border-radius: 2px;
            }
            
            .source-tag {
                background-color: #f0f2f5;
                color: #606266;
                border-color: #e4e7ed;
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
                    width: 32px;
                    height: 32px;
                    border-radius: 6px !important;
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
                        cursor: not-allowed;
                    }
                }
            }
        }
    }
}
</style>