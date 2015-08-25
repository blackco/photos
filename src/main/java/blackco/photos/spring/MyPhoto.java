package blackco.photos.spring;

import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component
public class MyPhoto {
	
	private static final Logger logger = Logger.getLogger(MyPhoto.class);
	
	private String filename;
	
	private String flickrPhotoId = FlickrPhoto.UNPROCESSED;
	
	private Date dateTaken;
	
	private String camera;

	
	
	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getFlickrPhotoId(){
		return flickrPhotoId;
	}
	
	public void setFlickrPhotoId(String flickrPhotoId){
		this.flickrPhotoId = flickrPhotoId;
	}
	
	public Date getDateTaken() {
		return dateTaken;
	}

	public void setDateTaken(Date dateTaken) {
		this.dateTaken = dateTaken;
	}

	public String getCamera() {
		return camera;
	}

	public void setCamera(String camera) {
		this.camera = camera;
	}
	
	public String toString(){
		return this.getFilename() 
					+ ", " + this.getCamera() + "," + this.getDateTaken() 
					+ ","  + this.getFlickrPhotoId();
	}

}
