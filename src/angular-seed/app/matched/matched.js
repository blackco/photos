'use strict';

var matchedControllers = angular.module('myApp.matched', ['ngRoute'])

matchedControllers.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/matched', {
    templateUrl: 'matched/matched.html',
    controller: 'matchedCtrl'
  });
}])

matchedControllers.controller('matchedCtrl',['$scope', '$http',
                function($scope, $http) {

        $http.get('matched/matched.json')
                .success(function(data,status,headers,config,statusText){
                        $scope.HttpStatus = status;
                        $scope.HttpHeaders = headers;
                        $scope.HttpConfig = config;
                        $scope.photos = data.splice(0,5);
                        $scope.HttpStatusText = statusText;
                }).error(function(data,status,headers,config,statusText){
                        $scope.HttpStatus = status;
                        $scope.HttpHeaders = headers;
                        $scope.HttpConfig = config;
                        $scope.photos = data;
                        $scope.HttpStatusText = statusText;
                });



}]);
