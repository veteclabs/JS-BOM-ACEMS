<template>
    <div>
        <div id="wrap">
            <div id="templateWrap">
                <commonNav/>
                <div id="content">
                    <div :class="searchFilterState ?'dashboard-box-8' : 'dashboard-box-12'">
                        <commonHeader/>
                        <div id="subContent">
                            <nuxt/>
                        </div>
                        <commonFooter/>
                    </div>
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
        computed: {
            searchFilterState: function () {
                return this.$store.getters.searchFilter;
            },
        },
    };
</script>
