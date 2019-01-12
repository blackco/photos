package blackco.photos;

import java.util.ArrayList;


public class PageSummary {

	public int page;
	public int pages;
	public int perPage;
	public String total;
	
	public ArrayList<Photo> photos = new ArrayList<Photo>();
	
	public String toString(){
		
		return "Page="+page+",pages="+pages+",total="+total;
		
	}
	
}
