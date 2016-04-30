'use strict';

/* https://github.com/angular/protractor/blob/master/docs/toc.md */

describe('Photo App', function() {


  it('should default to summary view', function() {
    browser.get('index.html');
    expect(browser.getLocationAbsUrl()).toMatch("/summary");
    

  });


  describe('matched', function() {

    beforeEach(function() {
      browser.get('index.html#/matched');
    });


    it('should render all matched photos in a list', function() {
      
      var matchedList = element.all(by.repeater('photo in photos'));
      var query = element(by.model('query'));

      expect(matchedList.count()).toBe(3);

    });

  });


  describe('unmatched', function() {

	    beforeEach(function() {
	      browser.get('index.html#/unmatched/unmatched');
	    });


	    it('should render all unmatched photos in a list', function() {
	      
	      var unmatchedList = element.all(by.repeater('photo in photos'));
	      var query = element(by.model('query'));

	      expect(unmatchedList.count()).toBe(1);

	    });

	  });
  
  describe('nocamera', function() {

	    beforeEach(function() {
	      browser.get('index.html#/unmatched/nocamera');
	    });


	    it('should render all photos in a list without a camera', function() {
	      
	      var unmatchedList = element.all(by.repeater('photo in photos'));
	      var query = element(by.model('query'));

	      expect(unmatchedList.count()).toBe(0);

	    });

	  });
  
  describe('nodate', function() {

	    beforeEach(function() {
	      browser.get('index.html#/unmatched/nodate');
	    });


	    it('should render all photos in a list without a date', function() {
	      
	      var unmatchedList = element.all(by.repeater('photo in photos'));
	      var query = element(by.model('query'));

	      expect(unmatchedList.count()).toBe(1);

	    });

	  });


});
