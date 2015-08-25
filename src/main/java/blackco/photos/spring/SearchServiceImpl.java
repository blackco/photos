package blackco.photos.spring;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.springframework.beans.factory.annotation.Autowired;


public class SearchServiceImpl implements SearchService {
	
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

	
	public PageSummary search(SearchCriteria criteria) {
		

		OAuthRequest request = new OAuthRequest(Verb.GET,
				"https://api.flickr.com/services/rest/");
		request.addQuerystringParameter("method", "flickr.photos.search");

		request.addQuerystringParameter("api_key", auth.getApiKey());
		request.addQuerystringParameter("user_id", auth.getUserId());
		request.addQuerystringParameter("min_taken_date",FlickrPhotoDate.getDateInFlickrTextFormat(criteria.min_taken_date));
		request.addQuerystringParameter("max_taken_date",FlickrPhotoDate.getDateInFlickrTextFormat(criteria.max_taken_date));
		request.addQuerystringParameter("format", "json");
		request.addQuerystringParameter("nojsoncallback", "1");

		System.out.println("Search.search():"
				+ "min_taken_date:"+criteria.min_taken_date
				+ "max_taken_date:"+criteria.max_taken_date);
		
		Response response = auth.get(request);

		System.out.println(response.getBody());
		StringReader reader = new StringReader(response.getBody());

		JsonReader jsonReader = Json.createReader(reader);

		return buildPageSummary(jsonReader);

	}

	private PageSummary buildPageSummary(JsonReader jsonReader) {

		PageSummary s = new PageSummary();

		JsonObject jobj = jsonReader.readObject();

		JsonObject object = (JsonObject) jobj.get("photos");

		s.page = object.getInt("page");
		s.pages = object.getInt("pages");
		s.perPage = object.getInt("perpage");
		s.total = object.getString("total");

		

		JsonArray array = (JsonArray) object.get("photo");
		for (JsonValue val : array) {
			
			JsonObject obj = (JsonObject) val;
			String id = obj.getString("id");
			
			FlickrPhoto p = photos.getPhoto(id);
			
			if ( p == null ){
					p = new FlickrPhoto();
					p.id = id;
					photos.setPhoto(p);
					
			}

			p.owner = obj.getString("owner");
			p.secret = obj.getString("secret");

			p.title = obj.getString("title");

			s.photos.add(p);
		}

		return s;
	}




}
