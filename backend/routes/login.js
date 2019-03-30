// tools for debugging
// todo: to be removed when it's in production
const debug = require("debug")("mds:server:register");
const dmp = require("util").inspect;

// import modules
const express = require("express");
const Isemail = require("isemail");
const async = require("async");
const flatten = require("flat");

module.exports = function (app) {
    let router = new express.Router();

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


    // Middleware for password confirmation validation
    /*
    router.use(function (req, res, next) {
        if (!req.body.passwordConfirmation) {
            return next(new Error("no password confirmation"));
        }

        if (req.body.password !== req.body.passwordConfirmation) {
            return next(new Error("Passwords are different"));
        }

        return next();
    });
    */

    router.post("/", function (req, res, next) {

        debug("login: " + dmp(req.body));
        // insert in mongo
        app.dbs.users.findOne(
            {"password": req.body.password},
            // Set projection option to return just the id field
            {"_id": 1},
            // Callback
            // todo: refactor as a extern function for performance reason
            function (error, result) {
                if (error) {
                    return next(error);
                }

                if (result == null) {
                    return next(new Error("wrong password"));
                }
                else {
                    let loginObject = {
                        "username": result["username"],
                        "email": result["email"],
                        "message": "Success"
                    };

                    res.json(loginObject);
                }
            }
        );
    });

    return router;
};