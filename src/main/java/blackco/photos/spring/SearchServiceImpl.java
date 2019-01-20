package blackco.photos.spring;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonValue;

import org.apache.log4j.Logger;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.springframework.beans.factory.annotation.Autowired;

import blackco.photos.apps.DownloadFiles;


public class SearchServiceImpl implements SearchService {
	
	private static final Logger logger = Logger.getLogger(SearchServiceImpl.class);
	
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
		request.addQuerystringParameter("content_type", "1");

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


	public void download(String path, PageSummary s){
		
		for (FlickrPhoto onFlickrPhoto : s.photos) {
			
			getSizes( path, onFlickrPhoto.id, onFlickrPhoto.title);

		}
		
	}
	
	private void getSizes(String path, String id, String title)  {
		

		OAuthRequest request = new OAuthRequest(Verb.GET,
				"https://api.flickr.com/services/rest/");
		request.addQuerystringParameter("method", "flickr.photos.getSizes");

		request.addQuerystringParameter("api_key", auth.getApiKey());
		request.addQuerystringParameter("photo_id", id);
		request.addQuerystringParameter("format", "json");
		request.addQuerystringParameter("nojsoncallback", "1");

		Response response = auth.get(request);

		StringReader reader = new StringReader(response.getBody());

		JsonReader jsonReader = Json.createReader(reader);
		
		JsonObject jobj = jsonReader.readObject();

		
		JsonObject object = (JsonObject) jobj.get("sizes");
		JsonArray array = (JsonArray) object.get("size");


		for (JsonValue val : array) {
			
			JsonObject obj = (JsonObject) val;
		

				
				if ( obj.getString("label").equals("Original")){

					boolean savedImage = false;
					int errorCount = 0;

					while ( !savedImage && errorCount < 4) {
						savedImage = saveImage(path, obj.getString("source"), title);
						if ( !savedImage){
							logger.info("Cannot persist title=" + title + ", number of tries= " + errorCount);
							errorCount ++;
						}

					}
				}

			}
		
		}

		
		

		


	private  boolean saveImage(String path, String imageUrl, String title)  {

			try {
				URL url = new URL(imageUrl);
				String fileName = url.getFile();
				logger.info("imageUrl = " + imageUrl);

				String destName = null;

				if ( title.isEmpty() ) {
					destName = path + "/" + fileName.substring(fileName.lastIndexOf("/"));
				} else {
					destName = path + "/" + title + fileName.substring(fileName.lastIndexOf("."));
				}
					System.out.println(destName);

				InputStream is = url.openStream();
				OutputStream os = new FileOutputStream(destName);

				byte[] b = new byte[2048];
				int length;

				while ((length = is.read(b)) != -1) {
					os.write(b, 0, length);
				}

				is.close();
				os.close();
				return true;

			} catch (Exception e){
				logger.info("Cannot persist title=" + title  );
				//logger.error(e);
				return false;

			}
	}

}
