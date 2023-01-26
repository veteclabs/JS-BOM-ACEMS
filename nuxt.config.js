const webpack = require('webpack');
require('dotenv').config();
module.exports = {
    telemetry: false,
    head: {
        title: 'BOM-ACEMS 공기압축기모니터링',
        meta: [
            {charset: 'utf-8'},
            {name: 'viewport', content: 'width=equipment-width, initial-scale=1'},
            {hid: 'description', name: 'description', content: process.env.npm_package_description || ''},
        ],
        script: [
            {src: 'https://cdn.polyfill.io/v2/polyfill.min.js'},
        ],
        link: [
            {rel: 'icon', type: 'image/x-icon', href: '/favicon.ico'},
            {rel: 'stylesheet', type: 'text/css', href: '/font.css'},
        ],
    },
    server: {
        host: '0.0.0.0',
    },
    loading: {color: '#fff'},
    plugins: [
        {src: '~plugins/vue-client', ssr: false},
        {src: '~plugins/axios', ssr: false},
    ],
    modules: [
        'bootstrap-vue/nuxt',
        '@nuxtjs/axios',
        '@nuxtjs/proxy',
        ['@nuxtjs/dotenv', {filename: `.env.${process.env.NODE_ENV}`}]
    ],
    bootstrapVue: {
        bootstrapCSS: false, // Or `css: false`
        bootstrapVueCSS: false, // Or `bvCSS: false`
    },
    css: [
        '~assets/css/bootstrap.min.css',
        '~assets/css/animate.css',
        {src: '~assets/css/common.scss', lang: 'scss'},
    ],
    build: {
        vendor: ['axios', 'jquery'],
        plugins: [
            new webpack.ProvidePlugin({
                $: 'jquery',
                jQuery: 'jquery',
                'window.jQuery': 'jquery',
            }),

        ],
    },
    serverMiddleware: [
        {path: '/nuxt', handler: '~/api/index.js'},
    ],
};
