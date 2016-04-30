'use strict';

var settingsControllers = angular.module('myApp.settings', ['ngRoute'])

settingsControllers.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/settings', {
    templateUrl: 'settings/settings.html',
    controller: 'settingsCtrl'
  });
}])

settingsControllers.controller('settingsCtrl',['$scope', '$http',
                function($scope, $http) {

        $http.get('settings/settings.json')
                .success(function(data,status,headers,config,statusText){
                        $scope.HttpStatus = status;
                        $scope.HttpHeaders = headers;
                        $scope.HttpConfig = config;
                        $scope.Directories = data;
                        $scope.HttpStatusText = statusText;
                        $scope.foo = 'SUCCESS';
                }).error(function(data,status,headers,config,statusText){
                        $scope.HttpStatus = status;
                        $scope.HttpHeaders = headers;
                        $scope.HttpConfig = config;
                        $scope.Directories = data;
                        $scope.HttpStatusText = statusText;
                        $scope.foo = 'ERROR';
                });



}]);
