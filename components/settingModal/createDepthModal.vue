

<template>
    <div>
        <b-modal v-model="propsdata.show" id="createDepthModal" title="부서 관리" hide-footer>
            <table class="bom-table">
                <caption>부서 정보를 입력합니다.</caption>
                <colgroup>
                    <col style="width:100px;"/>
                    <col style="width:auto;"/>
                </colgroup>
                <tbody>
                <tr>
                    <th>부서명</th>
                    <td>
                        <label class="input-100">
                            <input type="text" v-model="name" class="input-100"
                                   placeholder="부서명을 입력해주세요 "/>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('name') }}
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
            <div class="modal-footer">
                <button type="button" class="button cancel-button" @click="cancel">취소</button>
                <button type="button" class="button submit-button" @click="submit">등록</button>
            </div>
        </b-modal>
        <flashModal v-bind:propsdata="msgData"/>
    </div>
</template>

<script>
    import SimpleVueValidation from 'simple-vue-validator';
    import axios from 'axios';
    import flashModal from '~/components/flashmodal.vue';

    const {Validator} = SimpleVueValidation;

    export default {

        props: ['propsdata'],
        components: {
            axios,
            flashModal,
            SimpleVueValidation,
        },
        data() {
            return {
                msgData: { // 알람모달
                    msg: '',
                    show: false,
                    e: '',
                },
                state: 'new',
                id: '',
                name: '',
            };
        },
        validators: {
            name(value) {
                return Validator.value(value).required();
            },
        },
        methods: {
            submit() {
                const vm = this;
                const modal = this.$bvModal;
                const {state} = this;
                let url, method;

                if (state === 'new') {
                    url = `/api/setting/department`;
                    method = 'post';
                } else if (state === 'update') {
                    url = `/api/setting/department/${vm.id}`;
                    method = 'put';
                    if (!vm.id) {
                        vm.msgData.show = true;
                        vm.msgData.msg = '에러가 발생했습니다. 새로고침 후 다시 시도해주세요';
                        return;
                    }
                }

                const params = {
                    id: this.id,
                    name: this.name,
                };

                this.$validate()
                    .then((success) => {
                        if (success) {
                            axios({
                                method: method,
                                url: url,
                                data: params
                            }).then((res) => {
                                if (res.data.code === 1) {
                                    modal.hide('createDepthModal');
                                    vm.msgData.show = true;
                                    vm.msgData.msg = res.data.msg;
                                    vm.$emit('send-message', 1);
                                    vm.$emit('callSearch');
                                }
                            }).catch((error) => {
                                vm.msgData.show = true;
                                vm.msgData.msg = error;
                            });
                        }
                    });
            },
            cancel() {
                this.$bvModal.hide('createDepthModal');
            },
            reset() {
                this.name = '';
            },
            createdModal() {
                this.state = 'new';
                this.reset();
                this.validation.reset();
            },
            updateModal(e) {
                this.validation.reset();
                this.state = 'update';
                this.id = e.id;
                this.name = e.name;
            },
        },
    };
</script>
