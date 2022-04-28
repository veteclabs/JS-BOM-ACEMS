<template>
    <div id="SubContentWrap">
        <div class="row">
            <div class="col-lg-3">
                <div class="ibox">
                    <div class="ibox-title flex-ibox-title">실시간 유량</div>
                    <div class="ibox-content">
                        <client-only>
                            <apexchart type="radialBar" height="240" ref="liveCTLineChart"
                                       :options="radialChartOptions"
                                       :series="[totalFlow]"/>
                        </client-only>
                    </div>
                </div>
            </div>
            <div class="col-lg-6">
                <div class="ibox">
                    <div class="ibox-title flex-ibox-title">실시간 전력 Chart</div>
                    <div class="ibox-content">
                        <client-only>
                            <apexchart type="area" height="180" ref="liveChart" :options="liveChartOption"
                                       :series="liveChartData"/>
                        </client-only>
                    </div>
                </div>
            </div>
            <div class="col-lg-3">
                <div class="ibox">
                    <div class="ibox-title flex-ibox-title">실시간 압력</div>
                    <div class="ibox-content">
                        <client-only>
                            <apexchart type="radialBar" height="240" ref="liveCTLineChart"
                                       :options="radialChartOptions"
                                       :series="[totalCompressorBar]"/>
                        </client-only>
                    </div>
                </div>
            </div>
        </div>
        <div class="title-box flex-box">
            <h2>
                <img src="~assets/images/dashboard/icn_dashboard_aircompressor.png" alt="aircompressor"/>
                Air Compressor
            </h2>
        </div>
        <div class="dashboard-item-box">
            <masonry :cols="4" :gutter="30">
                <div v-for="device in airCompressorList" :key="device.id">
                    <div class="ibox">
                        <div class="ibox-title aircompressor-ibox-title flex-ibox-title">
                            <nuxt-link :to="`/dashboard/${device.id}`">
                                <h3>
                                    <div class="img-box">
                                        <img
                                                :src="compressorImage"
                                                @error="replaceImg"
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
                            <scheduleState v-bind:propsdata="device"/>
                            <ul class="tag-box">
                                <li>
                                    <div class="tagname">부하율</div>
                                    <div style="display:flex; flex:1; justify-content: end; align-items: center;">
                                        <div class="progressbar">
                                            <div class="inner-bar"
                                                 :style="`width:${getProgressBarValue(device.unitId)}%`"/>
                                        </div>
                                        <h3>{{tagVal | pickValue('Name',`${device.unitId}_COMP_PCY`, 'Value')}} %</h3>
                                    </div>
                                </li>
                                <li v-if="device.state['COMP_Trip'].value === 1">
                                    <div class="bom-badge red-bg-badge" style="margin:0 8px 0 0;">Trip</div>
                                    <div>{{TPCode[device.state['COMP_ActTripCode'].value.toString()]}}</div>
                                </li>
                                <li v-if="device.state['COMP_Warning'].value === 1">
                                    <div class="bom-badge orange-bg-badge" style="margin:0 8px 0 0;">warning</div>
                                    <div>{{TPCode[device.state['COMP_ActWarCode'].value.toString()]}}</div>
                                </li>
                            </ul>
                            <ul class="tag-box">
                                <li v-for="type in compTagSet" :key="type.tagName">
                                    <div v-if="device.tags[type] !== undefined">
                                        {{device.tags[type].description}}
                                    </div>
                                    <div v-if="device.tags[type] !== undefined">
                                        {{device.tags[type].value.toFixed(2)}} {{device.tags[type].unit}}
                                    </div>
                                </li>
                            </ul>

                            <ul v-for="power in device.devices.power" :key="power.tagName" class="tag-box">
                                <li v-for="type in powerTagSet">
                                    <div v-if="power.tags[type] !== undefined">
                                        {{power.tags[type].description}}
                                    </div>
                                    <div v-if="power.tags[type] !== undefined">
                                        {{power.tags[type].value.toFixed(2)}} {{power.tags[type].unit}}
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </masonry>
        </div>
        <equipmentTagGroup v-bind:propsdata="equipmentList.temperature" :title="'Thermometer'"
                           v-if="equipmentList.temperature"/>
        <equipmentTagGroup v-bind:propsdata="equipmentList.pressure" :title="'Pressure gauge'"
                           v-if=" equipmentList.pressure"/>
        <equipmentTagGroup v-bind:propsdata="equipmentList.flow" :title="'Flow gauge'" v-if=" equipmentList.flow"/>
        <settingEquipmentModal ref="settingEquipmentModal" v-bind:propsdata="settingModalData"
                               v-on:callSearch="getAirCompressor"/>
        <Loading v-bind:propsdata="loadingData"/>
    </div>
