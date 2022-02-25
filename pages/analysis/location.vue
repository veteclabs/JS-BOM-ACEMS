<template>
    <div class="analysisEnergy">
        <div class="header-title">
            <h2>시설별 분석</h2>
        </div>
        <div class="wrapper animated fadeInRight">
            <div class="row">
                <div class="col-lg-12">
                    <div class="ibox">
                        <div class="ibox-title ibox-normal-title ibox-noborder-title">
                            Grid
                        </div>
                        <div class="ibox-content">
                            <dx-data-grid id="gridContainer"
                                          :data-source="chartData"
                                          :columns="gridDataColumn"
                                          :allow-column-resizing="true"
                                          :allowColumnReordering="true"
                                          :column-min-width="100"
                                          style="height:700px;">
                                <dx-export :enabled="true" :allow-export-selected-data="true"
                                           file-name="facilitiesHistory"/>
                                <dx-paging :page-size="100"/>
                                <dx-pager :show-page-size-selector="true" :allowed-page-sizes="[31]"
                                          :show-info="true"/>
                                <DxScrolling mode="virtual"/>
                                <template #blockGridTemplate="{ data: cellData }">
                                    <blockGridTemplate :cell-data="cellData"/>
                                </template>
                                <DxSummary>
                                    <DxTotalItem v-for="item in Number(lastDay.date)" :column="item + lastDay.count"
                                                 :key="item" summary-type="sum" :value-format="customizeText"/>
                                </DxSummary>
                            </dx-data-grid>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <searchFilter v-bind:propsdata="filterSettingData" v-on:refresh="getSearch"/>
        <Loading v-bind:propsdata="LoadingData"/>
        <flashModal v-bind:propsdata="msgData"/>
    </div>
</template>
<script>
    import axios from 'axios';
    import dayjs from 'dayjs';
    import {
        DxDataGrid, DxExport, DxPager, DxPaging,
        DxSummary,
        DxTotalItem, DxScrolling
    } from 'devextreme-vue/data-grid';
    import Loading from '~/components/loading.vue';
    import flashModal from '~/components/flashmodal.vue';
    import searchFilter from '~/components/searchFilter.vue';
    import blockGridTemplate from '~/components/gridTemplate/blockGridTemplate.vue';
    import columnNameList from '~/assets/data/columnNameList.json';

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
            dayjs,
            flashModal,
            DxDataGrid,
            DxExport,
            DxPager,
            DxPaging,
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
                            {text: 'Day', value: 'D'},
                            {text: 'Month', value: 'M'},
                        ],
                    },
                    usageType: {show: true, only: true},
                    date: {
                        show: true,
                    },
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
                lastDay: {
                    date: 12,
                    count: '월'
                },
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
                dataFormatArray: [],
                chartData: [],
                gridDataColumn: [],
            };
        },
        created() {
            this.analysisSetting();
        },
        methods: {
            analysisSetting() {
                this.columnNameList = columnNameList.columnNameList;
            },
            async getSearch(params) {
                const vm = this;
                this.LoadingData.show = true;
                if (params.timeType === 'D') {
                    params.date.start = dayjs(params.date.start).format("YYYY-MM-01");
                    params.date.end = dayjs(params.date.start).add(1, 'month').format("YYYY-MM-01");
                } else if (params.timeType === 'M') {
                    params.date.start = dayjs(params.date.start).format("YYYY-01");
                    params.date.end = dayjs(params.date.start).add(1, 'year').format("YYYY-01");
                }
                vm.params = params;
                axios({
                    method: 'get',
                    url: '/api/analysis/location',
                    params: params,
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.chartData = [];
                        vm.gridDataColumn = [];

                        const tempResult = res.data.value;

                        vm.dataFormatArray = {locationName: ''};
                        if (vm.params.timeType === 'D') {
                            vm.lastDay.date = dayjs(vm.params.date.start).endOf('month').$D;
                            vm.lastDay.count = '일';

                            for (let i = 1; i <= vm.lastDay.date; i++) {
                                vm.dataFormatArray[i] = 0
                            }
                        } else {
                            vm.lastDay.date = 12;
                            vm.lastDay.count = '월';
                            for (let i = 1; i <= vm.lastDay.date; i++) {
                                vm.dataFormatArray[i] = 0
                            }
                        }


                        const result = [];
                        tempResult.forEach((item) => {
                            let temp = JSON.parse(JSON.stringify(vm.dataFormatArray));
                            temp.locationName = item.locationName;
                            temp.departmentName = item.departmentName;
                            temp.processName = item.processName;
                            if (item.dataSet) {
                                item.dataSet.forEach(data => {
                                    if (data.hasOwnProperty(vm.params.timeType)) {
                                        temp[data[vm.params.timeType]] = data.Usage;
                                    }
                                });

                            }
                            result.push(temp);
                        });

                        if (result.length !== 0) {
                            vm.drawDefaultChart(result);
                        }
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

                const nameArray = vm.columnNameList;
                let valueName;

                let count;
                if (vm.params.timeType === 'D') {
                    count = '일'
                } else {
                    count = '월'
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

                    if (valueName === 'locationName') {
                        vm.gridDataColumn.push({
                            dataField: valueFieldValue,
                            caption: '시설명',
                            dataType: 'string',
                            alignment: 'center',
                            width: 250,
                            visibleIndex: 0,
                            cellTemplate: 'blockGridTemplate',
                        });
                    } else if (valueName === 'departmentName') {
                        vm.gridDataColumn.push({
                            dataField: valueFieldValue,
                            caption: '부서명',
                            dataType: 'string',
                            alignment: 'center',
                            width: 150,
                            visibleIndex: 0,
                            cellTemplate: 'blockGridTemplate',
                        });
                    }else if (valueName === 'processName') {
                        vm.gridDataColumn.push({
                            dataField: valueFieldValue,
                            caption: '공정명',
                            dataType: 'string',
                            alignment: 'center',
                            width: 150,
                            visibleIndex: 0,
                            cellTemplate: 'blockGridTemplate',
                        });
                    }else if (valueName !== 'undefined') {
                        vm.gridDataColumn.push({
                            dataField: valueFieldValue,
                            caption: `${valueName}${count}`,
                            alignment: 'center',
                        });
                    }
                }

            },
            customizeText(target) {
                if (target === undefined) {
                    return 0;
                }
                if (typeof target === 'number') {
                    return target.toFixed(0).replace(/(\d)(?=(\d{3})+(?:\.\d+)?$)/g, '$1,');
                }
                return 0;
            },
        },
        filters: {
            numberFormat(target, limit) {
                if (target === undefined) {
                    return 0;
                }
                if (typeof target === 'number') {
                    return parseFloat(Number(target).toFixed(limit));
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
