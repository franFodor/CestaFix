const express = require("express");
const { createProxyMiddleware } = require("http-proxy-middleware");
require("dotenv").config();
const path = require("path")

const app = express();


// Proxy

app.use(
    "/api",
    createProxyMiddleware({
        target: "https://cestafix-be.onrender.com/",
        changeOrigin: true,
    })
);

app.use(express.static(path.join(__dirname, 'build')))

app.listen(3000, () => {
    console.log(`Starting Proxy at :3000`);
});

app.get("*", async (req, res) => {
        res.sendFile(path.join(__dirname, 'build', 'index.html'))
    }
);