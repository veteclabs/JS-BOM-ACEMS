const schedule = require("node-schedule");

let rule = new schedule.RecurrenceRule();

rule.second = [1, 10, 21, 24, 31, 34, 37, 40, 44, 47, 50, 53, 56, 59];

rule.id = 1;

rule.des = [2,3,24];

schedule.scheduleJob(`{"id":1, weeks:[1,3,4]}`, rule, () => {
    console.log("ewf");
});

let k = schedule.scheduledJobs['1'];

k.hour = 10;
console.log(k.des);