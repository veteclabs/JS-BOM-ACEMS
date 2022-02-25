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
            };
        },
        mounted() {
            if(this.cellData.value === 1) {
                this.colorType= 'red-badge red'
            }
            this.text = this.cellData.value;
        },
    };
</script>
