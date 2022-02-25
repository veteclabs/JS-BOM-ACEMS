<template>
    <div class="analysisDepartment">
        <div class="header-title">
            <h2>공정별 분석</h2>
        </div>
        <div class="wrapper animated fadeInRight">
            <div class="row">
                <div class="col-lg-6">
                    <div class="ibox">
                        <div class="ibox-title ibox-normal-title ibox-noborder-title">
                            Chart TOP10
                        </div>
                        <div class="ibox-content ibox-noborder-content">
                            <dx-chart
                                    id="analysisLocationChart"
                                    ref="analysisLocationChart"
                                    :data-source="processChartData"
                                    :series="chartSeries"
                                    :bar-group-padding="0.6"
                                    height="250px;"
                            >
                                <DxArgumentAxis color="#e4e9f1">
                                    <DxTick color="#e4e9f1"/>
                                    <DxLabel color="#8c96a5"/>
                                    <DxGrid color="#e4e9f1"/>
                                </DxArgumentAxis>
                                <DxValueAxis :visible="false">
                                    <DxTick :visible="false"/>
                                    <DxGrid color="#e4e9f1"/>
                                    <DxLabel color="#8c96a5"/>
                                </DxValueAxis>
                                <dx-legend
                                        vertical-alignment="bottom"
                                        horizontal-alignment="center"
                                        item-text-position="bottom"
                                />
                                <dx-tooltip
                                        :enabled="true"
                                        :format=DevNumberFormat
                                        :customize-tooltip="customizeTooltip"
                                />
                                <dx-export :enabled="true"/>
                            </dx-chart>
                        </div>
                    </div>
                    <div class="ibox">
                        <div class="ibox-title ibox-normal-title ibox-noborder-title">
                            Grid
                        </div>
                        <div class="ibox-content ibox-noborder-content" id="reportGridBox">
                            <dx-data-grid
                                    id="analysisLocationGrid"
                                    ref="analysisLocationGrid"
                                    :data-source="chartData"
                                    :columns="gridDataColumn"
                                    :allow-column-resizing="true"
                                    :allowColumnReordering="true"
                            >
                                <dx-export
                                        :enabled="true"
                                        :allow-export-selected-data="true"
                                        file-name="history"
                                />
                                <dx-paging :page-size="31"/>
                                <dx-pager
                                        :show-page-size-selector="true"
                                        :allowed-page-sizes="[31]"
                                        :show-info="true"
                                />
                                <template #blockGridTemplate="{ data: cellData }">
                                    <blockGridTemplate :cell-data="cellData"/>
                                </template>
                                <DxSummary>
                                    <DxTotalItem column="Usage" summary-type="sum" value-format="#,##0"/>
                                    <DxTotalItem column="beforeDayUsage" summary-type="sum" value-format="#,##0"/>
                                    <DxTotalItem column="beforeMonthUsage" summary-type="sum" value-format="#,##0"/>
                                    <DxTotalItem column="beforeYearUsage" summary-type="sum" value-format="#,##0"/>
                                </DxSummary>
                            </dx-data-grid>
                        </div>
                    </div>
                </div>
                <div class="col-lg-6">
                    <div class="ibox">
                        <div class="ibox-2Depth location-rate-box">
                            <div class="ibox-title ibox-normal-title ibox-noborder-title">
                                Rate Chart TOP10
                            </div>
                            <div class="ibox-content ibox-noborder-content">
                                <dx-pie-chart id="analysisLocationPieChart"
                                              ref="analysisLocationPieChart"
                                              :data-source="processChartData"
                                              type="doughnut"
                                              :inner-radius="0.75"
                                              :start-angle="90"
                                              :customize-point="customColorChange">
                                    <dx-series :argument-field="argumentField" value-field="Usage" name="사용량">
                                        <dx-label :visible="false"/>
                                    </dx-series>
                                    <dx-legend :margin="20" horizontal-alignment="center" vertical-alignment="bottom"/>
                                    <dx-tooltip :enabled="true" :format=DevNumberFormat
                                                :customize-tooltip="customizeTooltip"/>
                                </dx-pie-chart>
                                <ul class="location-piechart-ul">
                                    <li v-for="(item, index) in processChartData" :key="index">
                                        <p>
                                            <span :style='`background-color:${colorArray.rankColorArray[index]}`'/>
                                            {{item[argumentField]}}
                                        </p>
                                        <p class="g9">
                                            <strong>{{item.Usage | currency}}{{unitArray.Usage}}</strong>
                                        </p>
                                    </li>
                                </ul>
                            </div>
                        </div>
                        <div class="flex-box location-minmax-box">
                            <div>
                                <div class=" location-min-box ibox-2Depth">
                                    <p>최소 사용 공정은</p>
                                    <p><span class="green">{{minProcess}}</span>입니다.</p>
                                </div>
                            </div>
                            <div>
                                <div class=" location-max-box ibox-2Depth">
                                    <p>최대 사용 공정은</p>
                                    <p><span class="red">{{maxProcess}}</span>입니다.</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="ibox summary">
                        <div class="ibox-title ibox-normal-title ibox-noborder-title">
                            Summary
                        </div>
                        <div class="ibox-content ibox-noborder-content">
                            <div
                                    v-for="item in searchResult"
                                    :key="item.name"
                                    class="ibox-2Depth"
                            >
                                <p class="font1-2 g6"><img :src="item.icon" :alt="item.name" width="16"/>{{ item.name }}
                                </p>
                                <div class="flex-box">
                                    <div v-if="params.timeType !== 'Y'"
                                         :class="[ item.value - item.beforeValue < 0 ? 'warning' : 'normal']">
                                        <p>
                                            {{ (item.value - item.beforeValue) | currencyAbs(0) }}
                                        </p>
                                        <div class="bom-badge" v-if="item.value !== 0">
                                            {{ (((item.value - item.beforeValue) / item.value) * 100) | currency(2) }}%
                                        </div>
                                        <div class="bom-badge" v-if="item.value === 0">
                                            - %
                                        </div>
                                    </div>
                                    <h1>{{ item.value | currency(0) }}{{unitArray[Usage]}}</h1>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <searchFilter
                v-bind:propsdata="filterSettingData"
                v-on:refresh="getSearch"
                v-on:filterState="filterStateChange"
        />
        <Loading v-bind:propsdata="LoadingData"/>
        <flashModal v-bind:propsdata="msgData"/>
    </div>
