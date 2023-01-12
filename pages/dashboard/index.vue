<template>
    <div id="SubContentWrap">
        <div class="row">
            <!--<div class="col-lg-3">
                <div class="ibox">
                    <div class="ibox-title flex-ibox-title">실시간 유량</div>
                    <div class="ibox-content">
                        <apexchart type="radialBar" height="240" ref="flowRadialBar"
                                   :options="flowRadialChartOptions"
                                   :series="[totalFlow]"/>
                    </div>
                </div>
            </div>-->
            <div class="col-lg-9">
                <div class="ibox">
                    <div class="ibox-title flex-ibox-title">실시간 전력(kW)/ 유량(m3/min)</div>
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
                    <div class="ibox-title flex-ibox-title">Specific Power - Air Cooled</div>
                    <div class="ibox-content">
                        <client-only>
                            <apexchart type="radialBar" height="240" ref="barRadialBar"
                                       :options="basicUnitOptions"
                                       :series="[basicUnit]"/>
                        </client-only>
                    </div>
                </div>
            </div>
        </div>

        <equipmentTagGroup v-bind:propsdata="equipmentList.pressure" :title="'압력계'"
                           v-if=" equipmentList.pressure"/>
        <div class="title-box flex-box">
            <h2>
                <img src="~assets/images/dashboard/icn_dashboard_aircompressor.png" alt="aircompressor"/>
                공기압축기
            </h2>
        </div>
        <div class="dashboard-item-box row">
            <!--<masonry :cols="{default: 4, 1700: 3, 1400: 2, 970: 1}" :gutter="30">-->
            <div v-for="device in airCompressorList" :key="device.id" class="col-lg-3">
                <div class="ibox" style="height:100%;">
                    <div class="ibox-title aircompressor-ibox-title flex-ibox-title">
                        <nuxt-link :to="`/dashboard/${device.id}`">
                            <h3>
                                <div class="img-box">
                                    <img
                                            :src="device.image"
                                            @error="replaceImg"
                                            :alt="device.equipmentId"
                                            style="width:100%;"/>
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
                        <div v-if="device.state.COMP_LoadFactor !== undefined || device.state.COMP_Trip !== undefined || device.state.COMP_Warning !== undefined">
                            <ul class="tag-box">
                                <li v-if="device.state.COMP_LoadFactor">
                                    <div class="tagname">부하율</div>
                                    <div style="display:flex; flex:1; justify-content: end; align-items: center;">
                                        <div class="progressbar">
                                            <div class="inner-bar"
                                                 :style="`width:${device.state.COMP_LoadFactor.value === null ? 0 : device.state.COMP_LoadFactor.value}%`"/>
                                        </div>
                                        <h3>{{device.state.COMP_LoadFactor.value === null ? 0 :
                                            device.state.COMP_LoadFactor.value}}%</h3>
                                    </div>
                                </li>
                                <li v-if="processUndefinedValue(device.state['COMP_Trip']) === 1">
                                    <div class="bom-badge red-bg-badge" style="margin:0 8px 0 0;">Trip</div>
                                    <div>
                                        {{TPCode[processUndefinedValue(device.state['COMP_ActTripCode']).toString()]}}
                                    </div>
                                </li>
                                <li v-if="processUndefinedValue(device.state['COMP_Warning']) === 1">
                                    <div class="bom-badge red-bg-badge" style="margin:0 8px 0 0;">Trip</div>
                                    <div>{{TPCode[processUndefinedValue(device.state['COMP_ActWarCode']).toString()]}}
                                    </div>
                                </li>

                                <li v-if="processUndefinedValue(device.state['COMP_Trip']) === 0 && processUndefinedValue(device.state['COMP_ActTripCode']) === 0">
                                    <div class="bom-badge green-bg-badge" style="margin:0 8px 0 0;">Normal</div>
                                    정상
                                </li>
                            </ul>
                        </div>
                        <div v-if="device.tagByComponents !== null && device.tagByComponents.mainInfoComponent !== undefined">
                            <ul v-if="device.tagByComponents !== null" class="tag-box">
                                <li v-for="tag in device.tagByComponents.mainInfoComponent" :key="tag.id">
                                    <div>
                                        {{tag.description}}
                                    </div>
                                    <div>
                                        {{tag.value | valueFormat(2)}} {{tag.unit}}
                                    </div>
                                </li>
                            </ul>
                        </div>
                        <div v-if="device.devices.power !==undefined">
                            <ul v-for="power in device.devices.power" :key="power.tagName" class="tag-box">
                                <li v-for="type in powerTagSet" :key="type.description">
                                    <div v-if="power.tags[type] !== undefined">
                                        {{power.tags[type].description}}
                                    </div>
                                    <div v-if="power.tags[type] !== undefined">
                                        {{power.tags[type].value| valueFormat(2)}} {{power.tags[type].unit}}
                                    </div>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <!--</masonry>-->
        </div>
        <equipmentTagGroup v-bind:propsdata="equipmentList.temperature" :title="'온도계'"
                           v-if="equipmentList.temperature"/>
        <equipmentTagGroup v-bind:propsdata="equipmentList.flow" :title="'유량계'" v-if="equipmentList.flow"/>
        <airDryerTagGroup v-bind:propsdata="airDryerList" :title="'에어 드라이어'" v-if="airDryerList && airDryerList.length !== 0"/>
        <settingEquipmentModal ref="settingEquipmentModal" v-bind:propsdata="settingModalData"
                               v-on:callSearch="getAirCompressor"/>
        <Loading v-bind:propsdata="loadingData"/>
    </div>
