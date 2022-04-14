<template>
    <div>
        <ul class="date-ul" style="justify-content: center">
            <li v-for="date in cellData.value" :key="id">
                <label>
                    <div class="active">{{ date.name }}</div>
                </label>

            </li>
        </ul>
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
    };
</script>
