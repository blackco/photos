'use strict';

describe('myApp.summary module', function() {

  beforeEach(module('myApp.summary'));

  describe('summary controller', function(){

    it('should return overall report', inject(function($controller) {
      //spec body
      var scope = {}, summaryCtrl = $controller('summaryCtrl', {$scope:scope});
      expect(scope.summary.matched).toBe(3);
    }));

  });
});
