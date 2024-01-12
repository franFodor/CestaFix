const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
 app.use(
  '/api',
  createProxyMiddleware({
    target: 'https://cestafix-be-fbrc.onrender.com/',
    changeOrigin: true,
  })
 );
};