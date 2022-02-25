const express = require('express');

const router = express.Router();
const dataPool = require('../../config/dataDatabase');
const dayjs = require('dayjs');


/*
변전실 별, 부서 별, 공정 별
 */
// 히스토리검색
router.get('/analysis/data', async (req, res) => {
    let conn;
    try {
        conn = await dataPool.getConnection();
        const equipmentName = req.query.equipmentName;
        const timeType = req.query.timeType;
        const usageType = req.query.usageType;
        const substationId = req.query.substationId;
        const departmentId = req.query.departmentId;
        const processId = req.query.processId;
        const equipmentId = req.query.location;

        let substationIfWhere = '';
        let departmentIfWhere = '';
        let processIfWhere = '';
        let equipmentIfWhere = '';

        let tagNames;
        let tagName = '';
        let date = {};
        if(usageType === 'Usage') {
            tagName = 'PWR_KWh'
        }else if(usageType ==='PF') {
            tagName = 'PWR_PF'
        }

        if (substationId !== undefined) {
            substationIfWhere = `AND substation_id = ${substationId}`
        }
        if (departmentId !== undefined) {
            departmentIfWhere = `AND department_id = ${departmentId}`
        }
        if (processId !== undefined) {
            processIfWhere = `AND manufacturing_process_id = ${processId}`
        }
        if (equipmentId !== undefined) {
            equipmentIfWhere = `AND id = ${equipmentId}`
        }
        tagNames = await conn.query(`
        select group_concat(b.tag) as tagname
        from (SELECT concat('\\\'', tagname, '\\\'') as tag
        FROM v_department_substation
        WHERE description not like '%메인%' 
        ${substationIfWhere} 
        ${departmentIfWhere} 
        ${processIfWhere} 
        ${equipmentIfWhere}
        GROUP BY department_id, tagname) as \`b\`
        `);
        tagNames = tagNames[0].tagname
        date.start = req.query.start;
        date.end =req.query.end;
        if (req.query.date !== undefined) {
            date = JSON.parse(req.query.date)
        }

        if (timeType === 'H' || timeType==='15min') {
            date.end = dayjs(date.start).add(1, 'days').format('YYYY-MM-DD');
            date.beforeDate = dayjs(date.start).subtract(1, 'days').format('YYYY-MM-DD');
            date.lyStartDate = dayjs(date.start).subtract(1, 'years').format('YYYY-MM-DD');
            date.lyEndDate = dayjs(date.end).subtract(1, 'years').format('YYYY-MM-DD');
        }
        if (timeType === 'D') {
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
SELECT if(s.h = 0, 24, s.h)        AS \`H\`,
ifnull(shr.\`USAGE\`, 0)        AS \`${usageType}\`,
ifnull(\`bf\`.\`USAGE\`, 0) AS \`beforeDay${usageType}\`,
ifnull(\`LY\`.\`USAGE\`, 0)     AS \`beforeYear${usageType}\`
FROM (SELECT logvalue AS h FROM \`tagdata\`.sources_hour_with_day WHERE logvalue >= 0 AND logvalue <= 23) s
LEFT JOIN
(SELECT HOUR(\`TIMESTAMP\` + INTERVAL 1 HOUR) h, SUM(\`USAGE\`) \`USAGE\`
FROM \`tagdata\`.history_hour
WHERE tagname in (${tagNames})
AND timestamp >= '${date.start}'
AND timestamp < '${date.end}'
group by hour(\`TIMESTAMP\`)
) shr ON shr.h = s.h
LEFT JOIN
(SELECT HOUR(\`TIMESTAMP\` + INTERVAL 1 HOUR) h, SUM(\`USAGE\`) \`USAGE\`
FROM \`tagdata\`.history_hour
WHERE tagname in (${tagNames})
AND timestamp >= '${date.beforeDate}'
AND timestamp < '${date.start}'
group by hour(\`TIMESTAMP\`)
) \`bf\` ON \`bf\`.h = s.h
LEFT JOIN
(SELECT HOUR(\`TIMESTAMP\` + INTERVAL 1 HOUR) h, SUM(\`USAGE\`) \`USAGE\`
FROM \`tagdata\`.history_hour
WHERE tagname in (${tagNames})
AND timestamp >= '${date.lyStartDate}'
AND timestamp < '${date.lyEndDate}'
group by hour(\`TIMESTAMP\`)
) \`LY\` ON \`LY\`.h = s.h
ORDER BY if(s.h = 0, 24, s.h)`;
        }else if(timeType === '15min' && (usageType ==='Usage' || usageType ==='PF')) {
            let PFAvgSql;
            if(usageType === 'PF') {
                PFAvgSql = `if(tagname like '%PF%', avg(\`usage\`),sum(\`usage\`)) AS kWh,
                            if(tagname like '%PF%', avg(\`usage\`),sum(\`usage\`)*4) AS kW`
            }else {
                PFAvgSql =`SUM(\`usage\`) AS kWh, SUM(\`usage\`)*4 as kW`
            }
            query = `
SELECT if(s.f='00:00', '24:00:00',s.f) AS \`H\`,
IFNULL(f.kWh,0) AS \`${usageType}\`,
IFNULL(f.kW,0) AS \`kW\`,
IFNULL(b.kWh,0) AS \`beforeDay${usageType}\`,
IFNULL(b.kW,0) AS \`beforekW\`
FROM
(select logvalue as f from \`tagdata\`.sources_quarter_past) s
left join (SELECT time(from_unixtime(ceil(UNIX_TIMESTAMP(\`timestamp\`)/900)*900))t,tagname AS tagname, 
${PFAvgSql}
FROM \`tagdata\`.history
WHERE tagname in (${tagNames}) AND 
\`timestamp\` > '${date.start}' AND 
\`timestamp\` <= '${date.end}'
group by t) f on s.f = f.t
left join (SELECT time(from_unixtime(ceil(UNIX_TIMESTAMP(\`timestamp\`)/900)*900))t,tagname AS tagname, 
${PFAvgSql}
FROM \`tagdata\`.history
WHERE tagname in (${tagNames}) AND 
\`timestamp\` > '${date.beforeDate}' AND 
\`timestamp\` <= '${date.start}'
group by t) b on s.f = b.t
ORDER BY \`H\``;

        }else if(timeType === 'D'&& (usageType ==='Usage' || usageType ==='PF')) {
            query = `
SELECT s.d AS \`D\`, ifnull(shr.\`USAGE\`, 0) \`${usageType}\`, ifnull(bf.\`USAGE\`, 0) \`beforeMonth${usageType}\`, ifnull(ly.\`USAGE\`, 0) \`beforeYear${usageType}\`
FROM (SELECT logvalue AS d
FROM \`tagdata\`.sources_hour_with_day
WHERE logvalue >= 1
AND logvalue <= last_day('${date.start}-01')) s
LEFT JOIN
(SELECT day(\`TIMESTAMP\`) d,  SUM(\`USAGE\`) \`USAGE\`
FROM \`tagdata\`.history_day
WHERE tagname in (${tagNames})
AND timestamp >= '${date.start}-01'
AND timestamp < '${date.start}-${date.lastday}'
group by day(\`TIMESTAMP\`)
) shr ON shr.d = s.d
LEFT JOIN
(SELECT day(\`TIMESTAMP\`) d, SUM(\`USAGE\`) \`USAGE\`
FROM \`tagdata\`.history_day
WHERE tagname in (${tagNames})
AND timestamp >= '${date.beforeDate}-01'
AND timestamp < '${date.beforeDate}-${date.beforeLastDay}'
group by day(\`TIMESTAMP\`)
) \`bf\` ON bf.d = s.d
LEFT JOIN
(SELECT day(\`TIMESTAMP\`) d,  SUM(\`USAGE\`) \`USAGE\`
FROM \`tagdata\`.history_day
WHERE tagname in (${tagNames})
AND timestamp >= '${date.lyStartDate}-01'
AND timestamp < '${date.lyStartDate}-${date.lastday}'
group by day(\`TIMESTAMP\`)
) ly ON ly.d = s.d
ORDER BY s.d ASC;
`;
        }else if(timeType === 'M'&& (usageType ==='Usage' || usageType ==='PF')) {
            query = `
SELECT s.m AS \`M\`, ifnull(shr.\`USAGE\`, 0) \`${usageType}\`, ifnull(ly.\`USAGE\`, 0) \`beforeYearMonth${usageType}\`
FROM (SELECT logvalue AS m FROM \`tagdata\`.sources_hour_with_day WHERE logvalue >= 1 AND logvalue <= 12) s
LEFT JOIN
(SELECT month(\`TIMESTAMP\`) m, 
if(tagname like '%PF%', avg(\`usage\`),sum(\`usage\`)) \`USAGE\`
FROM \`tagdata\`.history_day
WHERE tagname in (${tagNames})
AND timestamp >= '${date.start}-01-01'
AND timestamp < '${date.nextYear}-01-01'
GROUP BY m
) shr ON shr.m = s.m
LEFT JOIN
(SELECT month(\`TIMESTAMP\`) m, 
if(tagname like '%PF%', avg(\`usage\`),sum(\`usage\`)) \`USAGE\`
FROM \`tagdata\`.history_day
WHERE tagname in (${tagNames})
AND timestamp >= '${date.beforeYear}-01-01'
AND timestamp < '${date.start}-01-01'
GROUP BY m
) ly ON ly.m = s.m
ORDER BY s.m ASC;
`
        }else if(timeType === 'Y'&& (usageType ==='Usage' || usageType ==='PF')) {
            query= `
SELECT year(\`timestamp\`) Y, 
if(tagname like '%PF%', avg(\`usage\`),sum(\`usage\`)) \`${usageType}\`
FROM \`tagdata\`.history_day
where tagname in (${tagNames})
group by Y; `;
        }
        else if(timeType === 'H' && (usageType ==='TOE' || usageType ==='tCo2')) {
            query = `
SELECT if(s.h = 0, 24, s.h)        AS \`H\`,
ROUND,5)(IFNULL(((\`shr\`.\`usage\` * 0.001) * (formula.${usageType})), 0), 5) AS \`${usageType}\`,
ROUND(IFNULL(((\`bf\`.\`usage\` * 0.001) * (formula.${usageType})), 0), 5) AS \`beforeDay${usageType}\`,
ROUND(IFNULL(((\`LY\`.\`usage\` * 0.001) * (formula.${usageType})), 0), 5) AS \`beforeYear${usageType}\`
FROM 
(SELECT \`toe\`,\`tco2\` FROM \`tagdata\`.energy WHERE id=28) formula,
(SELECT logvalue AS h FROM \`tagdata\`.sources_hour_with_day WHERE logvalue >= 0 AND logvalue <= 23) s
LEFT JOIN
(SELECT HOUR(\`TIMESTAMP\` + INTERVAL 1 HOUR) h,  SUM(\`USAGE\`) \`USAGE\`
FROM \`tagdata\`.history_hour
WHERE tagname in (${tagNames})
AND timestamp >= '${date.start}'
AND timestamp < '${date.end}'
group by hour(\`TIMESTAMP\`)
) shr ON shr.h = s.h
LEFT JOIN
(SELECT HOUR(\`TIMESTAMP\` + INTERVAL 1 HOUR) h, SUM(\`USAGE\`) \`USAGE\`
FROM \`tagdata\`.history_hour
WHERE tagname in (${tagNames})
AND timestamp >= '${date.beforeDate}'
AND timestamp < '${date.start}'
group by hour(\`TIMESTAMP\`)
) \`bf\` ON \`bf\`.h = s.h
LEFT JOIN
(SELECT HOUR(\`TIMESTAMP\` + INTERVAL 1 HOUR) h, SUM(\`USAGE\`) \`USAGE\`
FROM \`tagdata\`.history_hour
WHERE tagname in (${tagNames})
AND timestamp >= '${date.lyStartDate}'
AND timestamp < '${date.lyEndDate}'
group by hour(\`TIMESTAMP\`)
) \`LY\` ON \`LY\`.h = s.h
ORDER BY if(s.h = 0, 24, s.h)`;

        }else if(timeType === '15min' && (usageType ==='TOE' || usageType ==='tCo2')) {
            query = `
select if(s.f='00:00', '24:00:00',s.f) H,
ROUND(IFNULL(((\`kWh\` * 0.001) * (formula.${usageType})), 0), 5) AS \`${usageType}\`
FROM
(SELECT \`toe\`,\`tco2\` FROM \`tagdata\`.energy WHERE id=28) formula,
(select logvalue as f from \`tagdata\`.sources_quarter_past) s
left join (SELECT time(from_unixtime(ceil(UNIX_TIMESTAMP(\`timestamp\`)/900)*900))t,tagname AS tagname, SUM(\`usage\`) AS kWh, SUM(\`usage\`)*4 as kW
FROM \`tagdata\`.history
WHERE tagname in (${tagNames})
AND timestamp >= '${date.start}'
AND timestamp < '${date.end}'
group by t) f on s.f = f.t
order by H
`;

        }else if(timeType === 'D' && (usageType ==='TOE' || usageType ==='tCo2')) {
            query = `
SELECT s.d AS \`D\`, 
ROUND(IFNULL(((\`shr\`.\`usage\` * 0.001) * (formula.${usageType})), 0), 5) AS \`${usageType}\`,
ROUND(IFNULL(((\`bf\`.\`usage\` * 0.001) * (formula.${usageType})), 0), 5) AS \`beforeDay${usageType}\`,
ROUND(IFNULL(((\`LY\`.\`usage\` * 0.001) * (formula.${usageType})), 0), 5) AS \`beforeYear${usageType}\`
FROM 
(
SELECT \`toe\`,\`tco2\`
FROM \`tagdata\`.energy
WHERE id=28) formula,
(
SELECT logvalue AS d
FROM \`tagdata\`.sources_hour_with_day
WHERE logvalue >= 1 AND logvalue <= LAST_DAY('${date.start}-01')) s
LEFT JOIN
(SELECT day(\`TIMESTAMP\`) d,  SUM(\`USAGE\`) \`USAGE\`
FROM \`tagdata\`.history_day
WHERE tagname in (${tagNames})
AND timestamp >= '${date.start}-01'
AND timestamp < '${date.start}-${date.lastday}'
group by day(\`TIMESTAMP\`)
) shr ON shr.d = s.d
LEFT JOIN
(SELECT day(\`TIMESTAMP\`) d,  SUM(\`USAGE\`) \`USAGE\`
FROM \`tagdata\`.history_day
WHERE tagname in (${tagNames})
AND timestamp >= '${date.beforeDate}-01'
AND timestamp < '${date.beforeDate}-${date.beforeLastDay}'
group by day(\`TIMESTAMP\`)
) \`bf\` ON bf.d = s.d
LEFT JOIN
(SELECT day(\`TIMESTAMP\`) d,  SUM(\`USAGE\`) \`USAGE\`
FROM \`tagdata\`.history_day
WHERE tagname in (${tagNames})
AND timestamp >= '${date.lyStartDate}-01'
AND timestamp < '${date.lyStartDate}-${date.lastday}'
group by day(\`TIMESTAMP\`)
) ly ON ly.d = s.d
ORDER BY s.d ASC;
`;
        }else if(timeType === 'M' && (usageType ==='TOE' || usageType ==='tCo2')) {
            query = `
SELECT s.m AS \`M\`, 
ROUND(IFNULL(((\`shr\`.\`usage\` * 0.001) * (formula.${usageType})), 0), 5) AS \`${usageType}\`,
ROUND(IFNULL(((\`LY\`.\`usage\` * 0.001) * (formula.${usageType})), 0), 5) AS \`beforeYearMonth${usageType}\`
FROM 
(
SELECT \`toe\`,\`tco2\`
FROM \`tagdata\`.energy
WHERE id=28) formula,

(SELECT logvalue AS m FROM \`tagdata\`.sources_hour_with_day WHERE logvalue >= 1 AND logvalue <= 12) s
LEFT JOIN
(SELECT month(\`TIMESTAMP\`) m, sum(\`USAGE\`) \`USAGE\`
FROM \`tagdata\`.history_day
WHERE tagname in (${tagNames})
AND timestamp >= '${date.start}-01-01'
AND timestamp < '${date.nextYear}-01-01'
GROUP BY m
) shr ON shr.m = s.m
LEFT JOIN
(SELECT month(\`TIMESTAMP\`) m, sum(\`USAGE\`) \`USAGE\`
FROM \`tagdata\`.history_day
WHERE tagname in (${tagNames})
AND timestamp >= '${date.beforeYear}-01-01'
AND timestamp < '${date.start}-01-01'
GROUP BY m
) ly ON ly.m = s.m
ORDER BY s.m ASC;
`;
        }else if(timeType === 'Y' && (usageType ==='TOE' || usageType ==='tCo2')) {
            query= `
SELECT year(\`timestamp\`) y, 
ROUND(IFNULL((sum(\`usage\`) * 0.001) * (formula.${usageType}), 0), 5) AS \`${usageType}\`
FROM (
SELECT \`toe\`,\`tco2\`
FROM \`tagdata\`.energy
WHERE id=28) formula,
\`tagdata\`.history_day
where tagname in (${tagNames})
group by Y; `;
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
