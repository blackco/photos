package blackco.photos.spring;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public class FlickrPhotoDate {
	
	private static final Logger logger = Logger.getLogger(FlickrPhotoDate.class);

	public static String FlickrDateFormat = "yyyy-MM-dd HH:mm:ss";

	public static String getDateInFlickrTextFormat(Date date) {
		DateFormat df = new SimpleDateFormat(FlickrDateFormat);

		if ( date != null){
			return df.format(date);
		} else {
			return "NODATE";
		}
	}
	
	public static Date setDateInFlickrTextFormat(String date) {
		
		SimpleDateFormat formatter = new SimpleDateFormat(FlickrDateFormat);
        Date d = null;
		try {
			d = (Date) formatter.parse(date);
			
			logger.debug(formatter.format(d));
		} catch (ParseException e) {
			logger.error(e);
		}
				
		return d;
	}
	
	public static boolean compare(Date d1, Date d2){
	
		SimpleDateFormat formatter = new SimpleDateFormat(FlickrDateFormat);
		int result = formatter.format(d1).compareTo(formatter.format(d2));
		logger.debug("d1=" +formatter.format(d1) );
		logger.debug("d2=" +formatter.format(d2) );
		logger.debug("result=" + result);
	     
		return ( 0 == result );
	}
	
	public static boolean compareToSecondPrecision(Date d1, Date d2){
		
		if ( d1==null && d2 == null){
			return true;
		} else{
		Calendar c1 = Calendar.getInstance();
		Calendar c2 = Calendar.getInstance();
		
		c1.setTime(d1);
		c1.set(Calendar.MILLISECOND, 0);
		
		c2.setTime(d2);
		c2.set(Calendar.MILLISECOND, 0);
		
		return ( 0 == c1.getTime().compareTo(c2.getTime()));
		}
		
	}

}
