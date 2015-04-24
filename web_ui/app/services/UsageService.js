/**
 * Created by Tomer on 09/04/2015.
 */
app.service('UsageService', function (){
   'use strict';
    this.getCars = function (option) {
        // option can be user or owner, call api accordingly.
        console.log("getCar " + option);
        if (option === "owned") {
            return [
                {'carName': 'Renault', 'carID': 5}
            ];
        }
        return [
            {'carName': 'MG', 'carID': 1},
            {'carName': 'Toyota', 'carID': 2},
            {'carName': 'Audi', 'carID': 3},
            {'carName': 'Opel', 'carID': 4}
        ];
    };
    this.getDriversByCar = function (carID) {
        console.log("getDriversByCar");
        return [
            {'driverName': 'Matan', 'driverID': 1},
            {'driverName': 'Yaniv', 'driverID': 2},
            {'driverName': 'Tomer', 'driverID': 3}
        ];
    };
    this.getUsageData = function (car,driver) {
        console.log("getUsageData, car is: " + car + ", driver is: " + driver + ".");
        return {};
    }
});