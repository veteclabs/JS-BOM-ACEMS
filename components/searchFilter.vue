<template>
    <div :class="searchFilterState ? 'filter filter-show' : 'filter filter-hide'">
        <div id="DashboardCollapse">
            <a class="filter-toggle-btn" @click="collapseMenu">
                <img src="~assets/images/filter/icn_filter_hide.svg" v-if="searchFilterState" alt="toggle-btn"/>
                <img src="~assets/images/filter/icn_filter_hide.svg" v-if="!searchFilterState" alt="toggle-btn"/>
            </a>
            <div id="searchFilter">
                <div class="filter-title">
                    <p>
                        <img src="~assets/images/filter/icn_filter_show.svg" alt="filter" width="16" v-if="resetState"/>
                        <img src="~assets/images/filter/icn_filter_show_hover.svg" alt="filter" width="16"
                             v-if="!resetState"/>
                        Filter
                    </p>
                    <a @click="filterReset">Reset</a>
                </div>
                <div class="filter-content">
                    <div v-if="propsdata.timeType">
                        <ul class="button-group">
                            <li v-for="(type, index) in timeTypeOptions" :key="index">
                                <label v-bind:class=" { active: searchParams.timeType === type.value,
                                 last: index === timeTypeOptions.length - 1 && index % 2 === 0 }">
                                    <input type="radio" v-model="searchParams.timeType" v-bind:value="type.value"
                                           @change="refresh"/>
                                    {{ type.text }}
                                </label>
                            </li>
                        </ul>
                    </div>
                    <div v-if="propsdata.date">
                        <h4>날짜</h4>
                        <date-picker v-model="searchParams.date.start" :config="dateOptions"
                                     v-show="searchParams.timeType !== 'R'"
                                     class="datepicker"
                                     @dp-change="refresh()"/>
                        <v-date-picker
                                ref="rangeDatepicker"
                                mode="date"
                                v-model="searchParams.date"
                                :max-date="new Date()"
                                :masks="rangeDateOptions.masks"
                                is-range
                                v-show="searchParams.timeType === 'R'"
                                class="datepicker"
                        >
                            <template v-slot="{ inputValue, inputEvents }">
                                <div class="flex justify-center items-center">
                                    <input
                                            :value="inputValue.start"
                                            v-on="inputEvents.start"
                                            class="form-control"
                                    />
                                    <input
                                            :value="inputValue.end"
                                            v-on="inputEvents.end"
                                            class="form-control"
                                    />
                                </div>
                            </template>
                        </v-date-picker>
                    </div>
                    <div v-if="propsdata.tagType">
                        <h4>에너지원</h4>
                        <label class="radio-box" v-for="item in tagTypeOption" :key="item.id">
                            <input type="radio" v-model="searchParams.tagType" :value="item.id"/>
                            <div class="radio-circle">
                                <div class="inner-circle"/>
                            </div>
                            <div class="radio-label">{{ item.name }}</div>
                        </label>
                    </div>
                    <div v-if="propsdata.usageType">
                        <h4>사용 타입</h4>
                        <div v-for="item in usageTypeOption" :key="item.id">
                            <label class="radio-box" v-if="item.tagType.includes(searchParams.tagType)">
                                <input type="radio" v-model="searchParams.usageType" :value="item.id"
                                       @change="refresh"/>
                                <div class="radio-circle">
                                    <div class="inner-circle"/>
                                </div>
                                <div class="radio-label">{{ item.name }}</div>
                            </label>
                        </div>
                    </div>
                    <div v-if="propsdata.deviceId">
                        <h4>장비</h4>
                        <select v-model="searchParams.deviceId" @change="refresh"
                                v-show=" propsdata.deviceId && propsdata.deviceId.type === 'select'">
                            <option v-for="item in deviceList" :key="item.id" v-bind:value="item.id">
                                {{ item.name }}
                            </option>
                        </select>
                        <label v-show=" propsdata.deviceId && propsdata.deviceId.type === 'text'">
                            <input
                                    type="text"
                                    v-model="searchParams.deviceId"
                                    @keyup="refresh"
                            />
                        </label>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>
