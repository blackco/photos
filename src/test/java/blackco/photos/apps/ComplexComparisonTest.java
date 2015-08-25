package blackco.photos.apps;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import blackco.photos.spring.Contexts;
import blackco.photos.spring.Contexts.Context;
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

@Configuration
@ComponentScan
public class ComplexComparisonTest {

	private static final Logger logger = Logger
			.getLogger(ComplexComparisonTest.class);

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

	private FlickrPhoto photo1 = new FlickrPhoto();

	private AnnotationConfigApplicationContext context;

	private String cacheLocation = "/src/test/test-fixtures/myPhotosUnitTestsCache.json";
	
	private String home;

	@Before
	public void setup() {

		home = System.getProperty("home");
		
		photo1.camera = "TEST CAMERA";
		photo1.id = "1";
		photo1.dateTaken = FlickrPhotoDate
				.setDateInFlickrTextFormat("2015-06-02 20:01:00");

		context = new AnnotationConfigApplicationContext(
				ComplexComparisonTest.class);
		
		Contexts.addService(Context.PHOTOS, context);

		PhotosService photos = context.getBean(PhotosService.class);

		photos.setPhoto(photo1);

		MyPhotos myPhotos = context.getBean(MyPhotos.class);

		// TODO how do you pass config like this into the junit test
		// This will retrieve the previous state of the cache

		photos.setPhoto(new FlickrPhoto(FlickrPhoto.UNPROCESSED));
		photos.setPhoto(new FlickrPhoto(FlickrPhoto.UNMATCHED));
		photos.setPhoto(new FlickrPhoto(
				FlickrPhoto.INSUFFICIENT_METADATA_NO_DATE));
		photos.setPhoto(new FlickrPhoto(
				FlickrPhoto.INSUFFICIENT_METADATA_NO_CAMERA));

		myPhotos.init(home+cacheLocation);

	}

	
	@Test
	public void testMyPhotos() {
		MyPhotos myPhotos = context.getBean(MyPhotos.class);

		assertTrue(myPhotos != null);
	}
	
	@Test
	public void testContexts(){
		
		assertEquals(context, Contexts.getContext(Context.PHOTOS));
	}

	@Test
	public void testPhotoService() {

		PhotosService photos = context.getBean(PhotosService.class);

		assertEquals(photos.getPhoto(photo1.id), photo1);

	}

	@Test
	public void testGetInfoService() {

		GetInfoService getInfo = context.getBean(GetInfoService.class);

		assertEquals(getInfo.getInfo(photo1.id), photo1);
	}

	@Test
	public void testGetExifService() {

		GetExifService getExif = context.getBean(GetExifService.class);

		assertTrue(getExif != null);

	}

	@Test
	public void testSearch() {

		SearchService search = context.getBean(SearchService.class);

		SearchCriteria criteria = new SearchCriteria();

		criteria.max_taken_date = photo1.dateTaken;
		criteria.min_taken_date = photo1.dateTaken;

		PageSummary summary = search.search(criteria);

		System.out.println("testSearch(): " + summary);

		assertEquals(summary.photos.get(summary.photos.indexOf(photo1)), photo1);
	}

	/*
	 * Test Cases (1) Gets photos taken to second accuracy from MockFlickr API
	 * (2) Does not return photos taken a second earlier (3) Does not return
	 * photos taken a second later (4) Handles timezone? (5) Dates in
	 * inappropriate string format. (6) Empty Array if nothing found, never
	 * null!
	 * 
	 * Requires (1) Mock Search ( returns Flickr IDs ) (2) Mock GetInfo (
	 * enriches photo with date taken )
	 */

