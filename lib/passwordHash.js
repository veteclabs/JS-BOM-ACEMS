const crypto = require('crypto');

const mysalt = 'vetec_solution_';

// eslint-disable-next-line func-names
module.exports = function (password) {
  return crypto.createHash('sha512').update(password + mysalt).digest('base64');
};
