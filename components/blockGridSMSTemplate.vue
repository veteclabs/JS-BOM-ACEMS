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
                smsType: {
                    1: {
                        text:'문자수신',
                        color:'green-badge green'
                    },
                    0 :{
                        text:'수신거부',
                        color:'red-badge red'
                    }
                },
            };
        },
        mounted() {
            if (this.cellData.column.dataField === 'is_alarm_sms') {
                if (this.smsType[this.cellData.value]) {
                    this.colorType = this.smsType[this.cellData.value].color;
                    this.text = this.smsType[this.cellData.value].text;
                }
            }
        },
    };
</script>
