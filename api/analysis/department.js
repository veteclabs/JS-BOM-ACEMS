const express = require('express');

const router = express.Router();
const dataPool = require('../../config/dataDatabase');
const dayjs = require('dayjs');


/*
변전실 별, 부서 별, 공정 별
 */
// 히스토리검색
router.get('/analysis/department', async (req, res) => {
    let conn;
    try {
        conn = await dataPool.getConnection();
        const equipmentName = req.query.equipmentName;
        const timeType = req.query.timeType;
        const usageType = req.query.usageType;
        const substationId = req.query.substationId;
        const departmentId = req.query.department;
        const processId = req.query.processId;

        const equipmentId = req.query.equipmentId;

        let substationIfWhere = '';
        let departmentIfWhere = '';
        let processIfWhere = '';
        let equipmentIfWhere = '';
        let departmentGroupBy = '';
        let selectList = '';

        let tagNames;
        let tagName = '';
        let date = {};
        if(usageType === 'Usage') {
            tagName = 'PWR_KWh'
        }else if(usageType ==='PF') {
            tagName = 'PWR_PF'
        }

        if (substationId !== undefined) {
            substationIfWhere = `AND v.substation_id = ${substationId}`
        }
        if (departmentId !== undefined && departmentId !=='AU') {
            departmentIfWhere = `AND v.department_id = ${departmentId}`
            departmentGroupBy = `group by v.id \n order by v.id asc`;
            selectList = 'department_name departmentName, v.description locationName, ';

        } else {
            departmentGroupBy = `group by v.department_id \n order by v.department_id asc`;
            selectList = 'v.department_name departmentName, ';
        }

        if (processId !== undefined) {
            processIfWhere = `AND v.manufacturing_process_id = ${processId}`
        }
        if (processId !== undefined) {
            equipmentIfWhere = `AND id = ${equipmentId}`
        }
        date.start = req.query.start;
        date.end =req.query.end;
        date = JSON.parse(req.query.date)
        if (timeType === 'H' || timeType==='15min') {
            date.end = dayjs(date.start).add(1, 'days').format('YYYY-MM-DD');
            date.beforeDate = dayjs(date.start).subtract(1, 'days').format('YYYY-MM-DD');
            date.lyStartDate = dayjs(date.start).subtract(1, 'years').format('YYYY-MM-DD');
            date.lyEndDate = dayjs(date.end).subtract(1, 'years').format('YYYY-MM-DD');
        }
        if (timeType === 'D' || timeType === 'M') {
            date.lastday = dayjs(date.start).endOf('month').format('DD');
            date.beforeDate = dayjs(date.start).subtract(1, 'months').format('YYYY-MM');
            date.beforeLastDay = dayjs(date.beforeDate).endOf('month').format('DD');
            date.lyStartDate = dayjs(date.start).subtract(1, 'years').format('YYYY-MM');
        }if (timeType === 'M') {
            date.nextYear = dayjs(date.start).add(1,'years').format('YYYY');
            date.beforeYear = dayjs(date.start).subtract(1,'years').format('YYYY');
        }
        if (timeType === 'H' || timeType==='15min') {
            date.end = dayjs(date.start).add(1, 'days').format('YYYY-MM-DD');
            date.beforeDate = dayjs(date.start).subtract(1, 'days').format('YYYY-MM-DD');
            date.lyStartDate = dayjs(date.start).subtract(1, 'years').format('YYYY-MM-DD');
            date.lyEndDate = dayjs(date.end).subtract(1, 'years').format('YYYY-MM-DD');
        }
        if (timeType === 'D' || timeType === 'M') {
            date.lastday = dayjs(date.start).endOf('month').format('DD');
            date.beforeDate = dayjs(date.start).subtract(1, 'months').format('YYYY-MM');
            date.beforeLastDay = dayjs(date.beforeDate).endOf('month').format('DD');
            date.lyStartDate = dayjs(date.start).subtract(1, 'years').format('YYYY-MM');
        }if (timeType === 'M') {
            date.nextYear = dayjs(date.start).add(1,'years').format('YYYY');
            date.beforeYear = dayjs(date.start).subtract(1,'years').format('YYYY');
        }


        let query;
        if(timeType === 'H' && (usageType ==='Usage' || usageType ==='PF')) {

            query = `
SELECT 
${selectList}
      ifnull(SUM(d.\`usage\`), 0) as \`${usageType}\`,
      ifnull(SUM(omb.\`usage\`), 0) as \`beforeDay${usageType}\`
FROM v_department_substation v
left outer join tagdata.history_day d on d.tagname = v.tagname 
left outer join tagdata.history_day omb ON timestamp(date_add(d.timestamp, INTERVAL -1 day)) = omb.timestamp and omb.tagname = v.tagname 
WHERE description not LIKE '%메인%' 
#and department_id = id
and v.department_name != "-" 
and date('${date.start}') = d.timestamp
${substationIfWhere} 
${departmentIfWhere} 
${processIfWhere} 
${equipmentIfWhere} 
${departmentGroupBy}
            `;

        }else if(timeType === '15min' && (usageType ==='Usage' || usageType ==='PF')) {
            let PFAvgSql;
            if(usageType === 'PF') {
                PFAvgSql = `if(tagname like '%PF%', avg(\`usage\`),sum(\`usage\`)) AS kWh,
                            if(tagname like '%PF%', avg(\`usage\`),sum(\`usage\`)*4) AS kW`
            }else {
                PFAvgSql =`SUM(\`usage\`) AS kWh, SUM(\`usage\`)*4 as kW`
            }
            query = ``;

        }else if(timeType === 'D'&& (usageType ==='Usage' || usageType ==='PF')) {
            query = `
SELECT 
${selectList}
      ifnull(SUM(d.\`usage\`), 0) as \`${usageType}\`,
      ifnull(sum(om.\`usage\`), 0) as \`beforeMonth${usageType}\`
FROM v_department_substation v
left outer join tagdata.history_month d on d.tagname = v.tagname 
left outer join tagdata.history_month om on om.tagname = v.tagname 
WHERE description not LIKE '%메인%' 
and v.department_name != "-" 
and ${date.start.replace('-', '').substr(0,6)} = d.timestamp
and ${date.beforeDate.replace('-', '').substr(0,6)} = om.timestamp

${substationIfWhere} 
${departmentIfWhere} 
${processIfWhere} 
${equipmentIfWhere} 
${departmentGroupBy}
            `;

        }else if(timeType === 'M'&& (usageType ==='Usage' || usageType ==='PF')) {
            let dateStr = [];
            let beforeDate = []
            let beforeYeardateStr = date.beforeYear;
            let yearStr = date.start.substr(0,4);
            for (let i = 1; i < 13; i++) {
                let monthStr = '';

                if(i<10) {
                    monthStr = '0' + String(i);
                } else {
                    monthStr = String(i);
                }
                dateStr.push(`"` + yearStr + monthStr + `"`)
                beforeDate.push(`"` + beforeYeardateStr + monthStr + `"`)

            }
            query = `
SELECT 
${selectList}
      ifnull(SUM(d.\`usage\`), 0) as \`${usageType}\`,
      ifnull(sum(om.\`usage\`), 0) as \`beforeYear${usageType}\`
FROM v_department_substation v
left outer join tagdata.history_month d on d.tagname = v.tagname 
left outer join (SELECT * from tagdata.history_month hm 
    WHERE  hm.timestamp in (${beforeDate}) 
    ) om on om.tagname = v.tagname
WHERE description not LIKE '%메인%' 
and d.timestamp in (${dateStr}) 
and v.department_name != "-"
${substationIfWhere} 
${departmentIfWhere} 
${processIfWhere} 
${equipmentIfWhere} 
${departmentGroupBy}
            `;
        }
        const queryResult = await conn.query(query);
        res.json({
            code: 1,
            value: queryResult,
        });
    } catch (error) {
        res.json({
            code: 0,
            message: '응답이 없습니다. 새로고침 후 다시 시도하시기 바랍니다.',
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
