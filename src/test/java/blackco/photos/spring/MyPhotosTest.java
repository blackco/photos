package blackco.photos.spring;

import static org.junit.Assert.*;

import java.util.Calendar;
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
	 * Test Cases (1) Add Photo, persist and be able to open from disk again (2)
	 * Add multiple directories and be able to retrieve (3) Add photos in
	 * multiple directories and be able to retrieve (4) Add photos in a
	 * directory not yet added and add in all photos (5) Add directory and add
	 * in only photos not yet seen (6) Add photos and get the size (7) add
	 * photos and initialize, go back to cache contents
	 * 
	 * Question How do we create a Mock PhotoFinder ... (a) we don't, we use
	 * real photos, checked in alongside code. (b) we refactor MyPhotos to take
	 * out buildPhotos() method.
	 */

	@Test
	public void testAddDirectory() {

		String dir1 = home+photoDir1;
		
		MyPhotos myPhotos = context.getBean(MyPhotos.class);

		logger.debug("Reading photos in directories from disk " + dir1);
		myPhotos.addDirectory(dir1);
		assertEquals(myPhotos.size(), 2);

		myPhotos.addDirectory(dir1);
		assertEquals(myPhotos.size(), 2);
	}
	
	
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

		String cacheLocation = "/Users/blackco/Temp/myPhotosCacheMyPhotosTest.json";

		//Expected Files
		HashSet<String> expectedPhotos = new HashSet<String>();
		expectedPhotos.add(dir1+"/IMG_1108.jpg");
		expectedPhotos.add(dir1+"/IMG_2017.jpg");
		expectedPhotos.add(dir2+"/IMG_2017.jpg");

		
		MyPhotos myPhotos = context.getBean(MyPhotos.class);
		
		//Initialize with the cache location, 
		//empty the cache and save to ensure we start with a clean slate
		myPhotos.init(cacheLocation);
		myPhotos.empty();
		myPhotos.save();

		myPhotos.addDirectory(dir1);		
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

		assertEquals( expectedPhotos.size(), myPhotos.size());

		myPhotos.save();

		
		// Empty Cache
		myPhotos.empty();

		assertEquals(myPhotos.size(), 0);

		// Get back from disk
		myPhotos.init(cacheLocation);

		myPhotos.open();

		// Successfully retrieve files in cache
		assertTrue(myPhotos.getDirectories().contains(dir1));
		assertTrue(myPhotos.getDirectories().contains(dir2));
		assertEquals(myPhotos.size(), expectedPhotos.size());
		
		
	}


}
