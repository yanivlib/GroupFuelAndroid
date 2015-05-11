/**
 * Created by Tomer on 07/04/2015.
 */
app.controller('NavigationController', function ($scope, $location) {
    'use strict';
    (function() {
        $scope.currentUser = Parse.User.current();
        if ($scope.currentUser) {
            console.log($scope.currentUser + " is logged in");
            $scope.logged = true;
        }
        else {
            console.log("nobody is logged in");
            $scope.logged = false;
        }
    })();
    $scope.isActive = function (path) {
        return ($location.path() === path);
    };
    $scope.$on('UserLoggedIn', function() {
        console.log("triggered");
       $scope.logged = true;
    });
    $scope.doLogOut = function() {
        Parse.User.logOut();
        if (Parse.User.current()) {
            // TODO - logout failed
        }
        $scope.logged = false;
    };
});