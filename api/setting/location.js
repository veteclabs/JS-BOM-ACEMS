const express = require('express');

const router = express.Router();
const pool = require('../../config/database');
require('dotenv').config();

router.get('/setting/substations', async (req, res) => {
    let conn;
    try {
        conn = await pool.getConnection();
        const query = 'SELECT id, name from substation';
        const value = await conn.query(query);
        res.json({
            code: 1,
            value
        });
    } catch (error) {
        res.json({
            code: 2,
            message: process.env.DB_ERROR_MSG,
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});

router.put('/setting/subsation/:subsationId', async (req, res) => {
    let conn;
    try {

        conn = await pool.getConnection();
        const query = `
        update 
        substation 
        set 
        name = '${req.body.name}'
        where id = ${req.params.subsationId}
        `;
        const value = await conn.query(query);
        res.json({
            code: 1,
            message: process.env.DB_UPDATE_MSG,
        });
    } catch (error) {
        res.json({
            code: 2,
            message: process.env.DB_ERROR_MSG,
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});

router.post('/setting/subsation', async (req, res) => {
    let conn;
    try {

        conn = await pool.getConnection();
        const query = `
        insert into
        substation(\`name\`) values 
        ('${req.body.name}')
        `;
        const value = await conn.query(query);
        res.json({
            code: 1,
            message: process.env.DB_INSERT_MSG,
        });
    } catch (error) {
        res.json({
            code: 2,
            message: process.env.DB_ERROR_MSG,
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});


router.delete('/setting/subsation/:subsationId', async (req, res) => {
    let conn;
    try {

        conn = await pool.getConnection();
        const query = `
        delete from substation
        where id = ${req.params.subsationId}
        `;
        const value = await conn.query(query);
        res.json({
            code: 1,
            message: process.env.DB_DELETE_MSG,
        });
    } catch (error) {
        res.json({
            code: 2,
            message: process.env.DB_ERROR_MSG,
        });
    } finally {
        if (conn) {
            await conn.release();
        }
    }
});






module.exports = router;
