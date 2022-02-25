const express = require('express');

const router = express.Router();
const pool = require('../../config/database');
require('dotenv').config();

router.get('/setting/processes', async (req, res) => {
    let conn;
    try {
        conn = await pool.getConnection();
        const query = `
        SELECT 
        id, 
        name,
        cost_center as costCenter
        from 
        manufacturing_process
        order by id asc`;
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

router.put('/setting/process/:processId', async (req, res) => {
    let conn;
    try {

        conn = await pool.getConnection();
        let name = req.body.name;
        let costCenter = req.body.costCenter;
        let processId = req.params.processId;
        const query = `
        update 
        manufacturing_process 
        set 
        name = '${name}',
        cost_center = '${(costCenter)}'
        where id = ${(processId)}
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

router.post('/setting/process', async (req, res) => {
    let conn;
    try {

        conn = await pool.getConnection();
        const query = `
        insert into
        manufacturing_process(\`name\`, \`cost_center\`) values 
        ('${req.body.name}',
         '${req.body.costCenter}')
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


router.delete('/setting/process/:processId', async (req, res) => {
    let conn;
    try {

        conn = await pool.getConnection();
        const query = `
        delete from manufacturing_process
        where id = ${req.params.processId}
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

router.delete('/setting/process', async (req, res) => {
    let conn;
    try {

        conn = await pool.getConnection();
        const query = `
        delete from manufacturing_process
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
