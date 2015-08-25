package blackco.photos.spring;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;



@Configuration
@ComponentScan
public class AppConfig {

	private static final Logger logger = Logger.getLogger(AppConfig.class);
	
	   @Bean
	    public FlickrAuthService myFlickrAuthService(){
	    	return new FlickrAuthServiceImpl();
	    }

	    @Bean
	    public PhotosService myPhotosService(){
	    	return new PhotosServiceImpl();
	    }
	   
	    @Bean
	    public GetExifService myGetExifService(){
	    	return new GetExifServiceImpl();
	    }
	    
	    @Bean
	    public SearchService mySearchService(){
	    	return new SearchServiceImpl();
	    }
	    
	    
	    @Bean
	    public GetInfoService myGetInfoService(){
	    	return new GetInfoServiceImpl();
	    }
	  
	    @Bean
	    public MyPhotos myMyPhotos(){
	    	return new MyPhotosImpl();
	    }
	  	
	    @Bean
	    public MyPhotoMetaDataReader myMyPhotoMetaDataReader(){
	    	return new MyPhotoMetaDataReaderImpl();
	    }
	    
	    @Bean
	    public PhotoFinder myPhotoFinder(){
	    	return new PhotoFinderImpl();
	    }
	 
	    private FlickrAuth flickrAuth;
	    
	    private MyPhotos myPhotos;
	    
	    private PhotosService photoService;
	 
	    @Autowired
	    public void setFlickrAuth(FlickrAuth flickrAuth){
	    	this.flickrAuth = flickrAuth;
	    }
	    
	    @Autowired
	    public void setMyPhotos(MyPhotos myPhotos){
	    	this.myPhotos = myPhotos;
	    }
	    
	    @Autowired
	    public void setPhotosService(PhotosService service){
	    	this.photoService = service;
	    }
	    
	   public void init(String[] args){
		   
	    	String requestKey = null;
			String requestSecret = null;
			String userId = null;
			String accessKey = null;
			String accessSecret = null;
			String cache = null;
			
			int i;

			for (i = 0; i < args.length; i++) {
				switch (args[i]) {
				

				case "-reqKey":
					if (i < args.length)
						requestKey = args[++i];
					break;

				case "-reqSecret":
					if (i < args.length)
						requestSecret = args[++i];
					break;

				case "-accessKey":
					if ( i< args.length)
						accessKey = args[++i];
					break;
				
				case "-accessSecret":
					if ( i< args.length)
						accessSecret = args[++i];
					break;
					
				case "-userId":
					if (i < args.length)
						userId = args[++i];
					break;
					
				
				case "-cache":
					if (i < args.length)
						cache = args[++i];
					break;
				
				}
			}
			
				  
				 // FlickrAuth flickr = context.getBean(FlickrAuth.class);
			      
			      logger.info("initiating flickr connection:  requestKey "+ requestKey +
			    		  	", requestSecret= "+requestSecret +
			    		  	", userId=" + userId +
			    		  	", accessKey = " + accessKey+
			    		  	", accessSecret =" +  accessSecret);
			      
			      flickrAuth.init(requestKey, requestSecret
							 , userId, accessKey, accessSecret);
			      
			      logger.info("Creating default set of FlickrPhotos");
			      
			      photoService.setPhoto(new FlickrPhoto(FlickrPhoto.UNPROCESSED));
			      photoService.setPhoto(new FlickrPhoto(FlickrPhoto.UNMATCHED));
			      photoService.setPhoto(new FlickrPhoto(FlickrPhoto.INSUFFICIENT_METADATA_NO_DATE));
			      photoService.setPhoto(new FlickrPhoto(FlickrPhoto.INSUFFICIENT_METADATA_NO_CAMERA));
			      
			      logger.info("retrieving MyPhotos cache");
			      myPhotos.init(cache);
			      logger.debug("Retrieved " + myPhotos.size());
	    }

}
