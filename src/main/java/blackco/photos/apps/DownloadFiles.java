package blackco.photos.apps;

import java.util.GregorianCalendar;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import blackco.photos.spring.AppConfig;
import blackco.photos.spring.GetInfoService;
import blackco.photos.spring.MyPhoto;
import blackco.photos.spring.MyPhotos;
import blackco.photos.spring.PageSummary;
import blackco.photos.spring.SearchCriteria;
import blackco.photos.spring.SearchService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class DownloadFiles {

	private static final Logger logger = Logger.getLogger(DownloadFiles.class);
	
	private static String path = null;
	

	private SearchService search;
	
	private MyPhotos myPhotos;
	
	

	public DownloadFiles(final ApplicationContext context){
		
		
		search = context.getBean(SearchService.class);
		context.getBean(GetInfoService.class);
		myPhotos =context.getBean(MyPhotos.class);
	
	}
	
	
	/*
	 *  Test Cases
	 *  (1) Gets photos taken to second accuracy from MockFlickr API
	 *  (2) Does not return photos taken a second earlier
	 *  (3) Does not return photos taken a second later
	 *  (4) Handles timezone?
	 *  (5) Dates in inappropriate string format.
	 *  (6) Empty Array if nothing found, never null!
	 *  
	 *  Requires
	 *  (1) Mock Search  ( returns Flickr IDs )
	 *  (2) Mock GetInfo ( enriches photo with date taken )
	 * 
	 */
	
	public void process(boolean tryAgain){
		
		SearchCriteria s = new SearchCriteria();
		
		
		/*
		 *  DOWNLOAD FROM FLICKR Jan 2019 HACK
		 * 
		 */
		
		
		Date d1 = new GregorianCalendar(2005, Calendar.JANUARY,9).getTime();
		Date d2 = new GregorianCalendar(2005, Calendar.JANUARY,10).getTime();
		
		s.min_taken_date = d1;
		s.max_taken_date = d2;
		

		logger.debug("CALLING FLICKR");
		search.download(search.search(s));

	}
	
			
	private void log( FileWriter f, String message){
		
		try {
			f.write(message );
			f.write(System.getProperty("line.separator"));

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
		
	private final static ObjectMapper mapper;
	   static {
	      mapper = new ObjectMapper();
	}
	
	
	public ArrayList<Setting> getSettings(){
		
		ArrayList<Setting> settings = new ArrayList<Setting>();
		
		for ( String dir :  this.myPhotos.getDirectories()){
			settings.add( new Setting(dir));
		}
		
		return settings;
	}
		
		
	public static void main(String[] args) {

		AnnotationConfigApplicationContext context 
				=new AnnotationConfigApplicationContext(AppConfig.class);
		
		
		AppConfig config = context.getBean(AppConfig.class);
		
		config.init(args);

		int i;
		
		boolean tryAgain = ComplexComparison.IF_UNMATCHED_DO_NOT_SEARCH;
		
		String results = "/Users/blackco/Documents/java/src/photos/src/angular-seed/app";
				

		
		for (i = 0; i < args.length; i++) {
			switch (args[i]) {

			case "-path":
				if (i < args.length)
					path = args[++i];
				break;
				
			case "-results":
				if (i < args.length)
					results = args[++i];
				break;
			
			case "-tryAgain":
				if (i < args.length){
					
					tryAgain = new Boolean(args[++i]).booleanValue();
					
				}
				break;

				
			}
		}

		if (path == null) {
			throw new RuntimeException("Path must be defined");
		}

		DownloadFiles c = new DownloadFiles(context);
	

		MyPhotos myPhotos = context.getBean(MyPhotos.class);
		
		logger.debug("ComplexComparison: comparing contents of these directories= " + myPhotos.getDirectories());
		
		c.myPhotos.addDirectory(path);
		//c.myPhotos.addDirectory("/Users/blackco/Pictures/test3");
		//c.myPhotos.addDirectory("/Users/blackco/Pictures/test4");
		
		c.process(tryAgain);

	
	}
	
	}



