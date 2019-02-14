package blackco.photos.apps;

import org.apache.log4j.Logger;
import org.jbehave.core.annotations.BeforeStories;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import blackco.photos.spring.Contexts;
import blackco.photos.spring.FlickrPhoto;
import blackco.photos.spring.FlickrPhotoDate;
import blackco.photos.spring.GetExifService;
import blackco.photos.spring.GetInfoService;
import blackco.photos.spring.MyPhoto;
import blackco.photos.spring.MyPhotoMetaDataReader;
import blackco.photos.spring.MyPhotoMetaDataReaderImpl;
import blackco.photos.spring.MyPhotos;
import blackco.photos.spring.MyPhotosImpl;
import blackco.photos.spring.PageSummary;
import blackco.photos.spring.PhotoFinder;
import blackco.photos.spring.PhotoFinderImpl;
import blackco.photos.spring.PhotosService;
import blackco.photos.spring.SearchCriteria;
import blackco.photos.spring.SearchService;
import blackco.photos.spring.Contexts.Context;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

@Configuration
@ComponentScan
public class ComplexComparisonStorySteps {

	private static final Logger logger = Logger
			.getLogger(ComplexComparisonStorySteps.class);

	@Bean
	public PhotosService myPhotosService() {
		return new PhotosService() {

			private HashMap<String, FlickrPhoto> photos = new HashMap<String, FlickrPhoto>();

			public FlickrPhoto getPhoto(String photoId) {

				System.out.println("Test Fixture:" + photos.get(photoId));
				return photos.get(photoId);
			}

			public void setPhoto(FlickrPhoto photo) {

				System.out.println("myPhotosService.setPhoto(): " + photo);
				photos.put(photo.id, photo);
			}

			public Collection<FlickrPhoto> getAll() {
				return photos.values();
			}

			public void deleteCache() {
				photos = new HashMap<String, FlickrPhoto>();
			}

			public String toString() {
				return "ComplexComparisonTest.MockPhotosService: contains"
						+ photos.size();
			}

		};
	}

	@Bean
	public SearchService mySearchService() {
		return new SearchService() {

			private PhotosService photos;

			@Autowired
			public void setPhotos(PhotosService photos) {
				this.photos = photos;
			}

			public PageSummary search(SearchCriteria criteria) {

				PageSummary summary = new PageSummary();

				for (FlickrPhoto p : photos.getAll()) {

					if (p.dateTaken != null) {
						System.out.println("MockSearchService.search() "
								+ p.dateTaken + ","
								+ p.dateTaken.equals(criteria.max_taken_date)
								+ p.dateTaken.equals(criteria.min_taken_date));

						if (p.dateTaken.equals(criteria.max_taken_date)
								|| p.dateTaken.equals(criteria.min_taken_date)) {

							summary.photos.add(p);

							System.out
									.println("MockSearchService.search() : adding to summary "
											+ p);

						}
					}

				}

				return summary;
			}


		};
	}

	@Bean
	public GetInfoService myGetInfoService() {
		return new GetInfoService() {

			private PhotosService photos;

			@Autowired
			public void setPhotos(PhotosService photos) {
				this.photos = photos;
			}

			public FlickrPhoto getInfo(String photoId) {
				System.out
						.println("Mock GetInfoService.getInfo(): PhotoService"
								+ photos);
				System.out.println("Mock GetInfoService.getInfo(): Get "
						+ photoId + ", " + photos.getPhoto(photoId));
				return photos.getPhoto(photoId);
			}
		};
	}

	@Bean
	public GetExifService myGetExifService() {
		return new GetExifService() {

			private PhotosService photos;

			@Autowired
			public void setPhotos(PhotosService photos) {
				this.photos = photos;
			}

			public FlickrPhoto getExif(String photoId) {
				System.out
						.println("Mock GetExifService.getInfo(): PhotoService"
								+ photos);
				System.out.println("Mock GetExifService.getInfo(): Get "
						+ photoId + ", " + photos.getPhoto(photoId));
				return photos.getPhoto(photoId);
			}
		};
	}

	@Bean
	public MyPhotos myMyPhotos() {
		return new MyPhotosImpl();
	}

