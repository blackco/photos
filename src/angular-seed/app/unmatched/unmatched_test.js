'use strict';

describe('myApp.unmatched module', function() {

  beforeEach(module('myApp.unmatched'));

  describe('unmatched controller', function(){

    it('should ....', inject(function($controller) {
      //spec body
      var unmatchedCtrl = $controller('unmatchedCtrl');
      expect(unmatchedCtrl).toBeDefined();
    }));

  });
});