const express = require('express');
const app = express();
const serverPort = 3000;

const MongoClient = require('mongodb').MongoClient;
const dburl = 'mongodb://localhost:27017';
const client = new MongoClient(dburl);
const dbName = 'app'

/*
 * Handler functions definitions
*/

function register (req, res) {
    let username = req.get("username");
    let password = req.get("password");

    if (username == undefined || password == undefined) {
        res.status(400).end();
    }
}

/*
 * Declare handler functions
*/

app.get('/register', register);


/*
 * Main function
*/

function main() {
    app.listen(serverPort, function () {
        console.log(`App listening on port ${serverPort}`);
    })
}

/*
 * Start the main function
*/

main();