</template>
<script>
    import dayjs from 'dayjs';
    import axios from 'axios';
    import Loading from '~/components/loading.vue';
    import settingEquipmentModal from '~/components/settingModal/settingEquipmentModal.vue';
    import airCompressorState from '~/components/dashboard/airCompressorState.vue';
    import scheduleState from '~/components/dashboard/scheduleState.vue';
    import equipmentTagGroup from '~/components/dashboard/equipmentTagGroup.vue';
    import waTagSet from '~/assets/data/tagSet.json';


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
            airCompressorState,
            scheduleState,
            equipmentTagGroup
        },
        data() {
            return {
                TPCode: '',
                compTagSet: waTagSet.airCompressorGroupDeshboardSet.tags,
                powerTagSet: waTagSet.dashboardAccuraSet.tags,
                msgData: {
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
                compressorImage: '',
                tagVal: '',
                liveChartData: [{name: '실시간 유효전력', data: []}],
                liveChartOption: { //차트옵션 변수
                    chart: {
                        toolbar: {
                            tools: {
                                download: true,
                                selection: false,
                                zoom: false,
                                zoomin: false,
                                zoomout: false,
                                pan: false,
                            }
                        }
                    },
                    dataLabels: {enabled: false},
                    colors: ['#FFA100'],
                    grid: {borderColor: '#e4e9f1'},
                    stroke: {curve: 'smooth', width: 3},
                    fill: {
                        type: 'gradient',
                        gradient: {
                            shadeIntensity: 1,
                            opacityFrom: 0.3,
                            opacityTo: 0,
                            stops: [0, 100]
                        }
                    },
                    tooltip: {
                        style: {fontFamily: 'BOM-font'},
                        x: {show: true},
                        y: {
                            show: true,
                            formatter: function (val) {
                                return val + 'Bar'
                            }
                        }
                    },
                    xaxis: {
                        categories: [],
                        labels: {show: false, style: {color: ['#667082'], fontFamily: 'BOM-font', fontSize: '10px'}}
                    },
                    yaxis: {
                        forceNiceScale: true,
                        labels: {
                            style: {colors: ['#667082'], fontFamily: 'BOM-font', fontSize: '10px'},
                            formatter: (value) => {
                                return value.toFixed(1)
                            }
                        }
                    },
                    legend: {show: true, fontSize: '10px', fontFamily: 'BOM-font', offsetY: -5}
                },
                radialChartOptions: {
                    chart: {
                        type: 'radialBar',
                        toolbar: {
                            show: true
                        },
                        offsetY: -10,
                        animations: {
                            enabled: false,
                        }
                    },
                    plotOptions: {
                        radialBar: {
                            startAngle: -135,
                            endAngle: 135,
                            hollow: {
                                margin: 0,
                                size: '70%',
                                background: '#fff',
                                position: 'front',
                                dropShadow: {
                                    enabled: true,
                                    top: -3,
                                    left: 0,
                                    blur: 4,
                                    opacity: 0.05
                                }
                            },
                            track: {
                                background: '#f5f5f5',
                                strokeWidth: '67%',
                                margin: 0, // margin is in pixels
                            },

                            dataLabels: {
                                show: true,
                                name: {
                                    offsetY: 40,
                                    show: true,
                                    color: '#6c6c6c',
                                    fontSize: '18px',
                                    fontWeight: 500,
                                    fontFamily: 'BOM-font", Sans-serif',
                                },
                                value: {
                                    formatter: function (val) {
                                        return parseInt(val);
                                    },
                                    offsetY: -10,
                                    color: '#1b1b1b',
                                    fontSize: '40px',
                                    fontWeight: 600,
                                    fontFamily: 'BOM-font", Sans-serif',
                                    show: true,
                                }
                            }
                        }
                    },
                    fill: {
                        type: 'gradient',
                        colors: ['#c24285'],
                        gradient: {
                            shade: 'dark',
                            type: 'horizontal',
                            shadeIntensity: 0.5,
                            gradientToColors: ['#3363ff'],
                            inverseColors: true,
                            opacityFrom: 1,
                            opacityTo: 1,
                            stops: [0, 100]
                        }
                    },
                    stroke: {
                        lineCap: 'round'
                    },
                    labels: ['bar'],
                },
                totalFlow: 0,
                totalCompressorBar: 0,
                airCompressorList: [],
                equipmentList: [],
                timeCategories: [],
                Interval1M: '',
                interval: '',
                intervalTime: 20 * 1000,
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
            this.getAirCompressor();
            this.getEquipment();
            this.getTrip();
            this.loadingData.show = true;
        },
        beforeDestroy() {
            this.removeInterval();
        },
        methods: {
            async WaLogin() {
                await axios.get('/nuxt/WaLogin')
            },
            async getTagValues() {
                const vm = this;
                axios.post('/nuxt/wa/port/getTagValue', {
                    portId: [1, 2, 3, 4, 5],
                }, {
                    timeout: vm.intervalTime,
                }).then((res) => {
                    if (res.data.Result.Total > 0) {
                        vm.tagVal = res.data.Values;
                        vm.totalFlow = this.$options.filters.pickValue(vm.tagVal, 'Name', `AU_AIR_FLOW`, 'Value');
                        vm.totalCompressorBar = this.$options.filters.pickValue(vm.tagVal, 'Name', `AU_AIR_PRE `, 'Value');
                        vm.setLiveChart();
                    }
                }).catch((error) => {
                    vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },
            getNowTime: function () {
                this.nowTime = dayjs(new Date().toISOString()).format('HH:mm:ss');
            },
            replaceImg(e) {
                e.target.src = require(`~/assets/images/equipment/ingersollrand100.jpg`);
            },
            async getTrip() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/trip'
                }).then((res) => {
                    vm.TPCode = res.data
                })
            },
            async getAirCompressor() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/compressors'
                }).then((res) => {
                    vm.airCompressorList = res.data
                }).catch((error) => {
                    vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },
            async getEquipment() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/etcs'
                }).then((res) => {
                    vm.equipmentList = res.data;
                }).catch((error) => {
                    vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },
            settingModalOpen(device) {
                this.$refs.settingEquipmentModal.updateModal(device);
                this.settingModalData.show = true;
            },
            getProgressBarValue(unit) {
                const vm = this;
                let value = this.$options.filters.pickValue(vm.tagVal, 'Name', `${unit}_COMP_PCY`, 'Value');
                if (value < 0) {
                    value = 0;
                }
                return value;
            },
            setLiveChart: function () {
                const vm = this;
                vm.getNowTime();
                let liveChartXcount = this.liveChartData[0].data.length;
                let maxCount = 150;

                if (liveChartXcount > maxCount) {
                    vm.timeCategories.shift();
                    vm.liveChartData[0].data.shift();
                }
                vm.timeCategories.push(vm.nowTime);

                let data = this.$options.filters.pickValue(vm.tagVal, 'Name', `AU_PWR_KW`, 'Value');
                vm.liveChartData[0].data.push(data);
                vm.$refs.liveChart.updateOptions({
                    "xaxis": {"categories": vm.timeCategories}
                });
            },
            resetInterval() {
                const vm = this;
                clearInterval(this.interval);
                vm.interval = null;
                vm.interval = setInterval(() => {
                    vm.getTagValues();
                    vm.getAirCompressor();
                    vm.getEquipment();
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
