<template>
    <div>
        <span :class='`${colorType}`'>{{ text }}</span>
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
            if(this.cellData.value === 'Alarm') {
                this.colorType = 'bom-badge red-badge red';
                this.text = this.cellData.value
            }else {
                this.colorType = 'red';
                this.text =this.cellData.value
            }
        },
    };
</script>