	@Test
	public void testGetPhotosForTaken() {

		PhotosService service = context.getBean(PhotosService.class);

		FlickrPhoto p4, p5, p6;

		p4 = new FlickrPhoto();
		p4.camera = "TEST CAMERA";
		p4.id = "4";
		p4.dateTaken = FlickrPhotoDate
				.setDateInFlickrTextFormat("2015-06-02 19:04:00");

		p5 = new FlickrPhoto();
		p5.camera = "TEST CAMERA";
		p5.id = "5";
		p5.dateTaken = FlickrPhotoDate
				.setDateInFlickrTextFormat("2015-06-02 19:05:00");

		p6 = new FlickrPhoto();
		p6.camera = "TEST CAMERA";
		p6.id = "6";
		p6.dateTaken = FlickrPhotoDate
				.setDateInFlickrTextFormat("2015-06-02 19:06:00");

		service.setPhoto(p4);
		service.setPhoto(p5);
		service.setPhoto(p6);

		ComplexComparison complex = new ComplexComparison(context);

		ArrayList<FlickrPhoto> list = complex.getPhotosForTakenOn(p5.dateTaken);

		// photo1 taken before photo2
		assertTrue(!list.contains(p4));

		// photo2 is returned
		assertTrue(list.contains(p5));

		// photo3 taken after photo3
		assertTrue(!list.contains(p6));

		// (6) Empty Array if nothing found, never null!

		list = complex.getPhotosForTakenOn(FlickrPhotoDate
				.setDateInFlickrTextFormat("2015-06-01 07:00"));

		assertTrue(list.isEmpty());

	}

	/*
	 * Test (1) ComparedPhoto and Photo taken on same date (to second) , on the
	 * same camera (2) ComparedPhoto and Photo taken on same date (to
	 * second),but on different cameras (3) ComparedPhoto and Photo taken on
	 * different date (to second), same cameras
	 * 
	 * Needs (1) Photo and ComparedPhoto, no mock services
	 */

	private static String IPHONE = "IPHONE";

	private static String IPHONE6 = "IPHONE6";

	@Test
	public void testCompare() {

		ComplexComparison complex = new ComplexComparison(context);

		// (1) ComparedPhoto and Photo taken on same date (to second) , on the
		// same camera

		FlickrPhoto p1 = new FlickrPhoto();
		MyPhoto p2 = new MyPhoto();

		p1.camera = IPHONE;
		p1.dateTaken = FlickrPhotoDate
				.setDateInFlickrTextFormat("2015-06-01 15:00:05");

		p2.setFilename("testCompareP2");
		p2.setCamera(IPHONE);
		p2.setDateTaken(FlickrPhotoDate
				.setDateInFlickrTextFormat("2015-06-01 15:00:05"));

		assertTrue(complex.compare(p2, p1));

		// (2) ComparedPhoto and Photo taken on same date (to second), but on
		// different cameras
		p1.camera = IPHONE6;
		assertTrue(!complex.compare(p2, p1));

		// (3) ComparedPhoto and Photo taken on different date (to second), same
		// cameras
		p1.camera = IPHONE;
		p1.dateTaken = FlickrPhotoDate
				.setDateInFlickrTextFormat("2015-06-01 15:00:06");
		assertTrue(!complex.compare(p2, p1));

	}

	/*
	 * Test Cases (1) We process unprocessed photos, we don't re-process
	 * processed photos (2) We dont process unmatched photos again if tryAgain
	 * is not defined
	 * 
	 * Needs Mock Services: MyPhotos GetExif
	 */

