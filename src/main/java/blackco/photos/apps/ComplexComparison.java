package blackco.photos.apps;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import blackco.photos.spring.AppConfig;
import blackco.photos.spring.FlickrPhoto;
import blackco.photos.spring.FlickrPhotoDate;
import blackco.photos.spring.GetExifService;
import blackco.photos.spring.GetInfoService;
import blackco.photos.spring.MyPhoto;
import blackco.photos.spring.MyPhotos;
import blackco.photos.spring.PageSummary;
import blackco.photos.spring.PhotosService;
import blackco.photos.spring.SearchCriteria;
import blackco.photos.spring.SearchService;

public class ComplexComparison {

	private static final Logger logger = Logger.getLogger(ComplexComparison.class);
	
	private static String cache;
	private static String path = null;
	
	private ApplicationContext context ;
	private SearchService search;
	private GetInfoService getInfo;
	private GetExifService getExif;
	private MyPhotos myPhotos;
	private PhotosService photosService;
	

	public ComplexComparison(final ApplicationContext context){
		
		
		search = context.getBean(SearchService.class);
		getInfo = context.getBean(GetInfoService.class);
		getExif = context.getBean(GetExifService.class);
		myPhotos =context.getBean(MyPhotos.class);
		photosService = context.getBean(PhotosService.class);
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
	public ArrayList<FlickrPhoto> getPhotosForTakenOn(Date date){

		/* 
		 * (2) Ensure unique date to the second by retrieving from a
		 * hashmap keyed by date converted to string in following
		 * format "YYYY-MM-DD hh:mm:ss"
		 * 
		 * TODO - Do we have to do this??
		**/
		
		SearchCriteria s = new SearchCriteria();
		
		s.min_taken_date = date;
		s.max_taken_date = date;
		
		String searchDate = FlickrPhotoDate.getDateInFlickrTextFormat(date);
		
		PageSummary summary = search.search(s);

		HashMap<String, ArrayList<FlickrPhoto>> dates = 
				new HashMap<String, ArrayList<FlickrPhoto>>();

		for (FlickrPhoto onFlickrPhoto : summary.photos) {

			getInfo.getInfo(onFlickrPhoto.id);
			ArrayList<FlickrPhoto> list = dates
					.get(FlickrPhotoDate.getDateInFlickrTextFormat(onFlickrPhoto.dateTaken));
			if (list == null) {
				list = new ArrayList<FlickrPhoto>();
				dates.put(FlickrPhotoDate.getDateInFlickrTextFormat(onFlickrPhoto.dateTaken), list);
			}

			list.add(onFlickrPhoto);
		}

		ArrayList<FlickrPhoto> found;
		
		logger.debug("Searching for date=" + date + ", in KeySet=" + dates.keySet());
		
		if ( dates.get(searchDate) == null){
			found = new ArrayList<FlickrPhoto>();
		} else {
			found = dates.get(searchDate);
		}
		
		return found;

	}
	
	/*
	 * Test 
	 * (1) ComparedPhoto and Photo taken on same date (to second) , on the same camera
	 * (2) ComparedPhoto and Photo taken on same date (to second), but on different cameras
	 * (3) ComparedPhoto and Photo taken on different date (to second), same cameras
	 * 
	 * Needs
	 * (1) Photo and ComparedPhoto, no mock services
	 * 
	 */
	
	public boolean compare(MyPhoto photo, FlickrPhoto potentialMatchOnFlickr){
		
		logger.info("Matching : " +  photo.getFilename()
				 + ", "	+ photo.getDateTaken()
				 + " comparing " + potentialMatchOnFlickr.dateTaken
				 + " and  " 
				 + photo.getCamera()
				 + potentialMatchOnFlickr.camera);
		
		return ( FlickrPhotoDate.compareToSecondPrecision(
					potentialMatchOnFlickr.dateTaken, photo.getDateTaken())
				&& potentialMatchOnFlickr.camera
						.equals(photo.getCamera()) );
		
	}
	
	public static boolean IF_UNMATCHED_SEARCH_AGAIN = true;
	
	public static boolean IF_UNMATCHED_DO_NOT_SEARCH =false;
	
	public void process(){
		this.process(ComplexComparison.IF_UNMATCHED_DO_NOT_SEARCH);
	}
	
	public void process(boolean tryAgain) {
		
		int count=0;
			
		ArrayList<MyPhoto> list = new ArrayList<MyPhoto>();
		
		for ( String dir : myPhotos.getDirectories()){
			list.addAll(myPhotos.getUnprocessedPhotos(dir));
			logger.debug("process: unprocessed photos(): dir = " + dir 
					+ ", photos=" + myPhotos.getUnprocessedPhotos(dir));
			
			if ( tryAgain ){
				list.addAll(myPhotos.getUnmatchedPhotos(dir));	
				list.addAll(myPhotos.getInsufficientMetaDataNoCamera(dir));
				list.addAll(myPhotos.getInsufficientMetaDataNoDate(dir));
			}
		}
		
		logger.info("tryAgain="+ tryAgain + ",photos to process " + list);
		
		for ( MyPhoto p: list){
					
			count++;
			if (p.getDateTaken() == null) {
				
				p.setFlickrPhotoId(
						(FlickrPhoto.INSUFFICIENT_METADATA_NO_DATE));
			} else if ( p.getCamera() == null){

				p.setFlickrPhotoId(
						(FlickrPhoto.INSUFFICIENT_METADATA_NO_CAMERA));
			} else {
				
					p.setFlickrPhotoId(
							(FlickrPhoto.UNMATCHED));

					
					ArrayList<FlickrPhoto> found = 
							this.getPhotosForTakenOn(
									p.getDateTaken());
					

					for (FlickrPhoto potentialMatchOnFlickr : found) {
							getExif.getExif(potentialMatchOnFlickr.id);

							
							if ( this.compare(p, potentialMatchOnFlickr )) {

								p.setFlickrPhotoId(potentialMatchOnFlickr.id);
								

							} 

						}
					}
			
			logger.debug("MyPhoto=" + p);
			
			logger.info("ComplexComparision: Processed " + count
					+ " of " + list.size());
			
			myPhotos.save();
		}
			
			
			
			
			logger.info("ComplexComparision: Persisted");
			
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
	
	private int logMessages(FileWriter f, ArrayList<MyPhoto> photos){
		
		for ( MyPhoto p: photos){
			this.log(f, p.getFilename());
		}
		
		return photos.size();
	}

	public static void main(String[] args) {

		AnnotationConfigApplicationContext context 
				=new AnnotationConfigApplicationContext(AppConfig.class);
		
		
		AppConfig config = context.getBean(AppConfig.class);
		
		config.init(args);

		int i;
		boolean summary = false;
		boolean tryAgain = ComplexComparison.IF_UNMATCHED_DO_NOT_SEARCH;
				
		
		String upload = "/Users/blackco/Pictures/test/upload.txt";
		String matches = "/Users/blackco/Pictures/test/matches.txt";
		String noExif = "/Users/blackco/Pictures/test/noExif.txt";

		for (i = 0; i < args.length; i++) {
			switch (args[i]) {

			case "-path":
				if (i < args.length)
					path = args[++i];
				break;
			case "-summary":
				if (i < args.length)
					summary = true;
				break;
			case "-upload":
				if (i < args.length)
					upload = args[++i];
				break;

			case "-matches":
				if (i < args.length)
					upload = args[++i];
				break;

			case "-noExif":
				if (i < args.length)
					noExif = args[++i];
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

		ComplexComparison c = new ComplexComparison(context);
	

		MyPhotos myPhotos = context.getBean(MyPhotos.class);
		
		logger.debug("ComplexComparison: comparing contents of these directories= " + myPhotos.getDirectories());
		
		c.myPhotos.addDirectory(path);
		c.myPhotos.addDirectory("/Users/blackco/Pictures/test3");
		c.myPhotos.addDirectory("/Users/blackco/Pictures/test4");
		
		c.process(tryAgain);

		FileWriter f1 = null;
		FileWriter f2 = null;
		FileWriter f3 = null;
		FileWriter f4 = null;
		FileWriter f5 = null;

		try {
			f1 = new FileWriter("/Users/blackco/Pictures/test/unprocessed.txt");
			f2 = new FileWriter("/Users/blackco/Pictures/test/unmatched.txt");
			f3 = new FileWriter("/Users/blackco/Pictures/test/nodate.txt");
			f4 = new FileWriter("/Users/blackco/Pictures/test/nomcamera.txt");
			f5 = new FileWriter("/Users/blackco/Pictures/test/matched.txt");
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		int unprocessed = 0;
		int matched = 0;
		int unmatched = 0;
		int nodate=0;
		int nocamera = 0;
		
		
		for ( String dir : c.myPhotos.getDirectories()){
			
			logger.debug("Processing " + dir);
			
			unprocessed = unprocessed + c.logMessages(f1, c.myPhotos.getUnprocessedPhotos(dir));
			unmatched = unmatched + c.logMessages(f2, c.myPhotos.getUnmatchedPhotos(dir));
			nodate = nodate + c.logMessages(f3, c.myPhotos.getInsufficientMetaDataNoCamera(dir));
			nocamera = nocamera + c.logMessages(f4, c.myPhotos.getInsufficientMetaDataNoDate(dir));
			matched = matched + c.logMessages(f5, c.myPhotos.getMatchedPhotos(dir));
		}
		

		System.out.println("unprocessed     = " + unprocessed);
		System.out.println("unmatched		= " + unmatched);
		System.out.println("no date			= " + nodate);
		System.out.println("no camera		= " + nocamera);
		
		System.out.println("matched		    = " + matched);
		System.out.println("total files     = " + c.myPhotos.size());
		
			
				
		try {
			f1.close();
			f2.close();
			f3.close();
			f4.close();
			f5.close();

		} catch (IOException e) {
			
			logger.error("Cannot close output file",e);
			e.printStackTrace();
		}
	
	}
	
	}


