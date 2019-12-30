var exec = require('cordova/exec');

exports.toCustomActivity = function (arg0, success, error) {
    exec(success, error, 'allinpay', 'toCustomActivity', [arg0]);
};

exports.verifyWallet = function (arg0, success, error) {
    exec(success, error, 'allinpay', 'verifyWallet', [arg0]);
};
