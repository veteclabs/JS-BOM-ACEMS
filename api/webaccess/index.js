const express = require('express');

const router = express.Router();
const request = require('request');
const btoa = require('btoa');
require('dotenv').config();


// WEBACCESS-Login-Function
router.get('/WaLogin', (req, res) => {
    const UserName = process.env.WA_ID;
    const Password = process.env.WA_PASSWORD;
    const userPass = btoa(`${UserName}:${Password}`);
    const url = `${process.env.WA_IP}/WaWebService/Json/Login`;
    request({
        method: 'get',
        url,
        headers: {
            Authorization: `Basic ${userPass}`,
            Accept: 'application/json',
            LoginType: 'View',
            'cache-control': 'no-cache',
            'Content-Type': 'application/json; charset=utf-8',
        },
    }, () => {
        res.json({
            code: 0,
            msg: '로그인완료',
        });
    });
});

// WEBACCESS DATA 가져오기 (대시보드용)
router.post('/wa/port/getTagValue', async (req, res) => {
    const myUserName = process.env.WA_ID;
    const myPassword = process.env.WA_PASSWORD;
    const myProjName = process.env.WA_PROJECT;
    const myNodeName = process.env.WA_NODE;
    const userPass = btoa(`${myUserName}:${myPassword}`);
    const getTagURL = `${process.env.WA_IP}/WaWebService/Json/TagList/${myProjName}/${myNodeName}`;
    const targetPortId = req.body.portId;

    const tagList = [];
    for (let i = 0; i < targetPortId.length; i += 1) {
        const url = `${getTagURL}/${targetPortId[i]}`;
        tagList.push(new Promise((resolve, reject) => {
            request({
                method: 'GET',
                url,
                headers: {
                    Authorization: `Basic ${userPass}`,
                },
                json: true,
            }, (error, response, body) => {
                if (error) {
                    reject(error);
                } else {
                    resolve(body.Tags);
                }
            });
        }));
    }
    // 태그리스트 가져오기
    const tagListResult = await Promise.all(tagList);

    // 태그리스트 dataParma 형태로 변경하기
    const dataParam = {};
    const tagObjectList = [];
    for (let i = 0; i < tagListResult.length; i += 1) {
        for (let x = 0; x < tagListResult[i].length; x += 1) {
            const tagObject = {};
            tagObject.Name = tagListResult[i][x].Name;
            tagObjectList.push(tagObject);
        }
    }
    dataParam.Tags = tagObjectList;

    const url = `${process.env.WA_IP}/WaWebService/Json/GetTagValue/${myProjName}`;
    request({
        url,
        method: 'POST',
        headers: {
            Authorization: `Basic ${userPass}`,
            Accept: 'application/json',
            'Content-Type': 'application/json; charset=utf-8',
        },
        body: dataParam,
        json: true,
    }, (err, response) => {
        if (err) throw new Error(err);
        res.json(response.body);
    });
});


// WEBACCESS DATA 가져오기 (대시보드용)
router.post('/wa/tag/getTagValue', async (req, res) => {
    const UserName = process.env.WA_ID;
    const Password = process.env.WA_PASSWORD;
    const URL = process.env.WA_IP;
    const userPass = btoa(`${UserName}:${Password}`);
    const myProjURL = `${URL}/WaWebService/Json/GetTagValue/${process.env.WA_PROJECT}`;
    const {tagList} = req.body;


    let dataParam = {};
    let tagObjectList = [];
    for (let i = 0; i < tagList.length; i++) {
        let tagObject = {};
        tagObject["Name"] = tagList[i].tagName;
        tagObjectList.push(tagObject);
    }

    dataParam["Tags"] = tagObjectList;

    request({
        url: myProjURL,
        method: 'POST',
        headers: {
            'Authorization': 'Basic ' + userPass,
            'Accept': 'application/json',
            'Content-Type': 'application/json; charset=utf-8'
        },
        body: dataParam,
        json: true
    }, function (error, response, body) {
        res.json(body);
    });
});


// 모듈로 사용할 수 있도록 export
module.exports = router;
