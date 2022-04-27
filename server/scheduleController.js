const schedule = require("node-schedule");
const axios = require("axios");
const pool = require('../config/database.js');
const Dayjs = require("dayjs");

setInterval(async () => {
    let conn;
    try {
        const now = Dayjs();
        let schedules = await axios.get("http://localhost:8031/api/schedules");
        schedules = schedules.data
        let scheduleIds = schedules.map(t=>t.id)


        let threadList = Object.keys(schedule.scheduledJobs)
        // console.log(schedule.scheduledJobs)
        let threaIds = threadList.map(t => parseInt(t, 10))
        let query = `SELECT (weekofyear("${now.format("YYYY-MM-DD")}" + INTERVAL 1 day) - WEEKOFYEAR(LAST_DAY(("${now.format("YYYY-MM-DD")}" + INTERVAL 1 DAY) - INTERVAL 1 MONTH) + interval 1 DAY)) + 1 AS \`WEEK\`;`

        conn = await pool.getConnection();
        let week = await conn.query(query);
        week = week[0].WEEK;
        week = week === 6?1:week;
        let distoryThreadList = threaIds.filter(x => !scheduleIds.includes(x));
        let newThreadList = scheduleIds.filter(x => !threaIds.includes(x));
        // console.log(newThreadList)
        distoryThreadList.forEach(t=>{
            let scd = schedules.filter(k=>k.id === t)
            //console.log(t, "번 스레드 삭제")
            //schedule.cancelJob(String(t));
        })
        newThreadList.forEach(t=>{

            let scd = schedules.filter(k=>k.id === t)[0]
            const rule = new schedule.RecurrenceRule();
            //console.log(t, "번 스레드 생성")
            rule.second = scd.interval
            schedule.scheduleJob(String(t), rule, async () => {
                try {
                    const now = Dayjs();

                    let dayOfWeek = now.day()

                    let schedules = await axios.get(`http://localhost:8031/api/schedule/${parseInt(scd.id)}`);
                    let powerState = '';
                    for (let schedule of schedules.data) {

                        if (schedule.isActive) {
                            let startTime = new Date()
                            let now2 = new Date();
                            let endTime = new Date()

                            startTime.setHours(Number(schedule.startTime.substring(0, 2)), Number(schedule.startTime.substring(3, 5)), Number(schedule.startTime.substring(6, 8)))
                            endTime.setHours(Number(schedule.stopTime.substring(0, 2)), Number(schedule.stopTime.substring(3, 5)), Number(schedule.stopTime.substring(6, 8)))
                            if ((startTime <= now2 && now2 <= endTime) && schedule.dayOfWeeks.includes(dayOfWeek) && schedule.weeks.includes(week)) {
                                if (schedule.isGroup) {
                                    if (schedule.min > schedule.pressure) {
                                        powerState = 'ON'
                                    } else if (schedule.max < schedule.pressure) {
                                        powerState = 'OFF'
                                    } else {
                                        powerState = 'STAY'
                                    }
                                } else {
                                    powerState = "ON"
                                }
                            } else {
                                powerState = 'OFF'
                            }
                            if (powerState !== 'STAY') {

                                if (schedule.isGroup) {
                                    await this.groupOrder(schedule.scheduleId, week, powerState);
                                } else {
                                    await this.controllFacility(schedule.groupId, powerState);
                                }
                            }
                        }
                    }
                } catch (e) {
                    console.log(e)
                }
            });
        })
    } catch (e) {
        console.log(e)
    } finally {
        if (conn) {
            await conn.release();
        }
    }
}, 5000);


exports.groupOrder = async (scheduleId, week, powerState) => {
    try {
        let orders = await axios.get(`http://localhost:8031/api/schedule/${scheduleId}/week/${week}`);
        orders = orders.data
        let controllerResult = null;
        if (!orders.length !== 0) {
            for (let order of orders) {
                controllerResult = await this.controllFacility(order.groupId, powerState);
                if (controllerResult) {
                    return true;
                }
            }
        }
        return true;
    } catch (e) {
        // console.log(e)
    }
}

