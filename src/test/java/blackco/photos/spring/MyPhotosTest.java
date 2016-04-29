package blackco.photos.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

public class MyPhotosTest {

	private ApplicationContext context;
	
	private String home;
	private String photoDir1 = "/src/test/test-fixtures/photos1";
	private String photoDir2 = "/src/test/test-fixtures/photos2";
	private String cacheLocation = "/src/test/test-fixtures";

	private static final Logger logger = Logger.getLogger(MyPhotosTest.class);

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

	@Before
	public void setup() {
		
		home = System.getProperty("home");
		
		context = new AnnotationConfigApplicationContext(MyPhotosTest.class);
	}

	/*
	 * Test Cases 
	 * (1) Add Photo, persist and be able to open from disk again 
	 * (2) Add multiple directories and be able to retrieve 
	 * (3) Add photos in multiple directories and be able to retrieve 
	 * (4) Add photos in a directory not yet added and add in all photos 
	 * (5) Add directory and add in only photos not yet seen 
	 * (6) Add photos and get the size 
	 * (7) add photos and initialize, go back to cache contents, ensure same objects
	 * (8) Add photo, build cached results, update the photo with new metadata and ensure new information is available.
	 * 
	 * Question How do we create a Mock PhotoFinder ... (a) we don't, we use
	 * real photos, checked in alongside code. (b) we refactor MyPhotos to take
	 * out buildPhotos() method.
	 */

	/*
	@Test
	public void testAddDirectory() {

		String dir1 = home+photoDir1;
		
		MyPhotos myPhotos = context.getBean(MyPhotos.class);

		myPhotos.init(home+cacheLocation);
		myPhotos.empty();
		
		logger.debug("Reading photos in directories from disk " + dir1);
		logger.debug("Already initialized from disk = " +  myPhotos.size());
		
		
		myPhotos.addDirectory(dir1);
		assertEquals(myPhotos.size(), 2);

		myPhotos.addDirectory(dir1);
		assertEquals(myPhotos.size(), 2);
	}
	*/
	
	@Test
	public void testPhotoFinder(){
		PhotoFinder finder = context.getBean(PhotoFinder.class);
		
		String dir1 = home+photoDir1;
		String dir2 = home+photoDir2;
		
		assertEquals(finder.getOnDiskList(dir1).size(), 2);
	
		assertEquals(finder.getOnDiskList(dir2).size(), 1);

			
	}
	
	
	@Test
	public void test() {

		String dir1 = home+photoDir1;
		String dir2 = home+photoDir2;


		//Expected Files
		HashSet<String> expectedPhotos = new HashSet<String>();
		expectedPhotos.add(dir1+"/IMAG0071.jpg");
		expectedPhotos.add(dir1+"/IMG_1558.jpg");
		expectedPhotos.add(dir2+"/IMG_0131.JPG");
		
		
		MyPhotos myPhotos = context.getBean(MyPhotos.class);
		
		//Initialize with the cache location, 
		//empty the cache and save to ensure we start with a clean slate
		myPhotos.init(home+cacheLocation);
		myPhotos.empty();
		myPhotos.save(); 
		
		
		// remove file by saving blank, no longer required if move to a model 
		// whereby we load by looking at filesystem and then retrieving a json file
		// for that specific photo 
		// OR 
		// simply replace with a delete all and start again function specific for this test

		logger.info("About to add Dir1 ");
		
		myPhotos.addDirectory(dir1);		
		logger.info("(6) Add photos and get the size ");
		assertEquals(myPhotos.size(), 2);
		
		myPhotos.addDirectory(dir2);
		
		for (String dir : myPhotos.getDirectories()) {
			for (MyPhoto p : myPhotos.getUnprocessedPhotos(dir)) {

				Calendar c = Calendar.getInstance();

				logger.debug(p.getFilename() + ", is expected and in set "
						+ expectedPhotos.contains(p.getFilename()));

				assertTrue(expectedPhotos.contains(p.getFilename()));

			}
		}

		logger.info("(2) Add multiple directories and be able to retrieve"); 
		assertEquals( expectedPhotos.size(), myPhotos.size());

	
		myPhotos.save();
		
		// Empty Cache
		myPhotos.empty();

		logger.info("We can flush the cache in memory, and start again");
		assertEquals(myPhotos.size(), 0);

		// Get back from disk
		myPhotos.init(home+cacheLocation);
		myPhotos.open();

		logger.info("(1) Add Photo, persist and be able to open from disk again ");
		logger.info("Does it contain dir1=" + myPhotos.getDirectories().contains(dir1));
	
		
		// Successfully retrieve files in cache
		assertTrue(myPhotos.getDirectories().contains(dir1));
		assertTrue(myPhotos.getDirectories().contains(dir2));
		assertEquals(myPhotos.size(), expectedPhotos.size());
		
		
		// (7) Now add directories again, we should have the same photo objects
		// 7(a) get the photo objects in the cache
		
		logger.info("Scenario 7: re-adding the directory does not cause instances of MyPhoto to be recreated unnecessarily" );
		HashMap<String, MyPhoto> expectedPhotoInstance = 
					new HashMap<String, MyPhoto>();
		
		for (String dir : myPhotos.getDirectories()) {
			for (MyPhoto p : myPhotos.getUnprocessedPhotos(dir)) {

				expectedPhotoInstance.put(p.getFilename(), p);
				
				logger.info("MyPhoto p= " + p);

			}
		}
		
		myPhotos.addDirectory(dir1);		
		myPhotos.addDirectory(dir2);
		assertEquals(myPhotos.size(), expectedPhotos.size());
		
		logger.info("After add directoriesTesting has same object in cache" );
		
		for (String dir : myPhotos.getDirectories()) {
			for (MyPhoto p : myPhotos.getUnprocessedPhotos(dir)) {

				assertEquals(expectedPhotoInstance.get(p.getFilename()), p);
				
				logger.info("Same Object? : new MyPhoto p= " 
						+ p + ", old MyPhoto=" 
						+ expectedPhotoInstance.get(p.getFilename()));

			}
		}
		
	}


}
