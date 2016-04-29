package blackco.photos.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import blackco.photos.metadata.MyTag;
import blackco.photos.metadata.MyTags;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;

public class MyPhotoMetaDataReaderImplTest {
	

	private static final Logger logger = Logger
			.getLogger(MyPhotoMetaDataReaderImplTest.class);

	private ApplicationContext context;
	private String home;
	private String photoDir = "/src/test/test-fixtures/photos3";
	
	@Bean
	public MyPhotoMetaDataReader myPhotoMetaDataReader() {
		return new MyPhotoMetaDataReaderImpl();
	}
	
	@Bean
	public PhotoFinder myPhotoFinder() {
		return new PhotoFinderImpl();
	}

	@Before
	public void setup() {
		
		home = System.getProperty("basedir");
		
		context = new AnnotationConfigApplicationContext(MyPhotoMetaDataReaderImplTest.class);
	}
	
	/*
	 * Test 
	 * (1) For Metadata from an HTC camera just returns HTC
	 * (2) For Iphone returns iphone
	 * (3) For Sony returns sony
	 * (4) For Canon returns Canon
	 * (5) corrupted returns null
	 * 
	 * 
	 */
	
	@Test
	public void testGetCamera() {
		
		PhotoFinder photoFinder = context.getBean(PhotoFinder.class);
		MyPhotoMetaDataReader reader = context.getBean(	MyPhotoMetaDataReader.class );
		
		HashMap<String, String> expected = new HashMap<String,String>();
		
		expected.put("IMG_1558.jpg", "Apple iPhone 5");
		expected.put("IMAG0071.jpg", "HTC Wildfire");
		expected.put("IMG_0131.JPG", "Canon Canon DIGITAL IXUS 500");
		expected.put("IMG_2017.jpg", "null null");
	
	
		ArrayList<Path> list=photoFinder.getOnDiskList(home+photoDir);
		
		for (Path s : list) {

			File jpegFile = s.toFile();
			
			
			try{
			
				logger.debug("FileName=" + jpegFile.getName() + ", camera= " 
						+ reader.getCamera(ImageMetadataReader.readMetadata(jpegFile)));
				
				assertEquals( expected.get(jpegFile.getName()),	
						reader.getCamera(ImageMetadataReader.readMetadata(jpegFile)));
				
			} catch ( Exception e){
				
				logger.error(e);
				assertTrue(false);
			}
		}
		
		
	}

	private Date getDate(int yyyy, int mm, int dd, int hh, int mi, int ss){
		
		Calendar cal = Calendar.getInstance();
		
		cal.set(Calendar.YEAR, yyyy);
		cal.set(Calendar.MONTH, mm);
		cal.set(Calendar.DAY_OF_MONTH, dd);
		cal.set(Calendar.HOUR_OF_DAY, hh);
		cal.set(Calendar.MINUTE, mi);
		cal.set(Calendar.SECOND, ss);
		
		return cal.getTime();
	}
	
	@Test
	public void testGetDate() {
		
		PhotoFinder photoFinder = context.getBean(PhotoFinder.class);
		MyPhotoMetaDataReader reader = context.getBean(	MyPhotoMetaDataReader.class );
		
		
		HashMap<String, Date> expected = new HashMap<String,Date>();
		
		expected.put("IMG_1558.jpg", getDate(2014,6,10,20,39,40));
		expected.put("IMAG0071.jpg", getDate(2011,7,29,13,41,35));
		expected.put("IMG_0131.JPG", getDate(2005,0,9,13,33,27));
		expected.put("IMG_2017.jpg", null);
		
	
		ArrayList<Path> list=photoFinder.getOnDiskList(home+photoDir);
		
		for (Path s : list) {

			File jpegFile = s.toFile();
			
			
			try{
			
				logger.debug("FileName=" + jpegFile.getName() + ", date= " 
						+ reader.getDate(ImageMetadataReader.readMetadata(jpegFile)));
				
			
				assertTrue( FlickrPhotoDate.compareToSecondPrecision(expected.get(jpegFile.getName()),	
						reader.getDate(ImageMetadataReader.readMetadata(jpegFile))));
				
			} catch ( Exception e){
				
				logger.error(e);
				assertTrue(false);
			}
		}
		
		
	}
	
	/*
	 * Test
	 * 
	 * Directory photos3/IMG_2017.jpg
	 * 
			Dir			Name				Value
			File		File Modified Date	Sun Mar 27 16:21:11 BST 2016
			ICC Profile	Profile Date/Time	Mon Mar 09 06:49:00 GMT 1998
	 * 
	 */
	@Test
	public void testGetSuggestedDateTags(){
		
		MyPhotoMetaDataReader reader = context.getBean(	MyPhotoMetaDataReader.class );
		
		File jpegFile  = new File(home+photoDir+"/IMG_2017.jpg");
		HashMap<String, String> expectedResults = new HashMap<String, String>();
		
		expectedResults.put("File Modified Date", "Sun Mar 27 16:21:11 BST 2016");
		expectedResults.put("Profile Date/Time", "Mon Mar 09 06:49:00 GMT 1998");
		
		try {
			MyTags myTags = reader.getSuggestedDateTags(ImageMetadataReader.readMetadata(jpegFile));
		
			for ( MyTag tag : myTags.list){
				
				assertEquals(tag.tagValue, expectedResults.get(tag.tagName));
			}
		
		} catch (ImageProcessingException | IOException e) {
			// TODO Auto-generated catch block
			logger.error(e);
			assertTrue(false);
		}
		
		assertTrue(true);
	}
	
	
	@Test
	public void testGetSuggestedCamera(){
		logger.info("TODO");
		assertTrue(true);
	}

}
