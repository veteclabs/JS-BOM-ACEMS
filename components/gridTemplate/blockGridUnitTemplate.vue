<template>
    <div>
        <span class="bom-badge">{{ unitText }}</span>
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
                unitText: '',
            };
        },
        mounted() {
            if (this.cellData.column.dataField === 'usage') {
                this.unitText = this.cellData.value + 'kWh';
            } else {
                this.unitText = this.cellData.value;
            }
        },
    };
</script>
