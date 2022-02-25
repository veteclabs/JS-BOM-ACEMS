<template>
    <div id="settingSubContent">
        <div class="wrapper animated fadeInRight">
            <div class="ibox">
                <div class="ibox-title ibox-noborder-title">
                    시설 관리
                </div>
                <div class="grid-header">
                    <button id="createLocation" class="button add-button" @click="createLocation">
                        <img src="~assets/images/setting/icn_setting_add.svg" alt="등록"/>
                    </button>
                </div>
                <div id="gridBox">
                    <DxDataGrid
                            id="locationListGrid"
                            :data-source="locationList"
                            :show-borders="false"
                            :onCellClick="updateLocation"
                            key-expr="id"
                    >
                        <DxSearchPanel :visible="true" :highlight-case-sensitive="true"/>
                        <DxColumn data-field="id" caption="id" alignment="center" width="60"/>
                        <DxColumn data-field="description" caption="시설명" alignment="center"
                                  cell-template="blockGridTemplate" />
                        <DxColumn data-field="departmentName" caption="부서" alignment="center"/>
                        <DxColumn data-field="substationName" caption="변전실" alignment="center"/>
                        <DxColumn data-field="panelboardName" caption="판넬"  alignment="center"/>
                        <DxColumn data-field="switchboardName" caption="스위치" alignment="center"/>
                        <DxColumn data-field="manufacturingProcessName"  caption="공정" alignment="center"/>
                        <DxColumn data-field="substationId" alignment="center" :visible="false"/>



                        <DxColumn data-field="panelBoardId" alignment="center" :visible="false"/>

                        <DxColumn data-field="switchBoardId" alignment="center" :visible="false"/>

                        <DxColumn data-field="departmentId" alignment="center" :visible="false"/>

                        <DxColumn data-field="manufacturingProcessId" alignment="center" :visible="false"/>

                        <DxPaging :enabled="true" :page-size="20"/>
                        <DxPager :show-page-size-selector="true" :allowed-page-sizes="pageSizes" :show-info="true"/>
                        <template #blockGridTemplate="{ data: cellData }">
                            <blockGridTemplate :cell-data="cellData"/>
                        </template>
                    </DxDataGrid>
                </div>
            </div>
            <createLocationModal v-bind:propsdata="modalData" v-on:callSearch="getLocation" ref="locationModal"/>
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
    import createLocationModal from '~/components/settingModal/createLocationModal.vue';
    import createSubLocationModal from '~/components/settingModal/createSubLocationModal.vue';
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
            createLocationModal,
            createSubLocationModal,
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
                locationList: [],
                pageSizes: [5, 10, 20], // 페이지사이즈
            };
        },
        created() {
            this.id = this.$store.getters.ID;
        },
        mounted() {
            this.getLocation();
        },
        methods: {
            async getLocation() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/setting/locations',
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.locationList = res.data.value;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            createLocation() {
                this.$refs.locationModal.createdModal();
                this.modalData.show = true;
            },
            updateLocation(e) {
                if (e.columnIndex !== 0) {
                    this.$refs.locationModal.updateModal(e.data);
                    this.modalData.show = true;
                }
            },
        },
    };
</script>
