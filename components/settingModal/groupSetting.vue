<template>
    <div>
        <div class="ibox">
            <table class="bom-table">
                <caption>그룹 정보를 입력합니다.</caption>
                <colgroup>
                    <col style="width:100px;"/>
                    <col style="width:auto;"/>
                </colgroup>
                <tbody>
                <tr>
                    <th>그룹명</th>
                    <td>
                        <label class="input-100">
                            <input type="text" v-model="name" class="input-100"
                                   placeholder="그룹명을 입력해주세요 "/>
                        </label>
                        <div class="err-message" v-if="validation !== undefined">
                            {{ validation.firstError('name') }}
                        </div>
                    </td>
                </tr>
                <!--<tr>
                    <th>용량</th>
                    <td>
                        <label class="input-100">
                            <input type="text" v-model="capacity" class="input-100"/>
                        </label>
                    </td>
                </tr>-->
                </tbody>
            </table>
        </div>
        <div class="ibox">
            <div class="ibox-title ibox-noborder-title flex-box">
                공기압축기 압력 제어 범위
            </div>
            <table class="bom-table">
                <tr>
                    <td>
                        <div class="td-label">Min</div>
                        <label class="input-100">
                            <input type="number" v-model="barRange[0]" class="input-100" placeholder="최소압력"/>
                        </label>
                    </td>
                    <td>~</td>
                    <td>
                        <div class="td-label">Max</div>
                        <label class="input-100">
                            <input type="number" v-model="barRange[1]" class="input-100" placeholder="최대압력"/>
                        </label>
                    </td>
                </tr>
            </table>
        </div>
        <div class="ibox">
            <div class="ibox-title ibox-noborder-title flex-box">
                <div>그룹 스케줄 제어</div>
                <label class="switch-box">
                    <input type="checkbox" v-model="isSchedule" :true-value="1" :false-value="0">
                    <span class="slider round"></span>
                </label>
            </div>
            <table class="bom-table">
                <tr :class="{'disabled-td' : isSchedule === 0}">
                    <td colspan="3">
                        <div class="td-label">Date</div>
                        <ul class="date-ul">
                            <li v-for="item in dateList">
                                <label>
                                    <input type="checkbox" :value="item.id" v-model="date"
                                           :disabled="isSchedule === 0"/>
                                    <div>{{item.name}}</div>
                                </label>
                            </li>
                        </ul>
                    </td>
                </tr>
                <tr :class="{'disabled-td' : isSchedule === 0}">
                    <td style="vertical-align: bottom; position:relative;">
                        <div class="td-label">시작시간</div>
                        <label class="input-100">
                            <date-picker v-model="time.start" :config="timeOptions" :disabled="isSchedule === 0"/>
                        </label>
                    </td>
                    <td>~</td>
                    <td style="vertical-align: bottom; position:relative;">
                        <div class="td-label">종료시간</div>
                        <label class="input-100">
                            <date-picker v-model="time.end" :config="timeOptions" :disabled="isSchedule === 0"/>
                        </label>
                    </td>
                </tr>
            </table>
        </div>

        <div class="ibox">
            <div class="ibox-title ibox-noborder-title">공기압축기 전체 스케줄 제어</div>
            <div class="ibox-content">
                <div :class="{'disabled-ibox' : isSchedule === 0, 'all-schedule-list':true}">
                    <ul v-for="(targetWeek, property, targetIndex) in weekSchedule">
                        <div class="td-label">{{targetIndex + 1}}째주</div>
                        <li v-if="targetWeek.order.length === 0">
                            스케줄 장비가 없습니다.
                        </li>
                        <draggable :group="`week${targetIndex}`"  :list="targetWeek.order">
                            <li v-for="(equipment, property, index) in targetWeek.order"
                                :key="equipment.id">
                                <div class="equipment-box">{{ equipment.name }}</div>
                            </li>
                        </draggable>
                        <div class="td-label" style="margin:48px 0 0 0">스케줄 대기장비</div>
                        <li v-if="targetWeek.standby.length === 0">
                            대기 장비가 없습니다.
                        </li>
                        <draggable :group="`week${targetIndex}`" :list="targetWeek.standby">
                            <li v-for="(equipment, property, index) in targetWeek.standby" class="no-schedule-li"
                                :key="equipment.id">
                                <div class="equipment-box">{{ equipment.name }}</div>
                            </li>
                        </draggable>
                    </ul>

                </div>
            </div>
        </div>
        <flashModal v-bind:propsdata="msgData"/>
    </div>
