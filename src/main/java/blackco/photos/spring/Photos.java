package blackco.photos.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class Photos {

	private PhotosService service;
	
	@Autowired
	public Photos(PhotosService service){
		this.service = service;
	}
	
	
	public void setPhoto(FlickrPhoto photo){
		this.service.setPhoto(photo);
	}
	
	public FlickrPhoto getPhoto(String id){
		return this.service.getPhoto(id);
	}
}
