<template>
    <div>
        <div id="header">
            <div class="left-header-menu">
                <div class="header-title">
                    <div>
                        <h1>{{routeName}}</h1>
                        <ul class="header-nav">
                            <li><img src="~assets/images/common/icn_common_home.svg" alt="home"/></li>
                            <li v-for="menu in routeList" :key="menu">
                                {{menu}}
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            <div class="right-header-menu">
                <div class="manual">
                    <a title="매뉴얼"
                       href="/manual.pdf"
                       data-tooltip-text="매뉴얼"
                       target="_blank">
                        <img src="~assets/images/header/icn_header_manual.svg" width="32"/>
                    </a>
                </div>

                <div class="nowTime">
                    {{nowTime | dateFormat('YYYY-MM-DD ddd')}}
                    <span>{{nowTime | dateFormat('HH:mm:ss')}}</span>
                </div>
                <a class="user" title="계정" @click="downMenu()">I</a>
                <div id="downMenu" v-show="viewDownMenu">
                    <div class="user-info">
                        <div class="user">I</div>
                        <div>
                            <p class="user-name">{{user.name}}</p>
                            <p class="email">{{user.email}}</p>
                        </div>
                    </div>
                    <button class="logout-btn" @click="logout()">Logout</button>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import axios from 'axios';
    import dayjs from 'dayjs';
    import passwordChangeModal from '~/components/passwordChangeModal.vue';

    export default {
        name: 'commonHeader',
        components: {
            axios,
            dayjs,
            passwordChangeModal,
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
                nowTime: new Date(),
                user: '',
                viewDownMenu: false,
                routeName: '',
                routeList: [],
                interval: '',
                intervalTime: 1000,
            };
        },
        mounted() {
            this.user = this.$store.getters.User;
            this.resetInterval();
            this.routeSetting();
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
            routeSetting() {
                let menus = {
                    'dashboard': {
                        title: '전체대시보드',
                        pathList: ['대시보드'],
                    },
                    'dashboard-id': {
                        title: '개별대시보드',
                        pathList: ['대시보드', '개별대시보드'],
                    },
                    'dashboard-groupDashboard': {
                        title: '그룹별대시보드',
                        pathList: ['대시보드', '그룹별대시보드'],
                    },
                    'analysis': {
                        title: '데이터 조회',
                        pathList: ['분석', '데이터 조회'],
                    },
                    'setting-equipment': {
                        title: '장비관리',
                        pathList: ['설정', '장비관리'],
                    },
                    'setting-group': {
                        title: '그룹관리',
                        pathList: ['설정', '그룹관리'],
                    },
                    'setting-group-create': {
                        title: '그룹관리',
                        pathList: ['설정', '그룹관리', '등록'],
                    },
                    'setting-group-id': {
                        title: '그룹관리',
                        pathList: ['설정', '그룹관리', '수정'],
                    },
                    'setting-aircompressor': {
                        title: '공기압축기관리',
                        pathList: ['설정', '공기압축기관리'],
                    },
                    'setting-model': {
                        title: '모델관리',
                        pathList: ['설정', '모델관리'],
                    },
                    'setting-component': {
                        title: '컴포넌트관리',
                        pathList: ['설정', '컴포넌트관리'],
                    },
                };
                this.routeName = menus[this.$route.name] ? menus[this.$route.name].title : '';
                this.routeList = menus[this.$route.name] ? menus[this.$route.name].pathList : [];
            },
            downMenu() {
                this.viewDownMenu = !this.viewDownMenu;
            },
            //비밀번호 변경 모달 생성
            changePasswordModal: function () {
                this.passwordModalData.show = true;
                this.$refs.passwordModal.createdModal(this.email)
            },
            // 로그아웃
            async logout() {
                await axios.get('/nuxt/user/logout')
                    .then(() => {
                        this.$store.commit('SET_USER', null);
                        this.$router.push('/login');
                    });
            },
            settingTime() {
                this.nowTime = new Date();

            },
            resetInterval() {
                const vm = this;
                clearInterval(this.interval);
                vm.interval = null;
                vm.interval = setInterval(() => {
                    vm.settingTime();
                }, vm.intervalTime);
            },
            removeInterval() {
                const vm = this;
                clearInterval(vm.interval);
            },
        },
        watch: {
            $route() {
                this.routeSetting();
            }
        }
    };
</script>
