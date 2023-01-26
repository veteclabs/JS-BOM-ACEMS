<template>
    <div>
        {{date}} {{time}}
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
                date: '',
                time:'',
            };
        },
        mounted() {
            this.date  =this.cellData.data.eventDate;
            this.time  =this.cellData.data.occurrenceTime;
        },
    };
</script>
