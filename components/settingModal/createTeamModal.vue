<template>
    <div>
        <b-modal v-model="propsdata.show" id="createTeamModal" title="팀 관리" hide-footer>
            <table class="bom-table">
                <caption>팀 정보를 입력합니다.</caption>
                <colgroup>
                    <col style="width:100px;"/>
                    <col style="width:auto;"/>
                </colgroup>
                <tbody>
                <tr>
                    <th>부서명</th>
                    <td>
                        <label class="input-100">
                            <select v-model="departmentId" class="input-100">
                                <option v-for="list in departmentList" v-bind:value="list.id"
                                        :key="list.id">
                                    {{list.name}}
                                </option>
                            </select>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('departmentId') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>팀명</th>
                    <td>
                        <label class="input-100">
                            <input type="text" v-model="name" class="input-100"
                                   placeholder="팀명을 입력해주세요 "/>
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
                departmentList: [],
                departmentId: '',
                departmentName: '',
            };
        },
        validators: {
            departmentId(value) {
                return Validator.value(value).required();
            },
            name(value) {
                return Validator.value(value).required();
            },
        },
        mounted() {
            this.getDepartment();
        },
        methods: {
            async getDepartment() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/setting/departments',
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.departmentList = res.data.result;
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
                    url = `/api/setting/team`;
                    method = 'post';
                } else if (state === 'update') {
                    url = `/api/setting/team/${vm.id}`;
                    method = 'put';
                    if (!vm.id) {
                        vm.msgData.show = true;
                        vm.msgData.msg = '에러가 발생했습니다. 새로고침 후 다시 시도해주세요';
                        return;
                    }
                }

                const params = {
                    teamName: this.name,
                    departmentId: this.departmentId
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
                                    modal.hide('createTeamModal');
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
                this.$bvModal.hide('createTeamModal');
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
                this.id = e.teamId;
                this.name = e.teamName;
                this.departmentId = e.departmentId;
                this.departmentName = e.departmentName;
            },
        },
    };
</script>
