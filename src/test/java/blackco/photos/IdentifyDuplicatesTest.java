package blackco.photos;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import blackco.photos.Services.Service;

public class IdentifyDuplicatesTest {

	@Before
	public void setUp() throws Exception {
		
		String[] args = {"None","None"};
		
		try {
			Services.main(args);
		}catch (Exception e){
			//Ignore, we don't need to set up properly.
		}
		
		Services.addService(Service.FLICKR_AUTH, new MockFlickrAuth());
	}

	public class MockFlickrAuth{
		public String toString(){
			return "Mock";
		}
	}
	
	public class PageSummary{
	
		public ArrayList<Photo> photos = new ArrayList<Photo>();
	}
	
	public class Search{
	
		public PageSummary summary;
		
		public PageSummary search(Search s){
			

			return summary;
		}
	}
	
	@Test
	public void test() {
		
		/*
		 * Set Up
		 * 
		 */
		
		PageSummary page = new PageSummary();
		
		Photo p1 = new Photo();
		p1.dateTaken = "2014-04-04 12:10:01";
		p1.title = "P1";
		p1.camera = "iPhone 5";
		page.photos.add( p1);

		Photo p2 = new Photo();
		p2.dateTaken = "2014-04-04 12:10:01";
		p2.title = "P2";
		p2.camera = "iPhone 5";
		page.photos.add( p2);

		Photo p3 = new Photo();
		p3.dateTaken = "2014-04-04 12:10:01";
		p3.title = "P3";
		p3.camera = "iPhone 5";
		page.photos.add( p3);
		
		Photo p4 = new Photo();
		p4.dateTaken = "2014-04-04 12:10:02";
		p4.camera = "iPhone 5";
		p4.title = "P4";
		page.photos.add( p4);
		
		Photo p5 = new Photo();
		p5.title = "P5";
		p5.dateTaken = "2014-04-04 12:10:01";
		p5.camera = "iPhone 6";
		page.photos.add( p5);
			
		Search s = new Search();
		s.summary = page;
		
		HashMap<String, ArrayList> map = new HashMap<String, ArrayList>();

		PageSummary summary = s.search(s);

		System.out.println("IdentifyDuplicates.main(): Search Criteria = " + s);
		System.out.println(summary);

		/*
		 *  Build HashMap of photos keyed by date
		 *  Any key with more than one photo is a taken at the same time and a potential dupe
		 *  
		 */
		
		for (Photo p : summary.photos) {
//			info.getInfo(p.id);

			ArrayList<Photo> list = map.get(p.dateTaken);

			if (list == null) {
				System.out.println("IdentifyDuplicatesTest.test(): new unique timestamp:"+ p.dateTaken);
				list = new ArrayList<Photo>();
				map.put(p.dateTaken, list);
			} 

			list.add(p);
			
		}

		
		/*
		 * 
		 * Iterate through all the photos taken at the same time, 
		 * If they are taken on the same camera, they are a duplicate
		 * 
		 */
		for (ArrayList<Photo> list : map.values()) {

			System.out.println("IdentifyDuplicatesTest.test(): photos for timestamp:"+ list);
			
			if (list.size() > 1) {
				HashMap<String, ArrayList> camera = new HashMap();

				for (Photo p : list) {

					//GetExif.getExif(p.id).toString();

					ArrayList<Photo> match = camera.get(p.camera);

					if (match == null) {
						System.out.println("IdentifyDuplicatesTest.test(): new unique camera:"
									+ p.camera);
						
						match = new ArrayList<Photo>();
						camera.put(p.camera, match);
					} 
					
					match.add(p);
					
				}
				
				ArrayList<Photo> duplicates = new ArrayList<Photo>();
				
				for( String c: camera.keySet() ){
					
					ArrayList l = camera.get(c);
					if (l.size() > 1){
						duplicates.addAll(l);
					}
					
				}
					
				System.out.println("Potential Duplicates " + camera.keySet().size());
	
				
				assertEquals( duplicates.contains(p1) && duplicates.contains(p2) && duplicates.contains(p3) 
						&& !duplicates.contains(p4)
						&& !duplicates.contains(p5), true);
					
					
				
		
			}

		}

		
	}

}
