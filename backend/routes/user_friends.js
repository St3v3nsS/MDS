// todo: to be removed when it's in production
const debug = require("debug")("mds:server:events");
const dmp = require("util").inspect;

const express = require("express");

module.exports = function (app) {
    let router = new express.Router();

    // Middleware for checking credentials
    router.use("/", function (req, res, next) {
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

    router.get("/", function (req, res, next) {
        app.dbs.users.findOne(
            {
                "cookie": req.cookies.login,
                "ip": req.ip
            },
            function (error, account) {
                if (error) {
                    return next(error);
                } else {
                    if (account) {
                        return account.friends;
                    }
                    else {
                        return next(new Error("Friends error"));
                    }
                }
            }
        );
    });

    router.post("/", function (req, res, next) {
        app.dbs.users.findOne(
            {
                "cookie": req.cookies.login,
                "ip": req.ip
            },
            function (error, account) {
                if (error) {
                    return next(error);
                } else {
                    if (account) {
                        return account.friends;
                    }
                    else {
                        return next(new Error("Friends error"));
                    }
                }
            }
        );
    });

    return router;
};