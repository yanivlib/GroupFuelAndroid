/**
 * Created by matansab on 5/21/2015.
 */
app.controller('AddCarController', function ($scope, $modalInstance, $location, UserService) {
    'use strict';
    var cleanCarDetails = {
        car_number : "",
        mileage : ""
    };
    var carDetails = {
        car_number: "",
        mileage: "",
        make: "",
        model: ""
    };
    var selectedMake = "";

    (function () {
        console.log('A');
        $scope.carDetails = angular.copy(carDetails);
        //$scope.UserService = UserService;
        $scope.makes = [];
        $scope.model = [];
        console.log('B');
        Parse.Cloud.run('getCarMakes', {}, {
            success: function (results) {
                console.log('C');
                if (results===undefined){
                    console.log('The query has failed');
                    return;
                }
                for (var i=0;i<results.length;i++){
                    $scope.makes.push(results[i]);
                }
                $scope.$digest();
            },
            error: function () {
                console.log('D');
                // TODO - add notification error
                console.log("Error: failed to Makes.");
                console.log(Parse.Error);
            }
        });
    })();

    //$scope.$watch('makes', updateCars );
    $scope.$watch('carDetails.make', afterMakeSelected );
    $scope.$watch('carDetails', afterMakeSelected );

    function afterMakeSelected() {
        console.log($scope.carDetails.make);
    }

    $scope.addCar =  function () {


    };
    $scope.clearCarsForm = function (){

    };

    $scope.close = function (){

    };

    /*
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
*/
    // TODO - add forgot my password button
});