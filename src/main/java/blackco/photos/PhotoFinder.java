package blackco.photos;

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

public class PhotoFinder extends SimpleFileVisitor<Path> {
	
	  private ArrayList<Path> found = new ArrayList<Path>();
	  private String pattern;
	  
	  public PhotoFinder(String p){
		  pattern = p;
	  }
	  
	  public static ArrayList<Path> getOnDiskList(String onDiskPath) {

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

	  
	  @Override
      public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
      	
      	
      	PathMatcher matcher = FileSystems.getDefault()
                  .getPathMatcher("glob:" + pattern);
      	
      	if ( matcher.matches(file.getFileName())){
      	
      		found.add(file);
      		
      		//found.add(stripExtension(file.getFileName().toString()));
      		System.out.println("Matches file:=" + file.getFileName()) ;
      	
      	}
          
      	return FileVisitResult.CONTINUE;
      }

	  public ArrayList<Path> getFound(){
		  return found;
	  }
	  
}

