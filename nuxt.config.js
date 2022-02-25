const webpack = require('webpack');

module.exports = {
  telemetry: false,
  /*
  ** Headers of the page
  */
  head: {
    title: '정석엔지니어링 BOM-ACEMS',
    meta: [
      { charset: 'utf-8' },
      { name: 'viewport', content: 'width=device-width, initial-scale=1' },
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
    { src: '~plugins/vue-client', ssr: false },
    { src: '~plugins/html2canvas.js', ssr: false },
  ],
  /*
  ** Nuxt.js modules
  */
  modules: [
    // Doc: https://bootstrap-vue.js.org/docs/
    'bootstrap-vue/nuxt',
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
    vendor: ['axios', 'jquery', 'html2canvas'],
    plugins: [
      new webpack.ProvidePlugin({
        $: 'jquery',
        jQuery: 'jquery',
        'window.jQuery': 'jquery',
        html2canvas: 'html2canvas',
      }),
    ],
  },
  serverMiddleware: [
    { path: '/api', handler: '~/api/index.js' },
  ],
};
