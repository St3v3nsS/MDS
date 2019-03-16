/*
 * Environment variables used to configure the server
 */
const dburl = 'mongodb://localhost:27017';
const dbName = 'app';
const serverPort = 3000;

/*
 * Export environment variables to be used in server.js
 */
module.exports.dburl = dburl;
module.exports.dbName = dbName;
module.exports.serverPort = serverPort;