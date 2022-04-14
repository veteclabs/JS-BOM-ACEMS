<template>
    <div id="SubContentWrap">
        <div class="row">
            <div class="col-lg-3">
                <div class="ibox">
                    <div class="ibox-title flex-ibox-title">실시간 주요 정보</div>
                    <div class="ibox-content">
                        <ul class="tag-box">
                            <li>
                                <div class="tagname">총 전력량</div>
                                <div>{{tagVal | pickValue('Name',`AU_PWR_kWH`, 'Value')}}kWh</div>
                            </li>
                            <li>
                                <div class="tagname">총 순시전력</div>
                                <div>{{tagVal | pickValue('Name',`AU_PWR_kW`, 'Value')}}kW</div>
                            </li>
                            <li>
                                <div class="tagname">누적 유량</div>
                                <div>{{tagVal | pickValue('Name',`AU_WAR_Con`, 'Value')}}㎥/min</div>
                            </li>
                        </ul>
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
                                       :series="[airCompressorBar]"/>
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
        <div class="row dashboard-item-box">
            <div v-for="device in airCompressorList" :key="device.id" class="col-lg-3">
                <div class="ibox">
                    <div class="ibox-title aircompressor-ibox-title flex-ibox-title">
                        <nuxt-link :to="`/dashboard/${device.id}`">
                            <h3>
                                <div class="img-box">
                                    <img v-if="device.equipmentId"
                                         :src="require(`~/assets/images/equipment/${device.equipmentId}.jpg`)"
                                         :alt="device.equipmentId"
                                         style="max-width:100%;"/>
                                </div>
                                {{device.name}}
                            </h3>
                        </nuxt-link>
                        <img src="~assets/images/dashboard/icn_dashboard_setting.svg" alt="setting"
                             class="setting-btn"
                             @click="settingModalOpen(device)"/>
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
                                <div style="display:flex; flex:1; justify-content: end; align-items: center;">
                                    <div class="progressbar">
                                        <div class="inner-bar" :style="`width:${getProgressBarValue(device.unit)}%`"/>
                                    </div>
                                    <h3>{{tagVal | pickValue('Name',`${device.unit}_COMP_PCY`, 'Value')}} %</h3>
                                </div>
                            </li>
                            <li v-if="device.alarm !== ''">
                                <div class="bom-badge red-bg-badge"
                                     style="margin:0 8px 0 0;">Trip
                                </div>
                                <div>{{tagVal | pickErrorDescription('Name',`${device.unit}_COMP_WC`, 'Value')}}</div>
                            </li>
                        </ul>
                        <ul class="tag-box">
                            <li v-for="tag in airTagList" :key="tag.id">
                                <div class="tagname">{{tag.name}}</div>
                                <div>
                                    {{tagVal | pickValue('Name',`${device.unit}_${tag.tagName}`, 'Value')}}
                                    {{tag.unit}}
                                </div>
                            </li>
                        </ul>
                        <ul class="tag-box">
                            <li v-for="tag in powerTagList" :key="tag.id">
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
                                <div>
                                    {{tagVal | pickValue('Name',`${item.unit}_${tag.tagName}`, 'Value')}}
                                    {{tag.unit}}
                                </div>
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
                liveChartData: [{name: '실시간 유효전력', data: []}], // 데이터 변수
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
                airCompressorBar: 0,
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
                    {id: 3, name: 'KW시간', tagName: 'COMP_KWH', unit: ''},
                    {id: 4, name: '총 시간', tagName: 'COMP_TH', unit: ''},
                ],
                powerTagList: [
                    {id: 5, name: '유효전력량', tagName: 'PWR_KWh', unit: 'KWh'},
                    {id: 6, name: '무효전력량', tagName: 'PWR_Kvarh', unit: 'Kvarh'},
                    {id: 7, name: '전압', tagName: 'PWR_V', unit: 'V'},
                    {id: 8, name: '전류', tagName: 'PWR_A', unit: 'A'},
                    {id: 9, name: '역률', tagName: 'PWR_PF', unit: '%'},
                    {id: 10, name: '유효전력', tagName: 'PWR_KW', unit: 'kW'},
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
                timeCategories: [],
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
            this.getCompressor();
            this.loadingData.show = true;
        },
        beforeDestroy() {
            this.removeInterval();
        },
        methods: {
            getNowTime: function () {
                this.nowTime = dayjs(new Date().toISOString()).format('HH:mm:ss');
            },
            async WaLogin() {
                const vm = this;
                axios.get('/api/WaLogin')
                    .catch((error) => {
                        vm.msgData.msg = error;
                    });
            },
            async getCompressor() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/compressors',
                }).then((res) => {
                    vm.airCompressorList = res.data
                }).catch((error) => {
                    vm.msgData.msg = error;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },
            async getTagValues() {
                const vm = this;
                axios.post('/api/wa/port/getTagValue', {
                    portId: [1, 2, 3, 4, 5],
                }, {
                    timeout: vm.intervalTime,
                }).then((res) => {
                    if (res.data.Result.Total > 0) {
                        vm.tagVal = res.data.Values;
                        vm.airCompressorBar = this.$options.filters.pickValue(vm.tagVal, 'Name', `AU_COMP_PDP`, 'Value');
                        vm.setLiveChart();
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
                //실시간유효전력량 Graph 실시간 변경
                let liveChartXcount = this.liveChartData[0].data.length;
                let maxCount = 150;

                //X좌표 설정
                if (liveChartXcount > maxCount) {
                    vm.timeCategories.shift();
                    vm.liveChartData[0].data.shift();
                }
                vm.timeCategories.push(vm.nowTime);

                //Y좌표 설정
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
