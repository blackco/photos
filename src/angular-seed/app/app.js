'use strict';

// Declare app level module which depends on views, and components
var photoApp = angular.module('myApp', [
  'ngRoute',
  'myApp.summary',
  'myApp.view2',
  'myApp.matched',
  'myApp.unmatched',
  'myApp.metadata',
  'myApp.settings',
  'myApp.unprocessed',
  'myApp.version'
 
]);

photoApp.config(['$routeProvider',
  function($routeProvider) {
    $routeProvider.
      when('/photos', {
        templateUrl: 'summary.html',
        controller: 'summaryCtrl'
      }).
      otherwise({
        redirectTo: '/summary'
      });
  }]);


