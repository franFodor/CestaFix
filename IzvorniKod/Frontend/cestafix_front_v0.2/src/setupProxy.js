const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
 app.use(
  '/api',
  createProxyMiddleware({
    target: 'https://cestafix-be.onrender.com/',
    changeOrigin: true,
  })
 );
};