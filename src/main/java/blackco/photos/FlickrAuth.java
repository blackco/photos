package blackco.photos;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FlickrApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;


public class FlickrAuth {

	  private static OAuthService service;
	
	  private static Token accessToken;
	  private static String userId;
	  
	  public static final String SERVICE = "blackco.photos.FlickrAuth";


	public FlickrAuth(String apiKey, String apiSecret, String userId, 
			String accessKey, String accessSecret){
		
		accessToken = new Token( accessKey, accessSecret);
		service =  new ServiceBuilder().provider(FlickrApi.class).apiKey(apiKey).apiSecret(apiSecret).build();
		this.userId = userId;
	}
	
	public String getApiKey(){
		return accessToken.getToken();
	}
	
	public String getUserId(){
		return userId;
	}
	  
	public static Response get(OAuthRequest request){
	
		 	service.signRequest(accessToken, request);
		    return request.send();
		   
	}
	
	
	
}
