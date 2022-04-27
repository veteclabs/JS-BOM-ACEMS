const express = require('express');

const router = express.Router();
const passport = require('passport');
const LocalStrategy = require('passport-local').Strategy;
const passwordHash = require('../../lib/passwordHash');
const pool = require('../../config/database');

passport.serializeUser((user, done) => {
  // user.Password = "";
  done(null, user);
});

passport.deserializeUser((user, done) => {
  const result = user;
  // result.Password = "";
  done(null, result);
});

// passport로 로그인사용자 검증
passport.use(
    new LocalStrategy(
        {
          usernameField: 'id',
          passwordField: 'password',
          passReqToCallback: true,
        },
        (async (req, id, password, done) => {
          let conn;
          try {
            conn = await pool.getConnection();
            console.log(id)
            const result = await conn.query('CALL usp_get_users(?, ?)', [id, passwordHash(password)]);
            const user = result[0][0];
            if (typeof user === 'undefined') {
              return done(null, false, { message: '아이디 또는 비밀번호 오류 입니다.' });
            }
            if (typeof user !== 'undefined') {
              req.session.authUser = user;
              return done(null, user, { message: '로그인 성공' });
            }
          } catch (error) {
            return done(null, false, { message: process.env.DB_ERROR_MSG });
          } finally {
            if (conn) {
              await conn.release();
            }
          }
          return true;
        }),
    ),
);

// 로그인 최초 시 passport 호출
router.post('/user/login', (req, res, next) => {
  passport.authenticate('local', (err, user, info) => {
    if (err) {
      next(err);
    }
    if (!user) {
      res.status(401);
      res.json({
        message: info.message,
        result: 'login fail',
      });
      return;
    }
    // eslint-disable-next-line no-shadow
    req.logIn(user, (err) => {
      if (err) {
        next(err);
      }
      res.status(200);
      res.json(user);
    });
  })(req, res, next);
});

// passport 로그아웃
router.get('/user/logout', (req, res) => {
  delete req.session.authUser;
  req.logout();
  res.redirect('/login');
});


// 모듈로 사용할 수 있도록 export
module.exports = router;
