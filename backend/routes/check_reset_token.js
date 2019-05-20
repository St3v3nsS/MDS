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


    router.post('/', function (req, res, next) {
        const email = req.body.email;
        const securityCode = req.body.code;

        AM.validateSecurityCode(app.dbs.users, email, securityCode, function (err, account) {
            if (err) {
                return next(new Error("something went wrong"));
            } else {
                if (account) {
                    res.send("ok");
                } else {
                    return next(new Error("something went wrong"));
                }
            }
        });
    });

    return router;
};