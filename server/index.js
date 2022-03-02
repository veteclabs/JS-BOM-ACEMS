const express = require('express');
const consola = require('consola');
const { Nuxt, Builder } = require('nuxt');

const app = express();
const passport = require('passport');
const bodyParser = require('body-parser');
const flash = require('connect-flash');
const session = require('express-session');
// Import and Set Nuxt.js options
const MySQLStore = require('express-mysql-session')(session);
const config = require('../nuxt.config.js');

config.dev = !(process.env.NODE_ENV === 'production');
const { swaggerUi, specs } = require('../lib/swagger');


const dbConfig = require('../config/dbConfig.js');

async function start() {
  // Init Nuxt.js
  const nuxt = new Nuxt(config);

  const { host } = nuxt.options.server;
  const port2 = 3016;
  // Build only in dev mode
  if (config.dev) {
    const builder = new Builder(nuxt);
    await builder.build();
  } else {
    await nuxt.ready();
  }

  app.use(flash());

  app.use(bodyParser.json());
  app.use(bodyParser.urlencoded({ extended: true }));

  app.use(session({
    secret: 'afsdfiuhaiufheui123uh1i2hiuhqwd',
    resave: false,
    saveUninitialized: false,
    cookie: { maxAge: 24 * 12 * 60 * 1000 },
    store: new MySQLStore(dbConfig),
  }));

  app.use(passport.initialize());
  app.use(passport.session());
  app.use('/api-docs', swaggerUi.serve, swaggerUi.setup(specs,{ explorer: true }));

  app.use((req, res, next) => {
    app.locals.isLogin = req.isAuthenticated();
    app.locals.userData = req.user;
    next();
  });

  // Give nuxt middleware to express
  app.use(nuxt.render);

  // Listen the server
  app.listen(port2, host);
  consola.ready({
    message: `Server listening on http://${host}:${port2}`,
    badge: true,
  });
}
start();
