<template>
    <div id="settingSubContent">
        <div class="wrapper animated fadeInRight">
            <div class="ibox">
                <div class="ibox-title ibox-noborder-title">
                    장비 관리
                </div>
                <div class="grid-header">
                    <button id="createEquipment" class="button add-button" @click="createEquipment">
                        <img src="~assets/images/setting/icn_setting_add.svg" alt="등록"/>
                    </button>
                </div>
                <div id="gridBox">
                    <DxDataGrid
                            id="equipmentListGrid"
                            :data-source="equipmentList"
                            :show-borders="false"
                            :onCellClick="updateEquipment"
                            key-expr="id"
                    >
                        <DxSearchPanel :visible="true" :highlight-case-sensitive="true"/>
                        <DxColumn data-field="id" caption="id" alignment="center" width="60"/>
                        <DxColumn data-field="type" caption="Type" alignment="center"/>
                        <DxColumn data-field="type" caption="Model" alignment="center"/>
                        <DxColumn data-field="name" caption="장비명" alignment="center"
                                  cell-template="blockGridTemplate" />
                        <DxPaging :enabled="true" :page-size="20"/>
                        <DxPager :show-page-size-selector="true" :allowed-page-sizes="pageSizes" :show-info="true"/>
                        <template #blockGridTemplate="{ data: cellData }">
                            <blockGridTemplate :cell-data="cellData"/>
                        </template>
                    </DxDataGrid>
                </div>
            </div>
            <createEquipmentModal v-bind:propsdata="modalData" v-on:callSearch="getEquipment" ref="equipmentModal"/>
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
    import {DxButton} from 'devextreme-vue/button';
    import createEquipmentModal from '~/components/settingModal/createEquipmentModal.vue';
    import flashModal from '~/components/flashmodal.vue';
    import blockGridTemplate from '~/components/gridTemplate/blockGridTemplate.vue';
    import blockGridAlarmTemplate from '~/components/gridTemplate/blockGridAlarmTemplate.vue';

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
            createEquipmentModal,
            DxDataGrid,
            DxColumn,
            DxPaging,
            DxEditing,
            DxSelection,
            DxLookup,
            DxPager,
            DxSearchPanel,
            DxButton,
            blockGridAlarmTemplate,
            blockGridTemplate
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
                equipmentList: [],
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
