package blackco.photos.spring;

import org.apache.log4j.Logger;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Verb;
import org.springframework.beans.factory.annotation.Autowired;

import javax.json.*;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;

public class DownloadServiceImpl implements DownloadService {

    private FlickrAuth auth;

    private static final Logger logger = Logger.getLogger(DownloadServiceImpl.class);


    @Autowired
    public void setFlickrAuth(FlickrAuth auth) {
        this.auth = auth;
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

        System.out.println(response.getBody());

        StringReader reader = new StringReader(response.getBody());

        JsonReader jsonReader = Json.createReader(reader);

        JsonObject jobj = jsonReader.readObject();


        JsonObject object = (JsonObject) jobj.get("sizes");
        JsonArray array = (JsonArray) object.get("size");


        for (JsonValue val : array) {

            JsonObject obj = (JsonObject) val;


            if (  obj.getString("label").equals("Video Player")  ) {

                if ( saveVideo(path,obj.getString("source"), title + "_vp")) {
                    logger.info("Cannot persist = " + title);
                }
            }

            if ( obj.getString("label").equals("Video Original")   ) {

                if ( saveVideo(path,obj.getString("source"), title +"_vo")) {
                    logger.info("Cannot persist = " + title);
                }
            }

            if (  obj.getString("label").equals("Site MP4")  ) {

                if ( ! saveVideo(path,obj.getString("source"), title + "_s")) {
                    logger.info("Cannot persist = " + title);
                }
            }

            if (  obj.getString("label").equals("Mobile MP4")   ) {

                if ( saveVideo(path,obj.getString("source"), title + "_m")) {
                    logger.info("Cannot persist = " + title);
                }
            }

            if ( obj.getString("label").equals("Original")){

                boolean savedImage = false;
                int errorCount = 0;

                while ( !savedImage && errorCount < 4) {
                    savedImage = saveImage(path, id, obj.getString("source"), title);
                    if ( !savedImage){
                        logger.info("Cannot persist title=" + title + ", number of tries= " + errorCount);
                        errorCount ++;
                    } else {
                        //downloadedPhoto(id);
                    }

                }
            }


        }



    }

    private boolean saveVideo(String path, String imageUrl, String title){


        try {
            URL url = new URL(imageUrl);
            String fileName = url.getFile();


            String destName = null;

            destName = path + "/" + title + ".mp4";

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
            logger.info("Cannot persist video="   );
            logger.error(e);
            return false;

        }

    }





    private  boolean saveImage(String path, String id, String imageUrl, String title)  {

        //updatePhoto(id,imageUrl, title);

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
