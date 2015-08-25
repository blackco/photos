package blackco.photos.spring;

import java.util.ArrayList;

public class PageSummary {

	public int page;
	public int pages;
	public int perPage;
	public String total;
	
	public ArrayList<FlickrPhoto> photos = new ArrayList<FlickrPhoto>();
	
	public String toString(){
		
		return "Page="+page+",pages="+pages+",total="+total;
		
	}
	
}


