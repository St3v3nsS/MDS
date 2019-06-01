let EM = {};
module.exports = EM;

/**
 * Connect to email server using an account
 * @type {Client}
 */
EM.server = require("emailjs/email").server.connect({
    host: process.env.NL_EMAIL_HOST || "smtp.gmail.com",
    user: process.env.NL_EMAIL_USER || "andreinroscovanu@gmail.com",
    password: process.env.NL_EMAIL_PASS || "andreirosco7",
    ssl: true
});

/**
 * Send an email to an account
 * @param account
 * @param callback
 */
EM.dispatchResetPasswordCode = function(account, callback) {
    EM.server.send({
        from: process.env.NL_EMAIL_FROM || "andreinroscovanu@gmail.com",
        to: account.email,
        subject: "Password Reset",
        text: EM.composeEmail(account)
    }, callback);
};

/**
 * Compose a text for email
 * @param account
 * @returns {string}
 */
EM.composeEmail = function(account) {
    let text = "Hello " + account.username + ",\n\n";
    text += "Your security token is: " + account.securityCode + "\n\n";
    text += "Cheers,\nAndrei Roscovanu";
    return text;
};