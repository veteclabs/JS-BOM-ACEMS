<template>
    <div id="settingSubContent">
        <div class="wrapper animated fadeInRight">
            <div class="ibox">
                <div class="ibox-title ibox-normal-title ibox-noborder-title">
                    공기압축기관리
                </div>
                <div class="grid-header">
                    <button id="deleteUser" class="button del-button"
                            :disabled="!selectedKeys.length"
                            @click="deleteEquipment"/>
                    <button id="createEquipment" class="button add-button" @click="createEquipment">
                        <img src="~assets/images/setting/icn_setting_add.svg" alt="등록"/>
                    </button>
                </div>
                <div id="gridBox">
                    <DxDataGrid
                            id="equipmentListGrid"
                            :data-source="airCompressorList"
                            :show-borders="false"
                            :selected-row-keys="selectedKeys"
                            @selection-changed="selectionChanged"
                            :onCellClick="updateEquipment"
                            key-expr="id"
                            :allow-column-resizing="true"
                            :allowColumnReordering="true"
                            :column-min-width="100"
                    >
                        <DxScrolling mode="standard"/>
                        <DxSearchPanel :visible="true" :highlight-case-sensitive="true"/>
                        <DxSelection mode="multiple"/>
                        <DxColumn data-field="id" caption="id" alignment="center" :width="60"/>
                        <DxColumn data-field="groupName" caption="그룹명" alignment="center"/>
                        <DxColumn data-field="name" caption="공기압축기명" alignment="center"
                                  cell-template="blockGridTemplate"/>
                        <DxColumn data-field="schedule.isActive" caption="개별스케줄제어" alignment="center" :width="130"
                                  cell-template="ONOFFTemplate"/>
                        <DxColumn data-field="schedule.min" caption="최소압력" alignment="center" :width="50"/>
                        <DxColumn data-field="schedule.max" caption="최대압력" alignment="center" :width="50"/>
                        <DxColumn data-field="schedule.dayOfWeeks" caption="요일" alignment="center"
                                  cell-template="dayOfWeeksTemplate" :width="260"/>
                        <DxColumn data-field="schedule.startTime" caption="시작시간" alignment="center" width="100"/>
                        <DxColumn data-field="schedule.stopTime" caption="종료시간" alignment="center" width="100"/>
                        <DxPaging :enabled="true" :page-size="20"/>
                        <DxPager :show-page-size-selector="true" :allowed-page-sizes="pageSizes" :show-info="true"/>
                        <template #blockGridTemplate="{ data: cellData }">
                            <blockGridTemplate :cell-data="cellData"/>
                        </template>
                        <template #ONOFFTemplate="{ data: cellData }">
                            <ONOFFTemplate :cell-data="cellData"/>
                        </template>
                        <template #dayOfWeeksTemplate="{ data: cellData }">
                            <dayOfWeeksTemplate :cell-data="cellData"/>
                        </template>
                    </DxDataGrid>
                </div>
            </div>
            <settingEquipmentModal ref="equipmentModal" v-bind:propsdata="modalData" v-on:callSearch="getCompressor"/>
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
    import settingEquipmentModal from '~/components/settingModal/settingEquipmentModal.vue';
    import flashModal from '~/components/flashmodal.vue';
    import blockGridTemplate from '~/components/gridTemplate/blockGridTemplate.vue';
    import ONOFFTemplate from '~/components/gridTemplate/ONOFFTemplate.vue';
    import dayOfWeeksTemplate from '~/components/gridTemplate/dayOfWeeksTemplate.vue';
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
            settingEquipmentModal,
            DxDataGrid,
            DxColumn,
            DxPaging,
            DxEditing,
            DxSelection,
            DxLookup,
            DxPager,
            DxScrolling,
            DxSearchPanel,
            blockGridTemplate,
            ONOFFTemplate,
            dayOfWeeksTemplate,
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
                airCompressorList: [],
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
            this.getCompressor();
        },
        methods: {
            getCompressor() {
                const vm = this;
                vm.LoadingData.show = true;
                axios({
                    method: 'get',
                    url: '/api/compressors',
                    headers: {setting: true}
                }).then((res) => {
                    vm.airCompressorList = res.data
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                }).finally(() => {
                    vm.LoadingData.show = false;
                });
            },
            createEquipment() {
                this.$refs.equipmentModal.createdModal();
                this.modalData.show = true;
            },
            updateEquipment(e) {
                if (e.columnIndex !== 0) {
                    this.$refs.equipmentModal.updateModal(e.data.id);
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
                        vm.getCompressor();
                    }).catch((error) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = error.response.data.message ? error.response.data.message : error;
                    })
                }
            },
        },
    };
</script>
