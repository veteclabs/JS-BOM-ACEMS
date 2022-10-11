import Vue from 'vue';
import axios from 'axios';


export default ({store, app: {$axios}}) => {
  if (process.env.NODE_ENV !== 'production') {
    axios.interceptors.request.use(config => {
      //config.baseURL = 'dd'
      config.baseURL = "http://localhost:8031"; //원래는 여기가 cross-origin으로 막혀있음(spring boot에서 막아놨어) -> 그거 푸는 설정 추가해서 이거 추가 / 추가 안하면 nginx나 톰캣써서 두개 서버 연동 시켜야함
      return config;
    });
  }
  axios.interceptors.response.use(function (response) {
    return response;
  }, async function (error) {
    /*const errorAPI = error.config;
    let now = new Date();
     let curnSec = Number(now.getTime().toString().substr(0, 10));
       if (tokenExpiresIn <= curnSec-10) {
         console.log("aaa")
           store.commit('SET_REFESH_TOKEN', null);
           store.commit('SET_TOKEN', null);
           //return redirect('/login');
       }*/
    return error.response;
  });

}
Vue.use(axios);
