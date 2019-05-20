// todo: to be removed when it's in production
const debug = require("debug")("mds:server:events");
const dmp = require("util").inspect;

const express = require("express");
const AM = require("../modules/account-manager");
const Isemail = require("isemail");


module.exports = function (app) {
    let router = new express.Router();

    // Middleware to check if the email is valid
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
                    return next(new Error("Invalid email"));
                }
                else {
                    return next();
                }
            }
        );
    });


    router.post('/', function (req, res, next) {
        const email = req.body.email;
        const password = req.body.password;

        AM.updatePassword(app.dbs.users, email, password, function (error, result) {
            if (error) {
                return next("Unable to update password!");
            } else {
                res.send("ok");
            }
        })
    });

    return router;
};