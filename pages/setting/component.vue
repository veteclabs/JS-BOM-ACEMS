<template>
    <div id="settingSubContent">
        <div class="wrapper animated fadeInRight">
            <div class="ibox">
                <div class="ibox-title ibox-normal-title ibox-noborder-title">
                    컴포넌트 관리
                </div>
                <div class="component-list">
                    <div v-for="component in componentList" :key="component.id">
                        {{component.name}}
                    </div>
                </div>
            </div>
            <flashModal v-bind:propsdata="msgData"/>
        </div>
        <Loading v-bind:propsdata="LoadingData"/>
    </div>
</template>
<script>
    import axios from 'axios';
    import flashModal from '~/components/flashmodal.vue';
    import Loading from '~/components/loading.vue';

    export default {
        fetch({store, redirect}) {
            if (!store.state.authUser) {
                return redirect('/');
            }
            return false;
        },
        layout: 'settingTemplate',
        components: {
            axios,
            flashModal,
            Loading,
        },
        data() {
            return {
                id: '',
                LoadingData: {
                    show: false,
                },
                msgData: {
                    msg: '',
                    show: false,
                    e: '',
                },
                componentList: [
                    {id: 1, name: '', model: {id: 1, tag: []}}
                ]
            }
        },
        created() {
            this.id = this.$store.getters.ID;
        },
        mounted() {
            this.getComponent();
        },
        methods: {
            async getComponent() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/etcs',
                    headers: {setting: true}
                }).then((res) => {
                    vm.equipmentList = res.data
                })
            },
            save() {

            },
        },
    };
</script>
