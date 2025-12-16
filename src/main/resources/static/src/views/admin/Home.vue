<template>
    <div class="menu-container">
        <div class="menu-side" :class="{ 'menu-side-narrow': flag }">
            <div class="logo-wrapper">
                <Logo name="" :flag="flag" :bag="colorLogo" />
            </div>
            <div class="menu-content">
                <AdminMenu :flag="flag" :routes="adminRoutes" :bag="bagMenu" @select="handleRouteSelect" />
            </div>
        </div>

        <div class="main">
            <div class="header-section">
                <LevelHeader @eventListener="eventListener" @selectOperation="selectOperation" :tag="tag"
                    :userInfo="userInfo" />
            </div>
            <div class="content-section">
                <router-view></router-view>
            </div>
        </div>

        <el-dialog 
            :show-close="false" 
            :visible.sync="dialogOperaion" 
            width="30%" 
            class="user-center-dialog"
            :modal-style="{background: 'rgba(0, 0, 0, 0.1)'}">
            <div class="dialog-header">
                <span class="dialog-title">User Center</span>
            </div>
            <div class="dialog-content">
                <el-row class="form-row">
                    <p class="form-label">
                        <span class="required-mark">*</span>Avatar
                    </p>
                    <el-upload 
                        class="avatar-uploader"
                        action="http://localhost:21090/api/trading-website/v1.0/file/upload" 
                        :show-file-list="false"
                        :on-success="handleAvatarSuccess">
                        <img v-if="userInfo.url" :src="userInfo.url" class="avatar-img">
                        <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                    </el-upload>
                </el-row>
                <el-row class="form-row">
                    <p class="form-label">
                        <span class="required-mark">*</span>Username
                    </p>
                    <input class="form-input" v-model="userInfo.name" placeholder="Please enter username">
                </el-row>
                <el-row class="form-row">
                    <p class="form-label">
                        <span class="required-mark">*</span>User Email
                    </p>
                    <input class="form-input" v-model="userInfo.email" placeholder="Please enter user email">
                </el-row>
            </div>
            <div class="dialog-footer">
                <el-button 
                    class="btn-cancel" 
                    size="small" 
                    @click="dialogOperaion = false">
                    Cancel
                </el-button>
                <el-button 
                    class="btn-confirm" 
                    size="small" 
                    @click="updateUserInfo">
                    Modify
                </el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import request from "@/utils/request.js";
import router from "@/router/index";
import { clearToken } from "@/utils/storage"
import AdminMenu from '@/components/VerticalMenu.vue';
import Logo from '@/components/Logo.vue';
import LevelHeader from '@/components/LevelHeader.vue';