	@Test
	public void testProcessOnlyOnce() {

		ComplexComparison complex = new ComplexComparison(context);

		/*
		 * (1) We process unprocessed photos, we don't re-process processed
		 * photos
		 * 
		 * - one photo, one flickr photo - process - change some detail about so
		 * it would not match next go (not possible in real life) - process -
		 * assertEquals( p.getFlickrPhoto() == FlickrPhoto )
		 */

		String id = "testProcessOnlyOnceP1";
		PhotosService photosService = context.getBean(PhotosService.class);

		FlickrPhoto f1 = new FlickrPhoto();
		f1.id = id;
		f1.camera = ComplexComparisonTest.IPHONE6;
		f1.dateTaken = FlickrPhotoDate
				.setDateInFlickrTextFormat("2015-06-01 15:00:05");
		photosService.setPhoto(f1);

		MyPhotos myPhotos = context.getBean(MyPhotos.class);

		myPhotos.empty();

		MyPhoto p1 = new MyPhoto();
		p1.setFilename(id);
		p1.setCamera(ComplexComparisonTest.IPHONE6);
		p1.setDateTaken(FlickrPhotoDate
				.setDateInFlickrTextFormat("2015-06-01 15:00:05"));
		p1.setFlickrPhotoId(FlickrPhoto.UNPROCESSED);

		String mockDirectory = "TEST";
		myPhotos.addPhoto(mockDirectory, p1);

		complex.process();

		ArrayList<MyPhoto> unmatched = myPhotos
				.getUnmatchedPhotos(mockDirectory);

		assertTrue(unmatched.isEmpty());

		ArrayList<MyPhoto> unprocessed = myPhotos
				.getUnprocessedPhotos(mockDirectory);

		assertTrue(unprocessed.isEmpty());

		Collection<MyPhoto> cachedPhotos = myPhotos.getPhotos(mockDirectory);

		assertTrue(cachedPhotos.contains(p1));

		boolean matches = false;
		for (MyPhoto p : cachedPhotos) {
			
			if (p.equals(p1) && f1.id.equals(p1.getFlickrPhotoId())) {
				matches = true;
			}
		}

		assertTrue(matches);

		// Change some aspect, process() again, but still matches because not
		// actually processed
		p1.setCamera(IPHONE);

		complex.process();

		unmatched = myPhotos.getUnmatchedPhotos(mockDirectory);

		assertTrue(unmatched.isEmpty());

		unprocessed = myPhotos.getUnprocessedPhotos(mockDirectory);

		assertTrue(unprocessed.isEmpty());

		cachedPhotos = myPhotos.getPhotos(mockDirectory);

		assertTrue(cachedPhotos.contains(p1));

		matches = false;
		for (MyPhoto p : cachedPhotos) {
			if (p.equals(p1) && f1.id.equals(p1.getFlickrPhotoId())) {
				matches = true;
			}
		}

		assertTrue(matches);

	}

	/*
	 * Test Cases (2) We process unmatched photos again if tryAgain is defined
	 * (3) We dont process unmatched photos again if tryAgain is not defined (4)
	 * We don't match photos that have insufficient metadata for matching (4a)
	 * No TimeTaken (4b) No Camera
	 * 
	 * Needs Mock Services: MyPhotos GetExif
	 */

	@Test
	public void testProcessTryAgain() {

		String timeTaken = "2015-06-01 15:00:10";
		ComplexComparison complex = new ComplexComparison(context);

		/*
		 * 2 photos.
		 */
		PhotosService photosService = context.getBean(PhotosService.class);

		FlickrPhoto f1 = new FlickrPhoto();
		f1.camera = ComplexComparisonTest.IPHONE6;
		f1.dateTaken = FlickrPhotoDate.setDateInFlickrTextFormat(timeTaken);
		photosService.setPhoto(f1);

		MyPhotos myPhotos = context.getBean(MyPhotos.class);

		MyPhoto p1 = new MyPhoto();
		p1.setFilename("testProcessTryAgainP1");
		p1.setCamera(ComplexComparisonTest.IPHONE);
		p1.setDateTaken(FlickrPhotoDate.setDateInFlickrTextFormat(timeTaken));
		p1.setFlickrPhotoId(FlickrPhoto.UNPROCESSED);

		String mockDirectory = "TEST";
		myPhotos.addPhoto(mockDirectory, p1);

		complex.process();

		// Unmatched but is processed this iteration
		ArrayList<MyPhoto> unmatched = myPhotos
				.getUnmatchedPhotos(mockDirectory);

		assertTrue(!unmatched.isEmpty());

		ArrayList<MyPhoto> unprocessed = myPhotos
				.getUnprocessedPhotos(mockDirectory);

		assertTrue(unprocessed.isEmpty());

		p1.setCamera(ComplexComparisonTest.IPHONE6);
		complex.process(ComplexComparison.IF_UNMATCHED_SEARCH_AGAIN);

		// Unmatched but is processed this iteration
		unmatched = myPhotos.getUnmatchedPhotos(mockDirectory);

		assertTrue(unmatched.isEmpty());

		unprocessed = myPhotos.getUnprocessedPhotos(mockDirectory);

		assertTrue(unprocessed.isEmpty());

	}

