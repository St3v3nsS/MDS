const express = require("express");

/**
 * Create route users
 * @param app
 * @returns {Router}
 */
module.exports = function (app) {
    let router = new express.Router();

    /**
     * GET all users from database
     */
    router.get("/", function (req, res, next) {
        app.dbs.users.find(
            {},
            {
                "email": 1,
                "username": 1
            },
        ).toArray(function (error, documents) {
            if (error) {
                return next(error);
            } else {
                if (documents && documents.length > 0) {
                    let sendObj = [];
                    for (let i = 0; i < documents.length; ++i) {
                        sendObj.push({
                            "email": documents[i].email,
                            "username": documents[i].username
                        });
                    }

                    res.json({
                        "users": sendObj
                    });
                } else {
                    return next(new Error("No users"));
                }
            }
        });
    });

    return router;
};