	@Bean
	public MyPhotoMetaDataReader myMyPhotoMetaDataReader() {
		return new MyPhotoMetaDataReaderImpl();
	}

	@Bean
	public PhotoFinder myPhotoFinder() {
		return new PhotoFinderImpl();
	}

	
	private AnnotationConfigApplicationContext context;// = new AnnotationConfigApplicationContext(ComplexComparisonStorySteps.class);
	

	private String cacheLocation = "/src/test/test-fixtures";
	
	private ComplexComparisonStorySteps steps;
	private String home = "/Users/blackco/Documents/java/src/photos";
	
	
	protected ArrayList<FlickrPhoto> photosTakenAt; 
	protected String mockDirectory = "TEST";
	
	@BeforeStories
	public void beforeStories() {
		context = new AnnotationConfigApplicationContext(ComplexComparisonStorySteps.class);
		
		MyPhotos myPhotos = context.getBean(MyPhotos.class);

		myPhotos.init(home+cacheLocation);
	 
	}
	
	 @Given("the photos stored on Flickr: $table")
	 public void thePhotosOnFlickr(ExamplesTable table){
		 
			PhotosService service = context.getBean(PhotosService.class);
	

			for(Map<String, String> row : table.getRows()){
	        	FlickrPhoto p = new FlickrPhoto(); 
	            p.id = (row.get("id"));
	            p.dateTaken = FlickrPhotoDate
	        			.setDateInFlickrTextFormat(row.get("dateTaken"));
	            p.setCamera(row.get("camera"));
				
	            service.setPhoto(p);
	        }
	        
	}
	
	@When("retrieving photos taken at $maxdate $maxtime")
	public void retrievingPhotosTakenAt(String maxDate, String maxTime){
		
		ComplexComparison complex = new ComplexComparison(context);

		photosTakenAt = complex.getPhotosForTakenOn(FlickrPhotoDate
				.setDateInFlickrTextFormat(maxDate + " " + maxTime));
		
	}
	
	

	@Then("returned photo set has $size photos")
	public void theReturnedPhotoHasAnIdOf(int size){
		assertTrue(photosTakenAt.size() == size);
	       
	}
	
	boolean p1Andp2Compare;
	MyPhoto p1;
	FlickrPhoto p2;
	
	
	@Given("two amended photos from last statement: $table")
	public void theReusedPhotos(ExamplesTable table){
	    
				MyPhotos myPhotos = context.getBean(MyPhotos.class);
				
				PhotosService photosService = context.getBean(PhotosService.class);
				
				
		        for(Map<String, String> row : table.getRows()){
		        	
		        	if ( row.get("StoredOn").equals("Flickr")){
		        		
		        		FlickrPhoto p2 = photosService.getPhoto(row.get("id"));
		        		
		        		p2.dateTaken = FlickrPhotoDate
		        				.setDateInFlickrTextFormat(row.get("dateTaken"));
		        		p2.camera = row.get("camera");
		        		
		        		photosService.setPhoto(p2);
		        		
		        	} 
		        }
		        
	
	}
	
	@Given("two photos: $table")
	public void thePhotos(ExamplesTable table){
		
		    MyPhotos myPhotos = context.getBean(MyPhotos.class);
			myPhotos.empty();
			
			PhotosService photosService = context.getBean(PhotosService.class);
			
			
	        for(Map<String, String> row : table.getRows()){
	        	
	        	if ( row.get("StoredOn").equals("Flickr")){
	        		p2 = new FlickrPhoto(); 
	        		p2.id = row.get("id");
	        		p2.dateTaken = FlickrPhotoDate
	        				.setDateInFlickrTextFormat(row.get("dateTaken"));
	        		p2.camera = row.get("camera");
	        		
	        		photosService.setPhoto(p2);
	        		
	        	} else {
	        		p1 = new MyPhoto(); 
	        		p1.setFilename(row.get("id"));
	        		p1.setDateTaken( FlickrPhotoDate
	        				.setDateInFlickrTextFormat(row.get("dateTaken")));
	        		p1.setCamera(row.get("camera"));
	        		
	        		myPhotos.addPhoto(mockDirectory, p1);
	        		
	        	}
	        }
	        
	        
	      
		
	 }
	 

	
	@When("photos stored on my computer and flickr are compared")
	public void comparePhotos(){
		ComplexComparison complex = new ComplexComparison(context);

		p1Andp2Compare = complex.compare(p1, p2);
		
		
	}
	
