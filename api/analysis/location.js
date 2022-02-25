const express = require('express');

const router = express.Router();
const dataPool = require('../../config/dataDatabase');
const dayjs = require('dayjs');


/*
변전실 별, 부서 별, 공정 별
 */
// 히스토리검색
router.get('/analysis/location', async (req, res) => {
    let conn;
    try {
        conn = await dataPool.getConnection();
        const equipmentName = req.query.equipmentName;
        const timeType = req.query.timeType;
        let usageType = req.query.usageType;
        const substationId = req.query.substationId;
        const departmentId = req.query.departmentId;
        const processId = req.query.processId;
        const equipmentId = req.query.equipmentId;
        let type = '';


        let locations;
        let tagName = '';
        let date = {};
        usageType ='Usage'
        if(usageType === 'Usage') {
            tagName = 'PWR_KWh'
        }else if(usageType ==='PF') {
            tagName = 'PWR_PF'
        }


        locations = await conn.query(`
        select 
        v.id locationId,
        v.description locationName,
        v.department_name departmentName,
        v.substation_name subStationName,
        v.manufacturing_process_name processName,
        v.tagname
        FROM v_department_substation v
        WHERE description not like '%메인%'
        order BY v.id asc
        `);
        let tagNames = [];
        for (let a of locations) {
            tagNames.push(`"` + a.tagname + `"`);
        }
        date.start = req.query.start;
        date.end =req.query.end;
        date = JSON.parse(req.query.date)


        let s;
        let l;
        if (timeType === 'D') {
            s = 1;
            l = dayjs(date.start).daysInMonth();
            type = 'D'
        } else if (timeType === 'M') {
            s = 1;
            l = 12;
            type = 'M'
        }


        let query;
        if(timeType === 'H' && (usageType ==='Usage' || usageType ==='PF')) {

            query = `
SELECT
HOUR(\`TIMESTAMP\`) h, 
tagname,
\`USAGE\` \`Usage\`
FROM \`tagdata\`.history_hour
WHERE tagname in (${tagNames})
AND timestamp >= '${date.start}'
AND timestamp < '${date.end}'
order by hour(timestamp)
`;

        }else if(timeType === 'D'&& (usageType ==='Usage' || usageType ==='PF')) {
            query = `
SELECT
day(\`TIMESTAMP\`) D, 
tagname,
\`USAGE\` \`Usage\`
FROM \`tagdata\`.history_hour
WHERE tagname in (${tagNames})
AND timestamp >= '${date.start}-01'
AND timestamp < '${date.end}-01'
group by tagname, day(timestamp)
order by day(timestamp)
`;
        }else if(timeType === 'M'&& (usageType ==='Usage' || usageType ==='PF')) {
            query = `
SELECT
month(\`TIMESTAMP\`) M, 
tagname,
\`USAGE\` \`Usage\`
FROM \`tagdata\`.history_hour
WHERE tagname in (${tagNames})
AND timestamp >= '${date.start}-01'
AND timestamp < '${date.end}-01'
group by tagname, month(timestamp)
order by month(timestamp)
`
        }else if(timeType === 'Y'&& (usageType ==='Usage' || usageType ==='PF')) {
            query= `
SELECT year(\`timestamp\`) Y, 
if(tagname like '%PF%', avg(\`usage\`),sum(\`usage\`)) \`${usageType}\`
FROM \`tagdata\`.history_day
where tagname in (${tagNames})
group by Y; `;
        }


        const queryResult = await conn.query(query);
        let historySet = {};
        for (let dataSet of queryResult) {

            if (historySet[dataSet.tagname] === undefined) {
                historySet[dataSet.tagname]= [];
            } else {
                // delete dataSet.tagname
                historySet[dataSet.tagname].push(dataSet);
            }
        }
        for (let location of locations) {
            let dataSet = historySet[location.tagname];

            for (let d = s; d<=l;d++) {
                let check = false;
                let name;
                for (let ds in dataSet) {
                    if (dataSet[ds][type] == d) {
                        check = true;

                        break;
                    }
                    name = dataSet[ds].tagname
                }
                if (check == false) {
                    let _s = {}
                    _s[type] = d;
                    _s.tagname = name;
                    _s.Usage = 0;
                    if (dataSet !== undefined) {
                        dataSet.push(_s)
                    }

                }
            }
            if (dataSet !== undefined) {
                dataSet = dataSet.sort((a,b) => a[type]- b[type])
            }
            location['dataSet'] = dataSet
            delete location.tagname
        }
        res.json({
            code: 1,
            value: locations,
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
