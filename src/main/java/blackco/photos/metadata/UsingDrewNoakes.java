package blackco.photos.metadata;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;

import blackco.photos.GetExif;
import blackco.photos.GetInfo;
import blackco.photos.PageSummary;
import blackco.photos.Photo;
import blackco.photos.PhotoFinder;
import blackco.photos.Search;
import blackco.photos.Services;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifIFD0Directory;
import com.drew.metadata.exif.ExifSubIFDDirectory;
import com.drew.metadata.file.FileMetadataDirectory;

public class UsingDrewNoakes {

	public static void main(String[] args) {

		try {

			Services.main(args);
			Search s = new Search();

			UsingDrewNoakes noakes = new UsingDrewNoakes();

			FileWriter f1;

			f1 = new FileWriter(
					"/Users/blackco/Pictures/test/UsingDrewNoakes.txt");

			

			for (Photo p : noakes.buildPhotos(noakes
					.getOnDiskList("/Users/blackco/Pictures/test"))) {

				boolean match = false;

				s.max_taken_date = p.dateTaken;
				s.min_taken_date = p.dateTaken;

				System.out.println("Search criteria = " + s);
				PageSummary summary = s.search(s);

				HashMap<String, ArrayList<Photo>> dates = new HashMap();

				for (Photo ph : summary.photos) {

					GetInfo.getInfo(ph.id);
					ArrayList<Photo> list = dates.get(ph.dateTaken);
					if (list == null) {
						list = new ArrayList<Photo>();
						dates.put(ph.dateTaken, list);
					}

					list.add(ph);

					System.out.println("Processed potential match from Flickr:"
							+ ph);
				}

				ArrayList<Photo> found = dates.get(p.dateTaken);

				if (found != null) {

					for (Photo sameDate : found) {
						GetExif.getExif(sameDate.id);

						if (sameDate.dateTaken.equals(p.dateTaken)
								&& sameDate.camera.equals(p.camera)) {

							match = true;

							p.matchedPhoto = sameDate;

						}

					}

				}

				System.out.println(p);

				f1.write(p.toString());
				f1.write(System.getProperty("line.separator"));
			}

			f1.close();
		} catch (Exception e) {
			System.out.println(e);
		}

	}

	public static String getDateInFlickrTextFormat(Date date) {
		DateFormat df = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss");

		return df.format(date);
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

	/*
	 * /*
			 * PriorRun includes (1) List of on disk photos (2) results of
			 * whether matched or not (3) How far we got ...
			 * 
			 * 
			 * FileReader priorList = new
			 * FileReader("/Users/blackco/Pictures/test");
			 * 
			 * if ( priorList == null ){
			 * 
			 * }
			 */
	  
	 
	public void initialize() {

		JsonReader reader;
		try {

			reader = Json.createReader(new FileReader(
					"/Users/blackco/past.json"));

			System.out.println(reader.toString());
			
			/*
			 * Read all the photos, each photo marks whether 
			 * (1) Processed or not
			 * (2) it has been matched or not...
			 * 
			 */
			JsonObject jsonst = reader.readObject();

			JsonArray array = (JsonArray) jsonst.get("photo");
			

			// System.out.println( results );

			for (JsonObject result : array.getValuesAs(JsonObject.class)) {
				System.out.println(result.getJsonString("Mobile"));
			}

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public ArrayList<Photo> buildPhotos(ArrayList list) {

		ArrayList<Photo> results = new ArrayList<Photo>();

		for (Object s : list) {

			blackco.photos.Photo photo = new Photo();

			File jpegFile = ((Path) s).toFile();

			photo.originalFileName = ((Path) s).toFile().getAbsolutePath();

			results.add(photo);

			try {

				Metadata metadata = ImageMetadataReader.readMetadata(jpegFile);

				// obtain the Exif directory
				ExifSubIFDDirectory directory = metadata
						.getFirstDirectoryOfType(ExifSubIFDDirectory.class);

				// query the tag's value

				if (directory != null) {
					Date date = directory
							.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);

					if (date != null) {
						photo.originalDateTaken = date;
						photo.dateTaken = UsingDrewNoakes
								.getDateInFlickrTextFormat(date);
					}
				}

				FileMetadataDirectory fileDirectory = metadata
						.getFirstDirectoryOfType(FileMetadataDirectory.class);

				if (fileDirectory != null) {
					photo.title = fileDirectory
							.getString(FileMetadataDirectory.TAG_FILE_NAME);
				}

				ExifIFD0Directory exifDirectory = metadata
						.getFirstDirectoryOfType(ExifIFD0Directory.class);

				if (exifDirectory != null) {
					photo.camera = exifDirectory
							.getString(ExifIFD0Directory.TAG_MAKE)
							+ " "
							+ exifDirectory
									.getString(ExifIFD0Directory.TAG_MODEL);
				}

			} catch (Exception e) {
				System.err.println("UsingDrewNoakes.buildPhotos(): processing "
						+ photo);
				e.printStackTrace();

				this.printAllTags(jpegFile);

				// throw new RuntimeException(e);
			}
		}

		return results;
	}

	private ArrayList getOnDiskList(String onDiskPath) {

		ArrayList results = new ArrayList();

		ArrayList<PhotoFinder> patterns = new ArrayList();

		patterns.add(new PhotoFinder("*.JPG"));
		patterns.add(new PhotoFinder("*.jpg"));
		// patterns.add(new PhotoFinder("*.AVI"));
		// patterns.add(new PhotoFinder("*.avi"));

		for (PhotoFinder finder : patterns) {

			try {
				Files.walkFileTree(Paths.get(onDiskPath),
						EnumSet.of(FileVisitOption.FOLLOW_LINKS),
						Integer.MAX_VALUE, finder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			results.addAll(finder.getFound());
		}

		return results;
	}

}
