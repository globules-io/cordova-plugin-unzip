var exec = require('cordova/exec');
var Unzip = {
     unpack : function (src, dest, success, error) {      
          exec(success, error, 'Unzip', 'unpack', [src, dest]);
     }
};