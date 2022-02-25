<template>
    <div :class="collapseState? 'filter filter-show' : 'filter filter-hide'">
        <div id="DashboardCollapse">
            <a class="filter-toggle-btn" @click="collapseMenu">
                <img src="~assets/images/filter/icn_filter_hide.svg" v-if="collapseState" alt="toggle-btn"/>
                <img src="~assets/images/filter/icn_filter_hide.svg" v-if="!collapseState" alt="toggle-btn"/>
            </a>
            <div class="collapse-box">
                <div class="title flex-box" v-if="floatingData.load">
                    <p>현재 부하 시간대</p>
                    <div class="bom-badge red-badge red" v-if="floatingData.load.load === '최대부하'">
                        {{floatingData.load.load}}
                    </div>
                    <div class="bom-badge orange-badge orange" v-if="floatingData.load.load === '중간부하'">
                        {{floatingData.load.load}}
                    </div>
                    <div class="bom-badge green-badge green" v-if="floatingData.load.load === '경부하'">
                        {{floatingData.load.load}}
                    </div>
                    <p class="right" style="width:100%; margin:8px 0 0 0;">
                        {{floatingData.load.season}}
                        {{floatingData.load.start}}시 ~ {{floatingData.load.end }}시
                    </p>
                </div>
                <div class="title flex-box">
                    <p>kWh 당 요금</p>
                    <div class="bom-badge">{{floatingData.unitPrice}}원</div>
                </div>
                <div class="title">
                    <p>일일 전력 사용량</p>
                </div>
                <div class="content">
                    <h1>{{floatingData.dayUsage| numberFormat(0)}}kWh</h1>
                </div>
                <div class="title">
                    <p>부하별 사용량 비율</p>
                </div>
                <div class="content dashboard-collapse-chart-box">
                    <DxPieChart
                            id="collapsePieChart"
                            ref="collapsePieChart"
                            :data-source="floatingData.loadPrice"
                            type="doughnut"
                            :inner-radius="0.85"
                            :customize-point="customColorChange"
                            :start-angle="90"
                    >
                        <dx-series argument-field="load" value-field="Usage">
                            <dx-label :visible="false"/>
                        </dx-series>
                        <dx-legend :visible="false"/>
                        <dx-tooltip :enabled="true" :customize-tooltip="rateTooltip"/>
                    </DxPieChart>
                    <div class="flex-box" v-for="(item,index) in floatingData.loadPrice" :key="item.name">
                        <p class="g6">
                            <span :style="`background:${powerColorArray[index]}`"/>
                            {{item.load}}
                        </p>
                        <p>{{item.Usage | numberFormat(0)}}kWh</p>
                    </div>
                </div>
                <div v-if="departmentId === '' || departmentId === undefined">
                    <div class="title">
                        <p>부서별 사용량 비율</p>
                    </div>
                    <div class="content dashboard-collapse-chart-box">
                        <DxPieChart
                                v-if="departmentRate.length !== 0"
                                id="departmentRateChart"
                                ref="departmentRateChart"
                                :data-source="departmentRate"
                                type="doughnut"
                                :inner-radius="0.85"
                                :customize-point="departmentCustomColorChange"
                                :start-angle="90"
                        >
                            <dx-series argument-field="name" value-field="Usage">
                                <dx-label :visible="false"/>
                            </dx-series>
                            <dx-legend :visible="false"/>
                            <dx-tooltip :enabled="true" :customize-tooltip="rateTooltip"/>
                        </DxPieChart>
                        <div class="flex-box" v-for="(item,index) in departmentRate" :key="item.name">
                            <p class="g6">
                                <span :style="`background:${departmentColorArray[index]}`"/>
                                {{item.name}}
                            </p>
                            <p>{{item.Usage | numberFormat(0)}}kWh</p>
                        </div>
                    </div>

                </div>
                <div v-if="floatingData.departmentRank !== undefined">
                    <div class="title">
                        최대 사용부서
                    </div>
                    <div class="content flex-box" style="align-items: flex-start"
                         v-if="floatingData.departmentRank !== undefined">
                        <div>
                            <img src="~assets/images/dashboard/icn_dashboard_alert.png" alt="alarm" width="24"/>
                        </div>
                        <div>
                            <h1 class="right" style="margin:0 0 8px 0">{{floatingData.departmentRank.name}}</h1>
                            <p class="red right">
                                <strong>{{floatingData.departmentRank.Usage | numberFormat(0)}}kWh</strong>
                            </p>
                        </div>
                    </div>
                </div>
                <div class="title">
                    최대 사용시설
                </div>
                <div class="content flex-box">
                    <div>
                        <img src="~assets/images/dashboard/icn_dashboard_alert.png" alt="alarm" width="24"/>
                    </div>
                    <div v-if="floatingData.locationRank !== undefined">
                        <h1 class="right" style="margin:0 0 8px 0">{{floatingData.locationRank.locationName}}</h1>
                        <p class="red right">
                            <strong>{{floatingData.locationRank.Usage | numberFormat(0)}}kWh</strong>
                        </p>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
    import DxChart, {
        DxCommonSeriesSettings,
        DxLabel,
        DxLegend,
        DxSeries,
        DxTooltip,
        DxMargin
    } from 'devextreme-vue/chart';
    import DxPieChart from 'devextreme-vue/pie-chart';
    import axios from "axios";

    export default {
        name: "DashboardCollapse",
        components: {
            DxChart,
            DxPieChart,
            DxCommonSeriesSettings,
            DxLabel,
            DxLegend,
            DxSeries,
            DxTooltip,
            DxMargin
        },
        data() {
            return {
                floatingData: '',
                powerRate: [
                    {name: '부하1', value: 525},
                    {name: '부하2', value: 328},
                    {name: '부하3', value: 92}
                ],
                departmentId: '',
                powerColorArray: ["#09c488", "#ffa100", "#ff3a55"],
                departmentColorArray: ["#ffd514", "#81e400",
                    "#27aef3", "#3363ff", "#EB3673", "#C93A96", "#A63EB8"],
                departmentRate: [],
                Interval1M: '',
            };
        },
        filters: {
            numberFormat: (value, numFix) => {
                value = parseFloat(value);
                if (!value) return '0';
                return value.toLocaleString('ko-KR', {maximumFractionDigits: numFix});
            },
        },
        mounted() {
            if (this.$route.query.depth) {
                this.departmentId = this.$route.query.depth;
            }
            this.getFloating();
            this.getDepartmentRate();
            this.reset1MInterval();
        },
        beforeDestroy() {
            this.remove1MInterval();
        },
        computed: {
            collapseState: function () {
                return this.$store.getters.collapseMenu;
            },
        },
        methods: {
            async getDepartmentRate() {
                const vm = this;
                axios.get('/api/dashboard/department/rate')
                    .then((res) => {
                        if (res.data.code === 1) {
                            vm.departmentRate = res.data.data;
                        }
                    }).catch((error) => {
                    vm.msgData.msg = error;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },
            async getFloating() {
                const vm = this;
                let params = {};
                if (vm.departmentId !== '') {
                    params = {
                        departmentId: vm.departmentId,
                    }
                }
                axios.get('/api/dashboard/floating', {
                    timeout: vm.intervalTime,
                    params: params
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.floatingData = res.data.data;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                })
            },
            collapseMenu: function () {
                this.$store.commit("dashboardCollapse", !this.collapseState);
            },
            customColorChange(point) {
                return {
                    color: this.powerColorArray[point.index]
                };
            },
            departmentCustomColorChange(point) {
                return {
                    color: this.departmentColorArray[point.index]
                };
            },
            rateTooltip({valueText, percent}) {
                return {
                    text: `${valueText} - ${(percent * 100).toFixed(2)}%`
                };
            },
            reset1MInterval: function () {//5분 인터벌 시작(월별전력량/시간별전력량)
                let vm = this;
                clearInterval(this.Interval1M);
                this.Interval1M = setInterval(function () {
                    vm.getFloating();
                    vm.getDepartmentRate();
                }, 60 * 1000)
            },
            remove1MInterval: function () {//5분 인터벌삭제
                let vm = this;
                clearInterval(vm.Interval1M);
            }
        },
        watch: {
            $route: function () {
                this.departmentId = this.$route.query.depth;
                this.getFloating();
            }
        }
    };
</script>
