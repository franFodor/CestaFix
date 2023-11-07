const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(
    '/api', // Define the path you want to proxy
    createProxyMiddleware({
      target: 'http://localhost:8080', // Specify the target URL where your API is running
      changeOrigin: true,
    })
  );
};