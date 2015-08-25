package blackco.photos.spring;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.fasterxml.jackson.databind.ObjectMapper;


/*
 *  1) The Service needs to persist reference to the photo on disk
 *  2) The Service needs the ability to look again photos on disk and refresh its records
 *  3) The Service needs the ability to look again at flickr to see if it can find the photo now
 * 
 */

public class MyPhotosImpl implements MyPhotos {
	

	private static final Logger logger = Logger.getLogger(MyPhotosImpl.class);
	
	private MyPhotosCache myPhotosCache = new MyPhotosCache();
	
	private MyPhotoMetaDataReader myPhotoMetaDataReader;
	
	private PhotoFinder photoFinder;
		
	private String cache; 
	
	
	
	/*
	 *  The Service needs the ability to look again photos on disk and refresh its records
	 */
	
	public void init(String cacheLocation){
		
		cache = cacheLocation;
		
		// Get from persistant store
		open();
		
	
		logger.debug("myPhotoCache Location= " + cache) ;
		logger.debug("Initialized myPhotosCache:"  + myPhotosCache.getPhotos());
				
	}
	
	public void empty(){
		myPhotosCache = new MyPhotosCache();
	}


    @Autowired
    public void setMyPhotoMetaDataReader(MyPhotoMetaDataReader reader){
    	myPhotoMetaDataReader = reader;
    }
    
    @Autowired
    public void setPhotoFinder(PhotoFinder finder){
    	photoFinder = finder;
    }
 

    public int size(){
    	
    	int result=0;
    	
    	for ( String dir : this.getDirectories()){
    		result = result + this.getPhotos(dir).size();
    		
    	}
    	return result;
    }
	
	public ArrayList<MyPhoto> getUnprocessedPhotos(String directory){
		
		ArrayList<MyPhoto> results = new ArrayList<MyPhoto>();
		HashMap<String, MyPhoto> files = myPhotosCache.getPhotos().get(directory);
				
		for ( MyPhoto p :  files.values()){
				
			
			if ( FlickrPhoto.UNPROCESSED.equals(p.getFlickrPhotoId())){
				
				results.add(p);
			}
		}
		
		return results;
		
	}
	
	public ArrayList<MyPhoto> getInsufficientMetaDataNoDate(String directory){
		
		ArrayList<MyPhoto> results = new ArrayList<MyPhoto>();
		HashMap<String, MyPhoto> files = myPhotosCache.getPhotos().get(directory);
		
		for ( MyPhoto p :  files.values()){
			
			if ( FlickrPhoto.INSUFFICIENT_METADATA_NO_DATE.equals(p.getFlickrPhotoId())){
				
				results.add(p);
			}
		}
		
		return results;
		
	}
	
	public ArrayList<MyPhoto> getInsufficientMetaDataNoCamera(String directory){
		
		ArrayList<MyPhoto> results = new ArrayList<MyPhoto>();
		HashMap<String, MyPhoto> files = myPhotosCache.getPhotos().get(directory);
		
		for ( MyPhoto p :  files.values()){
			
			if ( FlickrPhoto.INSUFFICIENT_METADATA_NO_CAMERA.equals(p.getFlickrPhotoId())){
				
				results.add(p);
			}
		}
		
		return results;
		
	}
	
	
	public Collection<MyPhoto> getPhotos(String directory){
		return myPhotosCache.getPhotos().get(directory).values();
		
	}
	
	public ArrayList<MyPhoto> getUnmatchedPhotos(String directory){
		
		ArrayList<MyPhoto> results = new ArrayList<MyPhoto>();
		HashMap<String, MyPhoto> files = myPhotosCache.getPhotos().get(directory);
		
		for ( MyPhoto p :  files.values()){
			
			if ( FlickrPhoto.UNMATCHED.equals(p.getFlickrPhotoId())){
				
				results.add(p);
			}
		}
		
		return results;
		
	}
	
	public ArrayList<MyPhoto> getMatchedPhotos(String directory)
	{
		ArrayList<MyPhoto> results = new ArrayList<MyPhoto>();
		HashMap<String, MyPhoto> files = myPhotosCache.getPhotos().get(directory);
		
		Set<String> notMatched = new HashSet<String>();
		
		notMatched.add(FlickrPhoto.INSUFFICIENT_METADATA_NO_CAMERA);
		notMatched.add(FlickrPhoto.INSUFFICIENT_METADATA_NO_DATE);
		notMatched.add(FlickrPhoto.UNMATCHED);
		notMatched.add(FlickrPhoto.UNPROCESSED);
		
		for ( MyPhoto p :  files.values()){
			
			if (! notMatched.contains(p.getFlickrPhotoId())){
				
				results.add(p);
			}
		}
		
		return results;
	}
	

	public Set<String> getDirectories(){
		return myPhotosCache.getPhotos().keySet();
	}
	
	public void open() {

		ObjectMapper mapper = new ObjectMapper();
		
	
		logger.debug("MyPhotosImpl.open(): cache = " + cache );
		
		try {

			this.myPhotosCache = mapper.readValue(new File(cache),
					MyPhotosCache.class);

			for ( String dir : this.getDirectories()){
				for ( MyPhoto p: this.getPhotos(dir)){
					logger.debug("open(): p = " + p);
				}
			}
			
			

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}

	public void save() {

		ObjectMapper mapper = new ObjectMapper();

		try {
			mapper.writeValue(new File(cache), myPhotosCache);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.debug("save(): persisted to disk = " + cache);
	}

	public void addPhoto(String directory, MyPhoto myPhoto){
		HashMap<String,MyPhoto> cachedList = myPhotosCache.getPhotos().get(directory);
		
		if ( cachedList == null ){
			myPhotosCache.getPhotos().put(directory, new HashMap<String,MyPhoto>());
			cachedList = myPhotosCache.getPhotos().get(directory);
		}  
		
		
		if ( cachedList.get(myPhoto.getFilename()) == null){
			cachedList.put(myPhoto.getFilename(), myPhoto);
		}
	}
	
	
	public void addDirectory(String directory){
			
		
		HashMap<String,MyPhoto>  refreshedList = buildPhotos(photoFinder.getOnDiskList(directory));
		
		
		for ( MyPhoto p: refreshedList.values()){
			addPhoto(directory, p);
		}
		
	}
	
	
	
	private HashMap<String,MyPhoto> buildPhotos(ArrayList<Path> list) {

				
			HashMap<String,MyPhoto> photos = new HashMap<String,MyPhoto>();
			
			for (Path s : list) {

				MyPhoto photo = new MyPhoto();

				File jpegFile = s.toFile();

				photo.setFilename( s.toFile().getAbsolutePath());
						

				photos.put(photo.getFilename(),photo);

				try {
					
					logger.debug("BuildPhotos(): file=" + s.toFile().getAbsolutePath());
		
					//myPhotoMetaDataReader.printAllTags(jpegFile);
					
					Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);
				
					
					photo.setDateTaken( myPhotoMetaDataReader.getDate(metadata));
						

					//myPhotoMetaDataReader.getTitle(metadata);
					
					photo.setCamera(myPhotoMetaDataReader.getCamera(metadata));
					
				} catch (Exception e) {
					logger.error("MyPhotosImpl.buildPhotos(): processing "
									+ photo, e);
				}
			}
			
			return photos;
			

		}



}
