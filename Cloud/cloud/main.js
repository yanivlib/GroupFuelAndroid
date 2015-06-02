var common = require('cloud/common.js');

/*
 * returns a list of all car makers in the database.
 */
Parse.Cloud.define("getCarMakes", function (req, res) {
    // Initialize a query object for table CarModel
    var query = new Parse.Query("CarModel");
    query.select(["Make"]);
    query.find({
        success: function (results) {
            var distinctResults = distinct(results, 'Make');
            res.success(distinctResults);
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
Parse.Cloud.define("getCarModels", function (req, res) {
    var make = req.params.Make;
    if (make === undefined) {
        res.error("Car Maker is a required field.");
    }
    else {
        var query = new Parse.Query("CarModel");
        query.equalTo("Make", make);
        query.find({
            success: function (results) {
                var retVal = {};
                retVal.resultSet = results;
                retVal.distinctModels = distinct(results, 'Model');
                res.success(retVal);
            },
            error: function () {
                res.error("Failed to retrieve car models for maker " + make);
            }
        });
    }
});
 
/*
 * gets an Owner, and returns a list of all the cars owned by the Owner.
 * if no Owner is provided, the current user is used. if no user is provided with the request, returns an error.
 * returns an array of ParseObjects. 
 */
Parse.Cloud.define("getOwnedCars", function (req, res) {
    var user = req.params.Owner || req.user;
    if (user === undefined) {
        res.error("You must be logged in");
    }
    else {
        console.log(" --- getOwnedCars user: " + user);
        var query = new Parse.Query("Car");
        query.equalTo("Owner", user);
        query.include("Model");
        query.find({
            success: function (results) {
                console.log(results);
                res.success(results);
            },
            error: function () {
                res.error("Failed to retrieve owned cars");
            }
        });
    }
});
 
Parse.Cloud.define("removeCar", function (req, res) {
    // TODO change find to first
    var user = req.user;
    if (user === undefined) {
        res.error("You must be logged in");
    }
    else {
        var carNumber = req.params.carNumber;
        var query = new Parse.Query("Car");
        query.equalTo("Owner", user);
        query.equalTo("CarNumber", carNumber);
        query.find({
            success: function (results) {
                if (!results[0] === undefined) {
                    results[0].destroy({
                        success: function (object) {
                            res.success("Object was deleted");
                        },
                        error: function () {
                            res.error("Deletedion was failed" + Parse.Error);
                        }
                    });
                }
            },
            error: function () {
                res.error("Are you sure this is your car?");
            }
        });
    }
});
 
Parse.Cloud.define("addCar", function (req, res) {
    var user = req.user;
    var carModel;
    var Car = Parse.Object.extend("Car");
    var car = new Car();
    if (user === undefined) {
        res.error("You must be logged in");
    }
    else {
        var carDetails = req.params.carDetails;
        var query = new Parse.Query("CarModel");
        query.equalTo("Make", carDetails.make);
        query.equalTo("Model", carDetails.model);
        query.equalTo("Volume", parseInt(carDetails.volume));
        query.equalTo("Year", parseInt(carDetails.year));
        query.equalTo("FuelType", carDetails.fuelType);
        query.select(["objectId"]);
 
        query.first({
            success: function (results) {
                if (results !== undefined){
                    carModel = results;
                    car.set("CarNumber", carDetails.car_number);
                    car.set("Owner", user);
                    car.set("Model", carModel);
                    car.set("Mileage", parseInt(carDetails.mileage));
                    car.save(null, {
                            success: function (car) {
                                res.success(car);
                            },
                            error: function (car, error) {
                                res.error('Failed to create new object, with error code: ' + error.message);
                            }
                        })                                                    
                }
            },
            error: function () {
                res.error("Couldn't find the model in the DB");
            }
        })
 
 
    }
});
 
 
function distinct(objectList, field) {
    var distinctArray = [];
    var tempDict = {};
    for (var i = 0; i < objectList.length; i++) {
        var tmp = objectList[i].get(field);
        if (tempDict[tmp] === undefined) {
            tempDict[tmp] = 1;
            distinctArray.push(tmp);
        }
    }
    return distinctArray;
}

Parse.Cloud.afterSave("Fueling", function(request) {
	var user = request.user;
	var fueling = request.object;
  
	var car = fueling.get("Car");
	console.log(JSON.stringify(car));
	car.fetch({
		success: function(result) {
			result.add("FuelEvents", {"__type":"Pointer","className":"Fueling","objectId":fueling.id});
			result.save(); 
		},
		error: function() {
			console.log(" --- Fueling afterSave trigger, fetch error");
		}
	});
});

Parse.Cloud.define("getUsage", function (req, res) {
	var query = new Parse.Query("Car");
	query.find({
		success: function(results) {
			for (var i = 0; i < results.length; i++) {
				if (results[i].get("FuelEvents") !== undefined) {
					var events = results[i].get("FuelEvents");
					Parse.Object.fetchAll(events,{
						success: function(res) {
							console.log(res.length + "  || " + res);
						},
						error: function() {
							console.log(" --- getUsage function fetch error");
						}
					});
				}
			}
		},
		error: function() {
			console.log(" --- getUsage function query error");
		}
	});
});