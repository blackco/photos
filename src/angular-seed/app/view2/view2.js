'use strict';

angular.module('myApp.view2', ['ngRoute'])

.config(['$routeProvider', function($routeProvider) {
  $routeProvider.when('/view2/:photoId', {
    templateUrl: 'view2/view2.html',
    controller: 'View2Ctrl'
  });
}])

.controller('View2Ctrl',['$scope', '$routeParams','$http', '$location',
                                      function($scope, $routeParams,$http,$location){

	 $scope.theRouteParams = $routeParams;
	 $scope.photo = $routeParams.photo;
	 $scope.photoId = $routeParams.photoId;
	 $scope.path = $routeParams.path;
	 $scope.location = $location.search("test");
	 

	 $http.get('metadata/' + $routeParams.photoId + '.json')
     .success(function(data,status,headers,config,statusText){
             $scope.HttpStatus = status;
             $scope.HttpHeaders = headers;
             $scope.HttpConfig = config;
             $scope.Directories = data;
             $scope.HttpStatusText = statusText;
             $scope.foo = $location.search('test');
             $scope.bar = $routeParams;
     }).error(function(data,status,headers,config,statusText){
             $scope.HttpStatus = status;
             $scope.HttpHeaders = headers;
             $scope.HttpConfig = config;
             $scope.Directories = data;
             $scope.HttpStatusText = statusText;
             $scope.foo = $routeParams.photoId;;
     });


}]);