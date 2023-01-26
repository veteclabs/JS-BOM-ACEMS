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
            if(this.cellData.value === 'trip') {
                this.colorType = 'bom-badge red-bg-badge';
                this.text = this.cellData.value
            }else if(this.cellData.value === 'warning') {
                this.colorType = 'bom-badge orange-bg-badge';
                this.text = this.cellData.value
            }
            else {
                this.colorType = 'red';
                this.text =this.cellData.value
            }
        },
    };
</script>
