const swaggerUi = require('swagger-ui-express');
const swaggereJsdoc = require('swagger-jsdoc');

const options = {
    swaggerDefinition: {
        info: {
            title: '정석이엔지 BOM-ACEMS',
            version: '1.0.0',
            description: 'API Document'
        },
        host: 'localhost:3015',
        basePath: '../api'
    },
    apis: ['../api/*.js','../api/*']
};
const specs = swaggereJsdoc(options);
module.exports = {swaggerUi, specs};

