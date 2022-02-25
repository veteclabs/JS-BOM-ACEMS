const express = require('express');
const router = express.Router();
const pool = require('../../config/database');


router.get('/setting/targets', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();
        const query = `
        select 
        d.id departmentId,
        d.name departmentName,
        dt.day_kWh_target,
        dt.month_kWh_target,
        dt.year_kWh_target
        from department_target dt
        left join departments d on dt.department_id = d.id
        ;
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

router.post('/setting/target', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();
        const departmentId = req.body.departmentId;
        const day_kWh_target = req.body.day_kWh_target;
        const month_kWh_target = req.body.month_kWh_target;
        const year_kWh_target = req.body.year_kWh_target;

        const id = req.params.departmentId;

        let query = `
insert into department_target 
values (
${departmentId},
${day_kWh_target},
${month_kWh_target},
${year_kWh_target})
`;
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
router.delete('/setting/target/:departmentId', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();

        const id = req.params.departmentId;

        let query = `
                delete from department_target
                WHERE
                department_id=${id}
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
router.put('/setting/target/:departmentId', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();

        const departmentId = req.params.departmentId;
        const day_kWh_target = req.body.day_kWh_target;
        const month_kWh_target = req.body.month_kWh_target;
        const year_kWh_target = req.body.year_kWh_target;


        let query = `
                UPDATE department_target
                SET 
                day_kWh_target = ${day_kWh_target},
                month_kWh_target = ${month_kWh_target},
                year_kWh_target = ${year_kWh_target}
                WHERE
                department_id=${departmentId}
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
router.put('/setting/targets', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();
        const targets = req.body.target;

        for (let i in targets) {
            let query = `
                UPDATE department_target
                SET 
                day_kWh_target = ${targets[i].day_kWh_target},
                month_kWh_target = ${targets[i].month_kWh_target},
                year_kWh_target = ${targets[i].year_kWh_target}
                WHERE
                department_id=${targets[i].departmentId}
                `;
            await conn.query(query);
        }

        res.json({
            code: 1,
            msg: process.env.DB_UPDATE_MSG,
        });

    } catch (error) {
        (error)
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


router.get('/setting/substation/targets', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();
        const query = `
        select 
        d.id substationId,
        d.name substationName,
        dt.day_kWh_target,
        dt.month_kWh_target,
        dt.year_kWh_target
        from substation_target dt
        left join substation d on dt.substation_id = d.id
        ;
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

router.post('/setting/substation/target', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();
        const substationId = req.body.substationId;
        const day_kWh_target = req.body.day_kWh_target;
        const month_kWh_target = req.body.month_kWh_target;
        const year_kWh_target = req.body.year_kWh_target;

        const id = req.params.departmentId;

        let query = `
insert into substation_target 
values (
${substationId},
${day_kWh_target},
${month_kWh_target},
${year_kWh_target})
`;
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
router.delete('/setting/substation/target/:departmentId', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();

        const id = req.params.departmentId;

        let query = `
                delete from department_target
                WHERE
                department_id=${id}
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
router.put('/setting/substation/target/:departmentId', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();

        const departmentId = req.params.departmentId;
        const day_kWh_target = req.body.day_kWh_target;
        const month_kWh_target = req.body.month_kWh_target;
        const year_kWh_target = req.body.year_kWh_target;


        let query = `
                UPDATE department_target
                SET 
                day_kWh_target = ${day_kWh_target},
                month_kWh_target = ${month_kWh_target},
                year_kWh_target = ${year_kWh_target}
                WHERE
                department_id=${departmentId}
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
router.put('/setting/substation/targets', async function (req, res, next) {
    let conn;
    try {
        conn = await pool.getConnection();
        const targets = req.body;
        for (let taget of targets) {
            let query = `
                UPDATE substation_target
                SET 
                day_kWh_target = ${taget.day_kWh_target},
                month_kWh_target = ${taget.month_kWh_target},
                year_kWh_target = ${taget.year_kWh_target}
                WHERE
                substation_id=${taget.substationId}
                `;
            await conn.query(query);
        }

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

//모듈로 사용할 수 있도록 export
module.exports = router;
