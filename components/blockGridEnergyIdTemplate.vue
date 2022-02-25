<template>
    <div>
        <span :class='`bom-badge ${colorType}`'>{{ text }}</span>
    </div>
</template>

<script>
    export default {
        props: {
            cellData: {
                type: Object,
                default: () => {
                }
            },
        },
        data() {
            return {
                colorType: '',
                text: '',
                energy: {
                    28: '전력'
                },
                energy_id: {
                    28: 'green-badge',
                },
            };
        },
        mounted() {
            if (this.cellData.column.dataField === 'energy_id') {
                if (this.energy_id[this.cellData.value]) {
                    this.colorType = this.energy_id[this.cellData.value];
                    this.text = this.energy[this.cellData.value];
                }
            }
        },
    };
</script>
