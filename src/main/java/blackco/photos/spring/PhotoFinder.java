package blackco.photos.spring;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;

public interface PhotoFinder {
	
	public ArrayList<Path> getOnDiskList(String onDiskPath);
	
	public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
			throws IOException;

}
