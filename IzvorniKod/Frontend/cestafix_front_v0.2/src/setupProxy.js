const { createProxyMiddleware } = require('http-proxy-middleware');



//linkanje na produkcijski backend pri lokalnom test izvodenju
module.exports = function(app) {
 app.use(
  '/api',
  createProxyMiddleware({
    target: 'https://cestafix-be.onrender.com/',
    changeOrigin: true,
  })
 );
};