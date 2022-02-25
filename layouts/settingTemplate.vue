<template>
    <div v-bar style="height:100vh">
        <div id="wrap">
            <div id="templateWrap">
                <commonNav/>
                <div id="content">
                    <commonHeader/>
                    <div id="settingContent">
                        <div class="row">
                            <div class="setting-space-box"/>
                            <settingNav class="setting-nav-box"/>
                            <nuxt class="setting-content-box"/>
                            <div class="setting-space-box"/>
                        </div>
                    </div>
                    <commonFooter/>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
  import commonHeader from '~/components/commonHeader.vue';
  import commonNav from '~/components/commonNav.vue';
  import commonFooter from '~/components/commonFooter.vue';
  import settingNav from '~/components/settingNav.vue';

  export default {
    components: {
      commonHeader,
      commonNav,
      commonFooter,
      settingNav,
    },
  };
</script>
