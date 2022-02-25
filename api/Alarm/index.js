const express = require('express');

const router = express.Router();
const dataPool = require('../../config/dataDatabase');

// 알람검색
router.get('/alarms', async (req, res) => {
    let conn;
    try {
        conn = await dataPool.getConnection();

        const {today} = req.query;

        const {page} = req.query;
        const {pageSize} = req.query;


        let limitOfset = ''
        let todayWhereIf = ''
        let departmentWhereIf = ''

        let userAuth = req.session.authUser;
        let departmentId = userAuth.departmentId;
        let isList = ``
        if (page !== undefined && pageSize !== undefined) {
            limitOfset = `limit ${page}, ${pageSize}`
        }
        if (today !== undefined) {
            if (today === "true") {
                todayWhereIf = `AND date(a.timestamp_type) = date(now()) `
                isList = `GROUP BY ds.substation_id`;
            } else {
                isList = ``
            }
        }
        if (userAuth.ID !== "조두래") {
            departmentWhereIf = `AND ds.department_id = ${departmentId} `
        }


        let query = `
        SELECT 
        DATE(a.createdAt) \`date\`,
        	TIME(a.createdAt) \`time\`,
         a.alarm_type,
         a.substation_name substationName,
         a.description
        FROM \`tagdata\`.alarms_substation a
        LEFT JOIN v_department_substation ds ON  ds.substation_id = a.substation_id
        WHERE TRUE
        ${todayWhereIf} 
        ${departmentWhereIf}
        ${isList}
        ORDER BY DATE(a.createdAt) desc, TIME(a.createdAt) desc
        ${limitOfset} 
        `;
        let queryResult = await conn.query(query);
        if (today === "false") {
            query = `
            SELECT
            distinct date(a.createdAt) \`date\`, time(a.createdAt) \`time\`, a.alarm_type, a.description
                    FROM \`tagdata\`.alarms_substation a
                    LEFT JOIN v_department_substation ds ON  ds.substation_id = a.substation_id
                    WHERE TRUE
            ${departmentWhereIf}
            ORDER BY DATE(a.createdAt) desc, TIME(a.createdAt) desc
            ${limitOfset} 
            `;

            queryResult = await conn.query(query);
            query = `
            SELECT 
            DATE(a.timestamp) \`date\`,
        	TIME(a.timestamp) \`time\`,
            a.alarm_type,
            ds.substation_name substationName,
            a.description
            FROM \`tagdata\`.alarms a
            LEFT JOIN v_department_substation ds ON  ds.tag_unit = left(a.tagname, 4)
            WHERE TRUE
            ${todayWhereIf} 
            ${departmentWhereIf}
            ORDER BY DATE(a.timestamp) desc, TIME(a.timestamp) desc
            ${limitOfset} 
            `;
            const alarmResult = await conn.query(query);
            queryResult.push(...alarmResult)
        }
        for (let idx in queryResult ) {
            queryResult[idx].id = Number(idx) + 1
        }
        queryResult = queryResult.sort((a,b) => {
            return new Date(b.date) - new Date(a.date);
        })
        queryResult = queryResult.sort((a,b) => {
            return new Date(b.time) - new Date(a.time);
        })
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


router.get('/substation/alarm', async (req, res) => {
    let conn;
    try {
        conn = await dataPool.getConnection();
        const query = `
        SELECT 
        v.substation_id substationId, 
        v.substation_name substationName, 
        max(ifnull(isAlarm, FALSE)) isAlarm 
        FROM v_department_substation v 
        LEFT JOIN (select 
            DISTINCT tagname, 
            a.substation_id substation_id,
            TRUE isAlarm 
            FROM \`tagdata\`.alarms_substation a 
            WHERE a.createdAt >= DATE(now())
            and a.createdAt < date_add(DATE(now()),INTERVAL 1 DAY)) aa ON aa.substation_id = v.substation_id
        GROUP BY v.substation_id
`;
        const queryResult = await conn.query(query);
        for (let substation of queryResult) {
            substation.isAlarm = substation.isAlarm == 1? true : false;
        }
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


// 모듈로 사용할 수 있도록 export
module.exports = router;
