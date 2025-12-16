<template>
    <div style="width: 100%;">
        <el-row style="margin: 5px 0;">
            <h2 class="commentHeader">Comments&nbsp;{{ evaluationsCount }}</h2>
            
            <el-row style="margin: 15px 0; display: flex; align-items: flex-start; gap: 8px;">
                <el-col :span="2">
                    <el-avatar 
                        :src="userData.userAvatar" 
                        size="large" 
                        shape="circle"
                        style="border-radius: 50%; margin-left: -10px;" 
                    ></el-avatar>
                </el-col>
                <el-col :span="22" style="width: 100%;">
                    <div class="parent-comment"
                        :style="{ 
                            backgroundColor: '#fff',          
                            border: '1px solid #CEC3FF',      
                            borderRadius: '4px',              
                            position: 'relative',
                            height: '36px'                    
                        }">
                        <input 
                            class="comment-parent-input" 
                            v-model="content" 
                            placeholder="TYPE YOUR COMMENT HERE..."
                            @focus="onFocus"
                            @blur="onBlur"
                            type="text"
                            style="
                                width: 100%;
                                height: 100%;
                                padding: 0 40px 0 12px;
                                border: none;
                                outline: none;
                                background: transparent;
                                font-size: 14px;
                                box-sizing: border-box;
                            "
                        />
                        <el-button
                            :style="{ 
                                position: 'absolute',
                                right: '4px',
                                top: '50%',
                                transform: 'translateY(-50%)',
                                backgroundColor: '#CEC3FF',  
                                borderColor: '#CEC3FF',      
                                color: '#fff',               
                                width: '28px',
                                height: '28px',
                                padding: 0,
                                borderRadius: '3px'
                            }"
                            @click="commentClick" 
                            size="mini" 
                            type="primary"
                            :disabled="!content.trim()"
                        >
                            <span style="font-size: 16px; font-weight: 600;">></span>
                        </el-button>
                    </div>
                </el-col>
            </el-row>
        </el-row>
        
        <el-row style="margin: 5px 0px;">
            <el-row v-for="(comment, index) in commentList" :key="index" style="padding: 10px 0;">
                <el-row style="display: flex; align-items: center; gap: 8px;">
                    <el-col :span="2">
                        <el-avatar 
                            size="large" 
                            :src="comment.userAvatar"
                            style="margin-left: -10px;" 
                        ></el-avatar>
                    </el-col>
                    <el-col :span="22">
                        <span style="font-size: 16px;color: #515767;">{{ comment.userName }}</span>
                        <span v-if="comment.userId === userId" class="my-body-tag">Myself</span>
                    </el-col>
                </el-row>
                
                <el-row style="padding: 8px 0;">
                    <el-col :span="22" :offset="2">
                        <span style="font-size: 16px;color: #252933;">{{ comment.content }}</span>
                    </el-col>
                </el-row>
                
                <el-row style="padding: 8px 0;">
                    <el-col :span="22" :offset="2">
                        <div style="display: flex; align-items: center;">
                            
                            <span style="font-size: 14px;color: #8A919F; margin-right: 30px;">{{ comment.time }}</span>

                            <span @click="upvote(comment)"
                                style="cursor: pointer;font-size: 14px;color: #8A919F;user-select: none; display: flex; align-items: center;">
                                <span>
                                    <span v-if="!comment.upvoteFlag" style="display: flex; align-items: center;">
                                        <img src="/upvote_default.png"
                                            style="height: 15px;width: 15px;" alt="">
                                        &nbsp;{{ comment.upvoteCount === null ? 0 : comment.upvoteCount }}
                                    </span>
                                    <span v-else style="display: flex; align-items: center;">
                                        <img src="/upvote_active.png"
                                            style="height: 15px;width: 15px;" alt="">
                                        &nbsp;{{ comment.upvoteCount }}
                                    </span>
                                </span>
                            </span>
                            
                            <span 
                                @click="toggleReplyInput(comment)"
                                style="cursor: pointer; font-size: 14px; color: #8A919F; user-select: none; margin-left: 15px;"
                            >
                                <i class="el-icon-chat-dot-round"></i>
                                Reply<span v-if="comment.childTotal > 0">({{ comment.childTotal }})</span>
                            </span>
                            
                            <span v-if="comment.userId === userId" @click="deleteComment(comment)"
                                style="cursor: pointer;font-size: 14px;color: #8A919F;user-select: none; margin-left: 15px;">
                                <i class="el-icon-delete"></i>
                                Delete
                            </span>
                            
                        </div>
                    </el-col>
                </el-row>
                
                <el-row v-if="comment.showReplyInput" style="padding: 10px 0;">
                    <el-col :span="22" :offset="2">
                        <div class="parent-comment"
                            :style="{ 
                                backgroundColor: '#fff',          
                                border: '1px solid #CEC3FF',      
                                borderRadius: '4px',              
                                position: 'relative',
                                height: '110px'                   
                            }">
                            <textarea 
                                class="comment-parent-input" 
                                v-model="replyContent" 
                                placeholder="I want to reply"
                                style="
                                    width: 100%;
                                    height: 100%;                    
                                    padding: 0 40px 0 12px;          
                                    border: none;
                                    outline: none;
                                    background: transparent;         
                                    font-size: 14px;                 
                                    box-sizing: border-box;
                                    resize: none;                    
                                "
                            ></textarea>
                            <el-button
                                :style="{ 
                                    position: 'absolute',
                                    right: '4px',                    
                                    top: '50%',                      
                                    transform: 'translateY(-50%)',   
                                    backgroundColor: '#CEC3FF',  
                                    borderColor: '#CEC3FF',      
                                    color: '#fff',               
                                    width: '28px',
                                    height: '28px',
                                    padding: 0,
                                    borderRadius: '3px'
                                }"
                                @click="submitReply(comment)" 
                                size="mini" 
                                type="primary"
                                :disabled="!replyContent.trim()"    
                            >
                                <span style="font-size: 16px; font-weight: 600;">></span>
                            </el-button>
                        </div>
                    </el-col>
                </el-row>
                
                <el-row 
                    v-for="(commentChild, childIndex) in comment.commentChildVOS" 
                    :key="childIndex"
                    style="padding: 10px 15px; font-size: 16px;"
                >
                    <el-row>
                        <el-col :span="22" :offset="2">
                            <el-row style="display: flex; align-items: center; gap: 8px; flex-wrap: wrap;">
                                <el-avatar 
                                    size="small" 
                                    :src="commentChild.userAvatar"
                                    style="margin-right: 0;"
                                ></el-avatar>
                                <span style="color: #515767; padding: 0 5px;">{{ commentChild.userName }}</span>
                                <span v-if="commentChild.userId === userId" class="my-body-tag">Myself</span>
                                
                                <span v-if="commentChild.replierName"
                                    style="margin: 0 5px; color: #1c1c1c; user-select: none; font-size: 12px;"
                                >
                                    Reply to
                                </span>
                                <el-avatar 
                                    v-if="commentChild.replierName"
                                    size="small" 
                                    :src="commentChild.replierAvatar"
                                    style="margin-right: 0;"
                                ></el-avatar>
                                <span v-if="commentChild.replierName" style="color: #515767; padding: 0 5px;">
                                    {{ commentChild.replierName }}
                                </span>
                                <span v-if="commentChild.replierId === userId" class="my-body-tag">Myself</span>
                                
                                <span style="letter-spacing: 1px; font-size: 16px; color: #252933; white-space: normal; margin-left: 5px; padding: 6px 0;">
                                    : {{ commentChild.content }}
                                </span>
                            </el-row>
                            
                            <el-row style="padding: 10px 0; display: flex; align-items: center;">
                                
                                <span style="font-size: 14px; color: #8A919F; margin-right: 30px;">{{ commentChild.time }}</span>

                                <span @click="upvote(commentChild)"
                                    style="cursor: pointer;font-size: 14px;color: #8A919F;user-select: none; display: inline-flex; align-items: center;">
                                    <span>
                                        <span v-if="!commentChild.upvoteFlag" style="display: flex; align-items: center;">
                                            <img src="/upvote_default.png"
                                                style="height: 15px;width: 15px;" alt="">
                                            &nbsp;{{ commentChild.upvoteCount === null ? 0 : commentChild.upvoteCount }}
                                        </span>
                                        <span v-else style="display: flex; align-items: center;">
                                            <img src="/upvote_active.png"
                                                style="height: 15px;width: 15px;" alt="">
                                            &nbsp;{{ commentChild.upvoteCount }}
                                        </span>
                                    </span>
                                </span>
                                
                                <span 
                                    @click="toggleReplyInput1(commentChild)"
                                    style="cursor: pointer;font-size: 14px;color: #8A919F;user-select: none; margin-left: 15px;">
                                    <i class="el-icon-chat-dot-round"></i>
                                    Reply
                                </span>
                                
                                <span v-if="commentChild.userId === userId" @click="deleteCommentChild(commentChild)"
                                    style="cursor: pointer;font-size: 14px;color: #8A919F;user-select: none; margin-left: 15px;">
                                    <i class="el-icon-delete"></i>
                                    Delete
                                </span>
                                
                            </el-row>
                            
                            <el-row v-if="commentChild.replyInputStatus" style="padding: 10px 0;">
                                <el-col :span="24">
                                    <div class="parent-comment"
                                        :style="{ 
                                            backgroundColor: '#fff',          
                                            border: '1px solid #CEC3FF',      
                                            borderRadius: '4px',              
                                            position: 'relative',
                                            height: '110px'                   
                                        }">
                                        <textarea 
                                            class="comment-parent-input" 
                                            v-model="replyChildContent" 
                                            placeholder="I want to reply"
                                            style="
                                                width: 100%;
                                                height: 100%;
                                                padding: 0 40px 0 12px;          
                                                border: none;
                                                outline: none;
                                                background: transparent;
                                                font-size: 14px;
                                                box-sizing: border-box;
                                                resize: none;
                                            "
                                        ></textarea>
                                        <el-button
                                            :style="{ 
                                                position: 'absolute',
                                                right: '4px',
                                                top: '50%',
                                                transform: 'translateY(-50%)',
                                                backgroundColor: '#CEC3FF',  
                                                borderColor: '#CEC3FF',      
                                                color: '#fff',               
                                                width: '28px',
                                                height: '28px',
                                                padding: 0,
                                                borderRadius: '3px'
                                            }"
                                            @click="submitReply1(commentChild)" 
                                            size="mini" 
                                            type="primary"
                                            :disabled="!replyChildContent.trim()"  
                                        >
                                            <span style="font-size: 16px; font-weight: 600;">></span>
                                        </el-button>
                                    </div>
                                </el-col>
                            </el-row>
                        </el-col>
                    </el-row>
                </el-row>
            </el-row>
        </el-row>
    </div>
