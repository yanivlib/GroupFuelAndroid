app.controller('LoginController', function ($scope, $modalInstance, $location, UserService) {
    'use strict';
    var cleanLoginDetails = {
        username : "",
        password : ""
    };
    (function () {
        $scope.loginDetails = angular.copy(cleanLoginDetails);
        $scope.UserService = UserService;
    })();

    $scope.doLogin = function () {
        var details = $scope.loginDetails;
        if (details.username == "" || details.password == "") {
            return;
        }
        if ($scope.UserService.logged) {
            return;
        }
        Parse.User.logIn(details.username, details.password, {
            success: function(user) {
                console.log("login worked");
                $scope.UserService.logged = true;
                $scope.UserService.currentUser = user;
                $modalInstance.close("user " + details.username + " logged in succesfully");
            },
            error: function(user, error) {
                $scope.loginDetails.password = "";
                console.log("login failed with error: " + error);
            }
        });
    };

    $scope.doCancel = function () {
        // close modal
        console.log("cancel login");
        $modalInstance.dismiss('login canceled');
    };

    // TODO - add forgot my password button
});