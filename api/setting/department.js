const express = require('express');
const router = express.Router();
const pool = require('../../config/database');


router.get('/setting/departments', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();
        const query = `select * from departments;`;

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

// 연락처 존재 확인
router.post('/setting/user/phone-check', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();
        const query = `SELECT COUNT(phone) AS cnt FROM users where phone = '${req.body.phone}'`;
        await conn.query(query);

        if(result[0].cnt === 0) {
            res.json(true)
        }else {
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
        const {email} = req.body;
        let {isAlarm} = req.body;


        let query = `
    INSERT INTO users (username,phone,email,is_alarm)
    VALUES('${userName}','${phone}','${email}',${isAlarm})`;
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
router.delete('/setting/department', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();

        const ids = req.body.ids;
        let query = `
                delete from departments
                WHERE
                id in (${ids})
                `;
        await conn.query(query);

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
// 부서 수정
router.put('/setting/department/:departmentId', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();

        const id = req.params.departmentId;
        const {name} = req.body;

        let query = `
                UPDATE departments
                SET
                name='${name}' 
                WHERE
                id=${id}
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

// 부서 수정
router.post('/setting/department', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();

        const name = req.body.name;
        let query = `
                insert into departments(\`name\`)
                values 
                ('${name}')
                `;
        await conn.query(query);
        query = `
                insert into department_target(\`department_id\`)
                values 
                ((select id from departments order by id desc limit 1))
                `;
        await conn.query(query);
        res.json({
            code: 1,
            msg: process.env.DB_INSERT_MSG,
        });

    } catch (error) {
        res.json({
            code: 0,
            msg: process.env.DB_INSERT_MSG,
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
