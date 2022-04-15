const schedule = require("node-schedule");
const axios = require("axios");
const pool = require('../config/database');
const Dayjs = require("dayjs");

setInterval(async () => {
    try {
        let schedules = await axios.get("http://localhost:8031/api/schedules");
        schedules = schedules.data
        let scheduleIds = schedules.map(t=>t.id)
        const rule = new schedule.RecurrenceRule();
        rule.second = 30

        let threadList = Object.keys(schedule.scheduledJobs)
        let threaIds = threadList.map(t => parseInt(t, 10))
        let distoryThreadList = threaIds.filter(x => !scheduleIds.includes(x));
        let newThreadList = scheduleIds.filter(x => !threaIds.includes(x));
        distoryThreadList.forEach(t=>{
            console.log("(" + t + ") 번 스래드 삭제")
            schedule.cancelJob(String(t));
        })
        newThreadList.forEach(t=>{
            console.log("(" + t + ") 번 스래드 생성")
            schedule.scheduleJob(String(t), rule, function () {
                // console.log('The answer to life, the universe, and everything!');
            });
        })
        // console.log(schedule.scheduledJobs)
        // if (contain === -1) {
        //     // 생성
        //     console.log(-1)
        // }

    } catch (e) {
        console.log(e)
    }
}, 5000)

// this.threadManeger();
exports.checkPoserState = async (scheduleId) => {
    try {
        const now = Dayjs();
        const conn = await pool.getConnection();

        let week = await conn.query('CALL get_week_number(?)', now.toDate());
        week = week[0][0].WEEK;
        let dayOfWeek = now.day()
        let schedules = await axios.get(`http://112.216.32.6:8031/api/schedule/${scheduleId}`);
        let powerState = '';
        for (let schedule of schedules.data) {
            console.log(schedule.isGroup)
            if (schedule.isActive) {
                let startTime = new Date()
                let now2 = new Date();
                let endTime = new Date()
                startTime.setHours(Number(schedule.startTime.substring(0, 2)), Number(schedule.startTime.substring(3, 5)), Number(schedule.startTime.substring(6, 8)))
                endTime.setHours(Number(schedule.stopTime.substring(0, 2)), Number(schedule.stopTime.substring(3, 5)), Number(schedule.stopTime.substring(6, 8)))
                if ((startTime <= now2 && now2 <= endTime) && (dayOfWeek in schedule.dayOfWeeks) && (week in schedule.weeks)) {
                    if (schedule.min > schedule.pressure) {
                        powerState = 'ON'
                    } else if (schedule.max < schedule.pressure) {
                        powerState = 'OFF'
                    } else {
                        powerState = 'STAY'
                    }
                } else {
                    powerState = 'OFF'
                }
                if (powerState !== 'STAY') {

                    if (schedule.isGroup) {
                        await this.groupOrder(scheduleId, week, powerState);
                    } else {
                        // await this.controllFacility(schedule.groupId);
                    }
                }
            }
        }
    } catch (e) {
        console.log(e)
    }
}
exports.groupOrder = async (scheduleId, week, powerState) => {
    try {
        let orders = await axios.get(`http://112.216.32.6:8031/api/schedule/${scheduleId}/week/${week}`);
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
        console.log(e)
    }
}
exports.controllFacility = async (groupId, powerState) => {
    try {
        let tags = await axios.get(`http://localhost:8031/api/tags/${groupId}`, {params:{
                    tagType: "POWER",
                    equipmentType: "AIR_COMPRESSOR"
                }});
        for (let tag of tags.data) {
            let facilityState = await axios.post(`http://localhost:8031/WaWebService/Json/GetTagValue/BOM`, {
                    "Tags" : [
                        {
                            "Name": tag
                        }
                    ]
                });
            // facilityState = facilityState.data.Values[0].Value
            facilityState = facilityState.data.Values[0].Value
            if (facilityState === 0 && powerState === 'ON') {
                // 가동 제어 신호 보내기
                await axios.post(`http://localhost:8031/WaWebService/Json/SetTagValue/BOM`, {
                    "Tags" : [
                        {
                            "Name": tag,
                            "Value": 1
                        }
                    ]
                });
                return true;
            }   else if (facilityState === 1 && powerState === 'OFF') {
                // 정지 제어 신호 보내기
                await axios.post(`http://localhost:8031/WaWebService/Json/SetTagValue/BOM`, {
                    "Tags" : [
                        {
                            "Name": tag,
                            "Value": 0
                        }
                    ]
                });
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

