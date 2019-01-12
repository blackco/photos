package blackco.photos;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import blackco.photos.Services.Service;

public class GetExif {
	
	public static Photo getExif(String photoId){
		FlickrAuth auth = (FlickrAuth) Services.getService(Service.FLICKR_AUTH);

		OAuthRequest request = new OAuthRequest(Verb.GET,
				"https://api.flickr.com/services/rest/");
		request.addQuerystringParameter("method", "flickr.photos.getExif");

		request.addQuerystringParameter("api_key", auth.getApiKey());
		request.addQuerystringParameter("user_id", auth.getUserId());
		request.addQuerystringParameter("photo_id",photoId);
	
		request.addQuerystringParameter("format", "json");
		request.addQuerystringParameter("nojsoncallback", "1");
		
		Response response = auth.get(request);

	
		return buildExif(Json.createReader(new StringReader(response.getBody())));

	}

	private static Photo buildExif(JsonReader jsonReader){
		
		JsonObject jobj = jsonReader.readObject();

		JsonObject object = (JsonObject) jobj.get("photo");
		String id = object.getString("id");
				
		Photo p = ((Photos)Services.getService(Service.PHOTOS)).photos.get(id);
		
		if ( p == null ){
				p = new Photo();
				p.id = id;
				((Photos)Services.getService(Service.PHOTOS)).photos.put(id, p);
		}

		p.secret = object.getString("secret");
		p.camera = object.getString("camera");
		
		return p;
	}
		
}
