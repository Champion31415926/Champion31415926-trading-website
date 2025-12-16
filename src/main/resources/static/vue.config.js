module.exports = {
    lintOnSave: false,
    assetsDir: 'static',
    parallel: false,
    publicPath: './',
    devServer: {
        host: "localhost",
        port: 21091,
        https: false,
        proxy: {
            '/api': {
                target: 'http://localhost:21090',
                changeOrigin: true, 
                pathRewrite: { '^/api': '' }, 
            },
        },
        overlay: {
            warning: false,
            errors: false
        },
    },
}