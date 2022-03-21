<template>
    <div>
        <ul class="state-box">
            <li :class="{'active' : propsdata.state === 'RUN', 'set-btn run': true}"
                @click="setAirCompressor(propsdata.id, 'RUN')">RUN
            </li>
            <li :class="{'active' : propsdata.state === 'LOAD', 'load': true}">LOAD</li>
            <li :class="{'active' : propsdata.state === 'UNLOAD', 'unload': true}">UNLOAD</li>
            <li :class="{'active' : propsdata.state === 'STOP', 'set-btn stop': true}"
                @click="setAirCompressor(propsdata.id, 'STOP')">STOP
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
                }
            }
        },
        methods: {
            async setAirCompressor(device, stateValue) {
                const vm = this;

                const confirmResult = confirm(`해당 컴프레셔 상태를 ${stateValue}으로 변경합니다. 진행하시겠습니까?`);

                if (confirmResult) {
                    const params = {
                        device,
                        stateValue
                    };
                    vm.LoadingData.show = true;
                    axios.post('/api/setAirCompressor', params)
                        .then(() => {
                            vm.msgData.show = true;
                            vm.msgData.msg = '제어 명령이 완료되었습니다.';
                        })
                        .catch(() => {
                            vm.msgData.show = true;
                            vm.msgData.msg = '제어에 실패했습니다. 잠시 후 다시 시도해주세요.';
                        })
                        .finally(() => {
                            vm.LoadingData.show = false;
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
