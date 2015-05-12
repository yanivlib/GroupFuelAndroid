app.controller('SignupController', function ($scope, $location, UserService) {
    'use strict';

    var CleanUserDetails = {
        userName: "",
        firstName: "",
        lastName: "",
        email: "",
        password: "",
        repeatPassword: ""
    };

    (function () {
        $scope.signupDetails = angular.copy(CleanUserDetails);
        $scope.UserService = UserService;
        if ($scope.UserService.logged) {
            $location.url('/welcome');
        }
    })();

    // TODO - move doSignUp server call to userservice.js
    $scope.doSignUp = function () {
        var details = $scope.signupDetails;
        if (details.userName === "" || details.firstName === "" || details.lastName === "" ||
            details.email === "" || details.password === "" || details.repeatPassword === "") {
            console.log("empty fields");
            return;
        }
        if (details.password != details.repeatPassword) { //TODO should we === ?
            console.log("password don't match");
            return;
        }
        var newUser = new Parse.User();
        //TODO check email regexp // password logic
        newUser.set("username", details.userName);
        newUser.set("password", details.password);
        newUser.set("email", details.email);
        newUser.set("FirstName", details.firstName);
        newUser.set("LastName", details.lastName);
        //TODO change it - this is currently made up default values
        newUser.set("Gender", true);
        newUser.set("Age", details.age || -1);
        newUser.signUp(null, {
            success: function (user) {
                //TODO add notification for succesful sign up
                $scope.UserService.logged = true;
                $scope.$apply(function () {
                    $location.url('/welcome');
                });
            },
            error: function (user, error) {
                alert("Error: " + error.code + " " + error.message);
            }
        });
    }
});