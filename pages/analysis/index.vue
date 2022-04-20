<template>
    <div class="analysisEnergy">
        <div class="wrapper animated fadeInRight">
            <div class="row">
                <div class="col-lg-7">
                    <div class="ibox">
                        <div class="ibox-title ibox-normal-title ibox-noborder-title">
                            Chart
                        </div>
                        <div class="ibox-content">
                            <DxChart
                                    id="analysisEnergyChart"
                                    ref="analysisEnergyChart"
                                    :data-source="chartData"
                                    :series="chartSeries"
                                    :bar-group-padding="0.5"
                                    :customize-label="customizeLabel"
                                    :customizePoint="customizePoint"
                            >
                                <DxCommonSeriesSettings>
                                    <Dx-point :visible="false"/>
                                </DxCommonSeriesSettings>
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
                                <DxValueAxis name='temp' :visible="false" position="right">
                                    <DxTick :visible="false"/>
                                    <DxGrid color="#e4e9f1"/>
                                    <DxLabel color="#8c96a5"/>
                                </DxValueAxis>
                                <DxValueAxis name='hum' :visible="false" position="right">
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
                                        :customize-tooltip="customizeTooltip"
                                />
                                <dx-crosshair :enabled="true">
                                    <dx-horizontal-line :visible="false"/>
                                    <dx-label :visible="true"/>
                                </dx-crosshair>
                                <dx-export :enabled="true"/>
                            </DxChart>
                        </div>
                    </div>
                    <div class="ibox">
                        <div class="ibox-title ibox-normal-title ibox-noborder-title">
                            Grid
                        </div>
                        <div class="ibox-content">
                            <dx-data-grid
                                    id="analysisEnergyGrid"
                                    :data-source="chartData"
                                    :columns="gridDataColumn"
                                    :show-borders="false"
                                    :allow-column-resizing="true"
                                    :allowColumnReordering="true"
                            >
                                <DxScrolling
                                        mode="virtual"
                                        row-rendering-mode="virtual"/>
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
                                    <blockGridTemplate :cell-data="cellData" :gridEnergy="params.energy"/>
                                </template>
                                <DxSummary>
                                    <DxTotalItem column="Usage" summary-type="sum" value-format="#,##0"/>
                                    <DxTotalItem column="beforeDayUsage" summary-type="sum" value-format="#,##0"/>
                                    <DxTotalItem column="beforeMonthUsage" summary-type="sum" value-format="#,##0"/>
                                    <DxTotalItem column="beforeYearUsage" summary-type="sum" value-format="#,##0"/>
                                    <DxTotalItem column="temperature" summary-type="avg" value-format="#,##0.#"/>
                                    <DxTotalItem column="humidity" summary-type="avg" value-format="#,##0.#"/>
                                    <DxTotalItem column="TOE" summary-type="sum" value-format="#,##0.#####"/>
                                    <DxTotalItem column="beforeDayTOE" summary-type="sum" value-format="#,##0.#####"/>
                                    <DxTotalItem column="beforeMonthTOE" summary-type="sum" value-format="#,##0.#####"/>
                                    <DxTotalItem column="beforeYearTOE" summary-type="sum" value-format="#,##0.#####"/>
                                    <DxTotalItem column="tCo2" summary-type="sum" value-format="#,##0.#####"/>
                                    <DxTotalItem column="beforeDaytCo2" summary-type="sum" value-format="#,##0.#####"/>
                                    <DxTotalItem column="beforeMonthtCo2" summary-type="sum"
                                                 value-format="#,##0.#####"/>
                                    <DxTotalItem column="beforeYeartCo2" summary-type="sum" value-format="#,##0.#####"/>
                                </DxSummary>
                            </dx-data-grid>
                        </div>
                    </div>
                </div>
                <div class="col-lg-5">
                    <div class="ibox summary">
                        <div class="ibox-title ibox-normal-title ibox-noborder-title">
                            Summary
                        </div>
                        <div class="ibox-content">
                            <div v-for="item in searchResult" :key="item.name">
                                <div v-if="item.show" class="ibox-2Depth">
                                    <p class="font1-2 g6">
                                        <img :src="item.icon" :alt="item.name" width="16"/>
                                        {{ item.name }}
                                    </p>
                                    <div class="flex-box">
                                        <div :class="[ item.value - item.beforeValue > 0 ? 'warning' : 'normal']"
                                             v-show="params.timeType !== 'Y' && params.timeType !== 'R'">
                                            <p v-show="params.usageType ==='Usage'">
                                                {{ (item.value - item.beforeValue) | currencyAbs(0) }}
                                            </p>
                                            <p v-show="params.usageType !=='Usage'">
                                                {{ (item.value - item.beforeValue) | numberFormat(5) }}
                                            </p>
                                            <div class="bom-badge">
                                                {{ (((item.value - item.beforeValue) / item.value) * 100) | currency(2)
                                                }}%
                                            </div>
                                        </div>
                                        <h3 v-show="params.usageType ==='Usage'">
                                            {{ item.value | currency(0) }}{{unitArray[params.energy]}}
                                        </h3>
                                        <h3 v-show="params.usageType !=='Usage'">{{ item.value | numberFormat(5) }}</h3>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <searchFilter v-bind:propsdata="filterSettingData" v-on:refresh="getSearch"
                      v-on:filterState="filterStateChange"/>
        <Loading v-bind:propsdata="LoadingData"/>
        <flashModal v-bind:propsdata="msgData"/>
    </div>
