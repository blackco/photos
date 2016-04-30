'use strict';

var view1Controllers = angular.module('myApp.summary', ['ngRoute'])

view1Controllers.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/summary', {
    templateUrl: 'summary/summary.html',
    controller: 'summaryCtrl'
  });
}])


view1Controllers.controller('summaryCtrl',['$scope', '$http',
                                          function($scope, $http) {

        $http.get('summary/summary.json')
                .success(function(data,status,headers,config,statusText){
                        $scope.HttpStatus = status;
                        $scope.HttpHeaders = headers;
                        $scope.HttpConfig = config;
                        $scope.summary = data;
                        $scope.HttpStatusText = statusText;
                        $scope.foo = 'SUCCESS';
                }).error(function(data,status,headers,config,statusText){
                        $scope.HttpStatus = status;
                        $scope.HttpHeaders = headers;
                        $scope.HttpConfig = config;
                        $scope.summary = data;
                        $scope.HttpStatusText = statusText;
                        $scope.foo = 'ERROR';
                });


}]);

