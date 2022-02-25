const express = require('express');

const router = express.Router();
const Pool = require('../../config/database');

router.get('/setting/panelBoards', async (req, res) => {
  let conn;
  try {
    conn = await Pool.getConnection();
    const query = `SELECT * FROM panelboards`;
    const queryResult = await conn.query(query);
    res.json({
      code: 1,
      value: queryResult,
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

router.get('/setting/switchBoards', async (req, res) => {
  let conn;
  try {
    conn = await Pool.getConnection();
    const query = `SELECT * FROM switchboards`;
    const queryResult = await conn.query(query);
    res.json({
      code: 1,
      value: queryResult,
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

router.get('/setting/locations', async (req, res) => {
  let conn;
  try {
    conn = await Pool.getConnection();
    const query = `
    SELECT  
    ds.id,
    substation_id substationId,
    s.name substationName,
    panelboard_id panelBoardId,
    p.name panelboardName,
    switchboard_id switchBoardId,
    sw.name switchboardName,
    department_id departmentId,
    d.name departmentName,
    manufacturing_process_id manufacturingProcessId,
    mp.name manufacturingProcessName,
    description
    FROM department_substation ds
    inner join substation s on ds.substation_id = s.id
    inner join panelboards p on ds.panelboard_id = p.id
    inner join switchboards sw on ds.switchboard_id = sw.id
    inner join departments d on ds.department_id = d.id
    inner join manufacturing_process mp on ds.manufacturing_process_id = mp.id
    order by ds.id
    `;
    const queryResult = await conn.query(query);
    res.json({
      code: 1,
      value: queryResult,
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
router.get('/setting/equipments', async (req, res) => {
  let conn;
  try {
    conn = await Pool.getConnection();
    const query = `
    SELECT  
    id,
    description
    FROM 
    department_substation`;
    const queryResult = await conn.query(query);
    res.json({
      code: 1,
      value: queryResult,
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

router.post('/setting/location', async (req, res) => {
  let conn;
  try {
    conn = await Pool.getConnection();
    const id = req.params.equipmentId;
    const substationId = req.body.substationId;
    const panelBoardId = req.body.panelBoardId;
    const switchBoardId = req.body.switchBoardId;
    const departmentId = req.body.departmentId;
    const manufacturingProcessId = req.body.manufacturingProcessId;
    const description = req.body.description;
    let idValue = await conn.query(`SHOW TABLE STATUS FROM \`bom\` WHERE \`name\` LIKE 'department_substation';`);
    idValue = idValue[0].Auto_increment
    let unit = '';

    const query = ` 
    insert into 
department_substation(
\`substation_id\`,
\`panelboard_id\`,
\`switchboard_id\`,
\`department_id\`,
\`manufacturing_process_id\`,
\`description\`,
\`tag_unit\`) 
values
( ${substationId},
 ${panelBoardId},
 ${switchBoardId},
 ${departmentId},
 ${manufacturingProcessId},
 '${description}',
 'U${String(idValue).padStart(3-String(idValue).length, '0')}')
 `;
    await conn.query(query);

    // 완료 후 메시지전송
    res.json({
      code: 1,
      message: process.env.DB_INSERT_MSG,
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

router.put('/setting/location/:locationId', async (req, res) => {
  let conn;
  try {
    conn = await Pool.getConnection();
    const id = req.params.locationId;
    const substationId = req.body.substationId;
    const panelBoardId = req.body.panelBoardId;
    const switchBoardId = req.body.switchBoardId;
    const departmentId = req.body.departmentId;
    const manufacturingProcessId = req.body.manufacturingProcessId;
    const description = req.body.description;

    const query = ` 
    UPDATE 
    department_substation 
    SET 
substation_id = ${substationId},
panelboard_id = ${panelBoardId},
switchboard_id = ${switchBoardId},
department_id = ${departmentId},
manufacturing_process_id = ${manufacturingProcessId},
description = '${description}'
    WHERE id=${id}`;
    await conn.query(query);

    // 완료 후 메시지전송
    res.json({
      code: 1,
      message: process.env.DB_UPDATE_MSG,
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

// 모듈로 사용할 수 있도록 export
module.exports = router;
