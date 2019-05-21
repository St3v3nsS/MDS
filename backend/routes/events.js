// tools for debugging
// todo: to be removed when it's in production
const debug = require("debug")("mds:server:events");
const dmp = require("util").inspect;

// import modules
const express = require("express");
const AM = require("../modules/account-manager");
const moment = require("moment");

module.exports = function (app) {
    let router = new express.Router();

    // Middleware for checking credentials
    router.use("/", function (req, res, next) {
        console.log({
            "login": req.cookies.login,
            "ip": req.ip
        });

        AM.validateLoginKey(app.dbs.users, req.cookies.login, req.ip, function (error, results) {
            if (error) {
                return next(error);
            }

            if (results) {
                req.body["username"] = results["username"];
                return next();
            }
            else {
                return next(new Error("You need to be logged in!"));
            }
        });

    });

    // POST a new event to database
    router.post("/", function (req, res, next) {
        let username = null;
        if (!req.body["username"]) {
            return next(new Error("invalid username"));
        }
        else {
            username = req.body["username"];
        }

        if (!req.body["name"]) {
            return next(new Error("invalid event name"));
        }

        let startDate = null;
        if (!req.body["startDate"]) {
            return next(new Error("invalid start date"));
        }
        else {
            try {
                startDate = moment(req.body["startDate"], moment.ISO_8601).toDate();
            } catch (e) {
                return next(new Error("Invalid start date format!"));
            }
        }

        let endDate = null;
        if (!req.body["endDate"]) {
            return next(new Error("invalid end date"));
        }
        else {
            try {
                endDate = moment(req.body["endDate"], moment.ISO_8601).toDate();
            }
            catch (e) {
                return next(new Error("Invalid end date format!"));
            }
        }

        let friends = null;
        if (!req.body["friends"]) {
            return next(new Error("invalid friends"));
        }
        else {
            friends = req.body["friends"];
        }

        app.dbs.events.insertOne(
            {
                "username": username,
                "name": req.body["name"],
                "startDate": startDate,
                "endDate": endDate,
                "friends": friends
            },
            function (err, results) {
                if (err) {
                    return next(err);
                }
                console.log("succes insert");

                results["message"] = "ok";
                res.json({
                    "message": "ok"
                });
            }
        );
    });

    // GET all events for a user from database
    router.get("/", function (req, res, next) {
        let username = null;
        if (!req.body["username"]) {
            return next(new Error("invalid username"));
        }
        else {
            username = req.body["username"];
        }

        app.dbs.events.find({"username": username}).toArray(function (error, documents) {
            if (error) {
                return next(error);
            }

            if (documents instanceof Array && documents.length) {
                let auxArray = [];
                for (let i = 0;i < documents.length; ++i) {
                    auxArray.push({
                        "name": documents[i].name,
                        "startDate": new Date(documents[i].startDate).toString(),
                        "endDate": new Date(documents[i].endDate).toString(),
                        "friends": documents[i].friends
                    });
                }
                console.log(auxArray);
                res.json({
                    "events": auxArray
                });
            }
            else {
                return next(new Error("no events"));
            }
        });
    });

    // Delete route
    router.post("/del", function (req, res, next) {
        let name = req.body.name;

        app.dbs.events.deleteOne({
            "username": req.body.username,
            "name": name
        }, function (e, result) {
            if (e) {
                return next(e);
            }

            res.json({
                "message": "ok"
            });
        });
    });
    return router;
};