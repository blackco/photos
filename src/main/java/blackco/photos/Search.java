package blackco.photos;

import java.io.StringReader;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import blackco.photos.Services.Service;

public class Search {
	
	public String min_taken_date;
	public String max_taken_date;
	
	/*
	 * "2006-02-04 09:00:00"
	 * "2006-02-04 23:00:00"
	 */

	public static PageSummary search(Search criteria) {
		FlickrAuth auth = (FlickrAuth) Services.getService(Service.FLICKR_AUTH);

		OAuthRequest request = new OAuthRequest(Verb.GET,
				"https://api.flickr.com/services/rest/");
		request.addQuerystringParameter("method", "flickr.photos.search");

		request.addQuerystringParameter("api_key", auth.getApiKey());
		request.addQuerystringParameter("user_id", auth.getUserId());
		request.addQuerystringParameter("min_taken_date",criteria.min_taken_date);
		request.addQuerystringParameter("max_taken_date",criteria.max_taken_date);
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

	private static PageSummary buildPageSummary(JsonReader jsonReader) {

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
			
			Photo p = ((Photos)Services.getService(Service.PHOTOS)).photos.get(id);
			
			if ( p == null ){
					p = new Photo();
					p.id = id;
					((Photos)Services.getService(Service.PHOTOS)).photos.put(id, p);
					
			}

			p.owner = obj.getString("owner");
			p.secret = obj.getString("secret");

			p.title = obj.getString("title");

			s.photos.add(p);
		}

		return s;
	}
	
	public String toString(){
		return "min_date_taken=" + this.min_taken_date  +", max_date_taken=" + this.max_taken_date;
	}
	
	public static void main(String[] args) {

		Services.main(args);
		Search s = new Search();
		
		s.min_taken_date="2014-02-21 15:45:00";
		s.max_taken_date="2014-02-21 15:45:00";
		
		int i;

		for (i = 0; i < args.length; i++) {
			switch (args[i]) {
			
				
			case "-min_date_taken":
				if (i < args.length)
					s.min_taken_date = args[++i];
				break;
				

			case "-max_date_taken":
				if (i < args.length)
					s.max_taken_date = args[++i];
				break;
			
			}
		}

		try {
			PageSummary summary = s.search(s);
			
			System.out.println(summary);
			
			for( Photo p: summary.photos)
				System.out.println(GetExif.getExif(p.id).toString());
			
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}
