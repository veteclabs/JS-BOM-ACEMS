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
                                style="max-width:400px; width:100%;"/>
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
                                <div class="title">모델명</div>
                                <p v-if="airCompressor.equipment">{{airCompressor.equipment.model}}</p>
                            </li>
                        </ul>
                    </div>
                    <div class="ibox-content">
                        <airCompressorState v-bind:propsdata="airCompressor"/>
                        <scheduleState v-bind:propsdata="airCompressor"/>
                    </div>
                </div>

                <div class="ibox">
                    <div class="ibox-title">
                        공기압축기 전체 정보
                    </div>
                    <div class="ibox-content">
                        <ul class="tag-box" v-if="airCompressor.tags">
                            <li v-for="tag in airCompressor.tagByComponents.wholeInfoComponent" :key="tag.id">
                                <div>
                                    {{tag.description}}
                                </div>
                                <div>
                                    {{tag.value | valueFormat(2)}} {{tag.unit}}
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
                <div class="ibox" v-if="powerData">
                    <div class="ibox-title">
                        전력 정보
                    </div>
                    <div class="ibox-content">
                        <ul v-for="power in powerData" :key="power.id" class="tag-box">
                            <li v-for="type in powerTagSet" :key="type.id">
                                <div v-if="power.tags[type] !== undefined">
                                    {{power.tags[type].description}}
                                </div>
                                <div v-if="power.tags[type] !== undefined">
                                    {{power.tags[type].value | valueFormat(2)}} {{power.tags[type].unit}}
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
                        실시간 전력(kW)
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
                                실시간 공기압축기 압력 값
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
                                    <li v-for="tag in airCompressor.tagByComponents.importantInfoComponent" :key="tag.id">
                                        <div>
                                            {{tag.description}}
                                        </div>
                                        <div>
                                            {{tag.value | valueFormat(2)}} {{tag.unit}}
                                        </div>
                                    </li>

                                </ul>
                                <ul v-for="power in airCompressor.devices.power" :key="power.id" class="tag-box">
                                    <li v-for="type in powerKeyTag" :key="type.id">
                                        <div v-if="power.tags[type] !== undefined">
                                            {{power.tags[type].description}}
                                        </div>
                                        <div v-if="power.tags[type] !== undefined">
                                            {{power.tags[type].value | valueFormat(2)}} {{power.tags[type].unit}}
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
    import alarm from '~/components/dashboard/alarm.vue';
    import waTagSet from '~/assets/data/tagSet.json';
    import scheduleState from '~/components/dashboard/scheduleState.vue';
    import qs from 'qs';
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
            scheduleState
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
                    schedule: {
                        isActive: '',
                    }
                },
                compressorImage: '',
                compKeyTag: waTagSet.airKeyTag.tags,
                powerKeyTag: waTagSet.powerKeyTag.tags,
                compTagSet: waTagSet.airCompSet.tags,
                powerTagSet: waTagSet.accuraSet.tags,
                timeCategories: [],
                nowTime: '',
                liveChartData: [], // 데이터 변수
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
                    colors: ['#ffa100', '#003CFF', '#27AEF3', '#81E400', '#ff3a55', '#09c488', '#8ca7ff', '#3b4656'],
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
                    labels: ['bar'],
                },
                airCompressorBar: 0,
                powerData: '',
                Interval1M: '',
                interval: '',
                intervalTime: 30 * 1000,
            };
        },
        mounted() {
            this.getAirCompressor();
            this.resetInterval();
            this.getTrip();
            this.loadingData.show = true;
        },
        beforeDestroy() {
            this.removeInterval();
        },
        methods: {
            chartSetting() {
                const barMaxValue = 15;
                const bar = this.airCompressor.tags.COMP_SystemPre.value;
                this.airCompressorBar = (bar * 100) / barMaxValue;
            },
            async getAirCompressor() {
                const vm = this;
                const id = this.$route.params.id;
                axios.get(`/api/compressor/${id}`,
                 {
                    params: {
                        components: ["stateComponent", "wholeInfoComponent", "importantInfoComponent"]

                    }, paramsSerializer: params => {
                        return qs.stringify(params)
                    }
                }).then((res) => {
                    vm.airCompressor = res.data;
                    vm.compressorImage = require(`~/assets/images/equipment/${vm.airCompressor.equipment.model}.png`);
                    vm.powerData = vm.airCompressor.devices.power;
                    if(vm.powerData) {
                        vm.setLiveChart();
                    }
                    if(vm.airCompressor.tags.length !== 0) {
                        vm.chartSetting();
                    }
                }).catch((error) => {
                    vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
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
            settingModalOpen(device) {
                this.$refs.settingEquipmentModal.updateModal(device);
                this.settingModalData.show = true;
            },
            setLiveChart: function () {
                const vm = this;

                let data;
                vm.powerData.forEach((item, index) => {
                    if (vm.liveChartData[index] === undefined) {
                        vm.liveChartData[index] = {name: `${item.name} 실시간 유효전력`, data: []}
                    }
                    data = item.tags.PWR_KW.value;
                    vm.liveChartData[index].data.push(data.toFixed(2));
                });

                this.nowTime = dayjs(new Date().toISOString()).format('HH:mm:ss');
                let liveChartXcount = this.liveChartData[0].data.length;
                let maxCount = 150;

                if (liveChartXcount > maxCount) {
                    vm.timeCategories.shift();
                    vm.liveChartData[0].data.shift();
                }
                vm.timeCategories.push(vm.nowTime);

                vm.$refs.liveChart.updateOptions({
                    "xaxis": {"categories": vm.timeCategories}
                });
            },
            resetInterval() {
                const vm = this;
                clearInterval(this.interval);
                vm.interval = null;
                vm.interval = setInterval(() => {
                    vm.getAirCompressor();
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
                }else if(value <= -101 && value >= -113) {
                    return '-'
                }else {
                    return value.toLocaleString('ko-KR', {maximumFractionDigits: numFix});
                }
            },
        },
    };
</script>
