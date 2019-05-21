// todo: to be removed when it's in production
const debug = require("debug")("mds:server:events");
const dmp = require("util").inspect;

const express = require("express");
const AM = require("../modules/account-manager");

module.exports = function (app) {
    let router = new express.Router();

    // Middleware for checking credentials
    router.use("/", function (req, res, next) {
        AM.validateLoginKey(app.dbs.users, req.cookies.login, req.ip, function (error, results) {
            if (error) {
                return next(error);
            }

            if (results) {
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
            async function (error, account) {
                if (error) {
                    return next(error);
                } else {
                    if (account) {
                        let auxArr = [];
                        for (let i = 0;i < account.friends.length; ++i) {
                            let obj = await app.dbs.users.findOne(
                                {
                                    "username": account.friends[i]
                                }
                            );

                                auxArr.push({
                                    "username": obj.username,
                                    "password": obj.password,
                                    "email": obj.email
                                })

                        }

                        console.log(auxArr);
                        res.json({
                            "friends": auxArr
                        });
                    }
                    else {
                        return next(new Error("Friends error"));
                    }
                }
            }
        );
    });

    router.post("/", function (req, res, next) {
        const username = req.body.username;

        app.dbs.users.findOne(
            {
                "cookie": req.cookies.login,
                "ip": req.ip
            },
            function (error, account) {
                if (error) {
                    return next(error);
                } else {
                    let collection = app.dbs.users;
                    app.dbs.users.findOne(
                        {
                            "username": username
                        }, function (err, doc) {
                            if (err) {
                                return next(err);
                            } else {
                                if (doc) {
                                    let friends = account.friends;
                                    if (!friends) {
                                        friends = [];
                                    }
                                    else {
                                        for (let i = 0; i < friends.length; ++i) {
                                            if (friends[i] === username) {
                                                return next(username + " is already friend");
                                            }
                                        }
                                    }

                                    friends.push(username);

                                    collection.updateOne(
                                        {
                                            "cookie": req.cookies.login,
                                            "ip": req.ip
                                        },
                                        {
                                            "$set": {
                                                "friends": friends
                                            }
                                        },
                                        function (e, r) {
                                            if (e) {
                                                return next(e);
                                            } else {
                                                res.json({
                                                    "message": "ok"
                                                });
                                            }
                                        }
                                    )
                                } else {
                                    return next(new Error("No user " + username + " found!"));
                                }
                            }
                        }
                    );
                }
            }
        );
    });

    return router;
};