const express = require('express');

const router = express.Router();
const request = require('request');
const btoa = require('btoa');
const pool = require('../../config/database');
const dataPool = require('../../config/dataDatabase');
require('dotenv').config();

// WEBACCESS-Login-Function

// 사용자목표피크업데이트
router.post('/dashboard/changeTargetPeak', async (req, res) => {
    try {
        conn = await dataPool.getConnection();
        const usageType = req.query.usageType;



        let tagNames;
        let tagName = '';



        tagNames = await conn.query(`
        select group_concat(b.tag) as tagname
        from (SELECT concat('\\\'', tagname, '\\\'') as tag
        FROM v_department_substation
        WHERE description not like '%메인%' 

        GROUP BY department_id, tagname) as \`b\`
        `);

        request({
            url: url,
            method: 'POST',
            headers: {
                'Authorization': 'Basic ' + userPass,
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=utf-8',
                'LoginType': 'View'
            },
            body: tagObject,
            json: true
        }, function (error, response, body) {
            res.json({
                code: 1,
                msg: '목표 전력 수정이 완료되었습니다.',
            });
        });
    } catch (error) {
        res.json({
            message: '응답이 없습니다. 새로고침 후 다시 시도하시기 바랍니다.',
        });
    }
});

// 모듈로 사용할 수 있도록 export
module.exports = router;
