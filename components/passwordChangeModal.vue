<template>
    <div>
        <b-modal v-model="propsdata.show" id="passwordModal" title="비밀번호변경" hide-footer>
            <table class="bom-table">
                <caption>비밀번호 변경 테이블</caption>
                <colgroup>
                    <col style="width:150px;"/>
                    <col style="width:auto;"/>
                </colgroup>
                <tbody>
                <tr>
                    <td>현재 비밀번호</td>
                    <td>
                        <label class="input-100">
                            <input type="password" class="input-100" v-model="beforePW" placeholder="현재비밀번호를 입력해주세요."/>
                        </label>
                        <div class="err-message red" v-if="validation !== undefined">
                            {{ validation.firstError('beforePW') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>변경할 비밀번호</td>
                    <td>
                        <label class="input-100">
                            <input type="password" class="input-100" v-model="newPW"
                                   placeholder="변경할 비밀번호를 입력해주세요."/>
                        </label>
                        <div class="err-message red" v-if="validation !== undefined">
                            {{ validation.firstError('newPW') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>변경할 비밀번호 확인</td>
                    <td>
                        <label class="input-100">
                            <input type="password" class="input-100" v-model="newPWConfirm"
                                   placeholder="변경할 비밀번호를 한번 더 입력해주세요."/>
                        </label>
                        <div class="err-message red" v-if="validation !== undefined">
                            {{ validation.firstError('newPWConfirm') }}
                        </div>
                    </td>
                </tr>
                </tbody>
            </table>
            <div class="modal-footer">
                <button type="button" class="button cancel-button" @click="cancel">취소</button>
                <button type="button" class="button submit-button" @click="submit">확인</button>
            </div>
        </b-modal>
        <flashModal v-bind:propsdata="msgData"/>
    </div>
</template>

<script>
    import moment from 'moment'
    import flashModal from '~/components/flashmodal.vue'
    import SimpleVueValidation from 'simple-vue-validator';
    import axios from 'axios'

    const Validator = SimpleVueValidation.Validator;

    export default {
        props: ['propsdata'],
        components: {
            moment, axios, flashModal, SimpleVueValidation
        },
        data() {
            return {
                msgData: {
                    msg: '',
                    show: false
                },
                email: '',
                beforePW: '',
                newPW: '',
                newPWConfirm: '',
            }
        },
        validators: {
            beforePW: function (value) {
                const vm = this;
                return Validator.value(value).required().custom(function () {
                    if (!Validator.isEmpty(value)) {
                        return axios.post('/api/st/us/chck', {
                            pw: value,
                        }).then((res) => {
                            if (res.data.data !== true) {
                                return '현재 비밀번호와 동일하지 않습니다.';
                            }
                        })
                    }
                })
            },
            newPW: function (value) {
                return Validator.value(value).required().minLength(4);
            },
            'newPWConfirm, newPW': function (newPWConfirm, newPW) {
                if (this.submitted || this.validation.isTouched('newPWConfirm')) {
                    return Validator.value(newPWConfirm).required().match(newPW);
                }
            }
        },
        methods: {
            submit: function () {
                const vm = this;
                const modal = this.$bvModal;
                this.$validate()
                    .then(function (success) {
                        if (success) {
                            //form 입력완료
                            axios.post('/api/st/us/stpw',{
                                    pw: vm.newPW
                            }).then((res) => {
                                if (res.data.code === 1) {
                                    //등록완료시 모달 닫고 초기화 안내메시지 일여주기
                                    modal.hide('passwordModal');
                                    vm.msgData.show = true;
                                    vm.msgData.msg = res.data.message
                                }
                            }).catch((error) => {
                                vm.msgData.show = true;
                                vm.msgData.msg = error;
                            })
                        }
                    });

            },
            cancel: function () {
                this.$bvModal.hide('passwordModal');
            },
            createdModal: function (email) {
                this.beforePW = '';
                this.newPW = '';
                this.newPWConfirm = '';
                this.email = email;
            },
        }
    }
</script>