</template>
<script>
    import axios from 'axios';
    import 'devextreme/dist/css/dx.common.css';
    import 'devextreme/dist/css/dx.light.compact.css';
    import DxChart, {
        DxCommonSeriesSettings,
        DxConstantLine,
        DxCrosshair,
        DxHorizontalLine,
        DxLabel,
        DxLegend,
        DxSeries,
        DxTooltip,
        DxArgumentAxis,
        DxValueAxis,
        DxTick,
        DxGrid,
        DxMargin,
        DxPoint,
    } from 'devextreme-vue/chart';
    import {
        DxDataGrid,
        DxExport,
        DxPager,
        DxPaging,
        DxSummary,
        DxTotalItem,
        DxScrolling,
    } from 'devextreme-vue/data-grid';
    import Loading from '~/components/loading.vue';
    import flashModal from '~/components/flashmodal.vue';
    import searchFilter from '~/components/searchFilter.vue';
    import blockGridTemplate from '~/components/gridTemplate/blockGridTemplate.vue';
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
        name: 'history',
        components: {
            flashModal,
            DxChart,
            DxCommonSeriesSettings,
            DxLabel,
            DxLegend,
            DxSeries,
            DxTooltip,
            DxConstantLine,
            DxCrosshair,
            DxHorizontalLine,
            DxDataGrid,
            DxExport,
            DxPager,
            DxPaging,
            DxArgumentAxis,
            DxValueAxis,
            DxTick,
            DxGrid,
            DxMargin,
            DxPoint,
            DxSummary,
            DxTotalItem,
            DxScrolling,
            Loading,
            searchFilter,
            blockGridTemplate,
        },
        data() {
            return {
                filterState: true,
                filterSettingData: {
                    timeType: {
                        show: true,
                        value: [
                            {text: 'Hour', value: 'H'},
                            {text: '15min', value: '15min'},
                            {text: 'Day', value: 'D'},
                            {text: 'Month', value: 'M'},
                            {text: 'Year', value: 'Y'}
                        ],
                    },
                    date: {show: true,},
                    tagType: {show: true,},
                    usageType: {show: true,},
                    deviceId: {show: true, type: 'select'},
                },
                id: '',
                LoadingData: {
                    show: false,
                },
                msgData: {
                    // 알람모달
                    msg: '',
                    show: false,
                },
                gridColumn: 'Hour',
                min: 0,
                max: 0,
                avg: 0,
                sum: 0,
                tempAvg: 0,
                humAvg: 0,
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
                params: '',
                DevNumberFormat: '#,##0.##',
                chartData: [],
                chartSeries: [],
                gridDataColumn: [],
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
                    url: '/api/analysis/data',
                    params: params,
                }).then((res) => {
                    vm.chartData = [];
                    vm.chartSeries = [];
                    vm.gridDataColumn = [];

                    const chartDataArray = res.data;

                    if (chartDataArray.length !== 0) {
                        vm.drawDefaultChart(chartDataArray);
                        vm.keywordCalc(chartDataArray);
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                }).finally(() => {
                    vm.LoadingData.show = false;
                });
            },
            drawDefaultChart(chartDataTemp) {
                const vm = this;
                vm.chartData = chartDataTemp;
                const temp = chartDataTemp[0];

                const argumentFieldValue = Object.keys(temp)[0];
                const nameArray = vm.columnNameList;
                let valueName;

                let beforeTarget,
                    usageType;

                if (vm.params.usageType === 'Usage') {
                    usageType = 'Usage';
                } else {
                    usageType = vm.params.usageType;
                }
                if (vm.params.timeType === 'H' || vm.params.timeType === '15min') {
                    beforeTarget = `beforeDay${usageType}`;
                } else if (vm.params.timeType === 'D') {
                    beforeTarget = `beforeMonth${usageType}`;
                } else if (vm.params.timeType === 'M') {
                    beforeTarget = `beforeYearMonth${usageType}`;
                }

                for (let i = 0; i < Object.keys(temp).length; i += 1) {
                    const valueFieldValue = Object.keys(temp)[i];
                    const valueNameTarget = nameArray.filter(
                        (target) => target.value === valueFieldValue
                    );

                    if (valueNameTarget.length === 0) {
                        valueName = valueFieldValue;
                    } else {
                        valueName = valueNameTarget[0].name;
                    }
                    console.log(vm.params.tagTypeOption)
                    console.log(vm.params.usageType)
                    if (vm.params.usageType === 'Usage') {
                        if (valueFieldValue === 'kW' || valueFieldValue === 'beforekW') {
                            vm.DevNumberFormat = `#,##0.## kW`
                        } else if  (vm.params.tagTypeOption === 'FLOW') {
                            vm.DevNumberFormat = `#,##0.## ㎥`
                        } else {
                            vm.DevNumberFormat = `#,##0.## ${this.unitArray[vm.params.usageType]}`;
                        }
                    } else if (vm.params.usageType === 'PF') {
                        vm.DevNumberFormat = `#,##0.## %`
                    } else {
                        vm.DevNumberFormat = `#,##0.#####`;
                    }

                    if (vm.params.timeType === 'Y') {
                        if (i !== 0) {
                            vm.chartSeries.push({
                                argumentField: argumentFieldValue,
                                valueField: valueFieldValue,
                                name: valueName,
                                type: 'bar',
                                cornerRadius: 5,
                                barPadding: 0.1,
                                color: vm.colorArray.usageColorArray[valueFieldValue],
                            });
                        }
                    } else {
                        if (valueFieldValue === usageType) {
                            vm.chartSeries.push({
                                argumentField: argumentFieldValue,
                                valueField: valueFieldValue,
                                name: valueName,
                                type: 'bar',
                                cornerRadius: 5,
                                barPadding: 0.1,
                                color: vm.colorArray.usageColorArray[valueFieldValue],
                            });
                        } else if (valueFieldValue === beforeTarget) {
                            vm.chartSeries.push({
                                argumentField: argumentFieldValue,
                                valueField: valueFieldValue,
                                name: valueName,
                                type: 'bar',
                                cornerRadius: 5,
                                barPadding: 0.1,
                                color: vm.colorArray.beforeUsageColorArray[valueFieldValue],
                            });
                        } else if (valueFieldValue === 'kW') {
                            vm.chartSeries.push({
                                argumentField: argumentFieldValue,
                                valueField: valueFieldValue,
                                name: valueName,
                                type: 'line',
                                cornerRadius: 5,
                                barPadding: 0.1,
                                color: vm.colorArray.beforeUsageColorArray[valueFieldValue],
                            });
                        }
                    }

                    if (i === 0) {
                        vm.gridDataColumn.push({
                            dataField: valueFieldValue,
                            caption: valueName,
                            dataType: 'string',
                            alignment: 'center',
                        });
                    } else if (i > 2) {
                        vm.gridDataColumn.push({
                            dataField: valueFieldValue,
                            caption: valueName,
                            format: vm.DevNumberFormat,
                            alignment: 'center',
                        });
                    } else {
                        vm.gridDataColumn.push({
                            dataField: valueFieldValue,
                            caption: valueName,
                            format: vm.DevNumberFormat,
                            alignment: 'center',
                            cellTemplate: 'blockGridTemplate',
                        });
                    }
                }
            },
            keywordCalc(result) {
                const vm = this;
                vm.avg = 0;
                vm.max = 0;
                vm.sum = 0;
                vm.min = 0;
                vm.beforeAvg = 0;
                vm.beforeMax = 0;
                vm.beforeSum = 0;
                vm.beforeMin = 0;
                let tempSum = 0;
                let humSum = 0;
                let keyword;
                if (this.params.usageType === 'Usage') {
                    keyword = 'Usage';
                } else {
                    keyword = this.params.usageType;
                }

                let beforeKeyWord;
                if (vm.params.timeType === 'H' || vm.params.timeType === '15min') {
                    beforeKeyWord = 'beforeDay' + keyword;
                } else if (vm.params.timeType === 'D') {
                    beforeKeyWord = 'beforeMonth' + keyword;
                } else if (vm.params.timeType === 'M') {
                    beforeKeyWord = 'beforeYearMonth' + keyword;
                }

                // 최대최소평균합 구하기
                const chartDataArray = result;
                const chartDataKey = Object.keys(chartDataArray);
                const chartDataValue = Object.values(chartDataArray);

                // 합계
                vm.zeroCount = 0;
                for (let i = 0; i < chartDataKey.length; i += 1) {
                    vm.sum += chartDataValue[i][keyword];
                    if (chartDataValue[i][keyword] !== 0) {
                        vm.zeroCount += 1;
                    }
                }

                // 평균값 구하기
                vm.avg = vm.sum / chartDataArray.length;
                vm.tempAvg = tempSum / chartDataArray.length;
                vm.humAvg = humSum / chartDataArray.length;

                // max 값 구하기
                vm.max = Math.max.apply(null, Object.keys(chartDataArray)
                    .map((key) => chartDataArray[key][keyword]));

                // min 값구하기
                vm.min = 9999999999999;
                for (let i = 0; i < chartDataArray.length; i += 1) {
                    const minTarget = chartDataArray[i][keyword];

                    if (minTarget !== 0) {
                        if (minTarget <= vm.min) {
                            vm.min = minTarget;
                        }
                    }

                    if (i === chartDataArray.length - 1) {
                        if (vm.min === 9999999999999) {
                            vm.min = 0;
                        }
                    }
                }

                if (vm.params.timeType !== 'Y' || vm.params.timeType !== 'R') {
                    vm.zeroCount = 0;
                    for (let i = 0; i < chartDataKey.length; i += 1) {
                        vm.beforeSum += chartDataValue[i][beforeKeyWord];
                        if (chartDataValue[i][beforeKeyWord] !== 0) {
                        }
                        vm.zeroCount += 1;
                    }
                    vm.beforeAvg = vm.beforeSum / chartDataArray.length;
                    vm.beforeMax =
                        Math.max.apply(null, Object.keys(chartDataArray)
                            .map((key) => chartDataArray[key][beforeKeyWord]));

                    vm.beforeMin = 9999999999999;
                    for (let i = 0; i < chartDataArray.length; i += 1) {
                        const beforeMinTarget = chartDataArray[i][beforeKeyWord];
                        if (beforeMinTarget !== 0) {
                            if (beforeMinTarget <= vm.beforeMin) {
                                vm.beforeMin = beforeMinTarget;
                            }
                        }
                        if (i === chartDataArray.length - 1) {
                            if (vm.beforeMin === 9999999999999) {
                                vm.beforeMin = 0;
                            }
                        }
                    }
                }
                vm.ChartSummary();
            },
            ChartSummary() {
                const vm = this;
                vm.searchResult.min.value = parseFloat(Number(vm.min).toFixed(5));
                vm.searchResult.max.value = parseFloat(Number(vm.max).toFixed(5));
                vm.searchResult.avg.value = parseFloat(Number(vm.avg).toFixed(5));
                vm.searchResult.sum.value = parseFloat(Number(vm.sum).toFixed(5));
                vm.searchResult.min.beforeValue = parseFloat(Number(vm.beforeMin).toFixed(5));
                vm.searchResult.max.beforeValue = parseFloat(Number(vm.beforeMax).toFixed(5));
                vm.searchResult.avg.beforeValue = parseFloat(Number(vm.beforeAvg).toFixed(5));
                vm.searchResult.sum.beforeValue = parseFloat(Number(vm.beforeSum).toFixed(5));

                vm.searchResult.sum.show = !(vm.params.usageType === "PF" || vm.params.usageType === 'kW');

            },
            customizePoint(arg) {
                if (this.zeroCount >= 5) {
                    const vm = this;
                    let {min} = this;
                    let {max} = this;
                    let format;
                    if (vm.params.usageType === 'Usage') {
                        vm.DevNumberFormat = 0;
                        format = '#,##0.##';
                    } else {
                        vm.DevNumberFormat = 5;
                        format = '#,##0.#####';
                    }
                    const value = Number(Number(arg.value).toFixed(vm.DevNumberFormat));
                    min = Number(Number(min).toFixed(vm.DevNumberFormat));
                    max = Number(Number(max).toFixed(vm.DevNumberFormat));

                    if (arg.series.index === 0) {
                        if (value === max && max !== 0) {
                            return {
                                color: this.colorArray.colorDefault.red,
                                hoverStyle: {color: this.colorArray.colorDefault.red},
                            };
                        }
                        if (value === min && min !== 0) {
                            return {
                                color: this.colorArray.colorDefault.skyblue,
                                hoverStyle: {color: this.colorArray.colorDefault.skyblue},
                            };
                        }
                    }
                }
                return true;
            },
            customizeLabel(arg) {
                if (this.zeroCount >= 5) {
                    const vm = this;
                    let {min} = this;
                    let {max} = this;
                    let format;
                    if (vm.params.usageType === 'Usage') {
                        vm.DevNumberFormat = 0;
                        format = '#,##0.##';
                    } else {
                        vm.DevNumberFormat = 5;
                        format = '#,##0.#####';
                    }
                    const value = Number(Number(arg.value).toFixed(vm.DevNumberFormat));
                    min = Number(Number(min).toFixed(vm.DevNumberFormat));
                    max = Number(Number(max).toFixed(vm.DevNumberFormat));

                    if (arg.series.index === 0) {
                        if (value === max && max !== 0) {
                            return {
                                visible: true,
                                backgroundColor: this.colorArray.colorDefault.red,
                                format: format,
                                customizeText() {
                                    return this.valueText;
                                },
                            };
                        }
                        if (value === min && min !== 0) {
                            return {
                                visible: true,
                                backgroundColor: this.colorArray.colorDefault.skyblue,
                                format: format,
                                customizeText() {
                                    return this.valueText;
                                },
                            };
                        }
                    }
                }
                return true;
            },
            customizeTooltip: function (arg) {
                const vm = this;
                let format = '';
                if (arg.seriesName.indexOf('사용량') !== -1) {
                    format = 'kWh';
                } else if (arg.seriesName.indexOf('최대수요') !== -1) {
                    format = 'kW';
                } else {
                    format = this.unitArray[vm.params.usageType];
                }
                let value = arg.value.toFixed(2);
                return {
                    text: `구분 : ${arg.argument}<br>${arg.seriesName} : ${value} ${format}`
                };
            },
            chartRerender() {
                this.$refs.analysisEnergyChart.instance.refresh();
                this.$refs.radialChart0[0].refresh();
                this.$refs.radialChart1[0].refresh();
            },
            filterStateChange(state) {
                this.filterState = state;
                setTimeout(this.chartRerender, 400);
            },
        },
        filters: {
            numberFormat(target, limit) {
                if (target === undefined) {
                    return 0;
                }
                if (typeof target === 'number') {
                    return parseFloat(Number(target)
                        .toFixed(limit));
                }
                return 0;
            },
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
            percentageChk(current, before) {
                let Value;
                if (current != null && before != null) {
                    if (before === 0) {
                        return 100;
                    }
                    Value = (current / before) * 100;
                    Value = Value.toFixed(2);
                } else {
                    Value = 0;
                }
                return Value;
            },
        },
        watch: {
            radialOption: {
                deep: true,
                handler(val) {
                }
            },
        }
    };
</script>
