<template>
    <div id="SubContentWrap">
        <div class="title-box">
            <h2>
                <img src="~assets/images/dashboard/icn_dashboard_aircompressor.png" alt="aircompressor"/>
                Air Compressor
            </h2>
        </div>
        <div class="row dashboard-item-box">
            <div v-for="device in airCompressorList" :key="device.id" class="col-lg-3">
                <div class="ibox">
                    <div class="ibox-title aircompressor-ibox-title flex-ibox-title">
                        <nuxt-link :to="`/dashboard/${device.id}`">
                            <h3>
                                <div class="img-box">

                                </div>
                                {{device.name}}
                            </h3>
                        </nuxt-link>
                        <img src="~assets/images/dashboard/icn_dashboard_setting.svg" alt="setting"
                             class="setting-btn"
                             @click="settingModalOpen(device.id)"/>
                    </div>
                    <div class="ibox-content">
                        <ul class="state-box">
                            <li :class="{'run' : device.state === 'RUN'}">RUN</li>
                            <li :class="{'load' : device.state === 'LOAD'}">LOAD</li>
                            <li :class="{'unload' : device.state === 'UNLOAD'}">UNLOAD</li>
                            <li :class="{'stop' : device.state === 'STOP'}">STOP</li>
                        </ul>
                        <div :class="{'noti-box':true, 'alarm-box': device.alarm !== ''}">
                            <div v-if="device.alarm ===''" class="normal">Normal</div>
                            <div v-else class="alarm">Alarm</div>
                            <div class="text" v-if="device.alarm !==''">
                                {{device.alarm}}
                            </div>
                        </div>
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
        <settingEquipmentModal ref="settingEquipmentModal" v-bind:propsdata="settingModalData"/>
        <Loading v-bind:propsdata="loadingData"/>
    </div>
</template>
<script>
    import dayjs from 'dayjs';
    import axios from 'axios';
    import Loading from '~/components/loading.vue';
    import settingEquipmentModal from '~/components/settingModal/settingEquipmentModal.vue';

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
            settingEquipmentModal
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
                tagVal: '',
                todayTime: '',
                airCompressorList: [
                    {id: 1, state: 'RUN', alarm: '', name: 'Ingersoll Rand 100'},
                    {id: 2, state: 'STOP', alarm: '', name: 'Ingersoll Rand 200'},
                    {id: 3, state: 'LOAD', alarm: '', name: 'Ingersoll Rand 150'},
                    {id: 4, state: 'UNLOAD', alarm: '온도 2단계 알람이 발생했습니다.', name: 'YUJIN 100'},
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
            settingModalOpen (id) {
                this.$refs.settingEquipmentModal.createdModal(id);
                this.settingModalData.show = true;
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
