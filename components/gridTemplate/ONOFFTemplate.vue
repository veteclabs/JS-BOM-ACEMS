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
            if(this.cellData.value === true) {
                this.colorType= 'blue-badge blue';
                this.text = 'ON';
            }else if(this.cellData.value === false) {
                this.colorType= 'red-badge red';
                this.text = 'OFF';
            }else {
                this.text = this.cellData.value;
            }
        },
    };
</script>
