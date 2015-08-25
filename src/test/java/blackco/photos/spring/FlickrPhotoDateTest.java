package blackco.photos.spring;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.log4j.Logger;
import org.junit.Test;

public class FlickrPhotoDateTest {
	
	private static final Logger logger = Logger.getLogger(FlickrPhotoDateTest.class);
	
	@Test
	public void testFlickrDate(){
	
		String flickrDate = "2014-11-15 12:54:44";
			
		Calendar c = Calendar.getInstance();
		
		c.set(Calendar.YEAR, 2014);
		c.set(Calendar.MONTH, Calendar.NOVEMBER);
		c.set(Calendar.DAY_OF_MONTH, 15);
		c.set(Calendar.HOUR_OF_DAY, 12);
		c.set(Calendar.MINUTE,54);
		c.set(Calendar.SECOND,44);
		
		
		logger.debug("compareTo:" + c.getTime().compareTo(getDate()));
		
		logger.debug("date=" + c.getTime() + ", flickrDate=" 
					+ FlickrPhotoDate.setDateInFlickrTextFormat(flickrDate));
		
		logger.debug("compareTo=" + c.getTime().compareTo(
				FlickrPhotoDate.setDateInFlickrTextFormat(flickrDate)));
		
		logger.debug("Milliseconds (Calendar ) = " + c.get(Calendar.MILLISECOND));
		
		Calendar b = Calendar.getInstance();
		
		b.setTime(FlickrPhotoDate.setDateInFlickrTextFormat(flickrDate));
		
		logger.debug("Milliseconds ( FlickrDate ) = " + b.get(Calendar.MILLISECOND));
		
		assertTrue( 
				FlickrPhotoDate.compare(c.getTime()
						, FlickrPhotoDate.setDateInFlickrTextFormat(flickrDate)));
		
		assertTrue( 
				FlickrPhotoDate.compareToSecondPrecision(c.getTime()
						, FlickrPhotoDate.setDateInFlickrTextFormat(flickrDate)));
		
		//assertEquals( c.getTime().compareTo(
		//		FlickrPhotoDate.setDateInFlickrTextFormat(flickrDate)),0);
		
	}
	
	private Date getDate(){
	
		Calendar c = Calendar.getInstance();
		
		c.set(Calendar.YEAR, 2014);
		c.set(Calendar.MONTH, Calendar.NOVEMBER);
		c.set(Calendar.DAY_OF_MONTH, 15);
		c.set(Calendar.HOUR_OF_DAY, 12);
		c.set(Calendar.MINUTE, 54);
		c.set(Calendar.SECOND, 44);
		
		return c.getTime();
		
	}
	

}
