package blackco.photos.spring;

import java.util.Collection;


public interface PhotosService {
	
	public void setPhoto(FlickrPhoto photo);
	
	public FlickrPhoto getPhoto(String id);
	
	public Collection<FlickrPhoto> getAll();



}
