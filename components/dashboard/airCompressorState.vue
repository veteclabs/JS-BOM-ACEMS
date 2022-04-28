<template>
    <div v-if="propsdata.state">
        <ul class="state-box">
            <li :class="{'active' : propsdata.state.COMP_Power.value === 1, 'set-btn run': true}"
                @click="setAirCompressor(propsdata.id, 1)">RUN
            </li>
            <li :class="{'active' : propsdata.state.COMP_Load.value === 1, 'load': true}">LOAD</li>
            <li :class="{'active' : propsdata.state.COMP_Load.value === 0, 'unload': true}">UNLOAD</li>

            <li :class="{'active' : propsdata.state.COMP_Power.value === 0, 'set-btn stop': true}"
                @click="setAirCompressor(propsdata.id, 0)">STOP
            </li>
        </ul>
        <Loading v-bind:propsdata="loadingData"/>
        <flashmodal v-bind:propsdata="msgData"/>
    </div>
</template>

<script>
    import axios from 'axios';
    import Loading from '~/components/loading.vue';
    import flashmodal from '~/components/flashmodal.vue';

    export default {
        props: ['propsdata'],
        components: {
            axios,
            Loading,
            flashmodal
        },
        data() {
            return {
                msgData: {
                    // 알람모달
                    msg: '',
                    show: false,
                    e: '',
                },
                loadingData: {
                    show: false,
                },
                powerState: ['STOP', 'RUN']
            }
        },
        methods: {
            async setAirCompressor(id, stateValue) {
                const vm = this;

                const confirmResult = confirm(`해당 컴프레셔 상태를 ${this.powerState[stateValue]}으로 변경합니다. 수동제어 명령 진행 시 스케줄 제어는 모두 해제 됩니다.진행하시겠습니까?`);

                if (confirmResult) {
                    vm.loadingData.show = true;
                    axios.put(`/api/compressor/${id}/power/${stateValue}`)
                        .then((res) => {
                            vm.msgData.show = true;
                            vm.msgData.msg = res.data.message;
                        }).catch((error) => {
                            vm.msgData.show = true;
                            vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                        }).finally(() => {
                            vm.loadingData.show = false;
                        });
                } else {
                    vm.msgData.show = true;
                    vm.msgData.msg = '제어명령이 취소되었습니다';
                }
                return true;
            },
        },
    };
</script>
