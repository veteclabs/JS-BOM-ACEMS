<template>
    <div>
        <div v-for="(prop, index) in alarmInfoList" :key="index">
            <b-modal
                    v-model="prop.show"
                    id="eventModal"
                    ref="eventModal"
                    @hide="hideModal(prop.id)"
                    hide-header
                    ok-only
                    hide-backdrop
                    auto-focus-button="ok"
            >
                <div>
                    <div class="modal-title">
                        <img src="~/assets/images/common/icn_common_event.svg" alt="기준사용량 초과 알림"/>
                        <p class="font18 red">EVENT</p>
                    </div>
                    <div class="modal-info">
                        <div>
                            <div class="title">알람타입</div>
                            <div class="text red">{{ prop.alarm_type}}</div>
                        </div>
                        <div>
                            <div class="title">알람 내용</div>
                            <div class="text red"> {{ prop.description}}</div>
                        </div>
                        <div>
                            <div class="title">발생시간</div>
                            <div class="text">{{prop.logDate | dayjs('YYYY-MM-DD')}} {{ prop.logTime}}</div>
                        </div>
                    </div>
                </div>
            </b-modal>
        </div>
    </div>
</template>
<script>
    import dayjs from 'dayjs';
    export default {
        props: ['propsdata'],
        components: {
            dayjs,
        },
        data() {
            return {
                alarmList: [],
                alarmInfoList: [],
                alarmModalChk: {},
            };
        },
        methods: {
            async alarmModalShow() {
                const vm = this;
                vm.alarmList = vm.propsdata;
                let modalCookie = this.$cookies.get('chkModal');
                let ModalCookieTarget;
                vm.alarmList.forEach((index) => {
                    if (modalCookie !== null) {
                        ModalCookieTarget = modalCookie[`${index.id}`];
                    } else {
                        ModalCookieTarget = undefined
                    }
                    if (ModalCookieTarget === undefined) {
                        vm.alarmInfoList.push({
                            id: index.id,
                            logDate: index.date,
                            logTime: index.time,
                            description: index.description,
                            alarm_type: index.alarm_type,
                            show: true,
                        });
                    }
                });
                vm.alarmSetting();
            },
            hideModal(id) {
                let value = this.$cookies.get('chkModal');
                if (this.$cookies.get('chkModal') === null) {
                    value = {};
                } else {
                    value = this.$cookies.get('chkModal');
                }
                const cookieId = `${id}`;
                value[cookieId] = 1;
                const expireTime = new Date();
                expireTime.setHours(23, 59, 59);
                this.$cookies.set('chkModal', value, expireTime);
            },
            alarmSetting() {
                // 알람 확인 여부 확인
                this.alarmModalChk = this.$cookies.get('chkModal');
            },
        },
        filters: {
            currency: (value) => {
                value = parseFloat(value);
                if (!value) return '0';
                return value.toFixed(0)
                    .replace(/(\d)(?=(\d{3})+(?:\.\d+)?$)/g, '$1,');
            },
            dayjs: (value, format) => {
                return dayjs(value).format(format)
            }
        },
        watch: {
            propsdata() {
                this.alarmModalShow();
            },
        },
    };
</script>
