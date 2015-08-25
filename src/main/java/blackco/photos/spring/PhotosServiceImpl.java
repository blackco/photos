package blackco.photos.spring;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;



public class PhotosServiceImpl implements PhotosService {
	
		private static final Logger logger = Logger.getLogger(PhotosServiceImpl.class);

		private HashMap<String, FlickrPhoto> photos = new HashMap<String,FlickrPhoto>();
	
		
		private String cache = "/Users/blackco/Temp/photosServiceCache.json";

		
		public void setPhoto(FlickrPhoto photo){
			photos.put(photo.id, photo);
		}
		
		public FlickrPhoto getPhoto(String id){
			return photos.get(id);
		}

		public Collection<FlickrPhoto> getAll(){
			return photos.values();
		}
	
		
		/*
		 *  The Service needs the ability to look again photos on disk and refresh its records
		 */
		
		public void init(String cacheLocation){
			
			cache = cacheLocation;
			
			// Get from persistant store
			open();
			
		
			logger.debug("myPhotoCache Location= " + cache) ;
			logger.debug("Initialized photos:"  + photos);
					
		}
		
		public void empty(){
			photos = new HashMap<String,FlickrPhoto>();
		}

		public void open() {

			ObjectMapper mapper = new ObjectMapper();
			
		
			logger.debug("MyPhotosImpl.open(): cache = " + cache );
			
			try {

				this.photos = mapper.readValue(new File(cache),
						HashMap.class);

		
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			
		}


		
		public void save() {

			ObjectMapper mapper = new ObjectMapper();

			try {
				mapper.writeValue(new File(cache), photos);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			logger.debug("save(): persisted to disk = " + cache);
		}

}
