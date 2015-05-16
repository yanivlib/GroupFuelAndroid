var app = angular.module('GroupFuel', ['ui.bootstrap','ngRoute','ngTable','ui.select2']);

(function() {
	'use strict';
	console.log("app started");

    Parse.initialize("LkuUmj7OE1C9BzsbhkpMZEgeAT1A0ZACqTUZgN2f", "SjAjaVwR56asiH2VYZuY2j44LerSTflgvNTyCnzl");

    // Configure routes for app
    app.config(function($routeProvider, $locationProvider) {
        $locationProvider.html5Mode(true);
        $routeProvider.when('/welcome', {
            controller:'NavigationController',
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
        }).when('/signup', {
            controller:'SignupController',
            templateUrl: 'web_ui/app/partials/signup.html'
        }).otherwise({ redirectTo: '/welcome'});
    });

})();