package blackco.photos.apps;


import java.util.GregorianCalendar;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import blackco.photos.spring.*;
import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

public class DownloadFiles {

	private static final Logger logger = Logger.getLogger(DownloadFiles.class);
	
	private static String path = null;
	

	private SearchService search;
	
	private MyPhotos myPhotos;

	private GetExifService getExif;

	private GetInfoService getInfo;

	private DownloadService downloadService;
	
	

	public DownloadFiles(final ApplicationContext context){
		
		
		search = context.getBean(SearchService.class);
		context.getBean(GetInfoService.class);
		myPhotos =context.getBean(MyPhotos.class);
		getInfo = context.getBean(GetInfoService.class);
		getExif = context.getBean(GetExifService.class);
		downloadService = context.getBean(DownloadService.class);
	
	}
	
	


	
	public void process(boolean tryAgain){
		
		SearchCriteria s = new SearchCriteria();


		// parse date from yyyy-mm-dd pattern
		Date startDate = new GregorianCalendar(2004, Calendar.OCTOBER,31).getTime();
		Date endDate = new GregorianCalendar(2005, Calendar.DECEMBER,1).getTime();



		while ( startDate.before(endDate )) {


			s.min_taken_date = startDate;
			s.max_taken_date = this.addDay(startDate);

			logger.debug("Searching for startDate=" + s.min_taken_date + ", to=" + s.max_taken_date);

			PageSummary summary = search.search(s);


			for (FlickrPhoto onFlickrPhoto : summary.photos) {

				getExif.getExif(onFlickrPhoto.id);
				getInfo.getInfo(onFlickrPhoto.id);

				logger.info(onFlickrPhoto);



			}

			logger.info("calling download service = " + downloadService );

			//
			// TODO Moving download functionality into its onw service ...
			//
			downloadService.download(path,summary);

			//search.download(path, summary);

			startDate = this.addDay(startDate);
		}
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

	private Date addDay(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);
		return cal.getTime();
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
		

		
		for (i = 0; i < args.length; i++) {
			switch (args[i]) {

			case "-path":
				if (i < args.length)
					path = args[++i];
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
	

		c.process(tryAgain);

	
	}
	
	}



