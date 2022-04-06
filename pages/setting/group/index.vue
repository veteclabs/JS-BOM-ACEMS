<template>
    <div id="settingGroup">
        <div class="wrapper animated fadeInRight">
            <div class="ibox">
                <div class="grid-title-header">
                    <p>그룹 관리</p>
                    <div class="grid-header">
                        <button id="deleteUser" class="button del-button"
                                :disabled="!selectedGroupKeys.length"
                                @click="deleteGroup"/>
                        <button id="createGroup" class="button add-button" @click="createGroup">
                            <img src="~assets/images/setting/icn_setting_add.svg" alt="등록"/>
                        </button>
                    </div>
                </div>
                <div id="gridBox">
                    <dx-data-grid
                            id="GroupListGrid"
                            :selected-row-keys="selectedGroupKeys"
                            @selection-changed="selectionGroupChanged"
                            :data-source="groupList"
                            :show-borders="false"
                            :onCellClick="updateGroup"
                            key-expr="id"
                            :column-min-width="100"
                    >
                        <dx-search-panel :visible="true" :highlight-case-sensitive="true"/>
                        <DxSelection mode="multiple"/>
                        <dx-column data-field="id" caption="No" alignment="center" :width="100"/>
                        <dx-column data-field="name" caption="그룹명" alignment="center" />
                        <dx-paging :enabled="true" :page-size="20"/>
                        <dx-pager :show-page-size-selector="true" :allowed-page-sizes="pageSizes"
                                  :show-info="true"/>
                        <template #blockGridTemplate="{ data: cellData }">
                            <blockGridTemplate :cell-data="cellData"/>
                        </template>
                    </dx-data-grid>
                </div>
                <createModal v-bind:propsdata="modalData" ref="groupModal" v-on:callSearch="getGroup"/>
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
    import createModal from '~/components/settingModal/createGroupModal.vue';
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
                groupList: [],
                pageSizes: [5, 10, 20, 50], // 페이지사이즈
                selectedGroupKeys: [],
                selectionGroupChanged: (data) => {
                    this.selectedGroupKeys = data.selectedRowKeys;
                },
            };
        },
        created() {
            this.getGroup();
        },
        methods: {
            getGroup() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/device/groups',
                }).then((res) => {
                    vm.groupList = res.data
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            // 신규 장비 등록
            createGroup() {
                this.$router.push(`/setting/group/create`);
            },
            updateGroup(e) {
                if (e.columnIndex !== 0) {
                    this.$router.push(`/setting/group/${e.data.id}`);
                }
            },
            deleteGroup: function () {
                const vm = this;
                if (confirm("정말로 삭제 하시겠습니까? 삭제된 공정은 복원되지 않습니다.")) {
                    axios.delete('/api/setting/tank', {
                        data: {
                            ids: vm.selectedGroupKeys
                        }
                    }).then((res) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = res.data.msg;
                        vm.getGroup();
                    }).catch((error) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = error;
                    })
                }
            },
        },
    };
</script>
