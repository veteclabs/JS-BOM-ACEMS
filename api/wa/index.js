const express = require('express');

const router = express.Router();
const request = require('request');
const btoa = require('btoa');
const pool = require('../../config/database');
const dataPool = require('../../config/dataDatabase');
const axios = require('axios');
const schedule = require("node-schedule");
require('dotenv').config();



exports.getTagValues = async (tagNames) => {
    const UserName = process.env.WA_ID;
    const Password = process.env.WA_PASSWORD;
    const URL = process.env.WA_IP;
    const userPass = btoa(`${UserName}:${Password}`);
    const myProjURL = `${URL}/WaWebService/Json/GetTagValue/${process.env.WA_PROJECT}`;

    let dataParam = {};
    let tagObjectList = [];
    for (let name of tagNames) {
        let tagObject = {};
        tagObject["Name"] = name;
        tagObjectList.push(tagObject);
    }
    dataParam["Tags"] = tagObjectList;
    try {
        let result = await axios({
            url: myProjURL,
            method: 'POST',
            headers: {
                'Authorization': 'Basic ' + userPass,
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=utf-8'
            },
            data: dataParam
        });
        return result.data.Values
    } catch(e) {
        return e;
    }
};

exports.setTagValues = async (tagNames) => {
    const UserName = process.env.WA_ID;
    const Password = process.env.WA_PASSWORD;
    const URL = process.env.WA_IP;
    const userPass = btoa(`${UserName}:${Password}`);
    const myProjURL = `${URL}/WaWebService/Json/SetTagValue/${process.env.WA_PROJECT}`;

    let dataParam = {};
    let tagObjectList = [];
    for (let tag of tagNames) {
        let tagObject = {};
        tagObject["Name"] = tag.name;
        tagObject["Value"] = tag.value;
        tagObjectList.push(tagObject);
    }
    dataParam["Tags"] = tagObjectList;

    try {
        let result = await axios({
            url: myProjURL,
            method: 'POST',
            headers: {
                'Authorization': 'Basic ' + userPass,
                'Accept': 'application/json',
                'Content-Type': 'application/json; charset=utf-8'
            },
            data: dataParam
        });
        return result.data
    } catch(e) {
        return e;
    }
};
exports.test = async () => {
    let conn;
    try {
        conn = await dataPool.getConnection();
        let devices = await dataPool.query(`
            select
            d.device_id id,
            scd.max,
            scd.min
            from device d
            left join equipment eq on d.equipment_id = eq.equipment_id
            left join schedule scd on scd.device_id = d.device_id
            where eq.type = "pressure gauge"`);
        let tags = await dataPool.query(`
            select
            t.tagName tagname,
            t.device_id deviceId
            from tag t
            where t.device_id in (${devices.map(({id}) => id)})`);
        // console.log(tags);
        let waTags = await this.getTagValues(tags.map(({tagname}) => tagname));
        // console.log(waTags);
        let tagSet = {};
        for (let tag of tags) {
            tagSet[tag.deviceId] = tag
        }
        let waTagSet = {};
        for (let waTag of waTags) {
            waTagSet[waTag.Name] = waTag.Value
        }
        tags.forEach(t=>t.value = waTagSet[t.tagname]);
        devices.forEach(d => d.tag = tagSet[String(d.id)]);
        // console.log(devices);
        // console.log(tags);
        let aa = await dataPool.query(`
            select
            weekofday,
            weeks
            from
            schedule
            `);
        // console.log(aa);
        // let compTarget = await dataPool.query(`
        //     select
        //     tagName
        //     from device d
        //     left join eq on d.equipment_id = eq.equipment_id
        //     left join tag t on t.device_id = d.device_id
        //     where eq.type = "pressure gauge"`);
        // console.log(compTarget)

    } catch (error) {
        console.log(error)
    } finally {
        if (conn) {
            await conn.release();
        }
    }
}
this.test();
// this.getTagValues(["U001_Temp","U002_Temp"]);
// this.setTagValues([
//     {name:"U216_PWR_KWh_Today", value:22},
//     {name:"b", value:20}]);

