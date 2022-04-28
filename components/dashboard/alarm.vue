<template>
    <div>
        <div class="ibox-title">
            알람 정보
        </div>
        <div class="ibox-content">
            <DxDataGrid :data-source="alarmList"
                        :show-borders="false"
                        key-expr="id"
                        :allow-column-resizing="true"
                        :allowColumnReordering="true"
                        :column-min-width="100"
                        :column-auto-width="true"
                        @exporting="onExporting">
                <DxScrolling mode="standard"/>
                <DxSearchPanel :visible="true" :highlight-case-sensitive="true"/>
                <DxColumn data-field="id" caption="No" alignment="center"/>
                <DxColumn data-field="type" caption="Type" alignment="center" cell-template="blockGridAlarmTemplate"/>
                <DxColumn data-field="message" caption="message"/>
                <DxColumn data-field="tempValue" caption="Data" alignment="center" cell-template="dataGridTemplate"/>
                <DxColumn data-field="prssValue" caption="압력" :visible="false"/>
                <DxColumn data-field="kwhValue" caption="전기" :visible="false"/>
                <DxColumn data-field="eventDate" caption="Date" alignment="center" cell-template="dateTimeTemplate"/>
                <DxColumn data-field="checkIn"  cell-template="recoverTimeTemplate"/>
                <DxPaging :enabled="true" :page-size="20"/>
                <DxPager :show-page-size-selector="true" :allowed-page-sizes="pageSizes" :show-info="true"/>
                <DxExport :enabled="true" :allow-export-selected-data="true" file-name="alarmList"/>
                <template #blockGridAlarmTemplate="{ data: cellData }">
                    <blockGridAlarmTemplate :cell-data="cellData"/>
                </template>
                <template #dateTimeTemplate="{ data: cellData }">
                    <dateTimeTemplate :cell-data="cellData"/>
                </template>
                <template #dataGridTemplate="{ data: cellData }">
                    <dataGridTemplate :cell-data="cellData"/>
                </template>
                <template #recoverTimeTemplate="{ data: cellData }">
                    <recoverTimeTemplate :cell-data="cellData"/>
                </template>
            </DxDataGrid>
        </div>
    </div>
</template>

<script>
    import axios from 'axios';
    import dataGridTemplate from '~/components/gridTemplate/dataGridTemplate.vue';
    import blockGridAlarmTemplate from '~/components/gridTemplate/blockGridAlarmTemplate.vue';
    import dateTimeTemplate from '~/components/gridTemplate/dateTimeTemplate.vue';
    import recoverTimeTemplate from '~/components/gridTemplate/recoverTimeTemplate.vue';
    import {
        DxDataGrid,
        DxColumn,
        DxPaging,
        DxPager,
        DxSearchPanel,
        DxScrolling,
        DxExport,
    } from 'devextreme-vue/data-grid';
    import {Workbook} from 'exceljs';
    import {saveAs} from 'file-saver-es';
    import {exportDataGrid} from 'devextreme/excel_exporter';

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
            dataGridTemplate,
            blockGridAlarmTemplate,
            dateTimeTemplate,
            recoverTimeTemplate,
            DxDataGrid,
            DxColumn,
            DxPaging,
            DxPager,
            DxSearchPanel,
            DxScrolling,
            DxExport,
            Workbook,
            saveAs,
            exportDataGrid
        },
        data() {
            return {
                alarmList: '',
                pageSizes: [10, 20, 50, 100],
                interval: '',
                intervalTime: 30 * 1000,
            };
        },
        created() {
            this.getAlarm();
            this.resetInterval();
        },
        beforeDestroy() {
            this.removeInterval();
        },
        methods: {
            async getAlarm() {
                const id = this.$route.params.id;
                const vm = this;
                axios({
                    method: 'get',
                    url: `/api/alarms/${id}`
                }).then((res) => {
                    vm.alarmList = res.data
                })
            },
            onExporting(e) {
                e.component.beginUpdate();
                e.component.columnOption('tempValue', 'caption','온도');
                e.component.columnOption('prssValue', 'visible', true);
                e.component.columnOption('kwhValue', 'visible', true);

                const workbook = new Workbook();
                const worksheet = workbook.addWorksheet('Sheet');
                worksheet.columns = [
                    { width: 'auto' }, { width: 'auto' }, { width: 'auto' }, { width: 21 }, { width: 21 },
                    { width: 21 }, { width: 21 }, { width: 21 }
                ];
                exportDataGrid({
                    component: e.component,
                    worksheet,
                    customizeCell: ({gridCell, excelCell}) => {
                        if (gridCell.rowType === 'data') {
                            if (gridCell.column.dataField === 'eventDate') {
                                console.log(excelCell.column);
                                excelCell.value = `${gridCell.data.eventDate} ${gridCell.data.occurrenceTime}`;
                            }
                            if (gridCell.column.dataField === 'checkIn') {
                                let recoverDate = gridCell.data.recoverDate === null ? '' : gridCell.data.recoverDate;
                                let recoverTime = gridCell.data.recoverTime === null ? '' : gridCell.data.recoverTime;
                                excelCell.value = `${recoverDate} ${recoverTime}`;
                            }
                        }
                    },
                }).then(() => {
                    workbook.xlsx.writeBuffer().then((buffer) => {
                        saveAs(new Blob([buffer], {type: 'application/octet-stream'}), 'facilitiesHistory.xlsx');
                    });
                }).then(function() {
                    e.component.columnOption('tempValue', 'caption','data');
                    e.component.columnOption('prssValue', 'visible', false);
                    e.component.columnOption('kwhValue', 'visible', false);
                    e.component.endUpdate();
                });
                e.cancel = true;
            },
            resetInterval() {
                const vm = this;
                clearInterval(this.interval);
                vm.interval = null;
                vm.interval = setInterval(() => {
                    vm.getAlarm();
                }, vm.intervalTime);
            },
            removeInterval() {
                const vm = this;
                clearInterval(vm.interval);
            },
        },

    };
</script>
