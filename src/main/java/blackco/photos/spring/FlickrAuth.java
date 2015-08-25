package blackco.photos.spring;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;



@Component
public class FlickrAuth {
	
	final FlickrAuthService service;
	
	@Autowired
	public FlickrAuth(FlickrAuthService service){
		this.service = service;
	}
	
	public void init(String apiKey, String apiSecret, String userId,
			String accessKey, String accessSecret){
		this.service.init(apiKey, apiSecret, userId, accessKey, accessSecret);
	}

	public String getApiKey(){
		return this.service.getApiKey();
	}


	public String getUserId(){
		return this.service.getUserId();
	}

	public Response get(OAuthRequest request){
		return this.service.get(request);
	}

}
