/*
 * Import modules
 */
const MongoClient = require("mongodb").MongoClient;
const express = require("express");
const morgan = require("morgan");
const bodyParser = require("body-parser");
const async = require("async");
const configuration = require("./configuration");

const app = express();
const serverPort = process.env.PORT || configuration.serverPort;

// Set morgan for development
// todo: to be removed when it's in production
if (app.get("env") === "development") {
    app.use(morgan("dev"));
}
else {
    app.use(morgan("common"));
}

console.log("Startup " + new Date());

async.auto({
        mongodb: [function (callback) {
            const client = new MongoClient(configuration.dburl, {useNewUrlParser: true});

            client.connect(function(err) {
                if (err != null) {
                    return callback(err);
                }

                app.db = client.db(configuration.dbName);

                // Create app.dbs an object which will stores
                // references to all database collection
                app.dbs = {};
                app.dbs.users = app.db.collection("users");
                app.dbs.events = app.db.collection("events");

                // Ensure index on collections elements
                app.dbs.users.createIndex({"username": 1}, {"background": true});
                // todo: to be added when we decide how to index the events collection
                // app.dbs.events.createIndex({"events"})

                // todo: to be removed
                console.log("Connected successfully to db");
                return callback(null, "Connected successfully to db");
            });
        }],
        router: ["mongodb", function (results, callback) {
            // parse incoming requests
            app.use(bodyParser.urlencoded({ extended: true }));
            app.use(bodyParser.json());

            // Middleware for authentication
            app.use(function (req, res, next) {
                // todo: authentication, security
                return next();
            });

            // Middleware for registration
            app.use("/register", (require("./routes/register"))(app));

            // Middleware for login
            app.use("/login", (require("./routes/login"))(app));

            // Middleware for status 404
            app.use(function (req, res, next) {
                res.status(404).format({
                    json: function () {
                        res.jsonp({
                            ok: false,
                            error: '404',
                        });
                    },
                    default: function () {
                        res.send('404');
                    }
                });
            });

            // Middleware for status 500
            app.use(function (err, req, res, next) {
                res.status(500).format({
                    json: function () {
                        res.send({
                            ok: false,
                            message: 'Error: ' + (err.message || err),
                        })
                    },
                    default: function () {
                        res.send('Error.500: ' + (err.message || err));
                    }
                })
            });

            return callback(null, 'ok');
        }],
        startServer: ["mongodb", "router", function (results, callback) {
            app.listen(serverPort, function (err) {
                if (err) {
                    console.log(`Server can't start on port ${serverPort}`);
                    return callback(err);
                }

                console.log(`Server started with success on port ${serverPort}`);
                return callback();
            })
        }]
    }, function(error, res) {
        if (error) {
            console.log(error);
        }
    }
);