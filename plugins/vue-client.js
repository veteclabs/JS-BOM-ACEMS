import Vue from 'vue';
// datepicker
import datePicker from 'vue-bootstrap-datetimepicker';
// eslint-disable-next-line import/no-extraneous-dependencies
import 'pc-bootstrap4-datetimepicker/build/css/bootstrap-datetimepicker.css';

// Apex 그래프
import VueApexCharts from 'vue-apexcharts';

// date 기간선택
import VueCtkDateTimePicker from 'vue-ctk-date-time-picker';
import 'vue-ctk-date-time-picker/dist/vue-ctk-date-time-picker.css';

// 벨리데이션
import SimpleVueValidation from 'simple-vue-validator';

// Vue filter
import VueFilter from 'vue-filter';

// 스크롤바
import Vuebar from 'vuebar';

// report
import VueHtml2Canvas from 'vue-html2canvas';

import VCalendar from 'v-calendar';
import Dayjs from 'vue-dayjs';

Vue.use(datePicker);

Vue.use(VueApexCharts);
Vue.component('apexchart', VueApexCharts);
Vue.component('VueCtkDateTimePicker', VueCtkDateTimePicker);
Vue.use(SimpleVueValidation);

SimpleVueValidation.extendTemplates({
    required: '필수 입력 항목입니다.',
    length: '길이가 {0} 이어야 합니다.',
    minLength: '{0} 글자 이상이어야 합니다.',
    maxLength: '{0} 글자 이하여야 합니다.',
    digit: '숫자만 입력해주세요.',
    match: '비밀번호가 일치하지 않습니다.',
    email: '메일 형식으로 입력해주세요.ex)test@test.com',
    url:'url 형식으로 입력해주세요.'
});
Vue.use(VueFilter);
Vue.use(Vuebar);
Vue.use(VueHtml2Canvas);

// 쿠키
Vue.use(require('vue-cookies'));


Vue.use(VCalendar);

Vue.use(Dayjs);
