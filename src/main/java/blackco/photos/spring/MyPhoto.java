package blackco.photos.spring;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import blackco.photos.metadata.MyTags;

import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

@Component
public class MyPhoto {
	
	private static final Logger logger = Logger.getLogger(MyPhoto.class);
	
	private String filename;
	
	private String flickrPhotoId = FlickrPhoto.UNPROCESSED;
	
	private Date dateTaken;
	
	private String camera;
	
	private MyTags suggestedDateTags;
	
	private MyTags suggestedCameraTags;
	
	private long lastModifiedDate;
	
	public String getId(){
		
		return "MyPhoto" + Integer.toString(this.getFilename().hashCode())
				+ "_"+ this.getLastModifiedDate();
		
	}
	
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
	
	public String getFormatedDateTaken(){
		if ( dateTaken == null) {
			return "Unknown";
		} else {
			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
			return format1.format(dateTaken);
		}
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
	
	public void setSuggestedDateTags(MyTags tags){
		this.suggestedDateTags = tags;
	}
	
	public MyTags getSuggestedDateTags(){
		return suggestedDateTags;
	}
	
	public void setSuggestedCameraTags(MyTags tags){
		this.suggestedCameraTags = tags;
	}
	
	public MyTags getSuggestedCameraTags(){
		return suggestedCameraTags;
	}
	
	public void setLastModifiedDate(long date){
		lastModifiedDate = date;
	}
	
	public long getLastModifiedDate(){
		return lastModifiedDate;
	}
	/*
	public String toString(){
		return this.getFilename() 
					+ ", " + this.getCamera() + "," + this.getDateTaken() 
					+ ","  + this.getFlickrPhotoId();
					
	}
	*/

}
