<template>
    <div>
        <span :class='`bom-badge ${colorType}`'>{{ cellData.text }}</span>
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
            gridEnergy: {
                default: () => {
                }
            }
        },
        data() {
            return {
                colorType: '',
                dataFieldTypeColor: {
                    Action: 'red-badge',
                    COP: 'green-badge'
                },
                alarm_type: {
                    'UPS': 'red-badge',
                    '최대수요': 'orange-badge',
                    '목표전력': 'green-badge'
                },
                energy: {
                    "전력": 'green-badge',
                },
                energy_id: {
                    28: 'green-badge',
                },
                energyIdColor: {
                    18: 'red-badge',
                    28: 'green-badge',
                    30: 'skyblue-badge',
                }
            };
        },
        mounted() {
            if (typeof this.dataFieldTypeColor[this.cellData.column.dataField] !== 'undefined') {
                this.colorType = this.dataFieldTypeColor[this.cellData.column.dataField];
            } else {
                if (this.cellData.column.dataField === 'alarm_type') {
                    if (this.alarm_type[this.cellData.value]) {
                        this.colorType = this.alarm_type[this.cellData.value];
                    } else {
                        this.colorType = ''
                    }
                }

                if(this.gridEnergy) {
                    if (this.cellData.column.dataField === 'Usage') {
                        this.colorType = this.energyIdColor[this.gridEnergy];
                        if(this.cellData.text === null) {
                            this.cellData.text = 0;
                        }
                    }
                }

                if (this.cellData.column.dataField === 'energy_id') {
                    if (this.energy[this.cellData.value]) {
                        this.colorType = this.energy[this.cellData.value];
                    } else {
                        this.colorType = ''
                    }
                }

            }
        },
    };
</script>
