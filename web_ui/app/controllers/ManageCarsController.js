/**
 * Created by matansab on 5/18/2015.
 */
app.controller('ManageCarsController', function ($scope, $location, UserService) {
    'use strict';

    (function () {
        $scope.UserService = UserService;
        if ($scope.UserService.logged == false){
            console.log('user need to be logged in to watch his cars');
        }
        else{
            console.log('okay user is logged in');
        }

    })();
});
