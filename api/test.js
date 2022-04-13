const schedule = require("node-schedule");

const rule = new schedule.RecurrenceRule();
rule.second = 30;
schedule.scheduleJob("12",rule, function(){
    console.log('The answer to life, the universe, and everything!');
});

console.log(schedule.scheduledJobs)
