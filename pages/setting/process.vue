<template>
    <div id="settingProcess">
        <div class="wrapper animated fadeInRight">
            <div class="ibox">
                <div class="grid-title-header">
                    <p>공정 관리</p>
                    <div class="grid-header">
                        <button id="deleteUser" class="button del-button"
                                :disabled="!selectedProcessKeys.length"
                                @click="deleteProcess"/>
                        <button id="createProcess" class="button add-button" @click="createProcess">
                            <img src="~assets/images/setting/icn_setting_add.svg" alt="등록"/>
                        </button>
                    </div>
                </div>
                <div id="gridBox">
                    <dx-data-grid
                            id="ProcessListGrid"
                            :selected-row-keys="selectedProcessKeys"
                            @selection-changed="selectionProcessChanged"
                            :data-source="processList"
                            :show-borders="false"
                            :onCellClick="updateProcess"
                            key-expr="id"
                            :column-min-width="100"
                    >
                        <dx-search-panel :visible="true" :highlight-case-sensitive="true"/>
                        <DxSelection mode="multiple"/>
                        <dx-column data-field="id" caption="No" alignment="center" :width="100"/>
                        <dx-column data-field="name" caption="공정명" alignment="center" />
                        <dx-column data-field="costCenter" caption="Cost Center" alignment="center"
                                   cell-template="blockGridTemplate"/>
                        <dx-paging :enabled="true" :page-size="20"/>
                        <dx-pager :show-page-size-selector="true" :allowed-page-sizes="pageSizes"
                                  :show-info="true"/>
                        <template #blockGridTemplate="{ data: cellData }">
                            <blockGridTemplate :cell-data="cellData"/>
                        </template>
                    </dx-data-grid>
                </div>
                <createModal v-bind:propsdata="modalData" ref="processModal" v-on:callSearch="getProcess"/>
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
    import createModal from '~/components/settingModal/createProcessModal.vue';
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
                processList: [],
                pageSizes: [5, 10, 20, 50], // 페이지사이즈
                selectedProcessKeys: [],
                selectionProcessChanged: (data) => {
                    this.selectedProcessKeys = data.selectedRowKeys;
                },
            };
        },
        created() {
            this.getProcess();
        },
        methods: {
            async getProcess() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/setting/processes',
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.processList = res.data.value;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            // 신규 장비 등록
            createProcess() {
                this.$refs.processModal.createdModal();
                this.modalData.show = true;
            },
            updateProcess(e) {
                if (e.columnIndex !== 0) {
                    this.$refs.processModal.updateModal(e.data);
                    this.modalData.show = true;
                }
            },
            deleteProcess: function () {
                const vm = this;
                if (confirm("정말로 삭제 하시겠습니까? 삭제된 공정은 복원되지 않습니다.")) {
                    axios.delete('/api/setting/process', {
                        data: {
                            ids: vm.selectedProcessKeys
                        }
                    }).then((res) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = res.data.msg;
                        vm.getProcess();
                    }).catch((error) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = error;
                    })
                }
            },
        },
    };
</script>
