<template>
    <div>
        <span v-if="check">
            <img src="~assets/images/dashboard/icn_alarm_recover.svg" alt="recover"/>
            {{ cellData.dateTime }}
        </span>
        <span v-else>
            <img src="~assets/images/dashboard/icn_alarm_not-yet.svg" alt="not-yet"/>
        </span>
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
                check: '',
                dateTime:'',
            };
        },
        mounted() {
            this.check = this.cellData.data.checkIn === true;
            this.dateTime = `${this.cellData.data.recoverDate} ${this.cellData.data.recoverTime}`;
        },
    };
</script>
