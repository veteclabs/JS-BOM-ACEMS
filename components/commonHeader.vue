<template>
    <div>
        <div id="header">
            <div class="left-header-menu">
                <a class="navbar-minimalize" title="메뉴" @click="toggleMenu()"
                   data-tooltip-text="메뉴 펼치기">
                    <img src="~assets/images/header/icn_header_menu.svg" width="32"/>
                </a>
                <a class="fullscreen-btn" title="전체화면" @click="fullScreen()"
                   data-tooltip-text="전체화면">
                    <img src="~assets/images/header/icn_header_fullscreen.svg" width="32"
                         v-show="!viewFullScreen"/>
                    <img src="~assets/images/header/icn_header_fullscreen-exit.svg" width="32"
                         v-show="viewFullScreen"/>
                </a>
            </div>
            <div class="right-header-menu">
                <a class="user" title="매뉴얼" href="/manual.pdf" data-tooltip-text="매뉴얼" target="_blank"
                   v-if="teamId === 20">
                    <img src="~assets/images/header/icn_header_manual.svg" width="32"/>
                </a>
                <a :class="{'event-unconfirmed-state':unconfirmedEvent !== 0, 'event-btn':true}" title="알림"
                   @click="showEventList()"
                   data-tooltip-text="알림">
                    <img src="~assets/images/header/icn_header_event.svg" width="32"/>
                </a>
                <a class="user" title="계정" @click="downMenu()"
                   data-tooltip-text="계정">
                    <img src="~assets/images/header/icn_header_account.svg" width="32"/>
                </a>
                <ul id="eventDownMenu" v-show="viewEventList">
                    <li v-for="alarm in alarmList" :key="alarm.substationId" @click="alarmCookieSetting(alarm)"
                        :class="{'unconfirmed-li' : alarmChk === null || alarmChk[alarm.id] !== 1}">
                        <div class="event-time">
                            <label class="bom-badge red-badge red"
                                   v-show=" alarmChk === null || alarmChk[alarm.id] !== 1">NEW</label>
                        </div>
                        <div class="event-action">
                            <div class="bom-badge red-bg-badge">{{alarm.substationName}}</div>
                            <p>{{alarm.description}}</p>
                        </div>
                    </li>
                    <li v-show="alarmList.length === 0">
                        알람 발생 내역이 없습니다.
                    </li>
                    <li class="event-link">
                        <nuxt-link to="/alarm">
                            모든 알람 보러 가기
                            <img src="~assets/images/menu/icn_menu_arrow.svg" alt="arrow" width="16"/>
                        </nuxt-link>
                    </li>
                </ul>
                <ul id="downMenu" v-show="viewDownMenu">
                    <li @click="changePasswordModal()">비밀번호변경</li>
                    <li @click="logout()">Logout</li>
                </ul>
            </div>
        </div>
        <eventModal v-bind:propsdata='alarmList'/>
        <passwordChangeModal v-bind:propsdata='passwordModalData' ref="passwordModal"/>
    </div>
</template>

