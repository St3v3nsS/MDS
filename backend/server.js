const express = require('express');
const app = express();
const serverPort = 3000;

const MongoClient = require('mongodb').MongoClient;
const dburl = 'mongodb://localhost:27017';
const client = new MongoClient(dburl);
const dbName = 'app'
var db = null;

/*
 * Handler functions definitions
*/

function register (req, res) {
    console.log(req.headers)
    let username = req.get("username");
    let password = req.get("password");

    if (username == undefined || password == undefined) {
        res.status(400).end();
    } else {
        db.collection("users").find({'username': username}).toArray(function(err, docs) {
            if (err != null) {
                console.log(err);
            }
            
            if (docs.length == 0) {
                db.collection("users").insertOne({
                    "username" : username,
                    "password" : password,
                    "friends" : [],
                    "friend_requests" : [],
                }, function() {
                    res.status(200).end();
                });
            } else {
                res.status(400).end();
            }
        });
    }
}

function login (req, res) {
    console.log(req.headers)
    let username = req.get("username");
    let password = req.get("password");

    if (username == undefined || password == undefined) {
        res.status(400).end();
    } else {
        db.collection("users").find({'username': username, 'password' : password}).toArray(function(err, docs) {
            if (err != null) {
                console.log(err);
            }
            
            if (docs.length == 0) {
                res.status(400).end();
            } else if (docs.length == 1) {
                res.status(200).end();
            }
        });
    }
}

/*
 * Declare handler functions
*/

app.get('/register', register);
app.get('/login', login);


/*
 * Main function
*/

function main() {
    /* Start the db */
    client.connect(function(err) {
        if (err != null) {
            console.log(err);
        }
        console.log("Connected successfully to db");
      
        db = client.db(dbName);
    });

    app.listen(serverPort, function () {
        console.log(`App listening on port ${serverPort}`);
    })
}

/*
 * Start the main function
*/

main();
