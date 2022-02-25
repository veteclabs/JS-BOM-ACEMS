const express = require('express');

const router = express.Router();
const pool = require('../../config/database');
require('dotenv').config();

router.get('/setting/teams', async (req, res) => {
    let conn;
    try {
        let {departmentId} = req.query
        let departmentWhereIf = ''
        if (departmentId !== undefined) {
            departmentWhereIf = `AND department_id = ${departmentId}`
        }
        conn = await pool.getConnection();
        const query = `
        SELECT 
        t.id teamId, 
        t.team_name teamName,
        d.id departmentId,
        d.name departmendName
        from department_team t
        left join departments d on t.department_id = d.id
        where true
        ${departmentWhereIf}
        `;
        const value = await conn.query(query);
        res.json({
            code: 1,
            value
        });
    } catch (error) {
        res.json({
            code: 2,
            msg: process.env.DB_ERROR_MSG,
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});


router.put('/setting/team/:teamId', async (req, res) => {
    let conn;
    try {

        conn = await pool.getConnection();
        const query = `
        update 
        department_team 
        set 
        team_name = '${req.body.teamName}',
        department_id = '${req.body.departmentId}'
        where id = ${req.params.teamId}
        `;
        const value = await conn.query(query);
        res.json({
            code: 1,
            msg: process.env.DB_UPDATE_MSG,
        });
    } catch (error) {
        res.json({
            code: 2,
            msg: process.env.DB_ERROR_MSG,
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});

router.post('/setting/team', async (req, res) => {
    let conn;
    try {

        conn = await pool.getConnection();
        const query = `
        insert into
        department_team(\`team_name\`, \`department_id\`) values 
        ('${req.body.teamName}', '${req.body.departmentId}')
        `;

        await conn.query(query);
        res.json({
            code: 1,
            msg: process.env.DB_INSERT_MSG,
        });
    } catch (error) {
        (error)
        res.json({
            code: 2,
            msg: process.env.DB_ERROR_MSG,
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});


router.delete('/setting/team/:teamId', async (req, res) => {
    let conn;
    try {

        conn = await pool.getConnection();
        const query = `
        delete from department_team
        where id = ${req.params.teamId}
        `;
        await conn.query(query);
        res.json({
            code: 1,
            msg: process.env.DB_DELETE_MSG,
        });
    } catch (error) {
        res.json({
            code: 2,
            msg: process.env.DB_ERROR_MSG,
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});

router.delete('/setting/team', async (req, res) => {
    let conn;
    try {

        conn = await pool.getConnection();
        const query = `
        delete from department_team
        where id in (${req.body.ids})
        `;
        const value = await conn.query(query);
        res.json({
            code: 1,
            msg: process.env.DB_DELETE_MSG,
        });
    } catch (error) {
        res.json({
            code: 2,
            msg: process.env.DB_ERROR_MSG,
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});






module.exports = router;
