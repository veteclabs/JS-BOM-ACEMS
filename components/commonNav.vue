<template>
    <nav id="LeftMenu" class="navbar-default navbar-static-side" role="navigation">
        <div class="nav-info">
            <ul class="nav" id="side-menu">
                <li class="nav-header">
                    <div class="profile-element mini-info">
                        <img src="~assets/images/menu/icn_menu_bom-logo.svg" alt="Logo" width="32"/>
                    </div>
                    <div class="profile-element-detail detail-info">
                        <p class="profile-title">BOM-ACEMS</p>
                        <p class="profile-subtitle">정석엔지니어링</p>
                    </div>
                </li>
                <li v-for="(item, index) in menus" :key="index"
                    :class="[ path.split('/')[1] === item.class ? 'active ' + item.class : item.class, ]"
                    @mouseover="hoverEvent(index)" @mouseleave="leaveEvent(index)"
                >
                    <nuxt-link :to="item.link">
                        <img v-if="path.split('/')[1] === item.class" :src="item.iconSelect" :alt="item.class"/>
                        <img v-if="path.split('/')[1] !== item.class" :src="item.icon" :alt="item.class"/>
                    </nuxt-link>
                    <ul class="nav nav-second-level" v-if="item.subMenu">
                        <li class="title-li">
                            {{item.class | toUpperCase}}
                            <img src="~assets/images/menu/icn_menu_arrow.svg" alt="arrow" width="16"/>
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
                    this.menus[i].icon = require(`@/assets/images/menu/icn_menu_${iconName}.svg`);
                    this.menus[i].iconNormal = require(`@/assets/images/menu/icn_menu_${iconName}.svg`);
                    this.menus[i].iconHover = require(`@/assets/images/menu/icn_menu_b_${iconName}.svg`);
                    this.menus[i].iconActive = require(`@/assets/images/menu/icn_menu_w_${iconName}.svg`);
                    this.menus[i].iconSelect = require(`@/assets/images/menu/icn_menu_w_${iconName}.svg`);
                }

                this.menuPathChk();
            },
            menuPathChk() {
                this.path = this.$route.path;
            },
            hoverEvent(index) {
                this.menus[index].icon = this.menus[index].iconHover;
                this.menus[index].iconSelect = this.menus[index].iconActive;
            },
            leaveEvent(index) {
                this.menus[index].icon = this.menus[index].iconNormal;
                this.menus[index].iconSelect = this.menus[index].iconActive;
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
