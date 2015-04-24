/**
 * Created by Tomer on 07/04/2015.
 */
app.controller('NavigationController', function ($scope, $location) {
    'use strict';
    (function() {
        $scope.logged = false;
    })();
    $scope.isActive = function (path) {
        return ($location.path() === path);
    };
});