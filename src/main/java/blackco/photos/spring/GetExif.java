package blackco.photos.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GetExif {

	
	private GetExifService service;
	
	@Autowired
	public GetExif(GetExifService service){
		this.service = service;
	}
	
	public FlickrPhoto getExif(String photoId){
		return this.service.getExif(photoId);
	}
}