</template>

<script>
import { timeAgo } from '@/utils/data'
export default {
    props: {
        contentId: {
            type: Number,
            required: true
        },
        contentType: {
            type: String,
            default: ''
        }
    },
    data() {
        return {
            userData: {},
            content: '',
            commentList: [],
            replyContent: '',
            replyChildContent: '',
            userId: '',
            evaluationsCount: 0,
            isFocused: false
        };
    },
    watch: {
        contentId(newVal, oldVal) {
            if (newVal !== oldVal) {
                this.loadCommentList();
            }
        },
        content() {
            if (!this.content.trim()) {
                this.isFocused = false;
            }
        }
    },
    created() {
        this.getUserInfo();
        this.loadCommentList();
    },
    methods: {
        getUserInfo() {
            const userInfo = sessionStorage.getItem('userInfo');
            if (userInfo) {
                this.userData = JSON.parse(userInfo);
                this.userId = this.userData.id || '';
            }
        },
        deleteComment(comment) { 
            this.$axios.delete(`evaluations/delete/${comment.id}`).then(res => {
                if (res.data.code === 200) {
                    this.$message.success(res.data.msg);
                    this.loadCommentList();
                }
            }).catch(err => {
                console.error('Comment delete exception -> ', err);
            })
        },
        onFocus() {
            this.isFocused = true;
        },
        onBlur() {
            if (!this.content.trim()) {
                this.isFocused = false;
            }
        },
        commentClick() {
            if (!this.content.trim()) {
                this.$swal.fire({
                    title: 'Content Prompt',
                    text: 'Comment content is empty',
                    icon: 'warning',
                    showConfirmButton: false,
                    timer: 800
                });
                return;
            }
            const evaluations = {
                contentType: this.contentType,
                content: this.content,
                contentId: this.contentId,
            }
            this.$axios.post(`evaluations/insert`, evaluations).then(res => {
                if (res.data.code === 200) {
                    this.content = '';
                    this.$swal.fire({
                        title: 'Comment Operation',
                        text: 'Comment successful',
                        icon: 'success',
                        showConfirmButton: false,
                        timer: 1100
                    });
                    setTimeout(() => this.loadCommentList(), 1100);
                } else {
                    this.$swal.fire({
                        title: 'Comment Exception',
                        text: res.data.msg,
                        icon: 'error',
                        showConfirmButton: false,
                        timer: 1100
                    });
                }
            }).catch(err => {
                console.error('Comment insert exception -> ', err);
            })
        },
        toggleReplyInput(comment) {
            comment.showReplyInput = !comment.showReplyInput;
        },
        toggleReplyInput1(commentChild) {
            commentChild.replyInputStatus = !commentChild.replyInputStatus;
        },
        submitReply(comment) {
            if (!this.replyContent.trim()) {
                this.$message.warning('Comment content cannot be empty');
                return;
            }
            const evaluationsDTO = {
                contentType: this.contentType,
                content: this.replyContent,
                contentId: this.contentId,
                parentId: comment.id
            }
            this.$axios.post(`evaluations/insert`, evaluationsDTO).then(res => {
                if (res.data.code === 200) {
                    this.replyContent = '';
                    comment.showReplyInput = false;
                    this.$swal.fire({
                        title: 'Reply Operation',
                        text: 'Reply successful',
                        icon: 'success',
                        showConfirmButton: false,
                        timer: 1300
                    });
                    setTimeout(() => this.loadCommentList(), 1300);
                } else {
                    this.$swal.fire({
                        title: 'Comment Exception',
                        text: res.data.msg,
                        icon: 'error',
                        showConfirmButton: false,
                        timer: 1100
                    });
                }
            }).catch(err => {
                console.error('Reply insert exception -> ', err);
            })
        },
        submitReply1(commentChild) {
            if (!this.replyChildContent.trim()) {
                this.$message.warning('Comment content cannot be empty');
                return;
            }
            const evaluationsDTO = {
                replierId: commentChild.userId,
                contentType: this.contentType,
                content: this.replyChildContent,
                contentId: this.contentId,
                parentId: commentChild.parentId
            }
            this.$axios.post(`evaluations/insert`, evaluationsDTO).then(res => {
                if (res.data.code === 200) {
                    this.replyChildContent = '';
                    commentChild.replyInputStatus = false;
                    this.$swal.fire({
                        title: 'Reply Operation',
                        text: 'Reply successful',
                        icon: 'success',
                        showConfirmButton: false,
                        timer: 1300
                    });
                    setTimeout(() => this.loadCommentList(), 1300);
                } else {
                    this.$swal.fire({
                        title: 'Comment Exception',
                        text: res.data.msg,
                        icon: 'error',
                        showConfirmButton: false,
                        timer: 1100
                    });
                }
            }).catch(err => {
                console.error('Reply insert exception -> ', err);
            })
        },
        loadCommentList() {
            this.$axios.get(`evaluations/list/${this.contentId}/${this.contentType}`).then(res => {
                if (res.data.code === 200) {
                    this.commentList = res.data.data.data || [];
                    this.evaluationsCount = res.data.data.evaluationsCount || 0;
                    this.commentList.forEach(entity => {
                        entity.time = timeAgo(entity.createTime);
                        entity.upvoteCount = entity.upvoteCount === undefined ? 0 : entity.upvoteCount;
                        entity.upvoteFlag = entity.upvoteFlag === undefined ? false : entity.upvoteFlag;
                        
                        entity.commentChildVOS = entity.commentChildVOS || [];
                        entity.commentChildVOS.forEach(child => {
                            child.time = timeAgo(child.createTime);
                            child.upvoteCount = child.upvoteCount === undefined ? 0 : child.upvoteCount;
                            child.upvoteFlag = child.upvoteFlag === undefined ? false : child.upvoteFlag;
                        });
                    });
                }
            }).catch(err => {
                console.error('Comment list query exception -> ', err);
            })
        },
        upvote(comment) {
            this.$axios.post(`evaluations/upvoteOperation`, { id: comment.id }).then(res => {
                if (res.data.code == 200) {
                    let rep = res.data.data;
                    comment.upvoteCount = Number(rep.count);
                    comment.upvoteFlag = Boolean(rep.haveUpvote);
                } else {
                    this.$message.error(res.data.msg || 'Upvote operation failed');
                }
            }).catch(err => {
                console.error(`Upvote operation exception -> `, err);
                this.$message.error('Network error, upvote operation failed');
            })
        },
        deleteCommentChild(commentChild) {
            this.$axios.delete(`evaluations/delete/${commentChild.id}`).then(res => {
                if (res.data.code === 200) {
                    this.$message.success(res.data.msg);
                    this.loadCommentList();
                }
            }).catch(err => {
                console.error('Sub-comment delete exception -> ', err);
            })
        }
    }
};  
</script>

