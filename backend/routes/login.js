// tools for debugging
// todo: to be removed when it's in production
const debug = require("debug")("mds:server:login");
const dmp = require("util").inspect;

// import modules
const express = require("express");
const AM = require("../modules/account-manager");

module.exports = function (app) {
    let router = new express.Router();

    // Middleware for auto login
    router.use('/', function (req, res, next) {
        // check if the user has an auto login saved in a cookie
        if (req.cookies.login === undefined) {
            next();
        }
        else {
            // attempt automatic login
            AM.validateLoginKey(app.dbs.users, req.cookies.login, req.ip, function (error, results) {
                if (results) {
                    AM.autoLogin(app.dbs.users, results["username"], results["password"], function(e, o) {
                        if (o) {
                            console.log(o);
                            req.session.user = results;

                            let loginObject = {
                                "username": results["username"],
                                "email": results["email"],
                                "message": "success"
                            };

                            res.json(loginObject);
                        }
                        else {
                            return next(new Error("Please login to your account!"));
                        }
                    });
                }
                else {
                    next();
                }
            });
        }
    });

    // Middleware for checking if username is valid
    router.use(function (req, res, next) {
        if (!req.body.username) {
            return next(new Error("No username"));
        }

        // todo: modify regex
        if (/[^A-Za-z0-9]+/.test(req.body.username)) {
            return next(new Error("Username is invalid!"));
        }

        return next();
    });

    // Middleware for checking username duplicate
    router.use(function (req, res, next){
        app.dbs.users.findOne(
            {"username": req.body.username},
            // Set projection option to return just the id field
            {"_id": 1},
            // Callback
            // todo: refactor as a extern function for performance reason
            function (error, result) {
                if (error) {
                    return next(error);
                }

                if (result == null) {
                    return next(new Error("wrong username"));
                }
                else {
                    return next();
                }
            }
        );
    });

    // Middleware for password validation
    router.use(function (req, res, next) {
        if (!req.body.password) {
            return next(new Error("no password"));
        }

        return next();
    });

    router.post("/", function(req, res, next) {

        debug("login: " + dmp(req.body));
        console.log(req.body.password);
        // insert in mongo
        app.dbs.users.findOne(
            {"username": req.body.username},
            // Set projection option to return just the id field
            {"_id": 1},
            // Callback
            function(error, results) {
                if (error) {
                    return next(error);
                }

                AM.validatePassword(req.body.password, results["password"], function(err, o) {
                    if (o) {
                        req.session.user = results;
                        if (req.body["remember-me"] === 'false') {
                            results["message"] = "success";
                            res.json(results);
                        }
                        else {
                            AM.generateLoginKey(app.dbs.users, results["username"], req.ip, function(e, key) {
                                res.cookie("login", key, { maxAge: 2147483647 });
                                results["message"] = "success";
                                res.json(results);
                            });
                        }
                    }
                    else {
                        return next(new Error("wrong password"));
                    }
                });
            }
        );
    });

    return router;
};