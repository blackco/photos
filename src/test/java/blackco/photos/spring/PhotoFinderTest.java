package blackco.photos.spring;

import static org.junit.Assert.assertTrue;

import java.nio.file.Path;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

public class PhotoFinderTest {

	private ApplicationContext context; 
	private String home;
	private String photoDir = "/src/test/test-fixtures/photos1";
	
	@Bean
	public PhotoFinder myPhotoFinder() {
		return new PhotoFinderImpl();
	}
	
	@Before
	public void setup(){
		home = System.getProperty("home");
	
		context =new AnnotationConfigApplicationContext(PhotoFinderTest.class);
	}
	
	
	@Test
	public void testPhotoFinder(){
		
		PhotoFinder finder  =context.getBean(PhotoFinder.class);
		
		ArrayList<Path> files = finder.getOnDiskList(home+photoDir);
		
		for ( Path p : files){
			System.out.println(p);
		}
		
		assertTrue(true);
		
	}
	
}
