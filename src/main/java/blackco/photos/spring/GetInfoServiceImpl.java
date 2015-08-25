package blackco.photos.spring;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.log4j.Logger;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.springframework.beans.factory.annotation.Autowired;


public class GetInfoServiceImpl implements GetInfoService {

	private static final Logger logger = Logger.getLogger(GetInfo.class);
	
	private FlickrAuth auth;
	private Photos photos;
	
    @Autowired
	public void setFlickrAuth(FlickrAuth auth){
		this.auth = auth;
	}
	
    @Autowired
    public void setPhotos(Photos photos){
    	this.photos = photos;
    }
    
	public FlickrPhoto getInfo(String photoId){
	
		OAuthRequest request = new OAuthRequest(Verb.GET,
				"https://api.flickr.com/services/rest/");
		request.addQuerystringParameter("method", "flickr.photos.getInfo");

		request.addQuerystringParameter("api_key", auth.getApiKey());
		request.addQuerystringParameter("user_id", auth.getUserId());
		request.addQuerystringParameter("photo_id",photoId);
	
		request.addQuerystringParameter("format", "json");
		request.addQuerystringParameter("nojsoncallback", "1");
		
		Response response = auth.get(request);

	
		return buildInfo(Json.createReader(new StringReader(response.getBody())));

	}

	private FlickrPhoto buildInfo(JsonReader jsonReader){
		
		JsonObject jobj = jsonReader.readObject();

		JsonObject object = (JsonObject) jobj.get("photo");
		
		String id = object.getString("id");
		
		
		FlickrPhoto p = photos.getPhoto(id);
		
		if ( p == null ){
				p = new FlickrPhoto();
				p.id = id;
				photos.setPhoto(p);
		}
		
		
		
		p.id = id;
		
//		JsonArray array = (JsonArray) object.get("dates");
        
		object = (JsonObject) object.get("dates");
		
		logger.debug("date=" + object.getString("taken"));
		logger.debug("dateTaken=" + FlickrPhotoDate.setDateInFlickrTextFormat(object.getString("taken")));
		p.dateTaken = FlickrPhotoDate.setDateInFlickrTextFormat(
				object.getString("taken"));
				
		logger.debug("DateTaken=" + p.dateTaken);		
		
		return p;
	}
}