</template>
<script>
    import dayjs from 'dayjs';
    import 'dayjs/locale/ko';


    dayjs.locale('ko');

    import axios from 'axios';
    import Loading from '~/components/loading.vue';
    import settingEquipmentModal from '~/components/settingModal/settingEquipmentModal.vue';
    import airCompressorState from '~/components/dashboard/airCompressorState.vue';
    import scheduleState from '~/components/dashboard/scheduleState.vue';
    import equipmentTagGroup from '~/components/dashboard/equipmentTagGroup.vue';
    import airDryerTagGroup from '~/components/dashboard/airDryerTagGroup.vue';
    import waTagSet from '~/assets/data/tagSet.json';
    import qs from "qs";


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
            equipmentTagGroup,
            airDryerTagGroup
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
                tagVal: '',
                liveChartData: [
                    {name: '실시간 유효전력', data: []},
                    {name: '실시간 유량', data: []}
                ],
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
                    colors: ['#FFA100', '#3363FF'],
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
                            formatter: function (val, target) {
                                let unit = '';
                                if (target.seriesIndex === 0) {
                                    unit = 'kW'
                                }
                                if (target.seriesIndex === 1) {
                                    unit = 'N㎥/min'
                                }
                                return val + unit
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
                basicUnitOptions: {
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
                                margin: 0,
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
                                        let origValue = (val / 100) * 15;
                                        return origValue.toFixed(2)
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
                    labels: ['kW/m3/min'],
                },
                basicUnit: 0,
                airCompressorList: [],
                airDryerList: [],
                equipmentList: [],
                timeCategories: [],
                Interval1M: '',
                interval: '',
                intervalTime: 10 * 1000,
            };
        },
        mounted() {
            this.chartSetting();
            this.getTagValues();
            this.resetInterval();
            this.getAirCompressor();
            this.getEquipment();
            this.getAirDryers();
            this.getTrip();
            this.loadingData.show = true;
        },
        beforeDestroy() {
            this.removeInterval();
        },
        methods: {
          getAirDryers() {
            const vm = this;
            axios.get('/api/airDryers', {
              params: {
                components: ["stateComponent", "mainInfoComponent"]

              }, paramsSerializer: params => {
                return qs.stringify(params)
              }
            })
                .then((res) => {
                  if (res.status === 200) {
                    vm.airDryerList = res.data;
                  }
                }).catch((error) => {
              vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
            }).finally(() => {
              vm.loadingData.show = false;
            });
          },
            chartSetting() {
              axios.get('/api/specificalPower')
                  .then((res) => {
                    if (isNaN(res.data)) {
                      this.basicUnit = 0
                    } else {
                      this.basicUnit = res.data.toFixed(2);
                    }
                  });
            },
            async getTagValues() {
                const vm = this;

                axios.get('/api/totalValue')
                    .then((res) => {
                        if (res.status === 200) {
                            vm.tagVal = res.data;
                            vm.setLiveChart();
                            vm.chartSetting();
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
                e.target.src = require(`~/assets/images/equipment/default.png`);
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
                axios.get('/api/compressors',
                    {
                        params: {
                            components: ["stateComponent", "mainInfoComponent"]

                        }, paramsSerializer: params => {
                            return qs.stringify(params)
                        }
                    }).then((res) => {
                    if (res.status === 200) {
                        vm.airCompressorList = res.data;
                        vm.airCompressorList.forEach(item => {
                            item.image = require(`~/assets/images/equipment/${item.equipment.model}.png`);
                        });
                    }
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
            setLiveChart: function () {
                const vm = this;
                if (this.tagVal !== '') {
                    vm.getNowTime();
                    let liveChartXcount = this.liveChartData[0].data.length;
                    let maxCount = 150;

                    if (liveChartXcount > maxCount) {
                        vm.timeCategories.shift();
                        vm.liveChartData[0].data.shift();
                        vm.liveChartData[1].data.shift();
                    }
                    vm.timeCategories.push(vm.nowTime);

                    let kWData = vm.tagVal.PWR_KW;
                    vm.liveChartData[0].data.push(kWData.toFixed(2));


                    let flowData = vm.tagVal.AIR_Flow;
                    vm.liveChartData[1].data.push(flowData.toFixed(2));

                    vm.$refs.liveChart.updateOptions({
                        "xaxis": {"categories": vm.timeCategories}
                    });
                }
            },
            processUndefinedValue(type) {

                if (type === undefined) {
                    return 0;
                } else {

                    return type.value
                }
            },
            resetInterval() {
                const vm = this;
                clearInterval(this.interval);
                vm.interval = null;
                vm.interval = setInterval(() => {
                    this.chartSetting();
                    vm.getTagValues();
                    vm.getAirCompressor();
                    vm.getEquipment();
                    vm.getAirDryers();
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
            valueFormat: (value, numFix) => {
                value = parseFloat(value);
                if (!value) {
                    return '0';
                } else if (value <= -101 && value >= -113) {
                    return '-'
                } else {
                    return value.toLocaleString('ko-KR', {maximumFractionDigits: numFix});
                }
            },
            pickValue: function (object, property, value, returnValue) {
                if (object === undefined || object === null || object === "") {
                    return '-';
                } else {
                    let target = object.filter(object => object[property] === value);
                    if (target.length === 0) {
                        return '-';
                    } else {
                        return target[0][returnValue];
                    }
                }
            },
        },
    };
</script>

