const express = require('express');
const router = express.Router();
const pool = require('../../config/database');

//부서 리스트
router.get('/setting/departments', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();
        const query = `select department_team_id,
       user_id,
       department_id,
       team_name,
       username,
       d.name as departement_name,
       #password,
       #verified,
       #create_ip,
       email,
       phone,
       is_alarm,
       count(*) as \`count\`
from department_team_user tu
         inner join department_team dt on tu.department_team_id = dt.id
         inner join users u on tu.user_id = u.id
         inner join departments d on dt.department_id = d.id
group by department_team_id;`;

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

// 부서 생성
router.post('/setting/department', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();

        const {name} = req.body;
        let query = `
    INSERT INTO departments (name)
    VALUES('${name}');`;
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

// 부서 수정
router.put('/setting/department', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();

        const {id} = req.body;
        const {name} = req.body;
        let query = `
                UPDATE departments
                SET
                name='${name}',
                WHERE
                id='${id}'
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

// 부서 삭제
router.delete('/setting/department', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();

        let {selectUser} = req.body;

        await conn.beginTransaction();

        let query = '';
        let result;
        for (let userId of selectUser) {
            query = `delete from departments where id='${userId}'`;
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
