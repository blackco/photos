package blackco.photos.spring;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitOption;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.EnumSet;



public class PhotoFinderImpl extends SimpleFileVisitor<Path>  implements PhotoFinder {

	private ArrayList<Path> found = new ArrayList<Path>();
	private ArrayList<String> patterns = new ArrayList<String>();

	public PhotoFinderImpl() {

		patterns.add("*.JPG");
		patterns.add("*.jpg");
		patterns.add("*.AVI");
		patterns.add("*.avi");

	}

	public ArrayList<Path> getOnDiskList(String onDiskPath) {
		
		found = new ArrayList<Path>();
	
		try {
			Files.walkFileTree(Paths.get(onDiskPath),
					EnumSet.of(FileVisitOption.FOLLOW_LINKS),
					Integer.MAX_VALUE, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return found;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException {

		for (String pattern : patterns) {

			PathMatcher matcher = FileSystems.getDefault().getPathMatcher(
					"glob:" + pattern);

			if (matcher.matches(file.getFileName())) {

				found.add(file);

			}
		}

		return FileVisitResult.CONTINUE;
	}



}
