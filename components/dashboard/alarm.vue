<template>
    <div id="event">
        <div class="wrapper animated fadeInRight">
            <div class="row">
                <div class="col-lg-12">
                    <div class="ibox">
                        <dx-data-grid id="gridBox" :data-source="alarmList" :show-borders="false"
                                      :column-min-width="100">
                            <dx-search-panel :visible="true" :highlight-case-sensitive="true"/>
                            <dx-column data-field="date" caption="날짜" alignment="center" :width="150"
                                       data-type="date" format="yyyy-MM-dd"/>
                            <dx-column data-field="time" caption="시간" alignment="center" :width="150"/>
                            <dx-column data-field="alarm_type" caption="알람타입" :width="150" alignment="center"
                                       cell-template="blockGridTemplate"/>
                            <dx-column data-field="description" caption="알람내용" alignment="left"/>
                            <dx-export :enabled="true" :allow-export-selected-data="true" file-name="alarmList"/>
                            <dx-paging :enabled="true" :page-size="20"/>
                            <dx-pager :show-page-size-selector="true" :allowed-page-sizes="pageSizes"
                                      :show-info="true"/>
                            <template #blockGridTemplate="{ data: cellData }">
                                <blockGridTemplate :cell-data="cellData"/>
                            </template>
                            <template #blockGridUnitTemplate="{ data: cellData }">
                                <blockGridUnitTemplate :cell-data="cellData"/>
                            </template>
                        </dx-data-grid>
                    </div>
                </div>
            </div>
        </div>
        <flashModal v-bind:propsdata="msgData"/>
    </div>
</template>

<script>
    import {
        DxDataGrid,
        DxColumn,
        DxPaging,
        DxEditing,
        DxSelection,
        DxLookup,
        DxPager,
        DxSearchPanel,
        DxExport,
    } from 'devextreme-vue/data-grid';
    import axios from 'axios';
    import flashModal from '~/components/flashmodal.vue';
    import blockGridTemplate from '~/components/gridTemplate/blockGridTemplate.vue';


    export default {
        fetch({store, redirect}) {
            if (!store.state.authUser) {
                return redirect('/');
            }
            return false;
        },
        layout: 'template',
        components: {
            axios,
            flashModal,
            DxDataGrid,
            DxColumn,
            DxPaging,
            DxEditing,
            DxSelection,
            DxLookup,
            DxPager,
            DxSearchPanel,
            DxExport,
            blockGridTemplate,
            blockGridUnitTemplate
        },
        data() {
            return {
                id: '',
                msgData: { // 알람모달
                    msg: '',
                    show: false,
                    e: '',
                },
                alarmList: '',
                pageSizes: [10, 20, 50, 100],
            };
        },
        created() {
            this.getAlarmList();
        },
        methods: {
            async getAlarmList() {
                const vm = this;
                axios.get('/api/alarms',{
                    params: {
                        today:false,
                    },
                    timeout: vm.intervalTime,
                }).then((res) => {
                        if (res.data.code === 1) {
                            vm.alarmList = res.data.value;
                        }
                    }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
        },
    };
</script>
