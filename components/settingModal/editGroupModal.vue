<template>
    <div>
        <b-modal v-model="propsdata.show" id="createmodal" title="장비 관리" hide-footer>
            <div class="modal-body modal-overflow">
                <groupSetting ref="groupSetting" v-bind:groupData="groupInfo" v-on:modalClose="cancel"/>
            </div>
            <div class="modal-footer">
                <button type="button" class="button cancel-button" @click="cancel">취소</button>
                <button type="button" class="button submit-button" @click="submit">설정</button>
            </div>
        </b-modal>
        <flashModal v-bind:propsdata="msgData"/>
    </div>
</template>
<script>
    import axios from 'axios';
    import VueSimpleRangeSlider from 'vue-simple-range-slider';
    import draggable from 'vuedraggable';
    import flashModal from '~/components/flashmodal.vue';
    import groupSetting from '~/components/settingModal/groupSetting.vue';

    export default {
        props: ['propsdata'],
        layout: 'settingTemplate',
        components: {
            axios,
            VueSimpleRangeSlider,
            draggable,
            flashModal,
            groupSetting
        },
        data() {
            return {
                id: '',
                msgData: {
                    msg: '',
                    show: false,
                    e: '',
                },
                groupInfo: {
                    name: '',
                    schedule: {
                        dayOfWeeks: [],
                        isActive: false,
                        min: 0,
                        max: 0,
                        startTime: '00:00:00',
                        stopTime: '00:00:00',
                    }
                },
            };
        },
        methods: {
            getGroup: function () {
                const vm = this;
                axios({
                    method: 'get',
                    url: `/api/group/${vm.id}`,
                }).then((res) => {
                    vm.groupInfo = res.data
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error.response.data.error;
                });
            },
            cancel() {
                this.$bvModal.hide('createmodal');
            },
            submit() {
                this.$refs.groupSetting.submit('update');
            },
            updateModal(id) {
                this.state = 'update';
                this.id = id;
                this.getGroup();
            },
        },
    };
</script>
