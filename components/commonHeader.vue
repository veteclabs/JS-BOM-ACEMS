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
                <a class="user" title="계정" @click="downMenu()">I</a>
                <ul id="downMenu" v-show="viewDownMenu">
                    <li>
                        <a title="매뉴얼" href="/manual.pdf" data-tooltip-text="매뉴얼" target="_blank">
                            매뉴얼
                        </a>
                    </li>
                    <li @click="changePasswordModal()">비밀번호변경</li>
                    <li @click="logout()">Logout</li>
                </ul>
            </div>
        </div>
        <passwordChangeModal v-bind:propsdata='passwordModalData' ref="passwordModal"/>
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
                routeName:'',
                routeList:[],
                interval: '',
                intervalTime: 20 * 1000,
            };
        },
        mounted() {
            this.routeSetting();
        },
        filters: {
            dateFormat: function (value, format) {
                return dayjs(new Date(value)).format(format)
            },
        },
        methods: {
            routeSetting() {
                this.routeName =  this.$route.name;
                this.routeList =  this.$route.path.split("/");
                this.routeList.shift();
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
                await axios.get('/api/user/logout')
                    .then(() => {
                        this.$router.push('/login');
                    });
            },
        },
        watch: {
            $route() {
                this.routeSetting();
            }
        }
    };
</script>
