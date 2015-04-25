/**
 * Created by Tomer on 07/04/2015.
 */
app.controller('UsageController', function ($scope, UsageService) {
    'use strict';
    (function() {
        $scope.data = {};
        // TODO - when usageservice will be changed to work with an actual server,
        //        getCars will return a promise, change the next line (add .then())
        $scope.data.cars = [];
        $scope.data.drivers = [];
        $scope.data.usageData = [];
        // initialize the form
        $scope.usageForm = {};
        $scope.usageForm.car = "";
        $scope.usageForm.driver = "";
        $scope.usageForm.option = "owned";
        // initialize the data according to current user

    })();

    function getUsage() {
        console.log("Implement function getUsage()");
        if ($scope.usageForm.car && $scope.usageForm.driver && $scope.usageForm.option) {
            console.log("import data")
            //TODO - again, after usageservice will be implemented, change to handle promise.
            $scope.data.usageData = UsageService.getUsageData($scope.usageForm.car,$scope.usageForm.driver);
        }
    }
    $scope.$watch('usageForm.option', function (opt) {
        $scope.usageForm.car = "";
        if (opt === "owned" || opt === "used") {
            $scope.data.cars = UsageService.getCars(opt);
        }
        else {
            $scope.data.cars = [];
            console.error("only 2 options for usage - owned or used");
        }
    });
    $scope.$watch('usageForm.car', function (carID) {
        //TODO - again, after usageservice will be implemented, change to handle promise.
        $scope.usageForm.driver = "";
        $scope.data.drivers = UsageService.getDriversByCar(carID);
    });
    $scope.$watchCollection('[usageForm.car, usageForm.driver]', getUsage);
});