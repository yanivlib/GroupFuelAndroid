/**
 * Created by Tomer on 27/04/2015.
 */
(function xmlToJson (path) {
    var fs = require('fs');
    var path = require('path');
    var parser = require('xml2json');

    console.log("start run");

    var pathToXml = path.join(__dirname,'/data/cars.xml');
    var json = {};
    console.log("start run");

    console.log("path to xml is: " + pathToXml);

    fs.readFile(pathToXml, 'utf8', function (err,data) {
        if (err) {
            return console.log(err);
        }
        if (data === undefined) {
            console.log("data is undefined");
            return;
        }
        else {
            console.log("data is");
            console.log(data);
        }
        json = parser.toJson(data);
    });
    for (key in json) {
        console.log("key is: " + key + " and value is: " + json[key]);
    }
})();