const express = require('express');

const router = express.Router();
const Pool = require('../../config/database');
const schedule = require("node-schedule");
router.get('/compressors', async (req, res) => {
  let conn;
  try {
    conn = await Pool.getConnection();
    let query = `SELECT
dm.device_id id,
dm.name,
s.startTime,
s.endTime
FROM device dm
LEFT JOIN equipment e ON e.equipment_id = dm.equipment_id
LEFT JOIN \`schedule\` s ON dm.device_id = s.device_id
WHERE e.\`type\` = "compressor"`;
    let getComp = await conn.query(query);
    for (let comp of getComp) {
      query = `SELECT dow.name AS \`dayOfWeek\`, dow.dayofweek_id
FROM device d
LEFT join \`schedule\` s ON d.device_id = s.device_id
LEFT JOIN day_mapper dmp ON dmp.schedule_id = s.schedule_id
LEFT JOIN dayofweek dow ON dow.dayofweek_id = dmp.dayofweek_id
WHERE d.device_id = ${comp.id}
GROUP BY dow.name`;
      const getDayOfWeek = await conn.query(query);
      query = `SELECT w.name AS \`week\`, w.week_id
FROM device d
LEFT join \`schedule\` s ON d.device_id = s.device_id
LEFT JOIN week_mapper wmp ON wmp.schedule_id = s.schedule_id
LEFT JOIN week w ON w.week_id = wmp.week_id
WHERE d.device_id = ${comp.id}
GROUP BY w.name`;
      const getWeeks = await conn.query(query);
      comp.dayOfWeeks = getDayOfWeek[0].dayOfWeeks
      getDayOfWeek.map(({}))
      comp.weekNames = getWeeks.map((({week})=>week)).join(', ')
      comp.weekIds = getWeeks.map((({week_id})=>week_id))
    }

    res.json({
      code: 1,
      value: getComp,
    });
  } catch (e) {
    res.json({
      message: '응답이 없습니다. 새로고침 후 다시 시도하시기 바랍니다.',
    });
  } finally {
    if (conn) {
      await conn.release();
    }
  }
});

router.get('/dayofweek', async (req, res) => {
  let conn;
  try {
    conn = await Pool.getConnection();
    let query = `SELECT dayofweek_id id, name from dayofweek`;
    let getComp = await conn.query(query);
    let rule = new schedule.RecurrenceRule();
    rule.second = [1, 10, 21, 24, 31, 34, 37, 40, 44, 47, 50, 53, 56, 59];
    rule.id = 1;
    schedule.scheduleJob("5", rule, () => {
      console.log("ewf");
    });
    console.log(schedule.scheduledJobs)
    res.json({
      code: 1,
      value: getComp,
    });
  } catch (e) {
    res.json({
      message: '응답이 없습니다. 새로고침 후 다시 시도하시기 바랍니다.',
    });
  } finally {
    if (conn) {
      await conn.release();
    }
  }
});
router.get('/week', async (req, res) => {
  let conn;
  try {
    conn = await Pool.getConnection();
    let query = `SELECT week_id id, name from week`;
    let getComp = await conn.query(query);

    res.json({
      code: 1,
      value: getComp,
    });
  } catch (e) {
    res.json({
      message: '응답이 없습니다. 새로고침 후 다시 시도하시기 바랍니다.',
    });
  } finally {
    if (conn) {
      await conn.release();
    }
  }
});



router.post('/compressor', async (req, res) => {
  let conn;
  try {
    conn = await Pool.getConnection();
    const deviceName = req.body.name;
    const startTime = req.body.startTime;
    const endTime = req.body.endTime;
    const dayOfWeeks = req.body.dayOfWeeks;
    const weeks = req.body.weeks;
    const max = req.body.max;
    const min = req.body.min;
    let deviceId = await conn.query(`SHOW TABLE STATUS FROM \`ing\` WHERE \`name\` LIKE 'device'`);
    deviceId = deviceId[0].Auto_increment
    let createDevice = await conn.query(`insert into device(\`name\`, \`equipment_id\`) values (
    '${deviceName}',
    1)`);
    let scheduleId = await conn.query(`SHOW TABLE STATUS FROM \`ing\` WHERE \`name\` LIKE 'schedule'`);
    scheduleId = scheduleId[0].Auto_increment
    let createSchedule = await conn.query(`insert into schedule(\`startTime\`, \`endTime\`, \`min\`, \`max\`, \`device_id\`) values (
    '${startTime}',
    '${endTime}',
    ${max},
    ${min},
    ${deviceId})`);

    for (let dayOfWeek of dayOfWeeks) {
      await conn.query(`insert into day_mapper(\`schedule_id\`, \`dayofweek_id\`) values(${scheduleId}, ${dayOfWeek})`);
    }
    for (let week of weeks) {
      await conn.query(`insert into week_mapper(\`schedule_id\`, \`week_id\`) values(${scheduleId}, ${week})`);
    }
    // 완료 후 메시지전송
    res.json({
      code: 1,
      message: process.env.DB_INSERT_MSG,
    });
  } catch (e) {
    console.log(e)
    res.json({
      message: '응답이 없습니다. 새로고침 후 다시 시도하시기 바랍니다.',
    });
  } finally {
    if (conn) {
      await conn.release();
    }
  }
});

router.put('/compressor/:deviceId', async (req, res) => {
  let conn;
  try {
    conn = await Pool.getConnection();
    const deviceId = req.params.deviceId
    const deviceName = req.body.name;
    const startTime = req.body.startTime;
    const endTime = req.body.endTime;
    const dayOfWeeks = req.body.dayOfWeeks;
    const weeks = req.body.weeks;
    const max = req.body.max;
    const min = req.body.min;
    let createDevice = await conn.query(`update  device 
    set 
    name = '${deviceName}'
    where
    device_id = ${deviceId}`);
    let createSchedule = await conn.query(`insert into schedule(\`startTime\`, \`endTime\`, \`min\`, \`max\`, \`device_id\`) values (
    '${startTime}',
    '${endTime}',
    ${max},
    ${min},
    ${deviceId})`);

    for (let dayOfWeek of dayOfWeeks) {
      await conn.query(`insert into day_mapper(\`schedule_id\`, \`dayofweek_id\`) values(${scheduleId}, ${dayOfWeek})`);
    }
    for (let week of weeks) {
      await conn.query(`insert into week_mapper(\`schedule_id\`, \`week_id\`) values(${scheduleId}, ${week})`);
    }
    // 완료 후 메시지전송
    res.json({
      code: 1,
      message: process.env.DB_INSERT_MSG,
    });
  } catch (e) {
    (e)
    res.json({
      message: '응답이 없습니다. 새로고침 후 다시 시도하시기 바랍니다.',
    });
  } finally {
    if (conn) {
      await conn.release();
    }
  }
});

router.delete('/setting/location/:locationId', async (req, res) => {
  let conn;
  try {
    conn = await Pool.getConnection();
    const id = req.params.equipmentId;
    const query = ` 
    delete from
     department_substation
    WHERE id=${id}`;
    await conn.query(query);

    // 완료 후 메시지전송
    res.json({
      code: 1,
      message: process.env.DB_DELETE_MSG,
    });
  } catch (e) {
    res.json({
      message: '응답이 없습니다. 새로고침 후 다시 시도하시기 바랍니다.',
    });
  } finally {
    if (conn) {
      await conn.release();
    }
  }
});

// 모듈로 사용할 수 있도록 export
module.exports = router;
