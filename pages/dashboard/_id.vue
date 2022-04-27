<template>
    <div id="SubContentWrap">
        <div class="title-box">
            <h2>
                <img src="~assets/images/dashboard/icn_dashboard_aircompressor.png" alt="aircompressor"/>
                {{airCompressor.name}}
            </h2>
        </div>
        <div class="row">
            <div class="col-lg-4">
                <div class="ibox">
                    <div class="ibox-title ibox-noborder-title ibox-no-padding-content right">

                        <img src="~assets/images/dashboard/icn_dashboard_setting.svg" alt="setting"
                             class="setting-btn"
                             @click="settingModalOpen(airCompressor.id)"/>
                    </div>
                    <div class="ibox-title center">
                        <img
                                :src="compressorImage"
                                @error="replaceImg"
                                :alt="airCompressor.equipmentId"
                                style="max-width:100%;"/>
                    </div>
                    <div class="ibox-title">
                        <ul class="modal-info-box">
                            <li>
                                <div class="title">그룹명</div>
                                <p>{{airCompressor.groupName}}</p>
                            </li>
                            <li>
                                <div class="title">공기압축기명</div>
                                <p>{{airCompressor.name}}</p>
                            </li>
                            <li>
                                <div class="title">Model</div>
                                <p v-if="airCompressor.equipment">{{airCompressor.equipment.model}}</p>
                            </li>
                        </ul>
                    </div>
                    <div class="ibox-content">
                        <airCompressorState v-bind:propsdata="airCompressor.state"/>
                    </div><!--
                    <div :class="{'noti-box':true, 'alarm-box': airCompressor.alarm}">
                        <div v-if="airCompressor.alarm" class="alarm">Alarm</div>
                        <div v-else class="normal">Normal</div>
                        <div class="text" v-if="airCompressor.alarm">
                            {{airCompressor.alarmMention}}
                        </div>
                    </div>-->
                </div>

                <div class="ibox">
                    <div class="ibox-title">
                        공기압축기 전체 정보
                    </div>
                    <div class="ibox-content">
                        <ul class="tag-box" v-if="airCompressor.tags">
                            <li v-for="type in compTagSet" :key="type.tagName">
                                <div v-if="airCompressor.tags[type] !== undefined">
                                    {{airCompressor.tags[type].description}}
                                </div>
                                <div v-if="airCompressor.tags[type] !== undefined">
                                    {{airCompressor.tags[type].value.toFixed(2)}} {{airCompressor.tags[type].unit}}
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
                <div class="ibox" v-if="airCompressor.devices">
                    <div class="ibox-title">
                        전력 정보
                    </div>
                    <div class="ibox-content">
                        <ul v-for="power in airCompressor.devices.power" :key="power.id" class="tag-box">
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
            <div class="col-lg-8">
                <div v-if="airCompressor.state">
                    <div class="ibox" v-if="airCompressor.state['COMP_Trip'].value === 1">
                        <div class="ibox-content flex-box">
                            <div class="bom-badge red-bg-badge" style="margin:0 8px 0 0;">Trip</div>
                            <div>{{TPCode[airCompressor.state['COMP_ActTripCode'].value.toString()]}}</div>
                        </div>
                    </div>
                    <div class="ibox" v-if="airCompressor.state['COMP_Warning'].value === 1">
                        <div class="ibox-content flex-box">
                            <div class="bom-badge orange-bg-badge" style="margin:0 8px 0 0;">warning</div>
                            <div>{{TPCode[airCompressor.state['COMP_ActWarCode'].value.toString()]}}</div>
                        </div>
                    </div>
                </div>
                <div class="ibox">
                    <div class="ibox-title flex-ibox-title">
                        실시간 전력 Chart
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
                                <ul class="tag-box" v-if="airCompressor.tags">
                                    <li v-for="type in compKeyTag" :key="type.tagName">
                                        <div v-if="airCompressor.tags[type] !== undefined">
                                            {{airCompressor.tags[type].description}}
                                        </div>
                                        <div v-if="airCompressor.tags[type] !== undefined">
                                            {{airCompressor.tags[type].value.toFixed(2)}}
                                            {{airCompressor.tags[type].unit}}
                                        </div>
                                    </li>
                                </ul>

                                <ul v-for="power in airCompressor.devices.power" :key="power.id" class="tag-box">
                                    <li v-for="type in powerKeyTag">
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
                </div>
                <div class="ibox">
                    <alarm/>
                </div>
            </div>
        </div>
        <settingEquipmentModal ref="settingEquipmentModal" v-bind:propsdata="settingModalData" v-on:callSearch="getAirCompressor"/>
        <Loading v-bind:propsdata="loadingData"/>
    </div>
</template>
<script>
    import dayjs from 'dayjs';
    import axios from 'axios';
    import Loading from '~/components/loading.vue';
    import settingEquipmentModal from '~/components/settingModal/settingEquipmentModal.vue';
    import airCompressorState from '~/components/dashboard/airCompressorState.vue';
    import alarm from '~/components/dashboard/alarm.vue';
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
            alarm,
            waTagSet,
        },
        data() {
            return {
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
                TPCode: '',
                airCompressor: {
                    devices: {},
                },
                compressorImage: '',
                compKeyTag: waTagSet.airKeyTag.tags,
                powerKeyTag: waTagSet.powerKeyTag.tags,
                compTagSet: waTagSet.airCompSet.tags,
                powerTagSet: waTagSet.accuraSet.tags,
                timeCategories: [],
                nowTime: '',
                liveChartData: [{name: '실시간 유효전력', data: []}], // 데이터 변수
                liveChartOption: {
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
                alarmList: [],
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
            this.getAirCompressor();
            this.WaLogin();
            this.getTagValues();
            this.resetInterval();
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
                });
            },
            async getAirCompressor() {
                const vm = this;
                const id = this.$route.params.id;
                axios.get(`/api/compressor/${id}`
                ).then((res) => {
                    vm.airCompressor = res.data;
                    vm.compressorImage = require(`~/assets/images/equipment/${vm.airCompressor.equipment.model}.jpg`);
                }).catch((error) => {
                    vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },
            replaceImg(e) {
                e.target.src = require(`~/assets/images/equipment/ingersollrand100.jpg`);
            },
            getNowTime: function () {
                this.nowTime = dayjs(new Date().toISOString()).format('HH:mm:ss');
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
            settingModalOpen(device) {
                this.$refs.settingEquipmentModal.updateModal(device);
                this.settingModalData.show = true;
            },
            setLiveChart: function () {
                const vm = this;
                vm.getNowTime();
                let liveChartXcount = this.liveChartData[0].data.length;
                let maxCount = 150;

                //X좌표 설정
                if (liveChartXcount > maxCount) {
                    vm.timeCategories.shift();
                    vm.liveChartData[0].data.shift();
                }
                vm.timeCategories.push(vm.nowTime);

                //Y좌표 설정
                let data = this.$options.filters.pickValue(vm.tagVal, 'Name', `${vm.airCompressor.unitId}_PWR_KW`, 'Value');
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
                        if (targetError.length !== 0) {
                            return targetError[0].description
                        }
                    }
                }
            },
        },
    };
</script>
