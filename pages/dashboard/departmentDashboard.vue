<template>
    <div id="SubContentWrap">
        <div class="header-title" v-if="processUsedList.length !== 0">
            <h2>{{processUsedList[0].department_name}}</h2>
            <p>
                <span v-for="sub in substationList" :key="sub.substation_id">{{sub.substation_name}} </span>
            </p>
        </div>
        <div class="row">
            <div class="col-lg-12">
                <div class="ibox">
                    <div class="process-ibox" ref="processIbox">
                        <ul>
                            <li v-if="processUsedList.length === 0">
                                공정별 사용량이 없습니다.
                            </li>
                            <li v-for="item in processUsedList" :key="item.id">
                                <div class="ibox-2Depth">
                                    <div class="title flex-box" :data-tooltip-text="item.manufacturing_process_name">
                                        <h3>
                                            {{item.manufacturing_process_name}}
                                        </h3>
                                        <div class="bom-badge">TOP {{item.rank }}</div>
                                    </div>
                                    <div class="ibox-content" v-if="departmentTargetUsage.length !== 0">
                                        <div class="percent flex-box">
                                            <client-only>
                                                <apexchart type="radialBar" :options="processChartOptions"
                                                           width="50px"
                                                           :series="[(item.usage/departmentTargetUsage[0].Usage)*100]"/>
                                            </client-only>
                                            <h1>
                                                {{item.usage | percentageChk(departmentTargetUsage[0].Usage)}}%
                                            </h1>
                                        </div>
                                        <div class="value right">
                                            <h3>{{item.usage | numberFormat(0)}}kWh</h3>
                                        </div>
                                    </div>
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="col-lg-4">
                <div class="ibox">
                    <div class="ibox-title ibox-noborder-title">
                        사용량 순위
                    </div>
                    <div class="ibox-content">
                        <DxDataGrid id="gridBox" :data-source="processUsedList" :show-borders="false"
                                    :column-min-width="100">
                            <DxColumn data-field="rank" caption="순위" alignment="center"
                                      cell-template="deptRankingTemplate" :width="55"/>
                            <DxColumn data-field="manufacturing_process_name" caption="공정명"/>
                            <DxColumn data-field="usage" caption="사용량" alignment="right"
                                      cell-template="blockGridUnitTemplate"/>
                            <template #deptRankingTemplate="{ data: cellData}">
                                <deptRankingTemplate :cell-data="cellData"/>
                            </template>
                            <template #blockGridUnitTemplate="{ data: cellData }">
                                <blockGridUnitTemplate :cell-data="cellData"/>
                            </template>
                        </DxDataGrid>

                    </div>
                </div>
            </div>
            <div class="col-lg-3">
                <div class="ibox">
                    <div class="ibox-title">
                        금일 전체 전력 사용량 비율
                    </div>
                    <div class="ibox-content">
                        <client-only>
                            <apexchart type="radialBar" height="280" :options="chartOptions" :series="targetUsage"/>
                        </client-only>
                        <div class="ibox-2Depth" style="margin:0;">
                            <ul class="today-kWh-list" v-if="departmentTargetUsage.length !== 0">
                                <li>
                                    <div><span class="blue-0-bg"/>Used</div>
                                    <h2>{{departmentTargetUsage[0].Usage|numberFormat(0)}}kWh</h2>
                                </li>
                                <li>
                                    <div><span class="gray-1-bg"/>Total</div>
                                    <h2>{{departmentTargetUsage[0].day_kWh_target|numberFormat(0)}}kWh</h2>
                                </li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-5">
                <div class="ibox">
                    <div class="ibox-title">
                        변전실 실시간 유효전력 Chart
                    </div>
                    <div class="ibox-content">
                        <client-only>
                            <apexchart type="area" height="200" ref="liveCTLineChart" :options="liveCTChartOption"
                                       :series="liveCTChartData"/>
                        </client-only>
                    </div>
                </div>
                <div class="ibox">
                    <div class="ibox-title">
                        시간별 전력 사용량
                    </div>
                    <div class="ibox-content">
                        <client-only>
                            <apexchart type="heatmap" height="200" ref="timeHeatMap" :options="timeHeatMapOption"
                                       :series="timeHeatMapData"/>
                        </client-only>
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
    import {
        DxDataGrid,
        DxColumn,
    } from 'devextreme-vue/data-grid';
    import deptRankingTemplate from '~/components/gridTemplate/deptRankingTemplate.vue';
    import blockGridUnitTemplate from '~/components/gridTemplate/blockGridUnitTemplate.vue';


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
            DxDataGrid,
            DxColumn,
            deptRankingTemplate,
            blockGridUnitTemplate
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
                depthId: 1,
                nowDate: '',
                nowTime: '',
                processChartOptions: {
                    chart: {
                        type: 'radialBar',
                        sparkline: {
                            enabled: true
                        }
                    },
                    plotOptions: {
                        radialBar: {
                            hollow: {
                                size: '16px',
                            },
                            dataLabels: {
                                show: false
                            }
                        },
                    },
                    stroke: {
                        lineCap: "round",
                    },
                    fill: {
                        colors: '#3363FF'
                    }
                },
                substationList: [],
                processUsedList: [],
                departmentTargetUsage: [],
                tagList: [],
                targetUsage: [0],
                chartOptions: {
                    chart: {
                        type: 'radialBar',
                        offsetY: -20
                    },
                    plotOptions: {
                        radialBar: {
                            startAngle: -135,
                            endAngle: 135,
                            dataLabels: {
                                name: {
                                    fontSize: '14px',
                                    fontFamily: 'BOM-font',
                                    fontWeight: 500,
                                    color: '#667082',
                                    offsetY: 90
                                },
                                value: {
                                    offsetY: 56,
                                    fontFamily: 'BOM-font',
                                    fontSize: '22px',
                                    color: '#1b1b1b',
                                    fontWeight: 800,
                                    formatter: function (val) {
                                        return val + "%";
                                    }
                                }
                            }
                        }
                    },
                    stroke: {
                        dashArray: 4
                    },
                    labels: ['Used'],
                },
                // 실시간 유효전력량 그래프(라인그래프)
                timeCategories: [],
                liveCTChartData: [], // 데이터 변수
                liveCTChartOption: { //차트옵션 변수
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
                    colors: ['#FFA100', '#003CFF', '#27AEF3', '#81E400'],
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

                // 시간별 사용량 (HeatMap)
                timeHeatMapData: [
                    {name: '일', data: []},
                    {name: '토', data: []},
                    {name: '금', data: []},
                    {name: '목', data: []},
                    {name: '수', data: []},
                    {name: '화', data: []},
                    {name: '월', data: []},
                ],
                timeHeatMapOption: {
                    chart: {
                        width: 374,
                        height: 144,
                        type: 'heatmap',
                        offsetX: 0,
                        offsetY: 0,
                        toolbar: {show: false},
                    },
                    heatmap: {
                        inverse: true,
                    },
                    dataLabels: {enabled: false},
                    colors: ['#ff3a55'],
                    stroke: {
                        width: 1,
                    },
                    xaxis: {
                        type: 'category',
                    },
                    responsive: [{
                        breakpoint: 768,
                        options: {
                            xaxis: {
                                labels: {
                                    style: {
                                        fontSize: '8px',
                                    },
                                },
                            },
                            yaxis: {
                                labels: {
                                    style: {
                                        fontSize: '8px',
                                    },
                                },
                            },

                        },
                    }],
                },
                liveInterval: '',
                interval: '',
                intervalTime: 5 * 1000,
                updateTime: '',
            };
        },
        mounted() {
            this.depthId = this.$route.query.depth;
            this.WaLogin();
            this.getDepartmentData();
            this.getDepartmentTarget();
            this.getDepartmentCalendar();
            this.getTagValues();
            this.resetInterval();
            this.reset1MInterval();
            this.loadingData.show = true;
            this.$refs.processIbox.addEventListener('wheel', this.scrollEvent)
        },
        beforeDestroy() {
            this.removeInterval();
            this.remove1MInterval();
        },
        methods: {
            scrollEvent: function (event) {
                event.preventDefault();
                this.$refs.processIbox.scrollLeft += event.deltaY;
            },
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
            async getDepartmentData() {
                const vm = this;
                axios({
                    method: "get",
                    url: `/api/dashboard/departmant/${vm.depthId}`
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.processUsedList = res.data.data;

                        //변전실 중복제거
                        vm.substationList = vm.processUsedList.reduce(function (acc, current) {
                            if (acc.findIndex(({substation_id}) => substation_id === current.substation_id) === -1) {
                                acc.push(current);
                            }
                            return acc;
                        }, []);


                        vm.tagList = [];
                        vm.liveCTChartData = [];
                        vm.substationList.forEach((item) => {
                            vm.tagList.push({
                                substationName: item.substation_name,
                                tagName: `${item.substation_id}_PWR_kW`
                            });
                            vm.liveCTChartData.push({
                                "name": `${item.substation_name}`,
                                "data": []
                            });
                        });

                    }
                }).catch((error) => {
                    vm.msgData.msg = error;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },
            async getDepartmentTarget() {
                const vm = this;
                axios({
                    method: "get",
                    url: `/api/dashboard/departmant/target/${vm.depthId}`
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.departmentTargetUsage = res.data.data;
                        let percent = vm.departmentTargetUsage[0].Usage / vm.departmentTargetUsage[0].day_kWh_target * 100
                        vm.targetUsage = [percent.toFixed(2)]
                    }
                }).catch((error) => {
                    vm.msgData.msg = error;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },
            async getDepartmentCalendar() {
                const vm = this;
                axios({
                    method: "get",
                    url: `/api/dashboard/departmant/calendar/${vm.depthId}`
                }).then((res) => {
                    if (res.data.code === 1) {
                        let result = res.data.data;
                        let hourArray = [];
                        vm.timeHeatMapData.forEach(item => {
                            item.data = []
                        });
                        result.forEach(item => {
                            if (item.weekDay === 0) {
                                hourArray.push(item.H);
                            }
                            vm.timeHeatMapData[item.weekDay].data.push(item.usage)
                        });
                        vm.$refs.timeHeatMap.updateOptions({
                            xaxis: {categories: hourArray}, // 월을 넣어주세요.
                        }, false, true);
                    }
                }).catch((error) => {
                    vm.msgData.msg = error;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },
            async getTagValues() {
                const vm = this;
                axios.post('/api/dashboard/tag/getTagValue', {
                    tagList: vm.tagList
                }, {
                    timeout: vm.intervalTime,
                }).then((res) => {
                    if (res.data.Result.Total > 0) {
                        vm.tagVal = res.data.Values;
                        vm.setCTLiveChart();
                    }
                }).catch((error) => {
                    vm.msgData.msg = error;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },
            setCTLiveChart: function () {
                const vm = this;
                vm.getNowTime();
                //실시간유효전력량 Graph 실시간 변경
                let liveChartXcount = this.liveCTChartData[0].data.length;
                let maxCount = 150;

                //X좌표 설정
                if (liveChartXcount > maxCount) {
                    vm.timeCategories.shift();
                    vm.liveCTChartData[0].data.shift();
                }
                vm.timeCategories.push(vm.nowTime);

                //Y좌표 설정
                vm.tagList.forEach((item, index) => {
                    let data = vm.$options.filters.pickTagValue(vm.tagVal, item.tagName);
                    vm.liveCTChartData[index].data.push(data.toFixed(2));
                });

                vm.$refs.liveCTLineChart.updateOptions({
                    "xaxis": {"categories": vm.timeCategories}
                });
            },
            // Interval 시작
            resetInterval() {
                const vm = this;
                clearInterval(this.interval);
                vm.interval = null;
                vm.interval = setInterval(() => {
                    vm.getTagValues();
                }, vm.intervalTime);
            },
            // 인터벌삭제
            removeInterval() {
                const vm = this;
                clearInterval(vm.interval);
            },
            reset1MInterval: function () {//5분 인터벌 시작(월별전력량/시간별전력량)
                let vm = this;
                clearInterval(this.Interval1M);
                this.Interval1M = setInterval(function () {
                    vm.getDepartmentCalendar();
                    vm.getDepartmentTarget();
                }, 60 * 1000)
            },
            remove1MInterval: function () {//5분 인터벌삭제
                let vm = this;
                clearInterval(vm.Interval1M);
            }
        },
        filters: {
            percentageChk(current, total) {
                let value;
                if (current != null && total != null) {
                    if (total === 0) {
                        return 0
                    } else {
                        value = (current / total) * 100
                    }
                } else {
                    value = 0
                }
                return value.toFixed(2)
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
            numberFormat: (value, numFix) => {
                value = parseFloat(value);
                if (!value) return '0';
                return value.toLocaleString('ko-KR', {maximumFractionDigits: numFix});
            },
        },
        watch: {
            $route: function () {
                this.depthId = this.$route.query.depth;
                this.getDepartmentData();
                this.getDepartmentTarget();
                this.getDepartmentCalendar();
                this.loadingData.show = true;
            }
        }

    };
</script>
