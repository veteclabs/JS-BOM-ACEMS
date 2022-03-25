<template>
    <div id="settingTank">
        <div class="wrapper animated fadeInRight">
            <div class="ibox">
                <div class="grid-title-header">
                    <p>탱크 관리</p>
                    <div class="grid-header">
                        <button id="deleteUser" class="button del-button"
                                :disabled="!selectedTankKeys.length"
                                @click="deleteTank"/>
                        <button id="createTank" class="button add-button" @click="createTank">
                            <img src="~assets/images/setting/icn_setting_add.svg" alt="등록"/>
                        </button>
                    </div>
                </div>
                <div id="gridBox">
                    <dx-data-grid
                            id="TankListGrid"
                            :selected-row-keys="selectedTankKeys"
                            @selection-changed="selectionTankChanged"
                            :data-source="tankList"
                            :show-borders="false"
                            :onCellClick="updateTank"
                            key-expr="id"
                            :column-min-width="100"
                    >
                        <dx-search-panel :visible="true" :highlight-case-sensitive="true"/>
                        <DxSelection mode="multiple"/>
                        <dx-column data-field="id" caption="No" alignment="center" :width="100"/>
                        <dx-column data-field="name" caption="탱크명" alignment="center" />
                        <dx-column data-field="capacity" caption="용량" alignment="center"
                                   cell-template="blockGridTemplate"/>
                        <dx-paging :enabled="true" :page-size="20"/>
                        <dx-pager :show-page-size-selector="true" :allowed-page-sizes="pageSizes"
                                  :show-info="true"/>
                        <template #blockGridTemplate="{ data: cellData }">
                            <blockGridTemplate :cell-data="cellData"/>
                        </template>
                    </dx-data-grid>
                </div>
                <createModal v-bind:propsdata="modalData" ref="tankModal" v-on:callSearch="getTank"/>
                <flashModal v-bind:propsdata="msgData"/>
            </div>
        </div>
    </div>
</template>
<script>
    import axios from 'axios';
    import {
        DxDataGrid, DxColumn, DxPaging, DxEditing, DxSelection, DxLookup, DxPager, DxSearchPanel,
    } from 'devextreme-vue/data-grid';
    import {DxButton} from 'devextreme-vue/button';
    import createModal from '~/components/settingModal/createTankModal.vue';
    import flashModal from '~/components/flashmodal.vue';
    import blockGridTemplate from '~/components/gridTemplate/blockGridTemplate.vue';

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
            createModal,
            flashModal,
            DxDataGrid,
            DxColumn,
            DxPaging,
            DxEditing,
            DxSelection,
            DxLookup,
            DxPager,
            DxSearchPanel,
            DxButton,
            blockGridTemplate
        },
        data() {
            return {
                id: '',
                modalData: { // 계측기등록모달
                    show: false,
                    state: '',
                },
                msgData: { // 알람모달
                    msg: '',
                    show: false,
                    e: '',
                },
                tankList: [],
                pageSizes: [5, 10, 20, 50], // 페이지사이즈
                selectedTankKeys: [],
                selectionTankChanged: (data) => {
                    this.selectedTankKeys = data.selectedRowKeys;
                },
            };
        },
        created() {
            this.getTank();
        },
        methods: {
            async getTank() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/setting/tankes',
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.tankList = res.data.value;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            // 신규 장비 등록
            createTank() {
                this.$refs.tankModal.createdModal();
                this.modalData.show = true;
            },
            updateTank(e) {
                if (e.columnIndex !== 0) {
                    this.$refs.tankModal.updateModal(e.data);
                    this.modalData.show = true;
                }
            },
            deleteTank: function () {
                const vm = this;
                if (confirm("정말로 삭제 하시겠습니까? 삭제된 공정은 복원되지 않습니다.")) {
                    axios.delete('/api/setting/tank', {
                        data: {
                            ids: vm.selectedTankKeys
                        }
                    }).then((res) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = res.data.msg;
                        vm.getTank();
                    }).catch((error) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = error;
                    })
                }
            },
        },
    };
</script>
