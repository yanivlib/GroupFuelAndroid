require('cloud/common.js');

Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

Parse.Cloud.define("getCarMakes",function(req, res) {
    // Initialize a query object for table CarModel
    var query = new Parse.Query("CarModel");
    query.select(["Make"]);
    query.find({
            success: function (results) {
//                results = distinct(results);
                console.log(" --- getCarMakes results: " + results);
                res.success(results);
            },
            error: function () {
                res.error("Failed to retrieve car makers.");
            }
        });
});

Parse.Cloud.define("getCarModels", function(req, res) {
    var make = req.params.make;
    if (make === undefined) {
        res.error("Car Maker is a required field.");
    }
    else {
        var query = new Parse.Query("CarModel");
        query.equalTo("Make", make);
        query.find({
            success: function (results) {
//                results = distinct(results);
                console.log(" --- getCarModels results: " + results);
                res.success(results);
            },
            error: function () {
                res.error("Failed to retrieve car models for maker " + make);
            }
        });
    }
});

Parse.Cloud.define("getOwnedCars", function(req, res) {
    var user = req.user;
    if (user === undefined) {
        res.error("You must be logged in");
    }
    else {
        console.log(" --- getOwnedCars user: " + user);
        var query = new Parse.Query("Car");
        query.equalTo("Owner", {
            __type: "Pointer",
            className: "_User",
            objectId: user.objectId
        });
        query.find({
            success: function (results) {
//                results = distinct(results);
                console.log(" --- getOwnedCars results: " + results);
                res.success(results);
            },
            error: function () {
                res.error("Failed to retrieve owned cars");
            }
        });
    }
});

Parse.Cloud.define("getOwnedCars_2", function(req, res) {
    var user = req.user;
    if (user === undefined) {
        res.error("You must be logged in");
    }
    else {
        console.log(" --- getOwnedCars_2 user: " + user);
        var query = new Parse.Query("Car");
        query.equalTo(user);
        query.find({
            success: function (results) {
//                results = distinct(results);
                console.log(" --- getOwnedCars_2 results: " + results);
                res.success(results);
            },
            error: function () {
                res.error("Failed to retrieve owned cars");
            }
        });
    }
});
