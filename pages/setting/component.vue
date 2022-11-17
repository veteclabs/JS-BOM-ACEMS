<template>
    <div id="settingSubContent">
        <div class="wrapper animated fadeInRight">
            <div class="ibox">
                <div class="ibox-title ibox-normal-title ibox-noborder-title">
                    컴포넌트 관리
                </div>
                <div class="grid-header" style="margin-bottom:24px;">
                    <button id="createEquipment" class="button submit-button" @click="save">SAVE</button>
                </div>
                <div class="component-list">
                    <div v-for="component in componentList" :key="component.id" class="group">
                        <div class="title-box">{{component.description}}({{component.nickname}})</div>
                        <div class="row">
                            <div v-for="equipment in component.equipments" class="col-lg-4">
                                <div class="ibox">
                                    <div class="ibox-title">
                                        {{equipment.name}}
                                    </div>
                                    <div class="ibox-content">
                                        <div v-for="tag in equipment.tagList">
                                            <div class="flex-box">
                                                <div :data-tooltip-text="tag.tagDescription"
                                                style="width:calc(100% - 50px)">
                                                    {{tag.tagDescription}}<span>({{tag.type}})</span>
                                                </div>
                                                <label class="switch-box">
                                                    <input type="checkbox" v-model="tag.active"/>
                                                    <span class="slider round"></span>
                                                </label>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
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
                    {id: 1, name: '', equipments: {id: 1, tagList: []}}
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
                    url: '/api/components',
                }).then((res) => {
                    if (res.status === 200) {
                        vm.componentList = res.data
                    }
                });
            },
            save() {
                const vm = this;
                axios({
                    method: 'post',
                    url: '/api/components',
                    data: vm.componentList,
                }).then((res) => {
                    if (res.status === 200) {
                        vm.msgData.show = true;
                        vm.msgData.msg = res.data.message;
                        this.getComponent();
                    }
                });
            },
        },
    };
</script>
