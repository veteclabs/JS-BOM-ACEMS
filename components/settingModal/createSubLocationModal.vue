<template>
    <div>
        <b-modal v-model="propsdata.show" id="createmodal" title="상세 시설 관리" hide-footer>
            <table class="bom-table">
                <caption>상세 시설을 입력합니다.</caption>
                <colgroup>
                    <col style="width:100px;"/>
                    <col style="width:auto;"/>
                </colgroup>
                <tbody>
                <tr>
                    <th>시설명</th>
                    <td>
                        <label class="input-100">
                            <select v-model="locationId" class="input-100">
                                <option v-for="list in locationList" v-bind:value="list.id"
                                        :key="list.id">
                                    {{list.name}}
                                </option>
                            </select>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('locationName') }}
                        </div>
                    </td>
                </tr>
                <tr>
                    <th>상세위치명</th>
                    <td>
                        <label class="input-100">
                            <input type="text" v-model="name" class="input-100"
                                   placeholder="상세위치 명을 입력해주세요 "/>
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
    import SimpleVueValidation from 'simple-vue-validator';
    import axios from 'axios';
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
                MsgData: { // 알람모달
                    msg: '',
                    show: false,
                    e: '',
                },
                state: 'new',
                id: '',
                name: '',
                locationId: '',
                locationList: [
                    {id: 1, name: '가동'},
                    {id: 2, name: '나동'},
                    {id: 3, name: '다동'},
                    {id: 4, name: '자동'},
                ],
            };
        },
        validators: {
            locationId(value) {
                return Validator.value(value).required();
            },
            name(value) {
                return Validator.value(value).required().maxLength(50);
            },
        },
        mounted() {
            //this.getLocation();
        },
        methods: {
            getLocation() {
                const vm = this;
                axios({
                    method: 'get',
                    url: '/api/setting/location/getLocation',
                })
                    .then((res) => {
                        if (res.data.code === 1) {
                            vm.locationList = res.data.mainBuildingValue;
                        }
                    })
                    .catch((error) => {
                        vm.MsgData.show = true;
                        vm.MsgData.msg = error;
                    });
            },
            submit() {
                const vm = this;
                const modal = this.$bvModal;
                const {state} = this;
                let url;

                if (state === 'new') {
                    url = '/api/setting/location/createLocationDetail';
                } else if (state === 'update') {
                    url = '/api/setting/location/updateLocationDetail';
                }
                const params = {
                    subLocationUpdate: {
                        id: vm.id,
                        locationId: vm.locationId,
                        name: vm.name,
                    }
                };

                this.$validate()
                    .then((success) => {
                        if (success) {
                            axios.post(url, params)
                                .then((res) => {
                                    if (res.data.code === 1) {
                                        // 등록완료시 모달 닫고 초기화 안내메시지 일여주기
                                        modal.hide('createmodal');
                                        vm.MsgData.show = true;
                                        vm.MsgData.msg = res.data.message;
                                        vm.$emit('send-message', 1);
                                        vm.$emit('callSearch');
                                    }
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
            reset() {
                this.id = '';
                if (this.locationList.length !== 0) {
                    this.locationName = this.locationList[0].id;
                }
                this.name = '';
            },
            createdModal() {
                this.reset();
                this.state = 'new';
                this.validation.reset();
            },
            updateModal(e) {
                this.state = 'update';
                this.id = e.data.id;
                this.locationId = e.data.locationId;
                this.name = e.data.name;
            },
        },
    };
</script>
