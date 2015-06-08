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

Parse.Cloud.beforeSave("Fueling", function(req, res) {
    var fueling = req.object;

    var car = fueling.get("Car");
    car.fetch({
        success: function(result) {
			if (fueling.get("Mileage") > car.get("Mileage")) {
				res.success();
			}
			else {
				res.error("Error: current mileage must be bigger than saved mileage");
			}
        },
        error: function() {
            res.error("Error: failed to fetch car object");
        }
    });
});

Parse.Cloud.afterSave("Fueling", function(request) {
    var user = request.user;
    var fueling = request.object;
   
    var car = fueling.get("Car");
    console.log(JSON.stringify(car));
    car.fetch({
        success: function(result) {
            result.add("FuelEvents", {"__type":"Pointer","className":"Fueling","objectId":fueling.id});
			result.set("Mileage", fueling.get("Mileage"));
			result.save();
        },
        error: function() {
            console.log(" --- Fueling afterSave trigger, fetch error");
        }
    });
});
 
Parse.Cloud.define("getUsage", function (req, res) {
    var cleanStats = {
		totalPrice: 0,
		totalAmount: 0,
		startingMileage: 0,
		currentMileage: 0,
		numOfEvents: 0
	};
	var cars = req.params.cars;
	// Initialising the statistics dictionaries for each car
	var carsStats = {};
	for (var k = 0; k < cars.length; k++) {
		// Since there is no default deep clone function (and we dont want to start including packages for everything
		// because parse sucks) we're abusing JSON to clone the object
		carsStats[cars[k].id] = JSON.parse(JSON.stringify(cleanStats));
	}
	// Initialising entry for total usage for group - we'll add it to carsStats at the end so we won't iterate over it.
	total = JSON.parse(JSON.stringify(cleanStats));
	var events = [];
	Parse.Object.fetchAll(cars, {
		success: function (carObjects) {
			// Creating list of events for all cars for one big fetch
			for (var i = 0; i < carObjects.length; i++) {
				// We check the current mileage here, and also add it as starting mileage (so 
				// we can find the minimum later when iterating over fuel events)
				carsStats[carObjects[i].id].startingMileage = carObjects[i].get("Mileage");
				carsStats[carObjects[i].id].currentMileage = carObjects[i].get("Mileage");
                if (carObjects[i].get("FuelEvents") !== undefined) {
                    events = events.concat(carObjects[i].get("FuelEvents"));
                }
            }
			// Fetching all fuel events at one time
			Parse.Object.fetchAll(events, {
				success: function (events) {
					for (var j = 0; j < events.length; j++) {
						var carId = events[j].get("Car").id;
						carsStats[carId].numOfEvents += 1;
						carsStats[carId].totalPrice += events[j].get("Price");
						carsStats[carId].totalAmount += events[j].get("Amount");
						if (carsStats[carId].startingMileage > events[j].get("Mileage")) {
							carsStats[carId].startingMileage = events[j].get("Mileage");
						}
					}
					Object.keys(carsStats).forEach(function (key) {
						var stats = carsStats[key];
						total.currentMileage += stats.currentMileage - stats.startingMileage;
						total.numOfEvents += stats.numOfEvents;
						total.totalPrice += stats.totalPrice;
						total.totalAmount += stats.totalAmount;
					});
					carsStats.total = total;
					res.success(carsStats);
				},
				error: function () {
					console.log("Error: failed to fetch fuel events");
					res.error("Error: failed to fetch fuel events");
				}
			});
		},
		error: function () {
			console.log("Error: failed to fetch cars");
			res.error("Error: failed to fetch cars");
		}
	});
});

Parse.Cloud.define("getUsageTest", function (req, res) {
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