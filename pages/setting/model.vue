<template>
    <div id="settingSubContent">
        <div class="wrapper animated fadeInRight">
            <div class="ibox">
                <div class="ibox-title ibox-normal-title ibox-noborder-title">
                    모델관리
                </div>
                <div class="grid-header">
                    <button id="deleteUser" class="button del-button"
                            :disabled="!selectedKeys.length"
                            @click="deleteEquipment"/>
                    <button id="createEquipment" class="button add-button" @click="createdModal">
                        <img src="~assets/images/setting/icn_setting_add.svg" alt="등록"/>
                    </button>
                </div>
                <div id="gridBox">
                    <DxDataGrid
                            :data-source="modelList"
                            :show-borders="false"
                            :selected-row-keys="selectedKeys"
                            @selection-changed="selectionChanged"
                            :onCellClick="updateModal"
                            key-expr="id"
                            :allow-column-resizing="true"
                            :allowColumnReordering="true"
                            :column-min-width="100"
                    >
                        <DxScrolling mode="standard"/>
                        <DxSearchPanel :visible="true" :highlight-case-sensitive="true"/>
                        <DxSelection mode="multiple"/>
                        <DxColumn data-field="id" caption="id" alignment="center" :width="60"/>
                        <DxColumn data-field="type" alignment="center" :visible="false"/>
                        <DxColumn data-field="description" alignment="center"/>
                        <DxColumn data-field="maker" alignment="center"/>
                        <DxColumn data-field="model"/>
                        <DxColumn data-field="tagList" alignment="center"cell-template="tagListTemplate"/>
                        <DxPaging :enabled="true" :page-size="20"/>
                        <DxPager :show-page-size-selector="true" :allowed-page-sizes="pageSizes" :show-info="true"/>
                        <template #tagListTemplate="{ data: cellData }">
                            <div v-if="cellData.value && typeof(cellData.value) === 'object'">
                                <span class="badge" v-if="cellData.value">{{cellData.value.length}}</span>
                            </div>
                        </template>
                    </DxDataGrid>
                </div>
            </div>
            <settingModelModal ref="modelModal" v-bind:propsdata="modalData" v-on:callSearch="getModel"/>
            <flashModal v-bind:propsdata="msgData"/>
        </div>
        <Loading v-bind:propsdata="LoadingData"/>
    </div>
</template>
<script>
    import axios from 'axios';
    import {
        DxDataGrid,
        DxColumn,
        DxPaging,
        DxEditing,
        DxSelection,
        DxLookup,
        DxPager,
        DxScrolling,
        DxSearchPanel,
    } from 'devextreme-vue/data-grid';
    import settingModelModal from '~/components/settingModal/settingModelModal.vue';
    import flashModal from '~/components/flashmodal.vue';
    import Loading from '~/components/loading.vue';

    export default {
        fetch({store, redirect}) {
            if (!store.state.authUser) {
                return redirect('/');
            }
            return false;
        },
        layout: 'settingTemplate',
        components: {
            axios,
            flashModal,
            settingModelModal,
            DxDataGrid,
            DxColumn,
            DxPaging,
            DxEditing,
            DxSelection,
            DxLookup,
            DxPager,
            DxScrolling,
            DxSearchPanel,
            Loading
        },
        data() {
            return {
                id: '',
                LoadingData: {
                    show: false,
                },
                msgData: {
                    msg: '',
                    show: false,
                    e: '',
                },
                modalData: {
                    show: false,
                },
                modelList: [],
                pageSizes: [5, 10, 20],
                selectedKeys: [],
                selectionChanged: (data) => {
                    this.selectedKeys = data.selectedRowKeys;
                },
            };
        },
        created() {
            this.id = this.$store.getters.ID;
        },
        mounted() {
            this.getModel();
        },
        methods: {
            getModel() {
                const vm = this;
                vm.LoadingData.show = true;
                axios({
                    method: 'get',
                    url: '/api/equipments',
                }).then((res) => {
                    vm.modelList = res.data
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                }).finally(() => {
                    vm.LoadingData.show = false;
                });
            },
            createdModal() {
                this.$refs.modelModal.createdModal();
                this.modalData.show = true;
            },
            updateModal(e) {
                if (e.columnIndex !== 0) {
                    this.$refs.modelModal.updateModal(e.data);
                    this.modalData.show = true;
                }
            },
            deleteEquipment: function () {
                const vm = this;
                if (confirm("정말로 삭제 하시겠습니까? 삭제된 데이터는 복원되지 않습니다.")) {
                    axios({
                        url: '/api/compressors',
                        method: 'delete',
                        data: vm.selectedKeys
                    }).then((res) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = res.data.message;
                        vm.getModel();
                    }).catch((error) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                    })
                }
            },
        },
    };
</script>

COMP_ModeContinu
COMP_ModeLoading
COMP_ModeRemoval
COMP_ModeRemRese
COMP_ModeStop
COMP_RunningTime
COMP_SorAutoStop
COMP_SorRunWarm
COMP_SorSolenoid
COMP_SorStartOn
COMP_SeqPreOper
COMP_SeqReady
COMP_SeqReDrive
COMP_SeqRun
COMP_SeqSafeStop
COMP_SeqStabilit
COMP_SeqStart
COMP_SeqSutoStop
COMP_LED
COMP_LedAlarm
COMP_LedEmergenc
COMP_LedLoading
COMP_LedRemote
COMP_LedRunning
COMP_SystemPre
COMP_OilTemp
COMP_LoadFactor
COMP_LoadTime
COMP_kW
COMP_UA
COMP_UV
COMP_VA
COMP_VW
COMP_WA
COMP_Stop
COMP_StopAirFDPr
COMP_StopBackWar
COMP_StopCoolDPr
COMP_StopDOvePre
COMP_StopDPreFai
COMP_StopEmeStop
COMP_StopFanOvCu
COMP_StopInverFa
COMP_StopLowVol
COMP_StopOilFDPr
COMP_StopOilTemp
COMP_StopOilTeSe
COMP_StopOverCur
COMP_StopOverVol
COMP_WarAirFDPre
COMP_WarAirFilte
COMP_WarCoolDPre
COMP_WarEqPreFai
COMP_WarInComFai
COMP_WarInverFau
COMP_WarLowVol
COMP_WarMaComFai
COMP_WarOilFDPre
COMP_WarOilFilte
COMP_WarOilRepla
COMP_WarOilsepar
COMP_WarOilTempH
COMP_WarOilTempL
COMP_WarOver
COMP_WarOverHaul
