<template>
<!--    <div v-bar style="height:100vh">-->
    <div>
        <div id="wrap">
            <div id="templateWrap">
                <commonNav/>
                <div id="content">
                    <commonHeader/>
                    <div id="subContent">
                        <nuxt/>
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

  export default {
    components: {
      commonHeader,
      commonNav,
      commonFooter,
    },
  };
</script>
