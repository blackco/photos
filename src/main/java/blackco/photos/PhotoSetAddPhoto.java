package blackco.photos;

import java.io.StringReader;

import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;

import blackco.photos.Services.Service;

public class PhotoSetAddPhoto {

	public static void add(Photo p, String photoSetId) {
		FlickrAuth auth = (FlickrAuth) Services.getService(Service.FLICKR_AUTH);

		OAuthRequest request = new OAuthRequest(Verb.GET,
				"https://api.flickr.com/services/rest/");
		request.addQuerystringParameter("method", "flickr.photosets.addPhoto");

		request.addQuerystringParameter("api_key", auth.getApiKey());
		request.addQuerystringParameter("user_id", auth.getUserId());
		request.addQuerystringParameter("photoset_id",photoSetId);
		request.addQuerystringParameter("photo_id",p.id);
		request.addQuerystringParameter("format", "json");
		request.addQuerystringParameter("nojsoncallback", "1");

		Response response = auth.get(request);

		System.out.println(response.getBody());
		StringReader reader = new StringReader(response.getBody());

		
		//JsonReader jsonReader = Json.createReader(reader);

		// HOW DO YOU CHECK ITS OK??
		
		//return buildPageSummary(jsonReader);

	}

		
	
	public static void main(String[] args) {

		Services.main(args);
		Search s = new Search();
		
		String photoSetId = "72157651322092960";
		
		int i;

		for (i = 0; i < args.length; i++) {
			switch (args[i]) {
			
				
			case "-photoSetId":
				if (i < args.length)
					photoSetId = args[++i];
				break;
				
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
			
		
		PhotoSetAddPhoto album = new PhotoSetAddPhoto();
				

		try {
			PageSummary summary = s.search(s);
			
			System.out.println(summary);
			
			for( Photo p: summary.photos)
				album.add(p, photoSetId);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
