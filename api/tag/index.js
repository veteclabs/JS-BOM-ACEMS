const express = require('express');

const router = express.Router();
const request = require('request');
const btoa = require('btoa');
const pool = require('../../config/database');
const dataPool = require('../../config/dataDatabase');
require('dotenv').config();

// WEBACCESS-Login-Function

//부서별 tag
router.get('/tags/departmant/:departmentId', async (req, res) => {

    let conn;
    try {
        conn = await dataPool.getConnection();
        let hourElecQuery = `
select group_concat(b.tag) as tagname
from (SELECT concat('\\\'', tagname, '\\\'') as tag
FROM v_department_substation
WHERE department_id = ${req.params.departmentId}
GROUP BY department_id, tagname) as \`b\`
`;

        let data = await dataPool.query(hourElecQuery);
        // (hourElec)
        res.json({code: 1, data});
    } catch (error) {
        (error)
        res.json({

            message: '응답이 없습니다. 새로고침 후 다시 시도하시기 바랍니다.',
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});


// tag list
router.get('/tags', async (req, res) => {
    let conn;
    try {
        conn = await pool.getConnection();

        const query = `
select group_concat(b.tag) as tagname
from (SELECT concat('\\'', tagname, '\\'') as tag
FROM v_department_substation) as b
        `;
        const data = await conn.query(query);
        // (queryResult)
        res.json({code: 1, data});
    } catch (error) {
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
