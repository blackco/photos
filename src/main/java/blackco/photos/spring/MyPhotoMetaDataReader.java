package blackco.photos.spring;

import java.io.File;
import java.util.Date;

import com.drew.metadata.Metadata;

public interface MyPhotoMetaDataReader {
	
	public String getCamera(Metadata metadata);
	
	public Date getDate(Metadata metadata);
	
	public String getTitle(Metadata metadata);
	
	public void printAllTags(File jpegFile);

}
