const express = require('express');

const router = express.Router();
const request = require('request');
const btoa = require('btoa');
const pool = require('../../config/database');
const dataPool = require('../../config/dataDatabase');
require('dotenv').config();

// WEBACCESS-Login-Function
router.get('/WaLogin', (req, res) => {
    const UserName = process.env.WA_ID;
    const Password = process.env.WA_PASSWORD;
    const userPass = btoa(`${UserName}:${Password}`);
    const url = `${process.env.WA_IP}/WaWebService/Json/Login`;
    request({
        method: 'get',
        url,
        headers: {
            Authorization: `Basic ${userPass}`,
            Accept: 'application/json',
            LoginType: 'View',
            'cache-control': 'no-cache',
            'Content-Type': 'application/json; charset=utf-8',
        },
    }, () => {
        res.json({
            code: 0,
            msg: '로그인완료',
        });
    });
});

// WEBACCESS DATA 가져오기 (대시보드용)
router.post('/dashboard/port/getTagValue', async (req, res) => {
    const myUserName = process.env.WA_ID;
    const myPassword = process.env.WA_PASSWORD;
    const myProjName = process.env.WA_PROJECT;
    const myNodeName = process.env.WA_NODE;
    const userPass = btoa(`${myUserName}:${myPassword}`);
    const getTagURL = `${process.env.WA_IP}/WaWebService/Json/TagList/${myProjName}/${myNodeName}`;
    const targetPortId = req.body.portId;

    const tagList = [];
    for (let i = 0; i < targetPortId.length; i += 1) {
        const url = `${getTagURL}/${targetPortId[i]}`;
        tagList.push(new Promise((resolve, reject) => {
            request({
                method: 'GET',
                url,
                headers: {
                    Authorization: `Basic ${userPass}`,
                },
                json: true,
            }, (error, response, body) => {
                if (error) {
                    reject(error);
                } else {
                    resolve(body.Tags);
                }
            });
        }));
    }
    // 태그리스트 가져오기
    const tagListResult = await Promise.all(tagList);

    // 태그리스트 dataParma 형태로 변경하기
    const dataParam = {};
    const tagObjectList = [];
    for (let i = 0; i < tagListResult.length; i += 1) {
        for (let x = 0; x < tagListResult[i].length; x += 1) {
            const tagObject = {};
            tagObject.Name = tagListResult[i][x].Name;
            tagObjectList.push(tagObject);
        }
    }
    dataParam.Tags = tagObjectList;

    const url = `${process.env.WA_IP}/WaWebService/Json/GetTagValue/${myProjName}`;
    request({
        url,
        method: 'POST',
        headers: {
            Authorization: `Basic ${userPass}`,
            Accept: 'application/json',
            'Content-Type': 'application/json; charset=utf-8',
        },
        body: dataParam,
        json: true,
    }, (err, response) => {
        if (err) throw new Error(err);
        res.json(response.body);
    });
});


// WEBACCESS DATA 가져오기 (대시보드용)
router.post('/dashboard/tag/getTagValue', async (req, res) => {
    const UserName = process.env.WA_ID;
    const Password = process.env.WA_PASSWORD;
    const URL = process.env.WA_IP;
    const userPass = btoa(`${UserName}:${Password}`);
    const myProjURL = `${URL}/WaWebService/Json/GetTagValue/${process.env.WA_PROJECT}`;

    const {tagList} = req.body;

    let dataParam = {};
    let tagObjectList = [];
    for (let i = 0; i < tagList.length; i++) {
        let tagObject = {};
        tagObject["Name"] = tagList[i].tagName;
        tagObjectList.push(tagObject);
    }

    dataParam["Tags"] = tagObjectList;

    request({
        url: myProjURL,
        method: 'POST',
        headers: {
            'Authorization': 'Basic ' + userPass,
            'Accept': 'application/json',
            'Content-Type': 'application/json; charset=utf-8'
        },
        body: dataParam,
        json: true
    }, function (error, response, body) {
        res.json(body);
    });
});



