'use strict';

var unprocessedControllers = angular.module('myApp.unprocessed', ['ngRoute'])

unprocessedControllers.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/unprocessed', {
    templateUrl: 'unprocessed/unprocessed.html',
    controller: 'unprocessedCtrl'
  });
}])

unprocessedControllers.controller('unprocessedCtrl',['$scope', '$http',
                function($scope, $http) {

        $http.get('unprocessed/unprocessed.json')
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
