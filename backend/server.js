/**
 * Import modules
 */
const MongoClient = require("mongodb").MongoClient;
const express = require("express");
const session = require("express-session");
const morgan = require("morgan");
const bodyParser = require("body-parser");
const cookieParser = require("cookie-parser");
const async = require("async");
const configuration = require("./configuration");

const app = express();
const serverPort = process.env.PORT || configuration.serverPort;

// Set morgan for development or production
if (app.get("env") === "development") {
    app.use(morgan("dev"));
}
else {
    app.use(morgan("common"));
}

console.log("Startup " + new Date());

/**
 * Use async.auto to determine the best order for running the tasks, based
 * on their requirements. Each function depend on other functions being
 * completed first and each function is run as soon as its requirements
 * are satisfied:
 *      -> First task will establish a connection to MongoDB and will create
 *      an object that will contain the collections users and events from db;
 *      -> Second task will set the routes that will be available on server;
 *      -> Third task will start the server.
 */
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
                app.dbs.events.createIndex({"name": 1}, {"background": true});
                app.dbs.events.createIndex({"endDate": 1}, {"background": true, "expireAfterSeconds": 0});
                // todo: to be removed
                console.log("Connected successfully to db");
                return callback(null, "Connected successfully to db");
            });
        }],
        router: ["mongodb", function (results, callback) {
            // cookies parser
            app.use(cookieParser());

            // parse incoming requests
            app.use(bodyParser.urlencoded({ extended: true }));
            app.use(bodyParser.json());

            // setup session
            app.use(session({
                secret: configuration.secretSession,
                proxy: true,
                resave: true,
                saveUninitialized: true
            }));

            // Middleware for registration
            app.use("/register", (require("./routes/register"))(app));

            // Middleware for login
            app.use("/login", (require("./routes/login"))(app));

            // Middleware for logout
            app.use("/logout", (require("./routes/logout"))(app));

            // Middleware for register
            app.use("/events", (require("./routes/events"))(app));

            // Middleware for forgot password
            app.use("/forgot_password", (require("./routes/forgot_password"))(app));

            // Middleware for check reset token
            app.use("/check_reset_token", (require("./routes/check_reset_token"))(app));

            // Middleware for updating password
            app.use("/new_password", (require("./routes/new_password"))(app));

            // Middleware for returning users
            app.use("/users", (require("./routes/users"))(app));

            // Middleware for user friends
            app.use("/friends", (require("./routes/friends"))(app));

            // Middleware for posting profile photo
            app.use("/profile_photo", (require("./routes/profile_photo"))(app));

            // Middleware for matching friends
            app.use("/match_friends", (require("./routes/match_friends"))(app));

            // Middleware for status 404
            app.use(function (req, res) {
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
            app.use(function (err, req, res) {
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