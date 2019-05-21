// todo: to be removed when it's in production
const debug = require("debug")("mds:server:events");
const dmp = require("util").inspect;

const express = require("express");
const multer = require("multer");
const del = require("del");
const cloudinary = require("cloudinary").v2;
const AM = require("../modules/account-manager");

cloudinary.config({
    cloud_name: "syncevent",
    api_key: "724147596452914",
    api_secret: 'FJEDJWXqtRHI0zNNXZ1KSXyPV_o'
});

const storage = multer.diskStorage({
    destination: function (req, file, cb) {
        cb(null, "./upload/");
    },
    filename: function (req, file, cb) {
        cb(null, file.fieldname + "-" + Date.now() + ".bmp");
    }
});

const upload = multer({ storage: storage });

module.exports = function (app) {
    let router = new express.Router();
    let username = null;
    // Middleware for checking credentials
    router.use("/", function (req, res, next) {
        AM.validateLoginKey(app.dbs.users, req.cookies.login, req.ip, function (error, results) {
            if (error) {
                return next(error);
            }

            if (results) {
                username = results["username"];
                return next();
            } else {
                return next(new Error("You need to be logged in!"));
            }
        });

    });

    router.get("/", function (req, res, next) {
        app.dbs.users.findOne(
            {
                "cookie": req.cookies.login,
                "ip": req.ip
            }, function (e, result) {
                if (e) {
                    return next(e);
                }

                if (result && result.photo) {
                    res.send({"photo": result.photo});
                } else {
                    return next(new Error("no photo"));
                }
            }
        );
    });

    router.post("/", upload.single("upload"), function (req, res, next) {
        let tmp_path = req.file.path;
        cloudinary.uploader.upload(tmp_path, {tags: "basic_avatar"}, function (err, image) {
            if (err) {
                return next(err);
            }
            app.dbs.users.findOneAndUpdate(
                {
                    "cookie": req.cookies.login,
                    "ip": req.ip
                },
                {
                    "$set": {
                        photo: image.url
                    }
                }, function (e, result) {
                    if (e) {
                        return next(e);
                    }
                    del([tmp_path]);
                    res.json({
                        "message": "Image: " +image.url + " uploaded with succes."
                    });
                }
            );
        });
    });


    return router;
};