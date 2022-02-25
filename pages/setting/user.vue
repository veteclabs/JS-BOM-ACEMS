<template>
    <div id="settingSubContent">
        <div class="wrapper animated fadeInRight">
            <div class="ibox">
                <div class="ibox-title ibox-noborder-title">
                    구성원
                </div>
                <div class="grid-header">
                    <button id="deleteUser" class="button del-button"
                            :disabled="!selectedUserKeys.length"
                            @click="deleteUser"/>
                    <button id="createUser" class="button add-button" @click="createUser">
                        <img src="~assets/images/setting/icn_setting_add.svg" alt="등록"/>
                    </button>
                </div>
                <DxDataGrid
                        id="UserListGrid"
                        :selected-row-keys="selectedUserKeys"
                        @selection-changed="selectionUserChanged"
                        :data-source="userList"
                        :show-borders="false"
                        :onCellClick="updateUser"
                        key-expr="id"
                        :column-min-width="100"
                >
                    <DxSearchPanel :visible="true" :highlight-case-sensitive="true"/>
                    <DxSelection mode="multiple"/>
                    <DxColumn data-field="id" caption="No" :visible="false" alignment="center"/>
                    <DxColumn data-field="username" caption="이름" alignment="center"/>
                    <DxColumn data-field="email" caption="email"/>
                    <DxColumn data-field="phone" caption="연락처"/>
                    <DxColumn data-field="departmentId" :visible="false"/>
                    <DxColumn data-field="departmentName" caption="부서명"/>
                    <DxColumn data-field="teamId" :visible="false"/>
                    <DxColumn data-field="teamName" caption="팀명"/>
                    <DxColumn data-field="is_alarm" alignment="center" caption="알람수신여부"
                              cell-template="blockGridAlarmTemplate"/>
                    <dx-column caption="비밀번호초기화" data-field="id" alignment="center" cell-template="passwordTemplate"/>
                    <DxPaging :enabled="true" :page-size="5"/>
                    <DxPager :show-page-size-selector="true" :allowed-page-sizes="pageSizes" :show-info="true"/>
                    <template #blockGridTemplate="{ data: cellData }">
                        <blockGridTemplate :cell-data="cellData"/>
                    </template>
                    <template #blockGridAlarmTemplate="{ data: cellData }">
                        <blockGridAlarmTemplate :cell-data="cellData"/>
                    </template>
                    <template #passwordTemplate="{ data: cellData }">
                        <passwordTemplate :cell-data="cellData"/>
                    </template>
                </DxDataGrid>
            </div>
            <createUserModal v-bind:propsdata="modalUserData" v-on:callSearch="getUser" ref="userModal"/>
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
    import createUserModal from '~/components/settingModal/createUserModal.vue';
    import flashModal from '~/components/flashmodal.vue';
    import blockGridTemplate from '~/components/gridTemplate/blockGridTemplate.vue';
    import blockGridAlarmTemplate from '~/components/gridTemplate/blockGridAlarmTemplate.vue';
    import passwordTemplate from '~/components/gridTemplate/passwordTemplate.vue';

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
            createUserModal,
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
            blockGridTemplate,
            passwordTemplate
        },
        data() {
            return {
                id: '',
                teamId:'',
                msgData: {
                    msg: '',
                    show: false,
                    e: '',
                },
                modalUserData: {
                    show: false,
                },
                userList: [],
                selectedUserKeys: [], // 선택삭제 시 이용
                selectionUserChanged: (data) => {
                    this.selectedUserKeys = data.selectedRowKeys;
                },
                pageSizes: [5, 10, 20], // 페이지사이즈
            };
        },
        created() {
            this.teamId = this.$store.getters.User.teamId;
        },
        mounted() {
            this.getUser();
        },
        methods: {
            async getUser() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/setting/users',
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.userList = res.data.result;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            createUser() {
                this.$refs.userModal.createdModal();
                this.modalUserData.show = true;
            },
            updateUser(e) {
                if (e.columnIndex !== 0 &&  e.column.cellTemplate !== 'passwordTemplate') {
                    this.$refs.userModal.updateModal(e.data);
                    this.modalUserData.show = true;
                }
            },
            deleteUser: function () {
                const vm = this;
                if (confirm("정말로 삭제 하시겠습니까? 삭제된 회원은 복원되지 않습니다.")) {
                    axios.delete('/api/setting/user', {
                        data: {
                            selectUser: vm.selectedUserKeys
                        }
                    }).then((res) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = res.data.msg;
                        vm.getUser();
                    }).catch((error) => {
                        vm.msgData.show = true;
                        vm.msgData.msg = error;
                    })
                }
            },
        },
    };
</script>
