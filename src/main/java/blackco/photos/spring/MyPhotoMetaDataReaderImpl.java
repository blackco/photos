package blackco.photos.spring;

import java.io.File;
import java.util.Date;

import org.apache.log4j.Logger;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileMetadataDirectory;


public class MyPhotoMetaDataReaderImpl implements MyPhotoMetaDataReader {
	
	private static final Logger logger = Logger.getLogger(MyPhotoMetaDataReaderImpl.class);

	/*
	 * Test 
	 * (1) For Metadata from an HTC camera just returns HTC
	 * (2) For Iphone returns iphone
	 * (3) For Sony returns sony
	 * (4) For Canon returns Canon
	 * (5) corrupted returns null
	 * 
	 * 
	 * Needs
	 * (1) Metadata captured from example photos
	 * 
	 */
	
	public String getCamera(Metadata metadata){
		
		String result = null;
		
		ExifIFD0Directory exifDirectory = metadata
				.getFirstDirectoryOfType(ExifIFD0Directory.class);


		if (exifDirectory != null) {
			
			
			if ( exifDirectory.getString(ExifIFD0Directory.TAG_MAKE ) != null 
						&& exifDirectory.getString(ExifIFD0Directory.TAG_MAKE ).compareTo("HTC") == 0 ){
				
				result  =exifDirectory
						.getString(ExifIFD0Directory.TAG_MODEL);
			} else {
			
				result = exifDirectory
					.getString(ExifIFD0Directory.TAG_MAKE)
					+ " "
					+ exifDirectory
							.getString(ExifIFD0Directory.TAG_MODEL);
			}
		}

		return result;
		
	}
	
	/*
	 * Test
	 * (1) Get time and date of photo
	 * (2) Get null where corrupted metadata 
	 * 
	 * TODO - What's best practice for handling null, an empty object??
	 * 
	 * Need
	 * (1) Metadata captured from real photos in string format
	 * 
	 */
	public Date getDate(Metadata metadata){
		
		// obtain the Exif directory
		ExifSubIFDDirectory directory = metadata
				.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

		// query the tag's value

		if (directory != null) {
			return directory
					.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

		} else {
			return null;
		}

	}
	
	
	public String getTitle(Metadata metadata){
		
		
		FileMetadataDirectory fileDirectory = metadata
				.getFirstDirectoryOfType(FileMetadataDirectory.class);
		
		if (fileDirectory != null) {
			return fileDirectory
					.getString(FileMetadataDirectory.TAG_FILE_NAME);
		} else {
			return null;
		}

	}
	
	public void printAllTags(File jpegFile) {

		try {
			Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);

			for (Directory directory : metadata.getDirectories()) {
				for (Tag tag : directory.getTags()) {
					System.out.println(tag);
				}
			}
		} catch (Exception e) {
			System.err.println("UsingDrewNoakes.printAllTags(): error");
			e.printStackTrace();
		}
	}
	


}