export default {
    name: "Admin",
    components: {
        Logo,
        LevelHeader,
        AdminMenu
    },
    data() {
        return {
            adminRoutes: [],
            activeIndex: '',
            userInfo: {
                id: null,
                url: '',
                name: '',
                role: null,
                email: ''
            },
            flag: false,
            tag: 'Dashboard',
            bag: 'rgb(248,248,248)',
            // 重点修改 1: Logo 颜色改为白色
            colorLogo: '#ffffff',
            // 重点修改 2: 菜单背景色改为透明，以透出父容器的渐变色
            bagMenu: 'transparent', 
            dialogOperaion: false
        };
    },
    created() {
        let menus = router.options.routes.filter(route => route.path == '/admin')[0];
        this.adminRoutes = menus.children;
        this.tokenCheckLoad();
        this.menuOperationHistory();
        this.$router.push('/adminLayout');
    },
    methods: {
        async updateUserInfo() {
            try {
                const userUpdateDTO = {
                    userAvatar: this.userInfo.url,
                    userName: this.userInfo.name,
                    userEmail: this.userInfo.email
                }
                const resposne = await this.$axios.put(`/user/update`, userUpdateDTO);
                const { data } = resposne;
                if (data.code === 200) {
                    this.dialogOperaion = false;
                    this.tokenCheckLoad();
                    this.$swal.fire({
                        title: 'Modify Personal Info',
                        text: data.msg,
                        icon: 'success',
                        showConfirmButton: false,
                        timer: 1000,
                    });
                }
            } catch (e) {
                this.dialogOperaion = false;
                this.$swal.fire({
                    title: 'Modify Personal Info Exception',
                    text: e,
                    icon: 'error',
                    showConfirmButton: false,
                    timer: 2000,
                });
                console.error(`Modify Personal Info Exception:${e}`);
            }
        },
        handleAvatarSuccess(res, file) {
            if (res.code !== 200) {
                this.$message.error(`Avatar upload exception`);
                return;
            }
            this.$message.success(`Avatar upload successful`);
            this.userInfo.url = res.data;
        },
        eventListener(event) {
            if (event === 'center') this.dialogOperaion = true;
            if (event === 'address') this.$router.push('/address');
            if (event === 'loginOut') this.loginOut();
        },
        async loginOut() {
            const confirmed = await this.$swalConfirm({
                title: 'Log out?',
                text: `Need to re-login after logging out?`,
                icon: 'warning',
            });
            if (confirmed) {
                this.$swal.fire({
                    title: 'Logged out successfully',
                    text: 'Returning to login page in 1s',
                    icon: 'success',
                    showConfirmButton: false,
                    timer: 1000,
                });
                setTimeout(() => {
                    clearToken();
                    this.$router.push("/login");
                }, 1000)
            }
        },
        menuOperationHistory() {
            this.flag = sessionStorage.getItem('flag') === 'true';
        },
        selectOperation(flag) {
            this.flag = flag;
        },
        handleRouteSelect(index) {
            let ary = this.adminRoutes.filter(entity => entity.path == index);
            this.tag = ary[0].name;
            if (this.$router.currentRoute.fullPath !== index) {
                this.$router.push(index);
            }
        },
        async tokenCheckLoad() {
            try {
                const res = await request.get('user/auth');
                if (res.data.code === 400) {
                    this.$message.error(res.data.msg);
                    this.$router.push('/login');
                    return;
                }
                const { id, userAvatar: url, userName: name, userRole: role, userEmail: email } = res.data.data;
                this.userInfo = { id, url, name, role, email };
                const rolePath = role === 1 ? '/admin' : '/user';
                const targetMenu = router.options.routes.find(route => route.path === rolePath);
                if (targetMenu) this.routers = targetMenu.children;
                else console.warn(`Route corresponding to role not found: ${rolePath}`);
            } catch (error) {
                console.error('Error fetching user authentication info:', error);
                this.$message.error('Authentication info failed to load, please try again!');
            }
        },
    }
};
</script>

<style scoped lang="scss">
// 定义主色调和菜单色
$active-color: #395afc; // 选中时的强调色
$default-text-color: #ffffff; // 默认文字和图标颜色
$active-bg: rgba(255, 255, 255, 0.15); // 选中项的浅色背景

