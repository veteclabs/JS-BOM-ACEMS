import Vue from 'vue';
import axios from 'axios';


export default ({store, app: {$axios}}) => {
  axios.interceptors.request.use(config => {
    //config.baseURL = 'dd'
    config.baseURL = "http://localhost:8031";
    return config;
  });
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
