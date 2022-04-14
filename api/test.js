const schedule = require("node-schedule");
const axios = require("axios");





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

for (let scheduleOne in scheduleList) {
    console.log(scheduleOne)
}
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
        let schedules = await axios.get(`http://localhost:8031/api/schedule/${scheduleId}`);
        let powerState = false;
        for (let schedule of schedules.data) {
            if (schedule.isGroup) {
                await this.groupOrder(schedule.scheduleId);
            } else {
                await this.controllFacility(schedule.groupId);
            }
        }
    } catch (e) {
        console.log(e)
    }
}
exports.groupOrder = async (scheduleId) => {
    try {
        let weekId = 1;
        let orders = await axios.get(`http://localhost:8031/api/schedule/${scheduleId}/week/${weekId}`);
        orders = orders.data
        let powerState = false;
        orders = orders.sort((a,b)=> a.order < b.order);
        for(let order of orders) {
            await this.controllFacility(order.groupId);
        }
        return orders
    } catch (e) {
        console.log(e)
    }
}
exports.controllFacility = async (groupId) => {
    try {
        const params = {
            tagType: "POWER",
            equipmentType: "AIR_COMPRESSOR"
        }
        let tags = await axios.get(`http://localhost:8031/api/tags/${groupId}`, {params:params});
        tags = tags.data
        console.log(tags)
    } catch (e) {
        console.log(e)
    }
}
// this.threadManeger();

