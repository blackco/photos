package blackco.photos.spring;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;


/*
 *  (1) MyPhotos
	 *   		- This will need 
	 *   		 (a) list of directories being reconciled
	 *   		 (b) the files under each directory
	 *   		      - Each File needs
	 *   				 * Path
	 *   				 * Name
	 *   				 * Date Taken
	 *   				 * Camera
	 *   				 * Flickr Photo 
	 *   					  With a NullFlickr Photo type which indicates 
	 *   					  we looked but could'nt find it 
	 *   
	 *   	- Cache needs to 
	 *   		- get the files in each directory and links to flickr previously determined
	 *   		- add new files
	 *   
	 *   
	 *   
	 *   
 */
public interface MyPhotos {
	
	public ArrayList<MyPhoto> getMatchedPhotos(String directory);
	
	public ArrayList<MyPhoto> getUnprocessedPhotos(String directory);
	
	public ArrayList<MyPhoto> getUnmatchedPhotos(String directory);
	
	public ArrayList<MyPhoto> getInsufficientMetaDataNoDate(String directory);
	
	public ArrayList<MyPhoto> getInsufficientMetaDataNoCamera(String directory);
	
	public Set<String> getDirectories();
	
	public Collection<MyPhoto> getPhotos(String directory);
	
	public void addDirectory(String directory);
	
	public void addPhoto(String directory, MyPhoto photo);
	
	public void save();
	
	public void init(String cacheLocation);
	
	public void empty();
	
	public int size();
	
	public void open();

	

}
