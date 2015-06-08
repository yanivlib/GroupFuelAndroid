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
        year: "",
        fuelType: "",
        type: ""
    };

    (function () {
        $scope.carDetails = angular.copy(carDetails);
        $scope.makes = [];
        $scope.parseModels = [];
        $scope.markedDic = {};
        $scope.modelsDic = {};
        $scope.volumesDic = {};
        $scope.yearsDic = {};
        $scope.fuelTypesDic = {};
        $scope.typesDic = {};

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
        disableFollowingSelects('make', 'model');

        Parse.Cloud.run('getCarModels', {'Make': make}, {
            success: function (results) {
                $scope.parseModels = results.resultSet;
                fillDictionary($scope.parseModels, $scope.modelsDic, 'Model');
                $scope.$digest();
            },
            error: function (message) {
                console.log("Error: Failed to load models");
                console.log(Parse.Error + message);
            }
        });
    });
    $scope.$watch('carDetails.model', function (model) {
        if (String(model) == "" || model === undefined)
            return;
        disableFollowingSelects('model', 'volume');

        fillDictionary($scope.modelsDic[model], $scope.volumesDic, 'Volume');
    });

    $scope.$watch('carDetails.volume', function (volume) {
        if (String(volume) == "" || volume === undefined)
            return;
        disableFollowingSelects('volume', 'year');

        fillDictionary($scope.volumesDic[volume], $scope.yearsDic, 'Year');
    });

    $scope.$watch('carDetails.year', function (year) {
        if (String(year) == "" || year === undefined)
            return;
        disableFollowingSelects('year', 'fuelType');

        fillDictionary($scope.yearsDic[year], $scope.fuelTypesDic, 'FuelType');
    });

    $scope.$watch('carDetails.fuelType', function (fuelType) {
        if (String(fuelType) == "" || fuelType === undefined)
            return;
        disableFollowingSelects('fuelType', 'type');

        fillDictionary($scope.fuelTypesDic[fuelType], $scope.typesDic, 'Type');
    });

    $scope.$watch('carDetails.type', function (type) {
        if (String(type) == "" || type === undefined)
            return;
        disableFollowingSelects('type', '');
    });


    function fillDictionary(fromArray, toDic, parseKey) {
        // toDic = {};
        for (var i = 0; i < fromArray.length; i++) {
            var current = fromArray[i].get(parseKey);
            if (toDic[current] === undefined) {
                toDic[current] = [];
            }
            toDic[current].push(fromArray[i]);
        }
        console.dir(toDic);
    }

    $scope.addCar = function () {
        for(var p in x) {
            if(x.hasOwnProperty(p)) {
                if(x[p] === 0) {
                    //Found it!
                }
            }
        }
        var type, array, model;
        type = $scope.carDetails.type;
        array = $scope.typesDic[type];
        model = array[0];
        console.log(type);
        console.dir($scope.typesDic);
        console.dir(array);
        console.dir(model);
        // TODO Validate that all fields are filled
        /* Parse.Cloud.run('addCar', {'carDetails': $scope.carDetails, 'model': }, {
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
         });*/

    };
    $scope.clearCarsForm = function () {
        var marked = $scope.markedDic;
        for (var key in marked) {
            if (marked.hasOwnProperty(key)) {
                marked[key] = false;
            }
        }
        $scope.carDetails = angular.copy(carDetails);
    };
    $scope.close = function () {
        $modalInstance.close();
    };


    function disableFollowingSelects(current, next) {
        $scope.markedDic[current] = true;
        //noinspection FallThroughInSwitchStatementJS
        switch (next) {
            case 'model':
                $scope.markedDic['model'] = false;
                $scope.carDetails['model'] = "";
            case 'volume':
                $scope.markedDic['volume'] = false;
                $scope.carDetails['volume'] = "";
            case 'year':
                $scope.markedDic['year'] = false;
                $scope.carDetails['year'] = "";
            case 'fuelType':
                $scope.markedDic['fuelType'] = false;
                $scope.carDetails['fuelType'] = "";
                break;
            case 'type':
                $scope.markedDic['type'] = false;
                $scope.carDetails['type'] = "";
        }
    }
});