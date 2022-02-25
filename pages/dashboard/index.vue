<template>
    <div id="SubContentWrap">
        <div class="row">
            <div class="col-lg-12 flex-box header-title">
                <h2>전체</h2>
                <h2>{{todayTime}}</h2>
            </div>
            <div class="col-lg-12">
                <div class="blueprint-box">
                    <div class="blueprint">
                        <div class="address">
                            <h2>
                                <img src="~assets/images/common/icn_menu_factory.png" alt="공장" width="18"/>
                                정석엔지니어링
                            </h2>
                            <p>17118 경기도 용인시 처인구 남사면 경기동로 227 (남사면 북리 124)</p>
                        </div>
                        <div class="dot-list">
                            <div :class="`location location-${item.id}`" v-for="item in locationList" :key="item.id">
                                <span/>
                                <h1>{{item.locationName}}</h1>
                            </div>
                            <div :class="`dot dot-main`">
                                <span/>
                                <h1>한국전력</h1>
                            </div>
                            <div :class="`dot dot-${item}`" v-for="item in 5" :key="item.id">
                                <span/>
                                <h1>{{item}} 변전실</h1>
                            </div>
                        </div>
                        <div class="state-box-list">
                            <div v-for="item in substationList" :key="item.id"
                                 :class="`state-box state-box-${item.id}`">
                                <div class="ibox">
                                    <div class="ibox-title">
                                        <nuxt-link :to="`/dashboard/substationDashboard?id=${item.id}`"
                                                   v-if="item.id === 216">
                                            {{item.name}}
                                        </nuxt-link>
                                        <div v-else>{{item.name}}</div>
                                    </div>
                                    <div class="ibox-content ibox-no-padding-content">
                                        <ul class="tag-list">
                                            <li v-for="tag in mainTagList" :key="tag.id" v-if="item.name === '한국전력'">
                                                <div>{{tag.name}}</div>
                                                <div>
                                                    {{tagVal | pickTagValue(`${tag.tagName}`)| numberFormat(2) }}
                                                    {{tag.unit}}
                                                </div>
                                            </li>
                                            <li v-for="tag in tagList" :key="tag.id" v-if="item.name !== '한국전력'">
                                                <div>{{tag.name}}</div>
                                                <div>
                                                    {{tagVal | pickTagValue(`${item.id}_${tag.tagName}`)|
                                                    numberFormat(2) }}
                                                    {{tag.unit}}
                                                </div>
                                            </li>
                                            <li v-if="item.name !== '한국전력'">
                                                <div>상한치 알람</div>
                                                <div>
                                                    <div v-if="$options.filters.pickValue( alarmList, 'substationId', item.id, 'isAlarm') === true"
                                                         class="bom-badge red-bg-badge">Alarm
                                                    </div>
                                                    <div v-else-if="$options.filters.pickValue( alarmList, 'substationId', item.id, 'isAlarm') === false"
                                                         class="bom-badge green-bg-badge">Normal
                                                    </div>
                                                    <div v-else class="bom-badge">연결중</div>
                                                </div>
                                            </li>
                                        </ul>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <svg xmlns="http://www.w3.org/2000/svg" width="214" height="221.999" class="line main-line">
                            <defs>
                                <linearGradient id="a" x1="1" y1=".021" x2=".017" y2="1"
                                                gradientUnits="objectBoundingBox">
                                    <stop offset="0" stop-color="#ffa100"/>
                                    <stop offset="1" stop-color="#ff3a55"/>
                                </linearGradient>
                            </defs>
                            <g style="fill:none;">
                                <path
                                        d="M0-3142v-202a15.9 15.9 0 0 1 4.686-11.314A15.893 15.893 0 0 1 16-3360h194a4 4 0 0 1 4 4 4 4 0 0 1-4 4H16a8.01 8.01 0 0 0-8 8v202a4 4 0 0 1-4 4 4 4 0 0 1-4-4Z"
                                        transform="translate(0 3360)" style="fill:url(#a)"/>
                                <path d="M0 218V14A14 14 0 0 1 14 0h196a1 1 0 0 1 0"
                                      transform="translate(3.5 4)" class="white-line"/>
                            </g>
                        </svg>
                        <svg xmlns="http://www.w3.org/2000/svg" width="617" height="8" class="line line-1">
                            <path d="M0 0h609" transform="translate(4 4)" class="orange-line"/>
                            <path d="M0 0h609" transform="translate(4 4)" class="white-line"/>
                        </svg>
                        <svg xmlns="http://www.w3.org/2000/svg" width="203.876" height="117.876" class="line line-2">
                            <path d="M193 0 0 107" transform="translate(5.438 5.438)" class="orange-line"/>
                            <path d="M193 0 0 107" transform="translate(5.438 5.438)" class="white-line"/>
                        </svg>
                        <svg xmlns="http://www.w3.org/2000/svg" width="41.903" height="120.903" class="line line-3">
                            <path d="m0 0 32 111" transform="translate(4.952 4.952)" class="orange-line"/>
                            <path d="m0 0 32 111" transform="translate(4.952 4.952)" class="white-line"/>
                        </svg>
                        <svg xmlns="http://www.w3.org/2000/svg" width="204.127" height="75.127" class="line line-4">
                            <path d="m0 0 194 65" transform="translate(5.064 5.064)" class="orange-line"/>
                            <path d="m0 0 194 65" transform="translate(5.064 5.064)" class="white-line"/>
                        </svg>
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
    import DxPieChart, {
        DxLabel,
        DxLegend,
        DxSeries,
        DxTooltip,
        DxMargin,
        DxSize, DxConnector
    } from 'devextreme-vue/pie-chart';

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
            DxPieChart,
            DxLabel,
            DxLegend,
            DxSeries,
            DxTooltip,
            DxMargin,
            DxSize, DxConnector
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
                tagVal: '',
                todayTime: '',
                locationList: [
                    {id: 1, locationId: 1, locationName: '가동'},
                    {id: 2, locationId: 2, locationName: '나동'},
                    {id: 3, locationId: 3, locationName: '다동'},
                    {id: 4, locationId: 7, locationName: '바동'},
                    {id: 5, locationId: 7, locationName: '아동'},
                    {id: 6, locationId: 8, locationName: '자동'},
                    {id: 7, locationId: 9, locationName: '차동'},
                ],
                alarmList: [],
                substationList: [
                    {id: 216, name: '한국전력'},
                    {id: 5, name: '5변전실'},
                    {id: 4, name: '4변전실'},
                    {id: 3, name: '3변전실'},
                    {id: 2, name: '2변전실'},
                    {id: 1, name: '1변젼실'},
                ],
                tagList: [
                    {id: 11, name: '유효전력량', tagName: 'PWR_KWh', unit: 'kWh'},
                    {id: 12, name: '유효전력', tagName: 'PWR_kW', unit: 'kW'},
                ],
                mainTagList: [
                    {id: 1, name: '유효전력량', tagName: 'U216_PWR_KWh', unit: 'kWh'},
                    {id: 2, name: '유효전력', tagName: 'U216_PWR_kW', unit: 'kW'},
                    {id: 3, name: '무효전력', tagName: 'U216_PWR_kVar', unit: 'Kvar'},
                    {id: 5, name: '피상전력', tagName: 'U216_PWR_kVa', unit: 'kVa'},
                    {id: 7, name: '전압', tagName: 'U216_PWR_V', unit: 'V'},
                    {id: 8, name: '전류', tagName: 'U216_PWR_A', unit: 'A'},
                    {id: 9, name: '역률', tagName: 'U216_PWR_PF', unit: '%'},
                    {id: 10, name: '주파수', tagName: 'U216_PWR_PF', unit: 'Hz'},
                ],
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
            this.timer();
            this.WaLogin();
            this.getTagValues();
            this.resetInterval();
            this.getSubstationAlarm();
            this.loadingData.show = true;
        },
        beforeDestroy() {
            this.removeInterval();
        },
        methods: {
            timer() {
                this.todayTime = dayjs().format('YYYY-MM-DD ddd A hh:mm');
            },
            async WaLogin() {
                const vm = this;
                axios.get('/api/WaLogin')
                    .catch((error) => {
                        vm.msgData.msg = error;
                    });
            },
            async getSubstationAlarm() {
                const vm = this;
                axios.get('/api/substation/alarm')
                    .then((res) => {
                        if (res.data.code === 1) {
                            vm.alarmList = res.data.value;
                        }
                    }).catch((error) => {
                    vm.msgData.msg = error;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },
            async getTagValues() {
                const vm = this;
                axios.post('/api/dashboard/port/getTagValue', {
                    portId: [-2, 33],
                }, {
                    timeout: vm.intervalTime,
                }).then((res) => {
                    if (res.data.Result.Total > 0) {
                        vm.tagVal = res.data.Values;
                        vm.todayTime = dayjs().format('YYYY-MM-DD ddd A hh:mm');
                    }
                }).catch((error) => {
                    vm.msgData.msg = error;
                }).finally(() => {
                    vm.loadingData.show = false;
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
        },
    };
</script>
