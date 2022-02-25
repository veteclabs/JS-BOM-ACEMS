<template>
    <div>
        <b-modal v-model="propsdata.show" id="createmodal" title="계측기 관리" hide-footer>
            <table class="bom-table">
                <caption>
                    계측기관리 정보를 입력합니다.
                </caption>
                <colgroup>
                    <col style="width: 100px"/>
                    <col style="width: auto"/>
                </colgroup>
                <tbody>
                <tr>
                    <td>에너지</td>
                    <td>
                        <label class="radio-box" v-for="item in energyList" :key="item.id">
                            <input type="radio" v-model="energy" v-bind:value="item.id"/>
                            <div class="radio-circle">
                                <div class="inner-circle"/>
                            </div>
                            <div class="radio-label">{{ item.name }}</div>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('energy') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>계측기장비명</td>
                    <td>
                        <label class="input-100">
                            <select v-model="equipmentName" class="input-100">
                                <option v-for="item in equipmentNameList" v-bind:value="item.name" :key="item.name">
                                    {{ item.name }}
                                </option>
                            </select>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('equipmentName') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <td>계측기명</td>
                    <td>
                        <label class="input-100">
                            <input type="text" v-model="name" placeholder="계측기 이름을 입력해주세요" class="input-100"/>
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
        <flashModal v-bind:propsdata="MsgData"/>
    </div>
</template>

<script>
    import axios from 'axios';
    import SimpleVueValidation from 'simple-vue-validator';
    import flashModal from '~/components/flashmodal.vue';

    const {Validator} = SimpleVueValidation;

    SimpleVueValidation.extendTemplates({
        required: '필수 입력 항목입니다.',
        length: '길이가 {0} 이어야 합니다.',
        minLength: '{0} 글자 이상이어야 합니다.',
        maxLength: '{0} 글자 이하여야 합니다.',
        digit: '숫자만 입력해주세요.',
    });

    export default {
        props: ['propsdata'],
        components: {
            axios,
            flashModal,
            SimpleVueValidation,
        },
        data() {
            return {
                MsgData: {
                    // 알람모달
                    msg: '',
                    show: false,
                    e: '',
                },
                state: 'new',
                id: '', //계측기 아이디
                location: '',
                locationList: [],
                subLocation: '',
                subLocationTempList: [],
                subLocationList: [],
                energy: '',
                energyList: [],
                equipmentName: '',
                equipmentNameList: [],
                energyPurpose: '',
                energyPurposeList: [],
                name: '',
            };
        },
        validators: {
            energy(value) {
                return Validator.value(value).required();
            },
            equipmentName(value) {
                return Validator.value(value).required();
            },
            name(value) {
                return Validator.value(value).required();
            },
        },
        mounted() {
            this.getEquipmentFilter();
        },
        methods: {
            async getEquipmentFilter() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/common/getFilter',
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.energyList = res.data.energyValue;
                        vm.equipmentNameList = res.data.equipmentValue;

                        if (vm.energyList.length !== 0) {
                            vm.energy = vm.energyList[0].id;
                        }

                        if (vm.equipmentNameList.length !== 0) {
                            vm.equipmentName = vm.equipmentNameList[0].name;
                        }
                    }
                }).catch((error) => {
                    vm.MsgData.show = true;
                    vm.MsgData.msg = error;
                });
            },
            submit() {
                const vm = this;
                const modal = this.$bvModal;
                const {state} = this;
                let url;
                const params = {
                    id: vm.id,
                    energyId: vm.energy,
                    equipmentName: vm.equipmentName,
                    name: vm.name,
                };
                if (state === 'new') {
                    url = '/api/setting/equipment/create';
                } else if (state === 'update') {
                    url = '/api/setting/equipment/update';
                    if (!vm.id) {
                        vm.MsgData.show = true;
                        vm.MsgData.msg = '오류가 발생했습니다. 새로고침 후 다시 시도해주세요';
                        return;
                    }
                }
                this.$validate()
                    .then((success) => {
                        if (success) {
                            axios
                                .post(url, params)
                                .then((res) => {
                                    if (res.data.code === 1) {
                                        // 등록완료시 모달 닫고 초기화 안내메시지 보여주기
                                        modal.hide('createmodal');
                                        vm.Reset();
                                        vm.$emit('send-message', 1);
                                        vm.$emit('callSearch');
                                    }
                                    vm.MsgData.show = true;
                                    vm.MsgData.msg = res.data.message;
                                }).catch((error) => {
                                vm.MsgData.show = true;
                                vm.MsgData.msg = error;
                            });
                        }
                    });
            },
            cancel() {
                this.$bvModal.hide('createmodal');
            },
            Reset() {
                if (this.energyList.length !== 0) {
                    this.energy = this.energyList[0].id;
                } else {
                    this.energy = '';
                }
                if (this.equipmentNameList.length !== 0) {
                    this.equipmentName = this.equipmentNameList[0].name;
                } else {
                    this.equipmentName = '';
                }
                this.id = '';
                this.name = '';
            },
            createdModal() {
                this.state = 'new';
                this.Reset();
            },
            updateModal(target) {
                this.state = 'update';
                this.id = target.id;
                this.equipmentName = target.equipmentName;
                this.energy = target.energy;
                this.name = target.name;
            },
        },
    };
</script>
