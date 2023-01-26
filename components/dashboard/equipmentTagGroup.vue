<template>
    <div>
        <div class="title-box">
            <h2>
                {{title}}
            </h2>
        </div>
        <div class="row dashboard-item-box">
            <div v-for="item in propsdata" :key="item.id" class="col-lg-3">
                <div class="ibox">
                    <div class="ibox-title">
                        <h3>{{item.name}}</h3>
                    </div>
                    <div class="ibox-content" v-if="item.tags.length !== 0">
                        <ul class="tag-box">
                            <li v-for="tag in item.tags" :key="tag.id">
                                <div class="tagname">{{tag.description}}</div>
                                <div>
                                    {{tag.value| valueFormat(2)}}{{tag.unit}}
                                </div>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    export default {
        props: ['propsdata', 'title'],

        filters: {
            valueFormat: (value, numFix) => {
                value = parseFloat(value);
                if (!value) {
                    return '0';
                }else if(value <= -101 && value >= -113) {
                    return '-'
                }else {
                    return value.toLocaleString('ko-KR', {maximumFractionDigits: numFix});
                }
            },
        }
    };
</script>
