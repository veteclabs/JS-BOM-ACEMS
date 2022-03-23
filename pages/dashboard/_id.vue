<template>
    <div id="SubContentWrap">
        <div class="title-box">
            <h2>
                <img src="~assets/images/dashboard/icn_dashboard_aircompressor.png" alt="aircompressor"/>
                {{airCompressor[0].name}}
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
                        <airCompressorState v-bind:propsdata="airCompressor[0]"/>
                    </div>
                </div>

                <div class="ibox">
                    <div class="ibox-title">
                        공기압축기 전체 정보
                    </div>
                    <div class="ibox-content">
                        <ul class="tag-box">
                            <li v-for="tag in airTagList" :key="tag.id">
                                <div class="tagname">{{tag.name}}</div>
                                <div>
                                    {{tagVal | pickValue('Name',`${airCompressor[0].unit}_${tag.tagName}`, 'Value')}}
                                    {{tag.unit}}
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
                <div class="ibox">
                    <div class="ibox-title">
                        전력 정보
                    </div>
                    <div class="ibox-content">
                        <ul class="tag-box">
                            <li v-for="tag in powerTagList" :key="tag.id">
                                <div class="tagname">{{tag.name}}</div>
                                <div>
                                    {{tagVal | pickValue('Name',`${airCompressor[0].unit}_${tag.tagName}`, 'Value')}}
                                    {{tag.unit}}
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="col-lg-8">
                <div class="ibox">
                    <div class="ibox-content flex-box">
                        <div class="bom-badge red-bg-badge" style="margin:0 8px 0 0;">Trip</div>
                        <div>{{tagVal | pickErrorDescription('Name',`${airCompressor[0].unit}_COMP_WC`, 'Value')}}</div>
                    </div>
                </div>
                <div class="ibox">
                    <div class="ibox-title flex-ibox-title">
                        실시간 공기압축기 압력 Chart
                        <img src="~assets/images/dashboard/icn_dashboard_setting.svg" alt="setting"
                             class="setting-btn"
                             @click="settingModalOpen(airCompressor[0].id)"/>
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
                                    <apexchart type="radialBar" height="285" ref="liveCTLineChart"
                                               :options="radialChartOptions"
                                               :series="[airCompressorBar]"/>
                                </client-only>
                            </div>
                        </div>
                    </div>
                    <div class="col-lg-6">
                        <div class="ibox">
                            <div class="ibox-title">
                                공기압축기 주요 정보
                            </div>
                            <div class="ibox-content">
                                <ul class="tag-box">
                                    <li v-for="tag in mainTagList" :key="tag.id">
                                        <div class="tagname">{{tag.name}}</div>
                                        <div>
                                            {{tagVal | pickValue('Name',`${airCompressor[0].unit}_${tag.tagName}`, 'Value')}}
                                            {{tag.unit}}
                                        </div>
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
                            <DxColumn data-field="msg" caption="Message" cell-template="blockGridAlarmTemplate"/>
                            <DxColumn data-field="temp" caption="Data" cell-template="dataGridTemplate"/>
                            <DxColumn data-field="state" caption="State" cell-template="blockGridAlarmTemplate"
                                      :width="100"/>
                            <DxPaging :enabled="true" :page-size="5"/>
                            <DxPager :show-page-size-selector="true" :allowed-page-sizes="pageSizes" :show-info="true"/>
                            <template #blockGridAlarmTemplate="{ data: cellData }">
                                <blockGridAlarmTemplate :cell-data="cellData"/>
                            </template>
                            <template #dataGridTemplate="{ data: cellData }">
                                <dataGridTemplate :cell-data="cellData"/>
                            </template>
                        </DxDataGrid>

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
    import dataGridTemplate from '~/components/gridTemplate/dataGridTemplate.vue';
    import blockGridAlarmTemplate from '~/components/gridTemplate/blockGridAlarmTemplate.vue';
    import airCompressorState from '~/components/dashboard/airCompressorState.vue';
    import {
        DxDataGrid,
        DxColumn,
        DxPaging,
        DxPager,
        DxSearchPanel,
    } from 'devextreme-vue/data-grid';
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
            dataGridTemplate,
            blockGridAlarmTemplate,
            airCompressorState,
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
                settingModalData: {
                    show: false,
                },
                tagVal: '',
                airCompressor: [
                    {
                        id: 1,
                        unit: 'U001',
                        state: 'RUN',
                        alarm: '',
                        equipmentId: 'ingersollrand_rm55',
                        name: 'Ingersoll Rand RM55 -1'
                    },
                ],
                mainTagList: [
                    {id: 1, name: '유효전력량', tagName: 'PWR_KWh', unit: 'KWh'},
                    {id: 2, name: '유효전력', tagName: 'PWR_KW', unit: 'kW'},
                    {id: 3, name: '전압', tagName: 'PWR_V', unit: 'V'},
                    {id: 4, name: '패키치 배출압력', tagName: 'COMP_PDP', unit: ''},
                    {id: 5, name: '에어앤드온도', tagName: 'COMP_AT', unit: ''},
                    {id: 7, name: '총 시간', tagName: 'COMP_TH', unit: ''},
                ],
                airTagList: [
                    {id: 2, name: '패키지 배출 압력', tagName: 'COMP_PDP', unit: ''},
                    {id: 3, name: '압력에서 냉각수 필터', tagName: 'COMP_CFIP', unit: ''},
                    {id: 4, name: '냉각수에서 필터 아웃 압력', tagName: 'COMP_CFOP', unit: ''},
                    {id: 5, name: '원격압력', tagName: 'COMP_RP', unit: ''},
                    {id: 1, name: '퍼센트 용량', tagName: 'COMP_PCY', unit: ''},
                    {id: 2, name: 'KW시간', tagName: 'COMP_KWH', unit: ''},
                    {id: 4, name: '목표 압력', tagName: 'COMP_TP', unit: ''},
                    {id: 5, name: '자동 정지 압력', tagName: 'COMP_ASP', unit: ''},
                    {id: 5, name: '즉시 정지 압력', tagName: 'COMP_ISP', unit: ''},
                    {id: 7, name: '시동장치 코드', tagName: 'COMP_TC', unit: ''},
                ],
                powerTagList: [
                    {id: 2, name: '무효전력량', tagName: 'PWR_Kvarh', unit: 'Kvarh'},
                    {id: 3, name: '전압', tagName: 'PWR_V', unit: 'V'},
                    {id: 4, name: '전류', tagName: 'PWR_A', unit: 'A'},
                    {id: 5, name: '역률', tagName: 'PWR_PF', unit: '%'},
                ],
                timeCategories: [],
                nowTime: '',
                liveChartData: [{name: '잉가솔랜드', data: []}], // 데이터 변수
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
                            enabled:false,
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
                airCompressorBar:0,
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
            this.WaLogin();
            this.getTagValues();
            this.resetInterval();
            this.loadingData.show = true;
        },
        beforeDestroy() {
            this.removeInterval();
        },
        methods: {
            async WaLogin() {
                const vm = this;
                axios.get('/api/WaLogin')
            },
            async getTagValues() {
                const vm = this;
                axios.post('/api/dashboard/port/getTagValue', {
                    portId: [1, 5],
                }, {
                    timeout: vm.intervalTime,
                }).then((res) => {
                    if (res.data.Result.Total > 0) {
                        vm.tagVal = res.data.Values;
                        vm.airCompressorBar = this.$options.filters.pickValue(vm.tagVal,'Name',`${vm.airCompressor[0].unit}_COMP_PDP`, 'Value');
                    }
                    vm.setLiveChart();
                }).catch((error) => {
                    vm.msgData.msg = error;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },

            getNowTime: function () {
                this.nowTime = dayjs(new Date().toISOString()).format('HH:mm:ss');
            },
            settingModalOpen(id) {
                this.$refs.settingEquipmentModal.createdModal(id);
                this.settingModalData.show = true;
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
                let data = vm.airCompressorBar;
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
                            return
                        }else {
                            return targetError[0].description
                        }
                    }
                }
            },
        },
    };
</script>
