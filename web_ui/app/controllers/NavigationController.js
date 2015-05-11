/**
 * Created by Tomer on 07/04/2015.
 */
app.controller('NavigationController', function ($scope, $location) {
    'use strict';
    (function() {
        var currentUser = Parse.User.current();
        if (currentUser) {
            console.log("you are logged in");
            $scope.logged = true;
        }
        else {
            console.log("nobody logged");
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