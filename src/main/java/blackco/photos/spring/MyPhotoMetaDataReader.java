package blackco.photos.spring;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import blackco.photos.metadata.MyTag;
import blackco.photos.metadata.MyTags;

import com.drew.metadata.Metadata;

public interface MyPhotoMetaDataReader {
	
	public String getCamera(Metadata metadata);
	
	public Date getDate(Metadata metadata);
	
	public String getTitle(Metadata metadata);
	
	public void printAllTags(File jpegFile);
	
	//public MyTags getMyTags(Metadata metadata);
	
	public MyTags getSuggestedDateTags(Metadata metadata);
	
	public MyTags getSuggestedCameraTags(Metadata metadata);
	

}
