/*
 * Environment variables used to configure the server
 */
const dbHost = process.env.DB_HOST || "mds-mongo";
const dbPort = process.env.DB_PORT || 27017;
const dbName = process.env.DB_NAME || "app";
const dburl = 'mongodb://' + dbHost + ":" + dbPort;
const serverPort = 3000;

/*
 * Export environment variables to be used in server.js
 */
module.exports.dburl = dburl;
module.exports.dbName = dbName;
module.exports.serverPort = serverPort;