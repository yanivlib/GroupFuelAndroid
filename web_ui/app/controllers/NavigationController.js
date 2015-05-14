/**
 * Created by Tomer on 07/04/2015.
 */
app.controller('NavigationController', function ($scope, $modal, $location, UserService) {
    'use strict';

    (function() {
        $scope.UserService = UserService;
    })();

    $scope.isActive = function (path) {
        return ($location.path() === path);
    };

    $scope.doLogOut = function () {
        $scope.UserService.doLogout();
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
    };

    $scope.doSignup = function () {
        $location.url('/signup');
    };
});