<style lang="scss">
.cannel-btn,
.yes-btn {
    padding: 0px 15px 5px 15px;
    font-size: 14px !important;
    margin: 0 10px;
    border-radius: 3px;
    border: none;
}

.cannel-btn {
    color: #1c1c1c;
}

.yes-btn {
    background-color: #4b87bc;
    color: #EAF2FF;
}

.cannel-btn:hover {
    background-color: #f5f5f5;
}

.yes-btn:hover {
    background-color: #66a8e1;
}

.commentHeader {
    color: #252933;
    font-size: 18px;
    font-weight: 600;
    line-height: 30px;
}

.comment-parent-input {
    outline: none;
    border: none;
    background-color: transparent;
    font-size: 14px;
    width: 100%;
    user-select: text;
    margin: 0;
}

.parent-comment {
    padding: 0;
    transition: border-color 0.3s ease;
    user-select: none;
    position: relative;
}

.comment-clike {
    position: absolute;
    right: 10px;
    bottom: 5px;
}

.my-body-tag {
    font-size: 12px;
    padding: 3px 4px;
    color: #1E80FF;
    background-color: #EAF2FF;
    margin-left: 5px;
    line-height: 1;
}

.reportItem {
    display: inline-block;
    padding: 8px 22px;
    background-color: #f4f4f4;
    border: 1px solid #f4f4f4;
    margin: 5px 3px 5px 0;
    border-radius: 3px;
    cursor: pointer;
    user-select: none;
}

.reportItem:hover {
    border: 1px solid #4b87bc;
    color: #4b87bc;
}

.el-button:hover > span {
    transform: scale(1.1);
    transition: transform 0.2s ease;
    display: inline-block;
}
.el-button:hover {
    background-color: #BAAEFF !important;
    border-color: #BAAEFF !important;
}

.el-avatar {
    border-radius: 50% !important;
    overflow: hidden;
}
</style>