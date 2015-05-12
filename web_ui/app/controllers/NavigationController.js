/**
 * Created by Tomer on 07/04/2015.
 */
app.controller('NavigationController', function ($scope, $modal, $location) {
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

    $scope.$on('UserLoggedIn', function () {
        console.log("triggered");
       $scope.logged = true;
    });

    $scope.doLogOut = function () {
        Parse.User.logOut();
        if (Parse.User.current()) {
            // TODO - logout failed
        }
        $scope.logged = false;
    };

    $scope.doLogin = function () {
        // when modal closed - change url according to answer, change $scope.logged
        var modalInstance = $modal.open({
            templateUrl : 'web_ui/app/partials/login.html',
            controller : 'LoginController'
        });
        modalInstance.result.then(
            function (res) {
                // show notification
                console.log(res);
                $scope.logged = true;
                $location.url('/welcome');
            },
            function (res) {
                console.log(res);
                // show notification
            });
    }
});