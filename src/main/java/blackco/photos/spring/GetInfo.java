package blackco.photos.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class GetInfo {
	
	final GetInfoService service;
	
	@Autowired
	public GetInfo(GetInfoService service){
		this.service = service;
	}
	
	public FlickrPhoto getInfo(String id){
		return this.service.getInfo(id);
	}

}