router.post('/setAirCompressor', async (req, res) => {
    try {
        const unit = req.body.unit;
        const stateValue = req.body.stateValue;
        const UserName = process.env.WA_ID;
        const Password = process.env.WA_PASSWORD;
        const userPass = btoa(`${UserName}:${Password}`);
        const url = `${process.env.WA_IP}/WaWebService/Json/SetTagValue/${process.env.WA_PROJECT}`;
        const tagObject = {};
        let data;

        if (req.body.reSetting) {
            if (stateValue === 'OFF') {
                data = [{
                    Name: `GM_${unit}_DO_OFF`,
                    Value: 0,
                }];
            } else if (stateValue === 'ON') {
                data = [{
                    Name: `GM_${unit}_DO_ON`,
                    Value: 0,
                }];
            }
        } else if (stateValue === 'OFF') {
            data = [{
                Name: `GM_${unit}_DO_OFF`,
                Value: 1,
            }];
        } else if (stateValue === 'ON') {
            data = [{
                Name: `GM_${unit}_DO_ON`,
                Value: 1,
            }];
        }

        tagObject.Tags = data;

        request({
            url,
            method: 'POST',
            headers: {
                Authorization: `Basic ${userPass}`,
                Accept: 'application/json',
                'Content-Type': 'application/json; charset=utf-8',
                LoginType: 'View',
            },
            body: tagObject,
            json: true,
        }, (error, response, body) => {
            res.json(body);
        });
    } catch (e) {
        res.json({
            code: 2,
            message: process.env.DB_ERROR_MSG,
        });
    }
});


