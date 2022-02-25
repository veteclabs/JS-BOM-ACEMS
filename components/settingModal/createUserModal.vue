<template>
    <div>
        <b-modal v-model="propsdata.show" id="createUserModal" title="구성원 관리" hide-footer>
            <table class="bom-table">
                <caption>구성원 정보를 입력합니다.</caption>
                <colgroup>
                    <col style="width:100px;"/>
                    <col style="width:auto;"/>
                </colgroup>
                <tbody>
                <tr>
                    <th>이름</th>
                    <td>
                        <label class="input-100">
                            <input type="text" v-model="name" class="input-100"
                                   placeholder="이름을 입력해주세요 "/>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('name') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>email</th>
                    <td>
                        <label class="input-100">
                            <input type="text" v-model="email" class="input-100"
                                   placeholder="ex)test@test.com"/>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('email') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>연락처</th>
                    <td>
                        <label class="input-100">
                            <input type="text" v-model="phone" class="input-100"
                                   placeholder="숫자만 입력해주세요."/>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('phone') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>부서</th>
                    <td>
                        <label class="input-100">
                            <select type="text" v-model="departmentId" class="input-100" @change="getTeam">
                                <option v-for="department in departmentList" :value="department.id">
                                    {{department.name}}
                                </option>
                            </select>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('teamId') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>팀</th>
                    <td>
                        <label class="input-100">
                            <select type="text" v-model="teamId" class="input-100">
                                <option v-for="team in teamList" :value="team.teamId">
                                    {{team.teamName}}
                                </option>
                            </select>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('teamId') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>문자수신여부</th>
                    <td>
                        <label class="switch-box">
                            <input type="checkbox" v-model="isAlarmSMS" :true-value="1" :false-value="0">
                            <span class="slider round"></span>
                        </label>
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
                phone: '',
                email: '',
                departmentId: '',
                departmentList: [],
                teamId: '',
                teamList: [],
                isAlarmSMS: '',
            };
        },
        validators: {
            phone(value) {
                return Validator.value(value).required()
            },
            email(value) {
                let state = this.$vnode.componentInstance.state;
                let id = this.$vnode.componentInstance.id;
                return Validator.value(value).email().required().custom(() => {
                    if (!Validator.isEmpty(value)) {
                        let params;
                        if(state ==='update') {
                            params = {
                                userId:id,
                                email: value
                            }
                        }else {
                            params = {email: value}
                        }
                        return axios.post('/api/setting/user/email/check', params).then((res) => {
                            if (res.data.checkIn === false) {
                                return '이미 등록된 이메일입니다.';
                            }
                            return '';
                        });
                    }
                    return '';
                });
            },
            name(value) {
                return Validator.value(value).required();
            },
            departmentId(value) {
                return Validator.value(value).required();
            },
            teamId(value) {
                return Validator.value(value).required();
            },
        },
        mounted() {
            this.getDepartment();
            this.getTeam();
        },
        methods: {
            async getDepartment() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/setting/departments'
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.departmentList = res.data.result;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            async getTeam() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/setting/teams',
                    params:{
                        departmentId: vm.departmentId
                    },
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.teamList = res.data.value;
                    }
                }).catch((error) => {
                    vm.msgData.show = true;
                    vm.msgData.msg = error;
                });
            },
            submit() {
                const vm = this;
                const modal = this.$bvModal;
                const {state} = this;
                let url, method;

                if (state === 'new') {
                    url = `/api/setting/user`;
                    method = 'post';
                } else if (state === 'update') {
                    url = `/api/setting/user`;
                    method = 'put';
                    if (!vm.id) {
                        vm.msgData.show = true;
                        vm.msgData.msg = '에러가 발생했습니다. 새로고침 후 다시 시도해주세요';
                        return;
                    }
                }
                const params = {
                    userId: this.id,
                    userName: this.name,
                    email: this.email,
                    phone: this.phone,
                    departmentId: this.departmentId,
                    teamId: this.teamId,
                    isAlarm: this.isAlarmSMS,
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
                                    // 등록완료시 모달 닫고 초기화 안내메시지 일여주기
                                    modal.hide('createUserModal');
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
                this.$bvModal.hide('createUserModal');
            },
            reset() {
                this.id = '';
                this.name = '';
                this.phone = '';
                this.email = '';
                this.departmentId ='';
                this.teamId = '';
                this.isAlarmSMS = 0;
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
                this.name = e.username;
                this.phone = e.phone;
                this.email = e.email;
                this.teamId = e.teamId;
                this.departmentId = e.departmentId;
                this.isAlarmSMS = e.is_alarm;
                this.getTeam();
            },
        },
    };
</script>
