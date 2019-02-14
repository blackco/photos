package blackco.photos.spring;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.Collection;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;



public class PhotosServiceImpl implements PhotosService {
	
		private static final Logger logger = Logger.getLogger(PhotosServiceImpl.class);

		private HashMap<String, FlickrPhoto> photos = new HashMap<String,FlickrPhoto>();
	
		
		private String cache = "/Users/colinblack/Temp/photosServiceCache.json";

    private Connection conn  =null;

	private CallableStatement cStmt = null;


	public void setPhoto(FlickrPhoto photo){

    	logger.info("caching photo = " + photo);
        photos.put(photo.id, photo);
        updatePhoto(photo);
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


    private void getConnection(){


        try {

            Class.forName("com.mysql.jdbc.Driver");

            conn =
                    DriverManager.getConnection("jdbc:mysql://localhost", "photos", "elephants");

            conn.setCatalog("flickr");

            cStmt = conn.prepareCall("{call updatePhotos(?, ? , ? , ?, ? , ?)}");



		} catch (Exception ex) {
            // handle any errors
            logger.error(ex);

        }
    }


    /*

    CREATE PROCEDURE updatePhotos(IN _flickrId VARCHAR(255), \
                        IN _downloaded BOOLEAN, \
                        IN _url VARCHAR(255), \
                        IN _title VARCHAR(255), \
                        IN _takenInUtc timestamp, \
                        IN _camera VARCHAR(255))
     */


    private void updatePhoto(FlickrPhoto flickrPhoto){


        try {
            if (conn == null) {
                getConnection();
            }



			cStmt.setString(1 , flickrPhoto.getId());
			cStmt.setBoolean(2, false);
			cStmt.setString( 3, flickrPhoto.getDownloadUrl());
            cStmt.setString( 4, flickrPhoto.getTitle());
            if ( flickrPhoto.getDateTaken() != null) {
				cStmt.setTimestamp(5, new Timestamp(flickrPhoto.getDateTaken().getTime()));
			} else {
				cStmt.setTimestamp(5, null);
			}

			cStmt.setString(6, flickrPhoto.getCamera());

			logger.info("callableStatement cStmt after set =  " + cStmt);

			cStmt.execute();

        } catch ( Exception e){

            logger.error(e);
        }
    }


}
