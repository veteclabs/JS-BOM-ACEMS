<template>
    <div>
        <ul class="grid-data">
            <li>온도 <strong>{{temp}}℃ <span>|</span></strong></li>
            <li>압력 <strong>{{bar}} bar <span>|</span></strong></li>
            <li>전력 <strong>{{kWh}} kW </strong></li>
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
            console.log(this.cellData);
            this.temp  =this.cellData.data.temp;
            this.bar  =this.cellData.data.temp;
            this.kWh  =this.cellData.data.temp;
        },
    };
</script>
