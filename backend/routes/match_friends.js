// tools for debugging
// todo: to be removed when it's in production
const debug = require("debug")("mds:server:events");
const dmp = require("util").inspect;

// import modules
const express = require("express");
const AM = require("../modules/account-manager");
const moment = require("moment");

module.exports = function (app) {
    let router = new express.Router();

    // Middleware for checking credentials
    router.use("/", function (req, res, next) {
        console.log({
            "login": req.cookies.login,
            "ip": req.ip
        });

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

    router.get("/", async function (req, res, next) {
        let day = moment().date();
        let username = req.query.name;
        console.log(username);
        let eventsUser1 = await app.dbs.events.find({
            "username": req.body.username
        }).toArray();


        let eventsUser2 = await app.dbs.events.find({
            "username": username
        }).toArray();

        for (let i = 0; i < eventsUser1.length; ++i) {
            if (day !== moment(eventsUser1[i].startDate).date() - 1) {
                eventsUser1.splice(eventsUser1.indexOf(eventsUser1[i]), 1);
            }
        }

        for (let i = 0; i < eventsUser2.length; ++i) {
            if (day !== moment(eventsUser2[i].startDate).date() - 1) {
                eventsUser2.splice(eventsUser2.indexOf(eventsUser2[i]), 1);
            }
        }

        eventsUser2.sort(function (a, b) {
            return new Date(a.startDate) - new Date(b.startDate);
        });

        eventsUser1.sort(function (a, b) {
            return new Date(a.startDate) - new Date(b.startDate);
        });

        let freeIntervalForUser1 = [];
        for (let i = 0; i < eventsUser1.length - 1; ++i) {
            freeIntervalForUser1.push({
                "start": moment(eventsUser1[i].endDate, "Europe/Bucharest").subtract(3, "hours").format("HH:mm"),
                "end": moment(eventsUser1[i + 1].startDate, "Europe/Bucharest").subtract(3, "hours").format("HH:mm")
            });
        }

        freeIntervalForUser1.unshift({
            "start": moment().startOf('day').format("HH:mm"),
            "end": moment(eventsUser1[0].startDate, "Europe/Bucharest").subtract(3, "hours").format("HH:mm")
        });

        freeIntervalForUser1.push({
            "start": moment(eventsUser1[eventsUser1.length - 1].endDate, "Europe/Bucharest").subtract(3, "hours").format("HH:mm"),
            "end": moment().endOf('day').format("HH:mm")
        });

        let freeIntervalForUser2 = [];
        for (let i = 0; i < eventsUser2.length - 1; ++i) {
            freeIntervalForUser2.push({
                "start": moment(eventsUser2[i].endDate, "Europe/Bucharest").subtract(3, "hours").format("HH:mm"),
                "end": moment(eventsUser2[i + 1].startDate, "Europe/Bucharest").subtract(3, "hours").format("HH:mm")
            });
        }

        freeIntervalForUser2.unshift({
            "start": moment().startOf('day').format("HH:mm"),
            "end": moment(eventsUser2[0].startDate, "Europe/Bucharest").subtract(3, "hours").format("HH:mm")
        });

        freeIntervalForUser2.push({
            "start": moment(eventsUser2[eventsUser2.length - 1].endDate, "Europe/Bucharest").subtract(3, "hours").format("HH:mm"),
            "end": moment().endOf('day').format("HH:mm")
        });

        let i = 0, j = 0, n = freeIntervalForUser1.length, m = freeIntervalForUser2.length;
        let freeInterval = [];
        while (i < n && j < m) {
            if (freeIntervalForUser1[i].end < freeIntervalForUser2[j].end) {
                if (freeIntervalForUser1[i].start < freeIntervalForUser2[j].start) {
                    freeInterval.push({
                        "start": freeIntervalForUser1[i].start,
                        "end": freeIntervalForUser1[i].end
                    });
                }
                else {
                    freeInterval.push({
                        "start": freeIntervalForUser2[j].start,
                        "end": freeIntervalForUser1[i].end
                    });
                }
            } else {
                if (freeIntervalForUser1[i].start < freeIntervalForUser2[j].start) {
                    freeInterval.push({
                        "start": freeIntervalForUser1[i].start,
                        "end": freeIntervalForUser2[j].end
                    });
                }
                else {
                    freeInterval.push({
                        "start": freeIntervalForUser2[j].start,
                        "end": freeIntervalForUser2[j].end
                    });
                }
            }
            i++;
            j++;
        }

        let hours = [];
        for (let i = 0; i < freeInterval.length; ++i) {
            hours.push(freeInterval[i].start + '-' + freeInterval[i].end);
        }
        res.json({
            "hours": hours
        });
    });

    return router;
};
