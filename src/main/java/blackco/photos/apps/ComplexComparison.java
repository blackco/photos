package blackco.photos.apps;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javax.imageio.ImageIO;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import blackco.photos.metadata.MyTag;
import blackco.photos.spring.AppConfig;
import blackco.photos.spring.FlickrPhoto;
import blackco.photos.spring.FlickrPhotoDate;
import blackco.photos.spring.GetExifService;
import blackco.photos.spring.GetInfoService;
import blackco.photos.spring.MyPhoto;
import blackco.photos.spring.MyPhotos;
import blackco.photos.spring.PageSummary;
import blackco.photos.spring.SearchCriteria;
import blackco.photos.spring.SearchService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.drew.metadata.Metadata;

public class ComplexComparison {

	private static final Logger logger = Logger.getLogger(ComplexComparison.class);
	
	private static String path = null;
	

	private SearchService search;
	private GetInfoService getInfo;
	private GetExifService getExif;
	private MyPhotos myPhotos;
	
	

	public ComplexComparison(final ApplicationContext context){
		
		
		search = context.getBean(SearchService.class);
		getInfo = context.getBean(GetInfoService.class);
		getExif = context.getBean(GetExifService.class);
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
			
			// TODO 1) add argument of which photo to save!
			myPhotos.save(p);
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
	
	private int logMessagesInJson(FileWriter f, ArrayList<MyPhoto> photos){
		
		this.log(f, "{");
		for ( MyPhoto p: photos){
			this.log(f, "filename':");
			this.log(f, p.getFilename());
			this.log(f, ",");
		}
		this.log(f, "}");
		return photos.size();
	}

	
	private final static ObjectMapper mapper;
	   static {
	      mapper = new ObjectMapper();
	}
	
	public static String serialize(FileWriter f1, Object object) {
	      try {
	    	  
	    	  try {
				mapper.writeValue(f1, object);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	  
	         return mapper.writeValueAsString(object);
	      } catch (JsonProcessingException e) {
	         e.printStackTrace();
	      }
	      return null;
	   }
	
	
	public Summary getSummary(){
		
		Summary s = new Summary();
		
		for ( String dir : this.myPhotos.getDirectories()){
		
				s.unprocessed = s.unprocessed + this.myPhotos.getUnprocessedPhotos(dir).size();
				s.unmatched = s.unmatched + this.myPhotos.getUnmatchedPhotos(dir).size();
				s.matched = s.matched + this.myPhotos.getMatchedPhotos(dir).size();
				s.nocamera = s.nocamera + this.myPhotos.getInsufficientMetaDataNoCamera(dir).size();
				s.nodate = s.nodate + this.myPhotos.getInsufficientMetaDataNoDate(dir).size();
		}
	
		return s;
	}
	
	public ArrayList<Setting> getSettings(){
		
		ArrayList<Setting> settings = new ArrayList<Setting>();
		
		for ( String dir :  this.myPhotos.getDirectories()){
			settings.add( new Setting(dir));
		}
		
		return settings;
	}
	
	public ArrayList<MyPhoto> getUnprocessedPhotos(){
		
		ArrayList<MyPhoto> unprocessedToJson = new ArrayList<MyPhoto>();
	
		for ( String dir : this.myPhotos.getDirectories()){
			unprocessedToJson.addAll(this.myPhotos.getUnprocessedPhotos(dir));
		}
		
		return unprocessedToJson;
	}

	public ArrayList<MyPhoto> getMatchedPhotos(){
		
		ArrayList<MyPhoto> result = new ArrayList<MyPhoto>();
	
		for ( String dir : this.myPhotos.getDirectories()){
			result.addAll(this.myPhotos.getMatchedPhotos(dir));
		}
		
		return result;
	}

	public ArrayList<MyPhoto> getUnmatchedPhotos(){
		
		ArrayList<MyPhoto> result = new ArrayList<MyPhoto>();
	
		for ( String dir : this.myPhotos.getDirectories()){
			result.addAll(this.myPhotos.getUnmatchedPhotos(dir));
		}
		
		return result;
	}
	
	public ArrayList<MyPhoto> getInsufficientMetaDataNoDate(){
		
		ArrayList<MyPhoto> result = new ArrayList<MyPhoto>();
	
		for ( String dir : this.myPhotos.getDirectories()){
			
			result.addAll(this.myPhotos.getInsufficientMetaDataNoDate(dir));
		}
		
		logger.info("getInsufficientMetadataNoDate(): myPhotos = " + myPhotos ); 
		
		return result;
	}
	
	public void serializeMetadataForPhotosWithInsufficientMetaData(String dir) throws IOException{
		
		
		ArrayList<Metadata> results = new ArrayList<Metadata>();
		
		for ( MyPhoto p : getInsufficientMetaDataNoDate()){
			
			FileWriter f1 = new FileWriter(dir + "/metadata/nodate" + p.getId()+ ".json");
			this.serialize(f1, p.getSuggestedDateTags());
					
			f1.close();
		}
		
		for ( MyPhoto p : getInsufficientMetaDataNoCamera()){
			
			FileWriter f1 = new FileWriter(dir + "/metadata/nocamera" + p.getId() + ".json");
			this.serialize(f1, p.getSuggestedCameraTags());
			
				
			f1.close();
		}
		
		logger.info("serialMetadaraForPhotosWithInSufficientMetadata(): end" );

	}
	
	public ArrayList<MyPhoto> getInsufficientMetaDataNoCamera(){
		
		ArrayList<MyPhoto> result = new ArrayList<MyPhoto>();
	
		for ( String dir : this.myPhotos.getDirectories()){
			result.addAll(this.myPhotos.getInsufficientMetaDataNoCamera(dir));
		}
		
		return result;
	}
	
	public void createThumbnails(String location, ArrayList<MyPhoto> photos){
		
		for ( MyPhoto p: photos){
			BufferedImage originalImage;
			try {
				originalImage = ImageIO.read(new File(p.getFilename()));
				
				int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
				
				BufferedImage resizeImageJpg = resizeImage(originalImage, type);
				ImageIO.write(resizeImageJpg, "jpg", new File(location + "/img/" + p.getId() + ".jpg")); 
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				logger.error(e);
			}
			

		}
		
	}
	
	private static BufferedImage resizeImage(BufferedImage originalImage, int type){
		
		int IMG_WIDTH = 100;
		int IMG_HEIGHT = 100;
		
		BufferedImage resizedImage = new BufferedImage(IMG_WIDTH, IMG_HEIGHT, type);
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, IMG_WIDTH, IMG_HEIGHT, null);
		g.dispose();
			
		return resizedImage;
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

		ComplexComparison c = new ComplexComparison(context);
	

		MyPhotos myPhotos = context.getBean(MyPhotos.class);
		
		logger.debug("ComplexComparison: comparing contents of these directories= " + myPhotos.getDirectories());
		
		c.myPhotos.addDirectory(path);
		//c.myPhotos.addDirectory("/Users/blackco/Pictures/test3");
		//c.myPhotos.addDirectory("/Users/blackco/Pictures/test4");
		
		c.process(tryAgain);

		FileWriter f1 = null;
		FileWriter f2 = null;
		FileWriter f3 = null;
		FileWriter f4 = null;
		FileWriter f5 = null;
		FileWriter f6 = null;
		FileWriter f7 = null;

		try {
			f1 = new FileWriter(results + "/unprocessed/unprocessed.json");
			f2 = new FileWriter(results + "/unmatched/unmatched.json");
			f3 = new FileWriter(results + "/unmatched/nodate.json");
			f4 = new FileWriter(results + "/unmatched/nocamera.json");
			f5 = new FileWriter(results + "/matched/matched.json");
			f6 = new FileWriter(results + "/summary/summary.json");
			f7 = new FileWriter(results + "/settings/settings.json");
			
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
				
		try {
			c.serialize(f1, c.getUnprocessedPhotos());
			c.serialize(f2, c.getUnmatchedPhotos());
			c.serialize(f3, c.getInsufficientMetaDataNoDate());
			c.serialize(f4, c.getInsufficientMetaDataNoCamera());
			c.serialize(f5, c.getMatchedPhotos());
			c.serialize(f6, c.getSummary());
			c.serialize(f7 ,c.getSettings());
			c.serializeMetadataForPhotosWithInsufficientMetaData(results);
			c.createThumbnails(results, c.getInsufficientMetaDataNoDate() );
			c.createThumbnails(results, c.getInsufficientMetaDataNoCamera());
			c.createThumbnails(results, c.getUnmatchedPhotos());

			
			f1.close();
			f2.close();
			f3.close();
			f4.close();
			f5.close();
			f6.close();

		} catch (IOException e) {
			
			logger.error("Cannot close output file",e);
			e.printStackTrace();
		}
	
	}
	
	}


