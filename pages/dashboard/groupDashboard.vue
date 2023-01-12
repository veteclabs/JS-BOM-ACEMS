<template>
    <div id="SubContentWrap">
        <div class="row">
            <div class="col-lg-12">
                <!--<div class="grid-header" style="padding:0 0 12px 0;">
                    <button class="button submit-button" @click="submit">저장</button>
                </div>-->
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
                        <div class="col-lg-3">
                            <div class="td-label">압력계</div>
                            <draggable @change="submit" class="list-group" :list="group.devices.pressure" group="pressure">
                                <div class="ibox" v-for="device in group.devices.pressure" :key="device.id">
                                    <div class="ibox-title ibox-noborder-title ibox-normal-title flex-box">
                                        <div>{{device.name}}</div>
                                        <h3 v-if="device.tags['AIR_PRE'] !== undefined">{{device.tags["AIR_PRE"].value| valueFormat(2)}} {{device.tags["AIR_PRE"].unit}}</h3>
                                    </div>
                                </div>
                                <div v-if="group.devices.pressure.length === 0">등록된 압력계가 없습니다.</div>
                            </draggable>
                            <div class="td-label">온도계</div>
                            <draggable @change="submit" change="submit" class="list-group" :list="group.devices.temperature" group="temperature">
                                <div class="ibox" v-for="device in group.devices.temperature" :key="device.id">
                                    <div class="ibox-title ibox-noborder-title ibox-normal-title flex-box">
                                        <div>{{device.name}}</div>
                                        <h3 v-if="device.tags['WAR_Temp'] !== undefined" >{{device.tags["WAR_Temp"].value| valueFormat(2)}} {{device.tags["WAR_Temp"].unit}}</h3>
                                    </div>
                                </div>
                                <div v-if="group.devices.temperature.length === 0">온도계가 없습니다.</div>
                            </draggable>
                            <div class="td-label">유량계</div>
                            <draggable @change="submit" class="list-group" :list="group.devices.flow" group="flow">
                                <div class="ibox" v-for="device in group.devices.flow" :key="device.id">
                                    <div class="ibox-title ibox-noborder-title ibox-normal-title flex-box">
                                        <div>{{device.name}}</div>
                                        <h3 v-if="device.tags['AIR_Flow'] !== undefined">{{device.tags["AIR_Flow"].value| valueFormat(2)}} {{device.tags["AIR_Flow"].unit}}</h3>
                                    </div>
                                </div>
                                <div v-if="group.devices.flow.length === 0">유량계가 없습니다.</div>
                            </draggable>
                        </div>
                        <div class="col-lg-9">
                            <div class="td-label">공기압축기</div>
                            <div class="row">
                                <draggable @change="submit" class="list-group" :list="group.airCompressors" group="airCompressor">
                                    <div v-for="device in group.airCompressors" :key="device.id" class="col-lg-4"><div class="ibox">
                                        <div class="ibox-title aircompressor-ibox-title flex-ibox-title">
                                            <nuxt-link :to="`/dashboard/${device.id}`">
                                                <h3>{{device.name}}</h3>
                                            </nuxt-link>
                                            <img src="~assets/images/dashboard/icn_dashboard_setting.svg"
                                                 alt="setting"
                                                 class="setting-btn"
                                                 @click="settingModalOpen(device.id)"/>
                                        </div>
                                        <div class="ibox-content">
                                            <div class="group-state flex-box" v-if="device.tags">
                                                <div>
                                                    <span>상태</span>
                                                    <div class="flex-box">
                                                        <div v-if="device.tags.COMP_Power !== undefined && device.tags.COMP_Power.value === 1" class="bom-badge blue-badge blue">RUN</div>
                                                        <div v-if="device.tags.COMP_Power !== undefined &&  device.tags.COMP_Power.value === 0 " class="bom-badge red-badge red">STOP</div>
                                                    </div>
                                                </div>
                                                <div class="percent" v-if="device.tags.COMP_LoadFactor">
                                                    <span>부하율</span>
                                                    <h3>{{device.tags.COMP_LoadFactor.value === null ? 0 : device.tags.COMP_LoadFactor.value}}%</h3>
                                                </div>
                                            </div>
                                            <ul class="tag-box" v-if="device.tags">
                                                <li v-for="tag in device.tags" :key="tag.tagName">
                                                    <div>{{tag.description}}</div>
                                                    <div>
                                                        {{tag.value| valueFormat(2)}} {{tag.unit}}
                                                    </div>
                                                </li>
                                            </ul>
                                            <div v-if="device.devices">
                                                <ul class="tag-box" v-for="power in device.devices.power" :key="power.name">
                                                    <li v-for="tag in power.tags" :key="tag.tagName">
                                                        <div>{{tag.description}}</div>
                                                        <div>
                                                            {{tag.value| valueFormat(2)}} {{tag.unit}}
                                                        </div>
                                                    </li>
                                                </ul>
                                            </div>
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
                    <draggable @change="submit" class="list-group" :list="freeGroupList.pressure" group="pressure">
                        <div class="ibox" v-for="device in freeGroupList.pressure" :key="device.id">
                            <div class="ibox-title ibox-noborder-title ibox-normal-title flex-box">
                                <div>{{device.name}}</div>
                                <h3 v-if="device.tags['AIR_PRE'] !== undefined">{{device.tags["AIR_PRE"].value | valueFormat(2)}} {{device.tags["AIR_PRE"].unit}}</h3>
                            </div>
                        </div>
                        <div v-if="freeGroupList.pressure.length === 0">압력계가 없습니다.</div>
                    </draggable>
                    <div class="td-label">온도계</div>
                    <draggable @change="submit" class="list-group" :list="freeGroupList.temperature" group="temperature">
                        <div class="ibox" v-for="device in freeGroupList.temperature" :key="device.id">
                            <div class="ibox-title ibox-noborder-title ibox-normal-title flex-box">
                                <div>{{device.name}}</div>
                                <h3 v-if="device.tags['WAR_Temp'] !== undefined" >{{device.tags["WAR_Temp"].value| valueFormat(2)}} {{device.tags["WAR_Temp"].unit}}</h3>
                            </div>
                        </div>
                        <div v-if="freeGroupList.temperature.length === 0">온도계가 없습니다.</div>
                    </draggable>
                    <div class="td-label">유량계</div>
                    <draggable @change="submit" class="list-group" :list="freeGroupList.flow" group="flow">
                        <div class="ibox" v-for="device in freeGroupList.flow" :key="device.id">
                            <div class="ibox-title ibox-noborder-title ibox-normal-title flex-box">
                                <div>{{device.name}}</div>
                                <h3 v-if="device.tags['AIR_Flow'] !== undefined">{{device.tags["AIR_Flow"].value| valueFormat(2)}} {{device.tags["AIR_Flow"].unit}}</h3>
                            </div>
                        </div>
                        <div v-if="freeGroupList.flow.length === 0">유량계가 없습니다.</div>
                    </draggable>
                    <div class="td-label">공기압축기</div>
                    <draggable @change="submit" class="list-group" :list="freeGroupList.airCompressor" group="airCompressor">
                        <div v-for="device in freeGroupList.airCompressor" :key="device.id">
                            <div class="ibox">
                                <div class="ibox-title aircompressor-ibox-title flex-ibox-title">
                                    <nuxt-link :to="`/dashboard/${device.id}`">
                                        <h3>{{device.name}}</h3>
                                    </nuxt-link>
                                    <img src="~assets/images/dashboard/icn_dashboard_setting.svg" alt="setting"
                                         class="setting-btn"
                                         @click="settingModalOpen(device.id)"/>
                                </div>
                                <div class="ibox-content">
                                    <div class="group-state flex-box" v-if="device.tags">
                                        <div>
                                            <span>상태</span>
                                            <div class="flex-box">
                                                <div v-if="device.tags.COMP_Power.value === 1" class="bom-badge blue-badge blue">RUN</div>
                                                <div v-if="device.tags.COMP_Power.value === 0 " class="bom-badge red-badge red">STOP</div>
                                            </div>
                                        </div>
                                        <div class="percent" v-if="device.tags.COMP_LoadFactor">
                                            <span>부하율</span>
                                            <h3>{{device.tags.COMP_LoadFactor.value === null ? 0 : device.tags.COMP_LoadFactor.value}}%</h3>
                                        </div>
                                    </div>
                                    <ul class="tag-box" v-if="device.tags">
                                        <li v-for="tag in device.tags" :key="tag.tagName">
                                            <div>{{tag.description}}</div>
                                            <div>
                                                {{tag.value| valueFormat(2)}} {{tag.unit}}
                                            </div>
                                        </li>
                                    </ul>
                                    <div v-if="device.devices">
                                        <ul class="tag-box" v-for="power in device.devices.power" :key="power.name">
                                            <li v-for="tag in power.tags" :key="tag.tagName">
                                                <div>{{tag.description}}</div>
                                                <div>
                                                    {{tag.value| valueFormat(2)}} {{tag.unit}}
                                                </div>
                                            </li>
                                        </ul>
                                    </div>
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
    import axios from 'axios';
    import Loading from '~/components/loading.vue';
    import flashModal from '~/components/flashmodal.vue';
    import settingEquipmentModal from '~/components/settingModal/settingEquipmentModal.vue';
    import editGroupModal from '~/components/settingModal/editGroupModal.vue';
    import airCompressorState from '~/components/dashboard/airCompressorState.vue';
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
                Interval1M: '',
                interval: '',
                intervalTime: 10 * 1000,
            };
        },
        mounted() {
            this.resetInterval();
            this.getGroup();
            this.loadingData.show = true;
        },
        beforeDestroy() {
            this.removeInterval();
        },
        methods: {
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
                    vm.msgData.show = true;
                    vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
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
                    vm.msgData.show = true;
                    vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
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
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                }).finally(() => {
                    vm.getGroup();
                    vm.loadingData.show = false;
                });
            },
            settingModalOpen(id) {
                this.$refs.settingEquipmentModal.updateModal(id);
                this.settingModalData.show = true;
            },
            groupModalOpen(id) {
                this.groupModalData.show = true;
                this.$refs.editGroupModal.updateModal(id);
            },
            resetInterval() {
                const vm = this;
                clearInterval(this.interval);
                vm.interval = null;
                vm.interval = setInterval(() => {
                    vm.getGroup();
                }, vm.intervalTime);
            },
            removeInterval() {
                const vm = this;
                clearInterval(vm.interval);
            },
        },
        filters: {
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
            numberFormat: (value, numFix) => {
                value = parseFloat(value);
                if (!value) return '0';
                return value.toLocaleString('ko-KR', {maximumFractionDigits: numFix});
            },
        },
    };
</script>