</template>
<script>
    import dayjs from 'dayjs';
    import axios from 'axios';
    import 'devextreme/dist/css/dx.common.css';
    import 'devextreme/dist/css/dx.light.compact.css';
    import DxChart, {
        DxArgumentAxis,
        DxCommonSeriesSettings,
        DxLabel,
        DxLegend,
        DxSeries,
        DxTooltip,
        DxValueAxis,
        DxTick,
        DxGrid,
    } from 'devextreme-vue/chart';
    import DxPieChart from 'devextreme-vue/pie-chart';
    import {
        DxDataGrid,
        DxExport,
        DxPager,
        DxPaging,
        DxSummary,
        DxTotalItem,
    } from 'devextreme-vue/data-grid';
    import Loading from '~/components/loading.vue';
    import searchFilter from '~/components/searchFilter.vue';
    import flashModal from '~/components/flashmodal.vue';
    import blockGridTemplate from '~/components/blockGridTemplate.vue';
    import colorArray from '~/assets/data/colorArray.json';
    import columnNameList from '~/assets/data/columnNameList.json';
    import unitArray from '~/assets/data/unitArray.json';

    export default {
        fetch({store, redirect}) {
            if (!store.state.authUser) {
                return redirect('/');
            }
            return false;
        },
        layout: 'analysis',
        components: {
            dayjs,
            DxChart,
            DxArgumentAxis,
            DxCommonSeriesSettings,
            DxLabel,
            DxLegend,
            DxSeries,
            DxTooltip,
            DxValueAxis,
            DxDataGrid,
            DxExport,
            DxPager,
            DxPaging,
            DxSummary,
            DxTotalItem,
            DxTick,
            DxGrid,
            DxPieChart,
            Loading,
            searchFilter,
            flashModal,
            blockGridTemplate
        },
        data() {
            return {
                LoadingData: {
                    show: false,
                },
                filterState: true,
                filterSettingData: {
                    timeType: {
                        show: true,
                        value: [
                            {text: '일별합산', value: 'H',},
                            {text: '월별합산', value: 'D',},
                            {text: '연도별합산', value: 'M',},
                        ],
                    },
                    date: {show: true,},
                    usageType: {show: true, only: true},
                    process: {show: true, type: 'select'},
                },
                msgData: {
                    // 알람모달
                    msg: '',
                    show: false,
                },
                // 시설별 사용량/사용비율
                DevNumberFormat: '#,##0.##',
                processChartData: [],
                chartData: [],
                chartSeries: [],
                gridDataColumn: [],
                params: '',
                // 사용량분석
                argumentField: 'processName',
                keyword: '',
                beforeKeyWord: '',
                min: 0,
                minProcess: '',
                max: 0,
                maxProcess: '',
                avg: 0,
                sum: 0,
                beforeMin: 0,
                beforeMax: 0,
                beforeAvg: 0,
                beforeSum: 0,
                zeroCount: 0,
                searchResult: {
                    min: {
                        name: '최소사용량',
                        value: 0,
                        beforeValue: 0,
                        icon: require('~/assets/images/analysis/icn_monitoring_minimum@2x.png'),
                        show: true,
                    },
                    max: {
                        name: '최대사용량',
                        value: 0,
                        beforeValue: 0,
                        icon: require('~/assets/images/analysis/icn_monitoring_maximum@2x.png'),
                        show: true,
                    },
                    avg: {
                        name: '평균사용량',
                        value: 0,
                        beforeValue: 0,
                        icon: require('~/assets/images/analysis/icn_monitoring_average@2x.png'),
                        show: true,
                    },
                    sum: {
                        name: '사용량합계',
                        value: 0,
                        beforeValue: 0,
                        icon: require('~/assets/images/analysis/icn_monitoring_total@2x.png'),
                        show: true,
                    },
                },
            };
        },
        created() {
            this.analysisSetting();
        },
        methods: {
            analysisSetting() {
                this.colorArray = colorArray.colorArray;
                this.columnNameList = columnNameList.columnNameList;
                this.unitArray = unitArray.unitArray;
            },
            async getSearch(params) {
                const vm = this;
                this.LoadingData.show = true;
                vm.params = params;
                axios({
                    method: 'get',
                    url: '/api/analysis/process',
                    params: params,
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.chartData = [];
                        vm.gridDataColumn = [];
                        vm.processChartData = [];
                        vm.chartSeries = [];
                        vm.maxProcess = '-';
                        vm.minProcess = '-';
                        vm.searchResult.min.value = 0;
                        vm.searchResult.max.value = 0;
                        vm.searchResult.avg.value = 0;
                        vm.searchResult.sum.value = 0;
                        vm.searchResult.min.beforeValue = 0;
                        vm.searchResult.max.beforeValue = 0;
                        vm.searchResult.avg.beforeValue = 0;
                        vm.searchResult.sum.beforeValue = 0;
                        vm.DevNumberFormat = `#,##0.##${vm.unitArray.Usage}`;

                        vm.chartData = res.data.value;
                        vm.params = params;
                        // 시설별 사용량 차트
                        // 중복 1차 메인동 나누기
                        vm.keywordInit();
                    }
                })
                    .catch((error) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = error + 1;
                    })
                    .finally(() => {
                        vm.LoadingData.show = false;
                    });
            },
            keywordInit() {
                const vm = this;

                vm.keyword = 'Usage';

                if (vm.params.timeType === 'R') {
                    vm.beforeKeyWord = 'beforeYear' + vm.keyword;
                } else if (vm.params.timeType === 'H') {
                    vm.beforeKeyWord = 'beforeDay' + vm.keyword;
                } else if (vm.params.timeType === 'D') {
                    vm.beforeKeyWord = 'beforeMonth' + vm.keyword;
                } else if (vm.params.timeType === 'M') {
                    vm.beforeKeyWord = 'beforeYear' + vm.keyword;
                } else if (vm.params.timeType === 'Y') {
                    vm.beforeKeyWord = '';
                }

                if (vm.chartData) {
                    vm.resultLocationArray(vm.chartData);
                    vm.keywordCalc(vm.chartData);
                }
            },
            resultLocationArray(result) {
                const vm = this;
                vm.processChartData = [];

                if (vm.params.process === 'AU') {
                    vm.argumentField = 'processName';
                } else {
                    vm.argumentField = 'locationName';
                }

                let topArray = result;
                topArray.sort(function (a, b) {
                    return b.Usage - a.Usage;
                });
                vm.processChartData = topArray.slice(0, 10);


                const beforeNameTarget = vm.columnNameList.filter(
                    (target) => target.value === vm.beforeKeyWord
                );

                vm.chartSeries = [
                    {
                        argumentField: vm.argumentField,
                        valueField: 'Usage',
                        name: '사용량',
                        type: 'bar',
                        cornerRadius: 5,
                        barPadding: 0.3,
                        color: vm.colorArray.usageColorArray.Usage,
                    }
                ];
                if (vm.beforeKeyWord !== '') {
                    vm.chartSeries.push({
                        argumentField: vm.argumentField,
                        valueField: beforeNameTarget[0].value,
                        name: beforeNameTarget[0].name,
                        type: 'bar',
                        cornerRadius: 5,
                        barPadding: 0.3,
                        color: vm.colorArray.beforeUsageColorArray[beforeNameTarget[0].value]
                    });
                }

                // 전체사용량 Grid
                vm.gridDataColumn = [
                    {
                        dataField: 'processName',
                        caption: '공정명',
                        alignment: 'center',
                        visibleIndex: 0,
                    },
                    {
                        dataField: 'Usage',
                        caption: '사용량',
                        alignment: 'right',
                        format: vm.DevNumberFormat,
                        cellTemplate: 'blockGridTemplate',
                    },
                ];


                // 전체사용량 Grid
                if (vm.params.department !== 'AU') {
                    vm.gridDataColumn.push({
                        dataField: 'locationName',
                        caption: '시설명',
                        alignment: 'center',
                        visibleIndex: 1,

                    });
                }

                if (vm.beforeKeyWord !== '') {
                    vm.gridDataColumn.push({
                        dataField: beforeNameTarget[0].value,
                        caption: beforeNameTarget[0].name,
                        alignment: 'right',
                        format: vm.DevNumberFormat,
                    },);
                }

            },
            keywordCalc(result) {
                const vm = this;
                // 초기화 코드
                vm.max = 0;
                vm.sum = 0;
                vm.avg = 0;
                vm.min = 99999999;
                vm.beforeSum = 0;
                vm.beforeMin = 0;
                vm.beforeMax = 0;
                vm.beforeAvg = 0;

                if (vm.beforeKeyWord !== '') {
                    vm.beforeMin = 99999999;
                } else {
                    vm.beforeSum = 0;
                }

                // 최대최소평균합 구하기
                const chartDataArray = result;
                const chartDataKey = Object.keys(result);
                const chartDataValue = Object.values(result);

                // 합계
                vm.zeroCount = 0;
                for (let i = 0; i < chartDataKey.length; i += 1) {
                    vm.sum += chartDataValue[i][vm.keyword];
                    if (chartDataValue[i][vm.keyword] !== 0) {
                    }

                    if (vm.beforeKeyWord !== '') {
                        vm.beforeSum += chartDataValue[i][vm.beforeKeyWord];
                        if (chartDataValue[i][vm.beforeKeyWord] !== 0) {
                        }
                    }
                    vm.zeroCount += 1;
                }

                // 평균값 구하기
                vm.avg = vm.sum / result.length;
                if (vm.beforeKeyWord !== '') {
                    vm.beforeAvg = vm.beforeSum / result.length;
                }

                // max 값 구하기
                vm.max = Math.max.apply(
                    null,
                    Object.keys(result)
                        .map((key) => result[key][vm.keyword])
                );

                if (vm.beforeKeyWord !== '') {
                    vm.beforeMax = Math.max.apply(
                        null,
                        Object.keys(result)
                            .map((key) => result[key][vm.beforeKeyWord])
                    );
                }

                // min 값구하기
                for (let i = 0; i < result.length; i += 1) {
                    const minTarget = result[i][vm.keyword];
                    const beforeMinTarget = result[i][vm.beforeKeyWord];
                    if (minTarget !== 0) {
                        if (minTarget <= vm.min) {
                            vm.min = minTarget;
                        }
                    }
                    if (beforeMinTarget !== 0) {
                        if (beforeMinTarget <= vm.beforeMin) {
                            vm.beforeMin = beforeMinTarget;
                        }
                    }

                    if (i === result.length - 1) {
                        if (vm.min === 99999999) {
                            vm.min = 0;
                        }
                    }
                    if (vm.beforeKeyWord !== '') {
                        if (i === result.length - 1) {
                            if (vm.beforeMin === 99999999) {
                                vm.beforeMin = 0;
                            }
                        }
                    }
                }

                // 시설명구하기
                const list = result;
                const locationMax = list.filter((item) => item[vm.keyword] === vm.max);
                const locationMin = list.filter((item) => item[vm.keyword] === vm.min);
                if (locationMax.length !== 0) {
                    vm.maxProcess = `${locationMax[0][vm.argumentField]}`;
                } else {
                    vm.maxProcess = '-';
                }

                if (locationMin.length !== 0) {
                    vm.minProcess = `${locationMin[0][vm.argumentField]}`;
                } else {
                    vm.minProcess = '-';
                }

                vm.ChartSummary();
            },
            ChartSummary() {
                const vm = this;
                vm.searchResult.min.value = parseFloat(Number(vm.min)
                    .toFixed(2));
                vm.searchResult.max.value = parseFloat(Number(vm.max)
                    .toFixed(2));
                vm.searchResult.avg.value = parseFloat(Number(vm.avg)
                    .toFixed(2));
                vm.searchResult.sum.value = parseFloat(Number(vm.sum)
                    .toFixed(2));
                vm.searchResult.min.beforeValue = parseFloat(Number(vm.beforeMin)
                    .toFixed(2));
                vm.searchResult.max.beforeValue = parseFloat(Number(vm.beforeMax)
                    .toFixed(2));
                vm.searchResult.avg.beforeValue = parseFloat(Number(vm.beforeAvg)
                    .toFixed(2));
                vm.searchResult.sum.beforeValue = parseFloat(Number(vm.beforeSum)
                    .toFixed(2));
            },
            customColorChange(point) {
                return {
                    color: this.colorArray.rankColorArray[point.index],
                };
            },
            customizePoint(arg) {
                if (this.processChartData.length > 3) {
                    const {min} = this;
                    const {max} = this;
                    const {value} = arg;
                    if (arg.series.type === 'bar') {
                        if (value >= max && max !== 0) {
                            return {
                                color: '#CE4A61',
                                hoverStyle: {color: '#CE4A61'},
                            };
                        }
                        if (value <= min && min !== 0) {
                            return {
                                color: '#77B5D8',
                                hoverStyle: {color: '#77B5D8'},
                            };
                        }
                    }
                }
                return true;
            },
            customizeLabel(arg) {
                if (this.processChartData.length > 3) {
                    const {min} = this;
                    const {max} = this;
                    const {value} = arg;
                    if (arg.series.type === 'bar') {
                        if (value >= max && max !== 0) {
                            return {
                                visible: true,
                                format: '#,##0.##',
                                backgroundColor: '#CE4A61',
                                customizeText() {
                                    return this.valueText;
                                },
                            };
                        }
                        if (value === min && min !== 0) {
                            return {
                                visible: true,
                                format: '#,##0.##',
                                backgroundColor: '#77B5D8',
                                customizeText() {
                                    return this.valueText;
                                },
                            };
                        }
                    }
                }
                return true;
            },
            customizeTooltip: (arg) => ({
                text: `구분 : ${arg.argument}<br>${arg.seriesName} : ${arg.valueText}`,
            }),
            chartRerender() {
                this.$refs.analysisLocationChart.instance.refresh();
                this.$refs.analysisLocationPieChart.instance.refresh();
            },
            filterStateChange(state) {
                this.filterState = state;
                setTimeout(this.chartRerender, 400);
            },
        },
        filters: {
            currency(value, limit) {
                value = parseFloat(value);
                if (!value) return '0';
                return value
                    .toFixed(limit)
                    .replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ',');
            },
            currencyAbs: (value, numFix) => {
                let returnValue = parseFloat(value);
                if (!value) {
                    return '0';
                } else {
                    returnValue = Math.abs(value);
                }
                return returnValue
                    .toFixed(numFix)
                    .replace(/\B(?<!\.\d*)(?=(\d{3})+(?!\d))/g, ',');
            },
        },
    };
</script>
