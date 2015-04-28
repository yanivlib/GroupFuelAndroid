require('cloud/common.js');

Parse.Cloud.define("hello", function(request, response) {
  response.success("Hello world!");
});

/*
 * returns a list of all car makers in the database.
 */
Parse.Cloud.define("getCarMakes",function(req, res) {
    // Initialize a query object for table CarModel
    var query = new Parse.Query("CarModel");
    query.select(["Make"]);
    query.find({
            success: function (results) {
                results = distinct(results);
                console.log(results);
                res.success(results);
            },
            error: function () {
                res.error("Failed to retrieve car makers.");
            }
        });
});

/*
 * gets a name of a car maker, returns an array of all car models from that maker in the database.
 * if no maker matches the given name, it returns an empty list.
 * if no maker id provided, returns an error.
 * returns an array of ParseObjects. 
 */
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
                results = distinct(results);
                console.log(results);
                res.success(results);
            },
            error: function () {
                res.error("Failed to retrieve car models for maker " + make);
            }
        });
    }
});

/*
 * gets a user name, and returns a list of all the cars owned by the user.
 * if no user is provided, the current user is used. if no user is provided with the request, returns an error.
 * returns an array of ParseObjects. 
 */
Parse.Cloud.define("getOwnedCars", function(req, res) {
    var user = req.user;
    if (user === undefined) {
        res.error("You must be logged in");
    }
    else {
        console.log(user);
        var query = new Parse.Query("Car");
        query.equalTo("Owner", user);
        query.find({
            success: function (results) {
                results = distinct(results);
                console.log(results);
                res.success(results);
            },
            error: function () {
                res.error("Failed to retrieve owned cars");
            }
        });
    }
});
