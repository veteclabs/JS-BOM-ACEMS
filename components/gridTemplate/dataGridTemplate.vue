<template>
    <div>
        <ul class="grid-data">
            <li>온도 <strong>{{temp | numberFormat(2)}}℃ <span>|</span></strong></li>
            <li>압력 <strong>{{bar | numberFormat(2)}} bar <span>|</span></strong></li>
            <li>전력 <strong>{{kWh | numberFormat(2)}} kW </strong></li>
        </ul>
    </div>
</template>

<script>
    export default {
        props: {
            cellData: {
                type: Object,
                default: () => {}
            },
        },
        data() {
            return {
                temp: '',
                bar:'',
                kWh:'',
            };
        },
        mounted() {
            this.temp  =this.cellData.data.tempValue;
            this.bar  =this.cellData.data.prssValue;
            this.kWh  =this.cellData.data.kwhValue;
        },
        filters: {
            numberFormat: (value, numFix) => {
                value = parseFloat(value);
                if (!value) return '0';
                return value.toLocaleString('ko-KR', {maximumFractionDigits: numFix});
            },
        }
    };
</script>
