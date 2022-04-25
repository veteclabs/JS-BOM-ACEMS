<template>
    <div id="settingSubContent">
        <div class="wrapper animated fadeInRight">
            <div class="ibox">
                <div class="ibox-title ibox-normal-title ibox-noborder-title">
                    장비 관리
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
                            :data-source="equipmentList"
                            :show-borders="false"
                            :onCellClick="updateEquipment"
                            :selected-row-keys="selectedKeys"
                            @selection-changed="selectionChanged"
                            key-expr="id"
                    >
                        <DxSearchPanel :visible="true" :highlight-case-sensitive="true"/>
                        <DxSelection mode="multiple"/>
                        <DxColumn data-field="id" caption="id" alignment="center" width="60"/>
                        <DxColumn data-field="type" caption="type" alignment="center" cell-template="blockGridTemplate"/>
                        <DxColumn data-field="groupId" caption="groupId" :visible="false"/>
                        <DxColumn data-field="groupName" caption="그룹명" alignment="center"/>
                        <DxColumn data-field="name" caption="장비명"/>
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
                selectedKeys: [], // 선택삭제 시 이용
                selectionChanged: (data) => {
                    this.selectedKeys = data.selectedRowKeys;
                },
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
                    url: '/api/etcs',
                    headers: {setting: true}
                }).then((res) => {
                    vm.equipmentList = res.data
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error.response.data.error;
                });
            },
            createEquipment() {
                this.modalData.show = true;
                this.$refs.equipmentModal.createdModal();
            },
            updateEquipment(e) {
                if (e.columnIndex !== 0) {
                    this.$refs.equipmentModal.updateModal(e.data);
                    this.modalData.show = true;
                }
            },
            deleteEquipment: function () {
                const vm = this;
                if (confirm("정말로 삭제 하시겠습니까? 삭제된 데이터는 복원되지 않습니다.")) {
                    axios({
                        url:'/api/etcs',
                        method:'delete',
                        data: vm.selectedKeys
                    }).then((res) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = res.data.message;
                        vm.getEquipment();
                    }).catch((error) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = error.response.data.error;
                    })
                }
            },
        },
    };
</script>
