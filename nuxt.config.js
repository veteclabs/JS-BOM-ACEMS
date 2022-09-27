const webpack = require('webpack');
require('dotenv').config();
module.exports = {
  telemetry: false,
  /*
  ** Headers of the page
  */
  head: {
    title: 'BOM-ACEMS 공기압축기모니터링',
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=equipment-width, initial-scale=1' },
      { hid: 'description', name: 'description', content: process.env.npm_package_description || '' },
    ],
    script: [
      { src: 'https://cdn.polyfill.io/v2/polyfill.min.js' },
    ],
    link: [
      { rel: 'icon', type: 'image/x-icon', href: '/favicon.ico' },
      { rel: 'stylesheet', type: 'text/css', href: '/font.css' },
    ],
  },
  server: {
    // nuxt.js server options ( can be overrided by environment variables )
    host: '0.0.0.0',
  },
  /*
  ** Customize the progress-bar color
  */
  loading: { color: '#fff' },
  /*
  ** Plugins to load before mounnpm ting the App
  */
  plugins: [
    {src: '~plugins/vue-client', ssr: false},
    {src: '~plugins/axios', ssr: false},
  ],
  /*
  ** Nuxt.js modules
  */
  modules: [
    // Doc: https://bootstrap-vue.js.org/docs/
    'bootstrap-vue/nuxt',
    '@nuxtjs/axios',
    '@nuxtjs/proxy'
  ],
  bootstrapVue: {
    bootstrapCSS: false, // Or `css: false`
    bootstrapVueCSS: false, // Or `bvCSS: false`
  },
  /*
  ** Global CSS
  */
  css: [
    '~assets/css/bootstrap.min.css',
    '~assets/css/animate.css',
    { src: '~assets/css/common.scss', lang: 'scss' },
  ],
  /*
  ** Build configuration
  */

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
    { path: '/nuxt', handler: '~/api/index.js' },
  ],
  // axios: {
  //   // baseURL: "http://localhost:8030"
  //   proxy:true,
  //   prefix: process.env.NODE_ENV === 'development'? process.env.API_TEST_URL : process.env.API_PROD_URL
  // },
  // proxy: {
  //   '/api/': { target: process.env.NODE_ENV === 'development'? process.env.API_TEST_URL : process.env.API_PROD_URL,
  //     pathRewrite: {'^/api':''},
  //     changeOrigin: true
  //   }
  //
  // },
};
