/**
 * Created by matansab on 5/18/2015.
 */
app.controller('ManageCarsController', function ($scope, $filter, $modal, ngTableParams, UserService) {
    'use strict';
    (function () {
        $scope.UserService = UserService;
        $scope.userCars = [];

        // Initializing configuration for ngTable
        $scope.tableParams = new ngTableParams({
            page: 1,
            count: 10,
            sorting: {
                make: 'asc'
            }
        }, {
            total: 0,
            getData: function ($defer, params) {
                // Binding table's data. $scope.userCars == [] at initialization,
                // so we actually not showing anything right now.
                var data = $scope.userCars;
                var orderedData = params.sorting() ? $filter('orderBy')(data, params.orderBy()) : data;
                $defer.resolve(orderedData.slice((params.page() - 1) * params.count(), params.page() * params.count()));
            }
        });
        // Import user data and update table
        updateCars();
    })();

    /*
     * Gets user's car list from server, refine it to a simpler json using refineCars()
     * and reloads table.
     */
    function updateCars() {
        Parse.Cloud.run('getOwnedCars', {}, {
            success: function (results) {
                refineCars(results);
                // Updating the ngTable data
                $scope.tableParams.reload();
                $scope.tableParams.total($scope.userCars.length);
            },
            error: function () {
                // TODO - add notification error
                console.log("Error: failed to load user cars.")
            }
        });
    }

    $scope.$watch('UserService.logged', updateCars);

    /*
     * Private function - gets a list of parse car objects and
     * refines it to a bindable json.
     */
    function refineCars(parseCars) {
        $scope.userCars = [];
        for (var i = 0; i < parseCars.length; i++) {
            var currentCar = parseCars[i];
            var currentModel = currentCar.get('Model');
            var newCar = {};
            newCar.ID = currentCar.get('objectId');
            newCar.carNumber = currentCar.get('CarNumber');
            newCar.mileage = currentCar.get('Mileage');
            newCar.make = currentModel.get('Make');
            newCar.model = currentModel.get('Model');
            $scope.userCars[i] = angular.copy(newCar);
        }
        $scope.numCars = $scope.userCars.length;
    }

    $scope.removeCar = function (car) {
        Parse.Cloud.run('removeCar', {'carNumber': car.carNumber}, {
            success: function (results) {
                updateCars();
            },
            error: function () {
                // TODO add notification error
                console.log("Error: query failed in removeCar");
            }
        });
    };
    $scope.addCar = function () {
        console.log('in add a car');
        var modalInstance = $modal.open({
            templateUrl: 'web_ui/app/partials/addCar.html',
            controller: 'AddCarController',
            backdrop: 'static'
        });
        modalInstance.result.then(
            function (res) {
                // show notification
                console.log(res);
            },
            function (res) {
                console.log(res);
                // show notification
            });
    };
});
