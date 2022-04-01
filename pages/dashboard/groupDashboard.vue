<template>
    <div id="SubContentWrap">
        <div class="group" v-for="group in groupList">
            <div class="title-box flex-box">
                <h2>
                    <img src="~assets/images/dashboard/icn_dashboard_aircompressor.png" alt="aircompressor"/>
                    {{group.groupName}}
                </h2>
                <button class="button setting-button" @click="settingAirCompressorModalOpen()">TP</button>
            </div>
            <div class="row group-content">
                <div class="col-lg-2">
                    <div class="td-label">압력계</div>
                    <div class="ibox" v-for="device in group.pressure" :key="device.id">
                        <div class="ibox-title ibox-noborder-title ibox-normal-title flex-box">
                            <div>{{device.name}}</div>
                            <h3>{{tagVal | pickValue('Name',`${device.unit}_AIR_PRE`, 'Value')}} %</h3>
                        </div>
                    </div>
                    <div class="td-label">온도계</div>
                    <div class="ibox" v-for="device in group.temp" :key="device.id">
                        <div class="ibox-title ibox-noborder-title ibox-normal-title flex-box">
                            <div>{{device.name}}</div>
                            <h3>{{tagVal | pickValue('Name',`${device.unit}_AIR_PRE`, 'Value')}} %</h3>
                        </div>
                    </div>
                    <div class="td-label">유량계</div>
                    <div class="ibox" v-for="device in group.flow" :key="device.id">
                        <div class="ibox-title ibox-noborder-title ibox-normal-title flex-box">
                            <div>{{device.name}}</div>
                            <h3>{{tagVal | pickValue('Name',`${device.unit}_AIR_PRE`, 'Value')}} %</h3>
                        </div>
                    </div>
                </div>
                <div class="col-lg-10">
                    <div class="td-label">공기압축기</div>
                    <div class="row">
                        <div v-for="device in group.airCompressor" :key="device.id" class="col-lg-3">
                            <div class="ibox">
                                <div class="ibox-title aircompressor-ibox-title flex-ibox-title">
                                    <nuxt-link :to="`/dashboard/${device.id}`">
                                        <h3>{{device.name}}</h3>
                                    </nuxt-link>
                                    <img src="~assets/images/dashboard/icn_dashboard_setting.svg" alt="setting"
                                         class="setting-btn"
                                         @click="settingModalOpen(device.id)"/>
                                </div>
                                <div class="ibox-content">
                                    <div class="group-state flex-box">
                                        <div>
                                            <span>상태</span>
                                            <div :class="`${device.state} device-state`">{{device.state}}</div>
                                        </div>
                                        <div class="percent">
                                            <span>부하율</span>
                                            <h3>{{tagVal | pickValue('Name',`${device.unit}_COMP_PCY`, 'Value')}} %</h3>
                                        </div>
                                    </div>
                                    <ul class="tag-box">
                                        <li v-for="tag in airTagList" :key="tag.id">
                                            <div class="tagname">{{tag.name}}</div>
                                            <div>
                                                {{tagVal | pickValue('Name',`${device.unit}_${tag.tagName}`, 'Value')}}
                                                {{tag.unit}}
                                            </div>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <settingAirCompressorModal ref="settingAirCompressorModal" v-bind:propsdata="TPModalData"/>
        <settingEquipmentModal ref="settingEquipmentModal" v-bind:propsdata="settingModalData"/>
        <Loading v-bind:propsdata="loadingData"/>
    </div>
