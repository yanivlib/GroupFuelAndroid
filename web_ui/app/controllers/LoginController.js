app.controller('LoginController', function ($scope, $modalInstance, $location) {
    'use strict';
    var cleanLoginDetails = {
        username : "",
        password : ""
    };
    (function () {
        $scope.loginDetails = angular.copy(cleanLoginDetails);

    })();

    $scope.doLogin = function () {
        var details = $scope.loginDetails;
        if (details.username == "" || details.password == "") {
            return;
        }
        Parse.User.logIn(details.username, details.password, {
            success: function(user) {
                // Close Modal
                console.log("login worked");
                $scope.$emit('UserLoggedIn');
                $modalInstance.close("user " + details.username + " logged in succesfully");
            },
            error: function(user, error) {
                // clean password field and show error
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

});