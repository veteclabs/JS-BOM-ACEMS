const schedule = require("node-schedule");
const axios = require("axios");
const pool = require('../config/database');
const Dayjs = require("dayjs");

setInterval(async () => {
    try {
        let schedules = await axios.get("http://localhost:8031/api/schedules");
        schedules = schedules.data
        let scheduleIds = schedules.map(t=>t.id)


        let threadList = Object.keys(schedule.scheduledJobs)
        let threaIds = threadList.map(t => parseInt(t, 10))
        let distoryThreadList = threaIds.filter(x => !scheduleIds.includes(x));
        let newThreadList = scheduleIds.filter(x => !threaIds.includes(x));
        distoryThreadList.forEach(t=>{
            let scd = schedules.filter(k=>k.id === t)
            console.log("(" + t + ") 번 스래드 삭제")
            schedule.cancelJob(String(t));
        })
        newThreadList.forEach(t=>{

            let scd = schedules.filter(k=>k.id === t)[0]

            const rule = new schedule.RecurrenceRule();
            rule.second = scd.interval
            console.log("(" + t + ") 번 스래드 생성")
            // schedule.scheduleJob(String(t), rule,this.checkPoserState(t));
        })
    } catch (e) {
        console.log(e)
    }
}, 5000)

exports.checkPoserState = async (scheduleId) => {
    let conn;
    try {
        const now = Dayjs();
        conn = await pool.getConnection();
        let query = `SELECT (weekofyear("${now.format("YYYY-MM-DD")}" + INTERVAL 1 day) - WEEKOFYEAR(LAST_DAY(("${now.format("YYYY-MM-DD")}" + INTERVAL 1 DAY) - INTERVAL 1 MONTH) + interval 1 DAY)) + 1 AS \`WEEK\`;`

        let week = await conn.query(query);
        week = week === 6?1:week;
        week = week[0].WEEK;
        let dayOfWeek = now.day()
        let schedules = await axios.get(`http://localhost:8031/api/schedule/${scheduleId}`);
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
                        await this.groupOrder(scheduleId, week, powerState);
                    } else {
                        await this.controllFacility(schedule.groupId, powerState);
                    }
                }
            }
        }
    } catch (e) {
        console.log(e)
    } finally {
        if (conn) {
            await conn.release();
        }
    }
}
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
    try {
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


                // await conn.query('insert into alarm values()', now.toDate());

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

                // await conn.query('insert into alarm values()', now.toDate());

                return false;
            } else if (facilityState === 1 && powerState === 'ON') {
                return false;
            } else if (facilityState === 0 && powerState === 'OFF') {
                return false;
            }

        }
        return true;
    } catch (e) {
        console.log(e)
    }
}