	/*
	 * Test Cases (4) We don't match photos that have insufficient metadata for
	 * matching (4a) No TimeTaken (4b) No Camera
	 * 
	 * Needs Mock Services: MyPhotos GetExif
	 */

	@Test
	public void testProcessInsufficientMetaDataMissingDate() {

		String timeTaken = "2015-06-01 15:00:10";
		ComplexComparison complex = new ComplexComparison(context);

		PhotosService photosService = context.getBean(PhotosService.class);
		MyPhotos myPhotos = context.getBean(MyPhotos.class);

		MyPhoto p1 = new MyPhoto();
		p1.setFilename("testInsufficientMetaDataMissingDateP1");
		p1.setCamera(ComplexComparisonTest.IPHONE);
		p1.setFlickrPhotoId(FlickrPhoto.UNPROCESSED);

		String mockDirectory = "TEST3";
		myPhotos.addPhoto(mockDirectory, p1);

		complex.process();

		ArrayList<MyPhoto> unprocessed = myPhotos
				.getInsufficientMetaDataNoDate(mockDirectory);

		assertTrue(unprocessed.contains(p1));

		assertTrue(!myPhotos.getMatchedPhotos(mockDirectory).contains(p1));

	}

	@Test
	public void testProcessInsufficientMetaDataMissingCamera() {

		String timeTaken = "2015-06-01 15:00:10";
		String p1Key = "testProcessInsufficientMeteDataMissingCameraP1";
		ComplexComparison complex = new ComplexComparison(context);

		PhotosService photosService = context.getBean(PhotosService.class);

		MyPhotos myPhotos = context.getBean(MyPhotos.class);

		myPhotos.empty();

		MyPhoto p1 = new MyPhoto();
		// p1.setCamera(ComplexComparisonTest.IPHONE);
		p1.setFilename(p1Key);
		p1.setDateTaken(FlickrPhotoDate.setDateInFlickrTextFormat(timeTaken));
		p1.setFlickrPhotoId(FlickrPhoto.UNPROCESSED);

		String mockDirectory = "TEST3";
		myPhotos.addPhoto(mockDirectory, p1);

		complex.process();

		ArrayList<MyPhoto> insufficient = myPhotos
				.getInsufficientMetaDataNoCamera(mockDirectory);

		assertTrue(insufficient.contains(p1));

		assertTrue(!myPhotos.getMatchedPhotos(mockDirectory).contains(p1));

		// Starting the world again
		myPhotos.empty();
		myPhotos.init(home+cacheLocation);
		p1 = null;
		
		
		logger.debug("testProcessInsufficientMetaDataMissingCamera: "
				+ "myPhotos.getInsufficientMetaDataNoCamera(dir)"
				+ myPhotos.getInsufficientMetaDataNoCamera(mockDirectory));

		logger.debug("testProcessInsufficientMetaDataMissingCamera: "
				+ "myPhotos.getInsufficientMetaDataNoCamera(dir).contains(p1)"
				+ myPhotos.getInsufficientMetaDataNoCamera(mockDirectory)
						.contains(p1));

		boolean foundByKey = false;

		for (MyPhoto p : myPhotos.getPhotos(mockDirectory)) {

			if (p1Key.equals(p.getFilename())) {
				foundByKey = true;
				p1 = p; 
			}
		}

		logger.debug("foundByKey = " + foundByKey);
		assertTrue(foundByKey);

		assertTrue(myPhotos.getInsufficientMetaDataNoCamera(mockDirectory)
				.contains(p1));

		assertTrue(!myPhotos.getMatchedPhotos(mockDirectory).contains(p1));

	}
	

}
