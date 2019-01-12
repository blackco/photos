package blackco.photos;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import org.apache.log4j.Logger;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import blackco.photos.Services.Service;

public class GetInfo {
	
	private static final Logger logger = Logger.getLogger(GetInfo.class);
	
	public static Photo getInfo(String photoId){
		FlickrAuth auth = (FlickrAuth) Services.getService(Service.FLICKR_AUTH);

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

	private static Photo buildInfo(JsonReader jsonReader){
		
		JsonObject jobj = jsonReader.readObject();

		JsonObject object = (JsonObject) jobj.get("photo");
		
		String id = object.getString("id");
				
		Photo p = ((Photos)Services.getService(Service.PHOTOS)).photos.get(id);
		
		if ( p == null ){
				p = new Photo();
				p.id = id;
				((Photos)Services.getService(Service.PHOTOS)).photos.put(id, p);
		}

//		JsonArray array = (JsonArray) object.get("dates");
        
		object = (JsonObject) object.get("dates");
		
		logger.info("dates====");
		logger.info(object);
		logger.info("dates====");
		
		p.dateTaken = object.getString("taken");
		
		return p;
	}

	public static void main(String[] args) {

		Services.main(args);
		String photoId = null;
		
		int i;

		for (i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-photoId":
				if (i < args.length)
					photoId = args[++i];
				break;
			}
		}

		try {
		
				logger.info( GetInfo.getInfo(photoId));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


}
