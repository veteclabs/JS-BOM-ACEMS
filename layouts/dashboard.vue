<template>
    <div>
        <div id="wrap">
            <div id="templateWrap">
                <commonNav/>
                <div id="content">
                    <div :class="collapseState ?'dashboard-box-8' : 'dashboard-box-12'">
                        <commonHeader/>
                        <div id="subContent">
                            <nuxt/>
                        </div>
                        <commonFooter/>
                    </div>
                    <DashboardCollapse/>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
    import DashboardCollapse from "~/components/DashboardCollapse.vue";
    import commonHeader from '~/components/commonHeader.vue';
    import commonNav from '~/components/commonNav.vue';
    import commonFooter from '~/components/commonFooter.vue';

    export default {
        components: {
            commonHeader,
            commonNav,
            commonFooter,
            DashboardCollapse
        },
        computed: {
            collapseState: function () {
                return this.$store.getters.collapseMenu;
            },
        },
    };
</script>