</template>

<script>
    import SimpleVueValidation from 'simple-vue-validator';
    import axios from 'axios';
    import flashModal from '~/components/flashmodal.vue';
    import draggable from 'vuedraggable';

    const {Validator} = SimpleVueValidation;

    export default {

        props: ['propsdata'],
        components: {
            axios,
            flashModal,
            SimpleVueValidation,
            draggable,
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
                barRange: [6, 8],
                isSchedule: 1,
                dateList: [
                    {id: 1, name: '월'},
                    {id: 2, name: '화'},
                    {id: 3, name: '수'},
                    {id: 4, name: '목'},
                    {id: 5, name: '금'},
                    {id: 6, name: '토'},
                    {id: 7, name: '일'}
                ],
                date: [],
                time: {
                    start: '00:00',
                    end: '00:00'
                },
                timeOptions: {
                    format: 'HH:mm',
                },
                propertyName: {
                    standby: '스케줄대기장치'
                },
                weekName: ['첫째주', '둘째주', '셋째주', '넷째주'],
                weekSchedule: {
                    week1List: {
                        order: [
                            {id: 1, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -1', order: 1},
                            {id: 2, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -2', order: 1},
                            {id: 3, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -3', order: 1},
                        ],
                        standby: [
                            {id: 4, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -4', order: 1}
                        ]
                    },
                    week2List: {
                        order: [
                            {id: 1, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -1', order: 1},
                            {id: 2, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -2', order: 1},
                            {id: 3, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -3', order: 1},
                        ],
                        standby: [
                            {id: 4, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -4', order: 1}
                        ]
                    },
                    week3List: {
                        order: [
                            {id: 1, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -1', order: 1},
                            {id: 2, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -2', order: 1},
                            {id: 3, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -3', order: 1},
                        ],
                        standby: [
                            {id: 4, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -4', order: 1}
                        ]
                    },
                    week4List: {
                        order: [
                            {id: 1, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -1', order: 1},
                            {id: 2, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -2', order: 1},
                            {id: 3, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -3', order: 1},
                        ],
                        standby: [
                            {id: 4, equipmentId: 'ingersollrand_rm55', name: 'Ingersoll Rand RM55 -4', order: 1}
                        ]
                    },
                }


            };
        },
        validators: {
            name(value) {
                return Validator.value(value).required();
            }
            ,
        }
        ,
        methods: {
            onDragover(event) {
                event.preventDefault()
            },
            startDrag(event, value) {
                console.log("start")
                console.log(value)
                this.dragAssetItem = value;
            },
            onDrop(event, data) {
                console.log("drop")
                console.log(data)
                event.preventDefault();
            },
            submit() {
                const vm = this;
                const modal = this.$bvModal;
                const {state} = this;
                let url, method;

                if (state === 'new') {
                    url = `/api/setting/group`;
                    method = 'post';
                } else if (state === 'update') {
                    url = `/api/setting/group/${vm.id}`;
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
                    costCenter: this.costCenter,
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
                                    modal.hide('createGroupModal');
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
            }
            ,
            cancel() {
                this.$bvModal.hide('createGroupModal');
            }
            ,
            reset() {
                this.name = '';
                this.name = '';
                this.costCenter = '';
            }
            ,
            createdModal() {
                this.state = 'new';
                this.reset();
                this.validation.reset();
            }
            ,
            updateModal(e) {
                this.validation.reset();
                this.state = 'update';
                this.id = e.id;
                this.name = e.name;
                this.costCenter = e.costCenter;
            }
            ,
        }
        ,
    }
    ;
</script>