</template>
<script>
    import dayjs from 'dayjs';
    import axios from 'axios';
    import Loading from '~/components/loading.vue';
    import settingEquipmentModal from '~/components/settingModal/settingEquipmentModal.vue';
    import settingAirCompressorModal from '~/components/settingModal/settingAirCompressorModal.vue';
    import airCompressorState from '~/components/dashboard/airCompressorState.vue';
    import TPArray from '~/assets/data/TPCode.json';


    export default {
        fetch({store, redirect}) {
            if (!store.state.authUser) {
                return redirect('/');
            }
            return false;
        },
        layout: 'dashboard',
        components: {
            dayjs,
            Loading,
            settingEquipmentModal,
            settingAirCompressorModal,
            airCompressorState
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
                settingModalData: {
                    show: false,
                },
                TPModalData: {
                    show: false,
                },
                tagVal: '',
                groupList:[
                    {id:1, groupName:'groupA',
                        airCompressor: [
                            {
                                id: 1,
                                unit: 'U001',
                                state: 'RUN',
                                alarm: '',
                                equipmentId: 'ingersollrand_rm55',
                                name: 'Ingersoll Rand RM55 -1'
                            },
                            {
                                id: 2,
                                unit: 'U002',
                                state: 'STOP',
                                alarm: '',
                                equipmentId: 'ingersollrand_rm55',
                                name: 'Ingersoll Rand RM55 -2'
                            },
                        ],
                        pressure:[
                            {id:1, unit:"U007", name:'압력계#1'}
                        ],
                        temp: [
                            {id:1, unit:"U007", name:'온도계#2'}
                        ],
                        flow: [
                            {id:1, unit:"U007", name:'유량계#2'}
                        ]
                    },
                    {id:2, groupName:'groupB',
                        airCompressor: [
                            {
                                id: 3,
                                unit: 'U003',
                                state: 'LOAD',
                                alarm: '',
                                equipmentId: 'ingersollrand_rm55',
                                name: 'Ingersoll Rand RM55 -3'
                            },
                            {
                                id: 4,
                                unit: 'U004',
                                state: 'UNLOAD',
                                alarm: '온도 2단계 알람이 발생했습니다.',
                                equipmentId: 'ingersollrand_rm55',
                                name: 'Ingersoll Rand RM55 -4'
                            },
                        ],
                        pressure:[
                            {id:1, unit:"U008", name:'압력계#2'}
                        ],
                        temp: [
                            {id:1, unit:"U007", name:'온도계#2'}
                        ],
                        flow: [
                            {id:1, unit:"U007", name:'유량계#2'}
                        ]
                    },
                ],
                airCompressorList: [
                    {
                        id: 1,
                        unit: 'U001',
                        state: 'RUN',
                        alarm: '',
                        equipmentId: 'ingersollrand_rm55',
                        name: 'Ingersoll Rand RM55 -1'
                    },
                    {
                        id: 2,
                        unit: 'U002',
                        state: 'STOP',
                        alarm: '',
                        equipmentId: 'ingersollrand_rm55',
                        name: 'Ingersoll Rand RM55 -2'
                    },
                    {
                        id: 3,
                        unit: 'U003',
                        state: 'LOAD',
                        alarm: '',
                        equipmentId: 'ingersollrand_rm55',
                        name: 'Ingersoll Rand RM55 -3'
                    },
                    {
                        id: 4,
                        unit: 'U004',
                        state: 'UNLOAD',
                        alarm: '온도 2단계 알람이 발생했습니다.',
                        equipmentId: 'ingersollrand_rm55',
                        name: 'Ingersoll Rand RM55 -4'
                    },
                ],
                airTagList: [
                    {id: 1, name: '패키치 배출압력', tagName: 'COMP_PDP', unit: ''},
                    {id: 2, name: '에어앤드온도', tagName: 'COMP_AT', unit: ''},
                    {id: 7, name: '전압', tagName: 'PWR_V', unit: 'V'},
                    {id: 8, name: '전류', tagName: 'PWR_A', unit: 'A'},
                ],
                thermometerList: [
                    {id: 11, unit: 'U007', name: '흡착식 온도계', tag: [{name: '온도', tagName: 'Temp', unit: '℃'}]},
                    {
                        id: 12,
                        unit: 'U008',
                        name: '온도계#1',
                        tag: [{name: 'In', tagName: 'Temp', unit: '℃'}, {name: 'Out', tagName: 'Temp', unit: '℃'}]
                    },
                    {
                        id: 13,
                        unit: 'U009',
                        name: '온도계#2',
                        tag: [{name: 'In', tagName: 'Temp', unit: '℃'}, {name: 'Out', tagName: 'Temp', unit: '℃'}]
                    },
                ],
                Interval1M: '',
                interval: '',
                intervalTime: 5 * 1000,
            };
        },
        computed: {
            collapseState: function () {
                return this.$store.getters.collapseMenu;
            },
        },
        mounted() {
            this.WaLogin();
            this.getTagValues();
            this.resetInterval();
            this.getSubstationAlarm();
            this.loadingData.show = true;
        },
        beforeDestroy() {
            this.removeInterval();
        },
        methods: {
            async WaLogin() {
                const vm = this;
                axios.get('/api/WaLogin')
                    .catch((error) => {
                        vm.msgData.msg = error;
                    });
            },
            async getSubstationAlarm() {
                const vm = this;
                axios.get('/api/substation/alarm')
                    .then((res) => {
                        if (res.data.code === 1) {
                            vm.alarmList = res.data.value;
                        }
                    }).catch((error) => {
                    vm.msgData.msg = error;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },
            async getTagValues() {
                const vm = this;
                axios.post('/api/dashboard/port/getTagValue', {
                    portId: [1, 2, 3, 4, 5],
                }, {
                    timeout: vm.intervalTime,
                }).then((res) => {
                    if (res.data.Result.Total > 0) {
                        vm.tagVal = res.data.Values;
                    }
                }).catch((error) => {
                    vm.msgData.msg = error;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },
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
            settingModalOpen(id) {
                this.$refs.settingEquipmentModal.createdModal(id);
                this.settingModalData.show = true;
            },
            settingAirCompressorModalOpen() {
                this.TPModalData.show = true;
                this.$refs.settingAirCompressorModal.getTP();
            },
            getProgressBarValue(unit) {
                const vm = this;
                let value = this.$options.filters.pickValue(vm.tagVal, 'Name', `${unit}_COMP_PCY`, 'Value');
                if (value < 0) {
                    value = 0;
                }
                return value;
            },
            resetInterval() {
                const vm = this;
                clearInterval(this.interval);
                vm.interval = null;
                vm.interval = setInterval(() => {
                    vm.getTagValues();
                }, vm.intervalTime);
            },
            removeInterval() {
                const vm = this;
                clearInterval(vm.interval);
            },
        },
        filters: {
            numberFormat: (value, numFix) => {
                value = parseFloat(value);
                if (!value) return '0';
                return value.toLocaleString('ko-KR', {maximumFractionDigits: numFix});
            },
            pickValue: function (object, property, value, returnValue) {
                if (object === undefined || object === null || object === "") {
                    return -1;
                } else {
                    let target = object.filter(object => object[property] === value);
                    if (target.length === 0) {
                        return -100;
                    } else {
                        return target[0][returnValue];
                    }
                }
            },
            pickErrorDescription: function (object, property, value, returnValue) {
                if (object === undefined || object === null || object === "") {
                    return -1;
                } else {
                    let target = object.filter(object => object[property] === value);
                    if (target.length === 0) {
                        return -100;
                    } else {
                        const codeArray = TPArray.list;
                        let targetError = codeArray.filter(codeTarget => codeTarget.code === target[0][returnValue]);
                        if (targetError.length === 0) {
                            return;
                        } else {
                            return targetError[0].description
                        }
                    }
                }
            },
        },
    };
</script>
