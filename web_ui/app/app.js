var app = angular.module('GroupFuel', ['ui.bootstrap','ngRoute','ngTable','ui.select2']);

(function() {
	'use strict';
	console.log("app started");


    // Configure routes for app
    app.config(function($routeProvider, $locationProvider) {
        $locationProvider.html5Mode(true);
        $routeProvider.when('/welcome', {
            controller:'UsageController',
            templateUrl: 'web_ui/app/partials/welcome.html'
        }).when('/usage', {
            controller:'UsageController',
            templateUrl: 'web_ui/app/partials/usage.html'
        }).when('/account-manager', {
            controller:'AccountManagerController',
            templateUrl: 'web_ui/app/partials/account-manager.html'
        }).when('/statistics', {
            controller:'StatisticsController',
            templateUrl: 'web_ui/app/partials/statistics.html'
        }).when('/mail-report', {
            controller:'MailReportController',
            templateUrl: 'web_ui/app/partials/mailreport.html'
        }).when('/about', {
            controller:'UsageController',
            templateUrl: 'web_ui/app/partials/about.html'
        }).otherwise({ redirectTo: '/welcome'});
    });

})();