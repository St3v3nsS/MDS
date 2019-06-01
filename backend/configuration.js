/**
 * Environment variables used to configure the server
 */
const dbHost = process.env.DB_HOST || "localhost";
const dbPort = process.env.DB_PORT || 27017;
const dbName = process.env.DB_NAME || "app";
const dburl = "mongodb://" + dbHost + ":" + dbPort + "/" + dbName;

const serverPort = process.env.PORT || 3000;

/**
 * Export environment variables to be used in server.js
 */
exports.dburl = dburl;
exports.dbName = dbName;
exports.serverPort = serverPort;
exports.secretSession = process.env.SESSION_SECRET || "YM2onwOnkkRDn5gEwbyQxMQuHoPyFrzB";