// ---------------------------------------------
// 整体布局容器，去除所有白边
.menu-container {
    display: flex;
    // 重点修改 3: 清除 padding 和 box-sizing，实现全屏
    padding: 0;
    box-sizing: border-box;
    height: 100vh;
    width: 100%;

    .menu-side {
        width: 270px;
        min-width: 95px;
        // 重点修改 4: 侧边栏高度占满
        height: 100vh; 
        padding-top: 0; // 清除 padding-top
        box-sizing: border-box;
        transition: width 0.3s ease;
        // 重点修改 5: 应用渐变背景
        background: linear-gradient(180deg, #9EAEFF 0%, #bcaefe 100%); 
        
        display: flex;
        flex-direction: column;
        align-items: center;
        
        .logo-wrapper {
            width: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
            padding: 15px 0;
            margin: 0; 
            
            // Logo 标题/文本颜色设为白色
            :deep(.logo-text) {
                color: $default-text-color !important;
            }
            
            // 保持 Logo 图片样式
            :deep(img) {
                width: 70px !important;
                height: 70px !important;
                object-fit: contain;
            }
            
            .menu-side-narrow & :deep(img) {
                width: 60px !important;
                height: 60px !important;
            }
        }
        
        .menu-content {
            width: 100%;
            margin-top: 12px;
            
            // 重点修改 6: 穿透 AdminMenu 组件 (VerticalMenu.vue) 样式
            // 菜单项颜色和背景控制
            :deep(.el-menu) {
                background-color: transparent !important; // 菜单背景透明
                border-right: none; // 移除 Element 默认的右侧边框
            }
            
            :deep(.el-menu-item) {
                color: $default-text-color !important; // 默认文字颜色改为白色
                i {
                    color: $default-text-color !important; // 默认图标颜色改为白色
                }

                margin-bottom: 20px;

                // 移除 hover 时变白的功能，改为轻微变暗
                &:hover {
                    background-color: rgba(0, 0, 0, 0.1) !important; 
                    color: $default-text-color !important;
                    i {
                        color: $default-text-color !important;
                    }
                }
            }

            // 重点修改 7: 选中项样式
            :deep(.el-menu-item.is-active) {
                background-color: $active-bg !important; // 设置浅色透明背景
                // 选中时文字和图标改为强调色 #ff6b6b
                color: $active-color !important; 
                i {
                    color: $active-color !important; 
                }
            }
        }
    }

    .menu-side-narrow {
        width: 115px;
        .logo-wrapper {
            justify-content: center;
        }
    }

    .main {
        flex-grow: 1;
        overflow-x: hidden;

        .header-section {
            // 清除 Header Section 的右侧 padding
            max-width: 100%;
            padding: 0 0 0 0; 
        }

        .content-section {
            overflow-x: hidden;
            flex-grow: 1;
            // 统一内容区的左右 padding
            padding: 0 20px; 
            box-sizing: border-box;
            overflow-y: auto;
        }
    }
}

// 个人中心弹窗样式 (保持不变，但为了美观，调整了渐变颜色变量的定义)
$dialog-primary-blue: #9EAEFF;
$dialog-deep-blue: #0323FF;
$dialog-light-purple: #CEC3FF;

.user-center-dialog {
    ::v-deep .el-dialog {
        border-radius: 12px;
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);
        overflow: hidden;
    }

    ::v-deep .el-dialog__body {
        padding: 0;
    }

    .dialog-header {
        padding: 20px 20px 10px;
        border-bottom: 1px solid #f0f0f0;

        .dialog-title {
            font-size: 18px;
            font-weight: 600;
            color: #333;
        }
    }

    .dialog-content {
        padding: 20px;
    }

    .form-row {
        margin-bottom: 20px;
        &:last-child {
            margin-bottom: 0;
        }
    }

    .form-label {
        font-size: 14px;
        color: #606266;
        margin-bottom: 8px;
        font-weight: 500;

        .required-mark {
            color: #ff4f24;
            margin-right: 4px;
        }
    }

    .form-input {
        width: 100%;
        padding: 10px 15px;
        border: 1px solid #e4e7ed;
        border-radius: 8px;
        font-size: 14px;
        transition: all 0.3s;

        &:focus {
            border-color: $dialog-primary-blue;
            outline: none;
            box-shadow: 0 0 0 2px rgba(158, 174, 255, 0.2);
        }
    }

    .avatar-uploader {
        display: flex;
        align-items: center;
        justify-content: center;
        width: 80px;
        height: 80px;
        border-radius: 50%;
        overflow: hidden;
        border: 2px dashed #e4e7ed;
        background-color: #f8f8f8;
        transition: all 0.3s;

        &:hover {
            border-color: $dialog-primary-blue;
            background-color: rgba(158, 174, 255, 0.05);
        }
    }

    .avatar-img {
        width: 100%;
        height: 100%;
        object-fit: cover;
        border-radius: 50%;
    }

    .avatar-uploader-icon {
        font-size: 24px;
        color: #909399;
    }

    .dialog-footer {
        display: flex;
        justify-content: flex-end;
        gap: 10px;
        padding: 15px 20px;
        border-top: 1px solid #f0f0f0;

        .btn-cancel {
            padding: 8px 20px;
            border-radius: 8px;
            background-color: #f5f5f5;
            color: #666;
            border: none;
            transition: all 0.3s;

            &:hover {
                background-color: #e9e9e9;
                color: #333;
            }
        }

        .btn-confirm {
            padding: 8px 20px;
            border-radius: 8px;
            // 按钮渐变色使用了对话框原有的颜色变量
            background: linear-gradient(180deg, $dialog-light-purple, $dialog-primary-blue);
            color: #fff;
            border: none;
            box-shadow: 0 2px 8px rgba(158, 174, 255, 0.3);
            transition: all 0.3s;

            &:hover {
                background: linear-gradient(180deg, #8296fc 0%, #9881fb 100%);
                box-shadow: 0 4px 12px rgba(158, 174, 255, 0.4);
            }
        }
    }
}
</style>