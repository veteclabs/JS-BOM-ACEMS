<template>
    <div id="SubContentWrap">
        <div class="title-box flex-box">
            <h2>
                <img src="~assets/images/dashboard/icn_dashboard_aircompressor.png" alt="aircompressor"/>
                Air Compressor
            </h2>
            <button class="button setting-button" @click="settingAirCompressorModalOpen()">TP</button>
        </div>
        <div class="row dashboard-item-box">
            <div v-for="device in airCompressorList" :key="device.id" class="col-lg-3">
                <div class="ibox">
                    <div class="ibox-title aircompressor-ibox-title flex-ibox-title">
                        <nuxt-link :to="`/dashboard/${device.id}`">
                            <h3>
                                <div class="img-box">
                                    <img :src="require(`~/assets/images/equipment/${device.equipmentId}.jpg`)"
                                         :alt="device.equipmentId"
                                         style="max-width:100%;"/>
                                </div>
                                {{device.name}}
                            </h3>
                        </nuxt-link>
                        <img src="~assets/images/dashboard/icn_dashboard_setting.svg" alt="setting"
                             class="setting-btn"
                             @click="settingModalOpen(device.id)"/>
                    </div>
                    <div class="ibox-content">
                        <airCompressorState v-bind:propsdata="device"/>
                        <div :class="{'noti-box':true, 'alarm-box': device.alarm !== ''}">
                            <div v-if="device.alarm ===''" class="normal">Normal</div>
                            <div v-else class="alarm">Alarm</div>
                            <div class="text" v-if="device.alarm !==''">
                                {{device.alarm}}
                            </div>
                        </div>
                        <ul class="tag-box">
                            <li>
                                <div class="tagname">부하율</div>
                                <div><h3>68%</h3></div>
                            </li>
                            <li v-if="device.alarm !== ''">
                                <div class="bom-badge red-bg-badge"
                                     style="margin:0 8px 0 0;">Trip</div>
                                <div>High VSD Temperature</div>
                            </li>
                        </ul>
                        <ul class="tag-box">
                            <li v-for="tag in airTagList" :key="tag.id">
                                <div class="tagname">{{tag.name}}</div>
                                <div>{{tag.unit}}</div>
                            </li>
                        </ul>
                        <ul class="tag-box">
                            <li v-for="tag in powerTagList" :key="tag.id">
                                <div class="tagname">{{tag.name}}</div>
                                <div>{{tag.unit}}</div>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>

        <div class="title-box">
            <h2>
                <img src="~assets/images/dashboard/icn_dashboard_thermometer.png" alt="thermometer"/>
                Thermometer
            </h2>
        </div>

        <div class="row dashboard-item-box">
            <div v-for="item in thermometerList" :key="item.id" class="col-lg-3">
                <div class="ibox">
                    <div class="ibox-title">
                        <h3>{{item.name}}</h3>
                    </div>
                    <div class="ibox-content">
                        <ul class="tag-box">
                            <li v-for="tag in item.tag" :key="tag.id">
                                <div class="tagname">{{tag.name}}</div>
                                <div>{{tag.unit}}</div>
                            </li>
                        </ul>
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
                todayTime: '',
                airCompressorList: [
                    {id: 1, state: 'RUN', alarm: '', equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -1'},
                    {id: 2, state: 'STOP', alarm: '', equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -2'},
                    {id: 3, state: 'LOAD', alarm: '', equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -3'},
                    {id: 4, state: 'UNLOAD', alarm: '온도 2단계 알람이 발생했습니다.', equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -4'},
                ],
                airTagList: [
                    {id: 1, name: '전류', tagName: 'PWR_KWh', unit: 'A'},
                    {id: 2, name: '전압', tagName: 'PWR_kW', unit: 'V'},
                    {id: 3, name: '압력', tagName: 'PWR_KWh', unit: 'Bar'},
                    {id: 4, name: '온도', tagName: 'PWR_kW', unit: '℃'},
                ],
                powerTagList: [
                    {id: 11, name: '전력량', tagName: 'PWR_KWh', unit: 'kWh'},
                    {id: 12, name: '순시전력', tagName: 'PWR_kW', unit: 'kW'},
                ],
                thermometerList: [
                    {id: 1, name: '흡착식 온도계', tag: [{name: '온도', tagName: 'temp', unit: '℃'}]},
                    {
                        id: 2,
                        name: '온도계#1',
                        tag: [{name: 'In', tagName: 'temp', unit: '℃'}, {name: 'Out', tagName: 'temp', unit: '℃'}]
                    },
                    {
                        id: 3,
                        name: '온도계#2',
                        tag: [{name: 'In', tagName: 'temp', unit: '℃'}, {name: 'Out', tagName: 'temp', unit: '℃'}]
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
            this.timer();
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
            timer() {
                this.todayTime = dayjs().format('YYYY-MM-DD ddd A hh:mm');
            },
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
                    portId: [-2, 33],
                }, {
                    timeout: vm.intervalTime,
                }).then((res) => {
                    if (res.data.Result.Total > 0) {
                        vm.tagVal = res.data.Values;
                        vm.todayTime = dayjs().format('YYYY-MM-DD ddd A hh:mm');
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
        },
    };
</script>
