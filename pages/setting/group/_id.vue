<template>
    <div id="settingSubContent">
        <div class="wrapper animated fadeInRight">
            <div class="grid-header" style="padding:0 0 12px 0;">
                <button id="createEquipment" class="button submit-button" @click="submit">
                    저장
                </button>
            </div>
            <groupSetting ref="TPSetting"/>
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
        methods : {
            submit: function() {
                this.$refs.TPSetting.submit();
            }
        }
    };
</script>
