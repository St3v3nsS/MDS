const express = require("express");

/**
 * Create route logout
 * @param app
 * @returns {Router}
 */
module.exports = function(app) {
    let router = new express.Router();

    /**
     * Destroy a session for a user
     */
    router.post("/", function (req, res, next) {
        res.clearCookie("login");
        req.session.destroy(function(error) {
            if (error) {
                return next(error);
            }

            res.send("ok");
        });
    });

    return router;
};