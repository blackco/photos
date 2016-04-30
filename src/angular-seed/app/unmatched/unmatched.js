'use strict';

var nodateControllers = angular.module('myApp.unmatched', ['ngRoute'])

nodateControllers.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/unmatched/:typeId', {
    templateUrl: 'unmatched/unmatched.html',
    controller: 'unmatchedCtrl'
  });
}])

nodateControllers.controller('unmatchedCtrl',['$scope' , '$routeParams', '$http',
                function($scope, $routeParams, $http) {

        $http.get('unmatched/' + $routeParams.typeId + '.json')
                .success(function(data,status,headers,config,statusText){
                        $scope.HttpStatus = status;
                        $scope.HttpHeaders = headers;
                        $scope.HttpConfig = config;
                        $scope.photos = data;
                        $scope.reportTypeId = $routeParams.typeId;
                        $scope.HttpStatusText = statusText;
                        $scope.foo = 'SUCCESS';
                }).error(function(data,status,headers,config,statusText){
                        $scope.HttpStatus = status;
                        $scope.HttpHeaders = headers;
                        $scope.HttpConfig = config;
                        $scope.photos = data;
                        $scope.HttpStatusText = statusText;
                        $scope.foo = 'ERROR';
                });



}]);
