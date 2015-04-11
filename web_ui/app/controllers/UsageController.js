/**
 * Created by Tomer on 07/04/2015.
 */
app.controller('UsageController', function ($scope, UsageService) {
    'use strict';
    (function() {
        // initialize the form
        $scope.usageForm = {};
        $scope.usageForm.car = "";
        $scope.usageForm.driver = "";
        // initialize the data according to current user
        $scope.data = {};
        // TODO - when usageservice will be changed to work with an actual server,
        //        getCarsByOwner will return a promise, change the next line (add .then())
        $scope.data.cars = UsageService.getCarsByOwner();
        $scope.data.drivers = {};
        $scope.data.usageData = {};
    })();

    function getUsage() {
        alert("Implement function getUsage()");
        if ($scope.usageForm.car && $scope.usageForm.driver) {
            console.log("import data")
            //TODO - again, after usageservice will be implemented, change to handle promise.
            $scope.data.usageData = UsageService.getUsageData($scope.usageForm.car,$scope.usageForm.driver);
        }
    }
    $scope.$watch('usageForm.car', function (carID) {
        //TODO - again, after usageservice will be implemented, change to handle promise.
        $scope.data.drivers = UsageService.getDriversByCar(carID);
    });
    $scope.$watchCollection('[usageForm.car, usageForm.driver]', getUsage);
});