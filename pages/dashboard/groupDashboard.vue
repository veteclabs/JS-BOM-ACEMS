<template>
    <div id="SubContentWrap">
        <div class="row">
            <div class="col-lg-12">
                <div class="grid-header" style="padding:0 0 12px 0;">
                    <button class="button submit-button" @click="submit">저장</button>
                </div>
            </div>
            <div class="col-lg-10 group-overflow-box">
                <div class="group" v-for="group in groupList" :key="group.id">
                    <div class="title-box flex-box">
                        <h2>
                            <img src="~assets/images/dashboard/icn_dashboard_aircompressor.png" alt="aircompressor"/>
                            {{group.name}}
                        </h2>
                        <button class="button setting-button" @click="groupModalOpen(group.id)">
                            <img src="~assets/images/dashboard/icn_dashboard_setting.svg" alt="setting"/>
                        </button>
                    </div>
                    <div class="row group-content">
                        <div class="col-lg-2">
                            <div class="td-label">압력계</div>
                            <draggable class="list-group" :list="group.devices.pressure" group="pressure">
                                <div class="ibox" v-for="device in group.devices.pressure" :key="device.id">
                                    <div class="ibox-title ibox-noborder-title ibox-normal-title flex-box">
                                        <div>{{device.name}}</div>
                                        <h3 v-if="device.tags['AIR_PRE'] !== undefined">{{device.tags["AIR_PRE"].value.toFixed(2)}} {{device.tags["AIR_PRE"].unit}}</h3>
                                    </div>
                                </div>
                                <div v-if="group.devices.pressure.length === 0">등록된 압력계가 없습니다.</div>
                            </draggable>
                            <div class="td-label">온도계</div>
                            <draggable class="list-group" :list="group.devices.temperature" group="temperature">
                                <div class="ibox" v-for="device in group.devices.temperature" :key="device.id">
                                    <div class="ibox-title ibox-noborder-title ibox-normal-title flex-box">
                                        <div>{{device.name}}</div>
                                        <h3 v-if="device.tags['WAR_Temp'] !== undefined" >{{device.tags["WAR_Temp"].value.toFixed(2)}} {{device.tags["WAR_Temp"].unit}}</h3>
                                    </div>
                                </div>
                                <div v-if="group.devices.temperature.length === 0">온도계가 없습니다.</div>
                            </draggable>
                            <div class="td-label">유량계</div>
                            <draggable class="list-group" :list="group.devices.flow" group="flow">
                                <div class="ibox" v-for="device in group.devices.flow" :key="device.id">
                                    <div class="ibox-title ibox-noborder-title ibox-normal-title flex-box">
                                        <div>{{device.name}}</div>
                                        <h3 v-if="device.tags['AIR_Flow'] !== undefined">{{device.tags["AIR_Flow"].value.toFixed(2)}} {{device.tags["AIR_Flow"].unit}}</h3>
                                    </div>
                                </div>
                                <div v-if="group.devices.flow.length === 0">유량계가 없습니다.</div>
                            </draggable>
                        </div>
                        <div class="col-lg-8">
                            <div class="td-label">공기압축기</div>
                            <div class="row">
                                <draggable class="list-group" :list="group.airCompressors" group="airCompressor">
                                    <div v-for="device in group.airCompressors" :key="device.id" class="col-lg-4"><div class="ibox">
                                            <div class="ibox-title aircompressor-ibox-title flex-ibox-title">
                                                <nuxt-link :to="`/dashboard/${device.id}`">
                                                    <h3>{{device.name}}</h3>
                                                </nuxt-link>
                                                <img src="~assets/images/dashboard/icn_dashboard_setting.svg"
                                                     alt="setting"
                                                     class="setting-btn"
                                                     @click="settingModalOpen(device)"/>
                                            </div>
                                            <div class="ibox-content">
                                                <div class="group-state flex-box">
                                                    <div>
                                                        <span>상태</span>
                                                        <div :class="`${device.state} device-state`">{{device.state}}
                                                        </div>
                                                    </div>
                                                    <div class="percent">
                                                        <span>부하율</span>
                                                        <h3>{{tagVal | pickValue('Name',`${device.unit}_COMP_PCY`, 'Value')}} %</h3>
                                                    </div>
                                                </div>
                                                <ul class="tag-box" v-if="device.tags">
                                                    <li v-for="type in compTagSet">
                                                        <div v-if="device.tags[type] !== undefined">
                                                            {{device.tags[type].description}}
                                                        </div>
                                                        <div v-if="device.tags[type] !== undefined">
                                                            {{device.tags[type].value.toFixed(2)}} {{device.tags[type].unit}}
                                                        </div>
                                                    </li>
                                                </ul>
                                            </div>
                                        </div>
                                    </div>
                                    <div v-if="group.airCompressors.length === 0">공기압축기가 없습니다.</div>
                                </draggable>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-lg-2 group-overflow-box">
                <div class="group">
                    <div class="title-box flex-box">
                        <h2>
                            <img src="~assets/images/dashboard/icn_dashboard_aircompressor.png" alt="aircompressor"/>
                            그룹 미지정
                        </h2>
                    </div>
                    <div class="td-label">압력계</div>
                    <draggable class="list-group" :list="freeGroupList.pressure" group="pressure">
                        <div class="ibox" v-for="device in freeGroupList.pressure" :key="device.id">
                            <div class="ibox-title ibox-noborder-title ibox-normal-title flex-box">
                                <div>{{device.name}}</div>
                                <h3 v-if="device.tags['AIR_PRE'] !== undefined">{{device.tags["AIR_PRE"].value.toFixed(2)}} {{device.tags["AIR_PRE"].unit}}</h3>
                            </div>
                        </div>
                        <div v-if="freeGroupList.pressure.length === 0">압력계가 없습니다.</div>
                    </draggable>
                    <div class="td-label">온도계</div>
                    <draggable class="list-group" :list="freeGroupList.temperature" group="temperature">
                        <div class="ibox" v-for="device in freeGroupList.temperature" :key="device.id">
                            <div class="ibox-title ibox-noborder-title ibox-normal-title flex-box">
                                <div>{{device.name}}</div>
                                <h3 v-if="device.tags['WAR_Temp'] !== undefined" >{{device.tags["WAR_Temp"].value.toFixed(2)}} {{device.tags["WAR_Temp"].unit}}</h3>
                            </div>
                        </div>
                        <div v-if="freeGroupList.temperature.length === 0">온도계가 없습니다.</div>
                    </draggable>
                    <div class="td-label">유량계</div>
                    <draggable class="list-group" :list="freeGroupList.flow" group="flow">
                        <div class="ibox" v-for="device in freeGroupList.flow" :key="device.id">
                            <div class="ibox-title ibox-noborder-title ibox-normal-title flex-box">
                                <div>{{device.name}}</div>
                                <h3 v-if="device.tags['AIR_Flow'] !== undefined">{{device.tags["AIR_Flow"].value.toFixed(2)}} {{device.tags["AIR_Flow"].unit}}</h3>
                            </div>
                        </div>
                        <div v-if="freeGroupList.flow.length === 0">유량계가 없습니다.</div>
                    </draggable>
                    <div class="td-label">공기압축기</div>
                    <draggable class="list-group" :list="freeGroupList.airCompressor" group="airCompressor">
                        <div v-for="device in freeGroupList.airCompressor" :key="device.id">
                            <div class="ibox">
                                <div class="ibox-title aircompressor-ibox-title flex-ibox-title">
                                    <nuxt-link :to="`/dashboard/${device.id}`">
                                        <h3>{{device.name}}</h3>
                                    </nuxt-link>
                                    <img src="~assets/images/dashboard/icn_dashboard_setting.svg" alt="setting"
                                         class="setting-btn"
                                         @click="settingModalOpen(device)"/>
                                </div>
                                <div class="ibox-content">
                                    <div class="group-state flex-box">
                                        <div>
                                            <span>상태</span>
                                            <div :class="`${device.state} device-state`">{{device.state}}</div>
                                        </div>
                                        <div class="percent">
                                            <span>부하율</span>
                                            <h3>{{tagVal | pickValue('Name',`${device.unit}_COMP_PCY`, 'Value')}} %</h3>
                                        </div>
                                    </div>
                                    <ul class="tag-box">
                                        <li v-for="type in compTagSet">
                                            <div v-if="device.tags[type] !== undefined">
                                                {{device.tags[type].description}}
                                            </div>
                                            <div v-if="device.tags[type] !== undefined">
                                                {{device.tags[type].value.toFixed(2)}} {{device.tags[type].unit}}
                                            </div>
                                        </li>
                                    </ul>
                                </div>
                            </div>
                        </div>
                        <div v-if="freeGroupList.airCompressor.length === 0">공기압축기가 없습니다.</div>
                    </draggable>
                </div>
            </div>
        </div>
        <editGroupModal ref="editGroupModal" v-bind:propsdata="groupModalData"/>
        <settingEquipmentModal ref="settingEquipmentModal" v-bind:propsdata="settingModalData"/>
        <flashModal v-bind:propsdata="msgData"/>
        <Loading v-bind:propsdata="loadingData"/>
    </div>