<script>
    import axios from 'axios';
    import dayjs from 'dayjs';
    import eventModal from '~/components/eventModal.vue';
    import passwordChangeModal from '~/components/passwordChangeModal.vue';

    export default {
        name: 'commonHeader',
        components: {
            axios,
            dayjs,
            eventModal,
            passwordChangeModal
        },
        data() {
            return {
                msgData: { // 알람모달
                    msg: '',
                    show: false,
                    e: '',
                },
                passwordModalData: {
                    show: false,
                },
                viewDownMenu: false,
                viewEventList: false,
                unconfirmedEvent: 0,
                alarmChk: {},
                viewFullScreen: false,
                alarmList: [],
                departmentId: '',
                teamId: '',
                email:'',
                interval: '',
                intervalTime: 20 * 1000,
            };
        },
        mounted() {
            this.resetInterval();
            this.getAlarmList();
            this.departmentId = this.$store.getters.User.departmentId;
            this.teamId = this.$store.getters.User.teamId;
            this.email = this.$store.getters.User.email;
        },
        beforeDestroy() {
            this.removeInterval();
        },
        filters: {
            dateFormat: function (value, format) {
                return dayjs(new Date(value)).format(format)
            },
        },
        methods: {
            async getAlarmList() {
                const vm = this;
                let params = {};
                if (vm.teamId === 20) {
                    params = {
                        today: true
                    }
                } else {
                    params = {
                        today: true,
                        page: 0,
                        pageSize: 10,
                        departmentId: vm.departmentId
                    }
                }
                axios.get('/api/alarms', {
                    params: params,
                    timeout: vm.intervalTime,
                }).then((res) => {
                    if (res.data.code === 1) {
                        vm.alarmList = res.data.value;
                    }
                }).catch((error) => {
                    vm.msgData.msg = error;
                }).finally(() => {
                    vm.alarmSetting();
                });
            },
            toggleMenu() {
                this.setBodyClass();
            },
            setBodyClass() {
                const {body} = document;
                body.classList.toggle('full-navbar');
            },
            downMenu() {
                this.viewDownMenu = !this.viewDownMenu;
                this.viewEventList = false;
            },
            showEventList() {
                this.viewEventList = !this.viewEventList;
                this.viewDownMenu = false;
            },
            fullScreen() {
                const docV = document.documentElement;

                this.viewFullScreen = !document.fullscreenElement;
                if (docV.requestFullscreen) {
                    docV.requestFullscreen();
                } else if (docV.webkitRequestFullscreen) {
                    docV.webkitRequestFullscreen();
                } else if (docV.mozRequestFullScreen) {
                    docV.mozRequestFullScreen();
                } else if (docV.msRequestFullscreen) {
                    docV.msRequestFullscreen();
                }

                if (document.exitFullscreen) {
                    document.exitFullscreen();
                } else if (document.webkitExitFullscreen) {
                    document.webkitExitFullscreen();
                } else if (document.mozCancelFullScreen) {
                    document.mozCancelFullScreen();
                } else if (document.msExitFullscreen) {
                    document.msExitFullscreen();
                }
            },
            //비밀번호 변경 모달 생성
            changePasswordModal: function () {
                this.passwordModalData.show = true;
                this.$refs.passwordModal.createdModal(this.email)
            },
            // 로그아웃
            async logout() {
                await axios.get('/api/user/logout')
                    .then(() => {
                        this.$router.push('/login');
                    });
            },

            alarmCookieSetting(target) {
                let value = this.$cookies.get('sidebarAlarmChk');
                if (this.$cookies.get('sidebarAlarmChk') === null) {
                    value = {};
                } else {
                    value = this.$cookies.get('sidebarAlarmChk');
                }
                const cookieId = target.id;
                value[cookieId] = 1;
                const expireTime = new Date();
                expireTime.setHours(23, 59, 59);
                this.$cookies.set('sidebarAlarmChk', value, expireTime);

                this.alarmSetting();
            },
            alarmSetting() {
                // 알람 확인 여부 확인
                this.alarmChk = this.$cookies.get('sidebarAlarmChk');
                {
                }
                // 미확인 알람 개수 체크
                let unconfirmedCount = 0;
                if (this.alarmChk !== null) {
                    this.alarmList.forEach(index => {
                        if (this.alarmChk[index.substationId] === 1) {
                            unconfirmedCount++;
                        }
                    });
                }
                this.unconfirmedEvent = this.alarmList.length - unconfirmedCount;
            },
            // Interval 시작
            resetInterval() {
                const vm = this;
                clearInterval(this.interval);
                vm.interval = null;
                vm.interval = setInterval(() => {
                    vm.getAlarmList();
                }, vm.intervalTime);
            },
            // 인터벌삭제
            removeInterval() {
                const vm = this;
                clearInterval(vm.interval);
            },
        },
    };
</script>
