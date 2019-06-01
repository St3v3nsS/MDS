const express = require("express");
const AM = require("../modules/account-manager");

/**
 * Create route friends
 * @param app
 * @returns {Router}
 */
module.exports = function (app) {
    let router = new express.Router();

    /**
     * Middleware for checking credentials
     */
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

    /**
     * GET all friends of a user from database
     */
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
                        if (!account.friends) {
                            account.friends = [];
                        }
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
                            });
                        }

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

    /**
     * Post a new friend to a user friends list from database
     */
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

    /**
     * Delete a friend of a user
     */
    router.post("/del", function (req, res, next) {
        const username = req.body.username;

        console.log(username);
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
                                                friends.splice(friends.indexOf(username), 1);
                                                break;
                                            }
                                        }
                                    }

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