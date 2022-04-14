const schedule = require("node-schedule");
const axios = require("axios");
const rule = new schedule.RecurrenceRule();
rule.second = 30;
schedule.scheduleJob("12",rule, function(){
    console.log('The answer to life, the universe, and everything!');
});

console.log(schedule.scheduledJobs)


exports.threadManeger() {
    axios.get("http://112.216.32.6:8031/api/schedules")
}
