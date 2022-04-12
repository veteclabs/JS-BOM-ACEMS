<template>
    <div id="settingSubContent">
        <div class="wrapper animated fadeInRight">
            <div class="grid-header" style="padding:0 0 12px 0;">
                <button id="createEquipment" class="button submit-button" @click="submit">
                    저장
                </button>
            </div>
            <groupSetting ref="groupSetting"  v-bind:groupData="groupInfo"/>
        </div>
    </div>
</template>
<script>
    import groupSetting from '~/components/settingModal/groupSetting.vue';

    export default {
        fetch({store, redirect}) {
            if (!store.state.authUser) {
                return redirect('/');
            }
            return false;
        },
        layout: 'settingTemplate',
        components: {
            groupSetting
        },
        data() {
            return {
                id: '',
                groupInfo: {
                    name:'',
                    schedule : {
                        dayOfWeeks: [],
                        isActive: false,
                        min: 0,
                        max: 0,
                        startTime: '00:00:00',
                        stopTime: '00:00:00',
                    }
                },
            }
        },
        methods : {
            submit: function() {
                this.$refs.groupSetting.submit('new');
            }
        }
    };
</script>
