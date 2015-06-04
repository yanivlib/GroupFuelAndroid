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
