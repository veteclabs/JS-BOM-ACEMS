<template>
    <div id="settingSubContent">
        <div class="wrapper animated fadeInRight">
            <div class="ibox">
                <div class="ibox-title ibox-noborder-title">
                    부서
                </div>
                <div class="grid-header">
                    <button id="deleteAlarmUser" class="button del-button"
                            :disabled="!selectedDepthKeys.length"
                            @click="deleteDepth"/>
                    <button id="createDepth" class="button add-button" @click="createDepartment">
                        <img src="~assets/images/setting/icn_setting_add.svg" alt="등록"/>
                    </button>
                </div>
                <div id="gridBox">
                    <DxDataGrid
                            id="depthListGrid"
                            :selected-row-keys="selectedDepthKeys"
                            @selection-changed="selectionDepthChanged"
                            :data-source="departmentList"
                            :show-borders="false"
                            :onCellClick="updateDepartment"
                            key-expr="id"
                            :column-min-width="100"
                    >
                        <DxSearchPanel :visible="true" :highlight-case-sensitive="true"/>
                        <DxSelection mode="multiple"/>
                        <DxColumn data-field="id" caption="No" :visible="false" alignment="center"/>
                        <DxColumn data-field="name" caption="부서명" cell-template="blockGridTemplate"/>
                        <DxPaging :enabled="true" :page-size="5"/>
                        <DxPager :show-page-size-selector="true" :allowed-page-sizes="pageSizes" :show-info="true"/>
                        <template #unitGridTemplate="{ data: cellData }">
                            <unitGridTemplate :cell-data="cellData"/>
                        </template>
                        <template #blockGridTemplate="{ data: cellData }">
                            <blockGridTemplate :cell-data="cellData"/>
                        </template>
                    </DxDataGrid>
                </div>
            </div>
            <div class="ibox">
                <div class="ibox-title ibox-noborder-title">
                    팀
                </div>
                <div class="grid-header">
                    <button id="deleteTeam" class="button del-button"
                            :disabled="!selectedTeamKeys.length"
                            @click="deleteTeam"/>
                    <button id="createTeam" class="button add-button" @click="createTeam">
                        <img src="~assets/images/setting/icn_setting_add.svg" alt="등록"/>
                    </button>
                </div>
                <DxDataGrid
                        id="TeamListGrid"
                        :selected-row-keys="selectedTeamKeys"
                        @selection-changed="selectionTeamChanged"
                        :data-source="teamList"
                        :show-borders="false"
                        :onCellClick="updateTeam"
                        key-expr="teamId"
                        :column-min-width="100"
                >
                    <DxSearchPanel :visible="true" :highlight-case-sensitive="true"/>
                    <DxSelection mode="multiple"/>
                    <DxColumn data-field="id" caption="No" :visible="false" alignment="center"/>
                    <DxColumn data-field="departmentId" :visible="false"/>
                    <DxColumn data-field="departmendName" caption="부서명" alignment="center"
                              cell-template="blockGridTemplate"/>
                    <DxColumn data-field="teamName" caption="이름" alignment="center"/>
                    <DxPaging :enabled="true" :page-size="5"/>
                    <DxPager :show-page-size-selector="true" :allowed-page-sizes="pageSizes" :show-info="true"/>
                    <template #blockGridTemplate="{ data: cellData }">
                        <blockGridTemplate :cell-data="cellData"/>
                    </template>
                </DxDataGrid>
            </div>
            <createTeamModal v-bind:propsdata="modalTeamData" v-on:callSearch="getTeam" ref="teamModal"/>
            <createDepthModal v-bind:propsdata="modalDepartmentData" v-on:callSearch="getDepartment" ref="departmentModal"/>
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
    import createDepthModal from '~/components/settingModal/createDepthModal.vue';
    import createTeamModal from '~/components/settingModal/createTeamModal.vue';
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
            createDepthModal,
            createTeamModal,
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
                modalDepartmentData: {
                    show: false,
                },
                modalTeamData: {
                    show: false,
                },
                departmentList: [],
                teamList: [],
                selectedDepthKeys: [], // 선택삭제 시 이용
                selectionDepthChanged: (data) => {
                    this.selectedDepthKeys = data.selectedRowKeys;
                },
                selectedTeamKeys: [], // 선택삭제 시 이용
                selectionTeamChanged: (data) => {
                    this.selectedTeamKeys = data.selectedRowKeys;
                },
                pageSizes: [5, 10, 20], // 페이지사이즈
            };
        },
        created() {
            this.id = this.$store.getters.ID;
        },
        mounted() {
            this.getDepartment();
            this.getTeam();
        },
        methods: {
            async getTeam() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/setting/teams',
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.teamList = res.data.value;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            async getDepartment() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/setting/departments',
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.departmentList = res.data.result;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            createDepartment() {
                this.$refs.departmentModal.createdModal();
                this.modalDepartmentData.show = true;
            },
            createTeam() {
                this.$refs.teamModal.createdModal();
                this.modalTeamData.show = true;
            },
            updateDepartment(e) {
                if (e.columnIndex !== 0) {
                    this.$refs.departmentModal.updateModal(e.data);
                    this.modalDepartmentData.show = true;
                }
            },
            updateTeam(e) {
                if (e.columnIndex !== 0) {
                    this.$refs.teamModal.updateModal(e.data);
                    this.modalTeamData.show = true;
                }
            },
            deleteDepth: function () {
                const vm = this;
                if (confirm("정말로 삭제 하시겠습니까? 삭제된 부서는 복원되지 않습니다.")) {
                    axios.delete('/api/setting/department', {
                        data: {
                            ids: vm.selectedDepthKeys
                        }
                    }).then((res) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = res.data.msg;
                        vm.getDepartment();
                    }).catch((error) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = error;
                    })
                }
            },
            deleteTeam: function () {
                const vm = this;
                if (confirm("정말로 삭제 하시겠습니까? 삭제된 부서는 복원되지 않습니다.")) {
                    axios.delete('/api/setting/team', {
                        data: {
                            ids: vm.selectedTeamKeys
                        }
                    }).then((res) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = res.data.msg;
                        vm.getTeam();
                    }).catch((error) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = error;
                    })
                }
            },
        },
    };
</script>
