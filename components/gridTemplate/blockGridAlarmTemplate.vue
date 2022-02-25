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
                this.colorType = 'blue-badge blue';
                this.text = 'ON'
            }else {
                this.text ='OFF'
            }
        },
    };
</script>
