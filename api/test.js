const schedule = require("node-schedule");
const axios = require("axios");
const pool = require('../config/database');
const Dayjs = require("dayjs");

const rule = new schedule.RecurrenceRule();
rule.second = 30;
schedule.scheduleJob("12",rule, function(){
    console.log('The answer to life, the universe, and everything!');
});
schedule.scheduleJob("13",rule, function(){
    console.log('The answer to life, the universe, and everything!');
});
let scheduleList = schedule.scheduledJobs
const contain = Object.keys(scheduleList).indexOf('11');
if(contain === -1) {
    // 생성
    console.log(-1)
}



exports.a = async () => {
    try {

        console.log(week[0][0].WEEK)
    } catch(e) {

    }
}
this.a()
exports.threadManeger = async () => {
    console.log("managing thread.")
    try {
        let schedules = await axios.get("http://localhost:8031/api/schedules");
        schedules = schedules.data
        for (const schedule of schedules.data) {
            await this.checkPoserState(t.id);
        }
        await http;
    } catch (e) {
        console.log(e)
    }

}
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
this.checkPoserState(1);
exports.groupOrder = async (scheduleId, week, powerState) => {
    console.log(scheduleId)
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
        let s = await axios.post(`http://localhost:8031/WaWebService/Json/GetTagValue/BOM`, {data:{
                "Tags" : [
                    {
                        "Name": "020_temp",
                        "Value" : 23.3
                    }
                ]
            }});

        tags = tags.data
        return true;
    } catch (e) {
        console.log(e)
    }
}

