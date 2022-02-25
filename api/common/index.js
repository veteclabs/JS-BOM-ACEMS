const express = require('express');

const router = express.Router();
const pool = require('../../config/database');



router.get('/common/equipment/list', async (req, res) => {
    let conn;
    try {
        conn = await pool.getConnection();
        let query;
        const {all} = req.query;
        if(all === 'true') {
            query = `
select if(wt.tagname like '%AU%','AU',left(wt.tagname,4)) as code, eq.name as n
from webaccess_tags wt
inner join equipment_mgmt eq on wt.equipment_mgmt_id=eq.id
group by \`code\`,n;

select wt.tagname as code, eq.name as n
from webaccess_tags wt
inner join equipment_mgmt eq on wt.equipment_mgmt_id=eq.id;
        `;
        }else {
            query = `
select left(wt.tagname,4) as code, eq.name as n
from webaccess_tags wt
inner join equipment_mgmt eq on wt.equipment_mgmt_id=eq.id
WHERE eq.name  != '전체'
group by \`code\`,n;

select wt.tagname as code, eq.name as n
from webaccess_tags wt
inner join equipment_mgmt eq on wt.equipment_mgmt_id=eq.id;
        `;
        }
        const result = await conn.query(query);
        res.json({
            code: 1,
            result: result[0]
        });
    } catch (error) {
        res.json({
            code: 2,
            message: process.env.DB_ERROR_MSG,
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});

router.get('/common/department-substation/:id', async (req, res, next) => {
    let conn;
    try {
        const departmentId = req.params.id;
        let query = ''
        conn = await pool.getConnection();
        query = `SELECT substation_id, substation_name, department_id, department_name
    FROM v_department_substation
    #WHERE department_id = ${departmentId}
    GROUP BY substation_id, substation_name;`;

        const result = await conn.query(query);

        res.json({
            status: 200,
            code:1,
            result
        });

    } catch (error) {
        res.json({
            code: 0,
            msg: process.env.DB_ERROR_MSG,
            error
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});
router.get('/common/department-substations', async  (req, res, next) => {
    let conn;
    try {

        let query = ''
        conn = await pool.getConnection();

        query = `SELECT substation_id, substation_name, department_id, department_name
                            FROM v_department_substation
                            GROUP BY substation_id, substation_name;`;
        const result = await conn.query(query);
        res.json({
            code: 1,

            result
        });

    } catch (error) {
        res.json({
            code: 0,
            msg: process.env.DB_ERROR_MSG,
            error
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});

router.get('/common/departments', async  (req, res, next) => {
    let conn;
    try {

        let query = ''
        conn = await pool.getConnection();

        query = `
            select 
            dp.id, 
            dp.name 
            from departments dp
            where dp.name != 'SP'
            and dp.name != '-'`;
        const result = await conn.query(query);
        res.json({
            code: 1,

            result
        });

    } catch (error) {
        res.json({
            code: 0,
            msg: process.env.DB_ERROR_MSG,
            error
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});
// 모듈로 사용할 수 있도록 export
module.exports = router;
