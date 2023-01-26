const express = require('express');

const app = express();
// 라우터폴더
const login = require('./Login/index');
const webaccess = require('./webaccess/index');
//const scheduleController = require('./scheduleController');

// 로그인 - Login
app.post('/user/login', login);
app.get('/user/logout', login);

app.get('/WaLogin', webaccess);
app.post('/wa/port/getTagValue', webaccess);
app.post('/wa/tag/getTagValue', webaccess);
app.post('/wa/tag/setTagValue', webaccess);

// 모듈로 사용할 수 있도록 export
// 앱의 /api/* 라우트로 접근하는 모든 요청은 모두 app인스턴스에게 전달됨
module.exports = {
    path: '/nuxt',
    handler: app,
};
