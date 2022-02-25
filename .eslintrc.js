module.exports = {
  rules: {
    'import/no-unresolved': 'off',
    'no-param-reassign': 0,
    'no-restricted-globals': ['off'],
    'max-len': [1, 120],
    'prefer-destructuring': 'off',
  },
  extends: [
    'airbnb-base',
    'plugin:vue/base',
  ],
};
