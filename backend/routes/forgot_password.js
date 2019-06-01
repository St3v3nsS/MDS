const express = require("express");
const randomize = require("randomatic");
const AM = require("../modules/account-manager");
const EM = require("../modules/email-dispatcher");
const Isemail = require("isemail");

/**
 * Create route forgot_password
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
     * Middleware for checking if email is already used
     */
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


    /**
     * Generate a security code and send it by email so that a user
     * can reset their password.
     */
    router.post('/', function (req, res, next) {
        const securityCode = randomize('0',6);
        const email = req.body.email;

        AM.updateSecurityKey(app.dbs.users, email, req.ip, securityCode, function (error, account) {
            if (error) {
                return next(error);
            } else {
                EM.dispatchResetPasswordCode(account, function (err, message) {
                    if (!err) {
                        res.json({
                            "message": "ok"
                        });
                    } else {
                        return next(new Error("Unable to dispatch security code!"));
                    }
                });
            }
        });
    });

    return router;
};