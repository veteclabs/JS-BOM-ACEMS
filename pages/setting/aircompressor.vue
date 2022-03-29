<template>
    <div id="settingSubContent">
        <div class="wrapper animated fadeInRight">
            <div class="ibox">
                <div class="ibox-title ibox-normal-title ibox-noborder-title">
                    공기압축기관리
                </div>
                <div class="grid-header">
                    <button id="createEquipment" class="button add-button" @click="createEquipment">
                        <img src="~assets/images/setting/icn_setting_add.svg" alt="등록"/>
                    </button>
                </div>
                <div id="gridBox">
                    <DxDataGrid
                            id="equipmentListGrid"
                            :data-source="airCompressorList"
                            :show-borders="false"
                            :onCellClick="updateEquipment"
                            key-expr="id"
                    >
                        <DxSearchPanel :visible="true" :highlight-case-sensitive="true"/>
                        <DxColumn data-field="id" caption="id" alignment="center" width="60"/>
                        <DxColumn data-field="group" caption="그룹" alignment="center"/>
                        <DxColumn data-field="name" caption="공기압축기명" alignment="center"
                                  cell-template="blockGridTemplate"/>
                        <DxColumn data-field="isSchedule" caption="개별스케줄제어" alignment="center"
                                  cell-template="ONOFFTemplate"/>
                        <DxPaging :enabled="true" :page-size="20"/>
                        <DxPager :show-page-size-selector="true" :allowed-page-sizes="pageSizes" :show-info="true"/>
                        <template #blockGridTemplate="{ data: cellData }">
                            <blockGridTemplate :cell-data="cellData"/>
                        </template>
                        <template #ONOFFTemplate="{ data: cellData }">
                            <ONOFFTemplate :cell-data="cellData"/>
                        </template>
                    </DxDataGrid>
                </div>
            </div>
            <settingEquipmentModal ref="equipmentModal" v-bind:propsdata="modalData"/>
            <flashModal v-bind:propsdata="msgData"/>
        </div>
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
        DxSearchPanel,
    } from 'devextreme-vue/data-grid';
    import settingEquipmentModal from '~/components/settingModal/settingEquipmentModal.vue';
    import flashModal from '~/components/flashmodal.vue';
    import blockGridTemplate from '~/components/gridTemplate/blockGridTemplate.vue';
    import ONOFFTemplate from '~/components/gridTemplate/ONOFFTemplate.vue';

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
            DxSearchPanel,
            blockGridTemplate,
            ONOFFTemplate,
        },
        data() {
            return {
                id: '',
                msgData: {
                    msg: '',
                    show: false,
                    e: '',
                },
                modalData: {
                    show: false,
                },
                airCompressorList: [
                    {
                        id: 1,
                        barMin: 30,
                        barMax: 80,
                        isSchedule: 1,
                        schedule: '월, 화',
                        start: '18:00',
                        end: '07:00',
                        name: 'Ingersoll Rand 100'
                    },
                    {
                        id: 2,
                        barMin: 30,
                        barMax: 80,
                        isSchedule: 0,
                        schedule: '수, 목',
                        start: '18:00',
                        end: '07:00',
                        name: 'Ingersoll Rand 200'
                    },
                    {
                        id: 3,
                        barMin: 30,
                        barMax: 80,
                        isSchedule: 0,
                        schedule: '',
                        start: '18:00',
                        end: '07:00',
                        name: 'Ingersoll Rand 150'
                    },
                    {
                        id: 4,
                        barMin: 30,
                        barMax: 80,
                        isSchedule: 0,
                        schedule: '토, 일',
                        start: '18:00',
                        end: '07:00',
                        name: 'YUJIN 100'
                    },
                ],
                pageSizes: [5, 10, 20], // 페이지사이즈
            };
        },
        created() {
            this.id = this.$store.getters.ID;
        },
        mounted() {
            this.getEquipment();
        },
        methods: {
            async getEquipment() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/setting/equipments',
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.equipmentList = res.data.value;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            createEquipment() {
                this.$refs.equipmentModal.createdModal();
                this.modalData.show = true;
            },
            updateEquipment(e) {
                if (e.columnIndex !== 0) {
                    this.$refs.equipmentModal.updateModal(e.data);
                    this.modalData.show = true;
                }
            },
        },
    };
</script>