</template>
<script>
    import dayjs from 'dayjs';
    import axios from 'axios';
    import Loading from '~/components/loading.vue';
    import flashModal from '~/components/flashmodal.vue';
    import settingEquipmentModal from '~/components/settingModal/settingEquipmentModal.vue';
    import editGroupModal from '~/components/settingModal/editGroupModal.vue';
    import airCompressorState from '~/components/dashboard/airCompressorState.vue';
    import TPArray from '~/assets/data/TPCode.json';
    import waTagSet from '~/assets/data/tagSet.json';
    import draggable from 'vuedraggable';


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
            flashModal,
            settingEquipmentModal,
            editGroupModal,
            airCompressorState,
            draggable
        },
        data() {
            return {
                compTagSet: waTagSet.airCompressorGroupDeshboardSet.tags,
                msgData: {
                    // 알람모달
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
                groupModalData: {
                    show: false,
                },
                tagVal: '',
                groupList: [],
                freeGroupList: {
                    pressure: [],
                    temperature: [],
                    flow: [],
                    airCompressor: []
                },
                airTagList: [
                    {id: 1, name: '패키치 배출압력', tagName: 'COMP_PDP', unit: ''},
                    {id: 2, name: '에어앤드온도', tagName: 'COMP_AT', unit: ''},
                    {id: 7, name: '전압', tagName: 'PWR_V', unit: 'V'},
                    {id: 8, name: '전류', tagName: 'PWR_A', unit: 'A'},
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
            //this.WaLogin();
            //this.getTagValues();
            //this.resetInterval();
            this.getGroup();
            this.loadingData.show = true;
        },
        beforeDestroy() {
            this.removeInterval();
        },
        methods: {
            async WaLogin() {
                const vm = this;
                axios.get('/api/WaLogin')
                    .catch((error) => {
                        vm.msgData.msg = error;
                    });
            },
            async getGroup() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/groups',
                    headers: {
                        setting: false,
                    }
                }).then((res) => {
                    vm.groupList = res.data
                }).catch((error) => {
                    vm.msgData.msg = error.response.data.message;
                }).finally(() => {
                    vm.loadingData.show = false;
                });


                axios({
                    method: 'get',
                    url: '/api/orphans',
                    headers: {
                        setting: false,
                    }
                }).then((res) => {
                    vm.freeGroupList = res.data
                }).catch((error) => {
                    vm.msgData.msg = error.response.data.message;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },
            async submit() {
                const vm = this;
                vm.loadingData.show= true;
                const params = vm.groupList;
                axios({
                    method: 'put',
                    url: '/api/groups',
                    data: params
                }).then((res) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = res.data.message;
                }).catch((error) => {
                    vm.msgData.msg = error.response.data.message;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },
            async getTagValues() {
                const vm = this;
                axios.post('/api/wa/port/getTagValue', {
                    portId: [1, 2, 3, 4, 5],
                }, {
                    timeout: vm.intervalTime,
                }).then((res) => {
                    if (res.data.Result.Total > 0) {
                        vm.tagVal = res.data.Values;
                    }
                }).catch((error) => {
                    vm.msgData.msg = error.response.data.message;
                }).finally(() => {
                    vm.loadingData.show = false;
                });
            },
            async setAirCompressor(device, stateValue) {
                const vm = this;

                const confirmResult = confirm(`해당 컴프레셔 상태를 ${stateValue}으로 변경합니다. 진행하시겠습니까?`);

                if (confirmResult) {
                    const params = {
                        device,
                        stateValue
                    };
                    vm.LoadingData.show = true;
                    axios.post('/api/setAirCompressor', params)
                        .then(() => {
                            vm.msgData.show = true;
                            vm.msgData.msg = '제어 명령이 완료되었습니다.';
                        })
                        .catch(() => {
                            vm.msgData.show = true;
                            vm.msgData.msg = '제어에 실패했습니다. 잠시 후 다시 시도해주세요.';
                        })
                        .finally(() => {
                            vm.LoadingData.show = false;
                        });
                } else {
                    vm.msgData.show = true;
                    vm.msgData.msg = '제어명령이 취소되었습니다';
                }

                return true;
            },
            settingModalOpen(id) {
                this.$refs.settingEquipmentModal.updateModal(id);
                this.settingModalData.show = true;
            },
            groupModalOpen(id) {
                this.groupModalData.show = true;
                this.$refs.editGroupModal.updateModal(id);
            },
            getProgressBarValue(unit) {
                const vm = this;
                let value = this.$options.filters.pickValue(vm.tagVal, 'Name', `${unit}_COMP_PCY`, 'Value');
                if (value < 0) {
                    value = 0;
                }
                return value;
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
                        if (targetError.length === 0) {
                            return;
                        } else {
                            return targetError[0].description
                        }
                    }
                }
            },
        },
    };
</script>
