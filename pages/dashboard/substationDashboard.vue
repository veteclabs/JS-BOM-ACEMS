<template>
    <div id="SubContentWrap">
        <h1 class="header-title">
            <h2>한국전력</h2>
        </h1>
        <div class="CT-sub-info">
            <div class="row">
                <div class="col-lg-4 col-md-6" v-for="item in mainInfoList" :key="item.name">
                    <div class="ibox">
                        <div class="ibox-title">
                            <h5>{{item.name}}</h5>
                        </div>
                        <div class="ibox-content">
                            <h1>{{tagVal | pickCTValue(item.tagName, selectSubstation)| currency(2) }}</h1>
                            <p class="unit">{{item.unit}}</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <div class="ibox">
            <div class="ibox-title flex-box">
                <p>실시간 전력 소비 패턴</p>
                <p class="bom-badge ">Accura3700</p>
            </div>
            <div class="ibox-content">
                <client-only>
                    <apexchart type="line" height="300" ref="liveCTLineChart" :options="liveCTChartOption"
                               :series="liveCTChartData"/>
                </client-only>
            </div>
        </div>

        <div class="ibox CT-detail-grid">
            <div class="ibox-title">전력 품질</div>
            <div class="ibox-content CT-detail-panel">
                <ul class="CT-Monitoring-list-detail">
                    <li v-for="detail in detailList" :key="detail.tagName">
                        <div class="title">{{detail.name}}</div>
                        <div class="value">
                            <h3>{{tagVal| pickCTValue(detail.tagName, selectSubstation)| currency(2) }} </h3>
                            <p>{{detail.unit}}</p></div>
                    </li>
                </ul>
            </div>
        </div>
    </div>
