const express = require("express");
const AM = require("../modules/account-manager");
const Isemail = require("isemail");

/**
 * Create route check_reset_token
 * @param app
 * @returns {Router}
 */
module.exports = function (app) {
    let router = new express.Router();

    /**
     * Middleware to check if the email is valid
     */
    router.use(function (req, res, next) {
        if (!req.body.email) {
            return next(new Error("No email"));
        }

        if (Isemail.validate(req.body.email)) {
            return next();
        }

        return next(new Error("Invalid email"));
    });

    /**
     * Validate security code sent by user
     */
    router.post('/', function (req, res, next) {
        const email = req.body.email;
        const securityCode = req.body.code;

        AM.validateSecurityCode(app.dbs.users, email, securityCode, function (err, account) {
            if (err) {
                return next(new Error("something went wrong"));
            } else {
                if (account) {
                    res.json({
                        "message": "ok"
                    });
                } else {
                    return next(new Error("something went wrong"));
                }
            }
        });
    });

    return router;
};