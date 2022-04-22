<template>
    <div>
        <ul class="date-ul" style="justify-content: center">
            <li v-for="date in cellData.value" :key="date.id">
                <label>
                    <div class="active" style="width:30px; height:30px; line-height:1.6; font-size:16px;">
                        {{ date.name }}
                    </div>
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