<script>
    import axios from 'axios';
    import dayjs from 'dayjs';

    export default {
        props: ['propsdata'],
        components: {
            dayjs
        },
        data() {
            return {
                filterShow: true,
                timeTypeOptions: [],
                dateOptions: { //데이터피커 옵션 값
                    format: 'YYYY-MM-DD',
                    useCurrent: false,
                    maxDate: new Date(),
                    minDate: new Date('2020-12-31'),
                },
                rangeDateOptions: {
                    masks: {
                        input: 'YYYY-MM-DD',
                    },
                },
                tagTypeOption: [
                    {id: 'KWH', name: '전력'},
                    {id: 'FLOW', name: '유량'},
                ],
                usageTypeOption: [
                    {id: 'Usage', name: '사용량', tagType: ['KWH', 'FLOW']},
                    {id: 'PF', name: '역률', tagType: ['KWH']},
                    {id: 'TOE', name: 'TOE', tagType: ['KWH']},
                    {id: 'tCo2', name: 'tCo2', tagType: ['KWH']},
                ],
                deviceList: [], // 등록된 계측기 리스트,
                searchParams: {
                    timeType: 'H', //시간검색타입
                    date: {
                        //선택날짜
                        start: new Date(),
                        end: new Date(),
                    },
                    tagType: 'KWH',
                    usageType: 'Usage', //에너지원사용량
                    deviceId: 'AU', //계측기이름
                },
                resetParams: {
                    timeType: 'H', //시간검색타입
                    date: {
                        //선택날짜
                        start: new Date(),
                        end: new Date(),
                    },
                    tagType: 'KWH',
                    usageType: 'Usage', //에너지원사용량
                    deviceId: 'AU', //계측기이름
                },
                resetState: true,
            };
        },
        computed: {
            searchFilterState: function () {
                return this.$store.getters.searchFilter;
            },
        },
        mounted() {
            this.getFilter();
            this.filterReset();
        },
        methods: {
            collapseMenu: function () {
                this.$store.commit("searchFilter", !this.searchFilterState);
            },
            getFilter: async function () {
                const vm = this;
                axios({
                    method: 'get',
                    url: 'api/analysis/data/type',
                    params: {
                        type: vm.searchParams.tagType
                    }
                }).then((res) => {
                    let result = res.data;
                    vm.deviceList = [{name: '전체', id:'AU'}];
                    result.forEach((item) => {
                        let target = item;
                        if (target !== '') {
                            vm.deviceList.push({
                                name: `${target.id}. ${item.name}`,
                                id: target.id
                            })
                        }
                    });
                    vm.searchParams.deviceId = 'AU';
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                });
            },
            refresh() {
                const vm = this;
                let params = {};

                for (let i in this.propsdata) {
                    params[i] = vm.searchParams[i];
                }

                if (params.timeType === 'H' || params.timeType === '15min') {
                    params.date.start = dayjs(params.date.start).format('YYYY-MM-DD');
                    params.date.end = dayjs(params.date.start).add(1, 'days').format('YYYY-MM-DD');
                } else if (params.timeType === 'D') {
                    params.date.start = dayjs(params.date.start).format('YYYY-MM');
                } else if (params.timeType === 'M') {
                    params.date.start = dayjs(params.date.start).format('YYYY');
                } else if (params.timeType === 'Y') {
                    params.date.start = dayjs(params.date.start).format('YYYY');
                } else if (params.timeType === 'R') {
                    params.date.start = dayjs(params.date.start).format('YYYY-MM-DD');
                    params.date.end = dayjs(params.date.end).format('YYYY-MM-DD');
                }

                vm.resetState = vm.filterResetChk();
                vm.$emit('send-message', 1);
                vm.$emit('refresh', params);

            },
            filterResetChk: function () {
                let resetChk = JSON.stringify(this.resetParams) === JSON.stringify(this.searchParams);
                return resetChk;
            },
            filterReset: function () {

                if (this.propsdata.timeType) {
                    this.timeTypeOptions = this.propsdata.timeType.value;
                    this.resetParams.timeType = this.timeTypeOptions[0].value;
                }

                this.searchParams = JSON.parse(JSON.stringify(this.resetParams));
                this.resetState = this.filterResetChk();
            },
        },
        watch: {
            'searchParams.tagType': async function() {
                this.searchParams.usageType = 'Usage';
                this.searchParams.deviceId =  'AU';
                await this.getFilter();
                this.refresh();
            },
            'searchParams.timeType': function () {
                if (this.searchParams.timeType === 'H' || this.searchParams.timeType === '15min') {
                    this.dateOptions.format = 'YYYY-MM-DD';
                } else if (this.searchParams.timeType === 'D') {
                    this.dateOptions.format = 'YYYY-MM';
                } else if (this.searchParams.timeType === 'M') {
                    this.dateOptions.format = 'YYYY';
                } else if (this.searchParams.timeType === 'Y') {
                    this.dateOptions.format = 'YYYY';
                } else if (this.searchParams.timeType === 'R') {
                    this.searchParams.date.start = dayjs(new Date()).format('YYYY-MM-DD');
                    this.searchParams.date.end = dayjs(new Date()).format('YYYY-MM-DD');
                    this.dateOptions.format = 'YYYY-MM-DD';
                    this.rangeDateOptions.masks.input = 'YYYY-MM-DD';
                    this.$refs.rangeDatepicker.updateValue(this.searchParams.date, {formatInput: true,});
                }
            },
        },
    };
</script>
