// tools for debugging
// todo: to be removed when it's in production
const debug = require("debug")("mds:server:logout");
const dmp = require("util").inspect;

// import modules
const express = require("express");

module.exports = function(app) {
    let router = new express.Router();

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