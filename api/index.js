const express = require('express');
var bodyParser = require('body-parser')
const app = express();
app.use(express.json());
app.use(express.urlencoded({extended:true}));
// 라우터폴더
const common = require('./common/index');
const login = require('./Login/index');
const dashboard = require('./Dashboard/index');
const analysisData = require('./analysis/data');
const analysisDepartment = require('./analysis/department');
const analysisProcess = require('./analysis/process');
const analysisLocation = require('./analysis/location');
const alarm = require('./Alarm/index');
const tags = require('./tag/index');

const settingTarget = require('./setting/target');
const settingUser = require('./setting/user');

const settingEquipment = require('./setting/equipment');
const settingDepartment = require('./setting/department');
const settingProcess = require('./setting/process');
const settingLocation = require('./setting/location');
const settingTeam = require('./setting/team');

/** * @swagger * /product: * get: * tags: * - product * description: 모든 제품 조회 * produces: * - application/json * parameters: * - in: query * name: category * required: false * schema: * type: integer * description: 카테고리 * responses: * 200: * description: 제품 조회 성공 */


// 로그인 - Login
app.post('/user/login', login);
app.get('/user/logout', login);

// 대시보드 - Dashboard
app.get('/WaLogin', dashboard);
app.post('/dashboard/port/getTagValue', dashboard);
app.post('/dashboard/tag/getTagValue', dashboard);

app.get('/dashboard/getHistoryData', dashboard);
app.get('/dashboard/departmant/:departmentId', dashboard);
app.get('/dashboard/departmant/target/:departmentId', dashboard);
app.get('/dashboard/departmant/calendar/:departmentId', dashboard);
app.get('/dashboard/floating', dashboard);
app.get('/dashboard/department/rate', dashboard);
app.post('/dashboard/changeTargetPeak', dashboard);


// CT 대시보드 - CT

app.get('/getPeak', dashboard);
app.post('/UpdateUserPeak', dashboard);
app.get('/getUserPeak', dashboard);

// 태그 리스트
app.get('/tags', tags);
app.get('/tags/departmant/:departmentId', tags);
app.get('/dashboard', dashboard);


// 분석 - analysis
app.get('/analysis/data', analysisData);
app.get('/analysis/department', analysisDepartment);
app.get('/analysis/process', analysisProcess);
app.get('/analysis/location', analysisLocation);

// 알람 - alarm
app.get('/alarms', alarm);
app.get('/substation/alarm', alarm);


//사용자
app.get('/setting/users', settingUser);
app.post('/st/us/chck', settingUser);
app.post('/st/us/stpw', settingUser);
app.post('/setting/user/email/check', settingUser);
app.post('/setting/user', settingUser);
app.put('/setting/user', settingUser);
app.delete('/setting/user', settingUser);

//부서
app.get('/setting/departments', settingDepartment);
app.get('/common/departments', common);
app.get('/common/department-substation/:id', common);
app.get('/common/department-substations', common);
app.post('/setting/department', settingDepartment);
app.put('/setting/department/:departmentId', settingDepartment);
app.delete('/setting/department', settingDepartment);

// 팀 관리
app.get('/setting/teams', settingTeam);
app.get('/setting/teams/:departmentId', settingTeam);
app.post('/setting/team', settingTeam);
app.put('/setting/team/:teamId', settingTeam);
app.delete('/setting/team/:teamId', settingTeam);
app.delete('/setting/team', settingTeam);

// 공정 관리
app.get('/setting/processes', settingProcess);
app.post('/setting/process', settingProcess);
app.put('/setting/process/:processId', settingProcess);
app.delete('/setting/process/:processId', settingProcess);
app.delete('/setting/process', settingProcess);


// 변전실 관리
app.get('/setting/substations', settingLocation);
app.post('/setting/subsation', settingLocation);
app.put('/setting/subsation/:subsationId', settingLocation);
app.delete('/setting/subsation/:subsationId', settingLocation);



// 장치 관리
app.get('/setting/locations', settingEquipment);
app.get('/setting/equipments', settingEquipment);
app.get('/setting/panelBoards', settingEquipment);
app.get('/setting/switchBoards', settingEquipment);
app.post('/setting/location', settingEquipment);
app.put('/setting/location/:locationId', settingEquipment);
app.delete('/setting/location/:locationId', settingEquipment);

// 기준 정보 관리
app.get('/setting/targets', settingTarget);
app.post('/setting/target', settingTarget);
app.put('/setting/target/:targetId', settingTarget);
app.put('/setting/targets', settingTarget);
app.delete('/setting/target/:departmentId', settingTarget);

// 기준 정보 관리
app.get('/setting/substation/targets', settingTarget);
app.post('/setting/substation/target', settingTarget);
app.put('/setting/substation/target/:targetId', settingTarget);
app.put('/setting/substation/targets', settingTarget);
app.delete('/setting/substation/target/:departmentId', settingTarget);


app.post('/WaWebService/Json/SetTagValue/BOM', settingTarget);
app.post('/setting/target', settingTarget);

// 모듈로 사용할 수 있도록 export
// 앱의 /api/* 라우트로 접근하는 모든 요청은 모두 app인스턴스에게 전달됨
module.exports = {
  path: '/api',
  handler: app,
};
app.listen(4009, (err) => {
  if (err) {
  } else {
  }
});
