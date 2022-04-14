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
                        <DxColumn data-field="groupName" caption="그룹명" alignment="center"/>
                        <DxColumn data-field="name" caption="공기압축기명" alignment="center" cell-template="blockGridTemplate"/>
                        <DxColumn data-field="schedule.isActive" caption="개별스케줄제어" alignment="center" width="130"
                                  cell-template="ONOFFTemplate"/>
                        <DxColumn data-field="schedule.min" caption="최소압력" alignment="center" width="100"/>
                        <DxColumn data-field="schedule.max" caption="최대압력" alignment="center" width="100"/>
                        <DxColumn data-field="schedule.dayOfWeeks" caption="요일" alignment="center"
                                  cell-template="dayOfWeeksTemplate"/>
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
    import dayOfWeeksTemplate from '~/components/gridTemplate/dayOfWeeksTemplate.vue';

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
            dayOfWeeksTemplate
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
                airCompressorList: [],
                pageSizes: [5, 10, 20], // 페이지사이즈
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
                axios({
                    method: 'get',
                    url: '/api/compressors',
                }).then((res) => {
                    vm.airCompressorList = res.data
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
