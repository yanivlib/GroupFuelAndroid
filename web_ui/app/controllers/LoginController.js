/**
 * Created by matansab on 5/11/2015.
 */
app.controller('LoginController', function ($scope, $location) {
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
        $scope.details = angular.copy(CleanUserDetails);
        var currentUser = Parse.User.current();
        if (currentUser) {
            console.log("you are logged in asshole");
            $location.url('/welcome');
        }
    })();
    $scope.doSignUp = function () {
        var details = $scope.details;
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
                console.log('SIGN UP HAS BEEN DONE MATE');
                $scope.$apply(function () {
                    $scope.$emit('UserLoggedIn',1);
                    $location.url('/welcome');
                });
            },
            error: function (user, error) {
                console.log('SIGN UP HAS FAILED FUCKER');
                alert("Error: " + error.code + " " + error.message);
            }
        });
    }
});