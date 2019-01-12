package blackco.photos;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;


public class Comparison {

	private static ArrayList<String> flickr = new ArrayList<String>();

	private HashMap<String, Path> ondisk = new HashMap<String, Path>();

	
	private static String onDiskPath; 
	private static String onDiskFileList;
	private static String onFlickrList; 
	private static String results; 
	
	public static void main(String[] args) {

		Services.main(args);
		
		int i;

		for (i = 0; i < args.length; i++) {
			switch (args[i]) {
			case "-path":
				if (i < args.length)
					onDiskPath = args[++i];
				break;

						
			case "-debugOnDiskList":
				if (i < args.length)
					onDiskFileList = args[++i];
				break;

			case "-debugOnFlickrList":
				if (i < args.length)
					onFlickrList = args[++i];
				break;

			case "-results":
				if (i < args.length)
					results = args[++i];
				break;
			}
		}
		
		
		Comparison c = new Comparison();

		try {
			c.compare();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void compare() throws Exception {

		FileWriter f1;

		GetPhotos g = new GetPhotos();
		
		flickr = g.getFlickrList();

		if (onFlickrList != null) {
			f1 = new FileWriter(onFlickrList);

			for (String p : flickr) {
				f1.write(p);
				f1.write(System.getProperty("line.separator"));
			}

			f1.close();
		}

		getOnDiskList();

		if (onDiskFileList != null) {
			f1 = new FileWriter(onDiskFileList);

			for (Path p : ondisk.values()) {
				f1.write(p.toFile().getAbsolutePath());
				f1.write(System.getProperty("line.separator"));
			}

			f1.close();
		}

		for (String s : flickr) {
			ondisk.remove(s);
		}

		if (results != null) {
			f1 = new FileWriter(results);

			for (Path p : ondisk.values()) {
				f1.write(p.toFile().getAbsolutePath());
				f1.write(System.getProperty("line.separator"));
			}

			f1.close();
		}
	}



	private void getOnDiskList() {

		ArrayList<PhotoFinder> patterns = new ArrayList<PhotoFinder>();

		patterns.add(new PhotoFinder("*.JPG"));
		patterns.add(new PhotoFinder("*.jpg"));
		patterns.add(new PhotoFinder("*.AVI"));
		patterns.add(new PhotoFinder("*.avi"));

		for (PhotoFinder finder : patterns) {

			try {
				Files.walkFileTree(Paths.get(onDiskPath),
						EnumSet.of(FileVisitOption.FOLLOW_LINKS),
						Integer.MAX_VALUE, finder);
			} catch (IOException e) {
				e.printStackTrace();
			}

			//TODO What is this hard coded for.
			for (Path s : finder.getFound()) {
				ondisk.put(stripExtension(s.getFileName().toString())
						+"Canon Digital IXUS 500", s);
			}

		}


	}


	private static String stripExtension(String str) {
		// Handle null case specially.

		if (str == null)
			return null;

		// Get position of last '.'.

		int pos = str.lastIndexOf(".");

		// If there wasn't any '.' just return the string as is.

		if (pos == -1)
			return str;

		// Otherwise return the string, up to the dot.

		return str.substring(0, pos);
	}
	
	
}
