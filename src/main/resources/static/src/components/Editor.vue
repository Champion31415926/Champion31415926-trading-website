<template>
    <div>
        <Editor :style="{ height: height, overflowY: 'hidden' }" v-model="content" :defaultConfig="editorConfig"
            :mode="mode" @onCreated="onCreated" />
    </div>
</template>

<script>
import Vue from 'vue'
import { Editor, Toolbar } from '@wangeditor/editor-for-vue'
export default Vue.extend({
    components: { Editor, Toolbar },
    props: {
        receiveContent: {
            type: String,
            default: '',
            required: true
        },
        height: {
            type: String,
            default: 'calc(100vh - 100px)'
        }
    },
    data() {
        return {
            editor: null,
            content: '<p>Start typing here...</p>',
            toolbarConfig: {},
            editorConfig: {
                placeholder: 'Please enter product details...',
                MENU_CONF: {
                    uploadImage: {
                        server: 'http://localhost:21090/api/trading-website/v1.0/file/upload',
                        fieldName: 'file',
                        maxFileSize: 10 * 1024 * 1024,
                        maxNumberOfFiles: 10,
                        metaWithUrl: false,
                        withCredentials: true,
                        timeout: 10 * 1000,
                        headers: {
                            'token': sessionStorage.getItem('token')
                        },
                        customInsert(res, insertFn) {
                            insertFn(res.data, res.data, res.data);
                        },
                    },
                }
            },
            mode: 'default',
        }
    },
    methods: {
        onCreated(editor) {
            this.editor = Object.seal(editor);
            this.toolbarConfig.excludeKeys = ['group-video','group-image'];
        },
    },
    watch: {
        receiveContent: {
            handler(v1, v2) {
                console.log("Received content：", v1);
                this.content = v1;
            },
            deep: true, 
            immediate: true
        },
        content(newVal, oldVal) {
            this.$emit('on-receive', newVal);
        },
    },
    beforeDestroy() {
        const editor = this.editor;
        if (editor == null) return;
        editor.destroy();
    }
})
</script>

<style src="@wangeditor/editor/dist/css/style.css"></style>

<style scoped>
.line-number {
    display: block;
    margin-right: 10px;
    pointer-events: none;
    user-select: none;
    -webkit-user-select: none;
    color: #999;
    text-align: right;
}
</style>