const mariadb = require('mariadb');
require('dotenv').config();
console.log(12)
console.log(process.env.DB_HOST)
const pool = mariadb.createPool({
  host: process.env.DB_HOST,
  user: process.env.DB_USER,
  port: process.env.DB_PORT,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_DATABASE,
  multipleStatements: true,
  connectionLimit:5,
});

module.exports = pool;
