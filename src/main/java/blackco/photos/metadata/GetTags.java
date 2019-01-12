package blackco.photos.metadata;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import blackco.photos.spring.MyPhotosCache;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileMetadataDirectory;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GetTags {

	public static void main(String[] args) {

		try {

			String dir = "/Users/blackco/Pictures/test2/";
			GetTags tags = new GetTags();

			File file = new File(
					dir + "IMG_0719.jpg");
			
			tags.printAllTags(file);
			tags.printSpecificTags(file);
			
			//System.out.println("GetTags.main(): can serialize? : " + tags.canSerializeUsingMetadata(file));
			
			System.out.println("GetTags.main(): can serialize? : " + tags.canSerializeUsingTag(file));
						
			// tags.printAllTags(new
			// File("/Users/blackco/Pictures/test2/IMG_1983.jpg"));
		} catch (Exception e) {
			System.err.println("UsingDrewNoakes.printAllTags(): error");
			e.printStackTrace();
		}
	}

	public void printAllTags(File jpegFile) throws ImageProcessingException,
			IOException {

		Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);

		System.out.println("==============================================");
		for (Directory directory : metadata.getDirectories()) {
			for (Tag tag : directory.getTags()) {
				System.out.println(tag);
			}
		}

		
		
		System.out.println("==============================================");
	}

	private final static ObjectMapper mapper;
	   static {
	      mapper = new ObjectMapper();
	      mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}
	   
	public static String serialize(FileWriter f1, Object object) {
	      
		try {
	    	  
	    	  try {
				mapper.writeValue(f1, object);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    	  
	         return mapper.writeValueAsString(object);
	      } catch (JsonProcessingException e) {
	         e.printStackTrace();
	      }
	      return null;
	   }
	
	public Date getOriginalDate(File jpegFile){
		Date result =null;
		try {

			Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);

			// obtain the Exif directory
			ExifSubIFDDirectory directory = metadata
					.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

			// query the tag's value

			if (directory != null) {
				result = directory
						.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

			}

			
		} catch (Exception e) {

			e.printStackTrace();

			// this.printAllTags(jpegFile);

			// throw new RuntimeException(e);
		}
		
		return result;
	}
	

	public boolean canSerializeUsingTag(File jpegFile){

		try {

			Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);

			// obtain the Exif directory
			ExifSubIFDDirectory d1 = metadata
					.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
			
			FileWriter writer = new FileWriter(jpegFile.getPath() + "Tags.json");
			FileReader reader = new FileReader(jpegFile.getPath() + "Tags.json");
			
			MyTags l1 = new MyTags();
			
			for (Tag tag : d1.getTags()) {
				MyTag myTag = new MyTag();
				
				myTag.tagName = tag.getDescription();
				myTag.tagValue = tag.getTagName();
				
				l1.list.add(myTag);
			}
			
			this.serialize(writer,l1);
			
			MyTags l2 = mapper.readValue(reader,
					MyTags.class);
			
			System.out.println("GetTags.canSerializeUsingTag():l1 " + l1);
			
			System.out.println("GetTags.canSerializeUsingTag():l2 " + l2);
			
			int index = 0;
			
			for ( MyTag tag1 : l1.list){
				
				MyTag tag2 = l2.list.get(index);
				System.out.println("Index: " + index + ",l1 : " + tag1.tagName + "," + tag2.tagValue);
				System.out.println("Index: " + index + ",l2 : " + tag2.tagName + "," + tag2.tagValue);
				
				index++;
			}
			
			return l1.equals(l2);

		} catch ( Exception e){
			
			e.printStackTrace();
		}
		return false;

	}
	
	public boolean canSerializeUsingMetadata(File jpegFile){
		
		try {

			Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);

			// obtain the Exif directory
			ExifSubIFDDirectory d1 = metadata
					.getFirstDirectoryOfType(ExifSubIFDDirectory.class);
			
			FileWriter writer = new FileWriter(jpegFile.getPath() + "TEST.json");
			FileReader reader = new FileReader(jpegFile.getPath() + "TEST.json");
			
			this.serialize(writer,d1);
			
			ExifSubIFDDirectory d2 = mapper.readValue(reader,
					ExifSubIFDDirectory.class);
			
			return d1.equals(d2);

		} catch ( Exception e){
			
			e.printStackTrace();
		}
		return false;
			

		
	}
	
	public void printSpecificTags(File jpegFile) {

		try {

			Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);

			// obtain the Exif directory
			ExifSubIFDDirectory directory = metadata
					.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

			// query the tag's value

			if (directory != null) {
				Date date = directory
						.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

				// Original DateTime
				System.out.println(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL
						+ ": " + date);
			}

			FileMetadataDirectory fileDirectory = metadata
					.getFirstDirectoryOfType(FileMetadataDirectory.class);

			if (fileDirectory != null) {

				// title
				System.out
						.println(FileMetadataDirectory.TAG_FILE_NAME
								+ ": "
								+ fileDirectory
										.getString(FileMetadataDirectory.TAG_FILE_NAME));
			}

			ExifIFD0Directory exifDirectory = metadata
					.getFirstDirectoryOfType(ExifIFD0Directory.class);

			if (exifDirectory != null) {
				System.out.println(exifDirectory
						.getString(ExifIFD0Directory.TAG_MAKE)
						+ " "
						+ exifDirectory.getString(ExifIFD0Directory.TAG_MODEL));
			}

		} catch (Exception e) {

			e.printStackTrace();

			// this.printAllTags(jpegFile);

			// throw new RuntimeException(e);
		}
	}

}
