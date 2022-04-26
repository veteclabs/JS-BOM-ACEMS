<template>
    <nav id="LeftMenu" class="navbar-default navbar-static-side" role="navigation">
        <div class="nav-info">
            <ul class="nav" id="side-menu">
                <li class="nav-header">
                    <div class="profile-element mini-info">
                        <img src="~assets/images/common/icn_common_logo.png" alt="Logo" width="40"/>
                    </div>
                    <div class="profile-element-detail detail-info">
                        <p class="profile-title">BOM-ACEMS</p>
                        <p class="profile-subtitle">정석엔지니어링</p>
                    </div>
                </li>
                <li v-for="(item, index) in menus" :key="index"
                    :class="[ path.split('/')[1] === item.class ? 'active ' + item.class : item.class, ]"
                >
                    <nuxt-link :to="item.link">
                        <img :src="item.icon" :alt="item.class" :width="26"/>
                    </nuxt-link>
                    <ul class="nav nav-second-level" v-if="item.subMenu">
                        <li class="title-li">
                            {{item.class | toUpperCase}}
                            <img src="~assets/images/common/icn_down_arrow.svg" alt="arrow" width="16"/>
                        </li>
                        <li v-for="(subitem, index) in item.subMenu" :key="index"
                            :class="[subitem.link === path ? 'active' : '']">
                            <a v-if="subitem.hasOwnProperty('target')" :href="subitem.link" :target="subitem.target">
                                {{ subitem.menuName }}
                            </a>
                            <nuxt-link v-else :to="subitem.link">{{ subitem.menuName }}</nuxt-link>
                        </li>
                    </ul>
                </li>
            </ul>
        </div>
    </nav>
</template>
<script>
    import allMenuArray from '~/assets/data/allMenuArray.json';

    export default {
        name: 'commonNav',
        data() {
            return {
                path: '',
                menus: '',
                departments: [],
            };
        },
        created() {
            this.menuSetting();
        },
        methods: {
            menuSetting() {
                this.menus = allMenuArray.list;
                for (let i in this.menus) {
                    let iconName = this.menus[i].class;
                    this.menus[i].icon = require(`@/assets/images/common/icn_common_${iconName}.png`);
                }
                this.menuPathChk();
            },
            menuPathChk() {
                this.path = this.$route.path;
            },
        },
        watch: {
            $route() {
                this.menuPathChk();
            },
        },
        filters: {
            toUpperCase: function (value) {
                return value.toUpperCase();
            }
        }
    };
</script>
