<template>
    <div id="SubContentWrap">
        <div class="title-box">
            <h2>
                <img src="~assets/images/dashboard/icn_dashboard_aircompressor.png" alt="aircompressor"/>
                Ingersoll Rand 100
            </h2>
        </div>
        <div class="row">
            <div class="col-lg-4">
                <div class="ibox">
                    <div class="ibox-title center">
                        <img :src="require(`~/assets/images/equipment/${airCompressor[0].equipmentId}.jpg`)"
                             :alt="airCompressor[0].equipmentId"
                             style="max-width:100%;"/>
                    </div>
                    <div class="ibox-title">
                        <ul class="modal-info-box">
                            <li>
                                <div class="title">Model Name</div>
                                <p>Ingersoll Rand</p>
                            </li>
                            <li>
                                <div class="title">Facility Number</div>
                                <p>12345678</p>
                            </li>
                        </ul>
                    </div>
                    <div class="ibox-content">

                        <ul class="state-box">
                            <li :class="{'run' : airCompressor[0].state === 'RUN'}">RUN</li>
                            <li :class="{'load' : airCompressor[0].state === 'LOAD'}">LOAD</li>
                            <li :class="{'unload' : airCompressor[0].state === 'UNLOAD'}">UNLOAD</li>
                            <li :class="{'stop' : airCompressor[0].state === 'STOP'}">STOP</li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="col-lg-8">
                <div class="ibox">
                    <div class="ibox-title">
                        실시간 공기압축기 압력 Chart
                    </div>
                    <div class="ibox-content">
                        <client-only>
                            <apexchart type="area" height="200" ref="liveChart" :options="liveChartOption"
                                       :series="liveChartData"/>
                        </client-only>
                    </div>
                </div>
                <div class="row">
                    <div class="col-lg-6">
                        <div class="ibox">
                            <div class="ibox-title">
                                실시간 공기압축기 압력 Gauge
                            </div>
                            <div class="ibox-content">
                                <client-only>
                                    <apexchart type="radialBar" height="280" ref="liveCTLineChart"
                                               :options="radialChartOptions"
                                               :series="[75]"/>
                                </client-only>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-6">
                        <div class="ibox">
                            <div class="ibox-title">
                                공기압축기 정보
                            </div>
                            <div class="ibox-content">
                                <ul class="tag-box">
                                    <li v-for="tag in airTagList" :key="tag.id">
                                        <div class="tagname">{{tag.name}}</div>
                                        <div>{{tag.unit}}</div>
                                    </li>
                                </ul>
                            </div>
                        </div>
                    </div>
                </div>
                <div class="ibox">
                    <div class="ibox-title">
                        알람 정보
                    </div>
                    <div class="ibox-content">
                        <DxDataGrid :data-source="alarmList" :show-borders="false" key-expr="id"
                                    :column-min-width="100"
                                    :column-auto-width="true">
                            <DxSearchPanel :visible="true" :highlight-case-sensitive="true"/>
                            <DxColumn data-field="id" caption="No" alignment="center"/>
                            <DxColumn data-field="msg" caption="Message"/>
                            <DxColumn data-field="temp" caption="Data" cell-template="dataGridTemplate"/>
                            <DxColumn data-field="state" caption="State"/>
                            <DxPaging :enabled="true" :page-size="5"/>
                            <DxPager :show-page-size-selector="true" :allowed-page-sizes="pageSizes" :show-info="true"/>
                            <template #dataGridTemplate="{ data: cellData }">
                                <dataGridTemplate :cell-data="cellData"/>
                            </template>
                        </DxDataGrid>

                    </div>
                </div>
            </div>
        </div>
        <Loading v-bind:propsdata="loadingData"/>
    </div>
</template>
<script>
    import dayjs from 'dayjs';
    import axios from 'axios';
    import Loading from '~/components/loading.vue';
    import dataGridTemplate from '~/components/gridTemplate/dataGridTemplate.vue';
    import {
        DxDataGrid,
        DxColumn,
        DxPaging,
        DxPager,
        DxSearchPanel,
    } from 'devextreme-vue/data-grid';


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
            dataGridTemplate,
            DxDataGrid,
            DxColumn,
            DxPaging,
            DxPager,
            DxSearchPanel,
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
                tagVal: '',
                todayTime: '',
                airCompressor: [
                    {id: 1, state: 'RUN', alarm: '', equipmentId:'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -1'},
                ],
                airTagList: [
                    {id: 1, name: '유량', tagName: 'PWR_KWh', unit: '㎥/min'},
                    {id: 2, name: '전력량', tagName: 'PWR_kW', unit: 'kWh'},
                    {id: 3, name: '순시전력', tagName: 'PWR_KWh', unit: 'kW'},
                    {id: 4, name: '온도', tagName: 'PWR_kW', unit: '℃'},
                    {id: 5, name: '전류', tagName: 'PWR_KWh', unit: 'A'},
                ],

                timeCategories: [],
                nowTime: '',
                liveChartData: [{
                    name: '잉가솔랜드',
                    data: []
                }], // 데이터 변수
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
                    colors: ['#27aef3'],
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
                                return val + 'kW'
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
                alarmList: [
                    {id: 1, msg: '온도상향 알람', temp: '20', bar: '50', kWh: '120', state: 'Alarm'},
                    {id: 2, msg: '온도상향 알람', temp: '20', bar: '50', kWh: '120', state: 'Alarm'},
                    {id: 3, msg: '온도상향 알람', temp: '20', bar: '50', kWh: '120', state: 'Alarm'},
                    {id: 4, msg: '온도상향 알람', temp: '20', bar: '50', kWh: '120', state: 'Alarm'},
                    {id: 5, msg: '온도상향 알람', temp: '20', bar: '50', kWh: '120', state: 'Alarm'},
                ],
                pageSizes: [5, 10, 20], // 페이지사이즈
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

            getNowTime: function () {
                this.nowTime = dayjs(new Date().toISOString()).format('HH:mm:ss');
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
                let data = Math.floor(Math.random() * 100);
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
                    vm.setLiveChart();
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
            pickTagValue: function (object, tag) {
                if (object === undefined || object === null || object === "") {
                    return -1;
                } else {
                    let target = object.filter(object => object.Name === tag);
                    if (target.length === 0) {
                        return -100;
                    } else {
                        return target[0].Value;
                    }
                }
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
