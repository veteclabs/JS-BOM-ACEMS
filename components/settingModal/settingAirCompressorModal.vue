<template>
    <div>
        <b-modal v-model="propsdata.show" id="createmodal" title="Setting" size="lg" hide-footer hide-header>
            <div class="modal-header">
                <h5 class="font-20" style="margin:0;">
                    <img src="~assets/images/dashboard/icn_dashboard-modal_setting.png" alt="setting-icon" width="24"/>
                    Setting
                </h5>
                <button aria-label="Close" class="close" @click="cancel">
                    <img src="~assets/images/dashboard/icn_dashboard-modal_close.svg" alt="close" width="24"/>
                </button>
            </div>
            <div class="modal-body modal-overflow">
                <groupSetting ref="groupSetting"/>
            </div>
            <div class="modal-footer">
                <button type="button" class="button cancel-button" @click="cancel">취소</button>
                <button type="button" class="button submit-button" @click="submit">설정</button>
            </div>
        </b-modal>
    </div>
</template>

<script>
    import groupSetting from '~/components/settingModal/groupSetting.vue';

    export default {
        props: ['propsdata'],
        components: {
            groupSetting,
        },
        methods: {
            cancel() {
                this.$bvModal.hide('createmodal');
            },
            async getTP() {
                await this.$nextTick();
                this.$refs.groupSetting.getTP();

            },
            submit: function () {
                this.$refs.groupSetting.submit();
            },
        },
    };
</script>
