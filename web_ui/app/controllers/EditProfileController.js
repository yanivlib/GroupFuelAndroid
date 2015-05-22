/**
 * Created by matansab on 5/18/2015.
 */
app.controller('EditProfileController', function ($scope, $location, UserService) {
    'use strict';
    var CleanUserDetails = {
        userName: "",
        firstName: "",
        lastName: "",
        email: ""
    };
    (function () {
        $scope.UserService = UserService;
        $scope.userDetails = {};
        getUserDetails();
    })();

    $scope.doUpdate = function () {
        if (!$scope.UserService.logged ||
            $scope.userDetails.userName === "" || $scope.userDetails.firstName === "" ||
            $scope.userDetails.lastName === "" || $scope.userDetails.email === "") {
            // TODO - add notification error
            return;
        }
        var currentUser = $scope.UserService.currentUser;
        currentUser.set("username", $scope.userDetails.userName);
        currentUser.set("FirstName", $scope.userDetails.firstName);
        currentUser.set("LastName", $scope.userDetails.lastName);
        currentUser.set("email", $scope.userDetails.email);
        currentUser.save(null, {
            success: function (user) {
                // TODO - reload details
                console.log("update success");
            },
            error: function (user, err) {
                // TODO - show error
                console.log("update failed");
            }
        });
    };

    $scope.$watch('UserService.logged', getUserDetails);

    function getUserDetails() {
        if ($scope.UserService.logged) {
            var currentUser = $scope.UserService.currentUser;
            $scope.userDetails.userName = currentUser.get('username');
            $scope.userDetails.firstName = currentUser.get('FirstName');
            $scope.userDetails.lastName = currentUser.get('LastName');
            $scope.userDetails.email = currentUser.get('email');
        }
        else {
            $scope.userDetails = angular.copy(CleanUserDetails);
        }
        console.log($scope.userDetails);
    }
});
var common = require('cloud/common.js');

Parse.Cloud.define("hello", function (request, response) {
    response.success("Hello world!");
});

/*
 * returns a list of all car makers in the database.
 */
Parse.Cloud.define("getCarMakes", function (req, res) {
    // Initialize a query object for table CarModel
    var query = new Parse.Query("CarModel");
    query.select(["Make"]);
    query.find({
        success: function (results) {
            results = distinct(results, "Make");
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
                results = distinct(results, "Model");
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

