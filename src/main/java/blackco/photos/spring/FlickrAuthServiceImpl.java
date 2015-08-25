package blackco.photos.spring;

import org.scribe.builder.ServiceBuilder;
import org.scribe.builder.api.FlickrApi;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.oauth.OAuthService;

public class FlickrAuthServiceImpl implements FlickrAuthService {

	private static OAuthService service;

	private static Token accessToken;
	private static String userId;

	public void init(String apiKey, String apiSecret, String userId,
			String accessKey, String accessSecret) {

		accessToken = new Token(accessKey, accessSecret);
		service = new ServiceBuilder().provider(FlickrApi.class).apiKey(apiKey)
				.apiSecret(apiSecret).build();
		this.userId = userId;
	}

	/* (non-Javadoc)
	 * @see blackco.samples.FlickrService#getApiKey()
	 */
	@Override
	public String getApiKey() {
		return accessToken.getToken();
	}

	/* (non-Javadoc)
	 * @see blackco.samples.FlickrService#getUserId()
	 */
	@Override
	public String getUserId() {
		return userId;
	}

	public Response get(OAuthRequest request) {

		service.signRequest(accessToken, request);
		return request.send();

	}

}