exports.controllFacility = async (groupId, powerState) => {
    let conn;
    try {
        conn = await pool.getConnection();
        let powerCode = "COMP_Power";
        let localCode = "COMP_Local";
        let devices = await axios.get(`http://localhost:8031/api/devices/${groupId}`, {data:{
                "equipmentType" : "AIR_COMPRESSOR",
                "tagTypes" : [
                    powerCode,
                    localCode,

                ]
            }});

        for (let device of devices.data) {

            const localTag = device.tags[localCode];
            const powerTag = device.tags[powerCode];
            let facilityState = await axios.post(`http://localhost:8031/WaWebService/Json/GetTagValue/BOM`, {
                "Tags" : [
                    {
                        "Name": powerTag.tagName
                    }
                ]
            });
            facilityState = parseInt(facilityState.data.Values[0].Value)
            if (facilityState === 0 && powerState === 'ON') {
                // 가동 제어 신호 보내기
                for (let i = 0; i <3 ;i++) {
                    await axios.post(`http://localhost:8031/WaWebService/Json/SetTagValue/BOM`, {
                        "Tags" : [
                            {
                                "Name": localTag.tagName,
                                "Value": 1
                            }
                        ]
                    });
                    await axios.post(`http://localhost:8031/WaWebService/Json/SetTagValue/BOM`, {
                        "Tags" : [
                            {
                                "Name": powerTag.tagName,
                                "Value": 1
                            }
                        ]
                    });

                    setTimeout(async() => {
                        let powerState = await axios.post(`http://localhost:8031/WaWebService/Json/GetTagValue/BOM`, {
                            "Tags" : [
                                {
                                    "Name": powerTag.tagName
                                }
                            ]
                        });
                        if (parseInt(powerState.data.Values[0].Value) === 1) {
                            return true;
                        }
                    }, 3000);

                }


                await conn.query(`INSERT INTO ing.alarm ('check_in', 'event_date', 'kwh_value', 'message', 'occurrence_time', 'prss_value', 'recover_date', 'recover_time', 'temp_value', 'type', 'tag_id', 'trip_id') VALUES ( 0, date(NOW()), NULL, 'Motor Winding Temperature Failure', TIME(NOW()), null, NULL, NULL, null, 'error', ${powerTag.id}, null);`);

                return false;
            }   else if (facilityState === 1 && powerState === 'OFF') {
                // 정지 제어 신호 보내기
                for (let i = 0; i < 3;i++) {
                    await axios.post(`http://localhost:8031/WaWebService/Json/SetTagValue/BOM`, {
                        "Tags" : [
                            {
                                "Name": localTag.tagName,
                                "Value": 1
                            }
                        ]
                    });
                    await axios.post(`http://localhost:8031/WaWebService/Json/SetTagValue/BOM`, {
                        "Tags" : [
                            {
                                "Name": powerTag.tagName,
                                "Value": 0
                            }
                        ]
                    });

                    setTimeout(async() => {
                        let powerState = await axios.post(`http://localhost:8031/WaWebService/Json/GetTagValue/BOM`, {
                            "Tags" : [
                                {
                                    "Name": powerTag.tagName
                                }
                            ]
                        });
                        if (parseInt(powerState.data.Values[0].Value) === 0) {
                            return true;
                        }
                    }, 3000);
                }
                await conn.query(`INSERT INTO ing.alarm ('check_in', 'event_date', 'kwh_value', 'message', 'occurrence_time', 'prss_value', 'recover_date', 'recover_time', 'temp_value', 'type', 'tag_id', 'trip_id') VALUES ( 0, date(NOW()), NULL, 'Motor Winding Temperature Failure', TIME(NOW()), null, NULL, NULL, null, 'error', ${powerTag.id}, null);`);

                return false;
            } else if (facilityState === 1 && powerState === 'ON') {
                return false;
            } else if (facilityState === 0 && powerState === 'OFF') {
                return false;
            }

        }
        return true;
    } catch (e) {
        //console.log(e)
    } finally {
        if (conn) {
            await conn.release();
        }
    }
}
