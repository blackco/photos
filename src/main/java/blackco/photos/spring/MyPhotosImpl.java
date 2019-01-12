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

import blackco.photos.metadata.MyTag;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
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
	
	private String directoriesCache = "directoriesCache.json";

	/*
	 * The Service needs the ability to look again photos on disk and refresh
	 * its records
	 */

	public void init(String cacheLocation) {

		cache = cacheLocation;

		// Get from persistant store
		open();

		logger.debug("myPhotoCache Location= " + cache);
		logger.debug("Initialized myPhotosCache:" + myPhotosCache.getPhotos());

	}

	public void empty() {
		myPhotosCache = new MyPhotosCache();
	}

	@Autowired
	public void setMyPhotoMetaDataReader(MyPhotoMetaDataReader reader) {
		myPhotoMetaDataReader = reader;
	}

	@Autowired
	public void setPhotoFinder(PhotoFinder finder) {
		photoFinder = finder;
	}

	public int size() {

		int result = 0;

		for (String dir : this.getDirectories()) {
			result = result + this.getPhotos(dir).size();

		}
		return result;
	}

	public ArrayList<MyPhoto> getUnprocessedPhotos(String directory) {

		ArrayList<MyPhoto> results = new ArrayList<MyPhoto>();
		HashMap<String, MyPhoto> files = myPhotosCache.getPhotos().get(
				directory);

		for (MyPhoto p : files.values()) {

			if (FlickrPhoto.UNPROCESSED.equals(p.getFlickrPhotoId())) {

				results.add(p);
			}
		}

		return results;

	}

	public ArrayList<MyPhoto> getInsufficientMetaDataNoDate(String directory) {

		ArrayList<MyPhoto> results = new ArrayList<MyPhoto>();
		HashMap<String, MyPhoto> files = myPhotosCache.getPhotos().get(
				directory);

		for (MyPhoto p : files.values()) {

			if (FlickrPhoto.INSUFFICIENT_METADATA_NO_DATE.equals(p
					.getFlickrPhotoId())) {

				results.add(p);
			}
		}

		return results;

	}

	public ArrayList<MyPhoto> getInsufficientMetaDataNoCamera(String directory) {

		ArrayList<MyPhoto> results = new ArrayList<MyPhoto>();
		HashMap<String, MyPhoto> files = myPhotosCache.getPhotos().get(
				directory);

		for (MyPhoto p : files.values()) {

			if (FlickrPhoto.INSUFFICIENT_METADATA_NO_CAMERA.equals(p
					.getFlickrPhotoId())) {

				results.add(p);
			}
		}

		return results;

	}

	public Collection<MyPhoto> getPhotos(String directory) {
		return myPhotosCache.getPhotos().get(directory).values();

	}

	public ArrayList<MyPhoto> getUnmatchedPhotos(String directory) {

		ArrayList<MyPhoto> results = new ArrayList<MyPhoto>();
		HashMap<String, MyPhoto> files = myPhotosCache.getPhotos().get(
				directory);

		for (MyPhoto p : files.values()) {

			if (FlickrPhoto.UNMATCHED.equals(p.getFlickrPhotoId())) {

				results.add(p);
			}
		}

		return results;

	}

	public ArrayList<MyPhoto> getMatchedPhotos(String directory) {
		ArrayList<MyPhoto> results = new ArrayList<MyPhoto>();
		HashMap<String, MyPhoto> files = myPhotosCache.getPhotos().get(
				directory);

		Set<String> notMatched = new HashSet<String>();

		notMatched.add(FlickrPhoto.INSUFFICIENT_METADATA_NO_CAMERA);
		notMatched.add(FlickrPhoto.INSUFFICIENT_METADATA_NO_DATE);
		notMatched.add(FlickrPhoto.UNMATCHED);
		notMatched.add(FlickrPhoto.UNPROCESSED);

		for (MyPhoto p : files.values()) {

			if (!notMatched.contains(p.getFlickrPhotoId())) {

				results.add(p);
			}
		}

		return results;
	}

	public Set<String> getDirectories() {
		return myPhotosCache.getPhotos().keySet();
	}

	/*
	 * TODO This should be deprecated (non-Javadoc)
	 * 
	 * @see blackco.photos.spring.MyPhotos#open()
	 */
	public void open() {

		ObjectMapper mapper = new ObjectMapper();

		logger.debug("MyPhotosImpl.open(): cache = " + cache);

		try {

			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
					false);

			logger.info("reading directories cache");
			Set directories = mapper.readValue(new File(cache + "/" + directoriesCache),
					Set.class);

			for ( Object s : directories){
				logger.info("adding directory from cache" + (String)s);
				addDirectory((String)s);
			}
			
			for (String dir : this.getDirectories()) {
				for (MyPhoto p : this.getPhotos(dir)) {
					logger.debug("open(): p = " + p);
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	// TODO a save all function
	public void save() {
		
		// TODO SAVE the directories cache!!

		for (String dir : this.getDirectories()) {

			HashMap<String, MyPhoto> files = myPhotosCache.getPhotos().get(dir);

			for (MyPhoto p : files.values()) {
				this.save(p);
			}
		}
	}

	public void save(MyPhoto myPhoto) {

		ObjectMapper mapper = new ObjectMapper();

		// TODO
		/*
		 * 1. Change this to write a file per photo 
		 * 2. Create a copy of the file
		 * if it does not have sufficient metadata
		 */
		try {

			logger.info("Writing directories cache");
			mapper.writeValue(new File(cache + "/" + directoriesCache),
					this.getDirectories());

			logger.info("Writing specific photo " + myPhoto);

			// id needs to be hashcode of path and lastModified date of file
			mapper.writeValue(new File(getMyPhotoId(myPhoto)), myPhoto);

			if (FlickrPhoto.INSUFFICIENT_METADATA_NO_DATE.equals(myPhoto
					.getFlickrPhotoId()) || 
				FlickrPhoto.INSUFFICIENT_METADATA_NO_CAMERA.equals(myPhoto
							.getFlickrPhotoId())
					) {
				
				
					logger.info("Persist thumbnail of photo to disk");
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error(e);
		}

		logger.debug("save(): persisted to disk = " + cache);
	}

	public void addPhoto(String directory, MyPhoto myPhoto) {
		HashMap<String, MyPhoto> cachedList = myPhotosCache.getPhotos().get(
				directory);

		if (cachedList == null) {
			myPhotosCache.getPhotos().put(directory,
					new HashMap<String, MyPhoto>());
			cachedList = myPhotosCache.getPhotos().get(directory);
		}

		if (cachedList.get(myPhoto.getFilename()) == null) {
			cachedList.put(myPhoto.getFilename(), myPhoto);
			logger.info("Adding myPhoto to cache for first time" + myPhoto);
		} else {
			logger.info("Existing myPhoto instance exists, not adding"
					+ myPhoto);
		}
	}

	public void addDirectory(String directory) {

		HashMap<String, MyPhoto> refreshedList = buildPhotos(photoFinder
				.getOnDiskList(directory));

		for (MyPhoto p : refreshedList.values()) {
			addPhoto(directory, p);
		}

	}

	private String getMyPhotoId(Path f) {
		
	
		String id = "MyPhoto" + Integer.toString(f.toFile().getAbsolutePath().hashCode());
		return cache + "/" + id
				+ "_" + f.toFile().lastModified() + ".json";
	}

	private String getMyPhotoId(MyPhoto p){
	
		
		return cache + "/" + p.getId() + ".json";
	}
	
	/*
	 * 1) get directories, 2) Primary Key of MyPhoto = hashcode of path name of
	 * photo+last modified date 3) retrieve from cached object 4) if null then
	 * retrieve MyPhoto instances back from disk 5) add to cache
	 */

	private HashMap<String, MyPhoto> buildPhotos(ArrayList<Path> list) {

		ObjectMapper mapper = new ObjectMapper();

		HashMap<String, MyPhoto> photos = new HashMap<String, MyPhoto>();

		for (Path s : list) {

			mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
					false);

			MyPhoto photo = null;
			
			try {
				logger.info("reading photo from disk " + s.getFileName());
				photo = mapper.readValue(new File(getMyPhotoId(s)),
						MyPhoto.class);
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				logger.info("cannot locate previous record for " + s);
			}

			if (photo != null) {
				logger.info("photo built from file " + photo );
			};
			
			if (photo == null) {
				photo = new MyPhoto();

				File jpegFile = s.toFile();

				photo.setFilename(s.toFile().getAbsolutePath());
				photo.setLastModifiedDate(s.toFile().lastModified());

				

				try {

					Metadata metadata = ImageMetadataReader
							.readMetadata(jpegFile);

					photo.setDateTaken(myPhotoMetaDataReader.getDate(metadata));
					photo.setCamera(myPhotoMetaDataReader.getCamera(metadata));

					photo.setSuggestedDateTags(myPhotoMetaDataReader
							.getSuggestedDateTags(metadata));

					for (MyTag t1 : photo.getSuggestedDateTags().list) {
						logger.info("Suggested date tags for photo  "
								+ photo.toString() + " , " + photo.getId()
								+ "," + t1.tagName + ", " + t1.tagValue);
					}

					photo.setSuggestedCameraTags(myPhotoMetaDataReader
							.getSuggestedCameraTags(metadata));

					for (MyTag t2 : photo.getSuggestedCameraTags().list) {
						logger.info("Suggested camera tags =  " + t2.tagName
								+ ", " + t2.tagValue);
					}

				} catch (Exception e) {
					logger.error("MyPhotosImpl.buildPhotos(): processing "
							+ photo, e);
				}
			}
			
			photos.put(photo.getFilename(), photo);
		}
		
		

		return photos;

	}

}