router.get('/dashboard/departmant/:departmentId', async (req, res) => {

    let conn;
    try {
        conn = await dataPool.getConnection();
        let hourElecQuery = `
SELECT
substation_id,
substation_name,
id,
panelboard_id,
panelboard_name,
switchboard_id,
switchboard_name,
department_id,
department_name,
manufacturing_process_id,
manufacturing_process_name,
cost_center,
description,
breaker_spec,
ct_spec,
wire_capacity,
ct_serial_number,
tag_unit,
timestamp,
v.tagname as \`tagname\`,
ifnull(sum(\`usage\`),0) \`usage\`
FROM v_department_substation v
left outer join tagdata.history_day d on d.tagname = v.tagname and date(now()) = d.timestamp
where description not like '%메인%' and department_id = ${req.params.departmentId}
GROUP BY manufacturing_process_id
order by \`usage\` desc;`;

        let data = await dataPool.query(hourElecQuery);
        for (let d in data) {
            data[d].rank = Number(d) + 1;
        }
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


router.get('/dashboard/floating', async (req, res) => {

    let conn;
    try {
        conn = await dataPool.getConnection();
        let resultSet = {};
        let {departmentId} = req.query;

        let departmentIfWhere = '';
        let tagNames;

        if (departmentId !== undefined) {
            departmentIfWhere = `AND department_id = ${departmentId}`
        }
        if (departmentId === undefined) {
            tagNames = `'U216_PWR_KWh'`
        } else {
            let tagNameQuery = `
                select group_concat(b.tag) as tagname
                from (SELECT concat('\\\'', tagname, '\\\'') as tag
                FROM v_department_substation
                WHERE description not like '%메인%' 
                and department_id = ${departmentId}
                GROUP BY department_id, tagname) as \`b\`
                `;
            tagNames = await dataPool.query(tagNameQuery);
            tagNames = tagNames[0].tagname
        }


        let curnLoadQuery = `
SELECT 
c.load,
hour(c.\`start\`) \`start\`,
hour(date_add(c.\`end\`, INTERVAL 1 second)) \`end\`,
c.season
from season b 
join electricity_season_with_load c ON b.season = c.season 
WHERE month(NOW()) BETWEEN b.end_month AND b.start_month
AND TIME(NOW()) BETWEEN c.start AND c.end
        `;

        let load = await dataPool.query(curnLoadQuery);
        resultSet.load = load[0]

        let curnPriceQuery = `
            SELECT 
            eer.unit_price
            from season b
            join electricity_season_with_load c ON b.season = c.season 
            JOIN (SELECT
            er.season,
            er.\`load\`,
            er.unit_price
            FROM electricity_electric_rates er
            JOIN power_info pif ON er.contract_type = pif.contract_type AND pif.receiving_voltage = er.receiving_voltage) eer ON b.season = eer.season AND c.\`load\` = eer.\`load\`
            WHERE month(NOW()) BETWEEN b.end_month AND b.start_month
            AND TIME(NOW()) BETWEEN c.start AND c.end
        `;

        let price = await dataPool.query(curnPriceQuery);
        resultSet.unitPrice = price[0].unit_price


        let dayUsageQuery = `
SELECT ifnull(SUM(s.\`usage\`),0) dayUsage
      FROM \`tagdata\`.history_day s
      WHERE s.\`TIMESTAMP\` = date(now())
        AND tagname in (${tagNames})
        `;
        let dayUsage = await dataPool.query(dayUsageQuery);
        resultSet.dayUsage = dayUsage[0].dayUsage


        let curnLoadPriceQuery = `
sELECT 
eswl.\`load\` \`load\`,
ifnull(k.\`usage\`, 0) \`Usage\`,
(ifnull(k.\`usage\`, 0) * eer.unit_price) \`price\`
FROM (SELECT DISTINCT \`load\` FROM  electricity_season_with_load) eswl
LEFT JOIN 
(SELECT 
c.load,
c.season,
SUM(s.\`usage\`) \`Usage\`
FROM (SELECT *,MONTH(\`timestamp\`) AS m
      FROM \`tagdata\`.history s

        WHERE s.\`TIMESTAMP\` >= date(now())
       AND s.\`TIMESTAMP\` < date_add(date(now()), interval 1 day)
        AND tagname IN (${tagNames})) as s
join electricity_season_with_load c ON TIME(s.timestamp) BETWEEN c.start AND c.end
WHERE c.\`season\` = (
SELECT season
FROM season
WHERE (MONTH(now()) between start_month and end_month)
or (start_month > end_month AND (MONTH(now()) >= start_month OR MONTH(now()) <= end_month))
)
GROUP BY c.load) k ON k.load = eswl.load
LEFT JOIN electricity_electric_rates eer ON k.season = eer.season AND eer.\`load\` = k.load
WHERE eer.contract_type = (SELECT contract_type FROM power_info)
AND eer.receiving_voltage = (SELECT receiving_voltage FROM power_info)
        `;

        let loadPrice = await dataPool.query(curnLoadPriceQuery);
        resultSet.loadPrice = loadPrice

        let departmentRankQuery = `
SELECT
v.department_name name,
      ifnull(SUM(d.\`usage\`), 0) as \`Usage\`
FROM v_department_substation v
left outer join tagdata.history_day d on d.tagname = v.tagname
WHERE description not LIKE '%메인%'
and department_name != "-"
and date(now()) = d.timestamp
group by v.department_id
 order by SUM(d.\`usage\`) DESC LIMIT 1
        `;

        let departmentRank = await dataPool.query(departmentRankQuery);


        if (departmentRank[0] !== undefined && departmentId === undefined) {
            resultSet.departmentRank = departmentRank[0]
        }


        let locationRankQuery = `
SELECT
v.description locationName,
      ifnull(SUM(d.\`usage\`), 0) as \`Usage\`
FROM v_department_substation v
left outer join tagdata.history_day d on d.tagname = v.tagname
WHERE description not LIKE '%메인%'
and department_name != "-"
${departmentIfWhere}
and date(now()) = d.timestamp
group BY v.id
 order by SUM(d.\`usage\`) DESC LIMIT 1
        `;

        let locationRank = await dataPool.query(locationRankQuery);

        if (locationRank[0] === undefined) {
            resultSet.locationRank = {}
        } else {
            resultSet.locationRank = locationRank[0]
        }





        res.json({code: 1, data: resultSet});
    } catch (error) {
        console.log(error)
        res.json({

            message: '응답이 없습니다. 새로고침 후 다시 시도하시기 바랍니다.',
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});

router.get('/dashboard/department/rate', async (req, res) => {
    let conn;
    try {
        let departmentRateQuery = `
        SELECT
        v.department_name name,
              ifnull(SUM(d.\`usage\`), 0) as \`Usage\`
        FROM v_department_substation v
        left outer join tagdata.history_day d on d.tagname = v.tagname
        WHERE description not LIKE '%메인%'
        and department_name != "-"
        and department_name != "SP"
        and date(now())  = date(d.timestamp)
        group by v.department_id
         order by SUM(d.\`usage\`) DESC
        `;

        let departmentRate = await dataPool.query(departmentRateQuery);

        res.json({
            code: 1,
            data: departmentRate,
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
// 최근 5일 사용량 검색

router.get('/dashboard/departmant/calendar/:departmentId', async (req, res) => {

    let conn;
    try {

        let dateSet = [];

        conn = await dataPool.getConnection();
        const departmentId = req.params.departmentId;
        let taNamesQuery = `
select group_concat(b.tag) as tagname
from (SELECT concat('\\'', tagname, '\\'') as tag
FROM v_department_substation
WHERE department_id = ${departmentId}
GROUP BY department_id, tagname) as \`b\`
        `;

        let tagNames = await dataPool.query(taNamesQuery);
        let historyHourQuery = `
SELECT
if(HOUR( \`TIMESTAMP\` + INTERVAL 1 HOUR) = 0, 24, HOUR( \`TIMESTAMP\` + INTERVAL 1 HOUR)) H,
weekday(hh.timestamp) weekDay,
sum(hh.usage) \`usage\`
FROM \`tagdata\`.history_hour hh 
WHERE hh.timestamp > DATE_ADD(DATE_ADD(date(now()), INTERVAL -1 WEEK), INTERVAL +1 DAY) 
AND hh.timestamp <= date(now())
AND hh.tagname IN (${tagNames[0].tagname})
group by hour(hh.timestamp), weekday(hh.timestamp)
order by weekday(hh.timestamp) asc, hour(hh.timestamp) asc
        `;
        let data = await dataPool.query(historyHourQuery);
        for (let j = 0; j < 7; j++) {
            for (let i = 1; i <= 24; i++) {

                if (data.find(o => o.H === i && o.weekDay === j) !== undefined) {
                    let _set = {};
                    _set.H = i;
                    _set.weekDay = j;
                    _set.usage = data.find(o => o.H === i && o.weekDay === j).usage;
                    dateSet.push(_set);
                } else {
                    let _set = {};
                    _set.H = i;
                    _set.weekDay = j;
                    _set.usage = 0;
                    dateSet.push(_set);
                }

            }
        }

        res.json({code: 1, data: dateSet});
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
router.get('/dashboard/departmant/target/:departmentId', async (req, res) => {

    let conn;
    try {
        conn = await dataPool.getConnection();
        const departmentId = req.params.departmentId;
        let query = `
select group_concat(b.tag) as tagname
from (SELECT concat('\\'', tagname, '\\'') as tag
FROM v_department_substation
WHERE description not LIKE '%메인%'  
AND department_id = ${departmentId}
GROUP BY department_id, tagname) as \`b\`
        `;

        let departmentTagNames = await dataPool.query(query);

        query = `
select group_concat(b.tag) as tagname
from (SELECT concat('\\'', tagname, '\\'') as tag
FROM v_department_substation
WHERE description not LIKE '%메인%'  
GROUP BY department_id, tagname) as \`b\`
        `;

        let allTagNames = await dataPool.query(query);


        query = `
SELECT 
sum(hd.\`usage\`) as \`Usage\`
FROM \`tagdata\`.history_day hd 
WHERE hd.timestamp = DATE(NOW())
AND hd.tagname IN (${departmentTagNames[0].tagname})
group by hd.timestamp
        `;
        let totalReulst = await dataPool.query(query);

        query = `
SELECT 
sum(hd.\`usage\`) as \`Usage\`
FROM \`tagdata\`.history_day hd 
WHERE hd.timestamp = DATE(NOW())
AND hd.tagname IN (${allTagNames[0].tagname})
group by hd.timestamp
        `;


        let usageResult = await dataPool.query(query);
        let Usage;
        if (usageResult.length == 0) {
            Usage = 0
        } else {
            Usage = totalReulst[0].Usage
        }

        let day_kWh_target;
        if (totalReulst.length == 0) {
            day_kWh_target = 0
        } else {
            day_kWh_target = usageResult[0].Usage
        }



        let result = []
        result.push({Usage, day_kWh_target})

        res.json({code: 1, data : result});
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
