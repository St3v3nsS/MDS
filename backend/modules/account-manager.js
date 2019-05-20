const crypto = require("crypto");
const moment = require("moment");

const guid = function () {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g,
            function(c) {
                let r = Math.random()*16|0,v=c=='x'?r:r&0x3|0x8;
                return v.toString(16);
            }
        );
};

exports.autoLogin = function (usersCollection, user, pass, callback) {
    usersCollection.findOne(
            {"username": user},
            function (error, results) {
                if (results) {
                    if (results["password"] === pass) {
                        return callback(null, results);
                    }
                    else {
                        return callback(new Error("wrong password"));
                    }
                } else {
                    return callback(null);
                }
            }
        );
};

exports.generateLoginKey = function (usersCollection, user, ipAdress, callback) {
    let cookie = guid();
    usersCollection.findOneAndUpdate(
            {"username": user},
            {"$set":
                    {
                        "ip": ipAdress,
                        "cookie": cookie
                    }
            },
            {
                // We set returnOriginal to false
                // for returning the updated document
                // rather than the original
                "returnOriginal": false
            },
            function (error, results) {
                if (error)  {
                    return callback(error);
                }

                return callback(null, cookie);
            }

        );
};

exports.validateLoginKey = function (usersCollection, cookie, ipAddress, callback) {
    // ensure the cookie maps to the user's last recorded ip address
    usersCollection.findOne({"cookie": cookie, "ip": ipAddress}, callback);
};

exports.generatePasswordKey = function (usersCollection, email, ipAddress, callback) {
    let passKey = guid();
    usersCollection.findOneAndUpdate(
        {"email": email},
        {
            "$set":
                {
                    "ip": ipAddress,
                    "passKey": passKey
                },
            "$unset":
                {
                    "cookie": ''
                }
        },
        {
            "returnOriginal": false
        },
        function (error, result) {
            if (result.value != null) {
                return callback(null, result.value);
            }
            else {
                return callback(error || "account not found");
            }
        }
    );
};

exports.updateSecurityKey = function (usersCollection, email, ipAddress, securityCode, callback) {
    usersCollection.findOneAndUpdate(
        {"email": email},
        {
            "$set":
                {
                    "ip": ipAddress,
                    "securityCode": securityCode
                },
            "$unset":
                {
                    "cookie": ''
                }
        },
        {
            "returnOriginal": false
        },
        function (error, result) {
            if (result.value != null) {
                return callback(null, result.value);
            }
            else {
                return callback(error || "account not found");
            }
        }
    );
};

exports.validateSecurityCode = function (usersCollection, email, securityCode, callback) {
    usersCollection.findOneAndUpdate(
        {
            "email": email,
            "securityCode": securityCode
        },
        {
            "$set":
                {
                    "codeValidation": true
                },
            "$unset":
                {
                    "securityCode": '',
                    "cookie": ''
                }
        },
        {
            "returnOriginal": false
        },
        function (error, result) {
            if (result.value != null) {
                return callback(null, result.value);
            }
            else {
                return callback(error || "account not found");
            }
        }
    );
};

exports.validatePasswordKey = function (usersCollection, passKey, ipAddress, callback) {
    // ensure the passKey maps to the user's last record ip address
    usersCollection.findOneAndUpdate(
        {"email": email},
        {
            "$set":
                {
                    "ip": ipAddress,
                    "passKey": passKey
                },
            "$unset":
                {
                    "cookie": ''
                }
        },
        {
            "returnOriginal": false
        },
        function (error, result) {
            if (result.value != null) {
                return callback(null, result.value);
            }
            else {
                return callback(error || "account not found");
            }
        }
    );
};

exports.updatePassword = function (usersCollection, email, newPass, callback) {
    exports.saltAndHash(newPass, function (hash) {
        newPass = hash;
        usersCollection.findOneAndUpdate(
            {
                "email": email,
                "codeValidation": true
            },
            {
                "$set":
                    {
                        "password": newPass
                    },
                "$unset":
                    {
                        "codeValidation": false
                    }
            },
            {
                "returnOriginal": false
            },
            callback
        );
    });
};

exports.deleteAccount = function (usersCollection, id, callback) {
    usersCollection.deleteOne({"_id": getObjectId(id)}, callback);
};

exports.deleteAllAcounts = function (usersCollection, callback) {
    usersCollection.deleteMany({}, callback);
};

function generateSalt() {
    let set = "0123456789abcdefghijklmnopqurstuvwxyzABCDEFGHIJKLMNOPQURSTUVWXYZ";
    let salt = '';
    for (let i = 0; i < 10; ++i) {
        let p = Math.floor(Math.random() * set.length);
        salt += set[p];
    }

    return salt;
}

function md5 (str) {
    return crypto.createHash("md5").update(str).digest("hex");
}

exports.saltAndHash = function (pass, callback) {
    let salt = generateSalt();
    return callback(salt + md5(pass + salt));
};

exports.validatePassword = function (plainPass, hashedPass, callback) {
    let salt = hashedPass.substr(0, 10);
    let validHash = salt + md5(plainPass + salt);
    console.log(hashedPass, validHash);
    return callback(null, hashedPass === validHash);
};

function getObjectId (id) {
    return new require("mongodb").ObjectID(id);
}