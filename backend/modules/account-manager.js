const crypto = require("crypto");

/**
 * Generate a random version of Globally Unique Identifier using random numbers
 * @returns {string}
 */
const guid = function () {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g,
            function(c) {
                let r = Math.random()*16|0,v=c=='x'?r:r&0x3|0x8;
                return v.toString(16);
            }
        );
};

/**
 * Perform an auto login into an account saved in usersCollection from database
 * using the username and password for validation
 * @param usersCollection
 * @param user
 * @param pass
 * @param callback
 */
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

/**
 * Generate a unique cookie for a user ( using guid function) to login
 * without password and save this key into usersCollection from database
 * @param usersCollection
 * @param user
 * @param ipAdress
 * @param callback
 */
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

/**
 * Ensure the cookie maps to the user's last recorded ip address
 * @param usersCollection
 * @param cookie
 * @param ipAddress
 * @param callback
 */
exports.validateLoginKey = function (usersCollection, cookie, ipAddress, callback) {
    usersCollection.findOne({"cookie": cookie, "ip": ipAddress}, callback);
};

/**
 * When a user forget his password a security code is created
 * and is put in the database. The function updateSecurityKey
 * is updating the security code in database.
 * @param usersCollection
 * @param email
 * @param ipAddress
 * @param securityCode
 * @param callback
 */
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

/**
 * When a user forget his password a security code is sent
 * via email to be validated. If the security code inserted
 * by user is the same as the security code from database
 * the user can change his password.
 * @param usersCollection
 * @param email
 * @param securityCode
 * @param callback
 */
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

/**
 * Update the password for a user.
 * @param usersCollection
 * @param email
 * @param newPass
 * @param callback
 */
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

/**
 * Delete an account from database
 * @param usersCollection
 * @param id
 * @param callback
 */
exports.deleteAccount = function (usersCollection, id, callback) {
    usersCollection.deleteOne({"_id": getObjectId(id)}, callback);
};

/**
 * Delete all accounts from database
 * @param usersCollection
 * @param callback
 */
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