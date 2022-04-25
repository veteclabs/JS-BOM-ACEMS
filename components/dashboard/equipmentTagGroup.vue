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
                                    {{tagVal | pickValue('Name',`${tag.tagName}`, 'Value') | numberFormat(1)}} {{tag.unit}}
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
            numberFormat: (value, numFix) => {
                value = parseFloat(value);
                if (!value) return '0';
                return value.toLocaleString('ko-KR', {maximumFractionDigits: numFix});
            },
            pickValue: function (object, property, value, returnValue) {
                if (object === undefined || object === null || object === "") {
                    return -1;
                } else {
                    let target = object.filter(object => object[property] === value);
                    if (target.length === 0) {
                        return -100;
                    } else {
                        return target[0][returnValue];
                    }
                }
            },
        }
    };
</script>
