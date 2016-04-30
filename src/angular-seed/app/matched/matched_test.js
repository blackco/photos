'use strict';

describe('myApp.matched module', function() {

  beforeEach(module('myApp.matched'));

  describe('matched controller', function(){

    it('should ....', inject(function($controller) {
      //spec body
      var matchedCtrl = $controller('matchedCtrl');
      expect(matchedCtrl).toBeDefined();
    }));

  });
});