/**
 * Created by matansab on 5/21/2015.
 */
app.controller('AddCarController', function ($scope, $modalInstance) {
    // TODO remove console logs
    'use strict';
    var carDetails = {
        car_number: "",
        mileage: "",
        make: "",
        model: "",
        volume: "",
        fuelType: ""
    };


    (function () {
        $scope.carDetails = angular.copy(carDetails);
        $scope.makes = [];
        $scope.parseModels = [];
        $scope.models = [];
        $scope.volumes = [];
        $scope.years = [];
        $scope.fuelTypes = [];
        $scope.markedDic = {};

        Parse.Cloud.run('getCarMakes', {}, {
            success: function (results) {
                if (results === undefined) {
                    console.log('The query has failed');
                    return;
                }
                for (var i = 0; i < results.length; i++) {
                    $scope.makes.push(results[i]);
                }
                $scope.$digest();
            },
            error: function () {
                console.log("Error: failed to Makes.");
                console.log(Parse.Error);
            }
        });
    })();

    $scope.$watch('carDetails.make', function (make) {

        if (String(make) == "") {
            return;
        }
        console.log('selected make is: ' + make);

        disableFollowingSelects('make', 'model');

        Parse.Cloud.run('getCarModels', {'Make': make}, {
            success: function (results) {
                $scope.parseModels = results.resultSet;
                $scope.models = results.distinctModels;
                console.log(results.distinctModels);
                $scope.$digest();
            },
            error: function () {
                console.log("Error: Failed to load models");
                console.log(Parse.Error);
            }
        });
    });

    $scope.$watch('carDetails.model', function (model) {
        if (String(model) == "" || model === undefined)
            return;

        disableFollowingSelects('model', 'volume');

        $scope.volumes = $scope.parseModels.filter(function (value) {
            return value.get('Model') == model;
        }).map(function (value) {
            return value.get('Volume');
        }).filter(removeDuplicates);

        console.log('Volumes are:');
        console.log($scope.volumes);
    });

    $scope.$watch('carDetails.volume', function (volume) {
        if (String(volume) == "" || volume === undefined)
            return;

        disableFollowingSelects('volume', 'year');

        $scope.years = $scope.parseModels.filter(function (value) {
            var car = $scope.carDetails;
            return value.get('Model') == car.model && value.get('Volume') == volume;
        }).map(function (value) {
            return value.get('Year');
        }).filter(removeDuplicates);
    });

    $scope.$watch('carDetails.year', function (year) {
        if (String(year) == "" || year === undefined)
            return;

        disableFollowingSelects('year', 'fuelType');

        $scope.fuelTypes = $scope.parseModels.filter(function (value) {
            var car = $scope.carDetails;
            return value.get('Model') == car.model && value.get('Volume') == car.volume && value.get('Year') == year;
        }).map(function (value) {
            return value.get('FuelType');
        }).filter(removeDuplicates);
    });


    $scope.addCar = function () {
        // TODO Validate that all fields are filled
        Parse.Cloud.run('addCar', {'carDetails': $scope.carDetails}, {
            success: function (results) {
                // TODO close the modal
                console.log('Im here');
                console.dir(results);
                $scope.$digest();
            },
            error: function () {
                console.log("Error: Failed to add a car");
                console.log(Parse.Error);
            }
        });

    };
    $scope.clearCarsForm = function () {
        $scope.markedDic['make'] = false;
        $scope.markedDic['model'] = false;
        $scope.markedDic['volume'] = false;
        $scope.markedDic['year'] = false;

        $scope.carDetails = angular.copy(carDetails);
    };

    $scope.close = function () {
        $modalInstance.close();
    };

    function removeDuplicates(value, index, array) {
        return array.indexOf(value) == index;
    }

    function disableFollowingSelects(current, next) {
        $scope.markedDic[current] = true;
        $scope.markedDic[next] = false;
        $scope.carDetails[next] = "";

    }
});