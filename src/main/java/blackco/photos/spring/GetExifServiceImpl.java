package blackco.photos.spring;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.springframework.beans.factory.annotation.Autowired;


public class GetExifServiceImpl implements GetExifService {

	private FlickrAuth auth;
	private Photos photos;

	@Autowired
	public void setFlickrAuth(FlickrAuth auth) {
		this.auth = auth;
	}

	@Autowired
	public void setPhotos(Photos photos) {
		this.photos = photos;
	}

	public FlickrPhoto getExif(String photoId) {

		OAuthRequest request = new OAuthRequest(Verb.GET,
				"https://api.flickr.com/services/rest/");
		request.addQuerystringParameter("method", "flickr.photos.getExif");

		request.addQuerystringParameter("api_key", auth.getApiKey());
		request.addQuerystringParameter("user_id", auth.getUserId());
		request.addQuerystringParameter("photo_id", photoId);

		request.addQuerystringParameter("format", "json");
		request.addQuerystringParameter("nojsoncallback", "1");

		Response response = auth.get(request);

		return buildExif(Json
				.createReader(new StringReader(response.getBody())));

	}

	private FlickrPhoto buildExif(JsonReader jsonReader) {

		JsonObject jobj = jsonReader.readObject();

		JsonObject object = (JsonObject) jobj.get("photo");
		String id = object.getString("id");

		FlickrPhoto p = photos.getPhoto(id);

		if (p == null) {
			p = new FlickrPhoto();
			p.id = id;
			photos.setPhoto(p);
			;
		}

		p.secret = object.getString("secret");
		p.camera = object.getString("camera");

		return p;
	}

}
