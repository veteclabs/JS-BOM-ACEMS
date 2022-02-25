const express = require('express');
const router = express.Router();
const pool = require('../../config/database');
const passwordHash = require('../../lib/passwordHash');
var cookie = require('cookie');
const crypto = require('crypto');


router.get('/setting/users', async function (req, res, next) {
    let conn;
    try {

        conn = await pool.getConnection();
        const query = `
        SELECT 
u.id,
username,
email,
phone,
is_alarm,
dt.id teamId,
dt.team_name teamName,
d.id departmentId,
d.name departmentName
FROM 
users u
LEFT JOIN department_team_user dtu ON dtu.user_id = u.id
LEFT JOIN department_team dt ON dtu.department_team_id = dt.id 
left join departments d on d.id = dt.department_id
        `;

        const result = await conn.query(query);

        res.json({
            code: 1,
            result
        });

    } catch (error) {
        res.json({
            code: 0,
            msg: process.env.DB_ERROR_MSG,
            error
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});


router.post('/setting/user/email/check', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();
        let checkIn = false;
        let query = `
SELECT * FROM users WHERE email = "${req.body.email}"
        `;
        let emailActive = await conn.query(query);
        emailActive = emailActive.length !== 1
        if (req.body.userId === undefined) {
            res.json({
                code: 1,
                checkIn: emailActive
            });
        } else {
            query = `
SELECT email FROM users WHERE id = ${req.body.userId}
        `;
            let email = await conn.query(query);
            email = email[0].email

            if (emailActive === true) {
                checkIn = true;
            } else {
                if (email == req.body.email) {
                    checkIn = true;
                } else {
                    checkIn = false;
                }

            }
            res.json({
                code: 1,
                checkIn: checkIn
            });
        }

    } catch (error) {
        res.json({
            code: 0,
            msg: process.env.DB_ERROR_MSG,
            error
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});
router.post('/st/us/chck', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();
        let {pw} = req.body;

        let cry = passwordHash(pw);
        let userAuth = req.session.authUser;
        let query = ` SELECT * FROM users WHERE password =  "${cry}" and email = "${userAuth.email}" `;
        let emailActive = await conn.query(query);

        res.json({code: 1, data: emailActive.length !== 0});
    } catch (error) {
        res.json({
            message: '응답이 없습니다. 새로고침 후 다시 시도하시기 바랍니다.',
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});

router.post('/st/us/stpw', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();
        let {pw} = req.body;
        let {id} = req.body;
        let ifWhere = '';
        let userAuth;
        if (id !== undefined) {
            ifWhere = `id = ${id}`
        } else {
            userAuth = req.session.authUser;
            ifWhere = `email = "${userAuth.email}"`
        }
        let cry = passwordHash(pw);


        let query = `
                UPDATE users
                SET
                password = "${cry}"
                WHERE
                ${ifWhere}
        `;
         await conn.query(query);


        res.json({code: 1, data: true, message: process.env.DB_UPDATE_MSG});
    } catch (error) {
        res.json({
            message: '응답이 없습니다. 새로고침 후 다시 시도하시기 바랍니다.',
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});
// 연락처 존재 확인
router.post('/setting/user/phone-check', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();
        const query = `SELECT COUNT(phone) AS cnt FROM users where phone = '${req.body.phone}'`;
        const result = await conn.query(query);

        if (result[0].cnt === 0) {
            res.json(true)
        } else {
            res.json(false)
        }

        res.json({
            code: 1,
            result
        });

    } catch (error) {
        res.json({
            code: 0,
            msg: process.env.DB_ERROR_MSG,
            error
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});

// 사용자 생성
router.post('/setting/user', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();

        const {userName} = req.body;
        const {phone} = req.body;
        const {teamId} = req.body;
        const {email} = req.body;
        let {isAlarm} = req.body;

        let query = `
    INSERT INTO users (username,phone,email,is_alarm)
    VALUES('${userName}','${phone}','${email}',${isAlarm})`;

        await conn.query(query);
        let idValue = await conn.query(`SHOW TABLE STATUS FROM \`bom\` WHERE \`name\` LIKE 'users';`);
        idValue = idValue[0].Auto_increment - 1
        query = `
    INSERT INTO department_team_user (department_team_id,user_id)
    VALUES(${teamId},${idValue})`;

        await conn.query(query);


        res.json({
            code: 1,
            msg: process.env.DB_INSERT_MSG,
        });
    } catch (error) {
        res.json({
            code: 0,
            msg: process.env.DB_ERROR_MSG,
            error
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});

// 사용자 수정
router.put('/setting/user', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();

        const {userId} = req.body;
        const {userName} = req.body;
        const {email} = req.body;
        const {phone} = req.body;
        const {teamId} = req.body;
        let {isAlarm} = req.body;

        let query = `
                UPDATE users
                SET
                username='${userName}',
                email='${email}',
                phone='${phone}',
                is_alarm=${isAlarm}
                WHERE
                id='${userId}'
                `;
        await conn.query(query);

        query = `
                UPDATE department_team_user
                SET
                department_team_id=${teamId}
                WHERE
                user_id = ${userId};
                `;
        await conn.query(query);

        res.json({
            code: 1,
            msg: process.env.DB_UPDATE_MSG,
        });

    } catch (error) {
        res.json({
            code: 0,
            msg: process.env.DB_ERROR_MSG,
            error
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});

// 사용자 삭제
router.delete('/setting/user', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();

        let {selectUser} = req.body;
        (selectUser)

        await conn.beginTransaction();

        let query = '';
        let result;
        for (let userId of selectUser) {
            query = `delete from users where id='${userId}'`;
            result = await conn.query(query);
        }

        await conn.commit();

        res.json({
            code: 1,
            msg: process.env.DB_DELETE_MSG,
        });

    } catch (error) {
        res.json({
            code: 0,
            msg: process.env.DB_ERROR_MSG,
            error
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});


//모듈로 사용할 수 있도록 export
module.exports = router;
