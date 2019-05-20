// tools for debugging
// todo: to be removed when it's in production
const debug = require("debug")("mds:server:register");
const dmp = require("util").inspect;

// import modules
const express = require("express");
const Isemail = require("isemail");
const moment = require("moment");
const AM = require("../modules/account-manager");

module.exports = function (app) {
    let router = new express.Router();

    // Middleware for checking if email is valid
    // todo: refactor as a extern function in other file
    router.use(function (req, res, next) {
        if (!req.body.email) {
            return next(new Error("No email"));
        }

        if (Isemail.validate(req.body.email)) {
            return next();
        }

        return next(new Error("Invalid email"));
    });

    // Middleware for checking if email is already used
    router.use(function (req, res, next) {
        app.dbs.users.findOne(
            {"email": req.body.email},
            // Set projection option to return just the id field
            {"_id": 1},
            // Callback
            // todo: refactor as a extern function for performance reason
            function (error, result) {
                if (error) {
                    return next(error);
                }

                if (result == null) {
                    return next();
                }
                else {
                    return next(new Error("Email is already used!"));
                }
            }
        );
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
                    return next();
                }
                else {
                    return next("username is already used!");
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

    router.post("/", function (req, res, next) {

        debug("register: " + dmp(req.body));
        // insert in mongo
        AM.saltAndHash(req.body.password, function (hash) {
            app.dbs.users.insertOne(
                // document
                {
                    "email": req.body.email,
                    "username": req.body.username,
                    "password": hash,
                    "date": moment().format('MMMM Do YYYY, h:mm:ss a')
                },
                // todo: refactor as a extern function
                function (err) {
                    if (err) {
                        return next(err);
                    }

                    let registerObject = {
                        "username": req.body.username,
                        "email": req.body.email,
                        "message": "OK"
                    };

                    // Succes insertion in db
                    return res.json(registerObject);
                }
            );
        });
    });

    return router;
};