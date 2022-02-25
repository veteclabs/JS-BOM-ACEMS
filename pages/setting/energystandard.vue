<template>
    <div id="settingEnergy">
        <div class="wrapper animated fadeInRight">
            <div class="row">
                <div class="col-lg-12">
                    <div class="ibox">
                        <div class="ibox-title flex-ibox-title">
                            부서 별 목표 사용량 설정
                            <button class="button submit-button" @click="updateTarget">
                                저장
                            </button>
                        </div>
                        <div class="row ibox-content">
                            <div v-for="item in targetList" class="col-lg-6">
                                <div class="target-box">

                                    <div class="label-box">
                                        {{ item.departmentName }}
                                    </div>
                                    <table class="bom-table">
                                        <tr>
                                            <td>일별 목표 사용량</td>
                                            <td><input type="number" v-model="item.day_kWh_target"/></td>
                                        </tr>
                                        <tr>
                                            <td>월별 목표 사용량</td>
                                            <td><input type="number" v-model="item.month_kWh_target"/></td>
                                        </tr>
                                        <tr>
                                            <td>연도별 목표 사용량</td>
                                            <td><input type="number" v-model="item.year_kWh_target"/></td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="col-lg-12">
                    <div class="ibox">
                        <div class="ibox-title flex-ibox-title">
                            변전실 별 목표 사용량 설정
                            <button class="button submit-button" @click="updateSubstationTarget">
                                저장
                            </button>
                        </div>
                        <div class="row ibox-content">
                            <div v-for="item in substationTargetList" class="col-lg-6">
                                <div class="target-box">

                                    <div class="label-box">
                                        {{ item.substationName }}
                                    </div>
                                    <table class="bom-table">
                                        <tr>
                                            <td>일별 목표 사용량</td>
                                            <td><input type="number" v-model="item.day_kWh_target"/></td>
                                        </tr>
                                        <tr>
                                            <td>월별 목표 사용량</td>
                                            <td><input type="number" v-model="item.month_kWh_target"/></td>
                                        </tr>
                                        <tr>
                                            <td>연도별 목표 사용량</td>
                                            <td><input type="number" v-model="item.year_kWh_target"/></td>
                                        </tr>
                                    </table>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <flashModal v-bind:propsdata="msgData"/>
        </div>
    </div>
</template>
<script>
    import axios from "axios";
    import flashModal from "~/components/flashmodal.vue";

    export default {
        fetch({store, redirect}) {
            if (!store.state.authUser) {
                return redirect("/");
            }
            return false;
        },
        layout: "settingTemplate",
        components: {
            axios,
            flashModal,
        },
        data() {
            return {
                id: "",
                msgData: {
                    // 알람모달
                    msg: "",
                    show: false,
                    e: "",
                },
                targetList: [],
                substationTargetList:[],
            };
        },
        created() {
            this.id = this.$store.getters.ID;
        },
        mounted() {
            this.getTarget();
            this.getSubstationTarget();
        },
        methods: {
            async getTarget() {
                const vm = this;
                axios({
                    method: "GET",
                    url: "/api/setting/targets",
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.targetList = res.data.result;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },

            async getSubstationTarget() {
                const vm = this;
                axios({
                    method: "GET",
                    url: "/api/setting/substation/targets",
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.substationTargetList = res.data.result;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            // 목표량 수정하기
            async updateTarget() {
                const vm = this;
                const params = {
                    target: vm.targetList,
                };
                axios.put("/api/setting/targets", params)
                    .then((res) => {
                        vm.getTarget();
                        vm.msgData.show = true;
                        vm.msgData.msg = res.data.msg;
                    }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            async updateSubstationTarget() {
                const vm = this;
                axios.put("/api/setting/substation/targets", vm.substationTargetList)
                    .then((res) => {
                        vm.getSubstationTarget();
                        vm.msgData.show = true;
                        vm.msgData.msg = res.data.msg;
                    }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
        },
    };
</script>
