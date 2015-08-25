package blackco.photos.spring;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;

public interface FlickrAuthService {

	public void init(String apiKey, String apiSecret, String userId,
			String accessKey, String accessSecret);

	public String getApiKey();


	public String getUserId();

	public Response get(OAuthRequest request);

}
