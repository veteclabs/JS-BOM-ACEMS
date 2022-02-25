<template>
    <div>
        <span :class='`bom-badge`'>{{ cellData.text }}</span>
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
    };
</script>