</template>
<script>
    import moment from 'moment'
    import axios from 'axios'
    import DxChart, {DxArgumentAxis, DxLabel, DxLegend, DxSeries, DxTooltip} from 'devextreme-vue/chart';

    export default {
        fetch({store, redirect}) {
            if (!store.state.authUser) {
                return redirect('/')
            }
        },
        layout: 'template',
        components: {
            moment,
            axios,
            DxChart,
            DxArgumentAxis, DxLegend, DxLabel, DxSeries, DxTooltip
        },
        data() {
            return {
                userInfo: '',
                role: '',
                id: '',
                tagList: [],
                tagVal: '',
                mainInfoList: [
                    {name: '유효전력', tagName: 'PWR_kW', unit: 'kW'},
                    {name: '무효전력', tagName: 'PWR_kVar', unit: 'kVAr'},
                    {name: '피상전력', tagName: 'PWR_kVa', unit: 'kVA'},
                    {name: '유효전력량', tagName: 'PWR_KWh', unit: 'kWh'},
                    {name: '무효전력량', tagName: 'PWR_Kvarh', unit: 'Kvarh'},
                    {name: '피상전력량', tagName: 'PWR_Kvah', unit: 'Kvah'},
                ],
                selectSubstation: '1',
                CTName: '',
                //실시간 Chart
                timeCategories: [],
                liveCTChartData: [{name: "유효전력(kW)", data: []}, {name: "무효전력(kVAr)", data: []}], //데이터 변수
                liveCTChartOption: { //차트옵션 변수
                    dataLabels: {enabled: false},
                    colors: ['#003cff', '#06dc98', '#ffa100', '#ff3a55'],
                    grid: {borderColor: '#e4e9f1'},
                    stroke: {curve: 'smooth', width: 3},
                    tooltip: {
                        style: {fontFamily: 's-core_dream'},
                        x: {show: true},
                        y: {
                            show: true, formatter: function (val) {
                                return val + "<span style='color:#afafb1; font-size:10px;'>kW</span>"
                            }
                        }
                    },
                    xaxis: {categories: [], labels: {show: false}},
                    yaxis: {
                        labels: {
                            style: {colors: ['#667082'], fontFamily: 'Montserrat', fontSize: '10px'},
                            formatter: (value) => {
                                return value.toFixed(1)
                            }
                        }
                    },
                    legend: {fontSize: '10px', fontFamily: 's-core_dream', offsetY: 1}
                },
                //전력품질
                detailList: [
                    {name: 'A상 전류의 THD', tagName: 'PWR_A_THD_A', unit: '%'},
                    {name: 'B상 전류의 THD', tagName: 'PWR_A_THD_B', unit: '%'},
                    {name: 'C상 전류의 THD', tagName: 'PWR_A_THD_C', unit: '%'},
                    {name: 'A상 전류의 Crest Factor', tagName: 'PWR_CFa', unit: ''},
                    {name: 'B상 전류의 Crest Factor', tagName: 'PWR_CFb', unit: ''},
                    {name: 'C상 전류의 Crest Factor', tagName: 'PWR_CFc', unit: ''},
                    {name: 'A상 전류의 K-Factor', tagName: 'PWR_KFa', unit: ''},
                    {name: 'B상 전류의 K-Factor', tagName: 'PWR_KFb', unit: ''},
                    {name: 'C상 전류의 K-Factor', tagName: 'PWR_KFc', unit: ''},
                    {name: 'A상 전압의 TDD', tagName: 'PWR_V_TDD_A', unit: '%'},
                    {name: 'B상 전압의 TDD', tagName: 'PWR_V_TDD_B', unit: '%'},
                    {name: 'C상 전압의 TDD', tagName: 'PWR_V_TDD_C', unit: '%'},
                    {name: 'A상 전압의 THD', tagName: 'PWR_V_THD_A', unit: '%'},
                    {name: 'B상 전압의 THD', tagName: 'PWR_V_THD_B', unit: '%'},
                    {name: 'C상 전압의 THD', tagName: 'PWR_V_THD_C', unit: '%'},
                ],
                //인터벌 설정
                interval: null,
                intervalTime: 5000,
            }
        },
        created() {
            this.id = this.$store.getters.ID;
        },
        mounted() {
            this.selectSubstation = this.$route.query.id;
            this.tagSetting();
            this.resetInterval();
        },
        destroyed() {
            this.removeInterval();
        },
        methods: {
            tagSetting() {
                const vm = this;
                vm.tagList = [];

                vm.mainInfoList.forEach(item => {
                    vm.tagList.push({
                        tagName: `U${vm.selectSubstation}_${item.tagName}`
                    });
                });
                vm.detailList.forEach(item => {
                    vm.tagList.push({
                        tagName: `U${vm.selectSubstation}_${item.tagName}`
                    });
                });
                this.getTagValues();

            },
            //WEBACCESS TAG 값 가져오기
            getTagValues: async function () {//WEBACCESS TAG값 가져오기
                const vm = this;
                axios.post('/api/dashboard/tag/getTagValue', {
                    tagList: vm.tagList
                }, {
                    timeout: vm.intervalTime,
                }).then((res) => {
                    if (res.data.Result['Total'] > 0) {
                        vm.tagVal = res.data.Values;
                        vm.getLiveChartData();
                    }
                })
            },
            //LiveChart 그리기
            getLiveChartData() {
                let vm = this;

                //실시간유효전력량 Graph 실시간 변경
                let liveChartXcount = this.liveCTChartData[0].data.length;
                let maxCount = 360;
                let nowTime = moment(new Date().toISOString()).format('HH:mm:ss');

                //X좌표 설정
                if (liveChartXcount > maxCount) {
                    this.timeCategories.shift();
                }
                this.timeCategories.push(nowTime);
                this.$refs.liveCTLineChart.updateOptions({
                    "xaxis": {"categories": vm.timeCategories}
                });
                //Y좌표 설정
                let CTkWArray = [];
                let P_Tot = this.$options.filters.pickCTValue(vm.tagVal, 'PWR_kW', vm.selectSubstation);
                let Q_Tot = this.$options.filters.pickCTValue(vm.tagVal, 'PWR_kVar', vm.selectSubstation);

                CTkWArray = [P_Tot, Q_Tot];

                for (let i = 0; i < CTkWArray.length; i++) {
                    if (liveChartXcount > maxCount) {
                        this.liveCTChartData[0].data.shift();
                    }
                    let value = CTkWArray[i].toFixed(2);
                    this.liveCTChartData[i].data.push(value);
                }
            },
            //Interval 시작
            resetInterval: function () {
                let vm = this;
                clearInterval(this.interval);
                this.interval = setInterval(function () {
                    vm.getTagValues();
                }, vm.intervalTime);
            },
            //인터벌삭제
            removeInterval: function () {
                let vm = this;
                clearInterval(vm.interval);
            }
        },
        filters: {
            currency(value, limit) {
                value = parseFloat(value);
                if (!value) return '0';
                return value.toFixed(limit).replace(/(\d)(?=(\d{3})+(?:\.\d+)?$)/g, "$1,")
            },
            pickCTValue(object, key, CT) {
                key = `U${CT}_${key}`;
                if (object === undefined || object === null || object === "") {
                    return
                } else {
                    let target = object.filter(object => object.Name === key);
                    if (target.length === 0) {
                        return -100
                    } else {
                        return target[0].Value
                    }
                }
            },
        },
    }
</script>
