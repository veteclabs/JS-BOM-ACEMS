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
                    console.log(week)

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
        // orders = orders.sort((a,b)=> a.order < b.order);
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
        const params = {
            tagType: "POWER",
            equipmentType: "AIR_COMPRESSOR"
        }
        // let tags = await axios.get(`http://localhost:8031/api/tags/${groupId}`, {params:params});
        // tags = tags.data
        return true;
    } catch (e) {
        console.log(e)
    }
}
// this.threadManeger();



Date.prototype.getWeek = function (dowOffset) {
    /*getWeek() was developed by Nick Baicoianu at MeanFreePath: http://www.meanfreepath.com */

    dowOffset = typeof(dowOffset) == 'number' ? dowOffset : 0; // dowOffset이 숫자면 넣고 아니면 0
    var newYear = new Date(this.getFullYear(),0,1);
    var day = newYear.getDay() - dowOffset; //the day of week the year begins on
    day = (day >= 0 ? day : day + 7);
    var daynum = Math.floor((this.getTime() - newYear.getTime() -
        (this.getTimezoneOffset()-newYear.getTimezoneOffset())*60000)/86400000) + 1;
    var weeknum;
    //if the year starts before the middle of a week
    if(day < 4) {
        weeknum = Math.floor((daynum+day-1)/7) + 1;
        if(weeknum > 52) {
            let nYear = new Date(this.getFullYear() + 1,0,1);
            let nday = nYear.getDay() - dowOffset;
            nday = nday >= 0 ? nday : nday + 7;
            /*if the next year starts before the middle of
              the week, it is week #1 of that year*/
            weeknum = nday < 4 ? 1 : 53;
        }
    }
    else {
        weeknum = Math.floor((daynum+day-1)/7);
    }
    return weeknum;
};