	@When("this set of photos is processed")
	public void processedPhotos(){
		ComplexComparison complex = new ComplexComparison(context);

		complex.process();
		
		
	}
	
	@Given("the set of processed photos in the previous story")
	public void doNothing(){
		// use last set of photos!
	}
	
	@Then("there are no unmatched photos")
	public void noUnMatchedPhotos(){
		
	    MyPhotos myPhotos = context.getBean(MyPhotos.class);
		
		
		ArrayList<MyPhoto> unmatched = myPhotos
				.getUnmatchedPhotos(mockDirectory);

		assertTrue(unmatched.isEmpty());
	}
	
	@Then("there are no unprocessed photos")
	public void noUnProcessedPhotos(){
		
		MyPhotos myPhotos = context.getBean(MyPhotos.class);
		
		
		ArrayList<MyPhoto> unprocessed = myPhotos
				.getUnprocessedPhotos(mockDirectory);

		assertTrue(unprocessed.isEmpty());
		
	}
	
	
	@Then("the two photos match")
	public void bothPhotosMatch(){
		assertTrue(photosCompare());
	}
	
	@Then("the two photos are unmatched")
	public void bothPhotosDoNotMatch(){
		assertTrue(!photosCompare());
	}
	
	private boolean photosCompare(){
		MyPhotos myPhotos = context.getBean(MyPhotos.class);

		Collection<MyPhoto> cachedPhotos = myPhotos.getPhotos(mockDirectory);
		
		cachedPhotos = myPhotos.getPhotos(mockDirectory);

		assertTrue(cachedPhotos.contains(p1));

		boolean matches = false;
		for (MyPhoto p : cachedPhotos) {
			if (p.equals(p1) && p2.id.equals(p1.getFlickrPhotoId())) {
				matches = true;
			}
		}

		//assertEquals(p1.getFlickrPhotoId(), p2.id);
		return matches;
	}
	
	@Then("both photos are the same")
	public void photosAreSame(){
		assertTrue(p1Andp2Compare);
	}
	
	@Then("both photos are the different")
	public void photosAreDifferent(){
		assertTrue(!p1Andp2Compare);
	}
	
	@Given("a set of processed photos")
	public void samePhotosAsLastTest(){
		// Change some aspect, process() again, but still matches because not
		// actually processed
		p1.setCamera("IPHONE6");	
		processedPhotos();
	}
	
	@When("this set of photos is processed but we do not try to match any unmatched photos")
	public void reprocessButDontTryAgainToMatch(){
		
			ComplexComparison complex = new ComplexComparison(context);

			complex.process();
	}

	@When("this set of photos is processed and we try again to match any unmatched photos")
	public void reprocessButTryAgainToMatch(){
		ComplexComparison complex = new ComplexComparison(context);

		complex.process(ComplexComparison.IF_UNMATCHED_SEARCH_AGAIN);
	}
	
	@Then("these photos are not reprocessed")
	public void processedPhotosNotChanged(){
		noUnProcessedPhotos();
	}
	
	
	@Then("photo id 5 is identified as having insufficient metadata to process")
	public void photoIdInSufficientMetadata(String id){
		
		MyPhotos myPhotos = context.getBean(MyPhotos.class);	
		
		
		boolean foundByKey = false;
		for (MyPhoto p : myPhotos.getInsufficientMetaDataNoDate(mockDirectory)) {

			if (id.equals(p.getFilename())) {
				foundByKey = true;
			}
		}

		assertTrue(foundByKey);
	}
	
	@Then("photo id $id is not processed")
	public void photoIdNotProcessed(String id){
		
		MyPhotos myPhotos = context.getBean(MyPhotos.class);	
		
	
		boolean foundByKey = false;
		for (MyPhoto p : myPhotos.getUnprocessedPhotos(mockDirectory)) {

			if (id.equals(p.getFilename())) {
				foundByKey = true;
			}
		}

		assertTrue(foundByKey);
	}

}
